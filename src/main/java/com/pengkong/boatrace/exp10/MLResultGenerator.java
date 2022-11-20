package com.pengkong.boatrace.exp10;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.PatternTemplate;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.property.StrategyTemplate;
import com.pengkong.boatrace.exp10.result.AbstractResultCreator;
import com.pengkong.boatrace.exp10.result.graph.ResultGraphBuilder;
import com.pengkong.boatrace.exp10.result.stat.MlBorkEvaluationCreator;
import com.pengkong.boatrace.exp10.result.stat.MlEvaluationCreator;
import com.pengkong.boatrace.exp10.result.stat.MlPrEvaluationCreator;
import com.pengkong.boatrace.exp10.result.stat.MlRangeEvaluationCreator;
import com.pengkong.boatrace.exp10.result.stat.MlRorkEvaluationCreator;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.result.stat.ResultStatBuilder;
import com.pengkong.boatrace.exp10.simulation.data.AbstractRaceDataLoader;
import com.pengkong.boatrace.exp10.simulation.data.DBRaceDataLoader;
import com.pengkong.boatrace.mybatis.client.MlBorkEvaluationMapper;
import com.pengkong.boatrace.mybatis.client.MlEvaluationMapper;
import com.pengkong.boatrace.mybatis.client.MlPrEvaluationMapper;
import com.pengkong.boatrace.mybatis.client.MlRangeEvaluationMapper;
import com.pengkong.boatrace.mybatis.client.MlResultMapper;
import com.pengkong.boatrace.mybatis.client.MlRorkEvaluationMapper;
import com.pengkong.boatrace.mybatis.entity.MlBorkEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlBorkEvaluationExample;
import com.pengkong.boatrace.mybatis.entity.MlEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlEvaluationExample;
import com.pengkong.boatrace.mybatis.entity.MlPrEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlPrEvaluationExample;
import com.pengkong.boatrace.mybatis.entity.MlRangeEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlRangeEvaluationExample;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.mybatis.entity.MlResultExample;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * rec_raceとml_classificationを組み合わせてml_resultを生成する
 * 
 * properties: file_result_config, result_no, result_start_ymd, result_end_ymd, target_db_resource
 *   result_creator, result_sql_id, used_model_no, 
 * @author ttolt
 *
 */
public class MLResultGenerator {
	Logger logger = LoggerFactory.getLogger(MLResultGenerator.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** pattern template */
	PatternTemplate ptnTpl = PatternTemplate.getInstance();
	
	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();
	
	/** strategy template */
	StrategyTemplate staTpl = StrategyTemplate.getInstance(); 
	
	/** result統計データ生成しｔげ出力 */
	ResultStatBuilder statBuilder = ResultStatBuilder.getInstance();

	/** resultのgraphを生成して出力 */
	ResultGraphBuilder graphBuilder = ResultGraphBuilder.getInstance();
	
	/** result生成クラス */
	AbstractResultCreator resultCreator;
	
	String configProperty;
	
	public MLResultGenerator()  {
	}
	
	protected void setupConfigProperty() {
		configProperty = "file_result_config";
		graphBuilder.dirProperty = "dir_result";
	}
	
	/**
	 * 実験番号リストに対して一括で実験を実行する
	 * @param exNoList 実験番号一覧 例）1,2,3,5
	 * @throws Exception
	 */
	public void execute(List<String> exNoList) throws Exception {
		if (configProperty == null) {
			setupConfigProperty();
		}
		
		// 実験番号mapを生成する
		HashMap<String, String> mapExNo = new HashMap<>();
		for (String exNo : exNoList) {
			mapExNo.put(exNo, exNo);
		}
		
		prop.reset(configProperty);
		while(prop.hasNext()) {
			prop.next();
			if (!mapExNo.containsKey(prop.getString("result_no"))) 
				continue;
			// 実験実行
			try {
				prop.putProperty("evaluations_id", "x");
				executeExperiment(prop.getString("result_no"));
			} catch (Exception e) {
				logger.error("experiment " + prop.getString("result_no") + " is failed.", e);
			}
		}
	}
	
	/** Creatorクラス動的生成処理 */
	void setupResultCreator() throws Exception {
		// 결과생성모듈 reflection
		// 생성자 아규먼트는 없음.
		String className = staTpl.getStrategy(prop.getString("result_class_id")).javaClass;
		String clazz = "com.pengkong.boatrace.exp10.result." + className; 
		Constructor<?> c = Class.forName(clazz).getConstructor();
		resultCreator = (AbstractResultCreator) c.newInstance();
	}
	
	/**
	 * 実験番号に対してml_resultを生成する
	 * @param exNo 実験番号
	 * @throws Exception
	 */
	void executeExperiment(String exNo) throws Exception {
		String evaluationsId = prop.getString("evaluations_id");
		setupResultCreator();
		
		String usedModelNo = prop.getString("used_model_no");
		// 統計ビルダ初期化
		statBuilder.clear();
		
		String resultStartYmd = prop.getString("result_start_ymd");
		String resultEndYmd = prop.getString("result_end_ymd");
		double minBetrate = prop.getDouble("min_betrate");
		int minBetcnt = prop.getInteger("min_betcnt");
		
		// 결과생성 대상데이터 취득
		List<DBRecord> listData = loadDB(resultStartYmd, resultEndYmd);
		
		// 결과범위내 데이터를 날짜별로 분류
		HashMapList<DBRecord> mapListYmd = new HashMapList<>();
		for (DBRecord rec : listData) {
			String ymd = rec.getString("ymd");
			mapListYmd.addItem(ymd, rec);
		}
		
		logger.debug("db data retrieved. " + resultStartYmd + "~" + resultEndYmd);
		
		// 結果をDBに保存する・しない
		boolean isSaveResult = prop.getString("save_result").equals("yes");
		// 統計とグラフをDBに保存する・しない
		boolean isSaveStat = prop.getString("save_stat").equals("yes");
		boolean isSaveGraph = prop.getString("save_graph").equals("yes");
		// 出力対象統計データ
		SortedMap<String, ResultStat> statMap;
		
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		MlResultMapper mapper = session.getMapper(MlResultMapper.class);
		MlEvaluationMapper mapperEval = session.getMapper(MlEvaluationMapper.class);
		MlBorkEvaluationMapper mapperBorkEval = session.getMapper(MlBorkEvaluationMapper.class);
		MlRorkEvaluationMapper mapperRorkEval = session.getMapper(MlRorkEvaluationMapper.class);
		MlRangeEvaluationMapper mapperRangeEval = session.getMapper(MlRangeEvaluationMapper.class);
		MlPrEvaluationMapper mapperPrEval = session.getMapper(MlPrEvaluationMapper.class);
		
//		if (isSaveResult) {
//			// 既存ml_result削除
//			MlResultExample exam = new MlResultExample();
//			exam.createCriteria().andResultnoEqualTo(exNo).andResultTypeEqualTo(prop.getString("result_type"));
//			mapper.deleteByExample(exam);
//		}
		
		// 결과 생성 날짜범위 루프
		DateTime currDate = BoatUtil.parseYmd(resultStartYmd);
		DateTime toDate = BoatUtil.parseYmd(resultEndYmd);
		while (currDate.compareTo(toDate) <= 0) {
			String ymd = BoatUtil.formatYmd(currDate);
			if (!isValidDay(ymd)) {
				currDate = currDate.plusDays(1);
				continue;
			}
			
			// 해당날짜의 데이터 취득
			List<DBRecord> listRec = mapListYmd.get(ymd);
			if (listRec == null) {
				currDate = currDate.plusDays(1);
				continue;
			}

			// 確定オッズ 로딩
			//ResultOddsManager.getInstance().load(ymd);

			// 해당날짜의 레이스 데이터 루프
			for (DBRecord dbRec : listRec) {
				List<MlResult> listResult = createResult(dbRec, resultCreator);
				if (listResult == null || listResult.size() <= 0)
					continue;
				// 累積統計データ追加
				statBuilder.addAll(listResult);
				
//				String strResult = String.join(",", listResult.get(0).getYmd(), listResult.get(0).getJyocd(), listResult.get(0).getRaceno().toString()) + " bet:" + listResult.size();
//				for (MlResult result : listResult) {
//					 strResult += "," + String.join("_", result.getBettype(), result.getBetKumiban()); 
//				}
//				logger.debug(strResult);
				
				if (!isSaveResult)
					continue;
				
				// DB insert
				for (MlResult result : listResult) {
//					// 直前オッズが存在する場合のみDBに保存する
//					if (result.getBetOdds() == null) {
//						continue;
//					}
					mapper.insert(result);
				}
			}
			
			logger.debug(exNo + " processed... " + ymd + "/" + resultEndYmd);
			//session.commit();
			
			// 하루 증가
			currDate = currDate.plusDays(1);
		}

		// 既存実験番号削除
		if (isSaveStat) {
			MlEvaluationExample exam;
			exam = new MlEvaluationExample();
			exam.createCriteria().andResultnoEqualTo(exNo).andResultTypeEqualTo(prop.getString("result_type")).andEvaluationsIdEqualTo(evaluationsId);
			mapperEval.deleteByExample(exam);
			
			MlBorkEvaluationExample borkExam = new MlBorkEvaluationExample();
			borkExam.createCriteria().andResultnoEqualTo(exNo).andResultTypeEqualTo(prop.getString("result_type")).andEvaluationsIdEqualTo(evaluationsId);
			mapperBorkEval.deleteByExample(borkExam);

//			MlRorkEvaluationExample rorkExam = new MlRorkEvaluationExample();
//			rorkExam.createCriteria().andResultnoEqualTo(exNo).andResultTypeEqualTo(prop.getString("result_type")).andEvaluationsIdEqualTo(evaluationsId);
//			mapperRorkEval.deleteByExample(rorkExam);
			
			MlRangeEvaluationExample rangeExam = new MlRangeEvaluationExample();
			rangeExam.createCriteria().andResultnoEqualTo(exNo).andResultTypeEqualTo(prop.getString("result_type")).andEvaluationsIdEqualTo(evaluationsId);
			mapperRangeEval.deleteByExample(rangeExam);
			
			MlPrEvaluationExample prExam = new MlPrEvaluationExample();
			prExam.createCriteria().andResultnoEqualTo(exNo).andResultTypeEqualTo(prop.getString("result_type")).andEvaluationsIdEqualTo(evaluationsId);
			mapperPrEval.deleteByExample(prExam);
		}
		
		if (isSaveGraph || isSaveStat) {
			// 実験結果の統計データをDBに保存する
			int splitNum = prop.getInteger("split");
			for (ResultStat stat : statBuilder.getMapStat().values()) {
				
				// ！注意：graph出力用データがstatに更新されるためDB保存に関係なくcreateMlEvaluationを呼び出しておく必要がある
				MlEvaluationCreator evaluationCreator = new MlEvaluationCreator(stat);
				MlBorkEvaluationCreator borkEvaluationCreator = new MlBorkEvaluationCreator(stat);
//				MlRorkEvaluationCreator rorkEvaluationCreator = new MlRorkEvaluationCreator(stat);
				MlRangeEvaluationCreator rangeEvalCreator = new MlRangeEvaluationCreator(stat);
				MlPrEvaluationCreator prEvaluationCreator = new MlPrEvaluationCreator(stat);
				
				// グラフ出力時必須
				MlEvaluation rec = evaluationCreator.create(exNo, usedModelNo, prop.getString("pattern_id"), splitNum);
				MlRangeEvaluation rangeRec = rangeEvalCreator.create(exNo, usedModelNo, prop.getString("pattern_id"), splitNum);
				
				// オプション
				MlBorkEvaluation borkRec = borkEvaluationCreator.create(exNo, usedModelNo, prop.getString("pattern_id"), splitNum);
//				MlRorkEvaluation rorkRec = rorkEvaluationCreator.create(exNo, usedModelNo, prop.getString("pattern_id"), splitNum);
				MlPrEvaluation prRec = prEvaluationCreator.create(exNo, usedModelNo, prop.getString("pattern_id"), splitNum); 
				if (rec == null) { // 的中が一つも存在しない場合
					continue;
				}
				
				if (isSaveStat) {
					// insert
					mapperEval.insert(rec);
					mapperBorkEval.insert(borkRec);
//					mapperRorkEval.insert(rorkRec);
					mapperRangeEval.insert(rangeRec);
					mapperPrEval.insert(prRec);
				}
			}
		}

		session.commit();
		
		// 実験結果の統計データを基にgraphをファイルに出力する
		if (isSaveGraph) {
			// 結果出力対象の統計データマップを最低betcntでフィルタする
			statMap = limitBetcnt(statBuilder.getMapStat(), minBetcnt);
			
			// 結果出力対象の統計データマップを最低betcntでフィルタする
			statMap = limitBetrate(statMap, minBetrate);
			
			graphBuilder.save(statMap);
		}

		DatabaseUtil.close(session);
	}
	
	List<MlResult> createResult(DBRecord rec, AbstractResultCreator resultCreator) throws Exception {
		return resultCreator.execute(rec, prop.getString("bettype"), prop.getString("kumiban"));
	}
	
	/**
	 * 統計出力データからbetrateが設定値以下は除外する
	 * @param srcMap
	 * @return
	 */
	SortedMap<String, ResultStat> limitBetrate(SortedMap<String, ResultStat> srcMap, double minBetrate) {
		if (minBetrate <= 0) {
			return srcMap;
		}

		SortedMap<String, ResultStat> result = new TreeMap<>();
		for (Entry<String, ResultStat> entry : srcMap.entrySet()) {
			if (entry.getValue().betrate < minBetrate) {
				logger.info("limit betrate pattern excluded : " + entry.getValue().pattern);
				continue;
			}
			result.put(entry.getKey(), entry.getValue());
		}
		
		return result;
	}
	
	/**
	 * 統計出力データからbetrateが設定値以下は除外する
	 * @param srcMap
	 * @return
	 */
	SortedMap<String, ResultStat> limitBetcnt(SortedMap<String, ResultStat> srcMap, int minBetcnt) {
		if (minBetcnt <= 0) {
			return srcMap;
		}

		SortedMap<String, ResultStat> result = new TreeMap<>();
		for (Entry<String, ResultStat> entry : srcMap.entrySet()) {
			if (entry.getValue().sumOfBet < minBetcnt) {
				logger.info("limit betcnt pattern excluded : " + entry.getValue().pattern);
				continue;
			}
			result.put(entry.getKey(), entry.getValue());
		}
		
		return result;
	}

	/** DB data load */
	List<DBRecord> loadDB(String fromYmd, String toYmd) throws Exception {
		AbstractRaceDataLoader loader = new DBRaceDataLoader();
		return loader.load(fromYmd, toYmd);
	}
	
	/** 직전옺즈가 잘못 취득된 날짜들이 있다.
	 * 20191116 옺즈는 나중에 취급하기로 하고 일단 투표는 실행시킨다.  
	 * @param ymd
	 * @return
	 */
	private boolean isValidDay(String ymd) {
/*		int intYmd = Integer.valueOf(ymd);
		
		// 시뮬레이션 결과의 월단위 옺즈통계에 오차를 발생시키므로
		// 20190101-20190109(確定オッズ） 20190117-20190124(オッズ取得間隔変更） 사이는 아예 투표를 하지 않게 하자.
		if ( (intYmd >= 20190101 && intYmd <= 20190109) || 
				(intYmd >= 20190117 && intYmd <= 20190124) ||
				(intYmd >= 20190611 && intYmd <= 20190623) 
				) {
			return false;
		}
	*/	
		return true;
	}
	
	
	public static void main(String[] args) {
//		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
//		String exNoList = "1";
		String propertyFilepath = args[0];
		String exNoList = args[1];

		List<String> listExno = StringUtil.parseNumericOptionsString(exNoList);

		try {
			// PropertyUtilを使っている箇所もあるので互換性確保のために
			PropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			MLResultGenerator generator = new MLResultGenerator();
			generator.execute(listExno);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

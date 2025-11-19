package com.pengkong.boatrace.exp10;

import static org.mockito.ArgumentMatchers.contains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.model.ModelInfoManager;
import com.pengkong.boatrace.exp10.property.ClazzTemplate;
import com.pengkong.boatrace.exp10.property.FeatureSetTemplate;
import com.pengkong.boatrace.exp10.property.FeatureTemplate;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.remote.client.Classification;
import com.pengkong.boatrace.exp10.remote.client.RemoteExpectedClassifier;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.MlExpectedMapper;
import com.pengkong.boatrace.mybatis.client.MlExpectedPlusMapper;
import com.pengkong.boatrace.mybatis.entity.MlExpected;
import com.pengkong.boatrace.mybatis.entity.MlExpectedExample;
import com.pengkong.boatrace.mybatis.entity.MlExpectedPlus;
import com.pengkong.boatrace.mybatis.entity.MlExpectedPlusExample;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.MathUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.BeforeOddsProvider;
import com.pengkong.boatrace.exp10.odds.provider.ResultOddsProvider;
import com.pengkong.boatrace.exp10.odds.provider.OddsProviderInterface;

/**
 * modelに対してclassificationを生成してDBに格納する
 * 
 * Required properties
 *   target_db_resource, model_no, model_start_ymd, model_end_ymd, grade_condition, classification_sql
 * 
 * @author ttolt
 *
 */
public class MLExpectedClassificationGenerator {
	Logger logger = LoggerFactory.getLogger(MLExpectedClassificationGenerator.class);

	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** feature定義 */
	FeatureTemplate featureTemplate = FeatureTemplate.getInstance();
	
	/** feature set 定義 */
	FeatureSetTemplate featureSetTemplate = FeatureSetTemplate.getInstance();

	/** class定義 */
	ClazzTemplate classTemplate = ClazzTemplate.getInstance();
	
	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();

	/** MLのリモートクラス */
	RemoteExpectedClassifier rc;

	OddsProviderInterface bOP = new BeforeOddsProvider();
	OddsProviderInterface rOP = new ResultOddsProvider();

	/**
	 * 実験番号リストに対して一括で実験を実行する
	 * @param modelNoList モデル番号一覧 例）99100,11609
	 * @throws Exception
	 */
	protected void execute(String modelNoList) throws Exception {
		// 実験番号mapを生成する
		String[] modelNos = modelNoList.split(Delimeter.COMMA.getValue());
		
		HashMap<String, String> mapModelNo = new HashMap<>();
		for (String modelNo : modelNos) {
			mapModelNo.put(modelNo, modelNo);
		}
		
		// propの巡回前にモデル情報をscanして置く(scan中にpropを一回巡回するため）!!!
		ModelInfoManager.getInstance().scan();
		
		// リモートML通信open
		rc = new RemoteExpectedClassifier();
		rc.connect();
		try {
			prop.reset("file_model_config");
			while(prop.hasNext()) {
				prop.next();
				if (!mapModelNo.containsKey(prop.getString("model_no"))) 
					continue;
				// 実験実行
				executeExperiment(prop.getString("model_no"));
			}
		} finally {
			// リモートML通信close
			rc.disconnect();
		}
	}

	/**
	 * モデル番号毎に実験実行 
	 * @param modelNo モデル番号
	 * @throws Exception
	 */
	protected void executeExperiment(String modelNo) throws Exception {
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			// classification生成する期間。onlineの場合、overrideされる 
			String[] modelPeriod = getModelPeriod();
			
			String modelStartYmd = modelPeriod[0];
			String modelEndYmd = modelPeriod[1];
			
			// DB取得(classification対象）
			List<DBRecord> listData = loadDB(session, modelStartYmd, modelEndYmd);
			
			// 결과범위내 데이터를 날짜별로 분류
			HashMapList<DBRecord> mapListYmd = new HashMapList<>();
			for (DBRecord rec : listData) {
				String ymd = rec.getString("ymd");
				mapListYmd.addItem(ymd, rec);
			}

			DateTime currDate = BoatUtil.parseYmd(modelStartYmd);
			DateTime toDate = BoatUtil.parseYmd(modelEndYmd);
			// 결과 생성 날짜범위 루프
			while (currDate.compareTo(toDate) <= 0) {
				String ymd = BoatUtil.formatYmd(currDate);
				
				// 해당날짜의 데이터 취득
				List<DBRecord> listRec = mapListYmd.get(ymd);
				if (listRec == null) {
					currDate = currDate.plusDays(1);
					continue;
				}
				
				// 当該日付の既存classificationを削除
				deleteDB(session, modelNo, ymd);
				
				// 해당날짜의 레이스 데이터 루프
				for (DBRecord dbRec : listRec) {
					// classify DBから取得する場合のためにsessionを引き渡す。
					List<Classification> listClassification = rc.classify(session, dbRec);
					
					// db insert
					insertDB(modelNo, session, dbRec, listClassification);
				}
				
				logger.debug(modelNo + " ml_(pork)_classification inserted. " + ymd + "/" + modelEndYmd);
				
				// 하루 증가
				currDate = currDate.plusDays(1);
				
				session.commit();
			}
		} finally {
			DatabaseUtil.close(session);		
		}
	}
	
	protected void deleteDB(SqlSession session, String modelNo, String ymd) throws Exception {
		MlExpectedMapper mapper = session.getMapper(MlExpectedMapper.class);
		MlExpectedExample exam = new MlExpectedExample();
		exam.createCriteria().andModelnoEqualTo(modelNo).andYmdEqualTo(ymd);
		mapper.deleteByExample(exam);

		MlExpectedPlusMapper mapperPlus = session.getMapper(MlExpectedPlusMapper.class);
		MlExpectedPlusExample examPlus = new MlExpectedPlusExample();
		examPlus.createCriteria().andModelnoEqualTo(modelNo).andYmdEqualTo(ymd);
		mapperPlus.deleteByExample(examPlus);
	}

	/** classification結果をDBにinsertする */
	protected void insertDB(String exNo, SqlSession session, DBRecord dbRec, List<Classification> listClassification) throws Exception {
		MlExpectedMapper mapperExp = session.getMapper(MlExpectedMapper.class);
		MlExpectedPlusMapper mapperPlus = session.getMapper(MlExpectedPlusMapper.class);
		int size = listClassification.size();
		if (size != 120) {
			logger.error("classification size is not 120. " + size);
			return;
		}
		
		String ymd = dbRec.getString("ymd");
		String jyo = dbRec.getString("jyocd");
		Short raceno = (short)dbRec.getInt("raceno");
		double[] prob = new double[size];
		double[] bor = new double[size];
		int[] bork = new int[size];
		double[] ror = new double[size];
		int[] rork = new int[size];
		double[] bexpected = new double[size];
		double[] rexpected = new double[size];

		List<String> pbkumiban = new ArrayList<>();
		List<Double> pbprob = new ArrayList<>();
		List<Double> pbor = new ArrayList<>();
		List<Integer> pbork = new ArrayList<>();
		List<Double> pbexp = new ArrayList<>();
		List<String> prkumiban = new ArrayList<>();
		List<Double> prprob = new ArrayList<>();
		List<Double> pror = new ArrayList<>();
		List<Integer> prork = new ArrayList<>();
		List<Double> prexp = new ArrayList<>();
		int bcnt = 0;
		int rcnt = 0;

		for (int i = 0; i < size; i++) {
			Classification classification = listClassification.get(i);
			Odds bOdds = bOP.get(ymd, jyo, String.valueOf(raceno), BetType._3T.getValue(), classification.prediction);
			Odds rOdds = rOP.get(ymd, jyo, String.valueOf(raceno), BetType._3T.getValue(), classification.prediction);
			if (bOdds == null || rOdds == null) {
				logger.warn("bOdds or rOdds is null. " + ymd + ", " + jyo + ", " + raceno + ", " + classification.prediction);
				continue;
			}
			prob[i] = classification.probability;
			bor[i] = bOdds.value;
			bork[i] = bOdds.rank;
			ror[i] = rOdds.value;
			rork[i] = rOdds.rank;
			bexpected[i] = MathUtil.scale2(prob[i] * bor[i]);
			rexpected[i] = MathUtil.scale2(prob[i] * ror[i]);

			if ( bexpected[i] > 1.0 ) {
				bcnt++;
				pbkumiban.add(classification.prediction);
				pbprob.add(prob[i]);
				pbor.add(bor[i]);
				pbork.add(bork[i]);
				pbexp.add(bexpected[i]);
			}
			if ( rexpected[i] > 1.0 ) {
				rcnt++;
				prkumiban.add(classification.prediction);
				prprob.add(prob[i]);
				pror.add(ror[i]);
				prork.add(rork[i]);
				prexp.add(rexpected[i]);
			}
		}

		// db insert
		MlExpected recExp = new MlExpected();
		recExp.setModelno(exNo);
		recExp.setYmd(ymd);
		recExp.setJyocd(jyo);
		recExp.setRaceno(raceno);
		recExp.setProb(prob);
		recExp.setBor(bor);
		recExp.setBork(bork);
		recExp.setRor(ror);
		recExp.setRork(rork);
		recExp.setBexp(bexpected);
		recExp.setRexp(rexpected);
		mapperExp.insert(recExp);

		MlExpectedPlus recExpPlus = new MlExpectedPlus();
		recExpPlus.setModelno(exNo);
		recExpPlus.setYmd(ymd);
		recExpPlus.setJyocd(jyo);
		recExpPlus.setRaceno(raceno);
		recExpPlus.setBkumiban(pbkumiban.toArray(new String[0]));
		recExpPlus.setBprob(ArrayUtils.toPrimitive(pbprob.toArray(new Double[0])));
		recExpPlus.setBor(ArrayUtils.toPrimitive(pbor.toArray(new Double[0])));
		recExpPlus.setBork(ArrayUtils.toPrimitive(pbork.toArray(new Integer[0])));
		recExpPlus.setBexp(ArrayUtils.toPrimitive(pbexp.toArray(new Double[0])));
		recExpPlus.setBcnt(bcnt);
		recExpPlus.setRkumiban(prkumiban.toArray(new String[0]));
		recExpPlus.setRprob(ArrayUtils.toPrimitive(prprob.toArray(new Double[0])));
		recExpPlus.setRor(ArrayUtils.toPrimitive(pror.toArray(new Double[0])));
		recExpPlus.setRork(ArrayUtils.toPrimitive(prork.toArray(new Integer[0])));
		recExpPlus.setRexp(ArrayUtils.toPrimitive(prexp.toArray(new Double[0])));
		recExpPlus.setRcnt(rcnt);
		mapperPlus.insert(recExpPlus);
	}
	
	/** classification対象期間を取得する */
	protected String[] getModelPeriod() {
		String modelStartYmd = prop.getString("model_start_ymd");
		String modelEndYmd = prop.getString("model_end_ymd");
		
		return new String[] {modelStartYmd, modelEndYmd};
	}

	/**
	 * classificationを生成する対象のＤＢデータを取得する
	 * @return
	 * @throws Exception
	 */
	protected List<DBRecord> loadDB(SqlSession session, String modelStartYmd, String modelEndYmd) throws Exception {
		// classification用sql取得
		String sql = sqlTpl.get(prop.getString("classification_sql_id"));
		
		// 期間埋め込み
		sql = sql.replace("{fromYmd}", modelStartYmd);
		sql = sql.replace("{toYmd}", modelEndYmd);
		
		// 対象grade条件
		sql = sql.replace("{grade_condition}", prop.getString("grade_condition"));

		// feature条件
		sql = sql.replace("{features}", getFeaturesSql());
		
		HashMap<String, String> mapParam = new HashMap<>();
		mapParam.put("sql", sql);
		
		// 디비 데이터 일람 취득
		CustomMapper customMapper = session.getMapper(CustomMapper.class);
		List<DBRecord> results = customMapper.selectSql(mapParam);
		if (results.size() <= 0) {
			throw new Exception("db has no data. sql=" + sql);
		}
		
		return results;
	}
	
	/**
	 * rank1,2,3のfeatureすべてのsql分を取得する
	 * @return
	 */
	protected String getFeaturesSql() throws Exception {
		List<String> listFeatureId = new ArrayList<>();
		String[] featureIds;
		// rank1,2,3のarff featureをすべて取得
		
		int rankCnt = prop.getInteger("rankcnt");
		for (int rankNo = 1; rankNo <= rankCnt; rankNo++) {
			String featureStr = featureSetTemplate.getFeatureIdString(prop.getString("features_rank" + rankNo));
			if (featureStr.equals("x"))
				continue;
			featureIds = featureStr.split(Delimeter.COMMA.getValue());
			for (String featureId : featureIds) {
				if (listFeatureId.contains(featureId)) {
					continue;
				}
				listFeatureId.add(featureId);
			}
		}
		
		return featureTemplate.getFeaturesSqlForInstance(listFeatureId.toArray(new String[listFeatureId.size()]));
	}

	public static void main(String[] args) {
		//String propertyFilepath = "C:/Dev/github/pod_boatrace/properties/expr10/expr10.properties";
		//String exNoList = "39104";
		String propertyFilepath = args[0];
		String exNoList = args[1];
		try {
			// PropertyUtilを使っている箇所もあるので互換性確保のために
			PropertyUtil.getInstance().addFile(propertyFilepath);
			
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			MLExpectedClassificationGenerator generator = new MLExpectedClassificationGenerator();
			generator.execute(exNoList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.pengkong.boatrace.exp10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.model.ModelInfoManager;
import com.pengkong.boatrace.exp10.property.ClazzTemplate;
import com.pengkong.boatrace.exp10.property.FeatureSetTemplate;
import com.pengkong.boatrace.exp10.property.FeatureTemplate;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.remote.client.Classification;
import com.pengkong.boatrace.exp10.remote.client.RemoteClassifier;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.MlClassificationMapper;
import com.pengkong.boatrace.mybatis.client.OddsPredictMapper;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.mybatis.entity.MlClassificationExample;
import com.pengkong.boatrace.mybatis.entity.OddsPredict;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * modelに対してclassificationを生成してDBに格納する
 * 
 * Required properties
 *   target_db_resource, model_no, model_start_ymd, model_end_ymd, grade_condition, classification_sql
 * 
 * @author ttolt
 *
 */
public class MLClassificationGenerator {
	Logger logger = LoggerFactory.getLogger(MLClassificationGenerator.class);

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
	RemoteClassifier rc;
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
		rc = new RemoteClassifier();
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
		MlClassificationMapper mapper = session.getMapper(MlClassificationMapper.class);
		MlClassificationExample exam = new MlClassificationExample();
		exam.createCriteria().andModelnoEqualTo(modelNo).andYmdEqualTo(ymd);
		mapper.deleteByExample(exam);
	}

	/** classification結果をDBにinsertする */
	protected void insertDB(String exNo, SqlSession session, DBRecord dbRec, List<Classification> listClassification) throws Exception {
		OddsPredictMapper mapperRegr = session.getMapper(OddsPredictMapper.class); // odds regression 
		MlClassificationMapper mapper = session.getMapper(MlClassificationMapper.class);
		
		// db insert
		if (isRegression()) {
			mapperRegr.insert(createInsertOddsPredictDto(exNo, dbRec, listClassification));
		} else {
			MlClassification rec = new MlClassification();
			rec.setModelno(exNo);
			rec.setYmd(dbRec.getString("ymd"));
			rec.setJyocd(dbRec.getString("jyocd"));
			rec.setRaceno((short) dbRec.getInt("raceno"));
			rec.setSime(dbRec.getString("sime"));
			rec.setPattern(dbRec.getString("pattern"));

			rec.setPrediction1(listClassification.get(0).prediction);
			rec.setProbability1(listClassification.get(0).probability);
			
			if (listClassification.size() > 1) {
				rec.setPrediction2(listClassification.get(1).prediction);
				rec.setProbability2(listClassification.get(1).probability);
			}

			if (listClassification.size() > 2) {
				rec.setPrediction3(listClassification.get(2).prediction);
				rec.setProbability3(listClassification.get(2).probability);
			}
			
			DescriptiveStatistics desc = new DescriptiveStatistics();
			for (int i = 0; i < listClassification.size(); i++) {
				desc.addValue(listClassification.get(i).probability);
			}
			// 記述統計値を設定
			rec.setMin(desc.getMin());
			rec.setMax(desc.getMax());
			rec.setMean(desc.getMean());
			rec.setGeoMean(desc.getGeometricMean());
			rec.setQuadMean(desc.getQuadraticMean());
			rec.setStdDeviation(desc.getStandardDeviation());
			rec.setVariance(desc.getVariance());
			rec.setPopuVariance(desc.getPopulationVariance());
			rec.setSkewness(desc.getSkewness());
			rec.setKurtosis(desc.getKurtosis());
			
			mapper.insert(rec);
			//mapper.insert(createInsertDto(exNo, dbRec, listClassification));	
		}
	}
	
	/** classification対象期間を取得する */
	protected String[] getModelPeriod() {
		String modelStartYmd = prop.getString("model_start_ymd");
		String modelEndYmd = prop.getString("model_end_ymd");
		
		return new String[] {modelStartYmd, modelEndYmd};
	}

	protected OddsPredict createInsertOddsPredictDto(String exNo, DBRecord dbRec, List<Classification> listClassification) throws Exception {
		OddsPredict rec = new OddsPredict();
		rec.setModelno(exNo);
		rec.setYmd(dbRec.getString("ymd"));
		rec.setJyocd(dbRec.getString("jyocd"));
		rec.setRaceno((short) dbRec.getInt("raceno"));
		String[] token = prop.getString("class_rank1").split(Delimeter.DASH.getValue());  // ex) rg-1T-1
		rec.setBettype(token[1]);
		rec.setKumiban(token[2]);
		rec.setOdds(listClassification.get(0).probability);
		
		return rec;
	}
	
	boolean isRegression() {
		return (prop.getString("algorithm_rank1").startsWith("rg_"));
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
		for (int rankNo = 1; rankNo <= 3; rankNo++) {
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
//		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
//		String exNoList = "1";
		String propertyFilepath = args[0];
		String exNoList = args[1];
		try {
			// PropertyUtilを使っている箇所もあるので互換性確保のために
			PropertyUtil.getInstance().addFile(propertyFilepath);
			
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			MLClassificationGenerator generator = new MLClassificationGenerator();
			generator.execute(exNoList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

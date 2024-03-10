package com.pengkong.boatrace.exp10;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.model.ModelInfoManager;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.client.Classification;
import com.pengkong.boatrace.exp10.remote.client.RemoteRankingClassifier;
import com.pengkong.boatrace.mybatis.client.MlClassificationMapper;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * modelに対してranking classificationを生成してDBに格納する
 * 
 * Required properties
 *   target_db_resource, model_no, model_start_ymd, model_end_ymd, grade_condition, classification_sql
 * 
 * @author ttolt
 *
 */
public class MLRankingClassificationGenerator extends MLClassificationGenerator {
	Logger logger = LoggerFactory.getLogger(MLRankingClassificationGenerator.class);

	/** MLのリモートクラス */
	RemoteRankingClassifier rrc;
	
	@Override
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
		rrc = new RemoteRankingClassifier();
		rrc.connect();
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
			rrc.disconnect();
		}
	}
	
	/**
	 * モデル番号毎に実験実行 
	 * @param modelNo モデル番号
	 * @throws Exception
	 */
	@Override
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
				List<DBRecord> listYmdRec = mapListYmd.get(ymd);
				if (listYmdRec == null) {
					currDate = currDate.plusDays(1);
					continue;
				}

				// 当該日付の既存classificationを削除
				deleteDB(session, modelNo, ymd);
				
				// 해당날자의 데이터를 레이스단위로 다시 쪼갠다. ranker에 대해서는 racer단위의 복수레코드(6) 를 입력으로 하기때문.
				HashMapList<DBRecord> mapListRace = new HashMapList<>();
				for (DBRecord rec : listYmdRec) {
					String raceId = String.valueOf( rec.getString("raceid"));
					mapListRace.addItem(raceId, rec);
				}

				// 해당날짜의 레이스 데이터 루프
				for (String raceid : mapListRace.keySet()) {
					List<DBRecord> listRec = mapListRace.get(raceid);
					
					List<Classification> listClassification = rrc.classify(session, listRec);
					
					// db insert
					insertDB(modelNo, session, listRec.get(0) , listClassification);
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
	
	/** classification結果をDBにinsertする */
	protected void insertDB(String exNo, SqlSession session, DBRecord dbRec, List<Classification> listClassification) throws Exception {
		MlClassificationMapper mapper = session.getMapper(MlClassificationMapper.class);

		MlClassification rec = new MlClassification();
		rec.setModelno(exNo);
		rec.setYmd(dbRec.getString("ymd"));
		rec.setJyocd(dbRec.getString("jyocd"));
		rec.setRaceno((short) dbRec.getInt("raceno"));
		rec.setSime(dbRec.getString("sime"));
		rec.setPattern(dbRec.getString("pattern"));
		rec.setPrediction1(listClassification.get(0).prediction);
		rec.setProbability1(listClassification.get(0).probability);
		rec.setPrediction2(listClassification.get(1).prediction);
		rec.setProbability2(listClassification.get(1).probability);
		rec.setPrediction3(listClassification.get(2).prediction);
		rec.setProbability3(listClassification.get(2).probability);
		rec.setPrediction4(listClassification.get(3).prediction);
		rec.setProbability4(listClassification.get(3).probability);
		rec.setPrediction5(listClassification.get(4).prediction);
		rec.setProbability5(listClassification.get(4).probability);
		rec.setPrediction6(listClassification.get(5).prediction);
		rec.setProbability6(listClassification.get(5).probability);
		
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

			MLRankingClassificationGenerator generator = new MLRankingClassificationGenerator();
			generator.execute(exNoList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

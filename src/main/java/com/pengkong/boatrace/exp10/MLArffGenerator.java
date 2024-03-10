package com.pengkong.boatrace.exp10;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.exp10.arff.MLArffCreator;
import com.pengkong.boatrace.exp10.odds.provider.OddsProviderInterface;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.data.rmi.client.RmiBeforeOddsProvider;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.server.db.dto.YmdBeforeRangeDbList;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * arff 생성기.
 *   1. 디비데이터를 로드하여 패턴별로 격납 (패턴은 nopattern고정)
 *   2. arff파일 생성
 *   3. model생성
 *
 * @rev 20211125 pythonモデル生成に対応
 * 
 * Required properties
 *   model_no, model_start_ymd, model_end_ymd, model_data_days, model_interval_days, data_use_range.
 *   target_db_resource, arff_sql_rank1, arff_sql_rank2, arff_sql_rank3, grade_condition
 *   dir_model_arff, features_rank1, features_rank2, features_rank3, class_rank1, class_rank2, class_rank3
 *   dir_model_release, classpath, algorithm_rank1, algorithm_rank2, algorithm_rank3, 
 *   outlier_method, outlier_field
 *
 */
public class MLArffGenerator extends MLModelGeneratorBase {
	Logger logger = LoggerFactory.getLogger(MLArffGenerator.class);

	/** 패턴별 디비 데이터 리스트 パタンはnopatterh固定*/
	private HashMapList<DBRecord> mapPattern = new HashMapList<>();
	
	/**
	 * 実験番号に対してmodelを生成する
	 * @param exNo 実験番号
	 * @throws Exception
	 */
	void executeExperiment(String exNo) throws Exception {
		// 결과생성 범위시작,끝의 하루전을 모델생성범위로 한다.
		String modelStartYmd = BoatUtil.daysBeforeYmd(prop.getString("model_start_ymd"), 1);
		String modelEndYmd = BoatUtil.daysBeforeYmd(prop.getString("model_end_ymd"), 1);
		int modelDataDays = Integer.parseInt(prop.getString("model_data_days"));
		int modelIntervalDays = prop.getInteger("model_interval_days");
		boolean isDataUseRange = prop.getString("data_use_range").equals("yes");

		// arff파일 디렉토리`
		String dirArff = prop.getString("dir_model_arff");
		FileUtil.createDirIfNotExist(dirArff);
	
		MLArffCreator arff = new MLArffCreator();
		
		YmdBeforeRangeDbList listRange = new YmdBeforeRangeDbList();
		// 랭크 1,2,3 루프
		for (int rankNo = 1; rankNo <= 3; rankNo++) {
			// 他モデルを流用するまたはモデルは不要
			if (isReferentialModel(rankNo) || isRankNotExist(rankNo)) {
				continue;
			}
			
			// db load
			List<DBRecord> results = loadDB(rankNo, modelStartYmd, modelEndYmd, modelDataDays);
			
			// select結果をパタン別分類
			mapPattern = assignPattern(results);
			// 패턴 루프 
			for (String pattern : mapPattern.keySet()) { // nopatternで固定
				// 모델, evaluation 디렉토리
				String dirModel = prop.getString("dir_model_release") + StringUtil.leftPad(exNo, BoatConst.LEFT_PAD, "0")  + "/" + rankNo + "/" + pattern + "/";
				String dirEval = prop.getString("dir_model_evaluation");
				FileUtil.createDirIfNotExist(dirModel);
				FileUtil.createDirIfNotExist(dirEval);

				// 패턴별 디비데이터 재로딩
				listRange.initialize(modelIntervalDays, modelDataDays, modelStartYmd, modelEndYmd, mapPattern.get(pattern));
				
				// 데이터를 범위로 취할 것인지 누적적으로 취할 것인지의 여부 yes:데이터를 범위로 취득한다. 
				listRange.setUseRangeStartKey(isDataUseRange);
				
				List<DBRecord> listDb;
				// 모델생성간격으로 루프
				while( (listDb = listRange.get()) != null) {
					
					// 外れ値を処理する
					if (isProcessOutlier()) {
						listDb = processOutliers(listDb);	
					}
					
					// arffファイル モデル・ランク毎に生成
					String arffFilepath = dirArff + createArffFileName(exNo, rankNo);
					
					arff.create(listDb, getFeatureList(rankNo), getClazz(rankNo), arffFilepath);

					logger.debug("generating : " + arffFilepath);
				}
			}
		}
	}
	
	public static void main(String[] args) {
//		String propertyFilepath = "C:/Dev/github/pod_boatrace/properties/expr10/expr10.properties";
//		String exNoList = "79200";
		
		String propertyFilepath = args[0];
		String exNoList = args[1];
		try {
			MLPropertyUtil prop = MLPropertyUtil.getInstance();
			prop.addFile(propertyFilepath);
			
			MLArffGenerator generator = new MLArffGenerator();
			generator.execute(exNoList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

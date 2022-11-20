package com.pengkong.boatrace.exp10;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.enums.OutlierMethod;
import com.pengkong.boatrace.exp10.odds.provider.OddsProviderInterface;
import com.pengkong.boatrace.exp10.property.AlgorithmTemplate;
import com.pengkong.boatrace.exp10.property.Clazz;
import com.pengkong.boatrace.exp10.property.ClazzTemplate;
import com.pengkong.boatrace.exp10.property.Feature;
import com.pengkong.boatrace.exp10.property.FeatureSetTemplate;
import com.pengkong.boatrace.exp10.property.FeatureTemplate;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.property.SqlTemplate;
import com.pengkong.boatrace.exp10.simulation.data.rmi.client.RmiBeforeOddsProvider;
import com.pengkong.boatrace.exp10.util.MLUtil;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

public abstract class MLModelGeneratorBase {
	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** feature定義 */
	FeatureTemplate featureTemplate = FeatureTemplate.getInstance();

	/** feature set 定義 */
	FeatureSetTemplate featureSetTemplate = FeatureSetTemplate.getInstance();

	/** class定義 */
	ClazzTemplate classTemplate = ClazzTemplate.getInstance();
	
	/** algorithm定義 */
	AlgorithmTemplate algorithmTemplate = AlgorithmTemplate.getInstance();

	/** sql template */
	SqlTemplate sqlTpl = SqlTemplate.getInstance();

	public MLModelGeneratorBase() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * javaモデルかpythonモデルかで異なる拡張子をつけたファイル名を返却する
	 * @param exNo 実験番号
	 * @param rankNo rankNo
	 * @return arff ファイル名
	 */
	String createArffFileName(String exNo, int rankNo) {
		String fileName = StringUtil.leftPad(exNo, BoatConst.LEFT_PAD, "0") + "_rank" + rankNo;
		if (isPythonModel(rankNo)) {
			fileName += ".csv";
		} else {
			fileName += ".arff";
		}
		
		return fileName;
	}
	
	/**
	 * 該当ランクのモデルをすでにあるモデルで流用しないか返却する
	 * @param rankNo rank no
	 * @return true=流用する false=流用しない 
	 */
	boolean isReferentialModel(int rankNo) {
		return MLUtil.isReferentialClassId(prop.getString("class_rank" + rankNo));
	}
	
	/**
	 * @param rankNo
	 * @return true=モデル生成不要(3着完成型モデルなど） false=モデル生成必要
	 */
	boolean isRankNotExist(int rankNo) {
		return (prop.getString("class_rank" + rankNo).equals("x"));
	}
	
	/**
	 * @param rankNo
	 * @return true=pythonモデル false=javaモデル
	 */
	boolean isPythonModel(int rankNo) {
		return (prop.getString("algorithm_rank" + rankNo).endsWith("_py"));
	}
	
	boolean isRegressionModel(int rankNo) {
		return (prop.getString("algorithm_rank" + rankNo).startsWith("rg_"));
	}

	boolean isProcessOutlier() {
		return !(prop.getString("outlier_method").equals(OutlierMethod.NO.getValue()));
	}
	
	/**
	 * return list of Feature
	 * @param rankNo  1 or 2 or 3
	 */
	List<Feature> getFeatureList(int rankNo) throws Exception {
		String featureSetId = prop.getString("features_rank" + rankNo);
		String[] featureIds = featureSetTemplate.getFeatureIds(featureSetId);
		
		return featureTemplate.getFeatureList(featureIds);
	}

	/**
	 * return Clazz by id
	 * @param rankNo 1 or 2 or 3
	 */
	Clazz getClazz(int rankNo) throws Exception {
		String classId = prop.getString("class_rank" + rankNo);
		
		return classTemplate.getClazz(classId);
	}
	
	/**
	 * 外れ値を除外処理を行う https://qiita.com/papi_tokei/items/6f77a2a250752cfa850b  １次元データの外れ値の検出
	 * @param list
	 * @return
	 */
	List<DBRecord> processOutliers(List<DBRecord> list) {
		String outlierMethod = prop.getString("outlier_method");
		String outlierFieldName = prop.getString("outlier_field");
		
		// 外れ値測定対象の値をソートする
		List<Double> input = new ArrayList<>();
		for( DBRecord rec : list) {
			input.add(rec.getDouble(outlierFieldName));
		}
		Collections.sort(input);
		
		// 外れ値の境界を求める min, max
		double[] minMax = null;
		if (outlierMethod.equals(OutlierMethod.QUARTILE.getValue())) {
			minMax = MLUtil.getOutlierRangeByQuartile(input);	
		} else if (outlierMethod.equals(OutlierMethod.OCTILE.getValue())) {
			minMax = MLUtil.getOutlierRangeByOctile(input);
		} else if (outlierMethod.equals(OutlierMethod.PERCENTILE.getValue())) {
			minMax = MLUtil.getOurlierRangeByPercentile(input, 0, 0.95);
		}

		// 外れ値の境界外のレコードを削除する
		List<DBRecord> result = new ArrayList<>();
		for (DBRecord rec : list) {
			double outlierValue = rec.getDouble(outlierFieldName);
			if (outlierValue < minMax[0] || outlierValue > minMax[1]) {
				continue;
			}
			result.add(rec);
		}
		
		return result;
	}
	
	/**
	 * 実験番号リストに対して一括で実験を実行する
	 * @param exNoList 実験番号一覧 例）1,2,3,5
	 * @throws Exception
	 */
	public void execute(String exNoList) throws Exception {
		// 実験番号mapを生成する
		String[] exNos = exNoList.split(Delimeter.COMMA.getValue());
		
		HashMap<String, String> mapExNo = new HashMap<>();
		for (String exNo : exNos) {
			mapExNo.put(exNo, exNo);
		}
		
		prop.reset("file_model_config");
		while(prop.hasNext()) {
			prop.next();
			if (!mapExNo.containsKey(prop.getString("model_no"))) 
				continue;
			// 実験実行
			executeExperiment(prop.getString("model_no"));
		}
	}

	/**
	 * 実験番号に対してタスクを実行する
	 * @param exNo 実験番号
	 * @throws Exception
	 */
	abstract void executeExperiment(String exNo) throws Exception;
	
	/**
	 * select結果をパタン別に振り分ける
	 * @param dbRecordList select結果
	 * @return パタン別レコードリスト
	 */
	HashMapList<DBRecord> assignPattern(List<DBRecord> dbRecordList) {
		HashMapList<DBRecord> mapPattern = new HashMapList<>();		
		// 패턴 별로 분리해서 격납
		for (DBRecord rec : dbRecordList) {
			String pattern = rec.getString("pattern");
			if (!isValidPattern(pattern)) {
				continue;
			}
			
			mapPattern.addItem(pattern, rec);
		}
		
		return mapPattern;
	}
	
	/**
	 * DBからmodel生成に必要なデータをすべてロードする。
	 * @param rankNo 1 or 2 or 3
	 * @param modelStartYmd モデル生成の最初年月日
	 * @param modelEndYmd モデル生成の最後年月日
	 * @param modelDataDays モデル生成字使うtrainデータの日数
	 * @return DBレコードリスト
	 * @throws Exception
	 */
	List<DBRecord> loadDB(int rankNo, String modelStartYmd, String modelEndYmd, int modelDataDays) throws Exception {
		List<DBRecord> results;
		
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);		
		CustomMapper customMapper = session.getMapper(CustomMapper.class);
		
		String dataStartYmd = BoatUtil.daysBeforeYmd(modelStartYmd, modelDataDays);
		// rank別sql取得
		String sql = sqlTpl.get(prop.getString("arff_sql_id"));
		
		// sql埋め込み
		sql = sql.replace("{fromYmd}", dataStartYmd);
		sql = sql.replace("{toYmd}", modelEndYmd);

		// feature条件
		String featureSetId = prop.getString("features_rank" + rankNo);
		String[] featureIds = featureSetTemplate.getFeatureIds(featureSetId);
		String featureSql = featureTemplate.getFeaturesSqlForModel(featureIds);
		sql = sql.replace("{features}", featureSql);

		// class values条件
		Clazz clazz = classTemplate.getClazz(prop.getString("class_rank" + rankNo));
		sql = sql.replace("{class_features}", clazz.featuresSql);
		
		// 対象grade条件
		sql = sql.replace("{grade_condition}", prop.getString("grade_condition"));
		
		// rank1,2,3別のclass values制限条件 ex) rank1=条件なし rank2=rank1が1のみ rank3=rank12が12のみ
		sql = sql.replace("{class_condition}", clazz.conditionSql);
		
		HashMap<String, String> mapParam = new HashMap<>();
		mapParam.put("sql", sql);
		
		// 디비 데이터 일람 취득
		results = customMapper.selectSql(mapParam);
		if (results.size() <= 0) {
			throw new Exception("db has no data. sql=" + sql);
		}

		DatabaseUtil.close(session);
		return results;
	}
	
	/**
	 * 모델생성대상 패턴인지를 판정한다.
	 * @param pattern
	 */
	boolean isValidPattern(String pattern) {
		return true;
	}
}

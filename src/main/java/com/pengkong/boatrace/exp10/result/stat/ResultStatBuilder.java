package com.pengkong.boatrace.exp10.result.stat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.ResultHelper;
import com.pengkong.boatrace.mybatis.entity.MlResult;

import lombok.Getter;

public class ResultStatBuilder {
	Logger logger = LoggerFactory.getLogger(ResultStatBuilder.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	/** key=statbettype+kumiban  value=betcnt */
	@Getter
	Map<String, Double> mapTotalBetCnt = new HashMap<>();
	
	/** key = pattern+bettype+kumiban  value=ResultStat */
	@Getter
	SortedMap<String, ResultStat> mapStat = new TreeMap<>();

	ResultStatBuilderHelper statHelper = new ResultStatBuilderHelper();

	String[] tokenKumiban;
	
	boolean isLimitedFormation = false;
	
	private static class InstanceHolder {
		private static final ResultStatBuilder INSTANCE = new ResultStatBuilder();
	}
	
//	public static ResultStatBuilder getInstance() {
//		return InstanceHolder.INSTANCE;
//	}

	/**
	 * 初期化処理.
	 */
	public void clear() {
		mapTotalBetCnt = new HashMap<>();
		mapStat = new TreeMap<>();
		statHelper = new ResultStatBuilderHelper();
		
		isLimitedFormation = prop.getYesNo("limited_formation");
		if (isLimitedFormation) {
			tokenKumiban = ResultHelper.parseKumiban(prop.getString("limited_kumiban"));			
		} else {
			tokenKumiban = ResultHelper.parseKumiban(prop.getString("kumiban"));
		}
	}
	
	/* 以下ファイル保存機能は必要な時にpd_boatrace_oldから復元すること
	 * 統計情報をファイルに保存する
	 * @param exNo 実験番号
	 *
	public void save() throws Exception {
	*/
	
	/**
	 * 累積統計にml_resultデータを追加して、統計データを設定して返却する
	 * @param result ml_result
	 * @return
	 */
	public MlResult add(MlResult result) throws Exception {
		String predictions = ResultHelper.getPredictions(result);
		
		if (isLimitedFormation && !ResultHelper.isValidPredictionsRange(result.getBettype(),
				result.getBetKumiban().split(""), tokenKumiban)) {
			return result;
		}
		
		// bettype, prediction別
		String key = statHelper.createKey(result.getStatBettype(), predictions); 
		if (!mapTotalBetCnt.containsKey(key)) {
			mapTotalBetCnt.put(key, 0.0);
		}
		mapTotalBetCnt.put(key, mapTotalBetCnt.get(key) + 1); // counting

		// bettype, prediction, pattern別
		key = statHelper.createKey(result.getStatBettype(), predictions, result.getPatternid(), result.getPattern());
		ResultStat stat = mapStat.get(key);
		if (stat == null) {
			stat = statHelper.createResultStat(result.getStatBettype(), predictions, result.getPatternid(), result.getPattern());
			mapStat.put(key, stat);
		}
		stat.add(result, mapTotalBetCnt.get(stat.statBettype + stat.kumiban));
		
		return result;
	}

	/**
	 * 累積統計にml_resultデータを追加して、統計データを設定して返却する
	 * @param listResult ml_result list
	 * @return
	 */
	public List<MlResult> addAll(List<MlResult> listResult) throws Exception {
		for (MlResult mlResult : listResult) {
			mlResult = add(mlResult);
		}
		
		return listResult;
	}
}

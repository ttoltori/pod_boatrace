package com.pengkong.boatrace.exp10.result.stat;

import java.util.Arrays;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

/**
 * プロパティに設定されている統計単位に沿った統計処理を行うためのヘルパクラス。
 * 
 * @author ttolt
 *
 */
public class ResultStatBuilderHelper {
	/** 統計単位 ex)statbettype,prediction,pattern */
	List<String> statUnits;
	
	/** 統計開始残高 */
	int statStartBalance = -1;

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/**
	 * プロパティから統計単位及び開始残高を取得する
	 * @throws Exception
	 */
	void initialize() throws Exception {
		this.statStartBalance = prop.getInteger("stat_start_balance");
		statUnits = Arrays.asList(prop.getString("stat_unit").split(Delimeter.COMMA.getValue()));
	}
	
	void ensureInitialized() throws Exception {
		if (statUnits == null) {
			initialize();
		}
	}

	/**
	 * bettype, prediction, patternのキーでResultStatを生成する
	 * @param result MlResult
	 * @return ResultStat
	 * @throws Exception
	 */
	public ResultStat createResultStat(String statBettype, String prediction, String patternId, String pattern) throws Exception {
		ensureInitialized();
		
		statBettype = emptifyIfNotStatUnit("bettype", statBettype);
		prediction = emptifyIfNotStatUnit("prediction", prediction);
		pattern = emptifyIfNotStatUnit("pattern", pattern);
		
		return new ResultStat(statBettype, prediction, patternId, pattern, this.statStartBalance);
	}

	/** bettype, predictionのキーを取得する */
	public String createKey(String statBettype, String prediction) throws Exception{
		ensureInitialized();

		statBettype = emptifyIfNotStatUnit("bettype", statBettype);
		prediction = emptifyIfNotStatUnit("prediction", prediction);
		
		return String.join("", statBettype, prediction);
	}

	/** bettype, prediction, patternのキーを取得する */
	public String createKey(String statBettype, String prediction, String patternid, String pattern) throws Exception {
		ensureInitialized();
		
		statBettype = emptifyIfNotStatUnit("bettype", statBettype);
		prediction = emptifyIfNotStatUnit("prediction", prediction);
		patternid = emptifyIfNotStatUnit("patternid", patternid);
		pattern = emptifyIfNotStatUnit("pattern", pattern);
		
		String key = String.join("", statBettype, prediction, patternid, pattern);
		
		return key;
		
	}

	private final String emptifyIfNotStatUnit(String statUnit, String value) {
		if (statUnits.indexOf(statUnit) == -1) {
			return "";
		}
		
		return value;
	}
}

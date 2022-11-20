package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.util.HashMap;

@SuppressWarnings("serial")
/**
 * evaluationのtsvファイルの1行を保持するクラス。
 * @author ttolt
 *
 */
public class Evaluation extends HashMap<String, String>{
	public String getString(String id) {
		return super.get(id);
	}
	
	public Double getDouble(String id) {
		return Double.valueOf(super.get(id));
	}
	
	/**
	 * bettype_kumibanを返却する。
	 * @return bettype_kumiban_modelno 例）1T_1_99080
	 */
	public String getBettypeKumibanModelNo() {
		return String.join("_", super.get("bettype"), super.get("kumiban"), super.get("modelno"));  
	}

	public String toString() {
		return "Evaluation:[" + String.join("\t", get("bettype"), get("kumiban"), get("resultno"), get("modelno"), get("patternid"), get("pattern"), get("factor"),  
				"incamt="+get("incamt"), "betcnt="+get("betcnt"), "incrate="+get("incrate"), "hitrate="+get("hitrate")) + "]"; 
	}
	
	/** evaluationファイルから１行を特定できるキーを取得する */
	public String getUniqueKey() {
		return String.join("_", get("bettype"), get("kumiban"), get("modelno"), get("patternid"), get("pattern"));
//		return String.join("_", get("bettype"), get("kumiban"), get("modelno"), get("patternid"), get("pattern"), get("bonus_bork"));
	}
	
	/** evlauation固有のモデルとパタン */
	public String getModelPattern() {
		return String.join("_", get("modelno"), get("patternid"), get("pattern"));
	}
}

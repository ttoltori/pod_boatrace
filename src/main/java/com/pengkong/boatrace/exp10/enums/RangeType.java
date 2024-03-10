package com.pengkong.boatrace.exp10.enums;

public enum RangeType {
	RESULT_ODDS("resultOdds"), // 確定オッズ 
	RESULT_ODDS_RANK("resultOddsrank"), // 確定オッズランク 
	RODDS_PROB("rodds_prob"), // 確定オッズ / 予想確率 
	RODDSRANK_PROB("rodds_prob"), // 確定オッズランク / 予想確率 
	BEFORE_ODDS("beforeOdds"), // 直前オッズ
	BEFORE_ODDS_RANK("beforeOddsrank"), // 直前オッズランク
	BODDS_PROB("bodds_prob"), // 直前オッズ/予想確率
	BODDSRANK_PROB("boddsrank_prob"), // 直前オッズランク/予想確率
	PROBABILITY("probability"); // 予想確率
	
	private final String rangeType;
	
	private RangeType(String rangeType) {
		this.rangeType = rangeType;
	}
	
	public String getValue() {
		return this.rangeType;
	}
}

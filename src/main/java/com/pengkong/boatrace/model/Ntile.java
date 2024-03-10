package com.pengkong.boatrace.model;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.util.BoatUtil;

public class Ntile {
	public static final String ATTRIBUTE_NAME_BETCNT = "betCnt";
	public static final String ATTRIBUTE_NAME_HITCNT = "hitCnt";
	public static final String ATTRIBUTE_NAME_HITRATE = "hitRate";
	public static final String ATTRIBUTE_NAME_INCOMERATE = "incomeRate";
	public static final String ATTRIBUTE_NAME_BALANCE = "balance";

	/**
	 * key ntile_deviationの場合　枠ランク４桁_ntile番号(例：1212_1)
	 */
	public String id;
	public String kumiban;
	public int betCnt = 0;
	public int hitCnt = 0;
	public int income = 0;
	public float hitRate = 0;
	public float incomeRate = 0;
	public int balance = 0;
	public float dailyBet = 0;
	
	public static Ntile fromCsv(String csvLine) {
		Ntile result = new Ntile();
		
		String[] token = csvLine.split(BoatTemplate.CSV_DELIMITER);
		result.id = token[0];
		result.kumiban = token[1];
		result.betCnt = BoatUtil.toInt(token[2]);
		result.hitCnt = BoatUtil.toInt(token[3]);
		result.income = BoatUtil.toInt(token[4]);
		result.hitRate = BoatUtil.toFloat(token[5]);
		result.incomeRate = BoatUtil.toFloat(token[6]);
		result.balance = BoatUtil.toInt(token[7]);
		// result.dailybet = BoatUtil.toFloat(token[8]);
		
		return result;
	}
}

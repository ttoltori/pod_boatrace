package com.pengkong.boatrace.model;

import com.pengkong.boatrace.util.BoatUtil;

public class StatResult {
	public String patternType;
	public String betType;
	public String pattern;
	public int patternCnt;
	public int betAmount;
	public String kumiban;
	public int hitCount;
	public int prize;
	public float hitRate;
	public float incomeRate;
	public float avgincomeRate;
	
	public String createKey() {
		return betType + "_" + pattern;
	}
	
	public static StatResult fromCsv(String[] token) {
		StatResult rec = new StatResult();
		//rec.patternType = token[0];
		rec.betType = token[0];
		rec.pattern = token[1];
		rec.kumiban = token[2];
		rec.patternCnt = BoatUtil.toInt(token[3]);
		rec.betAmount = BoatUtil.toInt(token[4]);
		rec.hitCount = BoatUtil.toInt(token[5]);
		rec.prize = BoatUtil.toInt(token[6]);
		rec.hitRate = BoatUtil.toFloat(token[7]);
		rec.incomeRate =  BoatUtil.toFloat(token[8]);
		rec.avgincomeRate = BoatUtil.toFloat(token[9]);
		
		return rec;
	}
	
	public float getAverageIncomeRate() {
		return hitRate * incomeRate;
	}
}

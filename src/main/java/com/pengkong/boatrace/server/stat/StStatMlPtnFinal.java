package com.pengkong.boatrace.server.stat;

import com.pengkong.boatrace.mybatis.entity.StPtnFinal;
import com.pengkong.boatrace.server.util.BoatMath;

public class StStatMlPtnFinal {
	public BoatStatObject stPatternValue;
	
	public String betType;
	public String kumiban;
	public String patternname;
	public String patternvalue;
	
	public int betcnt; 
	public int hitcnt; 
	public int betamt; 
	public int hitamt; 
	public int incomeamt; 
	public double betrate;
	public double hitrate;
	public double incomerate;
	// public double hitamt_bias_rate numeric(7,2),
	public int minusBalanceCount; //적자 투표수
	public int plusBalanceCount; //흑자 투표수
	public int minusBalanceChangedCount; //적자전환 횟수
	public int plusBalanceChangedCount; // 흑자전환 횟수
	public double minusBalanceCntRate; // 전체투표수에 대한 적자투표수 비율
	
	public double slope_incomeamt;
	public double intercept_incomeamt;
	// 2019/9/8 분석통계는 필요시 추가해가자.
	//public double monthlyMinusSlopeIncomeAmtCount;
	//public double monthlyMinusSlopeIncomeAmtRate;
	//public double linear_incomeamt_slope = 0; 
	//public double recover_success_rate = 0; // 적중투표수에 대한 적자=>흑자전환투표수 비율
	//public double recover_fail_rate = 0; // 적중투표수에 대한 적자=>흑자전환실패투표수 비율
	//public double recover_plus_rate = 0; // 적중투표수에 대한 흑자증가투표수 비율
	//public double recover_minus_rate = 0; // 적중투표수에 대한 흑자감소투표수 비율
	
	private static final String DELIM = ",";
	
	public static StStatMlPtnFinal create(BoatStatObject bso) throws Exception {
		StStatMlPtnFinal stFinal = new StStatMlPtnFinal();
		stFinal.stPatternValue = bso;
		stFinal.betType = bso.parent.parent.parent.key;
		stFinal.kumiban = bso.parent.parent.key;
		stFinal.patternname = bso.parent.key;
		stFinal.patternvalue = bso.key;
		stFinal.betcnt = bso.betcnt;
		stFinal.hitcnt = bso.hitcnt;
		stFinal.betamt = bso.betamt;
		stFinal.hitamt = bso.hitamt;
		stFinal.incomeamt = bso.incomeamt;
		stFinal.betrate = BoatMath.getBetCntRatePerParent(bso);
		stFinal.hitrate = BoatMath.getHitRate(bso);
		stFinal.incomerate = BoatMath.getIncomeRate(bso);
		stFinal.minusBalanceCount = bso.minusBalanceCount;
		stFinal.plusBalanceCount = bso.plusBalanceCount;
		stFinal.minusBalanceChangedCount = bso.minusBalanceChangedCount;
		stFinal.plusBalanceChangedCount = bso.plusBalanceChangedCount;
		stFinal.minusBalanceCntRate = BoatMath.getMinusBalanceCntRate(bso);
		
		BoatRegressionResult regrResult = BoatMath.getRegression(bso);
		stFinal.slope_incomeamt = regrResult.slope;
		stFinal.intercept_incomeamt = regrResult.intercept;
		
		return stFinal;
	}
	
	public static StPtnFinal createDbRecord(String description, BoatStatObject bso) throws Exception {
		StPtnFinal rec = new StPtnFinal();
		rec.setDescription(description);
		rec.setBettype(bso.parent.parent.parent.key);
		rec.setKumiban(bso.parent.parent.key);
		rec.setPatternName(bso.parent.key);
		rec.setPatternValue(bso.key);
		rec.setBetcnt(bso.betcnt);
		rec.setHitcnt(bso.hitcnt);
		rec.setBetamt(bso.betamt);
		rec.setHitamt(bso.hitamt);
		rec.setIncomeamt(bso.incomeamt);
		rec.setBetrate(BoatMath.getBetCntRatePerParent(bso));
		rec.setHitrate(BoatMath.getHitRate(bso));
		rec.setIncomerate(BoatMath.getIncomeRate(bso));
		rec.setMinusBalanceCount(bso.minusBalanceCount);
		rec.setPlusBalanceCount(bso.plusBalanceCount);
		rec.setMinusBalanceChangedCount(bso.minusBalanceChangedCount);
		rec.setPlusBalanceChangedCount(bso.plusBalanceChangedCount);
		rec.setMinusBalanceCntRate(BoatMath.getMinusBalanceCntRate(bso));

		// regression
		BoatRegressionResult regrResult = BoatMath.getRegression(bso);
		rec.setSlopeIncomeamt(regrResult.slope);
		rec.setInterceptIncomeamt(regrResult.intercept);
		
		return rec;
	}
	
	public static String createCsv(StPtnFinal rec) {
		StringBuilder sb = new StringBuilder();
		sb.append(rec.getDescription()); sb.append(DELIM);
		sb.append(rec.getBettype()); sb.append(DELIM);
		sb.append(rec.getKumiban()); sb.append(DELIM);
		sb.append(rec.getPatternName()); sb.append(DELIM);
		sb.append(rec.getPatternValue()); sb.append(DELIM);
		sb.append(rec.getBetcnt()); sb.append(DELIM);
		sb.append(rec.getHitcnt()); sb.append(DELIM);
		sb.append(rec.getBetamt()); sb.append(DELIM);
		sb.append(rec.getHitamt()); sb.append(DELIM);
		sb.append(rec.getIncomeamt()); sb.append(DELIM);
		sb.append(rec.getBetrate()); sb.append(DELIM);
		sb.append(rec.getHitrate()); sb.append(DELIM);
		sb.append(rec.getIncomerate()); sb.append(DELIM);
		sb.append(rec.getMinusBalanceCount()); sb.append(DELIM);
		sb.append(rec.getPlusBalanceCount()); sb.append(DELIM);
		sb.append(rec.getMinusBalanceChangedCount()); sb.append(DELIM);
		sb.append(rec.getPlusBalanceChangedCount()); sb.append(DELIM);
		sb.append(rec.getMinusBalanceCntRate()); sb.append(DELIM);
		sb.append(rec.getSlopeIncomeamt()); sb.append(DELIM);
		sb.append(rec.getInterceptIncomeamt());
		
		return sb.toString();
	}
}

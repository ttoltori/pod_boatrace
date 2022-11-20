package com.pengkong.boatrace.server.stat;

import com.pengkong.boatrace.server.util.BoatMath;

public class StStatMlPtnFinalMonthly extends StStatMlPtnFinal {
	public String yyyyMm;
	
	public static StStatMlPtnFinalMonthly createFromBoatStatObject(BoatStatObject bso) {
		StStatMlPtnFinalMonthly stFinalMonthly = new StStatMlPtnFinalMonthly();
		stFinalMonthly.stPatternValue = (BoatStatObject)bso.parent;
		stFinalMonthly.yyyyMm = bso.key;
		stFinalMonthly.betType = bso.parent.parent.parent.key;
		stFinalMonthly.kumiban = bso.parent.parent.key;
		stFinalMonthly.patternname = bso.parent.key;
		stFinalMonthly.patternvalue = bso.key;
		stFinalMonthly.betcnt = bso.betcnt;
		stFinalMonthly.hitcnt = bso.hitcnt;
		stFinalMonthly.betamt = bso.betamt;
		stFinalMonthly.hitamt = bso.hitamt;
		stFinalMonthly.incomeamt = bso.incomeamt;
		stFinalMonthly.betrate = BoatMath.getBetCntRatePerParent(bso);
		stFinalMonthly.hitrate = BoatMath.getHitRate(bso);
		stFinalMonthly.incomerate = BoatMath.getIncomeRate(bso);
		stFinalMonthly.minusBalanceCount = bso.minusBalanceCount;
		stFinalMonthly.plusBalanceCount = bso.plusBalanceCount;
		stFinalMonthly.minusBalanceChangedCount = bso.minusBalanceChangedCount;
		stFinalMonthly.plusBalanceChangedCount = bso.plusBalanceChangedCount;
		stFinalMonthly.minusBalanceCntRate = BoatMath.getMinusBalanceCntRate(bso);
		
		return stFinalMonthly;
	}
}

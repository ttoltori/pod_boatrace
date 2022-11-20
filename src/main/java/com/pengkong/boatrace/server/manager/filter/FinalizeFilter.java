package com.pengkong.boatrace.server.manager.filter;

import com.pengkong.boatrace.server.stat.BoatStatObject;
import com.pengkong.boatrace.server.util.BoatMath;

public class FinalizeFilter {

	public int minBetCount = 100;
	public double minBetRate = 10;
	public double minIncomeRate = 100;
	
	public static FinalizeFilter create(String[] valueToken) {
		FinalizeFilter filter = new FinalizeFilter();
		filter.minBetCount = Integer.parseInt(valueToken[0]);
		filter.minBetRate = Double.parseDouble(valueToken[1]);
		filter.minIncomeRate = Double.parseDouble(valueToken[2]);
		
		return filter;
	}
	
	/**
	 * 대상 통계객체가 조건을 만족하는지 체크한다.
	 * @param obj
	 * @return
	 */
	public boolean isValid(BoatStatObject obj) {
		if (obj.betcnt < minBetCount) {
			return false;
		}
		
		double calcIncomeRate = BoatMath.getIncomeRate(obj);
		if (calcIncomeRate < minIncomeRate) {
			return false;
		}
		
		double calcBetRate = BoatMath.getBetCntRatePerParent(obj);
		if (calcBetRate < minBetRate) {
			return false;
		}
		
		return true; 
	}
	
	public String toString() {
		return "{" + minBetCount + ", " + minBetRate + ", " + minIncomeRate + "}"; 
	}
}

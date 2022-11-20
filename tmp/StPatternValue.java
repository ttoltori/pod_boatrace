package com.pengkong.boatrace.server.stat;

import com.pengkong.boatrace.server.manager.filter.FinalizeFilter;
import com.pengkong.boatrace.server.util.BoatMath;

public class StPatternValue extends BoatStatObject {

	public StPatternValue(StatObject parent, String key) {
		super(parent, key);
	}

	public StPatternValue(String key) {
		super(key);
	}
	
	public boolean isFinalizedOk(FinalizeFilter filter) {
		if (filter == null) {
			return true;
		}
		
		if (this.betcnt < filter.minBetCount) {
			return false;
		}
		
		if (BoatMath.getIncomeRate(this) < filter.minIncomeRate) {
			return false;
		}
		
		if (BoatMath.getBetCntRatePerParent(this) < filter.minBetRate) {
			return false;
		}
		
		return true; 
	}
}

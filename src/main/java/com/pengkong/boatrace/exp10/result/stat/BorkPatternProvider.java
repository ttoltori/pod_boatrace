package com.pengkong.boatrace.exp10.result.stat;

import com.pengkong.boatrace.exp10.odds.Odds;

public class BorkPatternProvider {

	public String getPattern(String patternId, Odds beforeOdds) throws IllegalStateException{
		//if (patternId.equals("bork1")) {
			return getBork1(beforeOdds);
		//}
		
		//throw new IllegalStateException("bork pattern id is invalid.");
	}
	
	String getBork1(Odds beforeOdds) {
		return String.valueOf(beforeOdds.rank);
	}
}

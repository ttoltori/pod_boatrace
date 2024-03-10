package com.pengkong.boatrace.service.stat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AverageTime {
	/** 出走回数 */
	public Integer runCount = 0;
	/** 平均ST */
	public BigDecimal start = new BigDecimal(0);
	/** タイム */
	public BigDecimal time = new BigDecimal(0);
	
	public static AverageTime createInstance(float valStart, float valTime, float count) {
		AverageTime averageTime = new AverageTime();
		
		averageTime.runCount = (int)count;
		if (valStart != 0) {
			averageTime.start = new BigDecimal((valStart / count)).setScale(2, RoundingMode.FLOOR);
		}
		
		if (valTime != 0) {
			averageTime.time = new BigDecimal((valTime / count)).setScale(2, RoundingMode.FLOOR);
		}
		
		return averageTime;
	}
}

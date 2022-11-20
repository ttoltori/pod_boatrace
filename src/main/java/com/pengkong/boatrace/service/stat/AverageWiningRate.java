package com.pengkong.boatrace.service.stat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AverageWiningRate {
	/** 出走回数 */
	public Integer runCount = 0;
	/** 勝率 */
	public BigDecimal rate1 = new BigDecimal(0);
	/** 2連帯率 */
	public BigDecimal rate2 = new BigDecimal(0);
	/** 3連帯率 */
	public BigDecimal rate3 = new BigDecimal(0);
	
	public static AverageWiningRate createInstance(float val1, float val2, float val3, float count) {
		AverageWiningRate winingRate = new AverageWiningRate();
		
		winingRate.runCount = (int)count;
		if (val1 != 0) {
			winingRate.rate1 = new BigDecimal((val1 / count)).setScale(2, RoundingMode.FLOOR);
		}
		
		if (val2 != 0) {
			winingRate.rate2 = new BigDecimal((val2 / count)).setScale(2, RoundingMode.FLOOR);
		}
		
		if (val3 != 0) {
			winingRate.rate3 = new BigDecimal((val3 / count)).setScale(2, RoundingMode.FLOOR);
		}
		
		return winingRate;
	}
}

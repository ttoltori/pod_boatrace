package com.pengkong.boatrace.exp10.racer.trend.calculator;

import com.pengkong.boatrace.mybatis.entity.RecRacer;

public class WakuAvgStartPointCalculator extends AbstractWakuPointCalculator {

	@Override
	protected void calculate(RecRacer rec) throws IllegalStateException {
		int waku = rec.getWaku();
		total[waku-1] += rec.getAveragestart().doubleValue(); 
	}
}

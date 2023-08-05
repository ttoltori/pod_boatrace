package com.pengkong.boatrace.exp10.racer.trend.calculator;

import com.pengkong.boatrace.mybatis.entity.RecRacer;

public class WakuRank123Calculator extends AbstractWakuPointCalculator {

	@Override
	protected void calculate(RecRacer rec) throws IllegalStateException {
		int waku = rec.getWaku();
		if (rec.getRank() < 4) {
			total[waku-1]++;
		}
	}
}

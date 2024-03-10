package com.pengkong.boatrace.exp10.racer.trend.calculator;

import com.pengkong.boatrace.mybatis.entity.RecRacer;

public class WakuRank12Calculator extends AbstractWakuPointCalculator {

	@Override
	protected void calculate(RecRacer rec) throws IllegalStateException {
		int waku = rec.getWaku();
		if (rec.getRank() < 3) {
			total[waku-1]++;
		}
	}
}

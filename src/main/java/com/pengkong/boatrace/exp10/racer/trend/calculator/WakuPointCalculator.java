package com.pengkong.boatrace.exp10.racer.trend.calculator;

import com.pengkong.boatrace.mybatis.entity.RecRacer;

public class WakuPointCalculator extends AbstractWakuPointCalculator {

	@Override
	protected void calculate(RecRacer rec) throws IllegalStateException {
		int waku = rec.getWaku();
		total[waku-1] += PointHelper.getPoint(rec.getGrade(), rec.getRacetype(), String.valueOf(rec.getRank()));
	}
}

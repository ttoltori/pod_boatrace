package com.pengkong.boatrace.exp10.racer.trend.calculator;

import com.pengkong.boatrace.mybatis.entity.RecRacer;

public class PointCalculator extends AbstractPointCalculator {

	@Override
	protected void calculate(RecRacer rec) {
		total += PointHelper.getPoint(rec.getGrade(), rec.getRacetype(), String.valueOf(rec.getRank()));
	}
}

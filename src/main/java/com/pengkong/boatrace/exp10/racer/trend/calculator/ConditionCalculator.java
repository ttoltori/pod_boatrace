package com.pengkong.boatrace.exp10.racer.trend.calculator;

import com.pengkong.boatrace.mybatis.entity.RecRacer;

public class ConditionCalculator extends AbstractPointCalculator{
	@Override
	protected void calculate(RecRacer rec) {
		total += rec.getWaku() - rec.getRank();
	}
}

package com.pengkong.boatrace.exp10.racer.trend.calculator;

import com.pengkong.boatrace.mybatis.entity.RecRacer;

/** ２連率計算 */
public class Rank12Calculator extends AbstractPointCalculator {

	@Override
	protected void calculate(RecRacer rec) throws IllegalStateException {
		if (rec.getRank() < 3) {
			total++;
		}
	}
}

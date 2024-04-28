package com.pengkong.boatrace.exp10.simulation.calculator.expectation;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class ProbabilityExpCalculatorFactory {

	public static AbstractProbabilityExpCalculator create() {
		String id = MLPropertyUtil.getInstance().getString("expect_type");
		if (id.equals("d123")) {
			return new ProbabilityExpCaculator123();
		} else if (id.equals("d1")) {
			return new ProbabilityExpCaculator1();
		}
		
		return null;
	}
}

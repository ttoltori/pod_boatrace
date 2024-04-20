package com.pengkong.boatrace.exp10.simulation.calculator.probability;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class ProbabilityCalculatorFactory {

	public static AbstractProbabilityCalculator create() {
		String id = MLPropertyUtil.getInstance().getString("probability_type");
		if (id.equals("default")) {
			return new ProbabilityCaculator1234();
		} else if (id.equals("digit1")) {
			return new ProbabilityCalculator1();
		}
		return null;
	}
}

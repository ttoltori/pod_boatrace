package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class ProbabilityCalculatorFactory {

	public static AbstractProbabilityCalculator create() {
		String id = MLPropertyUtil.getInstance().getString("probability_type");
		if (id.equals("default")) {
			return new ProbabilityCaculatorDefault();
		} else if (id.equals("default2")) {
			return new ProbabilityCaculatorDefault2();
		} else if (id.equals("digit1")) {
			return new ProbabilityCalculatorDigit1();
		} else if (id.equals("dist12")) {
			return new ProbabilityCaculatorDistance12();
		} else if (id.equals("popu_variance")) {
			return new ProbabilityCalculatorVariance();
		} else if (id.equals("releavance123")) {
			return new ProbabilityCalculatorReleavance123();
		}
		
		return null;
	}
}

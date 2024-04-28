package com.pengkong.boatrace.exp10.simulation.calculator.probability;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class ProbabilityCalculatorFactory {

	public static AbstractProbabilityCalculator create() {
		String id = MLPropertyUtil.getInstance().getString("probability_type");
		if (id.equals("d1234")) {
			return new ProbabilityCaculator1234();
		} else if (id.equals("d123")) {
			return new ProbabilityCaculator123();
		} else if (id.equals("d1")) {
			return new ProbabilityCalculator1();
		}
		
		return null;
	}
}

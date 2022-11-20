package com.pengkong.boatrace.exp10.simulation.range.validation;

import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;

public class BeforeOddsRangeValidationProvider extends AbstractRangeValidationProvider {

	@Override
	boolean isBestRange(Double value, Evaluation eval) {
		Double min = Double.valueOf(eval.get("bor_bestmin"));
		Double max = Double.valueOf(eval.get("bor_bestmax"));
		
		return ( value >= min && value <= max );
	}
}

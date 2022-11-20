package com.pengkong.boatrace.exp10.simulation.range.validation;

import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;

public class BeforeOddsRankRangeValidationProvider extends AbstractRangeValidationProvider {

	@Override
	boolean isBestRange(Double value, Evaluation eval) {
		Double min = Double.valueOf(eval.get("bork_bestmin"));
		Double max = Double.valueOf(eval.get("bork_bestmax"));
		
		return ( value >= min && value <= max );
	}
}

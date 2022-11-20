package com.pengkong.boatrace.exp10.simulation.range.validation;

import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;

@Deprecated
public class BeforeOddsProbabilityValidationProvider extends AbstractDoubleRangeValidationProvider {

	@Override
	boolean isBestRange(Double beforeOdds, Double probability, Evaluation eval) {
		Double minX = Double.valueOf(eval.get("xy_bestminx"));
		Double maxX = Double.valueOf(eval.get("xy_bestmaxx"));
		Double minY = Double.valueOf(eval.get("xy_bestminy"));
		Double maxY = Double.valueOf(eval.get("xy_bestmaxy"));

		return ((beforeOdds >= minX && beforeOdds <= maxX) && (probability >= minY && probability <= maxY));
	}

}

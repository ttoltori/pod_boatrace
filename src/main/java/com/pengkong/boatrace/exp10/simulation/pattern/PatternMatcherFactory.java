package com.pengkong.boatrace.exp10.simulation.pattern;

import com.pengkong.boatrace.exp10.enums.RangeValidationType;
import com.pengkong.boatrace.exp10.simulation.evaluation.AbstractEvaluationLoader;

public class PatternMatcherFactory {

	public static IEvaluationPatternMatcher create(String matcherType, AbstractEvaluationLoader loader) {
		if (matcherType.equals(RangeValidationType.NONE.getValue())) {
			return new EvaluationPatternMatcher(loader);
		} else {
			return new EvaluationPatternMatcher2(loader);
		}
	}
}

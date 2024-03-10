package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.MathUtil;

public class ProbabilityCalculatorVariance extends AbstractProbabilityCalculator {
	protected double min = 0.0;
	protected double max = 2.5;
	@Override
	Double getProbability1T(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("popu_variance"), min, max, 20);
	}

	@Override
	Double getProbability2T(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("popu_variance"), min, max, 20);
	}

	@Override
	Double getProbability3T(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("popu_variance"), min, max, 20);
	}

	@Override
	Double getProbability2F(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("popu_variance"), min, max, 20);
	}

	@Override
	Double getProbability3F(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("popu_variance"), min, max, 20);
	}
}

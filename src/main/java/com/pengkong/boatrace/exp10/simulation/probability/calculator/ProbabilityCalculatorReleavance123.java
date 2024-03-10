package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.MathUtil;

public class ProbabilityCalculatorReleavance123 extends AbstractProbabilityCalculator {
	protected double min1 = -2.3;
	protected double max1 = 1;
	protected double min12 = -3.6;
	protected double max12 = 2.2;
	protected double min123 = -4.6;
	protected double max123 = 3.4;
						
	@Override
	Double getProbability1T(DBRecord rec) {
		return MathUtil.convertToPercentile(rec.getDouble("probability1"), min1, max1, 20);
	}

	@Override
	Double getProbability2T(DBRecord rec) {
		return MathUtil.convertToPercentile(
				rec.getDouble("probability1") +rec.getDouble("probability2")
				, min12, max12, 20
				);
	}

	@Override
	Double getProbability3T(DBRecord rec) {
		return MathUtil.convertToPercentile(
				rec.getDouble("probability1") + rec.getDouble("probability2") + rec.getDouble("probability3")
				, min123, max123, 20
				);
	}

	@Override
	Double getProbability2F(DBRecord rec) {
		return MathUtil.convertToPercentile(
				rec.getDouble("probability1") +rec.getDouble("probability2")
				, min12, max12, 20
				);
	}

	@Override
	Double getProbability3F(DBRecord rec) {
		return MathUtil.convertToPercentile(
				rec.getDouble("probability1") + rec.getDouble("probability2") + rec.getDouble("probability3")
				, min123, max123, 20
				);
	}
}

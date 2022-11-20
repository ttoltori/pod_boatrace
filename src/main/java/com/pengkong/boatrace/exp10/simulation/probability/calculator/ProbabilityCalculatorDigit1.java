package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import com.pengkong.boatrace.server.db.dto.DBRecord;

public class ProbabilityCalculatorDigit1 extends AbstractProbabilityCalculator {
	@Override
	Double getProbability1T(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability2T(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3T(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability2F(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3F(DBRecord rec) {
		return rec.getDouble("probability1");
	}
}

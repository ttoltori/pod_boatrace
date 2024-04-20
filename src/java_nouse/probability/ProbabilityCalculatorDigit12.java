package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import com.pengkong.boatrace.server.db.dto.DBRecord;

public class ProbabilityCalculatorDigit12 extends AbstractProbabilityCalculator {
	@Override
	Double getProbability1T(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability2T(DBRecord rec) {
		return rec.getDouble("probability1") * rec.getDouble("probability2");
	}

	@Override
	Double getProbability3T(DBRecord rec) {
		return rec.getDouble("probability1") * rec.getDouble("probability2");
	}

	@Override
	Double getProbability2F(DBRecord rec) {
		return rec.getDouble("probability1") + rec.getDouble("probability2");
	}

	@Override
	Double getProbability3F(DBRecord rec) {
		return rec.getDouble("probability1") + rec.getDouble("probability2");
	}
	@Override
	Double getProbability4T(DBRecord rec) {
		// ranker이외의 경우는 사용하지 말 것.
		return 0.0;
	}
}

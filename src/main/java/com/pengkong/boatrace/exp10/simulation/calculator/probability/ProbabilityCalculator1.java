package com.pengkong.boatrace.exp10.simulation.calculator.probability;

import com.pengkong.boatrace.server.db.dto.DBRecord;

public class ProbabilityCalculator1 extends AbstractProbabilityCalculator {
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

	@Override
	Double getProbability2M(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3N(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability2N(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3P(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3R(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3X(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability2G(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3G(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3B(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3C(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3D(DBRecord rec) {
		return rec.getDouble("probability1");
	}

	@Override
	Double getProbability3E(DBRecord rec) {
		return rec.getDouble("probability1");
	}
}

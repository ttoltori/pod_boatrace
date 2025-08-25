package com.pengkong.boatrace.exp10.simulation.calculator.probability;

import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * DB取得値から予想的中確率をbettype別に組み合わせるクラス 
 * @author ttolt
 *
 */
public class ProbabilityCaculatorR123 extends AbstractProbabilityCalculator {
	@Override
	Double getProbability1T(DBRecord rec) {
		return rp1;
	}

	@Override
	Double getProbability2T(DBRecord rec) {
		return rp1 * rp2;
	}

	@Override
	Double getProbability3T(DBRecord rec) {
		return rp1 * rp2 * rp3;
	}

	@Override
	Double getProbability2F(DBRecord rec) {
		return rp1 + rp2;
	}

	@Override
	Double getProbability3F(DBRecord rec) {
		return rp1 + rp2 + rp3;
	}

	@Override
	Double getProbability2M(DBRecord rec) {
		return rp1 * rp2;
	}

	@Override
	Double getProbability3N(DBRecord rec) {
		return rp1 * rp2;
	}
	
	@Override
	Double getProbability2N(DBRecord rec) {
		return rp1 * rp2 * rp3;
	}

	@Override
	Double getProbability3P(DBRecord rec) {
		return rp1 * rp2 * rp3;
	}
	
	@Override
	Double getProbability3R(DBRecord rec) {
		return rp1 * rp2 * rp3;
	}

	@Override
	Double getProbability3X(DBRecord rec) {
		return rp1 * rp2 * rp3;
	}

	@Override
	Double getProbability2G(DBRecord rec) {
		return rp1 + rp2;
	}

	@Override
	Double getProbability3G(DBRecord rec) {
		return rp1 + rp2 + rp3;
	}

	@Override
	Double getProbability3B(DBRecord rec) {
		return rp1 * rp2 * rp3;
	}

	@Override
	Double getProbability3C(DBRecord rec) {
		return rp1 * rp2 * rp3;
	}
	
	@Override
	Double getProbability3D(DBRecord rec) {
		return rp1 * rp2 * rp3;
	}
	
	@Override
	Double getProbability3E(DBRecord rec) {
		return rp1 * rp2 * rp3;
	}
}

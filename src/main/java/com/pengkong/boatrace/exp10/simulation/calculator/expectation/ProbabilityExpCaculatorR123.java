package com.pengkong.boatrace.exp10.simulation.calculator.expectation;

import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * DB取得値から予想的中確率をbettype別に組み合わせるクラス 
 * @author ttolt
 *
 */
public class ProbabilityExpCaculatorR123 extends AbstractProbabilityExpCalculator {
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
}

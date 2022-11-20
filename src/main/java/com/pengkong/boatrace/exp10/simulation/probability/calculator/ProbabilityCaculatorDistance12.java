package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * DB取得値から予想的中確率をbettype別に組み合わせるクラス 
 * @author ttolt
 *
 */
public class ProbabilityCaculatorDistance12 extends AbstractProbabilityCalculator {
	@Override
	Double getProbability1T(DBRecord rec) {
		return (rec.getDouble("probability1") - rec.getDouble("probability2")) + 1;
	}

	@Override
	Double getProbability2T(DBRecord rec) {
		return (rec.getDouble("probability1") - rec.getDouble("probability2")) + 1;
	}

	@Override
	Double getProbability3T(DBRecord rec) {
		return (rec.getDouble("probability1") - rec.getDouble("probability2")) + 1;
	}

	@Override
	Double getProbability2F(DBRecord rec) {
		return (rec.getDouble("probability1") - rec.getDouble("probability2")) + 1;
	}

	@Override
	Double getProbability3F(DBRecord rec) {
		return (rec.getDouble("probability1") - rec.getDouble("probability2")) + 1;
	}
}

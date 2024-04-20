package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import org.apache.commons.math3.util.MathUtils;

import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.MathUtil;

@Deprecated
public class ProbabilityCalculatorRaceReleavance123 extends AbstractProbabilityCalculator {
	protected Double min1;
	protected Double max1;
	protected Double min12;
	protected Double max12;
	protected Double min123;
	protected Double max123;
	
	
	public ProbabilityCalculatorRaceReleavance123() {
		super();
	}

	private Double[] getMinMax(DBRecord rec) {
		Double results[] = new Double[2];
		Double min = MathUtil.min(rec.getDouble("probability1"), rec.getDouble("probability2"), rec.getDouble("probability3"),
				rec.getDouble("probability4"), rec.getDouble("probability5"), rec.getDouble("probability6"));
		Double max = MathUtil.max(rec.getDouble("probability1"), rec.getDouble("probability2"), rec.getDouble("probability3"),
				rec.getDouble("probability4"), rec.getDouble("probability5"), rec.getDouble("probability6"));
		results[0] = min;
		results[1] = max;
		
		return results;
	}
	
	@Override
	Double getProbability1T(DBRecord rec) {
		Double[] minMax = getMinMax(rec);
		return MathUtil.convertToPercentile(rec.getDouble("probability1"), minMax[0], minMax[1], 20);
	}

	@Override
	Double getProbability2T(DBRecord rec) {
		Double[] minMax = getMinMax(rec);
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
	@Override
	Double getProbability4T(DBRecord rec) {
		// ranker이외의 경우는 사용하지 말 것.
		return 0.0;
	}
}

package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.MathUtil;

public class ProbabilityCalculatorReleavance123 extends AbstractProbabilityCalculator {
	protected Double min1;
	protected Double max1;
	protected Double min12;
	protected Double max12;
	protected Double min123;
	protected Double max123;
	
	
	public ProbabilityCalculatorReleavance123() {
		super();
		String sql = "select min(probability1) min1, min(probability2) min2, min(probability3) min3, min(probability4) min4, " +
				" max(probability1) max1, max(probability2) max2, max(probability3) max3, max(probability4) max4 " + 
				"from ml_classification where modelno='" + MLPropertyUtil.getInstance().getString("used_model_no") + "'";
		try {
			DBRecord rec = DatabaseUtil.select(sql).get(0);
			min1 = rec.getDouble("min1");
			max1 = rec.getDouble("max1");
			min12 = rec.getDouble("min1") + rec.getDouble("min2");
			max12 = rec.getDouble("max1") + rec.getDouble("max2");
			min123 = rec.getDouble("min1") + rec.getDouble("min2") + rec.getDouble("min3");
			max123 = rec.getDouble("max1") + rec.getDouble("max2") + rec.getDouble("max3");
		} catch (Exception e) {
			logger.error("ProbabilityCalculatorReleavance123 initialize failed.", e);
		} 
	}

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
	@Override
	Double getProbability4T(DBRecord rec) {
		// ranker이외의 경우는 사용하지 말 것.
		return 0.0;
	}
}

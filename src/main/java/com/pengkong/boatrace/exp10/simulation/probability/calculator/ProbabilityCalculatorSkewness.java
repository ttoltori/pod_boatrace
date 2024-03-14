package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.MathUtil;

public class ProbabilityCalculatorSkewness extends AbstractProbabilityCalculator {
	protected Double min;
	protected Double max;
	
	public ProbabilityCalculatorSkewness() {
		super();
		String sql = "select min(skewness) min, max(skewness) max from ml_classification where modelno='" + MLPropertyUtil.getInstance().getString("used_model_no") + "'";
		try {
			DBRecord rec = DatabaseUtil.select(sql).get(0);
			min = rec.getDouble("min");
			max = rec.getDouble("max");
		} catch (Exception e) {
			logger.error("ProbabilityCalculatorSkewness initialize failed.", e);
		} 
	}
	
	@Override
	Double getProbability1T(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("skewness"), min, max, 20);
	}

	@Override
	Double getProbability2T(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("skewness"), min, max, 20);
	}

	@Override
	Double getProbability3T(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("skewness"), min, max, 20);
	}

	@Override
	Double getProbability2F(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("skewness"), min, max, 20);
	}

	@Override
	Double getProbability3F(DBRecord rec) {
		return MathUtil.convertToPercentile((Double) rec.getDouble("skewness"), min, max, 20);
	}
}

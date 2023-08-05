package com.pengkong.boatrace.exp10.racer.trend.calculator;

import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.common.MathUtil;

public abstract class AbstractPointCalculator {

	protected double total = 0.0;
	
	protected double count = 0.0;

	public Double get2ScaledResult() {
		return MathUtil.scale2(getResult());
	}
	
	public Double getResult() {
		if ( count <= 0 ) {
			return 0.0;
		}

		return  total / count;
	}
	
	public void add(RecRacer rec) {
		count++;
		calculate(rec);
	}
	
	protected abstract void calculate(RecRacer rec) throws IllegalStateException;
}

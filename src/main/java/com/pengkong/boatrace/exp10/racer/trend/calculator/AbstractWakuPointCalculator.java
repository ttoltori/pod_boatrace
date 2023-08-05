package com.pengkong.boatrace.exp10.racer.trend.calculator;

import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.common.MathUtil;

public abstract class AbstractWakuPointCalculator {

	protected double total[];
	
	protected double count[];
	
	public AbstractWakuPointCalculator() {
		total = new double[6];
		count = new double[6];
		for (int i = 0; i < 6; i++) {
			total[i] = 0.0;
			count[i] = 0.0;
		}
	}
	
	public double[] get2ScaledResult() {
		double[] results = new double[6];
		for (int i = 0; i < 6; i++) {
			if (count[i] <= 0) {
				results[i] = 0;
			}
			
			results[i] = MathUtil.scale2(total[i] / count[i]);
		}
		
		return results;
	}
	
	public double[] getResult() {
		double[] results = new double[6];
		for (int i = 0; i < 6; i++) {
			if (count[i] <= 0) {
				results[i] = 0;
			}
			
			results[i] = total[i] / count[i] ;
		}
		
		return results;
	}

	
	
	public void add(RecRacer rec) {
		int waku = rec.getWaku();
		count[waku-1]++;
		
		calculate(rec);
	}
	
	protected abstract void calculate(RecRacer rec) throws IllegalStateException;
}

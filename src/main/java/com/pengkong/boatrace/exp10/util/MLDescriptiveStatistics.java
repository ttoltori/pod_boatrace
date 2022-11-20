package com.pengkong.boatrace.exp10.util;

import java.util.List;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.pengkong.boatrace.common.enums.Delimeter;

public class MLDescriptiveStatistics extends DescriptiveStatistics {

	private static final long serialVersionUID = 1587372117256277016L;

	private static class InstanceHolder {
		private static final MLDescriptiveStatistics INSTANCE = new MLDescriptiveStatistics();
	}
	
	public static MLDescriptiveStatistics getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public MLDescriptiveStatistics() {
	}

	public MLDescriptiveStatistics(int window) throws MathIllegalArgumentException {
		super(window);
	}

	public MLDescriptiveStatistics(double[] initialDoubleArray) {
		super(initialDoubleArray);
	}

	public MLDescriptiveStatistics(DescriptiveStatistics original) throws NullArgumentException {
		super(original);
	}

	public void addValues(List<Double> listValues) {
		for (Double double1 : listValues) {
			addValue(double1);
		}
	}

	public void addValues(double[] values) {
		for (double d : values) {
			addValue(d);
		}
	}

	/**
	 * return the index of largest value from an array
	 * @return
	 */
	public int getIndexOfMax() {
		int largest = 0;
		double[] array = getValues(); 
		for (int i = 1; i < array.length; i++) {
			if (array[i] > array[largest])
				largest = i;
		}
		return largest; // position of the first largest found
	}

	public String getTitles(Delimeter delim) {
		StringBuilder sb = new StringBuilder();
		sb.append("count");sb.append(delim.getValue());
		sb.append("min");sb.append(delim.getValue());
		sb.append("max");sb.append(delim.getValue());
		sb.append("mean");sb.append(delim.getValue());
		sb.append("sum");sb.append(delim.getValue());
		sb.append("standardDeviation");sb.append(delim.getValue());
		sb.append("variance");sb.append(delim.getValue());
		sb.append("skewness");sb.append(delim.getValue());
		sb.append("kurtosis");
		
		return sb.toString();
	}
	
	public String getValues(Delimeter delim) {
		StringBuilder sb = new StringBuilder();
		sb.append(getN());sb.append(delim.getValue());
		sb.append(getMin());sb.append(delim.getValue());
		sb.append(getMax());sb.append(delim.getValue());
		sb.append(getMean());sb.append(delim.getValue());
		sb.append(getSum());sb.append(delim.getValue());
		sb.append(getStandardDeviation());sb.append(delim.getValue());
		sb.append(getVariance());sb.append(delim.getValue());
		sb.append(getSkewness());sb.append(delim.getValue());
		sb.append(getKurtosis());
		
		return sb.toString();
	}
}

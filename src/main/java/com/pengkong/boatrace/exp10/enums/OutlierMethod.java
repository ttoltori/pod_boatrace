package com.pengkong.boatrace.exp10.enums;

public enum OutlierMethod {
	QUARTILE("quartile"),
	OCTILE("octile"),
	PERCENTILE("percentile"),
	NO("no");

	private final String outlierMethod;
	
	private OutlierMethod(String outlierMethod) {
		this.outlierMethod = outlierMethod;
	}
	
	public String getValue() {
		return this.outlierMethod;
	}
}

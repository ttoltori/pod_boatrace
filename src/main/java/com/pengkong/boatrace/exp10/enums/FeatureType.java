package com.pengkong.boatrace.exp10.enums;

public enum FeatureType {
	NUMERIC("numeric"),
	NOMINAL("nominal"),
	STRING("string"),
	RELATIONAL("relational");

	private final String featureType;
	
	private FeatureType(String featureType) {
		this.featureType = featureType;
	}
	
	public String getValue() {
		return this.featureType;
	}
}

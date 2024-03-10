package com.pengkong.boatrace.exp10.enums;

public enum RangeValidationType {
	BEST("best"),
	WORST("worst"),
	NONE("x");

	private final String type;
	
	private RangeValidationType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return this.type;
	}
}

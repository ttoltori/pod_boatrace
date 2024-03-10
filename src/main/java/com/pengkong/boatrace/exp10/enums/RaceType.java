package com.pengkong.boatrace.exp10.enums;

public enum RaceType {
	CHAMPIONSHIP("7");

	private final String raceType;
	
	private RaceType(String value) {
		this.raceType = value;
	}
	
	public String getValue() {
		return this.raceType;
	}
}

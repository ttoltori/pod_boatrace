package com.pengkong.boatrace.common.enums;

public enum BetTypeJson {
	_1T("1"),
	_1F("2"),
	_2T("3"),
	_2F("4"),
	_KF("5"),
	_3T("6"),
	_3F("7");

	private final String bettype;
	
	private BetTypeJson(String bettype) {
		this.bettype = bettype;
	}
	
	public String getValue() {
		return this.bettype;
	}
}

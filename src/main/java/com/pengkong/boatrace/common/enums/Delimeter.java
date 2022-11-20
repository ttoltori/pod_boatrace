package com.pengkong.boatrace.common.enums;

public enum Delimeter {
	COMMA(","),
	PERIOD("\\."),
	TAB("\t"),
	SPACE(" "),
	DASH("-"),
	PLUS("\\+"),
	UNDERBAR("_"),
	EQUAL("="),
	WAVE("~"),
	COLON(":"),
	LINE_END("\n");

	private final String delimeter;
	
	private Delimeter(String delimeter) {
		this.delimeter = delimeter;
	}
	
	public String getValue() {
		return this.delimeter;
	}
}

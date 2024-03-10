package com.pengkong.boatrace.weka.automation.prop;

public class PatternComposition {

	public String betType;
	public String kumiban;
	public String name;
	public String value;
	@Deprecated
	public int compositionCount;
	public PatternComposition() {
	}

	public String toString() {
		//return "{bettype:" + betType + ", kumiban:" + kumiban + ", name:"+ name + ", value:" + value + ", compositionCount:" + compositionCount + "}";
		return "{bettype:" + betType + ", kumiban:" + kumiban + ", name:"+ name + "}";
	}
}

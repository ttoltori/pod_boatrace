package com.pengkong.boatrace.weka.automation.prop;

public class Pattern {
	public String name;
	public String value;
	@Deprecated
	public int count;
	
	public String toString() {
		return "{name:"+ name + ", value:" + value + ", count:" + count + "}";
	}
}

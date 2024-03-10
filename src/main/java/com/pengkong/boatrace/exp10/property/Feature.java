package com.pengkong.boatrace.exp10.property;

public class Feature {
	public String id;
	public String arffName;
	public String arffType;
	public String sqlForModel;
	public String sqlForInstance;

	/**
	 * @reture true=class定義なし（関連モデルも存在しない）  
	 */
	public static boolean isUndefined(String featureStr) {
		return (featureStr.equals("x"));
	}

}

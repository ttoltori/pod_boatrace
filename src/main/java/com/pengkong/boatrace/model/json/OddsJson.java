package com.pengkong.boatrace.model.json;

public class OddsJson {

	public String kumiban;
	public float oddsValue;
	public float minOddsValue;
	public float maxOddsValue;
	
	public OddsJson() {
	}
	
	public String getKumiban() {
		return kumiban.replaceAll("-", "").replaceAll("=", "");
	}

}

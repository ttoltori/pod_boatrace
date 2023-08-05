package com.pengkong.boatrace.exp10.odds;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Odds implements Serializable {
	public String ymd;
	public String jyoCd;
	public String raceNo;
	public String betType;
	public String kumiban;
	
	public Double value;
	public Integer rank;
	public Odds(String ymd, String jyoCd, String raceNo, String betType, String kumiban, Double value) {
		super();
		this.ymd = ymd;
		this.jyoCd = jyoCd;
		this.raceNo = raceNo;
		this.betType = betType;
		this.kumiban = kumiban;
		this.value = value;
	}
	
	public String getUniqueKey() {
		return String.join("_", ymd, jyoCd, raceNo, betType, kumiban);
	}

	public String getBettypeKey() {
		return String.join("_", ymd, jyoCd, raceNo, betType);
	}
	
	public String toString() {
		return String.join("_", ymd, jyoCd, raceNo, betType, kumiban, String.valueOf(rank), String.valueOf(value));
	}
}

package com.pengkong.boatrace.model;

import java.util.HashMap;

public class Racer {

	public String entry;
	public HashMap<String, Integer> mapWakuRankPower;
	public Racer(String entry) {
		this.entry = entry;
		mapWakuRankPower = new HashMap<>();
	}

	public void addWakuRankPower(String wakuRank, int power) {
		mapWakuRankPower.put(wakuRank, power);
	}
	
	public int getWakuRankPower(String wakuRank) {
		Integer ret = mapWakuRankPower.get(wakuRank);
		if (ret == null)
			return 0;
		return ret;
	}
}

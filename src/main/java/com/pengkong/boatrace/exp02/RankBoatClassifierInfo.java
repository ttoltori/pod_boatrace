package com.pengkong.boatrace.exp02;

import java.util.HashMap;

public class RankBoatClassifierInfo {

	// ex) 215_nopattern_20171231_rank1.model
	public String no; // 215
	public String pattern; // nopattern
	public int trainToYmd = -1; // 20171231
	// key = rank1, rank2, rank3
	public HashMap<String, BoatClassifierInfo> mapClsInfo;
	public RankBoatClassifier rankClassifier;
	public RankBoatClassifierInfo(String no, String pattern, int trainToYmd) {
		super();
		this.no = no;
		this.pattern = pattern;
		this.trainToYmd = trainToYmd;
		mapClsInfo = new HashMap<>();
	}
	
	public void add(BoatClassifierInfo cInfo) {
		// rank1 or rank2 or rank3
		String key = cInfo.keyRank;
		if (mapClsInfo.containsKey(key)) {
			throw new IllegalStateException("duplicate rank model key:" + key);
		}
		mapClsInfo.put(key, cInfo);
	}
	
	public void destroy() {
		for (BoatClassifierInfo cInfo :mapClsInfo.values()) {
			
			cInfo.destroy();
		}
		
		mapClsInfo = null;
		rankClassifier = null;
	}
}

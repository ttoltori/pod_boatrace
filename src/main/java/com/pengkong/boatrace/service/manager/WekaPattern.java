package com.pengkong.boatrace.service.manager;

import java.util.List;

import com.pengkong.boatrace.mybatis.entity.StPtnFinal;
import com.pengkong.common.collection.HashMapList;

public class WekaPattern {

	/** 組番 */
	public String kumiban;
	
	// key=pattern name
	public HashMapList<StPtnFinal> mapListPattern = new HashMapList<>();
	
	public WekaPattern() {
	}

	public int getPatternNameCount() {
		return mapListPattern.keySet().size();
	}
	
	public int getPatternValueCount(String patternName) {
		List<StPtnFinal> list = mapListPattern.get(patternName);
		if (list == null) {
			return 0;
		}
		
		return list.size();
	}
	
	public int getPatternValueCountAll() {
		int cnt = 0;
		for (String key : mapListPattern.keySet()) {
			cnt += mapListPattern.get(key).size();
		}
		return cnt;
	}
}

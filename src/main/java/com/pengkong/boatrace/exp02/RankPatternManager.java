package com.pengkong.boatrace.exp02;

import java.util.HashMap;
import java.util.List;

import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class RankPatternManager {

	PropertyUtil prop = PropertyUtil.getInstance();
	
	/** key = patternName:pattern, value= betcnt */
	HashMap<String, RankPattern> mapPattern;
	
	private static class InstanceHolder {
		private static final RankPatternManager INSTANCE = new RankPatternManager();
	}
	
	public static RankPatternManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public RankPatternManager() {
	}

	public int getBetcnt(String patternName, String pattern) throws Exception {
		if (mapPattern == null) {
			initialize();
		}
		
		String key = patternName + ":" + pattern;
		RankPattern ptn = mapPattern.get(key);
		if (ptn == null) {
			throw new Exception("pattern doesn't exist." + patternName + ":" + pattern);
		}
		
		return ptn.betcnt;
	}
	
	public boolean isValidForModel(String patternName, String pattern) throws Exception {
		int minBetCnt = prop.getInteger("min_betcnt_model");
		
		return (getBetcnt(patternName, pattern) >= minBetCnt); 
	}
	
	/**
	 *  모든 패턴들을 로딩한다.
	 */
	private void initialize() throws Exception {
		mapPattern = new HashMap<>();
		String filepath = prop.getString("rank_pattern_file");
		List<String> listLine = FileUtil.readFileByLineArr(filepath, "UTF-8");
		for (String line : listLine) {
			String[] token = line.split(",");
			RankPattern ptn = new RankPattern(token[0], token[1], token[2]);
			mapPattern.put(ptn.getKey(), ptn);
		}
	}
	
	private class RankPattern {
		public String patternName;
		public String pattern;
		public Integer betcnt;
		public RankPattern(String patternName, String pattern, String betcnt) {
			super();
			this.patternName = patternName;
			this.pattern = pattern;
			this.betcnt = Integer.parseInt(betcnt);
		}
		
		public String getKey() {
			 return patternName + ":" + pattern;
		}
	}
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().putProperty("rank_pattern_file", "C:\\Dev\\workspace\\Oxygen\\pod_boatrace_test\\experiment\\expr02\\rank_pattern.csv");
			PropertyUtil.getInstance().putProperty("min_betcnt_model", "500");
			

			RankPatternManager mgr = RankPatternManager.getInstance();
			System.out.println(mgr.isValidForModel("jyocd+level1", "20_B2"));
			System.out.println(mgr.isValidForModel("jyocd+level1", "20_B1"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

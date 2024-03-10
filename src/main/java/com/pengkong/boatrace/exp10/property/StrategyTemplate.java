package com.pengkong.boatrace.exp10.property;

import java.util.HashMap;
import java.util.List;

import com.pengkong.common.FileUtil;

/**
 * properties pre-condition: file_features
 * 
 * @author ttolt
 *
 */
public class StrategyTemplate {

	/** ex) key = manjiOdds, value = Strategy */ 
	HashMap<String, Strategy> strategyMap;
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	boolean isInitialized = false;
	private static class InstanceHolder {
		private static final StrategyTemplate INSTANCE = new StrategyTemplate();
	}
	
	public static StrategyTemplate getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public StrategyTemplate() {
	}

	private void initialize() throws Exception {
		strategyMap = new HashMap<>();
		String filepath = prop.getString("file_strategy");
		List<String> lines = FileUtil.readFileByLineArr(filepath);
		for (String string : lines) {
			if (string.startsWith("id")) { // 1行目はカラムタイトルのためスキップする
				continue;
			}
			Strategy strategy = createStrategy(string);
			strategyMap.put(strategy.id, strategy);
		}
		
		isInitialized = true;
	}
	
	/**
	 * return Strategy with id
	 * @param id
	 * @return Strategy
	 */
	public Strategy getStrategy(String id) throws Exception {
		if (!isInitialized)
			initialize();
		return strategyMap.get(id);
	}
	
	Strategy createStrategy(String line) {
		String[] token = line.split("\t");
		Strategy feature = new Strategy();
		feature.id = token[0];
		feature.javaClass = token[1];
		feature.manjiFactor = Double.valueOf(token[2]);
		feature.manjiOdds = Double.valueOf(token[3]);
		
		return feature;
	}
}

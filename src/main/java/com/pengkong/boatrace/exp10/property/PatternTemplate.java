package com.pengkong.boatrace.exp10.property;

import java.util.HashMap;
import java.util.List;

import com.pengkong.common.FileUtil;

/**
 * properties pre-condition: file_class
 * 
 * @author ttolt
 *
 */
public class PatternTemplate {

	/** ex) key = nopattern, value = Pattern */ 
	HashMap<String, Pattern> patternMap = new HashMap<>();
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	boolean isInitialized = false;
	
	private static class InstanceHolder {
		private static final PatternTemplate INSTANCE = new PatternTemplate();
	}
	
	public static PatternTemplate getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public PatternTemplate() {
	}

	private void initialize() throws Exception {
		String filepath = prop.getString("file_pattern");
		List<String> lines = FileUtil.readFileByLineArr(filepath);
		for (String string : lines) {
			Pattern item = createPattern(string);
			patternMap.put(item.id, item);
		}
		
		isInitialized = true;
	}
	
	/**
	 * return Pattern
	 * @param Pattern id
	 * @return
	 */
	public Pattern getPattern(String id) throws Exception {
		if (!isInitialized)
			initialize();
		
		return patternMap.get(id);
	}
	
	Pattern createPattern(String line) {
		String[] token = line.split("\t");
		Pattern pattern = new Pattern();
		pattern.id = token[0];
		pattern.sql = token[1];
		
		return pattern;
	}

	public static void main(String[] args) {
		try {
			MLPropertyUtil.getInstance().putProperty("file_pattern", "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/pattern.tsv");
			PatternTemplate template = new PatternTemplate();
			System.out.println(template.getPattern("nopattern").sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

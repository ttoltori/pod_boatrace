package com.pengkong.boatrace.exp10.property;

import java.util.HashMap;
import java.util.List;

import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

/**
 * properties pre-condition: file_class
 * 
 * @author ttolt
 *
 */
public class AlgorithmTemplate {

	/** ex) key = bayesnet, value = Algorithm */ 
	HashMap<String, Algorithm> algorithmMap = new HashMap<>();
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	boolean isInitialized = false;
	
	private static class InstanceHolder {
		private static final AlgorithmTemplate INSTANCE = new AlgorithmTemplate();
	}
	
	public static AlgorithmTemplate getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public AlgorithmTemplate() {
	}

	private void initialize() throws Exception {
		String filepath = prop.getString("file_algorithm");
		List<String> lines = FileUtil.readFileByLineArr(filepath);
		for (String string : lines) {
			Algorithm item = createAlgorithm(string);
			algorithmMap.put(item.id, item);
		}
		
		isInitialized = true;
	}
	
	/**
	 * return Algorithm
	 * @param algorithm id
	 * @return
	 */
	public Algorithm getAlgorithm(String id) throws Exception {
		if (!isInitialized)
			initialize();
		
		return algorithmMap.get(id);
	}
	
	Algorithm createAlgorithm(String line) {
		String[] token = line.split("\t");
		Algorithm algorithm = new Algorithm();
		algorithm.id = token[0];
		algorithm.cmd = token[1];
		
		return algorithm;
	}

	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().putProperty("file_algorithm", "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10_algorithm.tsv");
			AlgorithmTemplate template = new AlgorithmTemplate();
			System.out.println(template.getAlgorithm("bayesnet").cmd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

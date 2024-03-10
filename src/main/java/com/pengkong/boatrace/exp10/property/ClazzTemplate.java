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
public class ClazzTemplate {

	/** ex) key = c123only, value = Classes */ 
	HashMap<String, Clazz> clazzMap = new HashMap<>();
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	boolean isInitialized = false;
	
	private static class InstanceHolder {
		private static final ClazzTemplate INSTANCE = new ClazzTemplate();
	}
	
	public static ClazzTemplate getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public ClazzTemplate() {
	}

	private void initialize() throws Exception {
		String filepath = prop.getString("file_class");
		List<String> lines = FileUtil.readFileByLineArr(filepath);
		for (String string : lines) {
			Clazz clazz = createClass(string);
			clazzMap.put(clazz.id, clazz);
		}
		
		isInitialized = true;
	}

	/**
	 * return Clazz
	 * @param id id
	 * @return
	 */
	public Clazz getClazz(String id) throws Exception {
		if (!isInitialized)
			initialize();
		
		return clazzMap.get(id);
	}
	
	Clazz createClass(String line) {
		String[] token = line.split("\t");
		Clazz clazz = new Clazz();
		clazz.id = token[0];
		clazz.conditionSql = token[1];
		clazz.valuesArff = token[2];
		clazz.featuresSql = token[3];
		
		return clazz;
	}

	public static void main(String[] args) {
		try {
			MLPropertyUtil.getInstance().putProperty("file_class", "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/class.tsv");
			ClazzTemplate cTemplate = new ClazzTemplate();
			System.out.println(cTemplate.getClazz("r1-1x").conditionSql);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

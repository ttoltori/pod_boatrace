package com.pengkong.boatrace.exp10.property;

import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

/**
 * properties pre-condition: file_class
 * 
 * @author ttolt
 *
 */
public class FeatureSetTemplate {

	/** ex) key = en, value = en1,en2,en3,en4,en5,en6 */ 
	HashMap<String, String> featureSetMap = new HashMap<>();
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	FeatureTemplate fTpl = FeatureTemplate.getInstance();
	
	boolean isInitialized = false;
	
	private static class InstanceHolder {
		private static final FeatureSetTemplate INSTANCE = new FeatureSetTemplate();
	}
	
	public static FeatureSetTemplate getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public FeatureSetTemplate() {
	}

	private void initialize() throws Exception {
		String filepath = prop.getString("file_featureset");
		List<String> lines = FileUtil.readFileByLineArr(filepath);
		for (String string : lines) {
			String[] token = string.split("\t");
			featureSetMap.put(token[0], token[1]);
		}
		
		isInitialized = true;
	}
	
	public String[] getFeatureIds(String featuresetId) throws Exception {
		if (!isInitialized)
			initialize();
		
		return featureSetMap.get(featuresetId).split(Delimeter.COMMA.getValue());
	}

	public String getFeatureIdString(String featuresetId) throws Exception {
		if (!isInitialized)
			initialize();
		
		return featureSetMap.get(featuresetId);
	}
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().putProperty("file_algorithm", "C:/Dev/workspace/Oxygen/pod_boatrace/properties/featureset.tsv");
			FeatureSetTemplate template = new FeatureSetTemplate();
			System.out.println(template.getFeatureIds("ext_en_1"));
			System.out.println(template.getFeatureIds("ext_en_nw_1"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

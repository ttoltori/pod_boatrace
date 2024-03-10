package com.pengkong.boatrace.exp10.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.common.FileUtil;

/**
 * properties pre-condition: file_features
 * 
 * @author ttolt
 *
 */
public class FeatureTemplate {

	/** ex) key = nw1, value = Feature */ 
	HashMap<String, Feature> featureMap;
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	boolean isInitialized = false;
	private static class InstanceHolder {
		private static final FeatureTemplate INSTANCE = new FeatureTemplate();
	}
	
	public static FeatureTemplate getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public FeatureTemplate() {
	}

	private void initialize() throws Exception {
		featureMap = new HashMap<>();
		String filepath = prop.getString("file_features");
		List<String> lines = FileUtil.readFileByLineArr(filepath);
		for (String string : lines) {
			Feature feature = createFeature(string);
			featureMap.put(feature.id, feature);
		}
		
		isInitialized = true;
	}
	
	/**
	 * return Feature with featureId
	 * @param featureId
	 * @return Feature
	 */
	public Feature getFeature(String featureId) throws Exception {
		if (!isInitialized)
			initialize();
		return featureMap.get(featureId);
	}
	
	public List<Feature> getFeatureList(String[] featureIds) throws Exception {
		if (!isInitialized)
			initialize();
		
		List<Feature> result = new ArrayList<>();
		for (String featureId : featureIds) {
			result.add(getFeature(featureId));
		}
		
		return result;
	}
	
	/**
	 * return column list of features for sql of model generation   
	 * @param featureIds comma seperated id list ex) en1,en2
	 * @return
	 */
	public String getFeaturesSqlForModel(String[] featureIds) throws Exception {
		if (!isInitialized)
			initialize();

		StringBuilder sb = new StringBuilder();
		for (String string : featureIds) {
			Feature feature = featureMap.get(string.trim());
			sb.append(feature.sqlForModel);
			sb.append(Delimeter.COMMA.getValue());
		}
		
		return sb.substring(0, sb.length()-1);
	}
	
	/**
	 * return column list of features for sql of ml model execution   
	 * @param featureIds comma seperated id list ex) en1,en2
	 * @return
	 */
	public String getFeaturesSqlForInstance(String[] featureIds) throws Exception{
		if (!isInitialized)
			initialize();
		
		StringBuilder sb = new StringBuilder();
		for (String string : featureIds) {
			Feature feature = featureMap.get(string.trim());
			sb.append(feature.sqlForInstance);
			sb.append(Delimeter.COMMA.getValue());
		}
		
		return sb.substring(0, sb.length()-1);
	}
	
	
	
	Feature createFeature(String line) {
		try {
			String[] token = line.split("\t");
			Feature feature = new Feature();
			feature.id = token[0];
			feature.arffName = token[1];
			feature.arffType = token[2];
			feature.sqlForModel = token[3];
			feature.sqlForInstance = token[4];
			return feature;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static void main(String[] args) {
		try {
			MLPropertyUtil.getInstance().putProperty("file_features", "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10_features.tsv");
			FeatureTemplate fTemplate = new FeatureTemplate();
			System.out.println(fTemplate.getFeaturesSqlForModel("en1,en2,en3,en6".split(Delimeter.COMMA.getValue())));
			System.out.println(fTemplate.getFeaturesSqlForInstance("nw1,n2w1,n3w3,m3w6".split(Delimeter.COMMA.getValue())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

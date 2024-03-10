package com.pengkong.boatrace.exp02;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

import lombok.Getter;

public class RankModelManager {

	PropertyUtil prop = PropertyUtil.getInstance();
	
	/** key = description */
	private HashMap<String, RankModel> mapModel;
	
	private static class InstanceHolder {
		private static final RankModelManager INSTANCE = new RankModelManager();
	}
	
	public static RankModelManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public RankModelManager() {
	}

	
	public List<String> getDescriptionList() throws Exception {
		checkInitialize();
		
		return new ArrayList<String>(mapModel.keySet());
	}
	
	public String getPatternName(String description) throws Exception{
		checkInitialize();
		RankModel model = mapModel.get(description);
		
		return model.getPatternName();
	}
	
	private void checkInitialize() throws Exception {
		if (mapModel == null) {
			initialize();
		}
	}
	
	/**
	 *  모든 model들을 로딩한다.
	 */
	private void initialize() throws Exception {
		mapModel = new HashMap<>();
		String filepath = prop.getString("rank_model_file");
		List<String> listLine = FileUtil.readFileByLineArr(filepath, "UTF-8");
		for (String line : listLine) {
			String[] token = line.split(",");
			RankModel model = new RankModel(token[0], token[1], token[2], token[3], token[4]);
			mapModel.put(model.description, model);
		}
	}
	
	@Getter
	private class RankModel {
		private String description;
		private String patternName;
		private String attributes;
		private String algorithm;
		private Integer trainingDays;
		
		public RankModel(String description, String patternName, String attributes, String algorithm,
				String training_days) {
			super();
			this.description = description;
			this.patternName = patternName;
			this.attributes = attributes;
			this.algorithm = algorithm;
			this.trainingDays = Integer.parseInt(training_days);
		}
	}
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().putProperty("rank_model_file", "C:\\Dev\\workspace\\Oxygen\\pod_boatrace_test\\experiment\\expr02\\rank_model.csv");
			PropertyUtil.getInstance().putProperty("rank_pattern_file", "C:\\Dev\\workspace\\Oxygen\\pod_boatrace_test\\experiment\\expr02\\rank_pattern.csv");
			PropertyUtil.getInstance().putProperty("min_betcnt_model", "500");
			

			RankModelManager mgr = RankModelManager.getInstance();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

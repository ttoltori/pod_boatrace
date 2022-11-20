package com.pengkong.boatrace.service.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.model.PopularItem;
import com.pengkong.common.FileUtil;
import com.pengkong.common.collection.HashMapList;

public class PopularLevelManager {
	Logger logger = LoggerFactory.getLogger(PopularLevelManager.class);

	private HashMapList<PopularItem> mapPopular = new HashMapList<>();

	private static PopularLevelManager instance = new PopularLevelManager();

	public static PopularLevelManager getInstance() {
		return instance;
	}

	public PopularLevelManager() {
	}

	public List<PopularItem> getItems(String jyoCd, String setuTurn, String levelPattern) {
		return mapPopular.get(jyoCd + "_" + setuTurn + "_" + levelPattern);
	}

	public void load(String filepath, String type) throws Exception {
		List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
		for (String line : lines) {
			String[] token = line.split(",");
			ArrayList<PopularItem> itemList = new ArrayList<>();
			// key = 場コード_節日目_レベルパタン
			String key = token[0];
			for (int i = 2; i < token.length; i++) {
				PopularItem item = new PopularItem(type, i-1, Float.parseFloat(token[i]));
				itemList.add(item);
			}
			mapPopular.put(key, itemList);
		}
		logger.info("PopularManager initialization is OK.");
	}

	public static void main(String[] args) {
		try {
			PopularLevelManager.getInstance()
					.load("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/popular_level_2T_ip.csv", "2T");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

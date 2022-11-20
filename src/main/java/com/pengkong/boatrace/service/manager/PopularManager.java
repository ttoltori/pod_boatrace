package com.pengkong.boatrace.service.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.model.PopularItem;
import com.pengkong.common.FileUtil;
import com.pengkong.common.collection.HashMapList;

public class PopularManager {
	Logger logger = LoggerFactory.getLogger(PopularManager.class);

	private HashMapList<PopularItem> mapPopular = new HashMapList<>();

	private static PopularManager instance = new PopularManager();

	public static PopularManager getInstance() {
		return instance;
	}

	public PopularManager() {
	}

	public List<PopularItem> getItems(String jyoCd, String setuTurn, String raceNo) {
		return mapPopular.get(jyoCd + "_" + setuTurn + "_" + raceNo);
	}

	public void load(String filepath, String type) throws Exception {
		List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
		for (String line : lines) {
			String[] token = line.split(",");
			ArrayList<PopularItem> itemList = new ArrayList<>();
			// key = 場コード_節日目_レースNO
			String key = token[0];
			for (int i = 1; i < token.length; i++) {
				PopularItem item = new PopularItem(type, i, Float.parseFloat(token[i]));
				itemList.add(item);
			}
			mapPopular.put(key, itemList);
		}
		logger.info("PopularManager initialization is OK.");
	}

	public static void main(String[] args) {
		try {
			PopularManager.getInstance()
					.load("C:/Dev/workspace/Oxygen/pod_boatrace/release/properties/popular_2T_ip.csv", "2T");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

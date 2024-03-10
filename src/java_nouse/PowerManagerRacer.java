package com.pengkong.boatrace.service.manager;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatProperty;
import com.pengkong.boatrace.mybatis.entity.PowerRacer;
import com.pengkong.boatrace.mybatis.entity.PowerRacerBettype;
import com.pengkong.common.FileUtil;

public class PowerManagerRacer {
	private Logger logger = LoggerFactory.getLogger(PowerManagerRacer.class);

	private HashMap<String, PowerRacer> mapPowerRacer = new HashMap<>();
	private HashMap<String, PowerRacerBettype> mapPowerRacerBettype = new HashMap<>();

	private static PowerManagerRacer instance = new PowerManagerRacer();

	public PowerManagerRacer() {
	}

	public static PowerManagerRacer getInstance() {
		return instance;
	}

	public void loadDailyFile(String ymd) throws Exception {
		String filepath;
		List<String> lines;
//		// load power_racer
//		mapPowerRacer.clear();
//		filepath = BoatProperty.DIRECTORY_STASTICS + ymd + "_" + "power_racer.csv";
//		lines = FileUtil.readFileByLineArr(filepath, "UTF8");
//		for (String line : lines) {
//			String[] token = line.split(",");
//			PowerRacer pr = PowerRacer.fromCsv(token);
//			mapPowerRacer.put(pr.createKey(), pr);
//		}

		// load power_racer_bettype
		mapPowerRacerBettype.clear();
		filepath = BoatProperty.DIRECTORY_STASTICS + ymd + "_" + "power_racer_bettype.csv";
		lines = FileUtil.readFileByLineArr(filepath, "UTF8");
		for (String line : lines) {
			String[] token = line.split(",");
			PowerRacerBettype prb = PowerRacerBettype.fromCsv(token);
			mapPowerRacerBettype.put(prb.createKey(), prb);
		}
		
//		logger.info("PowerManagerRacer load OK. " + ymd);
	}
	
//	public PowerRacer getPowerRacer(String key) {
//		return mapPowerRacer.get(key);
//	}
	
	public PowerRacerBettype getPowerRacerBettype(String key) {
		return mapPowerRacerBettype.get(key);
	}
}

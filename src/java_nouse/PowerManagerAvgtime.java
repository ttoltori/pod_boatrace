package com.pengkong.boatrace.service.manager;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatProperty;
import com.pengkong.boatrace.mybatis.entity.PowerAvgtime;
import com.pengkong.boatrace.mybatis.entity.PowerAvgtimeBettype;
import com.pengkong.common.FileUtil;

public class PowerManagerAvgtime {
	private Logger logger = LoggerFactory.getLogger(PowerManagerAvgtime.class);

	private HashMap<String, PowerAvgtime> mapPowerAvgtime = new HashMap<>();
	private HashMap<String, PowerAvgtimeBettype> mapPowerAvgtimeBettype = new HashMap<>();

	private static PowerManagerAvgtime instance = new PowerManagerAvgtime();

	public PowerManagerAvgtime() {
	}

	public static PowerManagerAvgtime getInstance() {
		return instance;
	}

	public void loadDailyFile(String ymd) throws Exception {
		String filepath;
		List<String> lines;
//		// load power_avgtime
//		mapPowerAvgtime.clear();
//		filepath = BoatProperty.DIRECTORY_STASTICS + ymd + "_" + "power_avgtime.csv";
//		lines = FileUtil.readFileByLineArr(filepath, "UTF8");
//		for (String line : lines) {
//			String[] token = line.split(",");
//			PowerAvgtime pr = PowerAvgtime.fromCsv(token);
//			mapPowerAvgtime.put(pr.createKey(), pr);
//		}

		// load power_avgtime_bettype
		mapPowerAvgtimeBettype.clear();
		filepath = BoatProperty.DIRECTORY_STASTICS + ymd + "_" + "power_avgtime_bettype.csv";
		lines = FileUtil.readFileByLineArr(filepath, "UTF8");
		for (String line : lines) {
			String[] token = line.split(",");
			PowerAvgtimeBettype prb = PowerAvgtimeBettype.fromCsv(token);
			mapPowerAvgtimeBettype.put(prb.createKey(), prb);
		}
		
//		logger.info("PowerManagerAvgtime load OK. " + ymd);
	}
	
//	public PowerAvgtime getPowerAvgtime(String key) {
//		return mapPowerAvgtime.get(key);
//	}
	
	public PowerAvgtimeBettype getPowerAvgtimeBettype(String key) {
		return mapPowerAvgtimeBettype.get(key);
	}
}

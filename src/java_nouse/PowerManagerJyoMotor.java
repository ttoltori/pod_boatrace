package com.pengkong.boatrace.service.manager;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatProperty;
import com.pengkong.boatrace.mybatis.entity.PowerJyoMotor;
import com.pengkong.boatrace.mybatis.entity.PowerJyoMotorBettype;
import com.pengkong.common.FileUtil;

public class PowerManagerJyoMotor {
	private Logger logger = LoggerFactory.getLogger(PowerManagerJyoMotor.class);

	private HashMap<String, PowerJyoMotor> mapPowerJyoMotor = new HashMap<>();
	private HashMap<String, PowerJyoMotorBettype> mapPowerJyoMotorBettype = new HashMap<>();

	private static PowerManagerJyoMotor instance = new PowerManagerJyoMotor();

	public PowerManagerJyoMotor() {
	}

	public static PowerManagerJyoMotor getInstance() {
		return instance;
	}

	public void loadDailyFile(String ymd) throws Exception {
		String filepath;
		List<String> lines;
//		// load power_jyo_motor
//		mapPowerJyoMotor.clear();
//		filepath = BoatProperty.DIRECTORY_STASTICS + ymd + "_" + "power_jyo_motor.csv";
//		lines = FileUtil.readFileByLineArr(filepath, "UTF8");
//		for (String line : lines) {
//			String[] token = line.split(",");
//			PowerJyoMotor pr = PowerJyoMotor.fromCsv(token);
//			mapPowerJyoMotor.put(pr.createKey(), pr);
//		}

		// load power_jyo_motor_bettype
		mapPowerJyoMotorBettype.clear();
		filepath = BoatProperty.DIRECTORY_STASTICS + ymd + "_" + "power_jyo_motor_bettype.csv";
		lines = FileUtil.readFileByLineArr(filepath, "UTF8");
		for (String line : lines) {
			String[] token = line.split(",");
			PowerJyoMotorBettype prb = PowerJyoMotorBettype.fromCsv(token);
			mapPowerJyoMotorBettype.put(prb.createKey(), prb);
		}
		
//		logger.info("PowerManagerJyoMotor load OK. " + ymd);
	}
	
//	public PowerJyoMotor getPowerJyoMotor(String key) {
//		return mapPowerJyoMotor.get(key);
//	}
	
	public PowerJyoMotorBettype getPowerJyoMotorBettype(String key) {
		return mapPowerJyoMotorBettype.get(key);
	}
}

package com.pengkong.boatrace.exp10.racer.trend.calculator;

import java.util.HashMap;
import java.util.Map;

import com.pengkong.boatrace.exp10.enums.RaceType;

public class PointHelper {
	/** key = {grade}{racetype}_{着順} ex)SG7_1 or 0(7以外は0扱いする) */
	static Map<String, Double> mapPoints = new HashMap<>();
	
	static {
		mapPoints.put("LV1_7_1", 100.0);
		mapPoints.put("LV1_7_2", 98.0);
		mapPoints.put("LV1_7_3", 94.0);
		mapPoints.put("LV1_7_4", 91.0);
		mapPoints.put("LV1_7_5", 88.0);
		mapPoints.put("LV1_7_6", 85.0);
		
		mapPoints.put("LV1_0_1", 85.0);
		mapPoints.put("LV1_0_2", 82.0);
		mapPoints.put("LV1_0_3", 77.0);
		mapPoints.put("LV1_0_4", 73.0);
		mapPoints.put("LV1_0_5", 69.0);
		mapPoints.put("LV1_0_6", 65.0);

		mapPoints.put("LV2_7_1", 80.0);
		mapPoints.put("LV2_7_2", 78.0);
		mapPoints.put("LV2_7_3", 74.0);
		mapPoints.put("LV2_7_4", 71.0);
		mapPoints.put("LV2_7_5", 68.0);
		mapPoints.put("LV2_7_6", 65.0);
		
		mapPoints.put("LV2_0_1", 70.0);
		mapPoints.put("LV2_0_2", 67.0);
		mapPoints.put("LV2_0_3", 62.0);
		mapPoints.put("LV2_0_4", 58.0);
		mapPoints.put("LV2_0_5", 54.0);
		mapPoints.put("LV2_0_6", 50.0);
		
		mapPoints.put("LV3_7_1", 65.0);
		mapPoints.put("LV3_7_2", 63.0);
		mapPoints.put("LV3_7_3", 59.0);
		mapPoints.put("LV3_7_4", 56.0);
		mapPoints.put("LV3_7_5", 53.0);
		mapPoints.put("LV3_7_6", 50.0);
		
		mapPoints.put("LV3_0_1", 60.0);
		mapPoints.put("LV3_0_2", 58.0);
		mapPoints.put("LV3_0_3", 55.0);
		mapPoints.put("LV3_0_4", 50.0);
		mapPoints.put("LV3_0_5", 40.0);
		mapPoints.put("LV3_0_6", 30.0);
	}

	public static Double getPoint(String grade, String raceType, String rank) throws IllegalStateException{
		String level;
		if (grade.equals("SG") || grade.equals("G1")) {
			level = "LV1";
		} else if (grade.equals("G2")) {
			level = "LV2";
		} else {
			level = "LV3";
		}
		
		if (raceType == null) {
			System.out.println("debug");
		}
		String raceTypeSimple;
		if (raceType.equals(RaceType.CHAMPIONSHIP.getValue())) {
			raceTypeSimple = "7";
		} else {
			raceTypeSimple = "0";
		}
		
		String key = String.join("_", level, raceTypeSimple, rank);
		
		Double result = mapPoints.get(key);
		if (result == null) {
			throw new IllegalStateException("unable to calculate point. key=" + key);
		}
		
		return result;
	}
}

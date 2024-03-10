package com.pengkong.boatrace.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.pengkong.boatrace.common.BoatTemplate;

public class Odds {

	/** ３連単 */
	public HashMap<String, Float> odds3T;
	
	/** ３連複 */
	public HashMap<String, Float> odds3F;
	
	/** 2連単 */
	public HashMap<String, Float> odds2T;
	
	/** 2連複 */
	public HashMap<String, Float> odds2F;
	
	/** 拡連複 */
	public HashMap<String, Float[]> oddsKF;
	
	/** 単勝 */
	public HashMap<String, Float> odds1T;
	
	/** 複勝 */
	public HashMap<String, Float[]> odds1F;

	public static String toCsv(Map<String, Float> map) {
		StringBuilder sb = new StringBuilder();
		Map<String, Float> sortedMap = new TreeMap<>(map);
		Iterator<Entry<String, Float>> it = sortedMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Float> entry = it.next();
			sb.append(entry.getKey());
			sb.append(BoatTemplate.ODDS_DELIMITER);
			sb.append(entry.getValue());
			sb.append(BoatTemplate.CSV_DELIMITER);
		}
		sb.deleteCharAt(sb.length()-1);
		
		return sb.toString();
	}
	
	public static String toCsvFloats(Map<String, Float[]> map) {
		StringBuilder sb = new StringBuilder();
		Map<String, Float[]> sortedMap = new TreeMap<>(map);
		Iterator<Entry<String, Float[]>> it = sortedMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Float[]> entry = it.next();
			sb.append(entry.getKey());
			sb.append(BoatTemplate.ODDS_DELIMITER);
			Float[] values = entry.getValue();
			sb.append(values[0]);
			sb.append("-");
			sb.append(values[1]);
			sb.append(BoatTemplate.CSV_DELIMITER);
		}
		sb.deleteCharAt(sb.length()-1);
		
		return sb.toString();
	}

}

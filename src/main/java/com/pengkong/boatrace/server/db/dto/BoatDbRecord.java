package com.pengkong.boatrace.server.db.dto;

import com.pengkong.boatrace.exception.UndefinedPatternUnitException;

@SuppressWarnings("serial")
public class BoatDbRecord extends DBRecord {

	
	public BoatDbRecord() {
		super();
	}
	
	public static String getPatternValue(DBRecord rec, String patternName) throws UndefinedPatternUnitException {
		String[] tokenPtn = patternName.split("\\+");
		StringBuilder sb = new StringBuilder();
		for (String ptnUnit : tokenPtn) {
			String value = rec.getString(ptnUnit);
			if (value == null) {
				throw new UndefinedPatternUnitException("pattern unit=" + ptnUnit);
			}
			
			sb.append(value);
			sb.append("_");
		}
		String result = sb.toString();
		return result.substring(0,result.length()-1);
	}
	
	public static String getSortKey(BoatDbRecord rec) {
		return rec.getString("ymd") + rec.getString("sime") + rec.getString("jyocd"); 
	}
}

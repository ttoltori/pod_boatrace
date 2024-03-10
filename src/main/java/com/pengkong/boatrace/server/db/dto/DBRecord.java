package com.pengkong.boatrace.server.db.dto;

import java.math.BigDecimal;
import java.util.HashMap;

import com.pengkong.boatrace.exception.NullValueException;
import com.pengkong.common.MathUtil;

@SuppressWarnings("serial")
public class DBRecord extends HashMap<String, Object> {
	public DBRecord(){
	}

	
	public BigDecimal getBigDecimal(String key) {
		return (BigDecimal) super.get(key);
	}
	
	public Double getDouble(String key) {
		return (Double) super.get(key);
	}
	
	public Double getPercentizedDouble(String key, double min, double max) {
		return MathUtil.convertToPercentile((Double) super.get(key), min, max, 20); 
	}
	
	public double[] getDoubleArray(String key) {
		return (double[]) super.get(key);
	}
	
	public int[] getIntArray(String key) {
		return (int[]) super.get(key);
	}
	
	public double getDoubleValue(String key) throws NullValueException {
		Double value = (Double) super.get(key);
		if (value == null) {
			throw new NullValueException("key=" + key);
		}
		
		return value.doubleValue();
	}
	
	public Integer getInteger(String key) {
		return (Integer) super.get(key);
	}

	public String getIntegerStr(String key, String defaultValue) {
		Integer value = (Integer) super.get(key);
		if (value == null) {
			return defaultValue;
		}
		
		return value.toString();
	}
	
	public int getInt(String key) throws NullValueException {
		Integer value = (Integer) super.get(key);
		if (value == null) {
			throw new NullValueException("key=" + key);
		}
		
		return value.intValue();
	}
	
	public int getInt(String key, int defaultValue) {
		Integer value = (Integer) super.get(key);
		if (value == null) {
			return defaultValue;
		}
		
		return value.intValue();
	}
	
	public String getStringForced(String key) {
		return String.valueOf(super.get(key));
	}
	
	public String getString(String key) {
		return (String)super.get(key);
	}

	public String getString(String key, String defaultValue) {
		String value = (String) super.get(key);
		if (value == null) {
			return defaultValue;
		}
		
		return value;
	}
	
	public String[] getStringArray(String...columns) {
		String[] results = new String[columns.length];
		for (int i=0; i < columns.length; i++) {
			results[i] = getString(columns[i]);
		}
		
		return results;
	}

	public BigDecimal getDoubleAsDecimal(String key) throws NullValueException {
		return new BigDecimal(getDoubleValue(key));
	}
	
	public String toString() {
		return DBRecord.toCsv(this);
	}

	public static String toCsv(DBRecord rec) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : rec.values()) {
			sb.append(String.valueOf(obj));
			sb.append(",");
		}
		
		return sb.substring(0, sb.length()-1);
	}
}

package com.pengkong.boatrace.simulation.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Properties;

import com.pengkong.common.StringUtil;

public class BettingRule {

	private Properties properties = null;
	
	private String ruleFilepath;
	public String ruleName = "";
	
	public BettingRule(String ruleFilepath) throws Exception {
		this.ruleFilepath = ruleFilepath;
		
		properties  = new Properties();
		try (InputStreamReader istream = new InputStreamReader(new FileInputStream(ruleFilepath), "UTF-8")) {
			properties.load(istream);
		}
		ruleName = new File(ruleFilepath).getName();
	}

	public int[] getIntArray(String key) throws Exception{
		String str =  properties.getProperty(key);
		if (StringUtil.isEmpty(key)) {
			throw new Exception("Invalid property:" + key);
		}
		
		String[] arrStr = str.split(",");
		int[] arrInt = new int[arrStr.length];
		for (int i = 0; i < arrInt.length; i++) {
			arrInt[i] = Integer.parseInt(arrStr[i]);
		}
		
		return arrInt; 
	}
	
	public String getString(String key) throws Exception {
		
		String ret =  properties.getProperty(key);
		if (StringUtil.isEmpty(key)) {
			throw new Exception("Invalid property:" + key);
		}
		return ret;
	}
	
	public int getInt(String key) throws Exception {
		return Integer.parseInt(getString(key));
	}
	
	public float getFloat(String key) throws Exception {
		return Float.parseFloat(getString(key));
	}

	public BigDecimal getBigDecimal(String key) throws Exception {
		return new BigDecimal(getString(key));
	}
	
	public Short getShort(String key) throws Exception {
		return Short.parseShort(getString(key));
	}
	
	public void save() throws Exception {
		try (OutputStream ostream = new FileOutputStream(ruleFilepath)) {
			properties.store(ostream, "");
		}
	}
	
	public static void main(String[] args) {
		try {
			BettingRule rule = new BettingRule("C:/Dev/workspace/Oxygen/pod_boatrace/release/properties/rule4.properties");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

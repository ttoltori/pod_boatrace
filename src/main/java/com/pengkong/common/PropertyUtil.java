package com.pengkong.common;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class PropertyUtil {

	private Properties props = new Properties();
	
	private static class PropertyUtilInstanceHolder {
		private static final PropertyUtil INSTANCE = new PropertyUtil();
	}
	
	public static PropertyUtil getInstance() {
		return PropertyUtilInstanceHolder.INSTANCE;
	}

	public PropertyUtil() {
	}
	
	public void addFile(String filePath) throws Exception {
		InputStreamReader cf = new InputStreamReader(new FileInputStream(filePath), "UTF-8" );
		props.load(cf);
	}
	
	public List<Prop> getProps(String keyParts) {
		List<Prop> result = new ArrayList<>();
		for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements();) {
			String key = (String)e.nextElement();
			if (key.startsWith(keyParts)) {
				String newKey = key.replace(keyParts, "");
				result.add(new Prop(newKey, props.getProperty(key)));
			}
		}
		
		return result;
	}
	
	public String getString(String key) {
		return props.getProperty(key); //.trim();
	}
	
	public String getString(String key, String defaultValue) {
		String result = props.getProperty(key);//.trim();

		if (StringUtil.isEmpty(result)) {
			result = defaultValue;
		} 
		
		return result;
	}
	
	public Integer getInteger(String key) {
		return Integer.valueOf(props.getProperty(key).trim());
	}
	
	public Float getFloat(String key) {
		return Float.valueOf(props.getProperty(key).trim());
	}
	
	public Double getDouble(String key) {
		return Double.valueOf(props.getProperty(key).trim());
	}

	public void putProperty(String key, String value) {
		props.put(key, value);
	}
	
	public void clear() {
		props.clear();
	}
	
	/**
	 * プロパティから表現式でkeyを検索した結果を取得する。
	 * @param searchStr 検索文字列
	 * @return
	 */
	public List<Prop> getProperties(String searchStr) {
		List<Prop> result = new ArrayList<>();
		
		@SuppressWarnings("rawtypes")
		Enumeration e = props.propertyNames();
        while (e.hasMoreElements())
        {
            String key = e.nextElement().toString();
            if (key.startsWith(searchStr)) {
            	result.add(new Prop(key, props.getProperty(key)));
            }
        }
		
		return result;
	}
}

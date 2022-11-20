package com.pengkong.boatrace.weka.automation;

import java.io.File;
import java.util.HashMap;

import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class DefineFileParser {

	HashMap<String, String> mapItem;
	public String defineFilepath;
	public DefineFileParser() {
	}

	public void parseFile(String defineFilepath) throws Exception {
		mapItem = new HashMap<>();
		this.defineFilepath = defineFilepath;
		
		String fileNameOnly = new File(defineFilepath).getName().split("\\.")[0];
		mapItem.put("name", fileNameOnly);
		mapItem.put("no", fileNameOnly.split("_")[0]);
		
		String strAll = FileUtil.readFileIntoString(defineFilepath, "UTF8");
		//strAll = strAll.replace("\r\n", "");
		
		String[] tokenSection = strAll.split("//");
		for (String section : tokenSection) {
			String[] itemToken = section.split(":=");
			String key = itemToken[0].trim();
			String value = itemToken[1].trim();
			mapItem.put(key, value);
		}
		// 20191130 모델생성배치를 일괄실행시 모든 def파일을 다 수정해야하니 너무 번거롭다.
		// 프로퍼티를 참조하는 것으로 한다.
		String resultRankge = PropertyUtil.getInstance().getString("result_range");
		if (resultRankge != null) {
			String[] token = PropertyUtil.getInstance().getString("result_range").split("~");
			//String[] token = mapItem.get("result_range").split("~");
			mapItem.put("resultStartYmd", token[0]);
			mapItem.put("resultEndYmd", token[1]);
		}
	}
	
	public String getValue(String key) {
		return mapItem.get(key);
	}
}

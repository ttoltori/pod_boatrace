package com.pengkong.boatrace.exp02;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.weka.automation.DefineFileParser;
import com.pengkong.boatrace.weka.automation.NominalManager;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class RankArffCreator {
	Logger logger = LoggerFactory.getLogger(RankArffCreator.class);
	PropertyUtil prop = PropertyUtil.getInstance();
	
	NominalManager nominalManager = NominalManager.getInstance();
	
	DefineFileParser def;
	
	protected int rankCount = 3;
	public RankArffCreator(DefineFileParser def) {
		 this.def = def; 
	}

	/**
	 * arff파일을 생성한다. 
	 * @param listDb 대상 디비 데이터
	 * @param prefixFilepath arff파일풀패스 (뒤에 rank를 추가하여 완성시킨다.)
	 * @return 생성된 arff파일패스들의 리스트
	 * @throws Exception
	 */
	public List<String> create(List<DBRecord> listDb, String prefixFilepath) throws Exception {
		String arffFilepath;
		List<String> results = new ArrayList<String>();
		for (int i = 1; i <= rankCount; i++) {
			arffFilepath = prefixFilepath + "_rank" + i + ".arff";
			
			StringBuilder sb = new StringBuilder();
			sb.append(createArffHeader());
			sb.append(createArffBody("rank" + i, listDb));
			
			FileUtil.writeFile(arffFilepath, sb.toString());
			results.add(arffFilepath);
		}
		
		return results;
	}
	
	private StringBuilder createArffHeader() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("% " + def.getValue("name")); sb.append(System.lineSeparator());
		sb.append("@RELATION boatrace"); sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		String[] tokenAttrs = def.getValue("attributes").split(",");
		for (String attr : tokenAttrs) {
			sb.append(convertAttribute(attr));
			sb.append(System.lineSeparator());
		}
		
		String strClassValues = def.getValue("class_values");
		String classValues = null;
		if (strClassValues.startsWith("range")) {
			// ex) range:100:500:10  10単位増分の100~500(prize)
			String[] token = strClassValues.split(":");
			classValues = nominalManager.getRange(
					Integer.parseInt(token[1]), Integer.parseInt(token[2]), Integer.parseInt(token[3]));
		} else {
			classValues = strClassValues;
		}
		 
		sb.append("@ATTRIBUTE class {" + classValues + "}");sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		sb.append("@DATA");
		sb.append(System.lineSeparator());
		
		return sb;
	}
	
	private StringBuilder createArffBody(String rankColumnName, List<DBRecord> listDb) throws Exception {
		StringBuilder sb = new StringBuilder();
		String[] tokenAttrs = def.getValue("attributes").split(",");
		for (DBRecord rec : listDb) {
			for (String attribute : tokenAttrs) {
				String[] tokenAttr = attribute.split(":");
				String attrName = tokenAttr[0].trim();
				sb.append(rec.getString(attrName));
				sb.append(",");
			}
			sb.append(rec.getString(rankColumnName));
			sb.append(System.lineSeparator());
		}
		
		return sb;
	}
	
	private String convertAttribute(String attribute) {
		String[] tokenAttr = attribute.split(":");
		String attrName = tokenAttr[0].trim();
		String attrType = tokenAttr[1].trim();
		
		// nominal attribute
		if (attrType.equals("nominal")) {
			return "@ATTRIBUTE " + attrName + " " + nominalManager.getNominalAttr(attrName);
		} 
		
		// numeric attribute
		return "@ATTRIBUTE " + attrName + " NUMERIC"; 
	}
	
	public static void main(String[] args) {
	}
}

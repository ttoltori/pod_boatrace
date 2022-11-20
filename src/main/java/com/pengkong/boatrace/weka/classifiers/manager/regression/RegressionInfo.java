package com.pengkong.boatrace.weka.classifiers.manager.regression;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.weka.classifiers.regression.BoatRegressionDefault;

public class RegressionInfo {

	// ex) 34_2F_12-16_regr_20170101_20171231.model
	public String no; // 34
	public String bettype; // 2F
	public String classes; // 12-16
	public String attributes; // entry
	// public String trainFromYmd = "20100101";
	public int trainToYmd; // 20171231
	public String modelFilepath;
	public String defFilepath;
	public boolean isMulti = false;
	
	public BoatRegressionDefault classifier;
	
	Logger logger = LoggerFactory.getLogger(RegressionInfo.class);
	
	public RegressionInfo() {
	}
	
	/**
	 * create ClassifierInfo instance from mode file name without extension.
	 * @param modelFilenameOnly
	 * @return
	 */
	public static RegressionInfo createInstance(String modelFilenameOnly) {
		RegressionInfo cInfo = new RegressionInfo();
		
		// ex) 40_1T_1_entry_multi_20100101_20161231.... 37_1T_2_entry_20171231
		cInfo.isMulti = true; //multi
		String[] token = modelFilenameOnly.split("_");
		cInfo.no = token[0]; // 40
		cInfo.bettype = token[1]; // 1T
		cInfo.classes = token[2]; // 1 
		cInfo.attributes = token[3]; // entry
		cInfo.trainToYmd = Integer.parseInt(token[token.length-1]); // last token.
		
		return cInfo;
	}
	
	/**
	 * 모델 정보 리스트를 모델 리스트로 변환한다.
	 * @param listInfo
	 * @return
	 * @throws Exception
	 */
	public static List<BoatRegressionDefault> convertInfoList2ClassifierList(List<RegressionInfo> listInfo) throws Exception {
		List<BoatRegressionDefault> results = new ArrayList<>();
		for (RegressionInfo cInfo : listInfo) {
			// classifierがすでに生成されていれば再生成はしない。
			if (cInfo.classifier == null) {
				cInfo.classifier = BoatRegressionDefault.createInstance(cInfo);
				// cInfo.logger.info("BoatClassifierDefault " + cInfo.modelFilepath + " created.");
			}
			results.add(cInfo.classifier);
		}
		
		return results;
	}
}

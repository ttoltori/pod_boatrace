package com.pengkong.boatrace.exp02;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 랭크모델을 통합관리하는 정보클래스
 * @author qwerty
 *
 */
public class BoatClassifierInfo {
	
	// ex) 215_nopattern_20171231_rank1.model
	public String no; // 215
	public String pattern; // nopattern
	public int trainToYmd; // 20171231
	public String keyRank;
	public BoatClassifier classifier;
	public String modelFilepath;
	public String defineFilepath;
	
	Logger logger = LoggerFactory.getLogger(BoatClassifierInfo.class);
	
	public BoatClassifierInfo() {
	}

	public String getParentKey() {
		return pattern + "_" + trainToYmd;
	}
	
	public static BoatClassifierInfo create(File modelFile, String defineFilepath) {
		BoatClassifierInfo cinfo = new BoatClassifierInfo();
		 // 모델 파일명으로부터 랭크부분을 취득한다. (파일명토큰의 맨 마지막. ex) 215_20191020_100_rank1.model -> rank1)
		String fileNameOnly = modelFile.getName().split("\\.")[0];
		String[] token = fileNameOnly.split("_");
		cinfo.keyRank = token[token.length-1];
		cinfo.modelFilepath = modelFile.getPath();
		cinfo.defineFilepath = defineFilepath;
		cinfo.no = token[0];
		if (token.length == 4) {
			cinfo.pattern = token[1];
			cinfo.trainToYmd = Integer.parseInt(token[2]);
		} else if (token.length == 5) {
			cinfo.pattern = token[1] + "_" + token[2];
			cinfo.trainToYmd = Integer.parseInt(token[3]);
		} else if (token.length == 6) {
			cinfo.pattern = token[1] + "_" + token[2] + "_" + token[3];
			cinfo.trainToYmd = Integer.parseInt(token[4]);
		}
		
		return cinfo;
	}
	
	public void destroy() {
		// logger.debug("destroy classifier:" + this);
		classifier.destroy();
		classifier = null;
	}
	
	public String toString() {
		return "{no:"+ no + ", pattern:" + pattern + ", trainToYmd:" + trainToYmd + ", keyRank=" + keyRank + "}";
	}
}

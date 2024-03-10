package com.pengkong.boatrace.exp10.model;

import java.util.List;

/**
 * 実験番号/rankNo/patternをキーとして以下デレクとりの複数のモデルファイル名を保持する。 
 * @author ttolt
 *
 */
public class ModelInfoSet {
	public String exNo; // 実験番号
	public String rankNo;
	public String pattern;
	List<ModelInfo> models;
	
	public ModelInfoSet(String exNo, String rankNo, String pattern) {
		super();
		this.exNo = exNo;
		this.rankNo = rankNo;
		this.pattern = pattern;
	}
	
	/**
	 * 指定日付に対して直近の対象モデルファイルを返却する。
	 * @param reqYmd 指定日付
	 * @return
	 */
	public ModelInfo get(int reqYmd) {
		for (ModelInfo file : models) {
			if (reqYmd > file.ymd) {
				return file;
			}
		}
		
		return null;
	}
}

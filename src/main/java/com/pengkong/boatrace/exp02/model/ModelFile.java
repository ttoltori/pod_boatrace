package com.pengkong.boatrace.exp02.model;

public class ModelFile {

	public String description;
	public String pattern;
	public String ymd;
	public String rank;
	public ModelFile(String fileName) {
		String[] token = fileName.split("_");
		description = token[0];
		if (token.length == 6) {
			pattern = token[1] + "_" + token[2] + "_" + token[3];
			ymd = token[4];
			rank = token[5];
		} else if (token.length == 5) {
			pattern = token[1] + "_" + token[2];
			ymd = token[3];
			rank = token[4];
		} else {
			pattern = token[1];
			ymd = token[2];
			rank = token[3];
		}
	}

	public boolean isValid() {
		if (description == null || description.length() != 4) {
			return false;
		}
		
//		if (pattern == null) {
//			return false;
//		}
		
		if (ymd == null || ymd.length() != 8) {
			return false;
		}
		
		if (rank != null && (rank.equals("rank1.model") || rank.equals("rank2.model") || rank.equals("rank3.model") ))  {
			return true;
		}
		
		return false;
	}
	
	
	public String getYm() {
		return ymd.substring(0, 6);
	}
}

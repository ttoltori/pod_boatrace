package com.pengkong.boatrace.model;

import com.pengkong.boatrace.common.BoatTemplate;

/**
 * レース情報
 * @author qwerty
 *
 */
public class RaceInfo2 {
	public String fixedEntrance = "";
	public String raceType = "";
	public String wakuLevelList = "";
	public int aLevelCount = -1;
	public int femaleCount = -1; // 20180708追加
	
	// 20181118 追加
	public String avgstCondRank = ""; // 平均STコンディションランク=(平均ST－節平均ST）のランク
	public String setuwinRank = ""; //節着順ランク = 節平均着順のランク 
	public String flRank = ""; //罰点ランク＝（L回数＋F回数）のランク
	// 20181118 追加
	
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(fixedEntrance);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(raceType);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(wakuLevelList);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(aLevelCount);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(femaleCount);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(avgstCondRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(setuwinRank);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(flRank);

		return sb.toString();
	}
}

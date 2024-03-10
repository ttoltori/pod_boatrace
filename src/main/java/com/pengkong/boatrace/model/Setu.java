package com.pengkong.boatrace.model;

import com.pengkong.boatrace.common.BoatTemplate;

public class Setu {
	/** 節名 = Schedule.setu */
	public String name;
	/** グレード = Schedule.grade */
	public String grade;
	/** 女子戦  = Schedule.isVenus */
	public String isVenus;
	/** 時間帯  = Schedule.timezone */
	public String timezone;
	/** 今節の何日目  番組表ファイルから取得.*/
	public String turn;
	/** 場コード */
	public String jyoCd;
	/** 開催場 Schedule.jyo */
	public String jyo;
	/** 優勝者 Schedule.winner */
	public String winner;
	
	/** 当日節のレースリストページへのリンク */
	public String raceListPageUrl;
	
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(grade);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(isVenus);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(timezone);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(turn);
		sb.append(BoatTemplate.CSV_DELIMITER);
		// 20180415
		sb.append(jyoCd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(jyo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(winner);
		
		return sb.toString();
	}
}

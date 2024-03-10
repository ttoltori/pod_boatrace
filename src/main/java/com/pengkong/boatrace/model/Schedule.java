package com.pengkong.boatrace.model;

import com.pengkong.boatrace.common.BoatTemplate;

/**
 * 年間スケジュールのscraping情報.
 * Scheduleを特定するキー：yyyy + 場 + タイトル
 * @author qwerty
 *
 */
public class Schedule {
	public int fromYmd;
	public int toYmd;
	public String jyo;
	/**
	 * SG,G1,G2,G3,ip
	 */
	public String grade;
	/**
	 * Y=女子戦 N=混合
	 */
	public String isVenus;
	/**
	 * N=nighter, M=morning, S=summer time, O=ordinary
	 */
	public String timezone;
	public String title;
	public String winner;
	/**
	 * SGスケジュールページから取得したか。 Y/N
	 */
	public String isFromSgSchedulePage;
	
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(fromYmd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(toYmd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(jyo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(grade);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(isVenus);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(timezone);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(title);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(winner);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(isFromSgSchedulePage);
		
		return sb.toString();
	}
}

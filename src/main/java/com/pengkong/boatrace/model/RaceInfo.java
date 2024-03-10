package com.pengkong.boatrace.model;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.common.BoatTemplate;

/**
 * レース情報
 * @author qwerty
 *
 */
public class RaceInfo {
	public int year;
	public int month;
	public int day;
	public String ymd;
	/** 2018/4/15 レース時間（時）*/
	public int hh;
	/** 2021/5/30 レース時間`（分）*/
	public int mm;
	public int no;
	/** hhmm */
	public int sime;
	/** true = 過去のレース */
	public boolean isPastRace = false;
	public float temparature;
	public String weather;
	public String windDirection;
	public float wind;
	public float waterTemparature;
	public int wave;
	// 20180708 性別取得のために追加
	public List<String> sexList = new ArrayList<>();
	
//	/** 節情報 */
//	public Setu setu = new Setu();
//	
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(month);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(day);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(ymd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(hh);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(no);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(sime);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(temparature);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(weather);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(windDirection);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(wind);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(waterTemparature);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(wave);

		return sb.toString();
	}
}

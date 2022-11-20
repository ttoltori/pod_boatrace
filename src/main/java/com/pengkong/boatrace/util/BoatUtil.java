package com.pengkong.boatrace.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.common.StringUtil;

public class BoatUtil {
	private static DateTimeFormatter formatHm= DateTimeFormat.forPattern("HHmm");
	private static DateTimeFormatter formatHms= DateTimeFormat.forPattern("HHmmss");
	private static DateTimeFormatter formatYmd = DateTimeFormat.forPattern("yyyyMMdd");
	private static DateTimeFormatter formatYmdHm = DateTimeFormat.forPattern("yyyyMMddHHmm");
	private static DateTimeFormatter formatYmdHms = DateTimeFormat.forPattern("yyyyMMddHHmmss");

	/** 投票金額単位に換算する
	 * ex) 99 -> 100, 199 -> 100, 201 -> 200, 1999 -> 1900 
	 * @param betAmt 金額
	 * @return
	 */
	public static int convBetUnit(int betAmt) {
		int betamt = (betAmt / 100 * 100); 
		if (betamt <= 0) {
			return 100;
		}

		return betamt;
	}
	
	public static String currentYmd() {
		return formatYmd.print(DateTime.now());
	}
	
	public static int daysBetween(String fromYmd, String toYmd) {
		return Days.daysBetween(formatYmd.parseDateTime(fromYmd), formatYmd.parseDateTime(toYmd)).getDays();
	}
	
	public static String daysBeforeYmd(String ymd, int daysBefore) {
		return formatYmd.print(formatYmd.parseDateTime(ymd).minusDays(daysBefore));
	}
	
	public static String daysAfterYmd(String ymd, int daysAfter) {
		return formatYmd.print(formatYmd.parseDateTime(ymd).plusDays(daysAfter));
	}
	
	public static String yesterdayYmd(String todayYmd) {
		//return "20170308"; //JSJ 
		return formatYmd.print(formatYmd.parseDateTime(todayYmd).minusDays(1));
	}
	
	public static String yesterdayYmd() {
		return formatYmd.print(DateTime.now().minusDays(1));
	}
	
	public static int currentHmInt() {
		return Integer.parseInt(formatHm.print(DateTime.now()));
	}

	public static String currentYyyyMMddHHmmSS() {
		return formatYmdHms.print(DateTime.now());
	}
	
	public static String currentHms() {
		return formatHms.print(DateTime.now());
	}

	public static DateTime parseYmd(String ymd) {
		return formatYmd.parseDateTime(ymd);
	}
	
	public static String formatYmd(DateTime date) {
		return formatYmd.print(date);
	}
	
	public static String sortKumiban(String kumiban) {
		String[] token = kumiban.split("");
		Arrays.sort(token);
		
		return String.join("", token);
	}

	public static boolean isAfterHms(String hhmmss) {
		int currHms = Integer.parseInt(formatHms.print(DateTime.now()));
		int basisHms = Integer.parseInt(hhmmss);
		//logger.debug(String.format("%06d", currHms) + "," + String.format("%06d", basisHms));
		return (currHms >= basisHms);
	}
	
	public static boolean isBeforeInRangeSeconds(String hhmmss, int fromSeconds, int toSeconds){
		int currHms = Integer.parseInt(formatHms.print(DateTime.now()));
		
		DateTime fromDateTime = formatHms.parseDateTime(hhmmss);
		//fromDateTime = fromDateTime.plusMinutes(1);
		fromDateTime = fromDateTime.minusSeconds(fromSeconds);
		
		DateTime toDateTime = formatHms.parseDateTime(hhmmss);
		//toDateTime = toDateTime.plusMinutes(1);
		toDateTime = toDateTime.minusSeconds(toSeconds);
		
		int fromHms = Integer.parseInt(formatHms.print(fromDateTime));
		int toHms = Integer.parseInt(formatHms.print(toDateTime));
		
		//logger.debug(String.format("%06d", fromHms) + "," + String.format("%06d", currHms) + "," + String.format("%06d", toHms));
		return (currHms >= fromHms && currHms <= toHms);
	}
	
	/**
	 * 指定hmdHmsから指定分後のymdHmsを返却する
	 * @param ymdHms 例) 20180503110000
	 * @param minute 例) 15
	 * @return 例) 20180503111500
	 */
	public static String getYhdHmsAfterMinutes(String ymdHms, int minute) {
		DateTime dateTime = formatYmdHms.parseDateTime(ymdHms);
		dateTime = dateTime.plusMinutes(minute);
		return formatYmdHms.print(dateTime);
	}
	
	/**
	 * 時刻が現在時刻を過ぎているか判定する。
	 * @param HHmm 例)0814
	 * @return
	 */
	public static boolean isPastHms(RaceEx race) {
		String hhmmss = String.format("%06d", race.sortTime);
		DateTime basisTime = formatHms.parseDateTime(hhmmss);
		//basisTime.plusMinutes(1);
		int basisHhmmss = Integer.parseInt(formatHms.print(basisTime));
		int nowHms = Integer.parseInt(formatHms.print(DateTime.now()));
		return (basisHhmmss < nowHms);
	}
	
	/**
	 * 当日のサービス開始時刻まで待つ時間をミリ秒で返却する。
	 * (00:00~サービス開始時刻にAP起動された場合に呼び出される。
	 * @param serviceStartHm 例）0815
	 * @return
	 */
	public static long getWaitForTodayServiceStart(String serviceStartHm) {
		DateTime now = DateTime.now();
		String strYmdHm = formatYmd.print(now) + serviceStartHm;
		DateTime next = formatYmdHm.parseDateTime(strYmdHm);
		Duration duration = new Duration(now, next);
		
		return duration.getMillis();
	}
	
	/**
	 * 翌日のサービス開始時刻まで待つ時間をミリ秒で返却する。
	 * @param serviceStartHm 例）0815
	 * @return
	 */
	public static long getWaitForNextServiceStart(String serviceStartHm) {
		DateTime now = DateTime.now();
		String strYmdHm = formatYmd.print(now.plusDays(1)) + serviceStartHm;
		DateTime next = formatYmdHm.parseDateTime(strYmdHm);
		Duration duration = new Duration(now, next);
		
		return duration.getMillis();
	}
	
	public static int toInt(String s) {
		if (StringUtil.isEmpty(s) || s.equals("-")) {
			return -1;
		}
		return Integer.parseInt(s);
	}
	
	public static short toShort(String s) {
		if (StringUtil.isEmpty(s) || s.equals("-")) {
			return -1;
		}
		return Short.parseShort(s);
	}

	public static float toFloat(String s, Float defaultValue) {
		if (StringUtil.isEmpty(s) || s.equals("-")) {
			return defaultValue;
		}
		return Float.parseFloat(s);
	}
	
	public static float toFloat(String s) {
		return BoatUtil.toFloat(s, -1f);
	}

	public static BigDecimal toBigDecimal(String s) {
		return new BigDecimal(s);
	}
	
	public static String typeToKachisiki(String type) {
		if (type.equals("1T")) {
			return "1";
		} else if (type.equals("2T")) {
			return "3";
		} else if (type.equals("2F")) {
			return "4";
		} else if (type.equals("3T")) {
			return "6";
		} else if (type.equals("3F")) {
			return "7";
		} else {
			return null;
		}
	}
	
	/**
	 * 選手の勝点を計算する
	select avg(alevelcount) from rec_race where racetype in ('優勝戦'); 5.2646003262642741
	select avg(alevelcount) from rec_race where racetype in ('準優勝戦', '準優勝'); 4.454804535035004
	select avg(alevelcount) from rec_race where racetype in ('予選'); 2.4153585318556556
	select avg(alevelcount) from rec_race where racetype in ('一般戦', '一般'); 2.1493885036820781
	select avg(alevelcount) from rec_race where racetype in ('予選特選', '予選特賞'); 4.0436848140665101
	select avg(alevelcount) from rec_race where racetype like '選抜'; 3.5982658959537572
	select avg(alevelcount) from rec_race where racetype in ('特選', '特賞'); 4.2361232885951305
	*/
	/**
	 * @param rank
	 * @param raceType
	 * @return
	 */
	public static int calculateRacerWiningPoint(int rank, String raceType) {
		int point = 0;
		if (rank == 1) {
			point += 10;
			if (raceType.equals("優勝戦")) {
				point += 1;
			}
		} else if (rank == 2) {
			point += 8;
			if (raceType.equals("優勝戦")) {
				point += 1;
			}
		} else if (rank == 3) {
			point += 6;
			if (raceType.equals("優勝戦")) {
				point += 1;
			}
		} else if (rank == 4) {
			point += 4;
			if (raceType.equals("優勝戦")) {
				point += 2;
			}
		} else if (rank == 5) {
			point += 2;
			if (raceType.equals("優勝戦")) {
				point += 2;
			}
		} else if (rank == 6) {
			point += 1;
			if (raceType.equals("優勝戦")) {
				point += 2;
			}
		} else { // 転覆などランクアウト
			point = 0;
		}
		
		return point;
	}
	
	/** モータの勝点を計算する
	 * @param rank 着順
	 * @return
	 */
	public static int calculateMotorWiningPoint(int rank) {
		int point = 0;
		if (rank == 1) {
			point += 10;
		} else if (rank == 2) {
			point += 8;
		} else if (rank == 3) {
			point += 6;
		} else if (rank == 4) {
			point += 4;
		} else if (rank == 5) {
			point += 2;
		} else if (rank == 6) {
			point += 1;
		} else { // 転覆などランクアウト
			point = 0;
		}
		
		return point;
	}

	/** 平均STが０の場合統計に影響を最小化するために末端値に書き換える　例）0 -> 0.8  (0.8,0.3, ..., 0.1)
	 * 20181125
	 * @param listWaku
	 */
	public static void modifyAverageStart(List<Waku> listWaku) {
		// 0以外の最低値を求める
		float averageStartMax = 0;
		
		for (Waku waku : listWaku) {
			if (waku.averageStart != 0f) {
				if (waku.averageStart > averageStartMax) {
					averageStartMax = waku.averageStart;
				}
			}
		}
		
		for (Waku waku : listWaku) {
			if (waku.averageStart == 0f) {
				waku.averageStart = averageStartMax;
			}
		}
	}

	
	public static String getSetuNumber(String setu) {
		if (setu.equals("初日")) {
			return "1";
		} else if (setu.equals("２日目")) {
			return "2";
		} else if (setu.equals("３日目")) {
			return "3";
		} else if (setu.equals("４日目")) {
			return "4";
		} else if (setu.equals("５日目")) {
			return "5";
		} else if (setu.equals("６日目")) {
			return "6";
		} else if (setu.equals("最終日")) {
			return "7";
		} else {
			return "0";
		}
	}
	
	public static String getRacetypeSimple(String racetype) {
		if (racetype.equals("予選")) {
			return "予選";
		}
		if (racetype.equals("一般戦") || racetype.equals("一般")) {
			return "一般";
		}
		if (racetype.equals("準優勝戦") || racetype.equals("準優勝")) {
			return "準優勝戦";
		}
		if (racetype.equals("優勝戦")) {
			return "優勝戦";
		}
		if (racetype.equals("予選特選") || racetype.equals("予選特賞")) {
			return "予選特";
		}
		if (racetype.contains("選抜")) {
			return "選抜";
		}
		if (racetype.equals("特選") || racetype.equals("特賞")) {
			return "特";
		}
		
		return "else";
	}
	

	public static String getRacetypeSimpleExp10(String racetype) {
		if (racetype.equals("予選")) {
			return "1";
		}
		if (racetype.equals("一般戦") || racetype.equals("一般")) {
			return "3";
		}
		if (racetype.equals("準優勝戦") || racetype.equals("準優勝")) {
			return "6";
		}
		if (racetype.equals("優勝戦")) {
			return "7";
		}
		if (racetype.equals("予選特選") || racetype.equals("予選特賞")) {
			return "2";
		}
		if (racetype.contains("選抜")) {
			return "4";
		}
		if (racetype.equals("特選") || racetype.equals("特賞")) {
			return "5";
		}
		
		return "else";
	}

	public static String getFixedEntranceSimple(String src) throws IllegalStateException{
		if (src.equals("N")) {
			return "N";
		} else if (src.equals("安定板使用")) {
			return "Y";
		} else if (src.equals("進入固定")) {
			return "F";
		} else {
			throw new IllegalStateException("invalid fixed entrance. " + src);
		}
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(BoatUtil.daysBetween("20210602", "20220331"));
//			System.out.println(BoatUtil.convBetUnit(99));
//			System.out.println(BoatUtil.convBetUnit(199));
//			System.out.println(BoatUtil.convBetUnit(201));
//			System.out.println(BoatUtil.convBetUnit(1999));
//			System.out.println(BoatUtil.daysBetween("20190101", "20190731"));
//			System.out.println(BoatUtil.sortKumiban("312"));
//			System.out.println("12345".substring(0, 4));
//			System.out.println(BoatUtil.yesterdayYmd("20170501"));
//			System.out.println(BoatUtil.yesterdayYmd());
//			System.out.println(BoatUtil.isBeforeInRanceSeconds("034730", 30, 10));
//			System.out.println(BoatUtil.getYhdHmsAfterMinutes("20180503110000", 15));
//			System.out.println(BoatUtil.isPastHms(20900));
//			System.out.println(BoatUtil.isPastHms(21000));
//			System.out.println(BoatUtil.isPastHms(21100));
//			System.out.println(BoatUtil.getWaitForNextServiceStart("0815"));
			
//			BoatUtil.convertRaceEx(
//			"2017,3,11,20170311,11,1,1101,11.0,晴,13,2.0,11.0,1,第５回シニアＶＳヤング,ip,,,３日目,02,戸田,N/A,4553,3315,4639,3404,2910,4881,坪口竜也,広瀬聖仁,雑賀勇貴,矢崎誠一,片山晃,檀将太,27,51,26,49,61,21,B1,B1,B1,B1,B1,B1,51.1,52.4,53.3,51.2,50.5,50.5,長崎,愛知,大阪,東京,岡山,福岡,長崎,愛知,大阪,山梨,岡山,福岡,2,1,4,0,3,0,6.77,6.89,6.85,6.86,6.88,6.72,0.16,0.14,0.13,0.07,0.19,0.16,-0.5,-0.5,-0.5,-0.5,-0.5,-0.5,,,,,,,,,,,,,1,0,0,0,0,1,0,0,0,0,0,0,0.19,0.16,0.17,0.18,0.19,0.18,5.0,5.68,3.93,3.32,4.17,2.64,30.26,36.26,17.78,12.24,21.69,5.45,47.37,56.04,32.22,24.49,36.14,12.73,5.9,5.61,4.65,2.69,5.71,0.0,40.0,46.43,29.41,6.25,28.57,0.0,70.0,53.57,47.06,12.5,71.43,0.0,58,46,49,56,17,13,39.74,30.9,41.13,35.1,31.64,43.33,61.59,44.38,56.45,49.67,48.59,62.78,49,67,26,30,64,52,34.46,20.45,26.81,35.26,39.75,34.18,54.73,40.91,41.3,53.85,65.84,49.37,644,315,5 32,1 36,321,6 65,.33/.29/.27/////////,.09/.11/.18/////////,.14//.18/.16////////,.07//.20/.23////////,.15/.12/.22/////////,.20//.08/.02////////,246,454,5 34,3 56,651,3 4Ｆ,0.25,0.35,0.34,0.19,0.16,0.31,1,3,6,2,5,4,6R,11R,8R,,5R,,1.5,4.1,6.6,9.5,11.1,11.1,1.2,1.5,1.2,1.6,1.2,1.6,12.3,16.8,4.3,6.0,6.3,8.8,142,3640,10,124,780,3,14,1240,4,14,850,4,1,150,1,1,150,1,4,1230,6,逃げ,142653,142653,B1-B1-B1-B1-B1-B1,643512,643512,643512,643251,645312,643215,254136,254316,236145,236415,432615,613452,2-1-5-3,2,");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isBreakPoint(RaceEx race, String ymd, String jyoCd, String raceNo) {
		return ( (race.raceInfo.ymd.equals(ymd))
				&& (race.getJyoCd().equals(jyoCd))
				&& (race.getRaceNo().equals(raceNo))
				);
	}
}

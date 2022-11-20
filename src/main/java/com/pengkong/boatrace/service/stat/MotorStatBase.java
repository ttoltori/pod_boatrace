package com.pengkong.boatrace.service.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * 選手毎の統計情報
 * @author qwerty
 *
 */
public class MotorStatBase {
	// public static final String KEY_YMD = "ymd";
	public static final String KEY_JYOCD = "jyoCd";
	public static final String KEY_RACENO = "raceNo";
	public static final String KEY_MOTOR_2WININGRATE = "motor2WiningRate";
	public static final String KEY_MOTOR_3WININGRATE = "motor3WiningRate";
	public static final String KEY_WAKU = "waku";
	public static final String KEY_RANK = "rank";
	public static final String KEY_POINT = "point";
	
	public Short motorNo;
	
	/** モータ交換月 key=jyoCd, value=MMdd */
	HashMap<String, String> mapReplaceMMdd = new HashMap<>();
	
	/** 集計範囲の判定に必要 */
	List<Long> listYmd = new ArrayList<>();
	
	/** 処理を簡単にするためにすべての数値は格納値をFloatに変換して保存する */
	HashMapList<Float> mapFloatList = new HashMapList<>();

	/** 日付変換用フォーマッタ */
	DateTimeFormatter formatterYmd = DateTimeFormat.forPattern("yyyyMMdd");
	
	int startIdx = 0;
	int endIdx = 0;
	
	public MotorStatBase() {
		mapReplaceMMdd.put("01", "1201");
		mapReplaceMMdd.put("02", "0701");
		mapReplaceMMdd.put("03", "0401");
		mapReplaceMMdd.put("04", "0601");
		mapReplaceMMdd.put("05", "0801");
		mapReplaceMMdd.put("06", "0401");
		mapReplaceMMdd.put("07", "0501");
		mapReplaceMMdd.put("08", "1201");
		mapReplaceMMdd.put("09", "0901");
		mapReplaceMMdd.put("10", "0401");
		mapReplaceMMdd.put("11", "0601");
		mapReplaceMMdd.put("12", "0301");
		mapReplaceMMdd.put("13", "0401");
		mapReplaceMMdd.put("14", "0401");
		mapReplaceMMdd.put("15", "1101");
		mapReplaceMMdd.put("16", "0101");
		mapReplaceMMdd.put("17", "0901");
		mapReplaceMMdd.put("18", "0401");
		mapReplaceMMdd.put("19", "0201");
		mapReplaceMMdd.put("20", "1201");
		mapReplaceMMdd.put("21", "0501");
		mapReplaceMMdd.put("22", "0601");
		mapReplaceMMdd.put("23", "0801");
		mapReplaceMMdd.put("24", "0301");
	}

	public void addRecRacer(RecRacer recRacer){
		if (motorNo == null) {
			motorNo = recRacer.getMotorno();
		}

		listYmd.add(Long.valueOf(recRacer.getYmd()));
		
		mapFloatList.addItem(KEY_JYOCD, Float.valueOf(recRacer.getJyocd()));
		mapFloatList.addItem(KEY_RACENO, Float.valueOf(recRacer.getRaceno()));
		mapFloatList.addItem(KEY_MOTOR_2WININGRATE, recRacer.getMotor2winingrate().floatValue());
		mapFloatList.addItem(KEY_MOTOR_3WININGRATE, recRacer.getMotor3winingrate().floatValue());
		mapFloatList.addItem(KEY_WAKU, Float.valueOf(recRacer.getWaku()));
		mapFloatList.addItem(KEY_RANK, Float.valueOf(recRacer.getRank()));
		mapFloatList.addItem(KEY_POINT, Float.valueOf(BoatUtil.calculateMotorWiningPoint(recRacer.getRank())));
	}

	/**
	 * 基準日(baseYmd)から指定日数(daysBefore)までの範囲をlistYmdから計算してstartIdx, endIdxを設定する.
	 * @param baseYmd 基準日
	 * @param daysBefore 過去への範囲日数
	 */
	protected void setRangeIndex(String baseYmd, int daysBefore, String jyoCd) {
		// モータは一年周期で入れ替えられるのでモータ交換日に合わせて集計開始にする
		long replaceYmdLong = 0;
		int replaceMMdd = Integer.parseInt(mapReplaceMMdd.get(jyoCd));
		int baseYYYY = Integer.parseInt(baseYmd.substring(0, 4));
		int baseMmdd = Integer.parseInt(baseYmd.substring(4, 8));

		// モータ交換日が基準日より後ならモータ交換日の年度を前年にする
		if (replaceMMdd <= baseMmdd ) {
			replaceYmdLong = Long.parseLong(baseYmd);
		} else {
			replaceYmdLong = Long.parseLong(String.valueOf(baseYYYY - 1) + String.format("%04d", replaceMMdd));
		}
		
		DateTime toDateTime = DateTime.parse(baseYmd, formatterYmd).minusDays(1);
		DateTime fromDateTime = toDateTime.minusDays(daysBefore-1);
		long toYmdLong = Long.valueOf(formatterYmd.print(toDateTime));
		long fromYmdLong = Long.valueOf(formatterYmd.print(fromDateTime));
		
		// 集計開始日がモータ交換日より前ならモータ交換日を集計開始日とする
		if (fromYmdLong < replaceYmdLong) {
			fromYmdLong = replaceYmdLong;
		}
		
		//fromYmd～toYmdのリストインデックスを求める
		boolean flgEndYet = true;
		for (int i = listYmd.size()-1; i >= 0; i--) {
			if (listYmd.get(i) <= toYmdLong && flgEndYet) {
				endIdx = i;
				flgEndYet = false;
			}
			
			if (listYmd.get(i) < fromYmdLong) {
				startIdx = i+1;
				break;
			} 
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Float.valueOf("02"));
	}
}

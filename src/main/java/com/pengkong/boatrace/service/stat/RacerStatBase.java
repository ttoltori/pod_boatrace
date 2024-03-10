package com.pengkong.boatrace.service.stat;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.pengkong.boatrace.mybatis.entity.ex.RecRacerEx;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * 選手毎の統計情報
 * @author qwerty
 *
 */
public class RacerStatBase {
	// public static final String KEY_YMD = "ymd";
	public static final String KEY_JYOCD = "jyoCd";
	public static final String KEY_RACENO = "raceNo";
	public static final String KEY_RACETYPE = "raceType";
	public static final String KEY_EXHIBIT = "exhibit";
	public static final String KEY_STARTEXHIBIT = "startExhibit";
	public static final String KEY_AVGSTART = "avgStart";
	public static final String KEY_AVGTIME = "avgTime";
	public static final String KEY_NATION_WININGRATE = "nationWiningRate";
	public static final String KEY_NATION_2WININGRATE = "nation2WiningRate";
	public static final String KEY_NATION_3WININGRATE = "nation3WiningRate";
	public static final String KEY_LOCAL_WININGRATE = "localWiningRate";
	public static final String KEY_LOCAL_2WININGRATE = "local2WiningRate";
	public static final String KEY_LOCAL_3WININGRATE = "local3WiningRate";
	public static final String KEY_MOTOR_2WININGRATE = "motor2WiningRate";
	public static final String KEY_MOTOR_3WININGRATE = "motor3WiningRate";
	public static final String KEY_START = "start";
	public static final String KEY_WAKU = "waku";
	public static final String KEY_RANK = "rank";
	public static final String KEY_POINT = "point";
	
	public Short entry;
	public String sex;
	/** 所属支部の場コード */
	public String brabchJyocd;
	
	/** from rec_race. 勝率計算時必要 */
	//List<String> listRaceType = new ArrayList<>();
	
	/** 集計範囲の判定に必要 */
	List<Long> listYmd = new ArrayList<>();
	
	/** 処理を簡単にするためにすべての数値は格納値をFloatに変換して保存する */
	HashMapList<Float> mapFloatList = new HashMapList<>();

	/** 日付変換用フォーマッタ */
	DateTimeFormatter formatterYmd = DateTimeFormat.forPattern("yyyyMMdd");
	
	int startIdx = 0;
	int endIdx = 0;
	
	public RacerStatBase() {
	}

	public void addRecRacerEx(RecRacerEx recRacerEx){
		if (entry == null) {
			entry = recRacerEx.getEntry();
			sex = recRacerEx.getSex();
			brabchJyocd = JyoManager.getJyocdByBranch(recRacerEx.getBranch());
		}

		//listRaceType.add(recRacerEx.getRacetype());
		listYmd.add(Long.valueOf(recRacerEx.getYmd()));
		
		mapFloatList.addItem(KEY_JYOCD, Float.valueOf(recRacerEx.getJyocd()));
		mapFloatList.addItem(KEY_RACENO, Float.valueOf(recRacerEx.getRaceno()));
		mapFloatList.addItem(KEY_EXHIBIT, recRacerEx.getExhibit().floatValue());
		mapFloatList.addItem(KEY_STARTEXHIBIT, recRacerEx.getStartexhibit().floatValue());
		mapFloatList.addItem(KEY_AVGSTART, recRacerEx.getAveragestart().floatValue());
		mapFloatList.addItem(KEY_AVGTIME, recRacerEx.getAvgtime().floatValue());
		mapFloatList.addItem(KEY_NATION_WININGRATE, recRacerEx.getNationwiningrate().floatValue());
		mapFloatList.addItem(KEY_NATION_2WININGRATE, recRacerEx.getNation2winingrate().floatValue());
		mapFloatList.addItem(KEY_NATION_3WININGRATE, recRacerEx.getNation3winingrate().floatValue());
		mapFloatList.addItem(KEY_LOCAL_WININGRATE, recRacerEx.getLocalwiningrate().floatValue());
		mapFloatList.addItem(KEY_LOCAL_2WININGRATE, recRacerEx.getLocal2winingrate().floatValue());
		mapFloatList.addItem(KEY_LOCAL_3WININGRATE, recRacerEx.getLocal3winingrate().floatValue());
		mapFloatList.addItem(KEY_MOTOR_2WININGRATE, recRacerEx.getMotor2winingrate().floatValue());
		mapFloatList.addItem(KEY_MOTOR_3WININGRATE, recRacerEx.getMotor3winingrate().floatValue());
		mapFloatList.addItem(KEY_START, recRacerEx.getStartresult().floatValue());
		mapFloatList.addItem(KEY_WAKU, Float.valueOf(recRacerEx.getWaku()));
		mapFloatList.addItem(KEY_RANK, Float.valueOf(recRacerEx.getRank()));
		mapFloatList.addItem(KEY_POINT, Float.valueOf(BoatUtil.calculateRacerWiningPoint(recRacerEx.getRank(), recRacerEx.getRacetype())));
	}

	/**
	 * 基準日(baseYmd)から指定日数(daysBefore)までの範囲をlistYmdから計算してstartIdx, endIdxを設定する.
	 * @param baseYmd 基準日
	 * @param daysBefore 過去への範囲日数
	 */
	protected void setRangeIndex(String baseYmd, int daysBefore) {
		DateTime toDateTime = DateTime.parse(baseYmd, formatterYmd).minusDays(1);
		DateTime fromDateTime = toDateTime.minusDays(daysBefore-1);
		String strToDateTime = formatterYmd.print(toDateTime);
		String strFromDateTime = formatterYmd.print(fromDateTime);
		long toYmdLong = Long.valueOf(strToDateTime);
		long fromYmdLong = Long.valueOf(strFromDateTime);
		
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

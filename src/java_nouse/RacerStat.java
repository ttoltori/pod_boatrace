package com.pengkong.boatrace.service.stat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.boatrace.mybatis.entity.ex.RecRacerEx;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * 選手毎の統計情報
 * @author qwerty
 *
 */
public class RacerStat {

	
	public Short entry;
	public String sex;
	/** 所属支部の場コード */
	public String brabchJyocd;
	Integer currentYmd;
	List<Integer> listYmd = new ArrayList<>();
	List<String> listJyocd = new ArrayList<>();
	List<Short> listRaceNo = new ArrayList<>();
	/** from rec_race */
	List<String> listRaceType = new ArrayList<>();
	List<String> listLevel = new ArrayList<>();
	List<Float> listExhibit = new ArrayList<>();
	List<Float> listStartExhibit = new ArrayList<>();
	List<Float> listAvgStart = new ArrayList<>();
	List<Float> listAvgTime = new ArrayList<>();
	List<Float> listNationWiningRate = new ArrayList<>();
	List<Float> listNation2WiningRate = new ArrayList<>();
	List<Float> listNation3WiningRate = new ArrayList<>();
	List<Float> listLocalWiningRate = new ArrayList<>();
	List<Float> listLocal2WiningRate = new ArrayList<>();
	List<Float> listLocal3WiningRate = new ArrayList<>();
	List<Float> listMotor2WiningRate = new ArrayList<>();
	List<Float> listMotor3WiningRate = new ArrayList<>();
	List<Float> listStart = new ArrayList<>();
	List<Short> listWaku = new ArrayList<>();
	List<Short> listRank = new ArrayList<>();
	List<Integer> listPoint = new ArrayList<>();
	
	public static final String KEY_YMD = "ymd";
	public static final String KEY_JYOCD = "jyoCd";
	public static final String KEY_RACENO = "raceNo";
	public static final String KEY_RACETYPE = "raceType";
	public static final String KEY_LEVEL = "level";
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
	
	
	/** 処理を簡単にするためにすべての数値は格納値をFloatに変換して保存する */
	HashMapList<Float> mapFloatList = new HashMapList<>();
	/** 文字型データを格納する */
	HashMapList<String> mapStrList = new HashMapList<>();
	
	
	private Integer fromYmd = 0;
	private Integer toYmd = 99991231;
	
	private int itemCount = 0;
	
	public RacerStat() {
		fromYmd = 20100101;
	}

	public void setRange(String fromYmd, String toYmd) {
		this.fromYmd = Integer.parseInt(fromYmd);
		this.toYmd = Integer.parseInt(toYmd);
	}
	
	public void addRecRacerEx(RecRacerEx recRacerEx){
		if (entry == null) {
			entry = recRacerEx.getEntry();
			sex = recRacerEx.getSex();
			brabchJyocd = JyoManager.getJyocdByBranch(recRacerEx.getBranch());
		}
		mapFloatList.addItem("", value)
		
		
		listYmd.add(Integer.parseInt(recRacerEx.getYmd()));
		listJyocd.add(recRacerEx.getJyocd());
		listRaceNo.add(recRacerEx.getRaceno());
		listRaceType.add(recRacerEx.getRaceType());
		listLevel.add(recRacerEx.getLevel());
		listExhibit.add(recRacerEx.getExhibit().floatValue());
		listStartExhibit.add(recRacerEx.getStartexhibit().floatValue());
		listAvgStart.add(recRacerEx.getAveragestart().floatValue());
		listAvgTime.add(recRacerEx.getAvgtime().floatValue());
		listNationWiningRate.add(recRacerEx.getNationwiningrate().floatValue());
		listNation2WiningRate.add(recRacerEx.getNation2winingrate().floatValue());
		listNation3WiningRate.add(recRacerEx.getNation3winingrate().floatValue());
		listLocalWiningRate.add(recRacerEx.getLocalwiningrate().floatValue());
		listLocal2WiningRate.add(recRacerEx.getLocal2winingrate().floatValue());
		listLocal3WiningRate.add(recRacerEx.getLocal3winingrate().floatValue());
		listMotor2WiningRate.add(recRacerEx.getMotor2winingrate().floatValue());
		listMotor3WiningRate.add(recRacerEx.getMotor3winingrate().floatValue());
		listWaku.add(recRacerEx.getWaku());
		listRank.add(recRacerEx.getRank());
		listStart.add(recRacerEx.getStartresult().floatValue());
		listPoint.add(BoatUtil.calculateWiningPoint(recRacerEx.getRank(), recRacerEx.getRaceType()));
		itemCount++;
	}

	
	public WiningRate getRecentNationWiningRate
	
	/** 当該枠での選手勝率 ,２連帯率、3連帯率*/
	public WiningRate getStatRacerWakuWiningRate(String currentYmd, int days, Short waku) {
		float sum1 = 0;
		float sum2 = 0;
		float sum3 = 0;
		float count = 0;
		for (int i = 0; i < itemCount; i++) {
			if (waku == listWaku.get(i)) {
				sum1 += listPoint.get(i);
				if (listRank.get(i) <= 2) {
					sum2 ++;
				} 
				if (listRank.get(i) <= 3) {
					sum3 ++;
				}
				count++;
			}
		}
		
		return WiningRate.createInstance(sum1, sum2, sum3, count);
	}
}

package com.pengkong.boatrace.converter;

import java.util.List;

import com.pengkong.boatrace.model.comparator.WakuValueComparator;
import com.pengkong.boatrace.model.comparator.WakuValueReverseComparator;
import com.pengkong.boatrace.mybatis.entity.RecWakuRecent;
import com.pengkong.boatrace.mybatis.entity.StatRace;
import com.pengkong.boatrace.mybatis.entity.StatWakuRecent;
import com.pengkong.boatrace.mybatis.entity.StatWakuWin;
import com.pengkong.boatrace.service.stat.RaceStatDataSet;
import com.pengkong.boatrace.service.stat.WakuValue;

/**
 * rec_waku_recent, stat_waku_recent, stat_waku_winを変換してstat_raceを取得する
 * @author qwerty
 *
 */
public class RecentList2StatRace {

	public RecentList2StatRace() {
	}
	
	public static StatRace convert(RecWakuRecent recWakuRecent, StatWakuRecent statWakuRecent, StatWakuWin statWakuWin) {
		StatRace statRace = new StatRace();
		List<WakuValue> wakuValueList;
		WakuValueComparator wakuValueComparator = new WakuValueComparator();
		WakuValueReverseComparator wakuValueReverseComparator = new WakuValueReverseComparator();
		String delimiter = "";
		
		statRace.setYmd(statWakuWin.getYmd());
		statRace.setJyocd(statWakuWin.getJyocd());
		statRace.setRaceno(statWakuWin.getRaceno());
// 20190105 一時コメントアウト TODO		
//		// 直近全国勝率順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertNationWiningRate(recWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setRecentnationwiningrank(joinRank(wakuValueList, delimiter));
//		
//		// 直近全国2連率順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertNation2WiningRate(recWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setRecentnation2winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 直近全国3連率順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertNation3WiningRate(recWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setRecentnation3winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 直近当地勝率順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertLocalWiningRate(recWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setRecentlocalwiningrank(joinRank(wakuValueList, delimiter));
//		
//		// 直近当地2連率順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertLocal2WiningRate(recWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setRecentlocal2winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 直近当地3連率順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertLocal3WiningRate(recWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setRecentlocal3winingrank(joinRank(wakuValueList, delimiter));
//		
//		
//		// 直近平均ST順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertAvgSt(recWakuRecent);
//		wakuValueList.sort(wakuValueComparator);
//		statRace.setRecentavgstrank(joinRank(wakuValueList, delimiter));
//		
//		// 直近平均タイム順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertAvgTime(recWakuRecent);
//		wakuValueList.sort(wakuValueComparator);
//		statRace.setRecentavgtimerank(joinRank(wakuValueList, delimiter));
//		
//		// 直近モータ2連率順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertMotor2WiningRate(recWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setRecentmotor2winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 直近モータ3連率順位
//		wakuValueList = RecWakuRecent2WakuValueList.convertMotor3WiningRate(recWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setRecentmotor3winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 統計直近全国勝率順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertNationWiningRate(statWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setStatnationwiningrank(joinRank(wakuValueList, delimiter));
//		
//		// 統計直近全国2連率順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertNation2WiningRate(statWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setStatnation2winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 統計直近全国3連率順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertNation3WiningRate(statWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setStatnation3winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 統計直近当地勝率順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertLocalWiningRate(statWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setStatlocalwiningrank(joinRank(wakuValueList, delimiter));
//		
//		// 統計直近当地2連率順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertLocal2WiningRate(statWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setStatlocal2winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 統計直近当地3連率順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertLocal3WiningRate(statWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setStatlocal3winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 統計直近スタート順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertStart(statWakuRecent);
//		wakuValueList.sort(wakuValueComparator);
//		statRace.setStatstartrank(joinRank(wakuValueList, delimiter));
//		
//		
//		// 統計直近モータ勝率順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertMotorWiningRate(statWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setStatmotorwiningrank(joinRank(wakuValueList, delimiter));
//		
//		// 統計直近モータ2連率順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertMotor2WiningRate(statWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setStatmotor2winingrank(joinRank(wakuValueList, delimiter));
//		
//		// 統計直近モータ3連率順位
//		wakuValueList = StatWakuRecent2WakuValueList.convertMotor3WiningRate(statWakuRecent);
//		wakuValueList.sort(wakuValueReverseComparator);
//		statRace.setStatmotor3winingrank(joinRank(wakuValueList, delimiter));
		
		// 統計枠選手勝率順位
		wakuValueList = StatWakuWin2WakuValueList.convertRacerWiningRate(statWakuWin);
		wakuValueList.sort(wakuValueReverseComparator);
		statRace.setRacerwiningrank(joinRank(wakuValueList, delimiter));
		
		// 統計枠選手2連率順位
		wakuValueList = StatWakuWin2WakuValueList.convertRacer2WiningRate(statWakuWin);
		wakuValueList.sort(wakuValueReverseComparator);
		statRace.setRacer2winingrank(joinRank(wakuValueList, delimiter));
		
		// 統計枠選手3連率順位
		wakuValueList = StatWakuWin2WakuValueList.convertRacer3WiningRate(statWakuWin);
		wakuValueList.sort(wakuValueReverseComparator);
		statRace.setRacer3winingrank(joinRank(wakuValueList, delimiter));
		
		// 統計枠モータ勝率順位
		wakuValueList = StatWakuWin2WakuValueList.convertMotorWiningRate(statWakuWin);
		wakuValueList.sort(wakuValueReverseComparator);
		statRace.setMotorwiningrank(joinRank(wakuValueList, delimiter));
		
		// 統計枠モータ2連率順位
		wakuValueList = StatWakuWin2WakuValueList.convertMotor2WiningRate(statWakuWin);
		wakuValueList.sort(wakuValueReverseComparator);
		statRace.setMotor2winingrank(joinRank(wakuValueList, delimiter));
		
		// 統計枠モータ3連率順位
		wakuValueList = StatWakuWin2WakuValueList.convertMotor3WiningRate(statWakuWin);
		wakuValueList.sort(wakuValueReverseComparator);
		statRace.setMotor3winingrank(joinRank(wakuValueList, delimiter));
		
		return statRace;
	}
	
	private static String joinRank(List<WakuValue> wakuValueList, String delimiter) {
		String[] rankArr = new String[6];
		for (int i = 0; i < 6; i++) {
			rankArr[i] = wakuValueList.get(i).waku;
		}
		return String.join(delimiter, rankArr);
	}

}

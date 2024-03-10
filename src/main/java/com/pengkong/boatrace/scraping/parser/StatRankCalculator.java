package com.pengkong.boatrace.scraping.parser;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.model.comparator.WakuFloatComparator;
import com.pengkong.boatrace.model.comparator.WakuIntegerComparator;

/**
 * 20181125
 * DBにimportする時は勝利０⇒末端値、平均ST０⇒末端値にするため順位を再計算する。
 * @author qwerty
 *
 */
public class StatRankCalculator {
	/**
	 * WorkerBaseが呼び出す
	 * @param race
	 * @return
	 */
	public RaceEx calculateAndSetEx(RaceEx race) {
		List<Waku> wakuList = new ArrayList<>();
		String delimiter = "";
		
		// 平均ST順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatComparator("averageStart"));
		race.calculatedInfo.averageStartRank = joinRank(wakuList, delimiter);
		
		return race;
	}

	/**
	 * 節ランクデータだけ再計算する。(csvに保存されていないデータがあったため(2021/7/12) 
	 * @param race
	 * @return
	 */
	public RaceEx calculateAndSetEx2(RaceEx race) {
		List<Waku> wakuList = new ArrayList<>();
		String delimiter = "";
		
		// 節平均STコンディションランク　20181118
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatComparator("avgstCondRank"));
		race.raceInfo2.avgstCondRank = joinRank(wakuList, delimiter);
		
		// 節平均着順ランク 20181118
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuIntegerComparator("setuwinRank"));
		race.raceInfo2.setuwinRank = joinRank(wakuList, delimiter);
		
		// 節罰点ランク 20181118
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuIntegerComparator("flRank"));
		race.raceInfo2.flRank = joinRank(wakuList, delimiter);
		
		return race;
	}
	
	private String joinRank(List<Waku> wakuList, String delimiter) {
		String[] rankArr = new String[6];
		for (int i = 0; i < 6; i++) {
			rankArr[i] = String.valueOf(wakuList.get(i).no);
		}
		return String.join(delimiter, rankArr);
	}
}

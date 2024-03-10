package com.pengkong.boatrace.util.tmp;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.model.comparator.WakuFloatComparator;
import com.pengkong.boatrace.model.comparator.WakuIntegerComparator;

public class TmpRankCalculator5 {
	/**
	 * WorkerBaseが呼び出す
	 * @param race
	 * @return
	 */
	public RaceEx calculateAndSetEx(RaceEx race) {
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

package com.pengkong.boatrace.util.tmp;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.model.comparator.WakuFloatComparator;
import com.pengkong.boatrace.model.comparator.WakuFloatReverseComparator;

public class TmpRankCalculator4 {
	/**
	 * WorkerBaseが呼び出す
	 * @param race
	 * @return
	 */
	public RaceEx calculateAndSetEx(RaceEx race) {
		List<Waku> wakuList = new ArrayList<>();
		String delimiter = "";

		// 平均STの順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("averageStart"));
		race.calculatedInfo.averageStartRank = joinRank(wakuList, delimiter);

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

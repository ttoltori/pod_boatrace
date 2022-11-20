package com.pengkong.boatrace.util.tmp;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.model.comparator.WakuFloatComparator;
import com.pengkong.boatrace.model.comparator.WakuFloatReverseComparator;
import com.pengkong.boatrace.model.comparator.WakuIntegerComparator;

public class TmpRankCalculator6 {
	/**
	 * WorkerBaseが呼び出す
	 * @param race
	 * @return
	 */
	public RaceEx calculateAndSetEx(RaceEx race) {
		List<Waku> wakuList = new ArrayList<>();
		String delimiter = "";
		
		// 全国勝率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("nationWiningRate"));
		race.calculatedInfo.nationWiningRank = joinRank(wakuList, delimiter);
		// 全国２率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("nation2WiningRate"));
		race.calculatedInfo.nation2WiningRank = joinRank(wakuList, delimiter);
		// 全国3率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("nation3WiningRate"));
		race.calculatedInfo.nation3WiningRank = joinRank(wakuList, delimiter);
		// 当地勝率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("localWiningRate"));
		race.calculatedInfo.localWiningRank = joinRank(wakuList, delimiter);
		// 当地２率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("local2WiningRate"));
		race.calculatedInfo.local2WiningRank = joinRank(wakuList, delimiter);
		// 当地3率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("local3WiningRate"));
		race.calculatedInfo.local3WiningRank = joinRank(wakuList, delimiter);
		// モーター2率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("motor2WiningRate"));
		race.calculatedInfo.motor2Rank = joinRank(wakuList, delimiter);
		// モーター3率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("motor3WiningRate"));
		race.calculatedInfo.motor3Rank = joinRank(wakuList, delimiter);
		
		// 平均ST順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatComparator("averageStart"));
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

package com.pengkong.boatrace.util.tmp;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.model.comparator.WakuFloatComparator;
import com.pengkong.boatrace.model.comparator.WakuFloatReverseComparator;

public class TmpRankCalculator3 {
	/**
	 * WorkerBaseが呼び出す
	 * @param race
	 * @return
	 */
	public RaceEx calculateAndSetEx(RaceEx race) {
		List<Waku> wakuList = new ArrayList<>();
		String delimiter = "";
		String delimiter2 = "-";

		// 着順枠一覧
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort((Waku w1, Waku w2) -> w1.resulRank - w2.resulRank);
		race.calculatedInfo.wakuRank = joinRank(wakuList, delimiter);
		// 着順級一覧
		String[] resultLevelList = new String[6];
		for (int i = 0; i < 6; i++) {
			resultLevelList[i] = "X";
		}
		for (int i = 0; i < 6; i++) {
			// 事故発生した選手はランクが9になっているため
			if (wakuList.get(i).resulRank <= 6) {
				resultLevelList[wakuList.get(i).resulRank - 1] = wakuList.get(i).level;
			}
		}
		race.calculatedInfo.resultLevelRank = String.join(delimiter2, resultLevelList);
		
		// 級別枠一覧
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort((Waku w1, Waku w2) -> levelComparator(w1.level) - levelComparator(w2.level));
		race.calculatedInfo.levelRank = joinRank(wakuList, delimiter);

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
		// ボート2率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("boat2WiningRate"));
		race.calculatedInfo.boat2Rank = joinRank(wakuList, delimiter);
		// ボート3率順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("boat3WiningRate"));
		race.calculatedInfo.boat3Rank = joinRank(wakuList, delimiter);
		// スタート順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("startExhibit"));
		race.calculatedInfo.startExhibitRank = joinRank(wakuList, delimiter);
		// 展示順位
		wakuList.clear();
		wakuList.addAll(race.wakuList);
		wakuList.sort(new WakuFloatReverseComparator("exhibitTime"));
		race.calculatedInfo.exhibitRank = joinRank(wakuList, delimiter);

		return race;
	}

	private String joinRank(List<Waku> wakuList, String delimiter) {
		String[] rankArr = new String[6];
		for (int i = 0; i < 6; i++) {
			rankArr[i] = String.valueOf(wakuList.get(i).no);
		}
		return String.join(delimiter, rankArr);
	}

	private int levelComparator(String level) {
		if (level.equals("A1")) {
			return 1;
		} else if (level.equals("A2")) {
			return 2;
		} else if (level.equals("B1")) {
			return 3;
		} else {
			return 4;
		}
	}
}

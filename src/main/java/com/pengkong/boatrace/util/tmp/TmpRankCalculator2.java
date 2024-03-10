package com.pengkong.boatrace.util.tmp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.pengkong.boatrace.model.OddsTItem;
import com.pengkong.boatrace.model.OddsFItem;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.boatrace.service.manager.ResultOddsManager;

public class TmpRankCalculator2 {

	public RaceEx calculateAndSet(RaceEx race) {
//		// 20180415 Start
//		// 単勝オッズ人気
//		List<OddsTItem> oddsListTmp = OddsManager.getInstance().getOddsItemList(JyoManager.getJyoCd(race.setu.jyo),
//				String.valueOf(race.raceInfo.no), "1T");
//		List<OddsTItem> oddsList = new ArrayList<>();
//		oddsList.addAll(oddsListTmp);
//		
//		oddsList.sort(new Comparator<OddsTItem>() {
//			@Override
//			public int compare(OddsTItem o1, OddsTItem o2) {
//				return Float.compare(o1.value, o2.value);
//			}
//		});
//
//		for (int i = 0; i < oddsList.size(); i++) {
//			if (oddsList.get(i).kumiban.equals(race.raceResult.tansyoNo)) {
//				race.raceResult.tansyoPopular = i + 1;
//				break;
//			}
//		}
//
//		// 複勝オッズ人気
//		List<OddsFItem> oddsListTmp2 = OddsManager.getInstance().getOddsItemList1FKF(JyoManager.getJyoCd(race.setu.jyo),
//				String.valueOf(race.raceInfo.no), "1F");
//		List<OddsFItem> oddsList1F = new ArrayList<>();
//		oddsList1F.addAll(oddsListTmp2);
//		
//		oddsList1F.sort(new Comparator<OddsFItem>() {
//			@Override
//			public int compare(OddsFItem o1, OddsFItem o2) {
//				return Float.compare(o1.value[0], o2.value[0]);
//			}
//		});
//
//		boolean[] flgs = new boolean[2];
//		for (int i = 0; i < oddsList1F.size(); i++) {
//			if (flgs[0] && flgs[1]) {
//				break;
//			}
//			if (oddsList1F.get(i).kumiban.equals(race.raceResult.hukusyo1No)) {
//				race.raceResult.hukusyo1Popular = i + 1;
//				flgs[0] = true;
//				continue;
//			} else if (oddsList1F.get(i).kumiban.equals(race.raceResult.hukusyo2No)) {
//				race.raceResult.hukusyo2Popular = i + 1;
//				flgs[1] = true;
//				continue;
//			}
//		}
		// 20180415 End

		List<Waku> wakuListTmp = race.wakuList;
		List<Waku> wakuList = new ArrayList<>();
		wakuList.addAll(wakuListTmp);
		String delimiter = "";
//		String delimiter2 = "-";
//
//		// 枠順一覧
//		wakuList.sort((Waku w1, Waku w2) -> w1.resulRank - w2.resulRank);
//		race.calculatedInfo.wakuRank = joinRank(wakuList, delimiter);
//		// 級別枠一覧
//		wakuList.sort((Waku w1, Waku w2) -> levelComparator(w1.level) - levelComparator(w2.level));
//		race.calculatedInfo.levelRank = joinRank(wakuList, delimiter);
//
//		// 着順級一覧
//		String[] resultLevelList = new String[6];
//		for (int i = 0; i < 6; i++) {
//			resultLevelList[i] = "X";
//		}
//		for (int i = 0; i < 6; i++) {
//			// 事故発生した選手はランクが9になっているため
//			if (wakuList.get(i).resulRank <= 6) {
//				resultLevelList[wakuList.get(i).resulRank - 1] = wakuList.get(i).level;
//			}
//		}
//		race.calculatedInfo.resultLevelRank = String.join(delimiter2, resultLevelList);
//
//		// 全国勝率順位
		wakuList.sort(new WakuFloatComparator("nationWiningRate"));
		race.calculatedInfo.nationWiningRank = joinRank(wakuList, delimiter);
//		// 全国２率順位
//		wakuList.sort(new WakuFloatComparator("nation2WiningRate"));
//		race.calculatedInfo.nation2WiningRank = joinRank(wakuList, delimiter);
//		// 全国3率順位
//		wakuList.sort(new WakuFloatComparator("nation3WiningRate"));
//		race.calculatedInfo.nation3WiningRank = joinRank(wakuList, delimiter);
//		// 当地勝率順位
//		wakuList.sort(new WakuFloatComparator("localWiningRate"));
//		race.calculatedInfo.localWiningRank = joinRank(wakuList, delimiter);
//		// 当地２率順位
//		wakuList.sort(new WakuFloatComparator("local2WiningRate"));
//		race.calculatedInfo.local2WiningRank = joinRank(wakuList, delimiter);
//		// 当地3率順位
//		wakuList.sort(new WakuFloatComparator("local3WiningRate"));
//		race.calculatedInfo.local3WiningRank = joinRank(wakuList, delimiter);
//		// モーター2率順位
//		wakuList.sort(new WakuFloatComparator("motor2WiningRate"));
//		race.calculatedInfo.motor2Rank = joinRank(wakuList, delimiter);
//		// モーター3率順位
//		wakuList.sort(new WakuFloatComparator("motor3WiningRate"));
//		race.calculatedInfo.motor3Rank = joinRank(wakuList, delimiter);
//		// ボート2率順位
//		wakuList.sort(new WakuFloatComparator("boat2WiningRate"));
//		race.calculatedInfo.boat2Rank = joinRank(wakuList, delimiter);
//		// ボート3率順位
//		wakuList.sort(new WakuFloatComparator("boat3WiningRate"));
//		race.calculatedInfo.boat3Rank = joinRank(wakuList, delimiter);
//		// スタート順位
//		wakuList.sort(new WakuFloatComparator("startExhibit"));
//		race.calculatedInfo.startExhibitRank = joinRank(wakuList, delimiter);
//		// 展示順位
//		wakuList.sort(new WakuFloatComparator("exhibitTime"));
//		race.calculatedInfo.exhibitRank = joinRank(wakuList, delimiter);

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

	private class WakuFloatComparator implements Comparator<Waku> {

		private String varName;

		public WakuFloatComparator(String varName) {
			this.varName = varName;
		}

		@Override
		public int compare(Waku o1, Waku o2) {
			return Float.compare(getFloatValue(o1, varName), getFloatValue(o2, varName));
		}

		private float getFloatValue(Waku waku, String varName) {
			if (varName.equals("nationWiningRate")) {
				return waku.nationWiningRate;
			} else if (varName.equals("nation2WiningRate")) {
				return waku.nation2WiningRate;
			} else if (varName.equals("nation3WiningRate")) {
				return waku.nation3WiningRate;
			} else if (varName.equals("localWiningRate")) {
				return waku.localWiningRate;
			} else if (varName.equals("local2WiningRate")) {
				return waku.local2WiningRate;
			} else if (varName.equals("local3WiningRate")) {
				return waku.local3WiningRate;
			} else if (varName.equals("motor2WiningRate")) {
				return waku.motor2WiningRate;
			} else if (varName.equals("motor3WiningRate")) {
				return waku.motor3WiningRate;
			} else if (varName.equals("boat2WiningRate")) {
				return waku.boat2WiningRate;
			} else if (varName.equals("boat3WiningRate")) {
				return waku.boat3WiningRate;
			} else if (varName.equals("startExhibit")) {
				return waku.startExhibit;
			} else if (varName.equals("exhibitTime")) {
				return waku.exhibitTime;
			} else {
				return 0;
			}
		}
	}

}

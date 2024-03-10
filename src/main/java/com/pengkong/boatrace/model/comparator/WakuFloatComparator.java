package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.model.Waku;

public class WakuFloatComparator implements Comparator<Waku> {

	protected String varName;

	public WakuFloatComparator(String varName) {
		this.varName = varName;
	}

	@Override
	public int compare(Waku o1, Waku o2) {
		return Float.compare(getFloatValue(o1, varName), getFloatValue(o2, varName));
	}

	protected float getFloatValue(Waku waku, String varName) {
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
		} else if (varName.equals("basePoint")) {
			return waku.basePoint;
		} else if (varName.equals("averageStart")) {
			return waku.averageStart;
		} else if (varName.equals("avgstCondRank")) { // 20181118 平均STコンディション
			return Waku.getSetuAverageStart(waku) - waku.averageStart ;
		} else {
			return 0;
		}
	}
}

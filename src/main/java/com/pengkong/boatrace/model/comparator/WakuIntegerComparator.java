package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.model.Waku;

public class WakuIntegerComparator implements Comparator<Waku> {

	protected String varName;

	public WakuIntegerComparator(String varName) {
		this.varName = varName;
	}

	@Override
	public int compare(Waku o1, Waku o2) {
		return getIntValue(o1, varName) - getIntValue(o2, varName);
	}

	protected int getIntValue(Waku waku, String varName) {
		if (varName.equals("setuwinRank")) {
			return Waku.getSetuAvgWin(waku);
		} else if (varName.equals("flRank")) {
			return waku.flying + waku.late;
		} else {
			return 0;
		}
	}
}

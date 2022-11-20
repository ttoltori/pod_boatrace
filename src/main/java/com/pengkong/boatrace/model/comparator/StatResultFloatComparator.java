package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.model.StatResult;

public class StatResultFloatComparator implements Comparator<StatResult> {

	private String varName;

	public StatResultFloatComparator(String varName) {
		this.varName = varName;
	}

	@Override
	public int compare(StatResult o1, StatResult o2) {
		return Float.compare(getFloatValue(o1, varName), getFloatValue(o2, varName));
	}

	private float getFloatValue(StatResult rec, String varName) {
		if (varName.equals("hitRate")) {
			return rec.hitRate;
		} else if (varName.equals("incomeRate")) {
			return rec.incomeRate;
		} else if (varName.equals("prize")) {
			return (float)rec.prize;
		} else if (varName.equals("avgincomeRate")) {
			return rec.avgincomeRate;
		} else {
			return 0;
		}
	}
}

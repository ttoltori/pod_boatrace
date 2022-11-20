package com.pengkong.boatrace.model.comparator;

import com.pengkong.boatrace.model.Waku;

public class WakuFloatReverseComparator extends WakuFloatComparator {

	public WakuFloatReverseComparator(String varName) {
		super(varName);
	}

	@Override
	public int compare(Waku o1, Waku o2) {
		return Float.compare(getFloatValue(o2, varName), getFloatValue(o1, varName));
	}
}

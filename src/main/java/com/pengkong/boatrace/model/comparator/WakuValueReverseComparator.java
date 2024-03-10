package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.service.stat.WakuValue;

public class WakuValueReverseComparator implements Comparator<WakuValue> {

	public WakuValueReverseComparator() {
	}

	@Override
	public int compare(WakuValue o1, WakuValue o2) {
		return o2.value.compareTo(o1.value);
	}
	
}

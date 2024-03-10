package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.service.stat.WakuValue;

public class WakuValueComparator implements Comparator<WakuValue> {

	public WakuValueComparator() {
	}

	@Override
	public int compare(WakuValue o1, WakuValue o2) {
		return o1.value.compareTo(o2.value);
	}
	
}

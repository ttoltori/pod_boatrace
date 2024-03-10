package com.pengkong.boatrace.exp10.odds.loader;

import java.util.Comparator;

import com.pengkong.boatrace.exp10.odds.Odds;

public class OddsValueComparator implements Comparator<Odds> {
	@Override
	public int compare(Odds o1, Odds o2) {
		return o1.value.compareTo(o2.value);
	}
}

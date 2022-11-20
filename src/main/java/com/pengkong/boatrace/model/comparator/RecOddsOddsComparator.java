package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.mybatis.entity.RecOdds;

public class RecOddsOddsComparator implements Comparator<RecOdds> {

	@Override
	public int compare(RecOdds o1, RecOdds o2) {
		return o1.getOdds().compareTo(o2.getOdds());
	}
}

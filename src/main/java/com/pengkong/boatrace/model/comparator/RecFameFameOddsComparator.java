package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.mybatis.entity.RecFame;

public class RecFameFameOddsComparator implements Comparator<RecFame> {

	@Override
	public int compare(RecFame o1, RecFame o2) {
		return o1.getFameodds().compareTo(o2.getFameodds());
	}
}

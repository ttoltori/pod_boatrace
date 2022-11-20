package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.model.OddsTItem;
/**
 * 1T,2T,3T用
 * @author qwerty
 *
 */
public class OddsTItemValueComparator implements Comparator<OddsTItem> {

	@Override
	public int compare(OddsTItem o1, OddsTItem o2) {
		return Float.compare(o1.value, o2.value);
	}
}

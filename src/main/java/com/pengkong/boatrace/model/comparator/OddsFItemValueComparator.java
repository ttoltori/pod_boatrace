package com.pengkong.boatrace.model.comparator;

import java.util.Comparator;

import com.pengkong.boatrace.model.OddsFItem;
/**
 * 1F,KF用
 * @author qwerty
 *
 */
public class OddsFItemValueComparator implements Comparator<OddsFItem> {

	@Override
	public int compare(OddsFItem o1, OddsFItem o2) {
		return Float.compare(o1.value[0], o2.value[0]);
	}
}

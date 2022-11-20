package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.Collection;

public class LRtrimRangeFinder extends LtrimRangeFinder {
	public LRtrimRangeFinder() {
		super();
	}
	public LRtrimRangeFinder(Collection<RangeStatUnit> values) {
		super(new ArrayList<>(values));
	}

	@Override
	void parse() {
		super.parse();
		
		int size = listUnit.size();

		// 左⇒右で最初の赤字で区切る
		for (int x2 = startIdx; x2 < size; x2++) {
		    RangeStatUnit unit = listUnit.get(x2);
		    if (unit.getIncome() <= 0) {
		        bestRangeSumUnit = new RangeSumUnit(startIdx, x2-1);
		        break;
		    }
		}
	}
}

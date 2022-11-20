package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.Collection;

public class SelectiveLRtrimRangeFinder extends LtrimRangeFinder {
	public SelectiveLRtrimRangeFinder() {
		super();
	}
	public SelectiveLRtrimRangeFinder(Collection<RangeStatUnit> values) {
		super(new ArrayList<>(values));
	}

	@Override
	void parse() {
		super.parse();
		
		int size = listUnit.size();
		int ltrimMaxIdx = this.bestRangeSumUnit.x2;
		
		// 最初赤字のindex取得
		int minusStartIdx = size;
        for (int x2 = startIdx+1; x2 <= ltrimMaxIdx; x2++) {
            RangeStatUnit unit = listUnit.get(x2);
            if (unit.getIncome() <= 0) {
                minusStartIdx = x2;
                break;
            }
        }

		int minusIdxCount = 0;
        int plusIdxCount = 0;
        for (int x2 = minusStartIdx; x2 <= ltrimMaxIdx; x2++) {
		    RangeStatUnit unit = listUnit.get(x2);
		    if (unit.getIncome() <= 0) {
		        minusIdxCount++;
		    } else {
		        plusIdxCount++;
		    }
		}

        if (plusIdxCount > minusIdxCount) {
        	return;
        }
        

        if (minusStartIdx != size) {
        	bestRangeSumUnit = new RangeSumUnit(startIdx, minusStartIdx - 1);
        }
	}
}

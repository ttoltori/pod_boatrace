package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.Collection;

public class LtrimRangeFinder extends LeftRangeFinder {
	public LtrimRangeFinder() {
		super();
	}

	public LtrimRangeFinder(Collection<RangeStatUnit> values) {
		super(new ArrayList<>(values));
	}

	@Override
	void parse() {
		int size = listUnit.size();
		
		// 左から開始indexを最初の黒字に合わせる
		startIdx = size;
        for (int x1 = 0; x1 < size; x1++) {
            if (listUnit.get(x1).getIncome() > 0) {
                startIdx = x1;
                break;
            }
        }
        
        super.parse();
	}
}

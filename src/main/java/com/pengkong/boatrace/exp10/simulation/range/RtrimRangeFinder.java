package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.Collection;

public class RtrimRangeFinder extends RightRangeFinder {
	public RtrimRangeFinder() {
		super();
	}

	public RtrimRangeFinder(Collection<RangeStatUnit> values) {
		super(new ArrayList<>(values));
	}

	@Override
	void parse() {
		int size = listUnit.size();
		
		// 右から終了indexを最初の黒字に合わせる
		endIdx = size - 1;
        for (int x1 = endIdx; x1 >= 0; x1--) {
            if (listUnit.get(x1).getIncome() > 0) {
                endIdx = x1;
                break;
            }
        }
        
        super.parse();
	}
}

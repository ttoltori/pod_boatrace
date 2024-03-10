package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SelectiveRangeFinder extends AbstractRangeFinder {
	
	List<RangeStatUnit> listBestUnits;
	
	public SelectiveRangeFinder() {
		super();
		listBestUnits = new ArrayList<>();
	}

	public SelectiveRangeFinder(Collection<RangeStatUnit> values) {
		super(new ArrayList<>(values));
		listBestUnits = new ArrayList<>();
	}

	void parse() {
		int size = listUnit.size();
		
		bestRangeSumUnit = new RangeSumUnit(-1, -1);
        for (int x1 = 0; x1 < size; x1++) {
            if (listUnit.get(x1).getIncome() > 0) {
                listBestUnits.add(listUnit.get(x1));
            }
        }
	}
	
	@Override
	public void setListUnit(List<RangeStatUnit> listUnit) {
		super.setListUnit(listUnit);
		listBestUnits = new ArrayList<>();
	}
	
	/** BEST rangeに含まれるRangeStatUnitのリストを取得する */
	@Override
	public List<RangeStatUnit> findBestRangeStatUnits() {
		ensureParsed();
		
		return listBestUnits;
	}
	
}

package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

class LtrimRangeFinderTest {
	List<RangeStatUnit> units;


	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
		units  = new ArrayList<>();
	}
	
	void test_Ltrim() {
		LtrimRangeFinder target = new LtrimRangeFinder(); 
		units.add(create(0, 1000, 500, 1, 1) );
		units.add(create(1, 1000, 1400, 1, 1) );
		units.add(create(2, 1000, 500, 1, 1) );
		units.add(create(3, 1000, 1100, 1, 1) );
		units.add(create(4, 1000, 500, 1, 1) );
		units.add(create(5, 1000, 2900, 1, 1) );
		
		target.setListUnit(units);
		
		List<RangeStatUnit> results = target.findBestRangeStatUnits();
		System.out.println(results);
		
	}

	void test_LRtrim() {
		LRtrimRangeFinder target = new LRtrimRangeFinder();
		units.add(create(0, 1000, 500, 1, 1) );
		units.add(create(1, 1000, 1400, 1, 1) );
		units.add(create(2, 1000, 1500, 1, 1) );
		units.add(create(3, 1000, 1100, 1, 1) );
		units.add(create(4, 1000, 500, 1, 1) );
		units.add(create(5, 1000, 2900, 1, 1) );
		
		target.setListUnit(units);
		
		List<RangeStatUnit> results = target.findBestRangeStatUnits();
		System.out.println(results);
		}
	
	void test_SelectiveLRtrim() {
		SelectiveLRtrimRangeFinder target = new SelectiveLRtrimRangeFinder();
		units.add(create(0, 1000, 500, 1, 1) );
		units.add(create(1, 1000, 1400, 1, 1) );
		units.add(create(2, 1000, 500, 1, 1) );
		units.add(create(3, 1000, 2900, 1, 1) );
		units.add(create(4, 1000, 1100, 1, 1) );
		units.add(create(5, 1000, 500, 1, 1) );
		units.add(create(6, 1000, 900, 1, 1) );
		
		target.setListUnit(units);
		
		List<RangeStatUnit> results = target.findBestRangeStatUnits();
		System.out.println(results);
		
	}

	void test_Selective() {
		SelectiveRangeFinder target = new SelectiveRangeFinder();
		units.add(create(0, 1000, 500, 1, 1) );
		units.add(create(1, 1000, 1400, 1, 1) );
		units.add(create(2, 1000, 500, 1, 1) );
		units.add(create(3, 1000, 2900, 1, 1) );
		units.add(create(4, 1000, 1100, 1, 1) );
		units.add(create(5, 1000, 500, 1, 1) );
		units.add(create(6, 1000, 900, 1, 1) );
		
		target.setListUnit(units);
		
		List<RangeStatUnit> results = target.findBestRangeStatUnits();
		System.out.println(results);
		
	}

	private RangeStatUnit create(double factor, int betAmt, int hitAmt, double bOddsMin, double bOddsMax) {
		RangeStatUnit unit = new RangeStatUnit(Double.valueOf(factor));
		
		unit.betCnt = betAmt / 100;
		unit.betAmt = betAmt;
		unit.hitAmt = hitAmt;
		unit.bOddsMin = bOddsMin;
		unit.bOddsMax = bOddsMax;
		
		return unit;
	}
}

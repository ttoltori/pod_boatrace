package com.pengkong.boatrace.exp10.simulation.range;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class XYRangeFinderTest {
	Map<String, RangeStatUnit> mapXYStatUnit = new TreeMap<>();
	List<RangeStatUnit> xlist = new ArrayList<>();
	List<RangeStatUnit> ylist = new ArrayList<>();

	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
		mapXYStatUnit.put("1.2_0.4", createStatUnit("1.2_0.4", 2));
		mapXYStatUnit.put("1.2_0.7", createStatUnit("1.2_0.7", 3));
		mapXYStatUnit.put("1.3_0.6", createStatUnit("1.3_0.6", 3));
		mapXYStatUnit.put("1.4_0.3", createStatUnit("1.4_0.3", 4));
		mapXYStatUnit.put("1.4_0.5", createStatUnit("1.4_0.5", -1));
		mapXYStatUnit.put("1.5_0.6", createStatUnit("1.5_0.6", -2));
		mapXYStatUnit.put("1.6_0.2", createStatUnit("1.6_0.2", -3));
		mapXYStatUnit.put("1.6_0.6", createStatUnit("1.6_0.6", 1));
		mapXYStatUnit.put("1.7_0.3", createStatUnit("1.7_0.3", 4));
		mapXYStatUnit.put("1.7_0.4", createStatUnit("1.7_0.4", -3));
		mapXYStatUnit.put("1.7_0.7", createStatUnit("1.7_0.7", 1));
		mapXYStatUnit.put("1.8_0.8", createStatUnit("1.8_0.8", -9));
		mapXYStatUnit.put("1.8_0.9", createStatUnit("1.8_0.9", 7));
		mapXYStatUnit.put("1.9_0.5", createStatUnit("1.9_0.5", 3));
		
		xlist.add(createStatUnit(1.2, 5));
		xlist.add(createStatUnit(1.3, 3));
		xlist.add(createStatUnit(1.4, 3));
		xlist.add(createStatUnit(1.5, -2));
		xlist.add(createStatUnit(1.6, -2));
		xlist.add(createStatUnit(1.7, 2));
		xlist.add(createStatUnit(1.8, -2));
		xlist.add(createStatUnit(1.9, 3));
		
		ylist.add(createStatUnit(0.2, -3));
		ylist.add(createStatUnit(0.3, 8));
		ylist.add(createStatUnit(0.4, -1));
		ylist.add(createStatUnit(0.5, 2));
		ylist.add(createStatUnit(0.6, 2));
		ylist.add(createStatUnit(0.7, 4));
		ylist.add(createStatUnit(0.8, -9));
		ylist.add(createStatUnit(0.9, 7));
	}
	
	@Test
	void test_findBestRangeMinMaxXY() {
		XYRangeFinder target = new XYRangeFinder(mapXYStatUnit, xlist, ylist);
		Double[] values = target.findBestRangeMinMax();
		
		assertEquals(1.2, values[0]);
		assertEquals(1.9, values[1]);
		assertEquals(0.3, values[2]);
		assertEquals(0.7, values[3]);
	}

	@Test
	void test_findBestRangeMinMaxX() {
		BestRangeFinder target = new BestRangeFinder(xlist);
		Double[] values = target.findBestRangeMinMax();
		
		assertEquals(1.2, values[0]);
		assertEquals(1.4, values[1]);
	}
	
	@Test
	void test_findBestRangeMinMaxY() {
		BestRangeFinder target = new BestRangeFinder(ylist);
		Double[] values = target.findBestRangeMinMax();
		
		assertEquals(0.3, values[0]);
		assertEquals(0.7, values[1]);
	}
	
	private RangeStatUnit createStatUnit(String factor, int income) {
		RangeStatUnit unit = new RangeStatUnit(factor);
		
		unit.betCnt = 2;
		unit.betAmt = 2;
		unit.hitCnt = 1;
		unit.hitAmt = 2 + income;
		
		return unit;
	}

	private RangeStatUnit createStatUnit(Double factor, int income) {
		RangeStatUnit unit = new RangeStatUnit(factor);
		
		unit.betCnt = 2;
		unit.betAmt = 2;
		unit.hitCnt = 1;
		unit.hitAmt = 2 + income;
		
		return unit;
	}
}

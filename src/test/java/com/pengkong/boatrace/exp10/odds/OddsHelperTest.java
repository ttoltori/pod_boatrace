package com.pengkong.boatrace.exp10.odds;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengkong.boatrace.exp10.odds.loader.OddsValueComparator;
import com.pengkong.common.collection.HashMapList;

public class OddsHelperTest {
	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
	}
	

	@Test
	void test_applyRanking() {
		HashMapList<Odds> map = new HashMapList<>();
		map.addItem("1", createOdds("3T", "123", 4.0));
		map.addItem("1", createOdds("3T", "124", 1.0));
		map.addItem("1", createOdds("3T", "125", 6.0));
		map.addItem("1", createOdds("3T", "126", 0.0));
		map.addItem("1", createOdds("3T", "132", 9.0));
		map.addItem("1", createOdds("3T", "134", 2.0));
		map.addItem("1", createOdds("3T", "135", 2.0));
		map.addItem("1", createOdds("3T", "136", 0.0));
		
		HashMapList<Odds> result = OddsHelper.applyRanking(map, new OddsValueComparator());
		List<Odds> list = result.getAllItems();
		assertEquals(1, list.get(0).value);
		assertEquals("124", list.get(0).kumiban);
		
		assertEquals(4, list.get(3).value);
		assertEquals("123", list.get(3).kumiban);
	}
	
	Odds createOdds(String bettype, String kumiban, Double value) {
		return new Odds("ymd", "jyocd", "race", bettype, kumiban, value);
	}

}

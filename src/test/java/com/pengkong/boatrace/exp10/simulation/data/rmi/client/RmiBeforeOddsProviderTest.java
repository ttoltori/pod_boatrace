package com.pengkong.boatrace.exp10.simulation.data.rmi.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class RmiBeforeOddsProviderTest {
	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
	}
	

	@Test
	void test_getDaily() throws Exception {
		MLPropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
		
		RmiBeforeOddsProvider target = new RmiBeforeOddsProvider();
		Odds odds;
		odds = target.get("20210702", "21", "1", "3T", "123");
		assertEquals(8.5, odds.value);
		
		odds = target.get("20210703", "18", "1", "3T", "123");
		assertEquals(16.4, odds.value);
		
		odds = target.get("20210702", "21", "1", "3T", "123");
		assertEquals(8.5, odds.value);
	}
	
}

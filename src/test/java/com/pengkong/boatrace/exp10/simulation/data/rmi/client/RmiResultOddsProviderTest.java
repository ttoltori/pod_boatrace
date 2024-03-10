package com.pengkong.boatrace.exp10.simulation.data.rmi.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class RmiResultOddsProviderTest {
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
		
		RmiResultOddsProvider target = new RmiResultOddsProvider();
		Odds odds;
		odds = target.get("20100123", "04", "1", "3T", "123");
		assertEquals(97.3, odds.value);
		
		odds = target.get("20100124", "03", "1", "3T", "123");
		assertEquals(26.0, odds.value);
		
		odds = target.get("20100123", "04", "1", "3T", "123");
		assertEquals(97.3, odds.value);
	}
}

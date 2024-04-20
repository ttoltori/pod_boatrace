package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.calculator.probability.AbstractProbabilityCalculator;
import com.pengkong.boatrace.exp10.simulation.calculator.probability.ProbabilityCaculator1234;
import com.pengkong.boatrace.exp10.simulation.calculator.probability.ProbabilityCalculatorFactory;
import com.pengkong.boatrace.server.db.dto.DBRecord;

class ProbabilityCalculatorFactoryTest {

	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
	}
	
	@Test
	void test_create() {
		MLPropertyUtil.getInstance().putProperty("probability_type", "default");
		AbstractProbabilityCalculator pc = ProbabilityCalculatorFactory.create();
		assertTrue(pc instanceof ProbabilityCaculator1234);
		
		DBRecord rec = new DBRecord();
		rec.put("probability1", 1.0);
		rec.put("probability2", 2.0);
		rec.put("probability3", 3.0);

		Double result = pc.calculate("3T", rec);
		assertEquals(6, result);

		result = pc.calculate("2T", rec);
		assertEquals(2, result);

		result = pc.calculate("1T", rec);
		assertEquals(1, result);

		result = pc.calculate("3F", rec);
		assertEquals(6, result);

		result = pc.calculate("2F", rec);
		assertEquals(3, result);
	}
}

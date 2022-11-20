package com.pengkong.boatrace.exp10.simulation.range.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;

class RangeValidationProviderTest {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	RangeValidationProvider target;
	
	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
	}
	
	@Test
	void test_Pr() {
//		target = new RangeValidationProvider();
//		
//		List<Evaluation> results;
//		List<Evaluation> evals = new ArrayList<>();
//		evals.add(create(null, null, null, null, null, null, 1.0, 10.0));
//		
//		results = target.apply(null, 5.0, evals);
//		assertEquals(1, results.size());
//		
//		results = target.apply(null, 11.0, evals);
//		assertEquals(0, results.size());
	}
	
	@Test
	void test_Bor() {
		target = new RangeValidationProvider();
		
//		List<Evaluation> results;
//		List<Evaluation> evals = new ArrayList<>();
//		evals.add(create(null, null, null, null, 1.0, 10.0, null, null));
//		
//		results = target.apply(5.0, null, evals);
//		assertEquals(1, results.size());
//		
//		results = target.apply(11.0, null, evals);
//		assertEquals(0, results.size());
	}
	
	@Test
	void test_alland() {
//		target = new RangeValidationProvider("xy&&bor&&pr");
//		List<Evaluation> results;
//		List<Evaluation> evals = new ArrayList<>();
//		evals.add(create(1.0, 10.0, 5.0, 15.0, 2.0, 4.0, 3.0, 6.0));
//		
//		results = target.apply(3.0, 6.0, evals);
//		assertEquals(1, results.size());
//		
//		evals.add(create(4.0, 10.0, 5.0, 15.0, 2.0, 4.0, 3.0, 6.0));
//		results = target.apply(3.5, 6.0, evals);
//		assertEquals(1, results.size());
//
//		evals.add(create(4.0, 10.0, 5.0, 15.0, 2.0, 5.0, 3.0, 6.0));
//		results = target.apply(5.0, 6.0, evals);
//		assertEquals(1, results.size());
	}
	
	@Test
	void test_composite() {
//		target = new RangeValidationProvider("xy||(bor&&pr)");
//		List<Evaluation> results;
//		List<Evaluation> evals = new ArrayList<>();
//		evals.add(create(1.0, 10.0, 5.0, 15.0, 2.0, 4.0, 3.0, 6.0));
//		
//		results = target.apply(3.0, 100.0, evals);
//		assertEquals(0, results.size());
//
//		results = target.apply(6.0, 15.0, evals);
//		assertEquals(1, results.size());
//
//		results = target.apply(3.0, 4.0, evals);
//		assertEquals(1, results.size());
	}
	
	Evaluation create(Double...values) {
		Evaluation eval = new Evaluation();
		eval.put("xy_bestminx", values[0] == null ? null : values[0].toString());
		eval.put("xy_bestmaxx", values[1] == null ? null : values[1].toString());
		eval.put("xy_bestminy", values[2] == null ? null : values[2].toString());
		eval.put("xy_bestmaxy", values[3] == null ? null : values[3].toString());
		eval.put("bor_bestmin", values[4] == null ? null : values[4].toString());
		eval.put("bor_bestmax", values[5] == null ? null : values[5].toString());
		eval.put("pr_bestmin", values[6] == null ? null : values[6].toString());
		eval.put("pr_bestmax", values[7] == null ? null : values[7].toString());
		
		return eval;
	}
}

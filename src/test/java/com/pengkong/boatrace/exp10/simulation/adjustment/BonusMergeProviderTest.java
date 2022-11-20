package com.pengkong.boatrace.exp10.simulation.adjustment;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.MlResult;

class BonusMergeProviderTest {

	BonusMergeProvider target = new BonusMergeProvider();
	
	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
	}
	
	
	@Test
	void test_apply_andprob() {
		List<MlResult> listProb = new ArrayList<>();
		List<MlResult> listOdds = new ArrayList<>();
		
		MLPropertyUtil.getInstance().putProperty("merge_type", "and_prob");
		listProb.add(create("1T", "1", "prob"));
		listOdds.add(create("1T", "1", "odds"));
		List<MlResult> results = target.apply(listProb, listOdds);
		
		assertEquals(1, results.size());
		assertEquals("prob", results.get(0).getCustom());

		listProb.clear(); listOdds.clear();
		listProb.add(create("1T", "1", "prob"));
		results = target.apply(listProb, listOdds);
		
		assertEquals(0, results.size());

		listProb.clear(); listOdds.clear();
		listOdds.add(create("1T", "1", "odds"));
		results = target.apply(listProb, listOdds);
		
		assertEquals(0, results.size());
	}
	
	@Test
	void test_apply_andodds() {
		List<MlResult> listProb = new ArrayList<>();
		List<MlResult> listOdds = new ArrayList<>();
		
		MLPropertyUtil.getInstance().putProperty("merge_type", "and_odds");
		listProb.add(create("1T", "1", "prob"));
		listOdds.add(create("1T", "1", "odds"));
		List<MlResult> results = target.apply(listProb, listOdds);
		
		assertEquals(1, results.size());
		assertEquals("odds", results.get(0).getCustom());

		listProb.clear(); listOdds.clear();
		listProb.add(create("1T", "1", "prob"));
		results = target.apply(listProb, listOdds);
		
		assertEquals(0, results.size());

		listProb.clear(); listOdds.clear();
		listOdds.add(create("1T", "1", "odds"));
		results = target.apply(listProb, listOdds);
		
		assertEquals(0, results.size());
	}

	@Test
	void test_apply_x() {
		List<MlResult> listProb = new ArrayList<>();
		List<MlResult> listOdds = new ArrayList<>();
		
		MLPropertyUtil.getInstance().putProperty("merge_type", "x");
		listProb.add(create("1T", "1", "prob"));
		listOdds.add(create("1T", "1", "odds"));
		List<MlResult> results = target.apply(listProb, listOdds);
		
		assertEquals(1, results.size());
		assertEquals("prob", results.get(0).getCustom());

		listProb.clear(); listOdds.clear();
		listOdds.add(create("1T", "1", "odds"));
		results = target.apply(listProb, listOdds);
		
		assertEquals(1, results.size());
		assertEquals("odds", results.get(0).getCustom());
	}

	@Test
	void test_apply_error() {
		List<MlResult> listProb = new ArrayList<>();
		List<MlResult> listOdds = new ArrayList<>();
		
		MLPropertyUtil.getInstance().putProperty("merge_type", "x");
		listProb.add(create("1T", "1", "prob"));
		listOdds.add(create("1T", "1", "odds"));
		listOdds.add(create("1T", "1", "odds2"));
		try {
			target.apply(listProb, listOdds);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
		
	}
	
	MlResult create(String betType, String kumiban, String custom) {
		MlResult result = new MlResult();
		result.setBettype(betType);
		result.setBetKumiban(kumiban);
		result.setCustom(custom);
		
		return result;
	}
}

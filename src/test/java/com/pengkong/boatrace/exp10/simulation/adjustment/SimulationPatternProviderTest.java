package com.pengkong.boatrace.exp10.simulation.adjustment;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengkong.boatrace.TestUtil;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.SimulationInfo;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.common.collection.HashMapList;

class SimulationPatternProviderTest {

	SimulationPatternProvider target;
	
	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
	}
	
	
	@Test
	void test_initialize() {
		MLPropertyUtil.getInstance().putProperty("pattern_id", "nopattern");
		target = new SimulationPatternProvider();
		target.ensureInitialized();
		
		MLPropertyUtil.getInstance().putProperty("pattern_id", "sim+ptncnt");
		target = new SimulationPatternProvider();
		target.ensureInitialized();

	}
/*	
	@Test
	void test_apply_ptncnt() throws Exception {
		List<MlResult> results;
		
		target = new SimulationPatternProvider();
		MLPropertyUtil.getInstance().putProperty("pattern_id", "sim+ptncnt");
		
		Evaluation eval = new Evaluation();
		HashMapList<Evaluation> mapList = new HashMapList<>();
		mapList.addItem("1", eval);
		
		SimulationInfo si = new SimulationInfo("20220228", "01", "1", "1T", "1", mapList, "all", "all", "all", "all");
		List<MlResult> list = TestUtil.createResultList(1);

		si.patterns = Arrays.asList("B1");
		results =  target.apply(list, si);

		// ptncnt = 1
		assertEquals(1, results.size());
		assertEquals("1", results.get(0).getPattern());
		
		// ptncnt = 2
		si.patterns = Arrays.asList("B1", "A2");
		results =  target.apply(list, si);
		assertEquals(2, results.size());
		assertEquals("1", results.get(0).getPattern());
		assertEquals("2", results.get(1).getPattern());
	}

	@Test
	void test_apply_ptncnt2() throws Exception {
		List<MlResult> results;
		
		target = new SimulationPatternProvider();
		MLPropertyUtil.getInstance().putProperty("pattern_id", "sim+ptncnt");
		
		Evaluation eval = new Evaluation();
		HashMapList<Evaluation> mapList = new HashMapList<>();
		mapList.addItem("1", eval);
		
		SimulationInfo si = new SimulationInfo("20220228", "01", "1", "1T", "1", mapList, "all", "all", "all", "all");
		//!!!
		List<MlResult> list = TestUtil.createResultList(2);

		si.patterns = Arrays.asList("B1");
		results =  target.apply(list, si);

		// ptncnt = 1
		assertEquals(2, results.size());
		assertEquals("1", results.get(0).getPattern());
		assertEquals("1", results.get(1).getPattern());
		
		// ptncnt = 2
		si.patterns = Arrays.asList("B1", "A2");
		results =  target.apply(list, si);
		assertEquals(4, results.size());
		assertEquals("1", results.get(0).getPattern());
		assertEquals("1", results.get(1).getPattern());
		assertEquals("2", results.get(2).getPattern());
		assertEquals("2", results.get(3).getPattern());
	}

	@Test
	void test_apply_patterns() throws Exception {
		List<MlResult> results;
		
		target = new SimulationPatternProvider();
		MLPropertyUtil.getInstance().putProperty("pattern_id", "sim+patterns");
		
		Evaluation eval = new Evaluation();
		HashMapList<Evaluation> mapList = new HashMapList<>();
		mapList.addItem("1", eval);
		
		SimulationInfo si = new SimulationInfo("20220228", "01", "1", "1T", "1", mapList, "all", "all", "all", "all");
		List<MlResult> list = TestUtil.createResultList(1);

		si.patterns = Arrays.asList("B1");
		results =  target.apply(list, si);

		// ptncnt = 1
		assertEquals(1, results.size());
		assertEquals("B1", results.get(0).getPattern());
		
		// ptncnt = 2
		si.patterns = Arrays.asList("B1", "A2");
		results =  target.apply(list, si);
		assertEquals(3, results.size());
		assertEquals("B1", results.get(0).getPattern());
		assertEquals("A2", results.get(1).getPattern());
		assertEquals("B1_A2", results.get(2).getPattern());
	}

	@Test
	void test_apply_patterns2() throws Exception {
		List<MlResult> results;
		
		target = new SimulationPatternProvider();
		MLPropertyUtil.getInstance().putProperty("pattern_id", "sim+patterns");
		
		Evaluation eval = new Evaluation();
		HashMapList<Evaluation> mapList = new HashMapList<>();
		mapList.addItem("1", eval);
		
		SimulationInfo si = new SimulationInfo("20220228", "01", "1", "1T", "1", mapList, "all", "all", "all", "all");
		// !!!
		List<MlResult> list = TestUtil.createResultList(2);

		si.patterns = Arrays.asList("B1");
		results =  target.apply(list, si);

		// ptncnt = 1
		assertEquals(2, results.size());
		assertEquals("B1", results.get(0).getPattern());
		assertEquals("B1", results.get(1).getPattern());
		
		// ptncnt = 2
		si.patterns = Arrays.asList("B1", "A2");
		results =  target.apply(list, si);
		assertEquals(6, results.size());
		assertEquals("B1", results.get(0).getPattern());
		assertEquals("B1", results.get(1).getPattern());
		assertEquals("A2", results.get(2).getPattern());
		assertEquals("A2", results.get(3).getPattern());
		assertEquals("B1_A2", results.get(4).getPattern());
		assertEquals("B1_A2", results.get(5).getPattern());
	}
*/	
}

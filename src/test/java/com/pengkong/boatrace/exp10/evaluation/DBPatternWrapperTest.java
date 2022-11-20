package com.pengkong.boatrace.exp10.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pengkong.boatrace.exp10.simulation.pattern.PatternWrapper;
import com.pengkong.boatrace.server.db.dto.DBRecord;

class DBPatternWrapperTest {

	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
	}
	
	
	@Test
	void test_getPattern() {
		DBRecord rec = new DBRecord();
		rec.put("alevelcount", (short)2);
		rec.put("com_confidence", (short)5);
		rec.put("com_predict", "1234");
		rec.put("grade", "ip");
		rec.put("jyocd", "01");
		double[] arr = {7.51,6.51, 5.51, 4.51, 3.51, 2.51};
		rec.put("nationwiningrate", arr);
		rec.put("odds", 1.05);
		rec.put("prediction1", "1");
		rec.put("prediction2", "2");
		rec.put("prediction3", "3");
		double[] arr2 = {0.605,0.405,0.305,0.025,0.015,0.015};
		rec.put("probabilities1", arr2);
		double[] arr3 = {0.605,0.405,0.305,0.025,0.015,0.015};
		rec.put("probabilities2", arr3);
		rec.put("probability1", 0.75);
		rec.put("probability2", 0.45);
		rec.put("probability3", 0.25);
		rec.put("raceno", (short)2);
		rec.put("racetype", "else");
		rec.put("turn", "1");
		rec.put("wakulevellist", "A1-A2-B1-B2-A1-A2");
		
		PatternWrapper dpw = new PatternWrapper(rec);
		
		assertEquals(dpw.getPattern("turn"),"1");
		assertEquals(dpw.getPattern("jyocd"),"01");
		assertEquals(dpw.getPattern("raceno"),"2");
		assertEquals(dpw.getPattern("grade"),"ip");
		assertEquals(dpw.getPattern("racetype"),"else");
		assertEquals(dpw.getPattern("alevelcount"),"2");
		assertEquals(dpw.getPattern("wakulevel1"),"A1");
		assertEquals(dpw.getPattern("wakulevel12"),"A1-A2");
		assertEquals(dpw.getPattern("wakulevel13"),"A1-A2-B1");
		assertEquals(dpw.getPattern("turn+level1"),"1-A1");
		assertEquals(dpw.getPattern("turn+race"),"1-2");
		assertEquals(dpw.getPattern("turn+alvlcnt"),"1-2");
		assertEquals(dpw.getPattern("turn+racetype"),"1-else");
		assertEquals(dpw.getPattern("jyocd+racetype"),"01-else");
		assertEquals(dpw.getPattern("jyocd+race"),"01-2");
		assertEquals(dpw.getPattern("probr1-1dig"),"0.7");
		assertEquals(dpw.getPattern("probr1-2dig"),"0.75");
		assertEquals(dpw.getPattern("probr12-1dig"),"0.7-0.4");
		assertEquals(dpw.getPattern("probr123-1dig"),"0.7-0.4-0.2");
		assertEquals(dpw.getPattern("psum-r12"),"1.2");
		assertEquals(dpw.getPattern("pmul-r12"),"0.3");
		assertEquals(dpw.getPattern("psum-r123"),"1.4");
		assertEquals(dpw.getPattern("pmul-r123"),"0.0");
		assertEquals(dpw.getPattern("psum-r23"),"0.7-0.7");
		assertEquals(dpw.getPattern("pmul-r23"),"0.7-0.11");
		assertEquals(dpw.getPattern("pmulsum"),"1.4-0.08");
		assertEquals(dpw.getPattern("probr1-r2-1dig"),"0.1");
		assertEquals(dpw.getPattern("probr1-r2-2dig"),"0.19");
		assertEquals(dpw.getPattern("prob1mix"),"0.7-0.1");
		assertEquals(dpw.getPattern("prob1mix_2"),"0.7-0.19");
		assertEquals(dpw.getPattern("prob1mix_3"),"0.75-0.1");
		assertEquals(dpw.getPattern("prob1+waku1"),"0.7-A1");
		assertEquals(dpw.getPattern("prob1+waku12"),"0.7-A1-A2");
		assertEquals(dpw.getPattern("prob12+waku12"),"0.7-0.4-A1-A2");
		assertEquals(dpw.getPattern("prob1+turn"),"0.7-1");
		assertEquals(dpw.getPattern("prob1+raceno"),"0.7-2");
		assertEquals(dpw.getPattern("prob1+jyocd"),"0.7-01");
		assertEquals(dpw.getPattern("prob1+alevelcount"),"0.7-2");
		assertEquals(dpw.getPattern("jyocd+waku1"),"01-A1");
		assertEquals(dpw.getPattern("race+waku1"),"2-A1");
		assertEquals(dpw.getPattern("turn+race+waku1"),"1-2-A1");
		assertEquals(dpw.getPattern("turn+lcnt+waku1"),"1-2-A1");
		assertEquals(dpw.getPattern("swa1"),"A1-7");
		assertEquals(dpw.getPattern("swa12"),"A17-A26");
		assertEquals(dpw.getPattern("swa1+waku2"),"A17-A2");
		assertEquals(dpw.getPattern("pd12+wk1"),"12-A1");
		assertEquals(dpw.getPattern("pd12+wk12"),"12-A1-A2");
		assertEquals(dpw.getPattern("pd12+wk123"),"12-A1-A2-B1");
		assertEquals(dpw.getPattern("pd12+raceno"),"12-2");
		assertEquals(dpw.getPattern("pd12+jyocd"),"12-01");
		assertEquals(dpw.getPattern("pd12+turn+raceno"),"12-1-2");
		assertEquals(dpw.getPattern("pd12+turn+level1"),"12-1-A1");
		assertEquals(dpw.getPattern("pd12+turn+prob1"),"12-1-0.7");
		assertEquals(dpw.getPattern("pd12+turn+acnt"),"12-1-2");
		assertEquals(dpw.getPattern("pd123+wk1"),"123-A1");
		assertEquals(dpw.getPattern("pd123+jyocd"),"123-01");
		assertEquals(dpw.getPattern("pd123+wk12"),"123-A1-A2");
		assertEquals(dpw.getPattern("compred1"),"1");
		assertEquals(dpw.getPattern("compred12"),"12");
		assertEquals(dpw.getPattern("compred123"),"123");
		assertEquals(dpw.getPattern("compred1+conf"),"1-5");
		assertEquals(dpw.getPattern("ml+comconf"),"0.7-1-5");
		assertEquals(dpw.getPattern("ml+comconf_2"),"0.75-1-5");
		assertEquals(dpw.getPattern("comconf12"),"12-5");
		assertEquals(dpw.getPattern("resultodds"),"1.05");
		assertEquals(dpw.getPattern("prob1_oddr3T123"),"0.7-1.05");
		assertEquals(dpw.getPattern("oddr3T123"),"1.05");
		assertEquals(dpw.getPattern("exptrate1"),"0.7");
		assertEquals(dpw.getPattern("exptrate_mul12"),"0.3");
		assertEquals(dpw.getPattern("exptrate_mul123"),"0.0");
		assertEquals(dpw.getPattern("exptrate_sum12"),"1.2");
		assertEquals(dpw.getPattern("exptrate_sum123"),"1.5");
		
	}
}

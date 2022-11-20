package com.pengkong.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringUtilTest {

	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
	}
	
	
	@Test
	void test_parseOptionsString_OK() {
		List<String> results = StringUtil.parseNumericOptionsString("1"); 
		assertEquals(1, results.size());
		assertEquals("1", results.get(0));
		
		results = StringUtil.parseNumericOptionsString(""); 
		assertEquals(0, results.size());
		
		results = StringUtil.parseNumericOptionsString("1,2-5,6"); 
		assertEquals(6, results.size());
		assertEquals("4", results.get(3));
	}
}

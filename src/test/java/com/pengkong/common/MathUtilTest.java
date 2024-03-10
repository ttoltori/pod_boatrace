package com.pengkong.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MathUtilTest {

	@BeforeAll
	/** executed before all test cases */
	public static void beforeClass() throws Exception {
	}
	
	/** executed before every test cases */
	@BeforeEach
	public void before() {
	}
	
	
	@Test
	void splitList_OK() {
		// 準備
		// 125itemのリストを41itemのリスト３個に分ける。残りは捨てる
		List<Integer> list = new ArrayList<>();
		int total = 1;
		int split = 1;
		for (int i = 0; i < total; i++) {
			list.add(i);
		}
		
		// 実行
		List<Integer>[] arrList = MathUtil.splitList(list, split); 
		
		// 結果
		assertEquals(arrList.length, split);
		for (int i = 0 ; i < split; i++) {
			assertEquals(arrList[0].size(), total/split);
		}
	}
	
	@Test
	void test_getSortedArray() {
		double[] original = new double[] {0.78, 0.13, 0.5, 1.23};
		double[] sorted = MathUtil.getSortedArray(original, true);
		
		assertEquals(original[0], 0.78);
		assertEquals(original[3], 1.23);
		assertEquals(sorted[0], 0.13);
		assertEquals(sorted[3], 1.23);
		
		sorted = MathUtil.getSortedArray(original, false);
		assertEquals(original[0], 0.78);
		assertEquals(original[3], 1.23);
		assertEquals(sorted[0], 1.23);
		assertEquals(sorted[3], 0.13);
	}
	
	@Test
	void test_HarmonyMean() {
		Double result;
		
		result = MathUtil.getHarmonyMean(0.2, 0.7);
		assertEquals(0.15, MathUtil.scale2(result));
		
		result = MathUtil.getHarmonyMean(0.54, 1.03, 0.32);
		assertEquals(0.16, MathUtil.scale2(result));
	}
}

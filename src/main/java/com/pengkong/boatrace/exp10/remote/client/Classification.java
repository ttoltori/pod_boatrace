package com.pengkong.boatrace.exp10.remote.client;

/**
 * Classification結果をDB insert用に変換値クラス。
 * @author ttolt
 *
 */
public class Classification {
	/** classification:class value,  regression:"rf" */
	public String prediction;
	
	/** classification:最大確率値,  regression:結果値 */
	public double probability;
}

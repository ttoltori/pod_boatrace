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
	
	/** classification:クラス毎の確率値,  regressionは未使用 */
	public double[] probabilities;
	
	/** regressionは未使用 */
	public double skewness;
	
	/** regressionは未使用 */
	public double kurtosis;
}

package com.pengkong.boatrace.exp10.simulation.range;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.mybatis.entity.MlResult;

/**
 * 任意の値（予想確率、直前オッズ）範囲の最適値と統計を取得するための集計クラス.
 * @author ttolt
 */
public class RangeStatUnit {
	public Object factor;
	public int betCnt = 0;
	public int hitCnt = 0;
	public int betAmt = 0;
	public int hitAmt = 0;
	public Double hitOddsMax = 0.0;
	public Double hitOddsrankMax = 0.0;
	public Double bOddsMin = 999.0;
	public Double bOddsMax = 0.0;
	public Double bOddsrankMax = 0.0;
	
//	/** factorValueが直前オッズの場合設定する。*/
//	public List<MlResult> results = new ArrayList<>();

	public RangeStatUnit(Object factor) {
		this.factor = factor;
	}

	/** 収益金額 */
	public double getIncome() {
		return (double)(hitAmt - betAmt);
	}

	/** 的中率 */
	public double getHitrate() {
		return ((double)hitCnt / (double)betCnt);
	}

	/** 収益率 */
	public double getIncomerate() {
		return ((double)hitAmt / (double)betAmt);
	}

	public Double factorValue() {
		return (Double) factor;
	}
	
	/**
	 * Stringのfactorを"_"で分割する。結果をDoubleの配列で取得する
	 */
	public Double[] factorValues() {
		String[] token = ((String)factor).split(Delimeter.UNDERBAR.getValue());
		Double[] values = new Double[token.length];
		for (int i = 0; i < token.length; i++) {
			values[i] = Double.valueOf(token[i]);
		}
		
		return values;
	}
	
	/** 集計値追加 */
	public void add(MlResult result) {
		betCnt++;
		betAmt += result.getBetamt();
		if (result.getHity() == 1) {
			hitCnt++;
			hitAmt += result.getHitamt();
			
			if (result.getResultOdds() != null && result.getResultOdds() > hitOddsMax) {
				hitOddsMax = result.getResultOdds();
			}
			
			if (result.getResultOddsrank() != null && result.getResultOddsrank() > hitOddsrankMax) {
				hitOddsrankMax = result.getResultOddsrank().doubleValue();
			}
		}
		Double bOdds = result.getBetOdds();
		if (bOdds != null && bOdds > 0) {
			if (bOdds < bOddsMin) {
				bOddsMin = bOdds;
			}
			if (bOdds > bOddsMax) {
				bOddsMax = bOdds;
			}
		}

		Integer bOddsrank = result.getBetOddsrank();
		if (bOddsrank != null && bOddsrank > bOddsrankMax) {
			bOddsrankMax = bOddsrank.doubleValue();
		}
	}

	public String toString() {
		return "{" + factor + "," + betCnt + "," + getIncome() + "}";
	}
//	/** 集計値追加してresultを保存する */
//	public void addAndPreserve(MlResult result) {
//		add(result);
//		results.add(result);
//	}
}

package com.pengkong.boatrace.exp10.result.stat;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.simulation.range.AbstractRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.common.MathUtil;

public class MlCreatorBase<E> 
{
	Logger logger = LoggerFactory.getLogger(MlCreatorBase.class);
	
	/** regression失敗時のエラーslope値 */
	static double RG_ERROR_VALUE = -0.999;

	ResultStat stat;
	
	public MlCreatorBase(ResultStat stat) {
		this.stat = stat;
	}

	/**
	 * 指定valuesの記述統計量をdtoに設定して取得する
	 * @param dto MlEvaluation
	 * @param field ex) Hodds, Hoddsrk, Rodds, Roddsrk, Bodds, Boddsrk
	 * @param values 値リスト 
	 */
	E setDescriptiveStatistics(E dto, String field, List<Double> values) throws Exception {
		double[] descriptiveStat = MathUtil.getDescriptiveStatistics(values);

		String[] postFixes = {"Min", "Max", "Mean", "Stddev", "Median"};
		String methodName;
		for (int i = 0; i < postFixes.length; i++) {
			methodName = String.join("", "set", field, postFixes[i]);
			dto.getClass().getMethod(methodName, Double.class).invoke(dto, descriptiveStat[i]);
		}

		return dto;
	}
	
	/** 直前オッズの最適範囲探索結果の設定 */
	/**
	 * 指定RangeStatUnitのリストに対して最適範囲を求めて、その範囲内の集計値を設定して返却する.
	 * @param dto MlEvaluation
	 * @param field ex) Ror, Rork, Bor, Bork, Pr
	 * @param units
	 * @param totalBetCnt
	 * @return
	 * @throws Exception
	 */
	E setRangeStatistics(E dto, String field, AbstractRangeFinder rangeFinder, double totalBetCnt) throws Exception{
		String methodName;
		String[] postFixes = {"Betcnt", "Betamt", "Hitcnt", "Hitamt", "Betrate", "Hitrate", "Incomerate"};
		
		Class<? extends Object> clazz = dto.getClass();
		
		List<RangeStatUnit> bestRangeUnits = rangeFinder.findBestRangeStatUnits();
		
		if (bestRangeUnits.size() <= 0) {
			for (int i = 0; i < postFixes.length; i++) {
				methodName = String.join("", "set", field, postFixes[i]);
				if (methodName.endsWith("rate")) {
					clazz.getMethod(methodName, Double.class).invoke(dto, 0.0);
				} else {
					clazz.getMethod(methodName, Integer.class).invoke(dto, 0);
				}
			}
			return dto;
		}
		
		Double bestRangeMin = bestRangeUnits.get(0).factorValue();
		Double bestRangeMax = bestRangeUnits.get(bestRangeUnits.size()-1).factorValue();
		
		clazz.getMethod("set"+field+"Bestmin", Double.class).invoke(dto, bestRangeMin);
		clazz.getMethod("set"+field+"Bestmax", Double.class).invoke(dto, bestRangeMax);

		
		// 直前オッズ最適範囲に対する統計情報
		
		Double[] values = getRangeDtoValues(bestRangeUnits, totalBetCnt);
		
		for (int i = 0; i < postFixes.length; i++) {
			methodName = String.join("", "set", field, postFixes[i]);
			if (methodName.endsWith("rate")) {
				clazz.getMethod(methodName, Double.class).invoke(dto, MathUtil.scale2(values[i]));
			} else {
				clazz.getMethod(methodName, Integer.class).invoke(dto, values[i].intValue());
			}
		}
		
		return dto;
	}

	/** 範囲内データに対する集計結果を取得する */
	Double[] getRangeDtoValues(List<RangeStatUnit> listUnit, double totalBetCnt) {
		double rangeBetCnt = 0;
		double rangeBetamt = 0;
		double rangeHitCnt = 0;
		double rangeHitAmt = 0;
		double rangeBetrate = 0;
		double rangeHitrate = 0;
		double rangeIncomerate = 0;

		for (RangeStatUnit unit : listUnit) {
			rangeBetCnt += unit.betCnt; // 範囲内のデータ数
			rangeBetamt += unit.betAmt; // 範囲内のbet金額合計
			rangeHitCnt += unit.hitCnt; // 範囲内の的中したデータ数
			rangeHitAmt += unit.hitAmt; // 範囲内の的中金額合計
		}
		
		// zero divide 防止
		if (rangeBetCnt > 0) {
			rangeHitrate = rangeHitCnt / rangeBetCnt; // stat.sumOfBet;
			rangeIncomerate = rangeHitAmt / rangeBetamt; // stat.sumOfBetAmount;
			rangeBetrate = rangeBetCnt / totalBetCnt;
		}
		
		return new Double[]{rangeBetCnt, rangeBetamt, rangeHitCnt, rangeHitAmt, rangeBetrate, rangeHitrate, rangeIncomerate};
	}
}

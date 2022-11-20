package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 最大の収益金額が得られる直前オッズ or 予想的中確率の範囲を探索するクラス
 * @author ttolt
 */
public class BestRangeFinder extends AbstractRangeFinder{
	public BestRangeFinder(Collection<RangeStatUnit> values) {
		super(new ArrayList<>(values));
	}

	/** 直前オッズ or 予想的中確率単位の収益集計から全ての範囲を探索して、
	 * ①範囲単位の集計へ変換する。
	 * ②範囲単位の収益合計順に範囲単位をソートする
	 */
	@Override
	void parse() {
		int size = listUnit.size();
		int bestSum = 0;
		int sum = 0;
		
		bestRangeSumUnit = new RangeSumUnit(-1, -1);
		for (int x1 = 0; x1 < size; x1++) {
			sum = 0;
			for (int x2 = x1; x2 < size; x2++) {
				// 収益金額
				sum += listUnit.get(x2).getIncome();
				if (sum > bestSum) {
					bestRangeSumUnit = new RangeSumUnit(x1, x2);
					bestSum = sum;
				}
			}
		}
	}
}

package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 最大の収益金額が得られる直前オッズ or 予想的中確率の範囲を探索するクラス
 * @author ttolt
 */
public class LeftRangeFinder extends AbstractRangeFinder {
	protected int startIdx = 0;
	
	public LeftRangeFinder() {
		super();
	}
	
	public LeftRangeFinder(Collection<RangeStatUnit> values) {
		super(new ArrayList<>(values));
	}

	/** 直前オッズ or 予想的中確率単位の収益集計から全ての範囲を探索して、
	 * ①範囲単位の集計へ変換する。
	 * ②範囲単位の収益合計順に範囲単位をソートする
	 */
	void parse() {
		int size = listUnit.size();
		int bestSum = 0;
		int sum = 0;
		
		bestRangeSumUnit = new RangeSumUnit(-1, -1);
		for (int x1 = startIdx; x1 < size; x1++) {
			// 収益金額
			sum += listUnit.get(x1).getIncome();
			if (sum > bestSum) {
				bestRangeSumUnit = new RangeSumUnit(startIdx, x1);
				bestSum = sum;
			}
		}
	}
}

package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.List;

/**
 * 最大の収益金額が得られる直前オッズ or 予想的中確率の範囲を探索するクラス
 * @author ttolt
 */
public abstract class AbstractRangeFinder {
	List<RangeStatUnit> listUnit;
	
	RangeSumUnit bestRangeSumUnit;
	
	public AbstractRangeFinder(List<RangeStatUnit> listUnit) {
		this.listUnit = listUnit;
	}

	public AbstractRangeFinder() {
	}
	
	/** 直前オッズ or 予想的中確率単位の収益集計から全ての範囲を探索して、
	 * ①範囲単位の集計へ変換する。
	 * ②範囲単位の収益合計順に範囲単位をソートする
	 */
	abstract void parse();
	
	void ensureParsed() {
		if (bestRangeSumUnit == null) {
			parse();
		}
	}

	public void setListUnit(List<RangeStatUnit> listUnit) {
		this.listUnit = listUnit;
		bestRangeSumUnit = null;
	}
	
	/** BEST rangeに含まれるRangeStatUnitのリストを取得する */
	public List<RangeStatUnit> findBestRangeStatUnits() {
		ensureParsed();

		List<RangeStatUnit> results = new ArrayList<>();
		
		int[] indexes = findBestRangeIndex();
		if (indexes[0] < 0 || indexes[1] < 0) {
			return results;
		}
		
		for (int i = indexes[0]; i <= indexes[1]; i++) {
			results.add(listUnit.get(i));
		}
		return results;
	}
	
	/**
	 * 最大の収益金額合計が得られる直前オッズ or 予想的中確率の範囲を取得する。
	 * @return [0]=from [1]=to  例）[0]=0.75 ~ [1]=0.85
	 */
	@Deprecated
	Double[] findBestRangeMinMax() {
		ensureParsed();

		Double[] results = new Double[2];
		
		int[] indexes = findBestRangeIndex();
		results[0] = listUnit.get(indexes[0]).factorValue();
		results[1] = listUnit.get(indexes[1]).factorValue();
		
		return results;
	}

	/**
	 * 最大の収益金額合計が得られる直前オッズ or 予想的中確率の範囲を取得する。
	 * @return [0]=from [1]=to  例）[0]=0.75 ~ [1]=0.85
	 */
	int[] findBestRangeIndex() {
		int[] result = new int[2];
		result[0] = bestRangeSumUnit.x1;
		result[1] = bestRangeSumUnit.x2;
		
		return result;
	}
	
	/** 直前オッズ or 予想的中確率の範囲毎の収益金額合計を集計する単位クラス */
	class RangeSumUnit {
		public int x1;
		public int x2;
		public RangeSumUnit(int x1, int x2) {
			this.x1 = x1;
			this.x2 = x2;
		}
	}
}

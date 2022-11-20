package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 最大の収益金額が得られる直前オッズ or 予想的中確率の範囲を探索するクラス
 * @author ttolt
 */
public class XYRangeFinder {
	List<RangeStatUnit> xlist;
	List<RangeStatUnit> ylist;
	Map<String, RangeStatUnit> mapXYStatUnit;
	
	RangeSumUnit bestRangeSumUnit = null;
	
	public XYRangeFinder(Map<String, RangeStatUnit> mapXYStatUnit, List<RangeStatUnit> xlist, List<RangeStatUnit> ylist) {
		super();
		this.mapXYStatUnit = mapXYStatUnit;		
		this.xlist = xlist;
		this.ylist = ylist;
	}

	/** 直前オッズ/予想的中確率マップの収益集計から全ての範囲を探索して、
	 * 最適範囲を探索する
	 */
	void parse() {
		int xSize = xlist.size();
		int ySize = ylist.size();
		double bestSum = 0;
		double sum = 0;
		Ysum ySum;
		int x1,y1,x2,y2;
		
		bestRangeSumUnit = new RangeSumUnit(0, 0, 0, 0);
		for (x1 = 0; x1 < xSize; x1++) {
			for (y1 = 0; y1 < ySize; y1++) {
				ySum = new Ysum(ySize);
				for (x2 = x1; x2 < xSize; x2++) {
					sum = 0;
					for (y2 = y1; y2 < ySize; y2++) {
						double value = getValue(x2, y2);
						sum += (ySum.get(y2) + value);
						if (sum > bestSum) {
							bestRangeSumUnit = new RangeSumUnit(x1, x2, y1, y2);
							bestSum = sum;
						}
						ySum.add(y2, value);
					}
				}
			}
		}
	}

	/**
	 * xyマップにおいてy別にxの累積合計を集計する
	 * @author ttolt
	 *
	 */
	private class Ysum {
		double[] sums;
		double lastSum = 0;
		public Ysum(int size) {
			sums = new double[size];
			for (int i = 0; i < size; i++) {
				sums[i] = 0.0;
			}
		}
		public void add(int y, double value) {
			lastSum += value;
			if (value != 0) {
				sums[y] += lastSum;
				lastSum = 0;
			}
		}
		
		public double get(int y) {
			return sums[y];
		}
	}
	
	/** 直前オッズ/予想確率mapから指定インデックス(listの）の集計UNITの収益金額を取得する */
	double getValue(int x, int y) {
		RangeStatUnit unit = getUnit(x, y);
		if (unit == null) {
			return 0;
		}
		
		return unit.getIncome();
	}

	/** 直前オッズ/予想確率mapから指定インデックス(listの）の集計UNITを取得する */
	RangeStatUnit getUnit(int x, int y) {
		return mapXYStatUnit.get(createKey(x, y));
	}
	
	/**  
	 * 直前オッズ/予想確率mapのキーを取得する
	 * 
	 * @param x 的中オッズ別集計リストのインデックス
	 * @param y 予想確率別集計リストのインデックス
	 * @return ex) "1.23_0.67" 
	 */
	private String createKey(int x, int y) {
		return String.join("_", xlist.get(x).factorValue().toString(), ylist.get(y).factorValue().toString());
	}

	void ensureParsed() {
		if (bestRangeSumUnit == null) {
			parse();
		}
	}
	
	/** BEST rangeに含まれるRangeStatUnitのリストを取得する */
	public List<RangeStatUnit> findBestRangeStatUnits() {
		ensureParsed();

		List<RangeStatUnit> results = new ArrayList<>();
		RangeStatUnit unit;
		
		int[] indexes = findBestRangeIndex();
		for (int x = indexes[0]; x <= indexes[1]; x++) {
			for (int y = indexes[2]; y <= indexes[3]; y++) {
				unit = getUnit(x, y);
				if (unit == null) {
					continue;
				}
				results.add(unit);
			}
		}

		return results;
	}
	
	/**
	 * 最大の収益金額合計が得られる直前オッズ(x1,x2)/予想的中確率(y1,y2)の範囲値を取得する。
	 * @return [0]=x1 [1]=x2 [0]=y1 [1]=y2  例）[0]=1.2~[1]=1.5 [2]=0.6~[3]=0.8
	 */
	public Double[] findBestRangeMinMax() {
		ensureParsed();

		int[] indexes = findBestRangeIndex();

		Double[] results = new Double[4];
		results[0] = xlist.get(indexes[0]).factorValue();
		results[1] = xlist.get(indexes[1]).factorValue();
		results[2] = ylist.get(indexes[2]).factorValue();
		results[3] = ylist.get(indexes[3]).factorValue();
		
		return results;
	}

	/**
	 * 最大の収益金額合計が得られる直前オッズ(x1,x2)/予想的中確率(y1,y2)の範囲インデックスを取得する。
	 * @return [0]=x1 [1]=x2 [0]=y1 [1]=y2  例）[0]=1~[1]=5 [2]=2~[3]=4
	 */
	int[] findBestRangeIndex() {
		int[] result = new int[4];
		result[0] = bestRangeSumUnit.x1;
		result[1] = bestRangeSumUnit.x2;
		result[2] = bestRangeSumUnit.y1;
		result[3] = bestRangeSumUnit.y2;
		
		return result;
	}
	
	/** 直前オッズ/予想的中確率の範囲毎の収益金額合計を集計する単位クラス */
	private class RangeSumUnit {
		public int x1;
		public int x2;
		public int y1;
		public int y2;
		public RangeSumUnit(int x1, int x2, int y1, int y2) {
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}
	}
}

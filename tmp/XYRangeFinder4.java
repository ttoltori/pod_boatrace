package com.pengkong.boatrace.exp10.simulation.range;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 最大の収益金額が得られる直前オッズ or 予想的中確率の範囲を探索するクラス
 * @author ttolt
 */
public class XYRangeFinder4 {
	List<RangeStatUnit> xlist;
	List<RangeStatUnit> ylist;
	Map<String, RangeStatUnit> mapXYStatUnit;
	RangeSumUnit bestRangeSumUnit = null;
	
	public XYRangeFinder4(Map<String, RangeStatUnit> mapXYStatUnit, List<RangeStatUnit> xlist, List<RangeStatUnit> ylist) {
		super();
		this.mapXYStatUnit = mapXYStatUnit;		
		this.xlist = xlist;
		this.ylist = ylist;
	}

	/** 直前オッズ or 予想的中確率単位の収益集計から全ての範囲を探索して、
	 * ①範囲単位の集計へ変換する。
	 * ②範囲単位の収益合計順に範囲単位をソートする
	 */
	void parse() {
		int xSize = xlist.size();
		int ySize = ylist.size();

		int bestSum = -999999;
		int sum = 0;
		for (int x1 = 0; x1 < xSize; x1++) {
			//sum = 0;
			for (int y1 = 0; y1 < ySize; y1++) {
				sum = 0;
				//sum += getSum(x1, y1);
				for (int x2 = x1; x2 < xSize; x2++) {
					//sum += getSum(x2, y1);
					for (int y2 = y1; y2 < ySize; y2++) {
						sum += getSum(x2, y2);
						if (sum > bestSum) {
							bestSum = sum;
							bestRangeSumUnit = new RangeSumUnit(x1, x2, y1, y2, sum);
						}
					}
				}
			}
		}
	}
	
	double getSum(int x, int y) {
		RangeStatUnit unit = mapXYStatUnit.get(createKey(x, y));
		if (unit == null) {
			return 0;
		}
		
		return unit.getIncome();
	}

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

		return bestRangeSumUnit.units;
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
	
	/** 直前オッズ or 予想的中確率の範囲毎の収益金額合計を集計する単位クラス */
	private class RangeSumUnit {
		public int x1;
		public int x2;
		public int y1;
		public int y2;
		public int sum;
		public List<RangeStatUnit> units = new ArrayList<>();
		public RangeSumUnit(int x1, int x2, int y1, int y2, int sum) {
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
			this.sum = sum;
			//setRangeUnits();
		}

		/** index x1～x2, y1～y2のxy範囲の集計及び合計収益金額を取得する　*/
		void setRangeUnits() {
			RangeStatUnit unit;
			for (int x = x1; x <= x2; x++) {
				for (int y = y1; y <= y2; y++) {
					unit = mapXYStatUnit.get(createKey(x, y));
					if (unit == null) {
						continue;
					}
					sum += unit.getIncome();
				}
			}
		}

		
	}

	/** 収益金額合計順のソートクラス */
	private class RangeSumUnitComparator implements Comparator<RangeSumUnit> {
		@Override
		public int compare(RangeSumUnit o1, RangeSumUnit o2) {
			return Integer.compare(o1.sum, o2.sum);			
		}
	}
}

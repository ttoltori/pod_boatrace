package com.pengkong.boatrace.exp10.simulation.calculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.common.MathUtil;

public class HistogramCalculator {
	public List<Double > values;

	public HistogramCalculator() {
		super();
	}
	
	void setValues(List<Double> numbers) {
		values = new ArrayList<Double>();
		for (Double num : numbers) {
			values.add(num);
		}
		
        // Sort the list in ascending order
        Collections.sort(values);
	}
	
	Double getPercentile(Double percent) {
        // Calculate the index of the Nth percentile
        int indexOfPercentile = (int) Math.ceil(percent * values.size() / 100.0) - 1;

        // If the index is out of bounds, return the maximum or minimum value
        if (indexOfPercentile < 0) {
            return values.get(0);
        } else if (indexOfPercentile >= values.size()) {
            return values.get(values.size() - 1);
        }

        // Return the Nth percentile value
        return values.get(indexOfPercentile);
	}
	
	List<Double> getPercentiles(List<Double> listPercent) {
		List<Double> result = new ArrayList<Double>();
		for (Double percent : listPercent) {
			result.add(getPercentile(percent));
		}
		
		return result;
	}
	
	public Map<Double, RangeStatUnit> histogram(Map<Double, RangeStatUnit> map, List<Double> listPercent) {
		if (map.size() == 0) {
			return map;
		}
		
		setValues( new ArrayList<Double>(map.keySet()));
		Map<Double, RangeStatUnit> result = new TreeMap<Double, RangeStatUnit>();
		
		List<Double> splitValues = getPercentiles(listPercent);
		
		for (Double key : map.keySet()) {
			Double newKey = MathUtil.getHistogramValue(key, splitValues);
			getRangeStatUnit(result, newKey).add(map.get(key));
		}
		
		return result;
	}
	
	
	/* 指定値のRangeValueStatがmapに存在しなければ生成して取得する。生成したRangeValueStatは mapに登録する
	 * 
	 * @param mapStatUnit map
	 * @param rangeValue  指定値
	 */
	RangeStatUnit getRangeStatUnit(Map<Double, RangeStatUnit> mapStatUnit, Double rangeValue) {
		RangeStatUnit rsu = mapStatUnit.get(rangeValue);
		if (rsu == null) {
			rsu = new RangeStatUnit(rangeValue);
			mapStatUnit.put(rangeValue, rsu);
		}

		return rsu;
	}
	
}

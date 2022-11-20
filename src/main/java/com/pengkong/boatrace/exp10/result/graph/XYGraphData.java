package com.pengkong.boatrace.exp10.result.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;

import com.pengkong.boatrace.exp10.util.MLUtil;

/**
 * XYグラフ用データクラス
 * @author ttolt
 *
 */
public class XYGraphData {
	List<Double> listXdata = new ArrayList<>();
	List<Double> listYdata = new ArrayList<>();

	public XYGraphData() {
	}

	public void add(Double xData, Double yData) {
		listXdata.add(xData);
		listYdata.add(yData.doubleValue());
	}
	
	public double[] getXdataArray() {
		if (listXdata.size() <= 0)
			listXdata.add(0.0);
			
		return ArrayUtils.toPrimitive(listXdata.toArray(new Double[listXdata.size()]));
	}
	
	public double[] getYdataArray() {
		if (listYdata.size() <= 0)
			listYdata.add(0.0);
		return ArrayUtils.toPrimitive(listYdata.toArray(new Double[listYdata.size()]));
	}

	
	/** 指定Set<Double, Double>をXYGraphDataに変換して取得する */
	public static XYGraphData convertXYGraphData(Map<Double, Double> map) {
		XYGraphData data = new XYGraphData();
		for (Entry<Double, Double> entry : map.entrySet()) {
			data.add(entry.getKey(), entry.getValue());
		}
		
		return data;
	}

	/** 指定Set<Double, Double>を黒字のみのXYGraphDataに変換して取得する */
	public static XYGraphData convertXYGraphDataSurplus(Map<Double, Double> map) {
		XYGraphData data = new XYGraphData();
		for (Entry<Double, Double> entry : map.entrySet()) {
			data.add(entry.getKey(), entry.getValue());
		}
		
		return data;
	}

	/** 指定Set<Double, Double>をXYGraphDataに変換して取得する */
	public static XYGraphData convertXYGraphDataWithXOutLier(Map<Double, Double> map) {
		List<Double> xValues = new ArrayList<>(map.keySet());
		double[] minmax = MLUtil.getOurlierRangeByPercentile(xValues, 0, 0.95);
		
		XYGraphData data = new XYGraphData();
		for (Entry<Double, Double> entry : map.entrySet()) {
			// 外れ値除去(上位0.95)
			if (entry.getKey() < minmax[0] || entry.getKey() > minmax[1] ) {
				continue;
			}
			data.add(entry.getKey(), entry.getValue());
		}
		
		return data;
	}

	/**
	 * listの値に対して昇順値毎の件数をカウントして、グラフ表示用データを返却する。
	 * @param list 値の一覧
	 * 
	 * @return xdata=値 ydata=値毎のカウント
	 */
	public static XYGraphData createSortedXYCountdata(List<Double> list) {
		XYGraphData gData = new XYGraphData();
		SortedMap<Double, Double> map = new TreeMap<>();
		for (Double value : list) {
			if (!map.containsKey(value)) {
				map.put(value, 0.0);
			}
			map.put(value, map.get(value) + 1);
		}
		
		// 値を循環する
		Iterator<Entry<Double,Double>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Double, Double> entry = (Entry<Double,Double>)it.next();
			gData.add(entry.getKey(), entry.getValue());
		}
		
		return gData;
	}
	
	/**
	 * listの値に対して昇順値毎の件数をカウントして、グラフ表示用データを返却する。
	 * @param list 値の一覧
	 * 
	 * @return xdata=値 ydata=値毎のカウント
	 */
	public static XYGraphData createSortedXYCountdataWithYOutlier(List<Double> list) {
		XYGraphData gData = new XYGraphData();
		SortedMap<Double, Double> map = new TreeMap<>();
		for (Double value : list) {
			if (!map.containsKey(value)) {
				map.put(value, 0.0);
			}
			map.put(value, map.get(value) + 1);
		}
		
		List<Double> xValues = new ArrayList<>(map.values());
		double[] minmax = MLUtil.getOurlierRangeByPercentile(xValues, 0, 0.95);
		
		// 値を循環する
		Iterator<Entry<Double,Double>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Double, Double> entry = (Entry<Double,Double>)it.next();
			
			// 外れ値除去(上位0.95)
			if (entry.getValue() < minmax[0] || entry.getValue() > minmax[1] ) {
				continue;
			}
			
			gData.add(entry.getKey(), entry.getValue());
		}
		
		return gData;
	}

	/**
	 * x軸はカウンタ、y軸は値のxyグラフデータを取得する。x軸をxScaleFactorでscale変更する。
	 * @param listYdata y軸データ
	 * @param xScaleFactor x軸データのscale factor
	 * @return
	 */
	public static XYGraphData getScaledXCounterYData(List<Double> listYdata, double xScaleFactor ) {
		XYGraphData result =  new XYGraphData();
		Double count = 1.0;
		for (Double yValue : listYdata) {
			result.add((count++ * xScaleFactor), yValue);
		}
		
		return result;
	}

	/**
	 * 対象リストからmin～max範囲に入る値だけ収集する。
	 * @param values 対象リスト 
	 * @param min 最小値
	 * @param max 最大値
	 */
	public static List<Double> getLimitedList(List<Double> values, Double min, Double max) {
		List<Double> result = new ArrayList<>();
		if (min == null || max == null) {
			return result;
		}
		
		for (Double value : values) {
			if (value >= min && value <= max) 
				result.add(value);
		}
		
		return result;
	}
}

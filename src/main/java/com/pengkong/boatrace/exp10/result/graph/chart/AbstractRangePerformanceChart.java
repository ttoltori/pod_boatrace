package com.pengkong.boatrace.exp10.result.graph.chart;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.YAxisPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pengkong.boatrace.exp10.result.graph.XYGraphData;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;

@SuppressWarnings("rawtypes")
public abstract class AbstractRangePerformanceChart extends AbstractChart {
	protected Map<Double, RangeStatUnit> mapUnit; 
	
	public AbstractRangePerformanceChart(ResultStat stat) {
		super(stat);
	}

	protected Chart createRangePerformanceChart(Color barColor) {
		// 黒字
		SortedMap<Double, Double> mapIncome= new TreeMap<>(); // 収益金額
		// 赤字
		SortedMap<Double, Double> mapLoss= new TreeMap<>(); // 損害金額
		
		SortedMap<Double, Double> mapBetcnt = new TreeMap<>(); // 範囲内投票数
		
		for (Entry<Double, RangeStatUnit> entry : mapUnit.entrySet()) {
			// key = probability
			Double key =  entry.getKey();
			if (entry.getValue().getIncome() > 0) { // 黒字
				mapIncome.put(key, entry.getValue().getIncome());
				mapLoss.put(key, 0.0);
			} else { // 赤字
				mapIncome.put(key, 0.0);
				mapLoss.put(key, entry.getValue().getIncome());
			}
			mapBetcnt.put(key, Double.valueOf(entry.getValue().betCnt));
		}
		
		CategoryChart chart = new CategoryChartBuilder().width(SIZE_X).height(SIZE_Y).build();
		chart.getStyler().setXAxisMaxLabelCount(20);
		chart.getStyler().setAxisTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		//chart.getStyler().setLegendPosition(LegendPosition.InsideN);
		chart.getStyler().setLegendVisible(false);
		chart.getStyler().setYAxisGroupPosition(0, YAxisPosition.Right);
		chart.getStyler().setYAxisGroupPosition(1, YAxisPosition.Left);
		chart.getStyler().setOverlapped(true);
		
		XYGraphData xyData;
		CategorySeries series;
		
		// group 1
		// 範囲の投票数
		xyData = XYGraphData.convertXYGraphData(mapBetcnt);
		series = chart.addSeries("帳票数:1", xyData.getXdataArray(), xyData.getYdataArray());
		series.setChartCategorySeriesRenderStyle(CategorySeriesRenderStyle.Line);
		series.setMarker(SeriesMarkers.NONE);
		series.setLineColor(Color.ORANGE);
		series.setFillColor(Color.ORANGE);
		series.setYAxisGroup(0);

		// group 0
		// 範囲の黒字金額
		xyData = XYGraphData.convertXYGraphData(mapIncome);
		series = chart.addSeries("黒字:0", xyData.getXdataArray(), xyData.getYdataArray());
		series.setMarker(SeriesMarkers.NONE);
		series.setFillColor(barColor);
		series.setYAxisGroup(1);

		// 範囲の赤字金額
		xyData = XYGraphData.convertXYGraphData(mapLoss);
		series = chart.addSeries("赤字:0", xyData.getXdataArray(), xyData.getYdataArray());
		series.setMarker(SeriesMarkers.NONE);
		series.setFillColor(barColor);
		series.setYAxisGroup(1);
		
		return chart;
	}
}

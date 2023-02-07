package com.pengkong.boatrace.exp10.result.graph.split.chart;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.YAxisPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pengkong.boatrace.exp10.result.graph.XYGraphData;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;

@SuppressWarnings("rawtypes")
public abstract class AbstractRangePerformanceChartSplit extends AbstractChartSplit {
	protected Map<Double, RangeStatUnit> mapUnit; 
	protected List<Map<Double, RangeStatUnit>> listMapUnit = new ArrayList<Map<Double,RangeStatUnit>>();
	
	public AbstractRangePerformanceChartSplit(ResultStat stat, List<ResultStat> split) {
		super(stat, split);
	}

	protected Chart createRangePerformanceChart() {
		
		XYChart chart = new XYChartBuilder().width(SIZE_X).height(SIZE_Y).build();
		chart.getStyler().setXAxisMaxLabelCount(20);
		chart.getStyler().setAxisTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		//chart.getStyler().setLegendPosition(LegendPosition.InsideN);
		chart.getStyler().setLegendVisible(false);
		chart.getStyler().setYAxisGroupPosition(0, YAxisPosition.Right);
		chart.getStyler().setYAxisGroupPosition(1, YAxisPosition.Left);
		//chart.getStyler().setOverlapped(true);
		
		for (int i = 0; i < listMapUnit.size(); i++) {
			Map<Double, RangeStatUnit> mapUnitItem = listMapUnit.get(i);
			SortedMap<Double, Double> mapIncome= new TreeMap<>(); // 収益金額
			SortedMap<Double, Double> mapBetcnt = new TreeMap<>(); // 範囲内投票数
			
			for (Entry<Double, RangeStatUnit> entry : mapUnitItem.entrySet()) {
				Double key =  entry.getKey();
				mapIncome.put(key, entry.getValue().getIncome());
				mapBetcnt.put(key, 0.0);
			}
			
			XYGraphData xyData;
			XYSeries series;

			if (i == 0) {
				// group 0
				// 範囲の投票数
				xyData = XYGraphData.convertXYGraphData(mapBetcnt);
				series = chart.addSeries("帳票数" + i, xyData.getXdataArray(), xyData.getYdataArray());
				series.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
				series.setMarker(SeriesMarkers.NONE);
				series.setMarkerColor(colors[i]);
				series.setLineColor(colors[i]);
				series.setLineWidth(5);
				series.setYAxisGroup(0);
			}
			
			// group 1
			// 範囲の収益金額
			xyData = XYGraphData.convertXYGraphData(mapIncome);
			series = chart.addSeries("収益" + i, xyData.getXdataArray(), xyData.getYdataArray());
			series.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
			series.setMarker(SeriesMarkers.CIRCLE);
			series.setMarkerColor(colors[i+1]);
			series.setLineColor(colors[i+1]);
			series.setLineWidth(calculateLineWidth());
			series.setYAxisGroup(0);
		}
		
		return chart;
	}
}

package com.pengkong.boatrace.exp10.result.graph.chart.term;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.Styler.YAxisPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pengkong.boatrace.exp10.result.graph.chart.AbstractChart;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.common.MathUtil;

/**
 * 期間単位の的中オッズの最大値の推移 group1 : 的中オッズ group2 : 的中オッズランク
 * 
 * @author ttolt
 *
 */
public class TermTimelineOdds extends AbstractChart {

	/** 統計記述値. createChartで生成 -> createTitle, createBottonで利用 */
	private double[] desc1; // 確定オッズ
	private double[] desc2; // 確定オッズrank

	public TermTimelineOdds(ResultStat stat) {
		super(stat);
	}

	@Override
	protected String createTitleLabel() {
		String label = stat.termDays + "日別 的中odds";
		label += "(平均,中央,偏差)= ";
		if (desc1 != null) {
			label += "的中odds(" + desc1[2] + ',' + desc1[4] + "," + desc1[3] + ") ";
		} else {
			label += "的中odds(なし)";
		}

		return label;
	}


	@Override
	protected String createBottomLabel() {
		String label = stat.termDays + "日別 的中oddsrank";
		label += "(平均,中央,偏差)= ";
		if (desc2 != null) {
			label += "的中oddsrank(" + desc2[2] + ',' + desc2[4] + "," + desc2[3] + ") ";
		} else {
			label += "的中oddsrank(なし)";
		}

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		XYChart chart = new XYChartBuilder().width(SIZE_X).height(SIZE_Y).build();
		chart.getStyler().setLegendPosition(LegendPosition.InsideN);
		chart.getStyler().setYAxisGroupPosition(0, YAxisPosition.Left);
		chart.getStyler().setYAxisGroupPosition(1, YAxisPosition.Right);
		XYSeries series;

		List<RangeStatUnit> listUnit = new ArrayList<RangeStatUnit>(stat.termStatPerformance.values());

		List<Double> yList1 = new ArrayList<>();
		List<Double> yList2 = new ArrayList<>();
		double[] yData1;
		double[] yData2;

		for (int i = 0; i < listUnit.size(); i++) {
			//if (listUnit.get(i).hitOddsMax > 0) {
				yList1.add(listUnit.get(i).hitOddsMax); // 的中odds max
			//}
			//if (listUnit.get(i).hitOddsrankMax > 0) {
				yList2.add(listUnit.get(i).hitOddsrankMax); // 的中oddsrank max
			//}
		}

		// 的中odds
		if (yList1.size() > 0) {
			yData1 = ArrayUtils.toPrimitive(yList1.toArray(new Double[yList1.size()]));
			desc1 = MathUtil.getDescriptiveStatistics(yData1);
			
			series = chart.addSeries("的中odds", yData1);
			series.setMarker(SeriesMarkers.NONE);
			series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
			series.setYAxisGroup(0);
		}
			

		// 的中oddsrank
		if (yList2.size() > 0) {
			yData2 = ArrayUtils.toPrimitive(yList2.toArray(new Double[yList2.size()]));
			desc2 = MathUtil.getDescriptiveStatistics(yData2);
			
			series = chart.addSeries("的中oddsrank", yData2);
			series.setMarker(SeriesMarkers.NONE);
			series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
			series.setYAxisGroup(1);
		}
		
		return chart;
	}
}

package com.pengkong.boatrace.exp10.result.graph.chart.term;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
 * 期間単位の時系列推移
 * group1 : ベット数、収益金額
 * group2 : 的中率、収益率
 * @author ttolt
 *
 */
public class TermTimelineBetcntHicnt extends AbstractChart {

	/** 統計記述値. createChartで生成 -> createTitle, createBottonで利用 */
	private double[] desc1;
	private double[] desc2;
	
	public TermTimelineBetcntHicnt(ResultStat stat) {
		super(stat);
	}

	@Override
	protected String createTitleLabel() {
		String label = stat.termDays + "日別ベット,的中数 ";
		label += "(平均,中央,偏差)= ";
		label += "ベット数(" + desc1[2] + ',' + desc1[4] + "," + desc1[3] + ") ";
		
		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label = "" ;
		label += "(平均,中央,偏差)= ";
		label += "的中数(" + desc2[2] + ',' + desc2[4] + "," + desc2[3] + ") ";
	
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
		
		List<RangeStatUnit> listUnit = new ArrayList<RangeStatUnit>(stat.termStatUnit.values());
		
		double[] yData1 = new double[listUnit.size()];
		double[] yData2 = new double[listUnit.size()];
		
		for (int i = 0; i < listUnit.size(); i++) {
			yData1[i] = listUnit.get(i).betCnt; // ベット数
			yData2[i] = listUnit.get(i).hitCnt; // 的中数
		}
		
		desc1 = MathUtil.getDescriptiveStatistics(yData1);
		desc2 = MathUtil.getDescriptiveStatistics(yData2);
		
		// ベット数
		series = chart.addSeries("ベット数", yData1);
		series.setMarker(SeriesMarkers.NONE);
		series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
		series.setYAxisGroup(0);
		
		// 的中数
		series = chart.addSeries("的中数", yData2);
		series.setMarker(SeriesMarkers.NONE);
		series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
		series.setLineColor(Color.RED);
		series.setYAxisGroup(1);
		

		return chart;
	}
}

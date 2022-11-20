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
public class TermTimelineHitrateIncomerate extends AbstractChart {

	/** 統計記述値. createChartで生成 -> createTitle, createBottonで利用 */
	private double[] desc3;
	private double[] desc4;
	
	public TermTimelineHitrateIncomerate(ResultStat stat) {
		super(stat);
	}

	@Override
	protected String createTitleLabel() {
		String label = stat.termDays + "日別 的中率,収益率 ";
		label += "(平均,中央,偏差)= ";
		label += "的中率(" + desc3[2] + ',' + desc3[4] + "," + desc3[3] + ") ";
		
		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label = "" ;
		label += "(平均,中央,偏差)= ";
		label += "収益率(" + desc4[2] + ',' + desc4[4] + "," + desc4[3] + ") ";
	
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
		
		double[] yData3 = new double[listUnit.size()];
		double[] yData4 = new double[listUnit.size()];
		
		for (int i = 0; i < listUnit.size(); i++) {
			yData3[i] = listUnit.get(i).getHitrate(); // 的中率
			yData4[i] = listUnit.get(i).getIncomerate(); // 収益率
		}
		
		desc3 = MathUtil.getDescriptiveStatistics(yData3);
		desc4 = MathUtil.getDescriptiveStatistics(yData4);
		
		// 的中率
		series = chart.addSeries("的中率", yData3);
		series.setMarker(SeriesMarkers.NONE);
		series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
		series.setYAxisGroup(0);

		// 収益率
		series = chart.addSeries("収益率", yData4);
		series.setMarker(SeriesMarkers.NONE);
		series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
		series.setLineColor(Color.RED);
		series.setYAxisGroup(1);

		return chart;
	}
}

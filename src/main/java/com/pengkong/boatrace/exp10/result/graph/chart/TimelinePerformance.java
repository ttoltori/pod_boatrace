package com.pengkong.boatrace.exp10.result.graph.chart;

import org.apache.commons.lang3.ArrayUtils;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.Styler.YAxisPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pengkong.boatrace.exp10.result.graph.XYGraphData;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.common.MathUtil;

/**
 * 時系列推移
 * group1 : 累積的中率、累積収益率
 * group2 : 確定オッズ、的中オッズ、直前オッズ
 * @author ttolt
 *
 */
public class TimelinePerformance extends AbstractChart {

	public TimelinePerformance(ResultStat stat) {
		super(stat);
	}

	@Override
	protected String createTitleLabel() {
		String label = "(的中,収益,変動幅) rate (bet=" + MathUtil.scale2(stat.betrate) + " hit=" + MathUtil.scale3(stat.hitrate)
		  + " in=" + MathUtil.scale3(stat.incomerate) + ")";
		
		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label = "slope (bet=" + MathUtil.scale3(dto.getBetrSlope()) + " hit=" + MathUtil.scale3(dto.getHitrSlope())
		  + " income=" + MathUtil.scale3(dto.getIncrSlope()) + ")" ;
		
		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		XYChart chart = new XYChartBuilder().width(SIZE_X).height(SIZE_Y).build();
		chart.getStyler().setXAxisMaxLabelCount(12);
		chart.getStyler().setLegendPosition(LegendPosition.InsideN);
		chart.getStyler().setYAxisGroupPosition(0, YAxisPosition.Left);
		chart.getStyler().setYAxisGroupPosition(1, YAxisPosition.Right);
		
		double[] yData;
		XYSeries series;

		// group 1 (変動幅)
		// 確定オッズ
//		yData = ArrayUtils.toPrimitive(stat.listRor.toArray(new Double[stat.listRor.size()]));
//		series = chart.addSeries("確定オッズ:1", yData);
//		series.setMarker(SeriesMarkers.NONE);
//		//series.setXYSeriesRenderStyle(XYSeriesRenderStyle.Area);
//		series.setYAxisGroup(1);		

		// 的中オッズ(x軸は確定オッズの数に合わせてscaleする。数が確定オッズより足りないため）
		double xScaleFactor = (double)stat.listRor.size() /  (double)stat.listHitOdds.size();
		XYGraphData xyData = XYGraphData.getScaledXCounterYData(stat.listHitOdds, xScaleFactor);
		series = chart.addSeries("的中オッズ:1", xyData.getXdataArray(), xyData.getYdataArray());		
		series.setMarker(SeriesMarkers.NONE);
		series.setYAxisGroup(1);		
		
		// 直前オッズ(最後の直前オッズが有意味であれば）
		if (stat.listBor.size() > 0) {
			yData = ArrayUtils.toPrimitive(stat.listBor.toArray(new Double[stat.listBor.size()]));
			series = chart.addSeries("直前オッズ:1", yData);
			series.setMarker(SeriesMarkers.NONE);
			series.setYAxisGroup(1);
		}
		
		// group 0
		// 的中率
		yData = ArrayUtils.toPrimitive(stat.listHitrate.toArray(new Double[stat.listHitrate.size()]));
		series = chart.addSeries("的中率:0", yData);
		series.setMarker(SeriesMarkers.NONE);
		series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
		series.setYAxisGroup(0);
	    
		// 収益率
		yData = ArrayUtils.toPrimitive(stat.listIncomerate.toArray(new Double[stat.listIncomerate.size()]));
		series = chart.addSeries("収益率:0", yData);
		series.setMarker(SeriesMarkers.NONE);
		series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
		series.setYAxisGroup(0);

		return chart;
	}
}

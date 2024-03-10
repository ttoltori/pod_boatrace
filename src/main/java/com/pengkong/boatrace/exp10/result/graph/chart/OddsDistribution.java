package com.pengkong.boatrace.exp10.result.graph.chart;

import java.util.List;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pengkong.boatrace.exp10.result.graph.XYGraphData;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 確定オッズ、的中オッズ、直前オッズの分布
 * 
 * @author ttolt
 *
 */
public class OddsDistribution extends AbstractChart {

	public OddsDistribution(ResultStat stat) {
		super(stat);
	}

	@Override
	protected String createTitleLabel() {
		return "的中 (min=" + dto.getHoddsMin() + " max=" + dto.getHoddsMax() + " mea=" + dto.getHoddsMean() + " std="
				+ dto.getHoddsStddev() + " med=" + dto.getHoddsMedian() + ")";
	}

	@Override
	protected String createBottomLabel() {
		String label ="";
		
		if (stat.listBor.size() > 0) { // 直前オッズがあれば
			label = "直前 (min=" + dto.getBoddsMin() + " max=" + dto.getBoddsMax() + " mea=" + dto.getBoddsMean() + " std="
			+ dto.getBoddsStddev() + " med=" + dto.getBoddsMedian() + ")";
		}

		label += "確定 (min=" + dto.getRoddsMin() + " max=" + dto.getRoddsMax() + " mea=" + dto.getRoddsMean() + " std="
		+ dto.getRoddsStddev() + " med=" + dto.getRoddsMedian() + ")";
		
		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		XYChart chart = new XYChartBuilder().width(SIZE_X).height(SIZE_Y).build();

		chart.getStyler().setLegendPosition(LegendPosition.InsideN);
		
		// add series オッズ分布
		// 最小、最大オッズ範囲を的中オッズに合わせるためのバッファ
		List<Double> listLimitOdds;

		XYGraphData xyData;
		XYSeries series;

		// 確定オッズ
		listLimitOdds = XYGraphData.getLimitedList(stat.listRor, dto.getHoddsMin(), dto.getHoddsMax());
		xyData = XYGraphData.createSortedXYCountdata(listLimitOdds);
		series = chart.addSeries("確定odds", xyData.getXdataArray(), xyData.getYdataArray());
		series.setMarker(SeriesMarkers.NONE);

		// 的中オッズ
		xyData = XYGraphData.createSortedXYCountdata(stat.listHitOdds);
		series = chart.addSeries("的中odds", xyData.getXdataArray(), xyData.getYdataArray());
		series.setMarker(SeriesMarkers.NONE);

		// 直前オッズ
		if (stat.listBor.size() > 0) {
			listLimitOdds = XYGraphData.getLimitedList(stat.listBor, dto.getHoddsMin(), dto.getHoddsMax());
			xyData = XYGraphData.createSortedXYCountdata(listLimitOdds);
			series = chart.addSeries("直前odds", xyData.getXdataArray(), xyData.getYdataArray());
			series.setMarker(SeriesMarkers.NONE);
		}

		return chart;
		
	}

}

package com.pengkong.boatrace.exp10.result.graph.chart;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 *確定オッズ期待値の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class ExpRorkRangePerformance extends AbstractRangePerformanceChart {

	public ExpRorkRangePerformance(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapExpRorStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		String label;
		label = "確定オッズ期待値 BEST(" + dto.getExprorBestmin() + "～" + dto.getExprorBestmax() + ", " + dto.getExprorBetcnt() + ", "
				+ (dto.getExprorHitamt() - dto.getExprorBetamt()) + ")  ";
		label += "率(" + dto.getExprorBetrate() + ", " + dto.getExprorHitrate() + ", " + dto.getExprorIncomerate() + ")  ";
		label += "ALL(" + dto.getExprorMin() + "～" + dto.getExprorMax() + ", " + (int) stat.sumOfBet + ")";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label;
		label = "ALL STAT(mea=" + dto.getExprorMean() + " std=" + dto.getExprorStddev() + " med=" + dto.getExprorMedian()
				+ ")";

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.GRAY);
	}
}

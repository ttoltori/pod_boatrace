package com.pengkong.boatrace.exp10.result.graph.chart;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズ期待値の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class ExpBorRangePerformance extends AbstractRangePerformanceChart {

	public ExpBorRangePerformance(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapExpBorStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		String label;
		label = "直前オッズ期待値 BEST(" + dto.getExpborBestmin() + "～" + dto.getExpborBestmax() + ", " + dto.getExpborBetcnt() + ", "
				+ (dto.getExpborHitamt() - dto.getExpborBetamt()) + ")  ";
		label += "率(" + dto.getExpborBetrate() + ", " + dto.getExpborHitrate() + ", " + dto.getExpborIncomerate() + ")  ";
		label += "ALL(" + dto.getExpborMin() + "～" + dto.getExpborMax() + ", " + (int) stat.sumOfBet + ")";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label;
		label = "ALL STAT(mea=" + dto.getExpborMean() + " std=" + dto.getExpborStddev() + " med=" + dto.getExpborMedian()
				+ ")";

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.GRAY);
	}
}

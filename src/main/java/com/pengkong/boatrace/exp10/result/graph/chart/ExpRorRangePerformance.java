package com.pengkong.boatrace.exp10.result.graph.chart;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 *確定オッズrank期待値の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class ExpRorRangePerformance extends AbstractRangePerformanceChart {

	public ExpRorRangePerformance(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapExpRorkStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		String label;
		label = "確定オッズランク期待値 BEST(" + dto.getExprorkBestmin() + "～" + dto.getExprorkBestmax() + ", " + dto.getExprorkBetcnt() + ", "
				+ (dto.getExprorkHitamt() - dto.getExprorkBetamt()) + ")  ";
		label += "率(" + dto.getExprorkBetrate() + ", " + dto.getExprorkHitrate() + ", " + dto.getExprorkIncomerate() + ")  ";
		label += "ALL(" + dto.getExprorkMin() + "～" + dto.getExprorkMax() + ", " + (int) stat.sumOfBet + ")";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label;
		label = "ALL STAT(mea=" + dto.getExprorkMean() + " std=" + dto.getExprorkStddev() + " med=" + dto.getExprorkMedian()
				+ ")";

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.GRAY);
	}
}

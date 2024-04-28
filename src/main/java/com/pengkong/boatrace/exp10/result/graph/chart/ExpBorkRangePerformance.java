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
public class ExpBorkRangePerformance extends AbstractRangePerformanceChart {

	public ExpBorkRangePerformance(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapExpBorkStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		String label;
		label = "直前オッズランク期待値 BEST(" + dto.getExpborkBestmin() + "～" + dto.getExpborkBestmax() + ", " + dto.getExpborkBetcnt() + ", "
				+ (dto.getExpborkHitamt() - dto.getExpborkBetamt()) + ")  ";
		label += "率(" + dto.getExpborkBetrate() + ", " + dto.getExpborkHitrate() + ", " + dto.getExpborkIncomerate() + ")  ";
		label += "ALL(" + dto.getExpborkMin() + "～" + dto.getExpborkMax() + ", " + (int) stat.sumOfBet + ")";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label;
		label = "ALL STAT(mea=" + dto.getExpborkMean() + " std=" + dto.getExpborkStddev() + " med=" + dto.getExpborkMedian()
				+ ")";

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.GRAY);
	}
}

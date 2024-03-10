package com.pengkong.boatrace.exp10.result.graph.chart;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 予想確率の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class ProbabilityRangePerformance extends AbstractRangePerformanceChart {

	public ProbabilityRangePerformance(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapProbStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		String label;
		label = "予想確率 BEST(" + dto.getPrBestmin() + "～" + dto.getPrBestmax() + ", " + dto.getPrBetcnt() + ", "
				+ (dto.getPrHitamt() - dto.getPrBetamt()) + ")  ";
		label += "率(" + dto.getPrBetrate() + ", " + dto.getPrHitrate() + ", " + dto.getPrIncomerate() + ")  ";
		label += "ALL(" + dto.getProbMin() + "～" + dto.getProbMax() + ", " + (int) stat.sumOfBet + ")";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label;
		label = "ALL STAT(mea=" + dto.getProbMean() + " std=" + dto.getProbStddev() + " med=" + dto.getProbMedian()
				+ ")";

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.GRAY);
	}
}

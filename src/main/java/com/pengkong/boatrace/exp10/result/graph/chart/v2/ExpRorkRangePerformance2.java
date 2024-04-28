package com.pengkong.boatrace.exp10.result.graph.chart.v2;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.graph.chart.AbstractRangePerformanceChart;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 確定オッズランク期待値の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class ExpRorkRangePerformance2 extends AbstractRangePerformanceChart {

	public ExpRorkRangePerformance2(ResultStat stat) {
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
		label = "L(" + rangeDto.getLrorkBestmin() + "～" + rangeDto.getLrorkBestmax() + ", " + rangeDto.getLrorkBetcnt() + ", " + (cnvNull(rangeDto.getLrorkHitamt()) - cnvNull(rangeDto.getLrorkBetamt())) + ") ";
		label += "(" + rangeDto.getLrorkBetrate() + ", " + rangeDto.getLrorkHitrate() + ", " + rangeDto.getLrorkIncomerate() + ")  ";

		label += "R(" + rangeDto.getRrorkBestmin() + "～" + rangeDto.getRrorkBestmax() + ", " + rangeDto.getRrorkBetcnt() + ", " + (cnvNull(rangeDto.getRrorkHitamt()) - cnvNull(rangeDto.getRrorkBetamt())) + ") ";
		label += "(" + rangeDto.getRrorkBetrate() + ", " + rangeDto.getRrorkHitrate() + ", " + rangeDto.getRrorkIncomerate() + ")";		

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.GRAY);
	}
}

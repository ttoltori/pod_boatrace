package com.pengkong.boatrace.exp10.result.graph.chart.v2;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.graph.chart.AbstractRangePerformanceChart;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズランク期待値の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class ExpBorkRangePerformance2 extends AbstractRangePerformanceChart {

	public ExpBorkRangePerformance2(ResultStat stat) {
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
		label = "L(" + rangeDto.getLborkBestmin() + "～" + rangeDto.getLborkBestmax() + ", " + rangeDto.getLborkBetcnt() + ", " + (cnvNull(rangeDto.getLborkHitamt()) - cnvNull(rangeDto.getLborkBetamt())) + ") ";
		label += "(" + rangeDto.getLborkBetrate() + ", " + rangeDto.getLborkHitrate() + ", " + rangeDto.getLborkIncomerate() + ")  ";

		label += "R(" + rangeDto.getRborkBestmin() + "～" + rangeDto.getRborkBestmax() + ", " + rangeDto.getRborkBetcnt() + ", " + (cnvNull(rangeDto.getRborkHitamt()) - cnvNull(rangeDto.getRborkBetamt())) + ") ";
		label += "(" + rangeDto.getRborkBetrate() + ", " + rangeDto.getRborkHitrate() + ", " + rangeDto.getRborkIncomerate() + ")";		

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.GRAY);
	}
}

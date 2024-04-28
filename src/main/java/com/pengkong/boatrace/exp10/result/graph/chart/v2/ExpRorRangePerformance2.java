package com.pengkong.boatrace.exp10.result.graph.chart.v2;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.graph.chart.AbstractRangePerformanceChart;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 確定オッズ期待値の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class ExpRorRangePerformance2 extends AbstractRangePerformanceChart {

	public ExpRorRangePerformance2(ResultStat stat) {
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
		label = "L(" + rangeDto.getLrorBestmin() + "～" + rangeDto.getLrorBestmax() + ", " + rangeDto.getLrorBetcnt() + ", " + (cnvNull(rangeDto.getLrorHitamt()) - cnvNull(rangeDto.getLrorBetamt())) + ") ";
		label += "(" + rangeDto.getLrorBetrate() + ", " + rangeDto.getLrorHitrate() + ", " + rangeDto.getLrorIncomerate() + ")  ";

		label += "R(" + rangeDto.getRrorBestmin() + "～" + rangeDto.getRrorBestmax() + ", " + rangeDto.getRrorBetcnt() + ", " + (cnvNull(rangeDto.getRrorHitamt()) - cnvNull(rangeDto.getRrorBetamt())) + ") ";
		label += "(" + rangeDto.getRrorBetrate() + ", " + rangeDto.getRrorHitrate() + ", " + rangeDto.getRrorIncomerate() + ")";		

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.GRAY);
	}
}

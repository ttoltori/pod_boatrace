package com.pengkong.boatrace.exp10.result.graph.chart.v2;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.graph.chart.AbstractRangePerformanceChart;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズの収益性分布（収益率、収益金額、Best Range）
 * @author ttolt
 *
 */
public class BeforeOddsRangePerformance2 extends AbstractRangePerformanceChart {
	Logger logger = LoggerFactory.getLogger(BeforeOddsRangePerformance2.class);

	public BeforeOddsRangePerformance2(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapBeforeOddsStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		String label;
		if (mapUnit.size() <= 0) {
			logger.warn("no before odds data." + stat);
			return  "直前オッズなし";
		}

		label = "直前オッズ BEST(" + dto.getBorBestmin() + "～" + dto.getBorBestmax() + ", " + dto.getBorBetcnt() + ", " + (cnvNull(dto.getBorHitamt()) - cnvNull(dto.getBorBetamt())) + ")  ";
		label += "率(" + dto.getBorBetrate() + ", " + dto.getBorHitrate() + ", " + dto.getBorIncomerate() + ")  ";
		label += "ALL(" + dto.getBoddsMin() + "～" + dto.getBoddsMax() +  ", " + (int)stat.sumOfBetBodds + ")";
		
		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}
		String label;
		label = "L(" + rangeDto.getLborBestmin() + "～" + rangeDto.getLborBestmax() + ", " + rangeDto.getLborBetcnt() + ", " + (cnvNull(rangeDto.getLborHitamt()) - cnvNull(rangeDto.getLborBetamt())) + ") ";
		label += "(" + rangeDto.getLborBetrate() + ", " + rangeDto.getLborHitrate() + ", " + rangeDto.getLborIncomerate() + ")  ";

		label += "R(" + rangeDto.getRborBestmin() + "～" + rangeDto.getRborBestmax() + ", " + rangeDto.getRborBetcnt() + ", " + (cnvNull(rangeDto.getRborHitamt()) - cnvNull(rangeDto.getRborBetamt())) + ") ";
		label += "(" + rangeDto.getRborBetrate() + ", " + rangeDto.getRborHitrate() + ", " + rangeDto.getRborIncomerate() + ")";		
		
		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.BLUE);
	}
}

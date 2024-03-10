package com.pengkong.boatrace.exp10.result.graph.chart;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズの収益性分布（収益率、収益金額、Best Range）
 * @author ttolt
 *
 */
public class BeforeOddsRangePerformance extends AbstractRangePerformanceChart {
	Logger logger = LoggerFactory.getLogger(BeforeOddsRangePerformance.class);

	public BeforeOddsRangePerformance(ResultStat stat) {
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

		label = "直前オッズ BEST(" + dto.getBorBestmin() + "～" + dto.getBorBestmax() + ", " + dto.getBorBetcnt() + ", " + (dto.getBorHitamt() - dto.getBorBetamt()) + ")  ";
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
		label = "ALL STAT(mea=" + dto.getBoddsMean() + " std=" + dto.getBoddsStddev() + " med=" + dto.getBoddsMedian() + ")";
		
		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.BLUE);
	}
}

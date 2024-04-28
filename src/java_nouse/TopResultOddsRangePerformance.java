package com.pengkong.boatrace.exp10.result.graph.chart;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * TOP確定オッズの直前オッズの収益性分布（収益率、収益金額、Best Range）
 * @author ttolt
 *
 */
public class TopResultOddsRangePerformance extends AbstractRangePerformanceChart {
	Logger logger = LoggerFactory.getLogger(TopResultOddsRangePerformance.class);

	public TopResultOddsRangePerformance(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapTopResultOddsStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		String label;
		if (mapUnit.size() <= 0) {
			logger.warn("no before odds data." + stat);
			return  "TOP確定オッズなし";
		}

		label = "TOP確定オッズ BEST(" + dto.getRorBestmin() + "～" + dto.getRorBestmax() + ", " + dto.getRorBetcnt() + ", " + (dto.getRorHitamt() - dto.getRorBetamt()) + ")  ";
		label += "率(" + dto.getRorBetrate() + ", " + dto.getRorHitrate() + ", " + dto.getRorIncomerate() + ")  ";
		label += "ALL(" + dto.getRoddsMin() + "～" + dto.getRoddsMax() +  ", " + (int)stat.sumOfBet + ")";
		
		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}
		String label;
		label = "ALL STAT(mea=" + dto.getRoddsMean() + " std=" + dto.getRoddsStddev() + " med=" + dto.getRoddsMedian() + ")";
		
		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.BLUE);
	}
}

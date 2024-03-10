package com.pengkong.boatrace.exp10.result.graph.chart;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズRANKの収益性分布（収益率、収益金額、Best Range）
 * @author ttolt
 *
 */
public class BeforeOddsRankRangePerformance extends AbstractRangePerformanceChart {
	Logger logger = LoggerFactory.getLogger(BeforeOddsRankRangePerformance.class);

	public BeforeOddsRankRangePerformance(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapBorkStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		String label;
		if (mapUnit.size() <= 0) {
			logger.warn("no before odds rank data." + stat);
			return  "直前オッズRANKなし";
		}

		label = "直前オッズRANK BEST(" + dto.getBorkBestmin() + "～" + dto.getBorkBestmax() + ", " + dto.getBorkBetcnt() + ", " + (dto.getBorkHitamt() - dto.getBorkBetamt()) + ")  ";
		label += "率(" + dto.getBorkBetrate() + ", " + dto.getBorkHitrate() + ", " + dto.getBorkIncomerate() + ")  ";
		label += "ALL(" + dto.getBoddsrkMin() + "～" + dto.getBoddsrkMax() +  ", " + (int)stat.sumOfBetBodds +  ", " + (dto.getHitamt()-dto.getBetamt()) + ")";
		
		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}
		String label;
		label = "ALL STAT(mea=" + dto.getBoddsrkMean() + " std=" + dto.getBoddsrkStddev() + " med=" + dto.getBoddsrkMedian() + ")";
		
		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart(Color.BLUE);
	}
}

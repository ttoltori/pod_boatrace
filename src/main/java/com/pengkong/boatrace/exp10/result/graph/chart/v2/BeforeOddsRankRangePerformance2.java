package com.pengkong.boatrace.exp10.result.graph.chart.v2;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.graph.chart.AbstractRangePerformanceChart;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズRANKの収益性分布（収益率、収益金額、Best Range）
 * @author ttolt
 *
 */
public class BeforeOddsRankRangePerformance2 extends AbstractRangePerformanceChart {
	Logger logger = LoggerFactory.getLogger(BeforeOddsRankRangePerformance2.class);

	public BeforeOddsRankRangePerformance2(ResultStat stat) {
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

		label = "Boddsrk BEST(" + dto.getBorkBestmin() + "～" + dto.getBorkBestmax() + ", " + dto.getBorkBetcnt() + ", " + (cnvNull(dto.getBorkHitamt()) - cnvNull(dto.getBorkBetamt())) + ")  ";
		label += "率(" + dto.getBorkBetrate() + ", " + dto.getBorkHitrate() + ", " + dto.getBorkIncomerate() + ")  ";
		//label += "ALL(" + dto.getBoddsrkMin() + "～" + dto.getBoddsrkMax() +  ", " + (int)stat.sumOfBetBeforeOdds +  ", " + dto.getIncomerate() +  ", " + (dto.getHitamt()-dto.getBetamt()) + ")";
		label += "ALL(" + (int)stat.sumOfBetBodds +  ", " + stat.betrateBodds +  ", " + stat.hitrateBodds + ", " + stat.incomerateBodds +  ", " + (stat.sumOfHitAmountBodds-stat.sumOfBetAmountBodds) + ")";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}
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
		return createRangePerformanceChart(Color.BLUE);
	}
}
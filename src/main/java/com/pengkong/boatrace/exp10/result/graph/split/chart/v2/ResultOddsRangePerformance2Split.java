package com.pengkong.boatrace.exp10.result.graph.split.chart.v2;

import java.util.List;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.graph.split.chart.AbstractRangePerformanceChartSplit;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズの収益性分布（収益率、収益金額、Best Range）
 * @author ttolt
 *
 */
public class ResultOddsRangePerformance2Split extends AbstractRangePerformanceChartSplit {

	public ResultOddsRangePerformance2Split(ResultStat stat, List<ResultStat> split) {
		super(stat, split);
		mapUnit = stat.mapResultOddsStatUnit;
		for (ResultStat statItem : split) {
			listMapUnit.add(statItem.mapResultOddsStatUnit);
		}
	}

	@Override
	protected String createTitleLabel() {
		String label;
		if (mapUnit.size() <= 0) {
			logger.warn("no result odds data." + stat);
			return  "確定オッズなし";
		}
		
		label = "確定オッズ BEST(" + dto.getRorBestmin() + "～" + dto.getRorBestmax() + ", " + dto.getRorBetcnt() + ", " + (cnvNull(dto.getRorHitamt()) - cnvNull(dto.getRorBetamt())) + ")  ";
		label += "率(" + dto.getRorBetrate() + ", " + dto.getRorHitrate() + ", " + dto.getRorIncomerate() + ")  ";
		label += "ALL(" + dto.getRoddsMin() + "～" + dto.getRoddsMax() +  ", " + (int)stat.sumOfBet + ")";
		
		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (stat.mapBeforeOddsStatUnit.size() <= 0) {
			return "";
		}
		
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
		return createRangePerformanceChart();
	}
}

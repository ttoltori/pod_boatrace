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
public class ResultOddsRankRangePerformance2Split extends AbstractRangePerformanceChartSplit {

	public ResultOddsRankRangePerformance2Split(ResultStat stat, List<ResultStat> split) {
		super(stat, split);
		mapUnit = stat.mapRorkStatUnit;
		for (ResultStat statItem : split) {
			listMapUnit.add(statItem.mapRorkStatUnit);
		}
	}

	@Override
	protected String createTitleLabel() {
		String label;
		if (mapUnit.size() <= 0) {
			logger.warn("no result odds rank data." + stat);
			return  "確定オッズRANKなし";
		}
		
		label = "確定オッズRANK BEST(" + dto.getRorkBestmin() + "～" + dto.getRorkBestmax() + ", " + dto.getRorkBetcnt() + ", " + (cnvNull(dto.getRorkHitamt()) - cnvNull(dto.getRorkBetamt())) + ")  ";
		label += "率(" + dto.getRorkBetrate() + ", " + dto.getRorkHitrate() + ", " + dto.getRorkIncomerate() + ")  ";
		label += "ALL(" + dto.getRoddsrkMin() + "～" + dto.getRoddsrkMax() +  ", " + (int)stat.sumOfBet + ")";
		
		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}
		
		String label;
		label = "L(" + rangeDto.getLrorkBestmin() + "～" + rangeDto.getLrorkBestmax() + ", " + rangeDto.getLrorkBetcnt() + ", " + ( cnvNull(rangeDto.getLrorkHitamt()) - cnvNull(rangeDto.getLrorkBetamt())) + ") ";
		label += "(" + rangeDto.getLrorkBetrate() + ", " + rangeDto.getLrorkHitrate() + ", " + rangeDto.getLrorkIncomerate() + ")  ";

		label += "R(" + rangeDto.getRrorkBestmin() + "～" + rangeDto.getRrorkBestmax() + ", " + rangeDto.getRrorkBetcnt() + ", " + (cnvNull(rangeDto.getRrorkHitamt()) - cnvNull(rangeDto.getRrorkBetamt())) + ") ";
		label += "(" + rangeDto.getRrorkBetrate() + ", " + rangeDto.getRrorkHitrate() + ", " + rangeDto.getRrorkIncomerate() + ")";		
		
		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart();
	}
}

package com.pengkong.boatrace.exp10.result.graph.chart.v2;

import java.awt.Color;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.graph.chart.AbstractRangePerformanceChart;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズの収益性分布（収益率、収益金額、Best Range）
 * @author ttolt
 *
 */
public class ResultOddsRankRangePerformance2 extends AbstractRangePerformanceChart {

	public ResultOddsRankRangePerformance2(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapRorkStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		String label;
		if (mapUnit.size() <= 0) {
			logger.warn("no result odds rank data." + stat);
			return  "確定オッズRANKなし";
		}
		
		label = "確定オッズRANK BEST(" + dto.getRorkBestmin() + "～" + dto.getRorkBestmax() + ", " + dto.getRorkBetcnt() + ", " + (dto.getRorkHitamt() - dto.getRorkBetamt()) + ")  ";
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
		return createRangePerformanceChart(Color.BLUE);
	}
}

package com.pengkong.boatrace.exp10.result.graph.split.chart.v2;

import java.util.List;

import org.knowm.xchart.internal.chartpart.Chart;

import com.pengkong.boatrace.exp10.result.graph.split.chart.AbstractRangePerformanceChartSplit;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 予想確率の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class ProbabilityRangePerformance2Split extends AbstractRangePerformanceChartSplit {

	public ProbabilityRangePerformance2Split(ResultStat stat, List<ResultStat> split) {
		super(stat, split);
		mapUnit = stat.mapProbStatUnit;
		for (ResultStat statItem : split) {
			listMapUnit.add(statItem.mapProbStatUnit);
		}
		
	}

	@Override
	protected String createTitleLabel() {
		String label;
		label = "予想確率 BEST(" + dto.getPrBestmin() + "～" + dto.getPrBestmax() + ", " + dto.getPrBetcnt() + ", "
				+ (cnvNull(dto.getPrHitamt()) - cnvNull(dto.getPrBetamt())) + ")  ";
		label += "率(" + dto.getPrBetrate() + ", " + dto.getPrHitrate() + ", " + dto.getPrIncomerate() + ")  ";
		label += "ALL(" + dto.getProbMin() + "～" + dto.getProbMax() + ", " + (int) stat.sumOfBet + ")";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label;
		label = "L(" + rangeDto.getLprBestmin() + "～" + rangeDto.getLprBestmax() + ", " + rangeDto.getLprBetcnt() + ", " + (cnvNull(rangeDto.getLprHitamt()) - cnvNull(rangeDto.getLprBetamt())) + ") ";
		label += "(" + rangeDto.getLprBetrate() + ", " + rangeDto.getLprHitrate() + ", " + rangeDto.getLprIncomerate() + ")  ";

		label += "R(" + rangeDto.getRprBestmin() + "～" + rangeDto.getRprBestmax() + ", " + rangeDto.getRprBetcnt() + ", " + (cnvNull(rangeDto.getRprHitamt()) - cnvNull(rangeDto.getRprBetamt())) + ") ";
		label += "(" + rangeDto.getRprBetrate() + ", " + rangeDto.getRprHitrate() + ", " + rangeDto.getRprIncomerate() + ")";		

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart();
	}
}

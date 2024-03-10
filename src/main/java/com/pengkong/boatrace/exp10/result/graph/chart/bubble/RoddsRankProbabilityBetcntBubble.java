package com.pengkong.boatrace.exp10.result.graph.chart.bubble;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;

/**
 * 直前オッズの収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class RoddsRankProbabilityBetcntBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(RoddsRankProbabilityBetcntBubble.class);

	public RoddsRankProbabilityBetcntBubble(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapRoddsRankProbStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no resultodds rank / probability data." + stat);
			return "確定オッズRANK/予想確率統計 Betcntなし";
		}

		String label;
		label = "Roddsrk/Prob Betcnt BEST(" + dto.getRorkBestmin() + "～" + dto.getRorkBestmax() + " / " + dto.getPrBestmin() + "～"
				+ dto.getPrBestmax() + ")  ";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}

		String label;
		label = "Roddsrk(mea=" + dto.getRoddsrkMean() + " std=" + dto.getRoddsrkStddev() + " med=" + dto.getRoddsrkMedian()
				+ ") ";
		label += "Prob(mea=" + dto.getProbMean() + " std=" + dto.getProbStddev() + " med=" + dto.getProbMedian()
				+ ")";

		return label;
	}

	@Override
	protected double getBubbleDataSurplus(RangeStatUnit unit) {
		return (double)unit.betCnt;
	}
	
	@Override
	protected double getBubbleDataDeficit(RangeStatUnit unit) {
		return (double)(unit.betCnt);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart();
	}
}

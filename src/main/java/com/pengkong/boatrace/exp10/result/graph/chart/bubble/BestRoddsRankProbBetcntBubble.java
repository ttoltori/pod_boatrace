package com.pengkong.boatrace.exp10.result.graph.chart.bubble;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.result.stat.XYRange;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;

/**
 * BEST確定オッズRANK / 確定オッズ の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class BestRoddsRankProbBetcntBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(BestRoddsRankProbBetcntBubble.class);

	public BestRoddsRankProbBetcntBubble(ResultStat stat, XYRange xyRange) {
		super(stat);
		mapUnit = stat.mapRoddsRankProbStatUnit;
		setXrangeMinMax(xyRange.xMin, xyRange.xMax);
		setYrangeMinMax(xyRange.yMin, xyRange.yMax);
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no resultodds rank / probability data." + stat);
			return "BEST確定オッズRANK/確率なし";
		}

		String label;
		label = "Best Roddsrk/Probability Betcnt BEST(" + dto.getRorkBestmin() + "～" + dto.getRorkBestmax() + " / " + dto.getPrBestmin() + "～"
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

package com.pengkong.boatrace.exp10.result.graph.chart.bubble;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズの収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class RoddsProbabilityBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(RoddsProbabilityBubble.class);

	public RoddsProbabilityBubble(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapRoddsProbStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no resultodds / probability data." + stat);
			return "確定オッズ/予想確率統計なし";
		}

		String label;
		label = "Rodds/Prob BEST(" + dto.getRorBestmin() + "～" + dto.getRorBestmax() + " / " + dto.getPrBestmin() + "～"
				+ dto.getPrBestmax() + ")  ";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}

		String label;
		label = "Rodds(mea=" + dto.getRoddsMean() + " std=" + dto.getRoddsStddev() + " med=" + dto.getRoddsMedian()
				+ ") ";
		label += "Prob(mea=" + dto.getProbMean() + " std=" + dto.getProbStddev() + " med=" + dto.getProbMedian()
				+ ")";

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart();
	}
}

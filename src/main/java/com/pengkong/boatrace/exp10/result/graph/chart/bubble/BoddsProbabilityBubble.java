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
public class BoddsProbabilityBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(BoddsProbabilityBubble.class);

	public BoddsProbabilityBubble(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapBoddsProbStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no beforeodds / probability data." + stat);
			return "直前オッズ/予想確率統計なし";
		}

		String label;
		label = "Bodds/Prob (" + dto.getBorBestmin() + "～" + dto.getBorBestmax() + " / " + dto.getPrBestmin() + "～"
				+ dto.getPrBestmax() + ")  ";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}

		String label;
		label = "Bodds(mea=" + dto.getBoddsMean() + " std=" + dto.getBoddsStddev() + " med=" + dto.getBoddsMedian()
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

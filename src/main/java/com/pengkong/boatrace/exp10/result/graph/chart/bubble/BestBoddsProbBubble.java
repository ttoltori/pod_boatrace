package com.pengkong.boatrace.exp10.result.graph.chart.bubble;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.result.stat.XYRange;

/**
 * BEST直前オッズRANK / 直前オッズ の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class BestBoddsProbBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(BestBoddsProbBubble.class);

	public BestBoddsProbBubble(ResultStat stat, XYRange xyRange) {
		super(stat);
		mapUnit = stat.mapBoddsProbStatUnit;
		setXrangeMinMax(xyRange.xMin, xyRange.xMax);
		setYrangeMinMax(xyRange.yMin, xyRange.yMax);
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no beforeodds / probability data." + stat);
			return "BEST直前オッズ/確率なし";
		}

		String label;
		label = "Best Bodds/Probability BEST(" + dto.getBorBestmin() + "～" + dto.getBorBestmax() + " / " + dto.getPrBestmin() + "～"
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

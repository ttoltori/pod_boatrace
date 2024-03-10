package com.pengkong.boatrace.exp10.result.graph.chart.bubble;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズRANK / 確定オッズRANK の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class BoddsRankRoddsRankBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(BoddsRankRoddsRankBubble.class);

	public BoddsRankRoddsRankBubble(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapBorkRorkStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no beforeodds rank / resultodds rank data." + stat);
			return "直前オッズRANK/確定オッズRANKなし";
		}

		String label;
		label = "Boddsrk/Roddsrk BEST(" + dto.getBorkBestmin() + "～" + dto.getBorkBestmax() + " / " + dto.getRorkBestmin() + "～"
				+ dto.getRorkBestmax() + ")  ";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}

		String label;
		label = "Boddsrk(mea=" + dto.getBoddsrkMean() + " std=" + dto.getBoddsrkStddev() + " med=" + dto.getBoddsrkMedian()
				+ ") ";
		label += "Roddsrk(mea=" + dto.getRoddsrkMean() + " std=" + dto.getRoddsrkStddev() + " med=" + dto.getRoddsrkMedian()
				+ ")";

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart();
	}
}

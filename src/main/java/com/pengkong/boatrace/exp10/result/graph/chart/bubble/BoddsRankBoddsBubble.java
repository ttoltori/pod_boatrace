package com.pengkong.boatrace.exp10.result.graph.chart.bubble;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;

/**
 * 直前オッズRANK / 直前オッズ の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class BoddsRankBoddsBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(BoddsRankBoddsBubble.class);

	public BoddsRankBoddsBubble(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapBorkBorStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no beforeodds rank / beforeodds rank data." + stat);
			return "直前オッズRANK/直前オッズなし";
		}

		String label;
		label = "Boddsrk/Bodds BEST(" + dto.getBorkBestmin() + "～" + dto.getBorkBestmax() + " / " + dto.getBorBestmin() + "～"
				+ dto.getBorBestmax() + ")  ";

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
		label += "Bodds(mea=" + dto.getBoddsMean() + " std=" + dto.getBoddsStddev() + " med=" + dto.getBoddsMedian()
				+ ")";

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart();
	}
}

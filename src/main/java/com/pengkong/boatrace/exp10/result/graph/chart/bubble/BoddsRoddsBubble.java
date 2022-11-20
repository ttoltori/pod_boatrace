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
public class BoddsRoddsBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(BoddsRoddsBubble.class);

	public BoddsRoddsBubble(ResultStat stat) {
		super(stat);
		mapUnit = stat.mapBorRorStatUnit;
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no beforeodds / resultodds data." + stat);
			return "直前オッズ/確定オッズなし";
		}

		String label;
		label = "Bodds/Rodds BEST(" + dto.getBorBestmin() + "～" + dto.getBorBestmax() + " / " + dto.getRorBestmin() + "～"
				+ dto.getRorBestmax() + ")  ";

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
		label += "Rodds(mea=" + dto.getRoddsMean() + " std=" + dto.getRoddsStddev() + " med=" + dto.getRoddsMedian()
				+ ")";

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart();
	}
}

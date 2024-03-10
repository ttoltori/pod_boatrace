package com.pengkong.boatrace.exp10.result.graph.chart.bubble;

import java.util.Map;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.result.stat.XYRange;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;

/**
 * BEST直前オッズRANK / 直前オッズ の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class BestBoddsRankBoddsBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(BestBoddsRankBoddsBubble.class);

	public BestBoddsRankBoddsBubble(ResultStat stat, XYRange xyRange) {
		super(stat);
		mapUnit = stat.mapBorkBorStatUnit;
		setXrangeMinMax(xyRange.xMin, xyRange.xMax);
		setYrangeMinMax(xyRange.yMin, xyRange.yMax);
	}

	public BestBoddsRankBoddsBubble(ResultStat stat, Map<String, RangeStatUnit> mapUnit, XYRange xyRange) {
		super(stat);
		super.mapUnit = mapUnit;
		setXrangeMinMax(xyRange.xMin, xyRange.xMax);
		setYrangeMinMax(xyRange.yMin, xyRange.yMax);
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no beforeodds rank / beforeodds rank data." + stat);
			return "BEST直前オッズRANK/直前オッズなし";
		}

		String label;
		label = "Best Boddsrk/Bodds BEST(" + dto.getBorkBestmin() + "～" + dto.getBorkBestmax() + " / " + dto.getBorBestmin() + "～"
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

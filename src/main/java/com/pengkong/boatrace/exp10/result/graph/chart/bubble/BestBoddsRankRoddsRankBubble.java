package com.pengkong.boatrace.exp10.result.graph.chart.bubble;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.result.stat.XYRange;

/**
 * BEST直前オッズRANK / 確定オッズRANK の収益性分布（収益率、収益金額、Best Range）
 * 
 * @author ttolt
 *
 */
public class BestBoddsRankRoddsRankBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(BestBoddsRankRoddsRankBubble.class);

	public BestBoddsRankRoddsRankBubble(ResultStat stat, XYRange xyRange) {
		super(stat);
		mapUnit = stat.mapBorkRorkStatUnit;
		setXrangeMinMax(xyRange.xMin, xyRange.xMax);
		setYrangeMinMax(xyRange.yMin, xyRange.yMax);
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no best beforeodds rank / resultodds rank data." + stat);
			return "BEST直前オッズRANK/確定オッズRANKなし";
		}

		String label;
		label = "Best Boddsrk/Roddsrk BEST(" + dto.getBorkBestmin() + "～" + dto.getBorkBestmax() + " / " + dto.getRorkBestmin() + "～"
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

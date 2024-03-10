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
public class SingleBestBoddsRankBoddsBubble extends AbstractDoubleRangeBubbleChart {
	Logger logger = LoggerFactory.getLogger(SingleBestBoddsRankBoddsBubble.class);

	public SingleBestBoddsRankBoddsBubble(ResultStat stat, Map<String, RangeStatUnit> mapUnit, XYRange xyRange) {
		super(stat);
		super.mapUnit = mapUnit;
		setXrangeMinMax(xyRange.xMin, xyRange.xMax);
		setYrangeMinMax(xyRange.yMin, xyRange.yMax);
		SIZE_X = 150;
		SIZE_Y = 1400;
	}

	@Override
	protected String createTitleLabel() {
		if (mapUnit.size() <= 0) {
			logger.warn("no beforeodds rank / beforeodds rank data." + stat);
			return "BEST直前オッズRANK/直前オッズなし";
		}

		String label;
		int rank = getXRangeMax().intValue();
		label = rank + " [" + String.join(", ",
					String.valueOf(((int[])borkDto.getBetcnt())[rank-1]),
					String.valueOf(((int[])borkDto.getIncamt())[rank-1])
				) + "]";

		return label;
	}

	@Override
	protected String createBottomLabel() {
		if (mapUnit.size() <= 0) {
			return "";
		}

		String label;
		int rank = getXRangeMax().intValue();
		label = "[" + String.join(", ", 
				String.valueOf( ((double[])borkDto.getBetrate())[rank-1] ), 
				String.valueOf(((double[])borkDto.getHitrate())[rank-1]), 
				String.valueOf(((double[])borkDto.getIncomerate())[rank-1])) + "]"; 

		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		return createRangePerformanceChart();
	}
}

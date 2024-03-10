package com.pengkong.boatrace.exp10.result.graph.chart;

import org.apache.commons.lang3.ArrayUtils;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.MathUtil;

/**
 * 時系列の残高推移。
 * @author ttolt
 *
 */
public class TimelineBalance extends AbstractChart {

	public TimelineBalance(ResultStat stat) {
		super(stat);
	}

	@Override
	protected String createTitleLabel() {
		String label = "残高(";
		for (int i = 0; i < stat.balanceSplit.length; i++) {
			label += (stat.balanceSplit[i] + " "); 
		}
		label += ")";
		
		label += " 坂(";
		for (int i = 0; i < stat.balanceSlopeSplit.length; i++) {
			label += (MathUtil.scale1(stat.balanceSlopeSplit[i]) + " "); 
		}
		label += ")";
		
		label += " 率(" + MathUtil.scale2(stat.betrate) + " " + MathUtil.scale2(stat.hitrate)
		  + " " + MathUtil.scale2(stat.incomerate) + ")";
		return label;
	}

	@Override
	protected String createBottomLabel() {
		String label = "残高(" + stat.balance  + ") ";
		label += "BET(cnt=" + stat.sumOfBet + " rate=" + MathUtil.scale2(stat.betrate) + ") ";
		label += "率坂(bet=" + MathUtil.scale2(dto.getBetrSlope()) + " hit=" + MathUtil.scale2(dto.getHitrSlope())
		  + " income=" + MathUtil.scale2(dto.getIncrSlope()) + ")" ;
		
		return label;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Chart createChart() {
		XYChart chart = new XYChartBuilder().width(SIZE_X).height(SIZE_Y).build();
		chart.getStyler().setLegendPosition(LegendPosition.InsideN);
		
		// 月数 = (期間日数 / 区間数) / 30
		int monthCount = BoatUtil.daysBetween(prop.getString("result_start_ymd"), prop.getString("result_end_ymd")) / prop.getInteger("split") / 30;
		chart.getStyler().setXAxisMaxLabelCount(monthCount);
		
		double[] yData;
		XYSeries series;
		for (int i = 0; i < stat.arrBalancelist.length; i++) {
			yData = ArrayUtils.toPrimitive(stat.arrBalancelist[i].toArray(new Double[stat.arrBalancelist[i].size()]));
			// 1000円単位に変換
			for (int j = 0; j < yData.length; j++) {
				yData[j] = yData[j] / 1000;
			}
			series = chart.addSeries("term" + (i+1), yData);
			series.setMarker(SeriesMarkers.NONE);
		}
		
		return chart;
	}
}

package com.pengkong.boatrace.exp10.result.graph.chart.term;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.exception.NoDataException;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pengkong.boatrace.exp10.result.graph.chart.AbstractChart;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.common.MathUtil;

/**
 * 期間単位の時系列の残高推移。
 * @author ttolt
 *
 */
public class TermTimelineBalance extends AbstractChart {
	/** regression失敗時のエラーslope値 */
	static double RG_ERROR_VALUE = -0.999;

	public List<Double> listBalance = new ArrayList<>();
	public Double balance = 0.0;
	public Double slope = 0.0;
	
	public TermTimelineBalance(ResultStat stat) {
		super(stat);
	}

	@Override
	protected String createTitleLabel() {
		String label = stat.termDays + "日別 残高( " + balance + " )";
		
		label += " 坂( " + MathUtil.scale2(slope) + " )";
		
		label += " 率( " + MathUtil.scale2(stat.betrate) + "  " + MathUtil.scale2(stat.hitrate)
		  + " " + MathUtil.scale2(stat.incomerate) + " )";
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
		// balance list
		for (RangeStatUnit unit : stat.termStatUnit.values()) {
			 balance += unit.getIncome();
			 listBalance.add(balance);
		}
		// balance regression
		double[] regression;
		try {
			regression = MathUtil.getRegression(listBalance);
			slope = regression[0]; // slope (interceptは無視する)
		} catch (NoDataException e) {
			slope = RG_ERROR_VALUE; // regression error value
		}
		
		XYChart chart = new XYChartBuilder().width(SIZE_X).height(SIZE_Y).build();
		chart.getStyler().setLegendPosition(LegendPosition.InsideN);
		
		double[] yData;
		XYSeries series;
		yData = ArrayUtils.toPrimitive(listBalance.toArray(new Double[listBalance.size()]));
		// 1000円単位に変換
		for (int j = 0; j < yData.length; j++) {
			yData[j] = yData[j] / 1000;
		}
		series = chart.addSeries("term", yData);
		series.setMarker(SeriesMarkers.NONE);
		
		return chart;
	}
}

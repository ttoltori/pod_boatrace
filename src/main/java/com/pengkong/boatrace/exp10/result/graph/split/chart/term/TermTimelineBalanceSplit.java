package com.pengkong.boatrace.exp10.result.graph.split.chart.term;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.exception.NoDataException;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.pengkong.boatrace.exp10.result.graph.split.chart.AbstractChartSplit;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.common.MathUtil;

/**
 * 期間単位の時系列の残高推移。
 * @author ttolt
 *
 */
public class TermTimelineBalanceSplit extends AbstractChartSplit {
	/** regression失敗時のエラーslope値 */
	static double RG_ERROR_VALUE = -0.999;

	
	public TermTimelineBalanceSplit(ResultStat stat, List<ResultStat> split) {
		super(stat, split);
	}

	@Override
	protected String createTitleLabel() {
		List<Double> listBalanceAll = new ArrayList<>();
		Double balanceAll = 0.0;
		Double slopeAll = 0.0;
		
		// balance list
		for (RangeStatUnit unit : stat.termStatBalance.values()) {
			 balanceAll += unit.getIncome();
			 listBalanceAll.add(balanceAll);
		}
		// balance regression
		double[] regression;
		try {
			regression = MathUtil.getRegression(listBalanceAll);
			slopeAll = regression[0]; // slope (interceptは無視する)
		} catch (NoDataException e) {
			slopeAll = RG_ERROR_VALUE; // regression error value
		}
		
		String label = stat.termDays + "日別 残高( " + balanceAll + " )";
		
		label += " 坂( " + MathUtil.scale3(slopeAll) + " )";
		
		label += " 率( " + MathUtil.scale2(stat.betrate) + "  " + MathUtil.scale2(stat.hitrate)
		  + " " + MathUtil.scale2(stat.incomerate) + " )";

		label += " TERM( " + stat.termEvaluation.getTermcnt() + "  " + stat.termEvaluation.getPluscnt()
		  + " " + stat.termEvaluation.getPlusrate() + " )";
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
		chart.getStyler().setLegendVisible(false);
		
		for (int i = 0; i < statSplit.size(); i++) {
			List<Double> listBalance = new ArrayList<>();
			List<Double> listHline = new ArrayList<>();
			Double balance = 0.0;
			ResultStat stat = statSplit.get(i);
			for (RangeStatUnit unit : stat.termStatBalance.values()) {
				 balance += unit.getIncome();
				 listBalance.add(balance);
				 listHline.add(0.0);
			}

			// draw horizontal line
			if (i == 0) {
				double[] yData;
				XYSeries series;
				yData = ArrayUtils.toPrimitive(listHline.toArray(new Double[listHline.size()]));
				series = chart.addSeries("term" + i, yData);
				series.setMarker(SeriesMarkers.NONE);
				series.setMarkerColor(colors[i]);
				series.setLineColor(colors[i]);
				series.setLineWidth(5);
			}
			
			double[] yData;
			XYSeries series;
			yData = ArrayUtils.toPrimitive(listBalance.toArray(new Double[listBalance.size()]));
			series = chart.addSeries("term" + i+1, yData);
			series.setMarker(SeriesMarkers.CIRCLE);
			series.setMarkerColor(colors[i]);
			series.setLineColor(colors[i+1]);
			series.setLineWidth(3);
		}
		
		return chart;
	}
}

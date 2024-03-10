package com.pengkong.boatrace.exp10.result.graph.chart.bubble;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.BubbleChartBuilder;
import org.knowm.xchart.BubbleSeries;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.graph.chart.AbstractChart;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;

import lombok.Getter;

public abstract class AbstractDoubleRangeBubbleChart extends AbstractChart {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	protected Map<String, RangeStatUnit> mapUnit;

	private Double xRangeMin;
	@Getter
	private Double xRangeMax;
	
	private Double yRangeMin;
	private Double yRangeMax;
	
	//private Double xAxisMax;
	public AbstractDoubleRangeBubbleChart(ResultStat stat) {
		super(stat);
	}

	protected void setXrangeMinMax(Double min, Double max) {
		this.xRangeMin = min;
		this.xRangeMax = max;
	}

	protected void setYrangeMinMax(Double min, Double max) {
		this.yRangeMin = min;
		this.yRangeMax = max;
	}

	protected boolean isValidXRange(Double value) {
		if (xRangeMin == null) {
			return true;
		}
		
		return ( (value >= xRangeMin && value <= xRangeMax));
	}

	protected boolean isValidYRange(Double value) {
		if (yRangeMin == null) {
			return true;
		}
		
		return ( (value >= yRangeMin && value <= yRangeMax));
	}

	protected boolean isValidXYrange(Double xValue, Double yValue) {
		return (isValidXRange(yValue) && isValidYRange(yValue));
	}

	//	protected void setXAxisMax(Double xAxisMax) {
//		this.xAxisMax = xAxisMax;
//	}
	
	@SuppressWarnings("rawtypes")
	protected Chart createRangePerformanceChart() {
		// 黒字
		List<Number> xData1 = new ArrayList<>();
		List<Number> yData1 = new ArrayList<>();
		List<Number> bubbleData1 = new ArrayList<>();
		
		// 赤字
		List<Number> xData2 = new ArrayList<>();
		List<Number> yData2 = new ArrayList<>();
		List<Number> bubbleData2 = new ArrayList<>();
		
		List<RangeStatUnit> units = new ArrayList<>(mapUnit.values());
		for (RangeStatUnit unit : units) {
			Double[] factorValues = unit.factorValues();
			// xRange外はスキップ
			if (!isValidXRange(factorValues[0])) {
				continue;
			}
			
			if (!isValidYRange(factorValues[1])) {
				continue;
			}

			if (unit.getIncome() > 0) {
				xData1.add(factorValues[0]); // [before, reult] odds
				yData1.add(factorValues[1]); // probability
				bubbleData1.add(getBubbleDataSurplus(unit));
			} else {
				xData2.add(factorValues[0]); // [before, result] odds
				yData2.add(factorValues[1]); // probability
				bubbleData2.add(getBubbleDataDeficit(unit));
			}
		}
		
		BubbleChart chart = buildChart();
		chart.getStyler().setAxisTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		//chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
	    chart.getStyler().setLegendLayout(Styler.LegendLayout.Vertical);
		chart.getStyler().setLegendVisible(false);

//		Double maxBeforeOdds = Double.valueOf(prop.getString("beforeodds_max_" + prop.getString("bettype")));
//		if (this.xAxisMax != null) {
//			chart.getStyler().setXAxisMax(this.xAxisMax); //指定値maxで表示制限する
//		} else {
//			chart.getStyler().setXAxisMax(dto.getHoddsMax()); //的中オッズmaxで表示制限する	
//		}

		// series1의 데이터가 없으면 series2가 series1의 색깔로 표시되어 버리기때문에 
		if (xData1.size() <= 0 || yData1.size() <= 0) {
			xData1.add(1.0); yData1.add(1.0); bubbleData1.add(1.0);
		}
		
	    if (xData2.size() > 0) {
	    	BubbleSeries series = chart.addSeries("赤字", xData2, yData2, bubbleData2);
		    //series.setFillColor(Color.WHITE);
		    //series.setLineColor(Color.WHITE);
		    
	    }

	    if (xData1.size() > 0) {
		    BubbleSeries series = chart.addSeries("黒字", xData1, yData1, bubbleData1);
		    //series.setFillColor(null);
		    //series.setFillColor(Color.BLUE);
		    series.setLineColor(Color.WHITE);
	    } 

	    
		return chart;
	}
	
	protected double getBubbleDataSurplus(RangeStatUnit unit) {
		return unit.getIncome() / 50;
	}
	
	protected double getBubbleDataDeficit(RangeStatUnit unit) {
		return unit.getIncome() / 50 * -1;
	}
	
	protected BubbleChart buildChart() {
		return new BubbleChartBuilder().width(SIZE_X).height(SIZE_Y).build();
	}
}

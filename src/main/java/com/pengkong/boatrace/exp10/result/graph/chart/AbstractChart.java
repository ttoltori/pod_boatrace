package com.pengkong.boatrace.exp10.result.graph.chart;

import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.util.MLDescriptiveStatistics;
import com.pengkong.boatrace.mybatis.entity.MlBorkEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlRangeEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlRorkEvaluation;
import com.pengkong.boatrace.util.BoatUtil;

@SuppressWarnings("rawtypes")
public abstract class AbstractChart {
	protected Logger logger = LoggerFactory.getLogger(AbstractChart.class);
	
	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	MLDescriptiveStatistics mld = MLDescriptiveStatistics.getInstance();
	
	protected int SIZE_X = 700;
	protected int SIZE_Y = 700;
	
	/** 統計データ */
	protected ResultStat stat;

	/** 統計結果のMlEvaluation */
	protected MlEvaluation dto;

	/** BORK-BORのMlBorkEvaluation */
	protected MlBorkEvaluation borkDto;
	
	protected MlRorkEvaluation rorkDto;
	
	protected MlRangeEvaluation rangeDto;
	
	public AbstractChart(ResultStat stat) {
		super();
		this.stat = stat;
		this.dto = stat.evaluation;
		this.borkDto = stat.borkEvaluation;
		this.rangeDto = stat.rangeEvaluation;
		this.rorkDto = stat.rorkEvaluation;
		SIZE_X = Integer.parseInt(prop.getString("chart_size_x"));
		SIZE_Y = Integer.parseInt(prop.getString("chart_size_y"));
	}

	public Chart create() {
		Chart chart = createChart();
		chart.setTitle(createTitleLabel());
		chart.setXAxisTitle(createBottomLabel());
		
		return chart;
	}

	/** 上段タイトルメッセージ */
	protected abstract String createTitleLabel();
	
	/** 下段Xaxisメッセージ */
	protected abstract String createBottomLabel();

	/** チャート生成 */
	protected abstract Chart createChart();
	
	/**
	 * NullPointerException予防 (収益金額計算時) 
	 * @param value1
	 * @param value2
	 * @return
	 */
	protected Integer cnvNull(Integer value1) {
		if (value1 == null) {
			return -1;
		}
		return (value1);
	}
	
	protected int calculateLineWidth() {
		return 3;
	}
}

package com.pengkong.boatrace.exp10.result.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.internal.chartpart.Chart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.exp10.enums.ResultType;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.graph.chart.BeforeOddsRangePerformance;
import com.pengkong.boatrace.exp10.result.graph.chart.BeforeOddsRankRangePerformance;
import com.pengkong.boatrace.exp10.result.graph.chart.ProbabilityRangePerformance;
import com.pengkong.boatrace.exp10.result.graph.chart.TimelineBalance;
import com.pengkong.boatrace.exp10.result.graph.chart.TimelinePerformance;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BestBoddsRankBoddsBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BestBoddsRankProbBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BestBoddsRankRoddsRankBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BestRoddsRankProbBetcntBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BestRoddsRankProbBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsProbabilityBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRankBoddsBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRankProbabilityBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRankRoddsRankBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRoddsBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.RoddsRankProbabilityBetcntBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.RoddsRankProbabilityBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.term.TermTimelineBalance;
import com.pengkong.boatrace.exp10.result.graph.chart.term.TermTimelineBetcntHicnt;
import com.pengkong.boatrace.exp10.result.graph.chart.term.TermTimelineHitrateIncomerate;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.BeforeOddsRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.BeforeOddsRankRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ProbabilityRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ResultOddsRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ResultOddsRankRangePerformance2;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.result.stat.XYRange;
import com.pengkong.common.FileUtil;
import com.pengkong.common.MathUtil;
import com.pengkong.common.StringUtil;

/**
 * 実験結果の統計値をグラフで出力する。
 * properties: result_no, used_model_no, dir_result
 * 
 * @author ttolt
 *
 */
public class ResultGraphBuilder {
	Logger logger = LoggerFactory.getLogger(ResultGraphBuilder.class);
	
	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	public String dirProperty;
	
	public ResultGraphBuilder() {
	}

	/**
	 * 統計情報をgraphでファイル出力する
	 * 
	 * @param mapStat key = bettype+kumiban  value=ResultStat
	 */
	@SuppressWarnings("rawtypes")
	public void save(SortedMap<String, ResultStat> mapStat) throws Exception {
		String exNo = prop.getString("result_no"); // 実験番号
		String resultType = prop.getString("result_type"); // 実験タイプ

		// 既存実験番号のディレクトリを削除
		String dirAllResult = prop.getString(dirProperty);

		// 全ResultStatのgraphをファイルに出力する
		String filePath;
		String filePathBork;
		for (ResultStat stat : mapStat.values()) {
			if (stat.evaluation == null) { // Evaluationが作成されていない場合(的中が一つもない）
				continue;
			}

			List<Chart> listChart = new ArrayList<>();
			
			// 実験番号・パタン別の結果ディレクトリ
			// String dirExResult = dirAllResult + StringUtil.leftPad(exNo, BoatConst.LEFT_PAD, "0") + "/" + prop.getString("pattern_name") + "/" + stat.pattern + "/";
			// 実験番号 bettype パタンIDの結果ディレクトリ
			String exnoPath = StringUtil.leftPad(exNo, BoatConst.LEFT_PAD6, "0");
			String dirExResult;
			if ( resultType.startsWith(ResultType._1.getValue()) || resultType.startsWith(ResultType._2.getValue())  || resultType.startsWith(ResultType._3.getValue()) ) { // result typeが1で始まる
				//exnoPath = exnoPath + "_" + resultType  + "_" + prop.getString("used_model_no") + "_" + prop.getString("pattern_id");
				//exnoPath = exnoPath + "_" + resultType  + "_" + prop.getString("pattern_id");
				exnoPath = resultType  + "_" + prop.getString("pattern_id");

				//dirExResult = dirAllResult + exnoPath  + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				//dirExResult = dirAllResult + exnoPath  + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				dirExResult = dirAllResult + stat.statBettype + "/" + stat.kumiban + "/" + exnoPath  + "/";

				String filePathCommon = dirExResult + String.join("_", 
						prop.getString("result_type"),
						stat.statBettype, 
						stat.kumiban,
						prop.getString("pattern_id"), 
						stat.pattern, 
						prop.getString("used_model_no"),
						StringUtil.leftPad(exNo, BoatConst.LEFT_PAD6, "0")
						);
				filePath = filePathCommon + ".png";
				filePathBork = filePathCommon + "_bork.png";
			} else if (resultType.startsWith(ResultType._4.getValue()) || 
					resultType.startsWith(ResultType._5.getValue()))  { // // result type 4 = simulation1_*
				//dirExResult = dirAllResult + stat.statBettype + "/" + stat.kumiban + "/";
				// 임시 20220815
				dirExResult = dirAllResult + prop.getString("grade_type") // + "_" + prop.getString("models") 
						 + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				
				//prop.putProperty("evaluations_id", prop.getString("evaluations_id").replace("kumiban", stat.kumiban));
				String filePathCommon = dirExResult + prop.getString("evaluations_id");
/*				
				String.join("_",
						prop.getString("result_no"),
						stat.statBettype,
						stat.kumiban, 
						prop.getString("factor"), 
						prop.getString("limit"), 
						prop.getString("incr"), 
						prop.getString("group_sql_id", "x"),
						prop.getString("term"),
						prop.getString("result_type"),  
						prop.getString("models"), 
						prop.getString("grade_type"), 
						prop.getString("probability_type"),
						
						prop.getString("bonus_pr"), prop.getString("bonus_bor"), prop.getString("bonus_bork")
						);
*/						
				filePath = filePathCommon + ".png";
				filePathBork = filePathCommon + "_bork.png";
			} else   { // simul_last 最終確認
				dirExResult = dirAllResult + prop.getString("result_no") + "/";
				String filePathCommon = dirExResult + String.join("_",
						prop.getString("result_no"),
						prop.getString("result_type"), prop.getString("term"), 
						stat.statBettype,
						stat.kumiban 
						);
				filePath = filePathCommon + ".png";
				filePathBork = filePathCommon + "_bork.png";
				
			}
			
			FileUtil.createDirIfNotExist(dirExResult);
			
			filePath = filePath.replaceAll("\\*", "@");
			
			// BORK-BOR 詳細マップのXY範囲
			Double borkMin = prop.getDouble("bork_min");
			Double borkMax = prop.getDouble("bork_max");
			XYRange xyRange = new XYRange(borkMin, borkMax, null, null);

			String graphType = prop.getString("graph_type");
			if (graphType.equals("1")) { // graph types
				listChart.add(new TimelineBalance(stat).create());
				listChart.add(new BeforeOddsRangePerformance(stat).create());
				listChart.add(new BeforeOddsRankRangePerformance(stat).create());

				listChart.add(new ProbabilityRangePerformance(stat).create());
				listChart.add(new BoddsRoddsBubble(stat).create());
				listChart.add(new BoddsRankRoddsRankBubble(stat).create());

				BitmapEncoder.saveBitmap(listChart, 2, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("2")) { // graph type
				listChart.add(new TimelineBalance(stat).create());
				listChart.add(new BeforeOddsRangePerformance(stat).create());
				listChart.add(new BeforeOddsRankRangePerformance(stat).create());

				listChart.add(new ProbabilityRangePerformance(stat).create());
				listChart.add(new BestBoddsRankBoddsBubble(stat, xyRange).create());
				listChart.add(new BestBoddsRankRoddsRankBubble(stat, xyRange).create());

				listChart.add(new BoddsRoddsBubble(stat).create());
				listChart.add(new BoddsRankBoddsBubble(stat).create());
				listChart.add(new BoddsRankRoddsRankBubble(stat).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("3")) { 
				listChart.add(new TimelineBalance(stat).create());
				listChart.add(new BeforeOddsRangePerformance(stat).create());
				listChart.add(new BeforeOddsRankRangePerformance(stat).create());

				listChart.add(new TimelinePerformance(stat).create());
				listChart.add(new BestBoddsRankBoddsBubble(stat, xyRange).create());
				listChart.add(new BestBoddsRankRoddsRankBubble(stat, xyRange).create());

				listChart.add(new ProbabilityRangePerformance(stat).create());
				listChart.add(new BoddsProbabilityBubble(stat).create());
				listChart.add(new BoddsRankProbabilityBubble(stat).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("4")) {
				listChart.add(new TimelineBalance(stat).create());
				listChart.add(new ProbabilityRangePerformance2(stat).create());
				listChart.add(new BoddsRankProbabilityBubble(stat).create());
				
				listChart.add(new BeforeOddsRankRangePerformance2(stat).create());
				listChart.add(new BeforeOddsRangePerformance2(stat).create());
				//listChart.add(new BoddsProbabilityBubble(stat).create());
				listChart.add(new BestBoddsRankBoddsBubble(stat, xyRange).create());
				
				listChart.add(new ResultOddsRankRangePerformance2(stat).create());
				listChart.add(new ResultOddsRangePerformance2(stat).create());
				// listChart.add(new BestBoddsRankProbBubble(stat, xyRange).create());
				listChart.add(new BestBoddsRankRoddsRankBubble(stat, xyRange).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("5")) {  // probabilityとroddsrankの分布を見る
				listChart.add(new TimelineBalance(stat).create());
				listChart.add(new ProbabilityRangePerformance2(stat).create());
				//listChart.add(new RoddsProbabilityBubble(stat).create());
				listChart.add(new BestBoddsRankProbBubble(stat, xyRange).create());
				
				listChart.add(new BeforeOddsRankRangePerformance2(stat).create());
				listChart.add(new BeforeOddsRangePerformance2(stat).create());
				listChart.add(new BestRoddsRankProbBubble(stat, xyRange).create());
				
				listChart.add(new ResultOddsRankRangePerformance2(stat).create());
				listChart.add(new ResultOddsRangePerformance2(stat).create());
				listChart.add(new BestRoddsRankProbBetcntBubble(stat, xyRange).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("6")) {  // probabilityとboddsrankの分布を見る
				listChart.add(new TermTimelineBalance(stat).create());
				listChart.add(new ProbabilityRangePerformance2(stat).create());
				//listChart.add(new BestBoddsRankProbBubble(stat, xyRange).create());
				listChart.add(new TimelinePerformance(stat).create());
				
				listChart.add(new BeforeOddsRankRangePerformance2(stat).create());
				listChart.add(new BeforeOddsRangePerformance2(stat).create());
				//listChart.add(new BestBoddsRankRoddsRankBubble(stat, xyRange).create());
				listChart.add(new TermTimelineHitrateIncomerate(stat).create());
				
				listChart.add(new ResultOddsRankRangePerformance2(stat).create());
				listChart.add(new ResultOddsRangePerformance2(stat).create());
				//listChart.add(new BestBoddsRankRoddsRankBetcntBubble(stat, xyRange).create());
				listChart.add(new TermTimelineBetcntHicnt(stat).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("7")) {  // 指定日間隔単位の時系列の性能推移をみる
				listChart.add(new TermTimelineBalance(stat).create());
				listChart.add(new ProbabilityRangePerformance2(stat).create());
				listChart.add(new TimelinePerformance(stat).create());
				//listChart.add(new TermTimelineBetcntIncome(stat).create());
				
				listChart.add(new ResultOddsRankRangePerformance2(stat).create());
				listChart.add(new ResultOddsRangePerformance2(stat).create());
				listChart.add(new TermTimelineHitrateIncomerate(stat).create());

				listChart.add(new RoddsRankProbabilityBubble(stat).create());
				listChart.add(new RoddsRankProbabilityBetcntBubble(stat).create());
				listChart.add(new TermTimelineBetcntHicnt(stat).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else {
				throw new IllegalStateException("Undefined graph type " + graphType);
			}
			logger.info("graph saved. " + filePath);
			
		}
	}

	public static void main(String[] args) {
		System.out.println(MathUtil.scale3(-100.0));
	}
}

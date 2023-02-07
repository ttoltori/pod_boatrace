package com.pengkong.boatrace.exp10.result.graph.split;

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
import com.pengkong.boatrace.exp10.result.graph.chart.TimelinePerformance;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRankProbabilityBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.RoddsProbabilityBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.RoddsRankProbabilityBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.term.TermTimelineBalance;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.BeforeOddsRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.BeforeOddsRankRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ProbabilityRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ResultOddsRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ResultOddsRankRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.split.chart.term.TermTimelineBalanceSplit;
import com.pengkong.boatrace.exp10.result.graph.split.chart.v2.BeforeOddsRangePerformance2Split;
import com.pengkong.boatrace.exp10.result.graph.split.chart.v2.BeforeOddsRankRangePerformance2Split;
import com.pengkong.boatrace.exp10.result.graph.split.chart.v2.ProbabilityRangePerformance2Split;
import com.pengkong.boatrace.exp10.result.graph.split.chart.v2.ResultOddsRangePerformance2Split;
import com.pengkong.boatrace.exp10.result.graph.split.chart.v2.ResultOddsRankRangePerformance2Split;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.common.FileUtil;
import com.pengkong.common.MathUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * 実験結果の統計値をグラフで出力する。
 * properties: result_no, used_model_no, dir_result
 * 
 * @author ttolt
 *
 */
public class ResultGraphBuilderSplit {
	Logger logger = LoggerFactory.getLogger(ResultGraphBuilderSplit.class);
	
	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	public String dirProperty;
	
	public ResultGraphBuilderSplit() {
	}

	/**
	 * 統計情報をgraphでファイル出力する
	 * 
	 * @param mapStat key = bettype+kumiban  value=ResultStat
	 */
	@SuppressWarnings("rawtypes")
	public void save(SortedMap<String, ResultStat> mapStat, HashMapList<ResultStat> mapStatSplit) throws Exception {
		String exNo = prop.getString("result_no"); // 実験番号
		String resultType = prop.getString("result_type"); // 実験タイプ

		// 既存実験番号のディレクトリを削除
		String dirAllResult = prop.getString(dirProperty);

		// 全ResultStatのgraphをファイルに出力する
		String filePath;
		for (String mapKey : mapStat.keySet()) {
			ResultStat stat = mapStat.get(mapKey);
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
						StringUtil.leftPad(exNo, BoatConst.LEFT_PAD6, "0"),
						String.valueOf(stat.getDailyBetcnt())
						);
				filePath = filePathCommon + ".png";
			} else if (resultType.startsWith(ResultType._4.getValue()) || 
					resultType.startsWith(ResultType._5.getValue()))  { // // result type 4 = simulation1_*
				dirExResult = dirAllResult + prop.getString("grade_type") // + "_" + prop.getString("models") 
						 + "/" + stat.statBettype + "/" + stat.kumiban + "/";

				
				String evaluationsId = prop.getString("evaluations_id");
				
				// 統計単位がkumibanでかつ全組番のsimulationの場合組番文字列を書き換える 2023/1/24 
				if (!stat.kumiban.equals("")  && evaluationsId.contains("*-*-*")) {
					evaluationsId = evaluationsId.replace("*-*-*", stat.kumiban);
				}
				
				String filePathCommon = null;
				if (resultType.startsWith(ResultType._4.getValue())) {
					filePathCommon = dirExResult + evaluationsId + "_" + String.valueOf(stat.getDailyBetcnt());
				} else {
					filePathCommon = dirAllResult + evaluationsId + "_" + String.valueOf(stat.getDailyBetcnt());
				}
				
				filePath = filePathCommon + ".png";
			} else   { // simul_last 最終確認
				dirExResult = dirAllResult + prop.getString("result_no") + "/";
				String filePathCommon = dirExResult + String.join("_",
						prop.getString("result_no"),
						prop.getString("result_type"), prop.getString("term"), 
						stat.statBettype,
						stat.kumiban 
						);
				filePath = filePathCommon + ".png";
			}
			
			// 臨時 2023/1/29 comment out  
			//FileUtil.createDirIfNotExist(dirExResult);
			
			filePath = filePath.replaceAll("\\*", "@");
			
			String graphType = prop.getString("graph_type");
			List<ResultStat> statSplit = mapStatSplit.get(mapKey);
			if (graphType.equals("1")) {  // 分割result出力
				listChart.add(new TermTimelineBalance(stat).create());
				listChart.add(new ProbabilityRangePerformance2Split(stat, statSplit).create());
				listChart.add(new ProbabilityRangePerformance2(stat).create());
				
				listChart.add(new ResultOddsRankRangePerformance2Split(stat, statSplit).create());
				listChart.add(new ResultOddsRankRangePerformance2(stat).create());
				listChart.add(new RoddsRankProbabilityBubble(stat).create());
				
				listChart.add(new ResultOddsRangePerformance2Split(stat, statSplit).create());
				listChart.add(new ResultOddsRangePerformance2(stat).create());
				listChart.add(new RoddsProbabilityBubble(stat).create());
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("2")) {  // 分割simulation出力
				listChart.add(new TermTimelineBalance(stat).create());
				listChart.add(new ProbabilityRangePerformance2Split(stat, statSplit).create());
				listChart.add(new ProbabilityRangePerformance2(stat).create());
				
				listChart.add(new BeforeOddsRankRangePerformance2Split(stat, statSplit).create());
				listChart.add(new BeforeOddsRankRangePerformance2(stat).create());
				listChart.add(new ResultOddsRankRangePerformance2(stat).create());

				listChart.add(new BeforeOddsRangePerformance2Split(stat, statSplit).create());
				listChart.add(new BeforeOddsRangePerformance2(stat).create());
				listChart.add(new ResultOddsRangePerformance2(stat).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("3")) {  // 単一simulation出力
				listChart.add(new TermTimelineBalance(stat).create());
				listChart.add(new ProbabilityRangePerformance2(stat).create());
				listChart.add(new TimelinePerformance(stat).create());
				
				listChart.add(new BeforeOddsRankRangePerformance2(stat).create());
				listChart.add(new BeforeOddsRangePerformance2(stat).create());
				listChart.add(new BoddsRankProbabilityBubble(stat).create());

				listChart.add(new ResultOddsRankRangePerformance2(stat).create());
				listChart.add(new ResultOddsRangePerformance2(stat).create());
				listChart.add(new RoddsRankProbabilityBubble(stat).create());
				
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

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
import com.pengkong.boatrace.common.enums.Delimeter;
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
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRankProbabilityBetcntBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRankProbabilityBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRankRoddsRankBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRoddsBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.RoddsRankProbabilityBetcntBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.RoddsRankProbabilityBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.term.TermTimelineBalance;
import com.pengkong.boatrace.exp10.result.graph.chart.term.TermTimelineBetcntHicnt;
import com.pengkong.boatrace.exp10.result.graph.chart.term.TermTimelineBetcntIncome;
import com.pengkong.boatrace.exp10.result.graph.chart.term.TermTimelineHitrateIncomerate;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.BeforeOddsRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.BeforeOddsRankRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ExpBorRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ExpBorkRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ExpRorRangePerformance2;
import com.pengkong.boatrace.exp10.result.graph.chart.v2.ExpRorkRangePerformance2;
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

				//dirExResult = dirAllResult + exnoPath  + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				//dirExResult = dirAllResult + exnoPath  + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				//dirExResult = dirAllResult + stat.statBettype + "/" + stat.kumiban + "/" + exnoPath  + "/";
				//dirExResult = dirAllResult + resultType + "/" + stat.statBettype + "/" + stat.kumiban  + "/" + stat.patternId  + "/";
				//dirExResult = dirAllResult + prop.getString("evaluations_id")  + "_" + resultType + "/" + stat.statBettype + "/" + stat.kumiban  + "/";
				//dirExResult = dirAllResult + resultType + "/" + prop.getString("used_model_no") + "/" + stat.statBettype + "/" + stat.kumiban  + "/";
				//dirExResult = dirAllResult + resultType + "/" + stat.statBettype + "/" + stat.kumiban  + "/";
				//dirExResult = dirAllResult + resultType + "/" + prop.getString("pattern_id") + "/" + stat.statBettype + "/" + stat.kumiban  + "/";
				//dirExResult = dirAllResult + resultType + "/" + stat.statBettype + "/" + stat.kumiban  + "/";
				//dirExResult = dirAllResult + resultType + "/" + stat.statBettype + "/" + prop.getString("used_model_no")  + "/" + prop.getString("pattern_id") + "/";
				dirExResult = dirAllResult + "/" + exNo + "/" + resultType + "/" + stat.statBettype + "/";
				
				String filePathCommon = dirExResult + String.join("_", 
						prop.getString("result_type"),
						stat.statBettype, 
						stat.kumiban,
						prop.getString("pattern_id"), 
						stat.pattern, 
						prop.getString("used_model_no"),
						prop.getString("evaluations_id"),
						//prop.getString("limited_formation"),
						//StringUtil.leftPad(exNo, BoatConst.LEFT_PAD6, "0"),
						String.valueOf(stat.getDailyIncome()), String.valueOf(stat.getDailyHitrate()), String.valueOf(stat.getDailyBetcnt())
						);
				filePath = filePathCommon + ".png";
			} else if (resultType.startsWith(ResultType._4.getValue()) || 
					resultType.startsWith(ResultType._5.getValue())  || resultType.startsWith(ResultType._6.getValue())
					)  { 
				// 4=step1 result_config6,  5=step2 수동, 6=step2 자동
				//dirExResult = dirAllResult + stat.statBettype + "/" + stat.kumiban + "/";
				// 임시 20220815
				//dirExResult = dirAllResult + prop.getString("group_sql_id") + "/" + prop.getString("grade_type") + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				//dirExResult = dirAllResult + prop.getString("group_sql_id") + "_" + prop.getString("factor_type") + "_" + prop.getString("modelno") +   
				//		"/" + prop.getString("grade_type") + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				// step 2 자동
				// dirExResult = dirAllResult  + prop.getString("grade_type") + "/" + stat.statBettype + "/" + stat.kumiban + "/"
				//  + String.join("_", prop.getString("ranking"), prop.getString("group_sql_id"), prop.getString("factor"), prop.getString("simul_custom"))  + "/";
				//dirExResult = dirAllResult  + prop.getString("grade_type") + "/" + stat.statBettype + "/" + prop.getString("simul_patternid")+ "/";
				//dirExResult = dirAllResult  + prop.getString("grade_type") + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				//String factor = prop.getString("factor").split("/")[0];
				//dirExResult = dirAllResult  + factor + "/" + prop.getString("grade_type") + "/" + stat.statBettype + "/" + stat.kumiban + "/" + prop.getString("simul_patternid")+ "/";
				//dirExResult = dirAllResult  + prop.getString("grade_type") + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				//dirExResult = dirAllResult  + "31xxx" + "/" + prop.getString("grade_type") + "/" + stat.statBettype + "/" + stat.kumiban + "/";
				
				//dirExResult = dirAllResult + prop.getString("groups") + "/" + prop.getString("grade_type") + "/" +  prop.getString("bettype") + "/" + stat.kumiban + "/" ;
				
				dirExResult = dirAllResult + prop.getString("grade_type") + "/"  +  prop.getString("stat_bettype") + "/" + stat.kumiban + "/"; 
						//String.join("-", prop.getString("factor"), prop.getString("cond_range")) + "/";
						//String.join("-", prop.getString("factor"), prop.getString("cond_range"), prop.getString("limit"),prop.getString("models"),prop.getString("group_sql_id")) + "/";
				// VIC dirExResult = dirAllResult + prop.getString("grade_type") + "/" +  prop.getString("bettype") + "/" + stat.kumiban + "/" + String.join("-", prop.getString("factor_no"), prop.getString("factor")) + "/";
				//dirExResult = dirAllResult + prop.getString("grade_type") + "/" + stat.statBettype + "/" + stat.kumiban + "/" + prop.getString("group_sql_id") + "/";

				//simulation時に複数のResultStatのbettype, kumibanを代入する
				prop.evalIdBettype = stat.statBettype;
				prop.evalIdKumiban = stat.kumiban;
				String evaluationsId = prop.getString("evaluations_id");
				
				// tsvファイルの複数のシミュレーションをまとめて実行するための条件として
				// pattern_type=literal, pattern_fields=ptid
				evaluationsId = evaluationsId.replace("{patternid}", stat.pattern);
				
				
				// 統計単位がkumibanでかつ全組番のsimulationの場合組番文字列を書き換える 2023/1/24
//				if (!stat.kumiban.equals("")  && evaluationsId.contains("1234-*-*")) {
//					evaluationsId = evaluationsId.replace("1234-*-*", stat.kumiban);
//				}
				
				String filePathCommon = null;
				if (resultType.startsWith(ResultType._9.getValue())) { // step 2 수동
					filePathCommon = dirAllResult + String.join("_", evaluationsId,
							String.valueOf(stat.getDailyIncome()), String.valueOf(stat.getDailyHitrate()), String.valueOf(stat.getDailyBetcnt()) );
							//evaluationsId + "_" + String.valueOf(stat.getIncome()) + "_" + String.valueOf(stat.getDailyBetcnt());
				} else {
					filePathCommon = dirExResult + String.join("_", evaluationsId,  
							String.valueOf(stat.getDailyIncome()), String.valueOf(stat.getDailyHitrate()), String.valueOf(stat.getDailyBetcnt()) );
							//evaluationsId + "_" + String.valueOf(stat.getIncome()) + "_" + String.valueOf(stat.getDailyBetcnt());
				}
				
				filePath = filePathCommon + ".png";
			} else   { // simul_last 最終確認
				dirExResult = dirAllResult 
						+ String.join(Delimeter.UNDERBAR.getValue(), prop.getString("result_no"), prop.getString("group_no"), prop.getString("sql_type"), prop.getString("stat_unit"))
						+ "/" + prop.getString("grade_type") + "/";
				String filePathCommon = dirExResult + String.join("_",
						prop.getString("result_no"), prop.getString("result_type"), prop.getString("result_start_ymd") + "~" + prop.getString("result_end_ymd"),
						stat.statBettype,
						stat.kumiban,
						String.valueOf(stat.getDailyIncome()), String.valueOf(stat.getDailyHitrate()), String.valueOf(stat.getDailyBetcnt())
						);
				filePath = filePathCommon + ".png";
				
			}
			
			if (resultType.startsWith(ResultType._9.getValue())) {
				FileUtil.createDirIfNotExist(dirAllResult);
			} else {
				FileUtil.createDirIfNotExist(dirExResult);
			}
			
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
				listChart.add(new TermTimelineBalance(stat).create());
				listChart.add(new ProbabilityRangePerformance2(stat).create());
				listChart.add(new BoddsRankProbabilityBubble(stat).create());
				
				listChart.add(new BeforeOddsRankRangePerformance2(stat).create());
				listChart.add(new BeforeOddsRangePerformance2(stat).create());
				listChart.add(new BoddsRankProbabilityBetcntBubble(stat).create());

				
				listChart.add(new ResultOddsRankRangePerformance2(stat).create());
				listChart.add(new BoddsProbabilityBubble(stat).create());
				listChart.add(new BoddsRankBoddsBubble(stat).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("5")) {  // probabilityとroddsrankの分布を見る 
				listChart.add(new TermTimelineBalance(stat).create());
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
				listChart.add(new TermTimelineBetcntIncome(stat).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("8")) {
				listChart.add(new TermTimelineBalance(stat).create());
				listChart.add(new ProbabilityRangePerformance2(stat).create());
				listChart.add(new ExpBorRangePerformance2(stat).create());
				
				listChart.add(new BeforeOddsRankRangePerformance2(stat).create());
				listChart.add(new BeforeOddsRangePerformance2(stat).create());
				listChart.add(new ExpRorRangePerformance2(stat).create());

				
				listChart.add(new ResultOddsRankRangePerformance2(stat).create());
				listChart.add(new ExpBorkRangePerformance2(stat).create());
				listChart.add(new ExpRorkRangePerformance2(stat).create());
				
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

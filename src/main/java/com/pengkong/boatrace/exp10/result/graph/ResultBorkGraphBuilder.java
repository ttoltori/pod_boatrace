package com.pengkong.boatrace.exp10.result.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BestBoddsRankBoddsBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BestBoddsRankRoddsRankBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRankBoddsBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRankRoddsRankBubble;
import com.pengkong.boatrace.exp10.result.graph.chart.bubble.BoddsRoddsBubble;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.result.stat.XYRange;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
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
public class ResultBorkGraphBuilder {
	Logger logger = LoggerFactory.getLogger(ResultBorkGraphBuilder.class);
	
	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	public String dirProperty;
	
	private static class InstanceHolder {
		private static final ResultBorkGraphBuilder INSTANCE = new ResultBorkGraphBuilder();
	}
	
	public static ResultBorkGraphBuilder getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public ResultBorkGraphBuilder() {
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
			if ( resultType.startsWith(ResultType._1.getValue()) ) { // result typeが1で始まる
				exnoPath = exnoPath + "_" + prop.getString("used_model_no");
				dirExResult = dirAllResult + exnoPath + "_" + prop.getString("pattern_id") + "/" + stat.statBettype + "/" + stat.kumiban + "/";

				filePath = dirExResult + StringUtil.leftPad(exNo, BoatConst.LEFT_PAD6, "0") + "_" + stat.pattern + "_" + stat.statBettype + "_" + stat.kumiban + ".png";
			} else if (resultType.startsWith(ResultType._2.getValue()) ) { // result typeが2で始まる
				exnoPath = exnoPath + "_"
						+ String.join("-", resultType,
								StringUtil.leftPad(prop.getString("groups"), BoatConst.LEFT_PAD6, "0"), stat.statBettype,
								stat.kumiban);
				dirExResult = dirAllResult + exnoPath + "_" 
						+ prop.getString("pattern_type") + "_" 
						+ prop.getString("pattern_fields")  + "_"
						+ prop.getString("range_selector").replaceAll("\\|", "!") + "_" 
						+ prop.getString("bonus_pr") + "_"
						+ prop.getString("bonus_bor") + "_"
						+ prop.getString("bonus_bork")
						+ "/";
				
				filePath = dirExResult + StringUtil.leftPad(exNo, BoatConst.LEFT_PAD6, "0") + "_" + stat.pattern + "_" + stat.statBettype + "_" + stat.kumiban + ".png";
			} else { // // result type 4,41, 3, 31
				dirExResult = dirAllResult + stat.statBettype + "/" + stat.kumiban + "/";
				filePath = dirExResult + String.join("_",
						prop.getString("result_no"),
						prop.getString("result_type"),
						prop.getString("groups"),
						prop.getString("used_model_no"),
						prop.getString("pattern_id"),
						stat.statBettype,
						stat.kumiban
						);
			}

			FileUtil.createDirIfNotExist(dirExResult);
			
			//logger.debug(exNo + " graphing ResultStat:" + stat);
			String graphType = prop.getString("graph_type");
			if (graphType.equals("1")) { // result types
				listChart.add(new TimelineBalance(stat).create());
				listChart.add(new BeforeOddsRangePerformance(stat).create());
				listChart.add(new BeforeOddsRankRangePerformance(stat).create());

				listChart.add(new ProbabilityRangePerformance(stat).create());
				listChart.add(new BoddsRoddsBubble(stat).create());
				listChart.add(new BoddsRankRoddsRankBubble(stat).create());

				BitmapEncoder.saveBitmap(listChart, 2, 3, filePath, BitmapFormat.PNG);
			} else if (graphType.equals("2")) { // result type
				listChart.add(new TimelineBalance(stat).create());
				listChart.add(new BeforeOddsRangePerformance(stat).create());
				listChart.add(new BeforeOddsRankRangePerformance(stat).create());

				Double borkMin = prop.getDouble("bork_min");
				Double borkMax = prop.getDouble("bork_max");
				XYRange xyRange = new XYRange(borkMin, borkMax, null, null);
				
				listChart.add(new BoddsRoddsBubble(stat).create());
				listChart.add(new BestBoddsRankBoddsBubble(stat, xyRange).create());
				listChart.add(new BestBoddsRankRoddsRankBubble(stat, xyRange).create());

				listChart.add(new BoddsRoddsBubble(stat).create());
				listChart.add(new BoddsRankBoddsBubble(stat).create());
				listChart.add(new BoddsRankRoddsRankBubble(stat).create());
				
				BitmapEncoder.saveBitmap(listChart, 3, 3, filePath, BitmapFormat.PNG);
			} else {
				throw new IllegalStateException("Undefined graph type " + graphType);
			}
			
			//logger.debug("graph end:" + stat);
			logger.info("graph saved. " + filePath);
		}
	}

	public void saveBorkStat(ResultStat stat) throws Exception {
		
		
		
		
		Map<String, RangeStatUnit> mapStat = stat.mapBorkBorStatUnit; 
		Map<Double, Map<String, RangeStatUnit>> mapBorkBorStatByBork = splitMapStatByBork(mapStat);
		
		List<Chart> listChart = new ArrayList<>();
		// bork loop ex) 1~10
		XYRange xyRange = new XYRange(null,null,null,null);
		for (Double bork : mapBorkBorStatByBork.keySet()) {
			xyRange.xMin = bork;
			xyRange.xMax = bork;
			listChart.add(new BestBoddsRankBoddsBubble(stat, mapStat, xyRange).create());
		}
		
	}
	
	/**
	 * 
	 * @param mapStat stat.mapBorkBorStatUnit
	 * @throws Exception
	 */
	Map<Double, Map<String, RangeStatUnit>> splitMapStatByBork(Map<String, RangeStatUnit> mapStat) throws Exception {
		// BorkBor マップをBork別に分割する
		SortedMap<Double, Map<String, RangeStatUnit>> mapBorkStat = new TreeMap<>();

		Double borkMax = prop.getDouble("bork_max");
		// factor = bork_bor ex) 1_1.6
		for (String factor : mapStat.keySet()) {
			Double bork = Double.valueOf(factor.split(Delimeter.UNDERBAR.getValue())[0]);
			// borkが指定値より大きい場合はスキップ
			if (bork > borkMax) {
				continue;
			}
			
			Map<String, RangeStatUnit> mapBorkStatItem = mapBorkStat.get(bork);
			if (mapBorkStatItem == null) {
				mapBorkStatItem = new TreeMap<>();
				mapBorkStat.put(bork, mapBorkStatItem);
			}
			
			mapBorkStatItem.put(factor, mapStat.get(factor));
		}
		
		return mapBorkStat;
	}

	public static void main(String[] args) {
		System.out.println(MathUtil.scale3(-100.0));
	}
}

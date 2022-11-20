package com.pengkong.boatrace.exp10.simulation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.exp10.simulation.evaluation.EvaluationSet;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.common.collection.HashMapList;

/**
 * レース毎のsimulationによる評価結果を保持するクラス.
 * 
 * @author ttolt
 *
 */
public class SimulationInfo {
	public String ymd;
	public String jyoCd;
	public String raceNo;
	public String betType;
	public String prediction;
	public List<String> predictions;
	public List<String> selectors;
	/** 個別MlResultを出力するときに使う */
	public int betAmt = 0;
	public int hitAmt = 0;
	public int incAmt = 0;
	
	/** probability_selectorが適用された後の全てのevaluation */
	public List<Evaluation> evaluations;
	public EvaluationSet evSet;
	
	public SimulationInfo(String ymd, String jyoCd, String raceNo, String betType, String prediction,
			HashMapList<Evaluation> mapListKumibanEvaluations, String... selectors) {
		this.ymd = ymd;
		this.jyoCd = jyoCd;
		this.raceNo = raceNo;
		this.betType = betType;
		this.prediction = prediction;
		this.selectors = Arrays.asList(selectors);
		parse(prediction, mapListKumibanEvaluations);
	}

	void parse(String prediction, HashMapList<Evaluation> mapListKumibanEvaluations) {
		predictions = mapListKumibanEvaluations.keySet().stream().sorted().collect(Collectors.toList());
		
		//当該組番のevaluationsを取得する。
		evaluations = mapListKumibanEvaluations.get(prediction);
		evSet = new EvaluationSet(evaluations);
	}

	public void setResult(MlResult res) { 
		betAmt = res.getBetamt();
		hitAmt = res.getHitamt();
		incAmt = hitAmt - betAmt;
	}
	
	public String toString() {
		String result = String.join("\t", "[" + String.join(",", ymd, jyoCd, raceNo) + "]", betType, prediction, 
				String.valueOf(predictions.size()), predictions.toString(), selectors.toString(),
				String.valueOf(betAmt), String.valueOf(hitAmt), String.valueOf(incAmt)
			);
		
		return result;
	}
}

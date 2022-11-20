package com.pengkong.boatrace.exp10.simulation.range.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;

public class RangeValidationProvider {
	private static final String PR = "pr"; // probability range 
	private static final String BOR = "bor"; // before odds range 
	private static final String BORK = "bork"; // before odds rank range 
	
	BeforeOddsRangeValidationProvider borValidator;
	BeforeOddsRankRangeValidationProvider borkValidator;
	ProbabilityRangeValidationProvider prValidator;
	
	HashMap<String, JexlExpression> mapExpr;
	
	public RangeValidationProvider() {
		initialize();
	}
	
	/** 初期化 */
	void initialize() {
		mapExpr = new HashMap<>();
		
		borValidator = new BeforeOddsRangeValidationProvider();
		borkValidator = new BeforeOddsRankRangeValidationProvider();
		prValidator = new ProbabilityRangeValidationProvider();
	}
	
	/** 
	 *  提供された直前オッズと予想確率を基にevaluationのvalidationを行い、結果が妥当なものをリストで返却する
	 * @param beforeOdds 直前オッズ
	 * @param probability 予想確率
	 * @param evaluations 妥当性チェック対象のevaluationリスト
	 * @return 妥当性OKとなったevaluationのリスト
	 */
	public List<Evaluation> apply(Odds beforeOdds, Double probability, List<Evaluation> evaluations) {
		
		List<Evaluation> results = new ArrayList<>();
		String rangeSelector;

		for (Evaluation eval : evaluations) {
			// validationを行う論理式 ex) "bork&&(pr||bor)"
			rangeSelector = eval.get("range_selector");
			
			if ((rangeSelector.contains(BOR) || rangeSelector.contains(BORK)) && beforeOdds == null) {
				continue;
			}

			if (rangeSelector.contains(PR) && probability == null) {
				continue;
			}

			// 論理式評価 jexl
			JexlExpression jexlExp = getJexlExpression(rangeSelector);
			
			if (isBestRange(jexlExp, beforeOdds, probability, eval)) {
				results.add(eval);
			}
		}
		
		return results;
	}
	
	/** JexlExpressionを取得する */
	JexlExpression getJexlExpression(String rangeSelector) {
		JexlExpression jexlExp = mapExpr.get(rangeSelector);
		if (jexlExp == null) {
			jexlExp = new JexlBuilder().cache(512).strict(true).silent(false).create().createExpression(rangeSelector);
			mapExpr.put(rangeSelector, jexlExp);
		}
		
		return jexlExp; 
	}

	/**
	 * jexlExpを持ってEvaluationに対して｛直前オッズ、直前オッズランク、予想確率｝範囲の論理的評価を行う。
	 * @param jexlExp
	 * @param beforeOdds 直前オッズ
	 * @param probability 予想確率
	 * @param eval Evaluation
	 * @return true=範囲内 false=範囲外
	 */
	boolean isBestRange(JexlExpression jexlExp, Odds beforeOdds, Double probability, Evaluation eval) {
		String rangeSelector = jexlExp.getSourceText();
		JexlContext jexlContext = new MapContext();
		
		if (rangeSelector.contains(BOR)) {
			jexlContext.set(BOR, borValidator.isBestRange(beforeOdds.value, eval));
		}

		if (rangeSelector.contains(BORK)) {
			jexlContext.set(BORK, borkValidator.isBestRange(beforeOdds.rank.doubleValue(), eval));
		}
		
		if (rangeSelector.contains(PR)) {
			jexlContext.set(PR, prValidator.isBestRange(probability, eval));
		}
		
		return (Boolean) jexlExp.evaluate(jexlContext);
	}
}

package com.pengkong.boatrace.exp10.simulation.range.validation;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;

/**
 * selectorの値がEvaluationの妥当範囲にあるかをチェックする。
 * @author ttolt
 *
 */
public abstract class AbstractRangeValidationProvider {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	public List<Evaluation> apply(Double value, List<Evaluation> evaluations) {
		List<Evaluation> results = new ArrayList<>();
		// 比較対象値が設定されていない場合、不当としてskipする(ex:直前オッズが取得できなかった）
		if (value == null) {
			return results;
		}
		
		for (Evaluation eval : evaluations) {
			if (isBestRange(value, eval)) {
				results.add(eval);
			}
		}
		
		return results;
	}
	
	/**
	 * 指定の確率が妥当な範囲内(外)にあるか判定する。
	 * @param value チェック対象値
	 * @param eval Evaluation
	 * @return true=妥当 false=不妥当
	 */
	abstract boolean isBestRange(Double value, Evaluation  eval);
}

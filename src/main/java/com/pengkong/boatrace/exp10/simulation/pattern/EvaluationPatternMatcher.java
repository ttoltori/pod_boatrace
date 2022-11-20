package com.pengkong.boatrace.exp10.simulation.pattern;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.simulation.evaluation.AbstractEvaluationLoader;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public class EvaluationPatternMatcher implements IEvaluationPatternMatcher {
	Logger logger = LoggerFactory.getLogger(EvaluationPatternMatcher.class);

	AbstractEvaluationLoader evLoader;
	
	public EvaluationPatternMatcher(AbstractEvaluationLoader evLoader) {
		this.evLoader = evLoader;
	}

	/**
	 * DBレコードに対して複数Evaluationとのパタンマッチングを行う。
	 * @param betType
	 * @param kumiban
	 * @param rec
	 * @return マッチングしたEvaluationのリスト。リターン値なしの場合は空リスト
	 * @throws Exception
	 */
	@Override
	public List<Evaluation> match(String betType, String kumiban, String modelNo, DBRecord rec) throws Exception {
		List<Evaluation> result = new ArrayList<Evaluation>();
		List<Evaluation> listEval = evLoader.getEvaluations(betType + "_" + kumiban + "_" + modelNo); 
		if (listEval == null) {
			// 空リスト
			return result;
		}

		PatternWrapper dpw = new PatternWrapper(rec);
		for (Evaluation eval : listEval) {
			// 対象gradeでない場合スキップ
			if (!eval.get("grades").contains(rec.getString("grade"))) {
				continue;
			}
			
			String patternId = eval.get("patternid");
			String ptnRec = dpw.getPattern(patternId);
			String ptnEval = eval.get("pattern");
			
			// パタンが一致しない場合スキップ
			if (!ptnRec.equals(ptnEval)) {
				continue;
			}
			result.add(eval);
		}
		
		return result;
	}
}

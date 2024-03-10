package com.pengkong.boatrace.exp10.simulation.pattern;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.simulation.evaluation.AbstractEvaluationLoader;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.exp10.util.EvaluationHelper;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.collection.HashMapList;

/**
 * ロードしておいたevaluationに対して全てのpatternid別に1個のパタンがマッチしているか確認する。
 * 
 * @author ttolt
 *
 */
public class EvaluationPatternMatcher2 implements IEvaluationPatternMatcher {
	Logger logger = LoggerFactory.getLogger(EvaluationPatternMatcher2.class);

	AbstractEvaluationLoader evLoader;

	public EvaluationPatternMatcher2(AbstractEvaluationLoader evLoader) {
		this.evLoader = evLoader;
	}

	/**
	 * DBレコードに対して複数Evaluationとのパタンマッチングを行う。
	 * 
	 * @param betType
	 * @param kumiban
	 * @param rec
	 * @return マッチングしたEvaluationのリスト。リターン値なしの場合は空リスト
	 * @throws Exception
	 */
	@Override
	public List<Evaluation> match(String betType, String kumiban, String modelNo, DBRecord rec) throws Exception {
		List<Evaluation> listEval = evLoader.getEvaluations(betType + "_" + kumiban + "_" + modelNo);

		if (listEval == null) {
			// 空リスト
			return new ArrayList<Evaluation>();
		}

		PatternWrapper dpw = new PatternWrapper(rec);

		HashMapList<Evaluation> mapIdEval = EvaluationHelper.createMapPatternIdEvaluations(listEval,
				rec.getString("grade"));

		return validate(mapIdEval, dpw);
	}

	/**
	 * ローディングしたevaluationをpatternid別に１個以上のパタンが一致しているか確認する。
	 * 
	 * @param mapIdEval patternid別の複数patternを保持するHashMapList
	 * @param dpw       DBレコードのpattern wrapper
	 * @return 全てのpatternid別に1個のパタンが一致した場合一致したevaluationのリスト、そうでない場合空きリスト
	 */
	List<Evaluation> validate(HashMapList<Evaluation> mapIdEval, PatternWrapper dpw) {
		List<Evaluation> result = new ArrayList<Evaluation>();

		int sameCount = 0;
		for (String key : mapIdEval.keySet()) {
			String ptnRec = dpw.getPattern(key);
			for (Evaluation eval : mapIdEval.get(key)) {
				if (ptnRec.equals(eval.get("pattern"))) {
					sameCount++;
					result.add(eval);
					break;
				}
			}
		}

		//logger.debug("sameCount=" + sameCount + " pttnid count=" + mapIdEval.size());
		if (sameCount != mapIdEval.size()) {
			return new ArrayList<Evaluation>();
		}

		return result;
	}
}

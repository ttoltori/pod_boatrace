package com.pengkong.boatrace.exp10.simulation;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.exp10.enums.RangeValidationType;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.exp10.simulation.evaluation.EvaluationSet;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public class SCDefault extends AbstractSimulationCreator {
	@Override
	List<MlResult> get1Tresult(DBRecord rec) throws Exception {
		List<MlResult> result = execute(BetType._1T, rec);
		return result;
	}

	@Override
	List<MlResult> get2Tresult(DBRecord rec) throws Exception {
		List<MlResult> result = execute(BetType._2T, rec);
		return result;
	}

	@Override
	List<MlResult> get3Tresult(DBRecord rec) throws Exception {
		List<MlResult> result = execute(BetType._3T, rec);
		return result;
	}

	@Override
	List<MlResult> get2Fresult(DBRecord rec) throws Exception {
		List<MlResult> result = execute(BetType._2F, rec);
		return result;
	}

	@Override
	List<MlResult> get3Fresult(DBRecord rec) throws Exception {
		List<MlResult> result = execute(BetType._3F, rec);
		return result;
	}

	@Override
	List<MlResult> get2Mresult(DBRecord rec) throws Exception {
		List<MlResult> result = execute(BetType._2M, rec);
		return result;
	}

	@Override
	List<MlResult> get3Mresult(DBRecord rec) throws Exception {
		List<MlResult> result = execute(BetType._3M, rec);
		return result;
	}

	@Override
	List<MlResult> get3Nresult(DBRecord rec) throws Exception {
		List<MlResult> result = execute(BetType._3N, rec);
		return result;
	}

	@Override
	List<MlResult> get2Nresult(DBRecord rec) throws Exception {
		List<MlResult> result = execute(BetType._2N, rec);
		return result;
	}

    @Override
    List<MlResult> get3Presult(DBRecord rec) throws Exception {
        List<MlResult> result = execute(BetType._3P, rec);
        return result;
    }

    @Override
    List<MlResult> get3Rresult(DBRecord rec) throws Exception {
        List<MlResult> result = execute(BetType._3R, rec);
        return result;
    }
    @Override
    List<MlResult> get3Uresult(DBRecord rec) throws Exception {
        List<MlResult> result = execute(BetType._3U, rec);
        return result;
    }
    /**
	 * ①.複数のMlClassificationに対して
	 * ②-1.各MlClassificationに対して対してパタンマッチングしたい複数のEvaluationを取得する。
	 * ②-2.複数のEvaluationから戦略(pattern_selector)によって一つだけEvaluationをselectする（MlClassification:Evaluationは1:1関係）
	 * ③.②の結果の複数のEvaluationを組番毎に分類する（組番：Evaluationは1:N関係）
	 * ④.③で得られて複数組番から戦略(prediction_selector)によって複数の組番をselectする
	 * ⑤-1.④で得られた複数組番のそれぞれに対して戦略(model_selector)によって代表Evaluationを一つだけselectする
	 * ⑤-2.④で得られた複数組番のそれぞれに対して⑤-1で得られたEvaluationのMlClassificationを付加してResultCreatorを実行する
	 * @param betType 
	 * @param patternSelector
	 * @param predictionSelector
	 * @param modelSelector
	 * @param rec
	 * @return
	 * @throws Exception
	 */
	List<MlResult> execute(BetType betType, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();

		// ①.複数のMlClassificationに対して
		// ②-1.各MlClassificationに対して対してパタンマッチングしたい複数のEvaluationを取得する。
		// ②-2.複数のEvaluationから戦略(pattern_selector)によって一つだけEvaluationをselectする（MlClassification:Evaluationは1:1関係）
		// ③.②の結果の複数のEvaluationを組番毎に分類する（組番：Evaluationは1:N関係）
		mapPredictionEvaluations = createMapPredictionEvaluations(betType.getValue(), patternSelector, rec);
		if (mapPredictionEvaluations.isEmpty()) {
			return result;
		}
		
		// ④.③で得られて複数組番から戦略(prediction_selector)によって複数の組番をselectする
		predictionList = new EvaluationPredictionSelector(mapPredictionEvaluations).select(predictionSelector);

		// 組番に対して複数のMlClassificationからEvaluationの特定field値がもっとも高いMlClassificationを取得する。
		// simulationの後に再びパタン分類するときのclassification情報を提供することが目的で、
		// simulation結果を再びclasificaion情報からパタン分類なない場合は特に意味はない
		for (String prediction : predictionList) {
			List<Evaluation> evaluations = mapPredictionEvaluations.get(prediction);

			if (!rangeSelector.equals(RangeValidationType.NONE.getValue())) {
				// 予想確率を計算する (probability1,2,3からの算出方法 */
				Double probability = probCalculator.calculate(betType.getValue(), rec);
				
				// 直前オッズがあればDBrecordに設定（なければnullが設定される）
				Odds beforeOdds = getBeforeOdds(rec, betType, prediction);
				
				evaluations = rangeValidator.apply(beforeOdds, probability, evaluations);
			}
			
			// 該当するevaluationがなければ
			if (evaluations.size() < 1) {
				continue;
			}
			
			// ⑤-2.④で得られた複数組番のそれぞれに対して⑤-1で得られたEvaluationのMlClassificationを付加してResultCreatorを実行する
			Evaluation evaluation = new EvaluationSet(evaluations).selectMostHigh(modelSelector);

			// DBRecordにMlClassification情報設定
			rec = putClassification(rec, mapClassification.get(evaluation.get("modelno")));

			// 組番毎にResultCreator呼び出し
			
			List<MlResult> listResult = resultCreator.execute(rec, betType.getValue(), prop.getString("kumiban"));

			// BONUS適用(予想確率、直前オッズ、直前オッズランク)
			listResult = bonusProvider.apply(listResult, evaluation);
			
			// simulation用パタンが定義されていれば適用する
			listResult = applySimulationPattern(listResult,
					new EvaluationSet(mapPredictionEvaluations.get(prediction)));

			result.addAll(listResult);
		}

		return result;
	}
	
	/** simulation用パタンが定義されていれば適用する */
	protected List<MlResult> applySimulationPattern(List<MlResult> listResult, EvaluationSet evSet) throws Exception {
		return simulationPatternProvider.apply(listResult, evSet);
	}
}

package com.pengkong.boatrace.online.result;

import java.util.List;

import com.pengkong.boatrace.exp10.simulation.SCDefault;
import com.pengkong.boatrace.exp10.simulation.evaluation.EvaluationSet;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.online.classifier.DailyMultiModelDBClassifierCache;

/**
 * Online Simulation Creator
 * @author ttolt
 *
 */
public class OnlineSimulationCreator extends SCDefault {
	
	@Override
	protected void initialize() throws Exception {
		super.initialize();
		
		// classificationキャッシュは当日に限定する
		mmdClassifier = new DailyMultiModelDBClassifierCache(
				new EvaluationSet(evLoader.getEvaluations()).getUniqueList("modelno"));
	}

	@Override
	protected List<MlResult> applySimulationPattern(List<MlResult> listResult, EvaluationSet evSet) throws Exception {
		// シミュレーションパタン設定は不要
		return listResult;
	}
}

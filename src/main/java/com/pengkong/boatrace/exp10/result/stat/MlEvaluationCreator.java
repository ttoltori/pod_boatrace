package com.pengkong.boatrace.exp10.result.stat;

import org.apache.commons.math3.exception.NoDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.ResultHelper;
import com.pengkong.boatrace.exp10.simulation.range.BestRangeFinder;
import com.pengkong.boatrace.mybatis.entity.MlEvaluation;
import com.pengkong.common.MathUtil;

public class MlEvaluationCreator extends MlCreatorBase<MlEvaluation> 
{
	public MlEvaluationCreator(ResultStat stat) {
		super(stat);
	}

	Logger logger = LoggerFactory.getLogger(MlEvaluationCreator.class);
	
	/**
	 * ml_evaluationのinsert用DTOを生成する。
	 * !!!  int[]をMybatisでinsertするためにはIntegerTypeHandlerの追加作成が必要 mybatis-config.xmlを参照
	 * @param exNo 実験番号
	 * @param modelNo モデル番号
	 * @param patternId パタンID ex) turn+level1
	 * @param stat ResultStat
	 * @return MlEvaluation
	 */
	public MlEvaluation create(String exNo, String modelNo, String patternId, int balanceSplitNum) throws Exception {
		logger.debug("creating MlEvaluation. " + String.join(",", exNo, modelNo, stat.statBettype, stat.kumiban, patternId, stat.pattern));
		double[] regression;
		// 的中が一つもなければEvaluationを作成しない
//		if (stat.listHitOdds.size() <= 0) {
//			logger.warn("Sorry zero Hit.  Evaluation skipped. " + stat.kumiban + "," +  patternId + "," + stat.pattern + ", betcnt=" + stat.sumOfBet);
//			//return null;
//		}
		
		MlEvaluation dto = new MlEvaluation();
		dto.setResultno(exNo);
		dto.setModelno(modelNo);
		dto.setPatternid(patternId);
		dto.setPattern(stat.pattern);
		dto.setBettype(stat.statBettype);
		dto.setKumiban(stat.kumiban);
		dto.setBetcnt((int)stat.sumOfBet);
		dto.setHitcnt((int)stat.sumOfHit);
		dto.setBetamt((int)stat.sumOfBetAmount);
		dto.setHitamt((int)stat.sumOfHitAmount);
		dto.setBetrate(MathUtil.scale3(stat.betrate));
		dto.setHitrate(MathUtil.scale2(stat.hitrate));
		dto.setIncomerate(MathUtil.scale2(stat.incomerate));
		// 調和平均
		dto.setHmeanrate(MathUtil.scale2(MathUtil.getHarmonyMean(stat.betrate, stat.hitrate, stat.incomerate)));		
		
		// 残高一覧を区間毎に分割
		stat.arrBalancelist = MathUtil.splitList(stat.listBalance, balanceSplitNum);
		if (stat.arrBalancelist.length < balanceSplitNum) {
			return null;
		}
		
		// 区間内の全金額に対して、以前区間の最終残高を減額する
		ResultHelper.applyBalancing(stat.arrBalancelist, stat.startBalance);
		
		stat.balanceSplit = new int[stat.arrBalancelist.length];
		stat.balanceSlopeSplit = new double[stat.arrBalancelist.length];		
		for (int i = 0; i < stat.arrBalancelist.length; i++) {
			stat.balanceSplit[i] = stat.arrBalancelist[i].get(stat.arrBalancelist[i].size()-1).intValue();
		}
		dto.setBalance(stat.balanceSplit);

		for (int i = 0; i < stat.arrBalancelist.length; i++) {
			// balance regression
			try {
				regression = MathUtil.getRegression(stat.arrBalancelist[i]);
				stat.balanceSlopeSplit[i] = regression[0]; // slope (interceptは無視する)
			} catch (NoDataException e) {
				stat.balanceSlopeSplit[i] = RG_ERROR_VALUE; // regression error value
			}
		}
		
		// slopeのprecisionを補正する
		double[] arrSlope = new double[stat.balanceSlopeSplit.length];
		for (int i = 0; i < stat.balanceSlopeSplit.length; i++) {
			arrSlope[i] = MathUtil.scale3(stat.balanceSlopeSplit[i]);
		}
		dto.setBalSlope(arrSlope);
		// ---------------------------------------------------

		// Machine Learning Model evaluation
		// bettype,prediction,pattern
		dto.setPtPrecision(MathUtil.scale2(stat.matrix.getPrecisionForLabel(stat.kumiban)));
		dto.setPtRecall(MathUtil.scale2(stat.matrix.getRecallForLabel(stat.kumiban)));
		dto.setPtFmeasure(MathUtil.scale2(stat.matrix.getFMeasureForLabels().get(stat.kumiban)));

		// ---------------- 記述統計量設定 -----------------------------  
		//  的中オッズ / RANK
		dto = setDescriptiveStatistics(dto, "Hodds", stat.listHitOdds);
		dto = setDescriptiveStatistics(dto, "Hoddsrk", stat.listHitOddsRank);

		//  確定オッズ / RANK
		dto = setDescriptiveStatistics(dto, "Rodds", stat.listRor);
		dto = setDescriptiveStatistics(dto, "Roddsrk", stat.listRork);

		// 予想確率
		dto = setDescriptiveStatistics(dto, "Prob", stat.listProb);

		// ---------------- 最適範囲設定 -----------------------------  
		//  確定オッズ / RANK 
		dto = setRangeStatistics(dto, "Ror", new BestRangeFinder(stat.mapResultOddsStatUnit.values()), stat.sumOfBet);
		dto = setRangeStatistics(dto, "Rork", new BestRangeFinder(stat.mapRorkStatUnit.values()), stat.sumOfBet);

		// 予想確率
		dto = setRangeStatistics(dto, "Pr", new BestRangeFinder(stat.mapProbStatUnit.values()), stat.sumOfBet);
		
		// ---------------- 直前オッズが存在する場合 記述統計量/最適範囲設定 -----------------------------  
		if (stat.mapBeforeOddsStatUnit.size() > 0) {
			// 直前オッズ / RANK
			dto = setDescriptiveStatistics(dto, "Bodds", stat.listBor);
			dto = setDescriptiveStatistics(dto, "Boddsrk", stat.listBork);
			
			dto = setRangeStatistics(dto, "Bor", new BestRangeFinder(stat.mapBeforeOddsStatUnit.values()), stat.sumOfBetBodds);
			dto = setRangeStatistics(dto, "Bork", new BestRangeFinder(stat.mapBorkStatUnit.values()), stat.sumOfBetBodds);
		}

		// betrate regression
		dto.setBetrSlope(MathUtil.scale3(MathUtil.getRegressionSlope(stat.listBetrate)));
		
		// hitrate regression
		dto.setHitrSlope(MathUtil.scale3(MathUtil.getRegressionSlope(stat.listHitrate)));
		
		// incomerate regression
		dto.setIncrSlope(MathUtil.scale3(MathUtil.getRegressionSlope(stat.listIncomerate)));
		
		int balPluscnt = 0;
		for (int bal : stat.balanceSplit) {
			if (bal > stat.startBalance) {
				balPluscnt++;
			}
		}
		dto.setBalPluscnt(balPluscnt);
		
		MLPropertyUtil prop = MLPropertyUtil.getInstance();
		dto.setResultType(prop.getString("result_type"));
		dto.setEvaluationsId(prop.getString("evaluations_id"));
		
		stat.evaluation = dto;
		return dto;
	}
}

package com.pengkong.boatrace.exp10.result.stat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.boatrace.mybatis.entity.MlPrEvaluation;
import com.pengkong.common.MathUtil;

public class MlPrEvaluationCreator 
{
	Logger logger = LoggerFactory.getLogger(MlPrEvaluationCreator.class);
	
	/** regression失敗時のエラーslope値 */
	static double RG_ERROR_VALUE = -0.999;

	ResultStat stat;
	
	public MlPrEvaluationCreator(ResultStat stat) {
		this.stat = stat;
	}

	public MlPrEvaluation create(String exNo, String modelNo, String patternId, int balanceSplitNum) throws Exception {
		// 的中が一つもなければEvaluationを作成しない
//		if (stat.listHitOdds.size() <= 0) {
//			return null;
//		}
		MlPrEvaluation dto = new MlPrEvaluation();
		dto.setResultno(exNo);
		dto.setModelno(modelNo);
		dto.setPatternid(patternId);
		dto.setPattern(stat.pattern);
		dto.setBettype(stat.statBettype);
		dto.setKumiban(stat.kumiban);

		MLPropertyUtil prop = MLPropertyUtil.getInstance();
		Double min = prop.getDouble("pr_min");
		Double max = prop.getDouble("pr_max");
		int intMin = (int)(min * 100);
		int intMax = (int)(max * 100);
		
		int arrMax = (int)(intMax - intMin  +1);
		int[] betcnt = new int[arrMax];
		int[] hitcnt = new int[arrMax];
		int[] betamt = new int[arrMax];
		int[] hitamt = new int[arrMax];
		int[] incamt = new int[arrMax];
		double[] betrate = new double[arrMax];
		double[] hitrate = new double[arrMax];
		double[] incomerate = new double[arrMax];
		
		for (int i = intMin; i <= intMax; i++) {
			int idx = i - 1;
			Double doubleIdx = (double)i / 100.0;
			RangeStatUnit unit = stat.mapProbStatUnit.get(doubleIdx);
			if (unit == null) {
				continue;
			}
			betcnt[idx] = unit.betCnt;
			hitcnt[idx] = unit.hitCnt;
			betamt[idx] = unit.betAmt;
			hitamt[idx] = unit.hitAmt;
			incamt[idx] = (int)unit.getIncome();
			betrate[idx] = MathUtil.scale3(((double)unit.betCnt / stat.sumOfBet));
			hitrate[idx] = MathUtil.scale2(unit.getHitrate());
			incomerate[idx] = MathUtil.scale2(unit.getIncomerate());
		}
		
		dto.setBetcnt(betcnt);
		dto.setHitcnt(hitcnt);
		dto.setBetamt(betamt);
		dto.setHitamt(hitamt);
		dto.setIncamt(incamt);
		dto.setBetrate(betrate);
		dto.setHitrate(hitrate);
		dto.setIncomerate(incomerate);
		
		dto.setResultType(prop.getString("result_type"));
		dto.setEvaluationsId(prop.getString("evaluations_id"));
		
		stat.prEvaluation = dto;
		return dto;
	}
}

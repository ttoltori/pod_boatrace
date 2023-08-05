package com.pengkong.boatrace.exp10.result.stat;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.boatrace.mybatis.entity.MlBorkEvaluation;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.MathUtil;

public class MlBorkEvaluationCreator 
{
	Logger logger = LoggerFactory.getLogger(MlBorkEvaluationCreator.class);
	
	/** regression失敗時のエラーslope値 */
	static double RG_ERROR_VALUE = -0.999;

	ResultStat stat;
	
	public MlBorkEvaluationCreator(ResultStat stat) {
		this.stat = stat;
	}

	public MlBorkEvaluation create(String exNo, String modelNo, String patternId) throws Exception {
		// 的中が一つもなければEvaluationを作成しない
//		if (stat.listHitOdds.size() <= 0) {
//			return null;
//		}
		MlBorkEvaluation dto = new MlBorkEvaluation();
		dto.setResultno(exNo);
		dto.setModelno(modelNo);
		dto.setPatternid(patternId);
		dto.setPattern(stat.pattern);
		dto.setBettype(stat.statBettype);
		dto.setKumiban(stat.kumiban);

		MLPropertyUtil prop = MLPropertyUtil.getInstance();
		Double min = prop.getDouble("bork_min");
		Double max = prop.getDouble("bork_max");
		
		int arrMax = (int)(max-min+1);
		int[] betcnt = new int[arrMax];
		int[] hitcnt = new int[arrMax];
		int[] betamt = new int[arrMax];
		int[] hitamt = new int[arrMax];
		int[] incamt = new int[arrMax];
		double[] betrate = new double[arrMax];
		double[] hitrate = new double[arrMax];
		double[] incomerate = new double[arrMax];
		double[] borMin = new double[arrMax];
		double[] borMax = new double[arrMax];
		int[] termcnt = new int[arrMax];
		int[] termPluscnt = new int[arrMax];
		
		for (Double i = min; i <= max; i++) {
			int idx = i.intValue() - 1;
			RangeStatUnit unit = stat.mapBorkStatUnit.get(i);
			if (unit == null) {
				continue;
			}
			betcnt[idx] = unit.betCnt;
			hitcnt[idx] = unit.hitCnt;
			betamt[idx] = unit.betAmt;
			hitamt[idx] = unit.hitAmt;
			incamt[idx] = (int)unit.getIncome();
			betrate[idx] = MathUtil.scale3(((double)unit.betCnt / stat.sumOfBetBodds));
			hitrate[idx] = MathUtil.scale2(unit.getHitrate());
			incomerate[idx] = MathUtil.scale2(unit.getIncomerate());
			borMin[idx] = MathUtil.scale2(unit.bOddsMin);
			borMax[idx] = MathUtil.scale2(unit.bOddsMax);

			// 各bork毎の期間単位黒数数を求める
			List<BorkTermUnit> list = stat.mapBorkTerm.get(String.valueOf(i.intValue()));
			if (list == null) {
				continue;
			}
			
			int borkTermMax = prop.getInteger("bork_term_max");
			List<BorkTermUnit>[] arrTerm = MathUtil.splitList(list, borkTermMax);
			if (arrTerm.length < borkTermMax) {
				continue;
			}
			
			int plusCnt = 0;
			for (int j = 0; j < borkTermMax; j++) {
				if (BorkTermUnit.isPlus(arrTerm[j])) {
					plusCnt++;
				}
			}
			termcnt[idx] = borkTermMax;
			termPluscnt[idx] = plusCnt;
		}
		
		dto.setBetcnt(betcnt);
		dto.setHitcnt(hitcnt);
		dto.setBetamt(betamt);
		dto.setHitamt(hitamt);
		dto.setIncamt(incamt);
		dto.setBetrate(betrate);
		dto.setHitrate(hitrate);
		dto.setIncomerate(incomerate);
		dto.setBorMin(borMin);
		dto.setBorMax(borMax);
		dto.setTermCnt(termcnt);
		dto.setTermPluscnt(termPluscnt);
		
		dto.setResultType(prop.getString("result_type"));
		dto.setEvaluationsId(prop.getString("evaluations_id"));
		
		stat.borkEvaluation = dto;
		return dto;
	}
}

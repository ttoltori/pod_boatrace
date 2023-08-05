package com.pengkong.boatrace.exp10.result.stat;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.boatrace.mybatis.entity.MlTermEvaluation;
import com.pengkong.common.MathUtil;

public class MlTermEvaluationCreator extends MlCreatorBase<MlTermEvaluation> 
{
	public MlTermEvaluationCreator(ResultStat stat) {
		super(stat);
	}

	Logger logger = LoggerFactory.getLogger(MlTermEvaluationCreator.class);
	
	/**
	 * ml_evaluationのinsert用DTOを生成する。
	 * !!!  int[]をMybatisでinsertするためにはIntegerTypeHandlerの追加作成が必要 mybatis-config.xmlを参照
	 * @param exNo 実験番号
	 * @param modelNo モデル番号
	 * @param patternId パタンID ex) turn+level1
	 * @param stat ResultStat
	 * @return MlEvaluation
	 */
	public MlTermEvaluation create(String exNo, String modelNo, String patternId, int balanceSplitNum) throws Exception {
//		logger.debug("creating MlTermEvaluation. " + String.join(",", exNo, modelNo, stat.statBettype, stat.kumiban, patternId, stat.pattern));
		// 的中が一つもなければEvaluationを作成しない
//		if (stat.listHitOdds.size() <= 0) {
//			logger.warn("Sorry zero Hit.  Evaluation skipped. " + stat.kumiban + "," +  patternId + "," + stat.pattern + ", betcnt=" + stat.sumOfBet);
//			return null;
//		}
		MLPropertyUtil prop = MLPropertyUtil.getInstance();

		MlTermEvaluation dto = new MlTermEvaluation();
		dto.setResultno(exNo);
		dto.setModelno(modelNo);
		dto.setPatternid(patternId);
		dto.setPattern(stat.pattern);
		dto.setBettype(stat.statBettype);
		dto.setKumiban(stat.kumiban);
		
		
		List<Double> listBetcnt = new ArrayList<>(); 
		List<Double> listIncome = new ArrayList<>(); 
		List<Double> listHitrate = new ArrayList<>(); 
		List<Double> listIncomerate = new ArrayList<>(); 
		int termcnt = 0;
		int pluscnt = 0;
		for (RangeStatUnit unit : stat.termStatBalance.values()) {
			if (unit.getIncome() > 0) {
				pluscnt++;
			}
			listBetcnt.add((double)unit.betCnt);
			listIncome.add((double)unit.getIncome());
			listHitrate.add((double)unit.getHitrate());
			listIncomerate.add((double)unit.getIncomerate());
			termcnt++;
		}
		double plusrate = (double)pluscnt / (double)termcnt;
		dto.setTermcnt(termcnt);
		dto.setPluscnt(pluscnt);
		dto.setPlusrate(MathUtil.scale2(plusrate));
		
		dto = setDescriptiveStatistics(dto, "Betcnt", listBetcnt);
		dto = setDescriptiveStatistics(dto, "Inc", listIncome);
		dto = setDescriptiveStatistics(dto, "Hitr", listHitrate);
		dto = setDescriptiveStatistics(dto, "Incr", listIncomerate);

		dto.setResultType(prop.getString("result_type"));
		dto.setEvaluationsId(prop.getString("evaluations_id"));

		stat.termEvaluation = dto;
		return dto;
	}
}

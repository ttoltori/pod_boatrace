package com.pengkong.boatrace.exp10.result.stat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.range.LtrimRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.RtrimRangeFinder;
import com.pengkong.boatrace.mybatis.entity.MlRangeEvaluation;

public class MlRangeEvaluationCreator extends MlCreatorBase<MlRangeEvaluation> 
{
	public MlRangeEvaluationCreator(ResultStat stat) {
		super(stat);
	}

	Logger logger = LoggerFactory.getLogger(MlRangeEvaluationCreator.class);
	
	/**
	 * ml_evaluationのinsert用DTOを生成する。
	 * !!!  int[]をMybatisでinsertするためにはIntegerTypeHandlerの追加作成が必要 mybatis-config.xmlを参照
	 * @param exNo 実験番号
	 * @param modelNo モデル番号
	 * @param patternId パタンID ex) turn+level1
	 * @param stat ResultStat
	 * @return MlEvaluation
	 */
	public MlRangeEvaluation create(String exNo, String modelNo, String patternId, int balanceSplitNum) throws Exception {
//		logger.debug("creating MlRangeEvaluation. " + String.join(",", exNo, modelNo, stat.statBettype, stat.kumiban, patternId, stat.pattern));

		// 的中が一つもなければEvaluationを作成しない
//		if (stat.listHitOdds.size() <= 0) {
//			return null;
//		}
		
		MlRangeEvaluation dto = new MlRangeEvaluation();
		dto.setResultno(exNo);
		dto.setModelno(modelNo);
		dto.setPatternid(patternId);
		dto.setPattern(stat.pattern);
		dto.setBettype(stat.statBettype);
		dto.setKumiban(stat.kumiban);

		// ---------------- 最適範囲設定 -----------------------------
		// 予想確率
		dto = setRangeStatistics(dto, "Lpr", new LtrimRangeFinder(stat.mapProbStatUnit.values()), stat.sumOfBet);
		dto = setRangeStatistics(dto, "Rpr", new RtrimRangeFinder(stat.mapProbStatUnit.values()), stat.sumOfBet);
		//  確定オッズ / RANK 
		dto = setRangeStatistics(dto, "Lror", new LtrimRangeFinder(stat.mapResultOddsStatUnit.values()), stat.sumOfBet);
		dto = setRangeStatistics(dto, "Rror", new RtrimRangeFinder(stat.mapResultOddsStatUnit.values()), stat.sumOfBet);

		dto = setRangeStatistics(dto, "Lrork", new LtrimRangeFinder(stat.mapRorkStatUnit.values()), stat.sumOfBet);
		dto = setRangeStatistics(dto, "Rrork", new RtrimRangeFinder(stat.mapRorkStatUnit.values()), stat.sumOfBet);

		// ---------------- 直前オッズが存在する場合 記述統計量/最適範囲設定 -----------------------------  
		if (stat.mapBeforeOddsStatUnit.size() > 0) {
			dto = setRangeStatistics(dto, "Lbor", new LtrimRangeFinder(stat.mapBeforeOddsStatUnit.values()), stat.sumOfBetBodds);
			dto = setRangeStatistics(dto, "Rbor", new RtrimRangeFinder(stat.mapBeforeOddsStatUnit.values()), stat.sumOfBetBodds);

			dto = setRangeStatistics(dto, "Lbork", new LtrimRangeFinder(stat.mapBorkStatUnit.values()), stat.sumOfBetBodds);
			dto = setRangeStatistics(dto, "Rbork", new RtrimRangeFinder(stat.mapBorkStatUnit.values()), stat.sumOfBetBodds);
		}

		MLPropertyUtil prop = MLPropertyUtil.getInstance();
		dto.setResultType(prop.getString("result_type"));
		dto.setEvaluationsId(prop.getString("evaluations_id"));
		
		stat.rangeEvaluation = dto;
		
		return dto;
	}
}

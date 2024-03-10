package com.pengkong.boatrace.exp10.result.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.exception.NoDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.ResultHelper;
import com.pengkong.boatrace.exp10.simulation.range.BestRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.boatrace.mybatis.entity.MlBorkEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlEvaluation;
import com.pengkong.common.MathUtil;

@Deprecated
public class ResultStatHelper 
{
	Logger logger = LoggerFactory.getLogger(ResultStatHelper.class);
	
	/** regression失敗時のエラーslope値 */
	static double RG_ERROR_VALUE = -0.999;

	ResultStat stat;
	@Deprecated
	public ResultStatHelper(ResultStat stat) {
		this.stat = stat;
	}

	public MlBorkEvaluation createMlBorkEvaluation(String exNo, String modelNo, String patternId, int balanceSplitNum) throws Exception {
		// 的中が一つもなければEvaluationを作成しない
		if (stat.listHitOdds.size() <= 0) {
			return null;
		}
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
		
		stat.borkEvaluation = dto;
		return dto;
	}
	
	/**
	 * ml_evaluationのinsert用DTOを生成する。
	 * !!!  int[]をMybatisでinsertするためにはIntegerTypeHandlerの追加作成が必要 mybatis-config.xmlを参照
	 * @param exNo 実験番号
	 * @param modelNo モデル番号
	 * @param patternId パタンID ex) turn+level1
	 * @param stat ResultStat
	 * @return MlEvaluation
	 *
	@Deprecated
	public MlEvaluation createMlEvaluation(String exNo, String modelNo, String patternId, int balanceSplitNum) throws Exception {
		logger.debug("creating MlEvaluation. " + String.join(",", exNo, modelNo, stat.statBettype, stat.kumiban, patternId, stat.pattern));
		double[] regression;
		// 的中が一つもなければEvaluationを作成しない
		if (stat.listHitOdds.size() <= 0) {
			logger.warn("Sorry zero Hit.  Evaluation skipped. " + stat.kumiban + "," +  patternId + "," + stat.pattern + ", betcnt=" + stat.sumOfBet);
			return null;
		}
		
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
		dto = setRangeStatistics(dto, "Ror", stat.mapResultOddsStatUnit.values(), stat.sumOfBet);
		dto = setRangeStatistics(dto, "Rork", stat.mapRorkStatUnit.values(), stat.sumOfBet);

		// 予想確率
		dto = setRangeStatistics(dto, "Pr", stat.mapProbStatUnit.values(), stat.sumOfBet);
		
		// ---------------- 直前オッズが存在する場合 記述統計量/最適範囲設定 -----------------------------  
		if (stat.mapBeforeOddsStatUnit.size() > 0) {
			// 直前オッズ / RANK
			dto = setDescriptiveStatistics(dto, "Bodds", stat.listBor);
			dto = setDescriptiveStatistics(dto, "Boddsrk", stat.listBork);
			
			dto = setRangeStatistics(dto, "Bor", stat.mapBeforeOddsStatUnit.values(), stat.sumOfBetBodds);
			dto = setRangeStatistics(dto, "Bork", stat.mapBorkStatUnit.values(), stat.sumOfBetBodds);
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
		dto.setEvaluationsId(prop.getString("groups"));
		
		stat.evaluation = dto;
		return dto;
	}
	*/
	/** 範囲内データに対する集計結果を取得する */
	Double[] getRangeDtoValues(List<RangeStatUnit> listUnit, double totalBetCnt) {
		double rangeBetCnt = 0;
		double rangeBetamt = 0;
		double rangeHitCnt = 0;
		double rangeHitAmt = 0;
		double rangeBetrate = 0;

		for (RangeStatUnit unit : listUnit) {
			rangeBetCnt += unit.betCnt; // 範囲内のデータ数
			rangeBetamt += unit.betAmt; // 範囲内のbet金額合計
			rangeHitCnt += unit.hitCnt; // 範囲内の的中したデータ数
			rangeHitAmt += unit.hitAmt; // 範囲内の的中金額合計
		}

		double rangeHitrate = rangeHitCnt / rangeBetCnt; // stat.sumOfBet;
		double rangeIncomerate = rangeHitAmt / rangeBetamt; // stat.sumOfBetAmount;
		rangeBetrate = rangeBetCnt / totalBetCnt;
		
		return new Double[]{rangeBetCnt, rangeBetamt, rangeHitCnt, rangeHitAmt, rangeBetrate, rangeHitrate, rangeIncomerate};
	}

	/**
	 * 指定valuesの記述統計量をdtoに設定して取得する
	 * @param dto MlEvaluation
	 * @param field ex) Hodds, Hoddsrk, Rodds, Roddsrk, Bodds, Boddsrk
	 * @param values 値リスト 
	 */
	MlEvaluation setDescriptiveStatistics(MlEvaluation dto, String field, List<Double> values) throws Exception {
		double[] descriptiveStat = MathUtil.getDescriptiveStatistics(values);

		String[] postFixes = {"Min", "Max", "Mean", "Stddev", "Median"};
		String methodName;
		for (int i = 0; i < postFixes.length; i++) {
			methodName = String.join("", "set", field, postFixes[i]);
			MlEvaluation.class.getMethod(methodName, Double.class).invoke(dto, descriptiveStat[i]);
		}

		return dto;
	}
	
	/** 直前オッズの最適範囲探索結果の設定 */
	/**
	 * 指定RangeStatUnitのリストに対して最適範囲を求めて、その範囲内の集計値を設定して返却する.
	 * @param dto MlEvaluation
	 * @param field ex) Ror, Rork, Bor, Bork, Pr
	 * @param units
	 * @param totalBetCnt
	 * @return
	 * @throws Exception
	 */
	MlEvaluation setRangeStatistics(MlEvaluation dto, String field, Collection<RangeStatUnit> units, double totalBetCnt) throws Exception{
		
		// 直前オッズから計算した最適範囲index情報 例）[0]= 1 [1]= 4
		BestRangeFinder rangeFinder = new BestRangeFinder(new ArrayList<>(units));
		List<RangeStatUnit> bestRangeUnits = rangeFinder.findBestRangeStatUnits();
		
		if (bestRangeUnits.size() <= 0) {
			return dto;
		}
		
		Double bestRangeMin = bestRangeUnits.get(0).factorValue();
		Double bestRangeMax = bestRangeUnits.get(bestRangeUnits.size()-1).factorValue();
		
		MlEvaluation.class.getMethod("set"+field+"Bestmin", Double.class).invoke(dto, bestRangeMin);
		MlEvaluation.class.getMethod("set"+field+"Bestmax", Double.class).invoke(dto, bestRangeMax);

		
		// 直前オッズ最適範囲に対する統計情報
		String methodName;
		String[] postFixes = {"Betcnt", "Betamt", "Hitcnt", "Hitamt", "Betrate", "Hitrate", "Incomerate"};
		
		Double[] values = getRangeDtoValues(bestRangeUnits, totalBetCnt);
		
		for (int i = 0; i < postFixes.length; i++) {
			methodName = String.join("", "set", field, postFixes[i]);
			if (methodName.endsWith("rate")) {
				MlEvaluation.class.getMethod(methodName, Double.class).invoke(dto, MathUtil.scale2(values[i]));
			} else {
				MlEvaluation.class.getMethod(methodName, Integer.class).invoke(dto, values[i].intValue());
			}
		}
		
		return dto;
	}
}

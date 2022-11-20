package com.pengkong.boatrace.exp10.result.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.habernal.confusionmatrix.ConfusionMatrix;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.boatrace.mybatis.entity.MlBorkEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlPrEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlRangeEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.mybatis.entity.MlRorkEvaluation;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.MathUtil;

public class ResultStat {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	public String patternId;
	public String pattern;
	public String statBettype;
	public String kumiban;

	/** bet count */
	public double sumOfBet = 0;
	public double sumOfBetBodds = 0;

	/** bet amount */
	public double sumOfBetAmount = 0;
	public double sumOfBetAmountBodds = 0;
	
	/** hit count */
	public double sumOfHit = 0;
	public double sumOfHitBodds = 0;
	
	/** 払戻金（的中oddsによる計算を四捨五入した確定金額) */
	public double sumOfHitAmount = 0;
	public double sumOfHitAmountBodds = 0;

	/** 投票率(全体パタンの中で) */
	public Double betrate = 0.0;
	public Double betrateBodds = 0.0;
	
	/** 的中率推移 */
	public Double hitrate = 0.0;
	public Double hitrateBodds = 0.0;
	
	/** 収益率推移 */
	public Double incomerate = 0.0;
	public Double incomerateBodds = 0.0;
	
	/** 残高 */
	public int balance;

	/** bettingした全レースの払戻金 */
	@Deprecated
	public List<Double> listAllIncome = new ArrayList<>();

	/** 投票率変化値 */
	public List<Double> listBetrate = new ArrayList<>();

	/** 的中率変化値 */
	public List<Double> listHitrate = new ArrayList<>();

	/** 収益率変化値 */
	public List<Double> listIncomerate = new ArrayList<>();

	/** 残高変化値 */
	public List<Double> listBalance = new ArrayList<>();

	/** 全ての直前(予測)オッズ */
	public List<Double> listBor = new ArrayList<>();
	
	/** 全ての直前(予測)オッズRANK */
	public List<Double> listBork = new ArrayList<>();

	/** 的中したレースの確定オッズ */
	public List<Double> listHitOdds = new ArrayList<>();

	/** 的中したレースの確定オッズRANK */
	public List<Double> listHitOddsRank = new ArrayList<>();

	/** 確定オッズ */
	public List<Double> listRor = new ArrayList<>();

	/** 確定オッズRANK */
	public List<Double> listRork = new ArrayList<>();

	/** 全ての予想的中確率 */
	public List<Double> listProb = new ArrayList<>();

	/** 区間別の残高一覧 */
	public List<Double>[] arrBalancelist;
	/** 区間別の最終残高 */
	public int[] balanceSplit;
	/** 区間別の残高slope */
	public double[] balanceSlopeSplit;

	public int startBalance;

	/** 確定オッズ毎にカウント、的中カウント、的中金額を集計する */
	public Map<Double, RangeStatUnit> mapResultOddsStatUnit = new TreeMap<>();

	/** 確定オッズRANK毎にカウント、的中カウント、的中金額を集計する */
	public Map<Double, RangeStatUnit> mapRorkStatUnit = new TreeMap<>();

	/** 直前オッズ毎にカウント、的中カウント、的中金額を集計する */
	public Map<Double, RangeStatUnit> mapBeforeOddsStatUnit = new TreeMap<>();

	/** 直前オッズRANK毎にカウント、的中カウント、的中金額を集計する */
	public Map<Double, RangeStatUnit> mapBorkStatUnit = new TreeMap<>();

	/** 予想確率毎にカウント、的中カウント、的中金額を集計する */
	public Map<Double, RangeStatUnit> mapProbStatUnit = new TreeMap<>();

	/** [直前オッズ/予想確率]毎にカウント、的中カウント、的中金額を集計する ex) key="0.78_1.24" */
	
	public Map<String, RangeStatUnit> mapBoddsProbStatUnit = new TreeMap<>();

	/** [直前オッズRANK/予想確率]毎にカウント、的中カウント、的中金額を集計する ex) key="0.78_1.24" */
	
	public Map<String, RangeStatUnit> mapBoddsRankProbStatUnit = new TreeMap<>();

	/** [確定オッズ/予想確率]毎にカウント、的中カウント、的中金額を集計する ex) key="0.78_1.24" */
	@Deprecated
	public Map<String, RangeStatUnit> mapRoddsProbStatUnit = new TreeMap<>();

	/** [確定オッズRANK/予想確率]毎にカウント、的中カウント、的中金額を集計する ex) key="0.78_1.24" */
	
	public Map<String, RangeStatUnit> mapRoddsRankProbStatUnit = new TreeMap<>();

	/** 直前オッズ/確定オッズ */
	public Map<String, RangeStatUnit> mapBorRorStatUnit = new TreeMap<>();

	/** 直前オッズRANK/確定オッズRANK */
	public Map<String, RangeStatUnit> mapBorkRorkStatUnit = new TreeMap<>();
	
	/** 直前オッズRANK/直前オッズ */
	public Map<String, RangeStatUnit> mapBorkBorStatUnit = new TreeMap<>();
	
	/** TOP直前オッズの直前(予測)オッズ */
	@Deprecated
	public List<Double> listTopBeforeOdds = new ArrayList<>();

	/** TOP直前オッズの直前オッズ毎にカウント、的中カウント、的中金額を集計する */
	@Deprecated
	public Map<Double, RangeStatUnit> mapTopBeforeOddsStatUnit = new TreeMap<>();
	
	/** TOP確定オッズの直前(予測)オッズ */
	@Deprecated
	public List<Double> listTopResultOdds = new ArrayList<>();

	/** TOP直前オッズの直前オッズ毎にカウント、的中カウント、的中金額を集計する */
	@Deprecated
	public Map<Double, RangeStatUnit> mapTopResultOddsStatUnit = new TreeMap<>();

	/** 時系列グラフの期間単位カウンタ */
	public int termCount = 0;
	
	/** 時系列グラフの期間単位 */
	public int termDays = 0;
	
	public String lastTermYmd;
	
	/** 期間単位の範囲統計 */
	public Map<Double, RangeStatUnit> termStatUnit = new TreeMap<>();  
	
	/** 性能評価ConfusionMatrix */
	public ConfusionMatrix matrix = new ConfusionMatrix();

	/** MlEvaluation 生成結果 graph出力時のlabel出力メッセージ生成で利用する*/
	public MlEvaluation evaluation;
	
	public MlBorkEvaluation borkEvaluation;
	
	public MlRorkEvaluation rorkEvaluation;

	public MlRangeEvaluation rangeEvaluation;
	
	public MlPrEvaluation prEvaluation;

	public ResultStat(String statBettype, String kumiban, String patternId, String pattern, int startBalance) {
		this.statBettype = statBettype;
		this.kumiban = kumiban;
		this.patternId = patternId;
		this.pattern = pattern;
		this.startBalance = startBalance;
		this.balance = startBalance;
		this.termDays = prop.getInteger("term_days");
	}

	public String getKey() {
		return pattern + statBettype + kumiban;
	}

	public void add(MlResult result) {
		String factor;
		
		Double probability = MathUtil.scale2(result.getProbability());
		Double beforeOdds = result.getBetOdds();
		Integer beforeOddsRank = result.getBetOddsrank();
		Double resultOdds = MathUtil.scale2(result.getResultOdds());
		Integer resultOddsRank = result.getResultOddsrank();
		
		
		
		// 直前オッズの統計単位取得
		if (beforeOdds != null
				// 20221026 bork가 1-10 사이만 evaluation 대상으로 제한해본다.
				&& (beforeOddsRank != null && beforeOddsRank <= prop.getInteger("bork_max"))
				) { // 直前オッズが存在すれば
			sumOfBetBodds++;
			sumOfBetAmountBodds += result.getBetamt();
			if (result.getHity() == 1) {
				sumOfHitBodds++;
				sumOfHitAmountBodds += result.getHitamt(); // 払戻金
			}

			// 直前オッズ別集計
			listBor.add(beforeOdds);
			getRangeStatUnit(mapBeforeOddsStatUnit, beforeOdds).add(result);
			
			// 直前オッズ：予想確率別集計
			factor = String.join("_", beforeOdds.toString(), probability.toString());
			getRangeStatUnit(mapBoddsProbStatUnit, factor).add(result);
			
			// 直前オッズ：確定オッズ
			factor = String.join("_", beforeOdds.toString(), String.valueOf(resultOdds));
			getRangeStatUnit(mapBorRorStatUnit, factor).add(result);
		}

		if (beforeOddsRank != null
				// 20221026 bork가 1-10 사이만 evaluation 대상으로 제한해본다.
				&& (beforeOddsRank <= prop.getInteger("bork_max"))
				) {
			// 直前オッズRANK別集計
			listBork.add(beforeOddsRank.doubleValue());
			getRangeStatUnit(mapBorkStatUnit, beforeOddsRank.doubleValue()).add(result);
			
			// 直前オッズRANK：予想確率別集計
			factor = String.join("_", beforeOddsRank.toString(), probability.toString());
			getRangeStatUnit(mapBoddsRankProbStatUnit, factor).add(result);
			
			// 直前オッズRANK：確定オッズRANK
			factor = String.join("_", beforeOddsRank.toString(), String.valueOf(resultOddsRank));
			getRangeStatUnit(mapBorkRorkStatUnit, factor).add(result);
			
			// 直前オッズRANK：直前オッズ
			factor = String.join("_", beforeOddsRank.toString(), String.valueOf(beforeOdds));
			getRangeStatUnit(mapBorkBorStatUnit, factor).add(result);
			
			// TOP直前オッズ
			//if (beforeOddsRank <= 3) {
			//	listTopBeforeOdds.add(beforeOdds);
			//	getRangeStatUnit(mapTopBeforeOddsStatUnit, beforeOdds).add(result);
			//}
		}
		betrateBodds = MathUtil.scale2(sumOfBetBodds / ResultStatBuilder.getInstance().getMapTotalBetCnt().get(statBettype + kumiban));
		hitrateBodds = MathUtil.scale2(sumOfHitBodds / sumOfBetBodds);
		incomerateBodds = MathUtil.scale2(sumOfHitAmountBodds / sumOfBetAmountBodds);
		
		
		// result生成かつ直前オッズ開始以降ならskip
		// 20220807 직전옺즈 존재이전까지만으로 한다. resultType.startsWith(ResultType._1.getValue()) &&
		int oddsMonitorStartYmd = Integer.parseInt(prop.getString("odds_monitoring_start_ymd"));
		int resultStartYmd = Integer.parseInt(prop.getString("result_start_ymd"));
		int resultYmd = Integer.parseInt(result.getYmd());
		
		if ( (oddsMonitorStartYmd <= resultYmd) && (resultStartYmd < oddsMonitorStartYmd) ) {
			return;
		}

		// add sum
		sumOfBet++;
		sumOfBetAmount += result.getBetamt();

		// 予想確率別集計
		listProb.add(probability);
		getRangeStatUnit(mapProbStatUnit, probability).add(result);

		// 確定オッズ別集計
		if (resultOdds != null) {
			listRor.add(resultOdds);
			getRangeStatUnit(mapResultOddsStatUnit, resultOdds).add(result);
			
			// 確定オッズ：予想確率別集計
			//factor = String.join("_", resultOdds.toString(), probability.toString());
			//getRangeStatUnit(mapRoddsProbStatUnit, factor).add(result);
		}
		
		if (resultOddsRank != null) {
			// 確定オッズRANK別集計
			listRork.add(resultOddsRank.doubleValue());
			getRangeStatUnit(mapRorkStatUnit, resultOddsRank.doubleValue()).add(result);
			
			// 確定オッズRANK：予想確率別集計
			factor = String.join("_", resultOddsRank.toString(), probability.toString());
			getRangeStatUnit(mapRoddsRankProbStatUnit, factor).add(result);

			// TOP確定オッズ
			//if (resultOddsRank <= 3) {
			//	listTopResultOdds.add(resultOdds);
			//	getRangeStatUnit(mapTopResultOddsStatUnit, resultOdds).add(result);
			//}
		}
		
		// 的中した場合
		if (result.getHity() == 1) {
			sumOfHit++;
			sumOfHitAmount += result.getHitamt(); // 払戻金

			listHitOdds.add(result.getRaceOdds()); // レース情報から取得されるため必ず存在する
			if (result.getRaceOddsrank() != null) { // ResultOddsファイルから取得できない場合がある(バッチエラー）
				listHitOddsRank.add(result.getRaceOddsrank().doubleValue());
			}
		}

		// 期間単位stat
		getRangeStatUnit(termStatUnit, getTermFactor(result.getYmd())).add(result);
		
		//listAllIncome.add((double) (result.getHitamt() - result.getBetamt()));

		// set stat
		betrate = sumOfBet / ResultStatBuilder.getInstance().getMapTotalBetCnt().get(statBettype + kumiban);
		hitrate = sumOfHit / sumOfBet;
		// incomerate = sumOfResultAmount / sumOfBetAmount;
		incomerate = sumOfHitAmount / sumOfBetAmount;
		balance = balance + (result.getHitamt() - result.getBetamt());

		// confusion matrix data 追加
		matrix.increaseValue(result.getResultKumiban(), result.getBetKumiban());

		listBetrate.add(betrate);
		listHitrate.add(hitrate);
		listIncomerate.add(incomerate);
		listBalance.add((double) balance);

		result.setHitrateTransition(MathUtil.scale2(hitrate));
		result.setIncomerateTransition(MathUtil.scale2(incomerate));
		result.setBalance(balance);
	}

	/**
	 * 指定値のRangeValueStatがmapに存在しなければ生成して取得する。生成したRangeValueStatは mapに登録する
	 * 
	 * @param mapStatUnit map
	 * @param rangeValue  指定値
	 */
	RangeStatUnit getRangeStatUnit(Map<Double, RangeStatUnit> mapStatUnit, Double rangeValue) {
		RangeStatUnit rsu = mapStatUnit.get(rangeValue);
		if (rsu == null) {
			rsu = new RangeStatUnit(rangeValue);
			mapStatUnit.put(rangeValue, rsu);
		}

		return rsu;
	}

	RangeStatUnit getRangeStatUnit(Map<String, RangeStatUnit> mapStatUnit, String factor) {
		RangeStatUnit rsu = mapStatUnit.get(factor);
		if (rsu == null) {
			rsu = new RangeStatUnit(factor);
			mapStatUnit.put(factor, rsu);
		}

		return rsu;
	}

	/** 
	 * 指定期間単位のfactorを取得する。
	 * @return 
	 */
	Double getTermFactor(String ymd) {
		if (lastTermYmd == null) {
			lastTermYmd = ymd;
		}
		
		int days = BoatUtil.daysBetween(lastTermYmd, ymd);
		if (days >= termDays) {
			termCount++;
			lastTermYmd = ymd;
		}
		
		return Double.valueOf(termCount);
	}
	
	public String toString() {
		return statBettype + "_" + kumiban + "_" + pattern;
	}
}

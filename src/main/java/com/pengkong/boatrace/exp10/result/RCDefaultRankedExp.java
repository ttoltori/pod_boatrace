package com.pengkong.boatrace.exp10.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.converter.MlExpectedRec;
import com.pengkong.boatrace.exp10.odds.provider.ResultOddsProvider;
import com.pengkong.boatrace.exp10.result.ranked.MlExpectedRecProvider;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * 投票結果を生成するdefaultクラス
 * 
 * @author ttolt
 *
 */
public class RCDefaultRankedExp extends AbstractResultCreatorRankedExp {

	Logger logger = LoggerFactory.getLogger(RCDefaultRankedExp.class);

	/** 勝式毎のベットカウントmax   1T,2T,2F,3T,3F順 */
	Map<BetType, Integer> betcntMap = new HashMap<>();

	/** 勝式毎の確率下限   1T,2T,2F,3T,3F順 */
	Map<BetType, Double> minProbMap = new HashMap<>();

	/** 勝式毎の期待値上限   1T,2T,2F,3T,3F順 */
	Map<BetType, Double> maxExpMap = new HashMap<>();

	Map<BetType, Integer> maxBorkMap = new HashMap<>();

	public RCDefaultRankedExp() {
		super();
	}

	@Override
	protected void preExecute() {
		// 予想確率組み合わせクラス (statBettype기준)
		//probabilityCalculator = ProbabilityCalculatorFactory.create();
		
		// 기대치 계산용  예상확률을 취득 (bettype기준) 
		//probabilityExpCalculator = ProbabilityExpCalculatorFactory.create();
		
		// 直前オッズ
		//beforeOddsProvider = new RmiBeforeOddsProvider();
		//beforeOddsProvider = new BeforeOddsProvider();
		
		// 確定オッズ
		//resultOddsProvider = new RmiResultOddsProvider();
		resultOddsProvider = new ResultOddsProvider();
		mlExpRecProvider = new MlExpectedRecProvider();

		String[] token = prop.getString("bexp_cnt").split(Delimeter.COMMA.getValue());
		betcntMap.put(BetType._1T, Integer.valueOf(token[0]));
		betcntMap.put(BetType._2T, Integer.valueOf(token[1]));
		betcntMap.put(BetType._2F, Integer.valueOf(token[2]));
		betcntMap.put(BetType._3T, Integer.valueOf(token[3]));
		betcntMap.put(BetType._3F, Integer.valueOf(token[4]));

		// 勝式毎の期待値上限 (bexp_max=2.5,2.5,2.5,3.0,3.0)
		String[] token2 = prop.getString("bexp_max").split(Delimeter.COMMA.getValue());
		maxExpMap.put(BetType._1T, Double.valueOf(token2[0]));
		maxExpMap.put(BetType._2T, Double.valueOf(token2[1]));
		maxExpMap.put(BetType._2F, Double.valueOf(token2[2]));
		maxExpMap.put(BetType._3T, Double.valueOf(token2[3]));
		maxExpMap.put(BetType._3F, Double.valueOf(token2[4]));

		// 勝式毎の確率下限 (bprob_min=0.12,0.04,0.06,0.01,0.025)
		String[] token3 = prop.getString("bprob_min").split(Delimeter.COMMA.getValue());
		minProbMap.put(BetType._1T, Double.valueOf(token3[0]));
		minProbMap.put(BetType._2T, Double.valueOf(token3[1]));
		minProbMap.put(BetType._2F, Double.valueOf(token3[2]));
		minProbMap.put(BetType._3T, Double.valueOf(token3[3]));
		minProbMap.put(BetType._3F, Double.valueOf(token3[4]));

		String[] token4 = prop.getString("bork_max2").split(Delimeter.COMMA.getValue());
		maxBorkMap.put(BetType._1T, Integer.valueOf(token4[0]));
		maxBorkMap.put(BetType._2T, Integer.valueOf(token4[1]));
		maxBorkMap.put(BetType._2F, Integer.valueOf(token4[2]));
		maxBorkMap.put(BetType._3T, Integer.valueOf(token4[3]));
		maxBorkMap.put(BetType._3F, Integer.valueOf(token4[4]));
	}

	@Override
	protected List<MlResult> get1Tresult(DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = mlExpRecProvider.getBetsByCondition(
			rec, BetType._1T.getValue(), maxExpMap.get(BetType._1T), 
			minProbMap.get(BetType._1T), maxBorkMap.get(BetType._1T), betcntMap.get(BetType._1T));

		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._1T, BetType._1T, expRec.bkumiban, rec, expRec));
		}
		return result;
	}

	@Override
	protected List<MlResult> get2Tresult(DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = mlExpRecProvider.getBetsByCondition(
			rec, BetType._2T.getValue(), maxExpMap.get(BetType._2T), 
			minProbMap.get(BetType._2T), maxBorkMap.get(BetType._2T), betcntMap.get(BetType._2T));
		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._2T, BetType._2T, expRec.bkumiban, rec, expRec));
		}
		
		return result;
	}

	@Override
	protected List<MlResult> get3Tresult(DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = mlExpRecProvider.getBetsByCondition(
			rec, BetType._3T.getValue(), maxExpMap.get(BetType._3T), 
			minProbMap.get(BetType._3T), maxBorkMap.get(BetType._3T), betcntMap.get(BetType._3T));
		
		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._3T, BetType._3T, expRec.bkumiban, rec, expRec));
		}
	
		return result;
	}

	@Override
	protected List<MlResult> get2Fresult(DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = mlExpRecProvider.getBetsByCondition(
			rec, BetType._2F.getValue(), maxExpMap.get(BetType._2F), 
			minProbMap.get(BetType._2F), maxBorkMap.get(BetType._2F), betcntMap.get(BetType._2F));
		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._2F, BetType._2F, expRec.bkumiban, rec, expRec));
		}

		return result;
	}

	@Override
	protected List<MlResult> get3Fresult(DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = mlExpRecProvider.getBetsByCondition(
			rec, BetType._3F.getValue(), maxExpMap.get(BetType._3F), 
			minProbMap.get(BetType._3F), maxBorkMap.get(BetType._3F), betcntMap.get(BetType._3F));
		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._3F, BetType._3F, expRec.bkumiban, rec, expRec));
		}

		return result;
	}
}

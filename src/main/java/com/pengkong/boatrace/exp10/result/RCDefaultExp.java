package com.pengkong.boatrace.exp10.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.converter.MlExpectedRec;
import com.pengkong.boatrace.exp10.odds.provider.ResultOddsProvider;
import com.pengkong.boatrace.exp10.simulation.calculator.expectation.ProbabilityExpCalculatorFactory;
import com.pengkong.boatrace.exp10.simulation.calculator.probability.ProbabilityCalculatorFactory;
import com.pengkong.boatrace.exp10.simulation.data.rmi.client.RmiBeforeOddsProvider;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * 投票結果を生成するdefaultクラス
 * 
 * @author ttolt
 *
 */
public class RCDefaultExp extends AbstractResultCreatorExp {

	Logger logger = LoggerFactory.getLogger(RCDefaultExp.class);

	public RCDefaultExp() {
		super();
	}

	@Override
	protected void preExecute() {
		// 予想確率組み合わせクラス (statBettype기준)
		probabilityCalculator = ProbabilityCalculatorFactory.create();
		
		// 기대치 계산용  예상확률을 취득 (bettype기준) 
		probabilityExpCalculator = ProbabilityExpCalculatorFactory.create();
		
		// 直前オッズ
		beforeOddsProvider = new RmiBeforeOddsProvider();
		//beforeOddsProvider = new BeforeOddsProvider();
		
		// 確定オッズ
		//resultOddsProvider = new RmiResultOddsProvider();
		resultOddsProvider = new ResultOddsProvider();
	}

	@Override
	protected List<MlResult> get1Tresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = MlExpectedRec.create(rec);
		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._1T, BetType._1T, kumiban, rec, expRec));
		}
		return result;
	}

	@Override
	protected List<MlResult> get2Tresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = MlExpectedRec.create(rec);
		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._2T, BetType._2T, kumiban, rec, expRec));
		}
		
		return result;
	}

	@Override
	protected List<MlResult> get3Tresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = MlExpectedRec.create(rec);
		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._3T, BetType._3T, kumiban, rec, expRec));
		}
	
		return result;
	}

	@Override
	protected List<MlResult> get2Fresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = MlExpectedRec.create(rec);
		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._2F, BetType._2F, kumiban, rec, expRec));
		}

		return result;
	}

	@Override
	protected List<MlResult> get3Fresult(String kumiban, DBRecord rec) throws Exception {
		List<MlResult> result = new ArrayList<>();
		List<MlExpectedRec> mlExpectedRecList = MlExpectedRec.create(rec);
		for (MlExpectedRec expRec : mlExpectedRecList) {
			result.add(createDefault(BetType._3F, BetType._3F, kumiban, rec, expRec));
		}

		return result;
	}

	/**
	 * 組番1,2,3,4,5,6からstrings以外を返却する
	 * 
	 * @param strings 除外するnum ex)1,2
	 * @return ex)3,4,5,6
	 */
	private final List<String> getRemainKumibans(String... strings) {
		List<String> numList = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6"));
		for (String str : strings) {
			numList.remove(str);
		}

		return numList;
	}
}

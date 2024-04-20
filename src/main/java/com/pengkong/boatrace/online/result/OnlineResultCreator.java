package com.pengkong.boatrace.online.result;

import java.util.HashMap;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.RCDefault;
import com.pengkong.boatrace.exp10.simulation.calculator.probability.ProbabilityCalculatorFactory;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.online.rmi.odds.RmiOnlineOddsProvider;
import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * Online Result Creator
 * @author ttolt
 *
 */
public class OnlineResultCreator extends RCDefault {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	/** key=bettype, value=betamt */
	HashMap<String, Integer> mapBetAmt;
	
	@Override
	protected void preExecute() {
		mapBetAmt = new HashMap<>();
		mapBetAmt.put("1T", prop.getInteger("initial_betamt_1T"));
		mapBetAmt.put("2T", prop.getInteger("initial_betamt_2T"));
		mapBetAmt.put("2F", prop.getInteger("initial_betamt_2F"));
		mapBetAmt.put("3T", prop.getInteger("initial_betamt_3T"));
		mapBetAmt.put("3F", prop.getInteger("initial_betamt_3F"));
		
		// 予想確率組み合わせクラス
		probabilityCalculator = ProbabilityCalculatorFactory.create();
		
		// 直前オッズ
		beforeOddsProvider = new RmiOnlineOddsProvider();
	}

	@Override
	protected MlResult setResultOdds(MlResult result) throws Exception {
		// 確定オッズ設定なし
		return result;
	}
	
	@Override
	protected MlResult setRaceResult(BetType betType, DBRecord rec, MlResult result) throws Exception {
		// レース結果設定なし
		return result;
	}
	
	@Override
	protected int getDefaultBetamt(String betType) {
		// 初期ベット金額
		return mapBetAmt.get(betType);
	}
}

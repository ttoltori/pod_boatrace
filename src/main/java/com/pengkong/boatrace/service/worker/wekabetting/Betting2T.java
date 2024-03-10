package com.pengkong.boatrace.service.worker.wekabetting;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.mybatis.entity.StPtnFinal;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.boatrace.weka.classifiers.BoatClassifierDefault;
import com.pengkong.boatrace.weka.classifiers.manager.ClassifierManager;
import com.pengkong.boatrace.weka.classifiers.manager.PatternManager;

public class Betting2T extends BettingDefault {
	// kumiban 선정 weka model
	BoatClassifierDefault kumibanClassifier;
	
	// oddsrankgap 판정 weka model　⇒　bet_kumiban기준, result_kumiban기준 두가지로 해볼것
	BoatClassifierDefault oddsRankGapClassifier;
	
	// 패턴 관리자
	PatternManager ptnManager = PatternManager.getInstance();
	
	// weka 모델 관리자
	ClassifierManager clsManager = ClassifierManager.getInstance();
	
	public Betting2T(BettingRule rule) throws Exception {
		super(rule);
	}

	@Override
	public RaceEx createAndSetBetList(RaceEx race) throws Exception {
		race.multiplyType = RaceEx.MULTIPLY_NORMAL;
		int betMoney = 0;

		String betType = rule.getString("type");
		betMoney = (int) (rule.getInt("baseBetAmount") * mapJyoRate.get(race.getJyoCd())
				* mapRaceRate.get(race.getRaceNo()));
		
		kumibanClassifier = clsManager.getClassifierList(betType, "12-31", race.raceInfo.ymd).get(0);
		// kumiban 예측
		String predictKumiban = kumibanClassifier.predictFromHashmap(RaceEx.getWekaInputEntry(race));
		//if (predictKumiban.equals("else")) {
		if (!predictKumiban.equals("21")) {
			return race;
		}
		
		// 패턴 체크사용
		List<StPtnFinal> listStPtnFinal = new ArrayList<>();
		if (rule.getString("USE_PATTERN." + predictKumiban).equals("yes")) {
			listStPtnFinal = ptnManager.getStPtnFinalList(betType, predictKumiban, race);
			if (listStPtnFinal.size() <= 0) {
				return race;
			}
		}

		OddsItemEx odds = oddsManager.getOddsItemEx(race.getJyoCd(), race.getRaceNo(), betType, predictKumiban);
		if (odds == null || (odds.value < 2 || odds.value > 100)) {
			return race;
		}
		
		// oddsrank gap 사용
		if (rule.getString("USE_ODDSRANK_GAP." + predictKumiban).equals("yes")) {
			oddsRankGapClassifier = clsManager.getClassifierList(betType, "21", race.raceInfo.ymd).get(0);
			String ret = oddsRankGapClassifier.predictFromHashmap(RaceEx.getWekaInputOddsRankGap(betType, predictKumiban, race));
			if (ret.equals("0")) {
				return race;
			}
		}
		
		Bet bet = new Bet(betType, predictKumiban, betMoney, odds.value, odds.rank);
		race.betlist.add(bet);
		
		return race;
	}

}

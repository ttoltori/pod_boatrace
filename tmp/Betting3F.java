package com.pengkong.boatrace.service.worker.wekabetting.old1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.mybatis.entity.StatMlPtnFiltered;
import com.pengkong.boatrace.service.manager.WekaPattern;
import com.pengkong.boatrace.simulation.model.Bet;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.common.collection.HashMapList;

public class Betting3F extends BettingDefault {

	public Betting3F(BettingRule rule) throws Exception {
		super(rule);
	}

	@Override
	public RaceEx createAndSetBetList(RaceEx race) throws Exception {
		race.multiplyType = RaceEx.MULTIPLY_NORMAL;
		int betMoney = 0;

		String betType = rule.getString("type");
		betMoney = (int) (rule.getInt("baseBetAmount") * mapJyoRate.get(race.getJyoCd())
				* mapRaceRate.get(race.getRaceNo()));

		// weka patternを取得する
		HashMapList<StatMlPtnFiltered> mapListPatterns = wekaManager.getPatterns(betType, race);

		// 組番毎のループ
		for (Entry<String, ArrayList<StatMlPtnFiltered>> entry : mapListPatterns.entrySet()) {
			String kumiban = entry.getKey();
			OddsItemEx odds = oddsManager.getOddsItemEx(race.getJyoCd(), race.getRaceNo(), betType, kumiban);
			if (odds == null) {
				continue;
			}

			// オッズ判定したWekaPatternを取得
			WekaPattern wekaPattern = convert(kumiban, entry.getValue(), odds);
			int patternNameCount = wekaPattern.getPatternNameCount();
			if (patternNameCount > 0) {
				Bet bet = new Bet(betType, kumiban, betMoney, odds.value, odds.rank, wekaPattern);
				race.betlist.add(bet);
				//break;
			}
		}

		return race;
	}

	/**
	 * パタン候補とオッズからWekaPattern を生成して返却する.
	 * 
	 * @param kumiban
	 *            組番
	 * @param listPattern
	 *            全体パタンリスト
	 * @param odds
	 *            オッズ
	 * 
	 * @return WekaPattern
	 */
	private WekaPattern convert(String kumiban, List<StatMlPtnFiltered> listPattern, OddsItemEx odds) {
		WekaPattern result = new WekaPattern();
		result.kumiban = kumiban;
		for (StatMlPtnFiltered ptn : listPattern) {
			// オッズ、オッズランクが範囲外なら除外
//			if (odds.value < ptn.getMinOdds() || odds.value > ptn.getMaxOdds() || odds.rank < ptn.getMinOddsrank()
//					|| odds.rank > ptn.getMaxOddsrank()) {
//				continue;
//			}

//			if (!kumiban.equals("124")) {
//				continue;
//			}
//			if (ptn.getPtnBetamt() < 1800) {
//				continue;
//			}
			
			
//			if (ptn.getPtnDailyPlusRate() < ptn.getPtnHitrate()) {
//				continue;
//			}

// 123
//			if (!kumiban.equals("123")) {
//				continue;
//			}
//			if (ptn.getPtnIncomerate() < 105) {
//				continue;
//			}
// 124
//			if (!kumiban.equals("124")) {
//				continue;
//			}
//			if (ptn.getPtnBetamt() < 1800) {
//				continue;
//			}
			
			// if (ptn.getPtnBetamt() < 10000 || ptn.getPtnIncomerate() < 120) {
			// if (ptn.getPtnBetamt() < 70000) {
			// if (ptn.getPtnIncomerate() > 120) {
			// if (ptn.getPtnTotalrate() < 30) {
			// if (ptn.getPtnDailyPlusRate() < 40) {
			// if (ptn.getHitrate() < 30 || ptn.getHitrate() > 99) {
			// if (ptn.getTotalrate() < 20 || ptn.getIncomerate() < 130 ||
			// ptn.getBetcountDaily() < 1) {
			// if (ptn.getTotalrate() < 20 || ptn.getIncomerate() < 130) {
			// if (ptn.getTotalrate() < 20 ) {
			// if (ptn.getIncomerate() < 115 || ptn.getHitrate() > 50) {

			result.mapListPattern.addItem(ptn.getPatternName(), ptn);
		}

		return result;
	}

}

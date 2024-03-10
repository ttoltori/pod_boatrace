package com.pengkong.boatrace.service.worker.wekabetting.old1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.mybatis.entity.StatMlPtnFiltered;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.service.manager.WekaPattern;
import com.pengkong.boatrace.service.manager.WekaPatternManager;
import com.pengkong.boatrace.service.worker.betting.Betting;
import com.pengkong.boatrace.simulation.model.Bet;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.common.collection.HashMapList;

/**
 * weka対応のBetクラス.
 * <p>
 * 勝式毎に付加処理が必要な場合はこのクラスを拡張する
 * </p>
 * @author qwerty
 *
 */
public class BettingDefault implements Betting {
	BettingRule rule;
	HashMap<String, Float> mapJyoRate; 
	HashMap<String, Float> mapRaceRate;

	WekaPatternManager wekaManager = WekaPatternManager.getInstance();
	// TODO BeforeOddsManagerとOddsManagerを統合する
	BeforeOddsManager oddsManager = BeforeOddsManager.getInstance();
	
	public BettingDefault(BettingRule rule) throws Exception {
		this.rule = rule;
		mapJyoRate = new HashMap<>();
		mapRaceRate = new HashMap<>();
		
		String[] jyoRateToken = rule.getString("jyoRateList").split(",");
		for (int i = 0; i < jyoRateToken.length; i++) {
			String[] token = jyoRateToken[i].split("=");
			mapJyoRate.put(token[0], Float.parseFloat(token[1]));
		}
		
		String[] raceRateToken = rule.getString("raceRateList").split(",");
		for (int i = 0; i < raceRateToken.length; i++) {
			String[] token = raceRateToken[i].split("=");
			mapRaceRate.put(token[0], Float.parseFloat(token[1]));
		}
	}

	@Override
	public RaceEx createAndSetBetList(RaceEx race) throws Exception {
		return race;
	}
}

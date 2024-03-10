package com.pengkong.boatrace.service.worker.betting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.StatResult;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.service.manager.ResultOddsManager;
import com.pengkong.boatrace.service.manager.StatManager;
import com.pengkong.boatrace.simulation.model.BettingRule;

public class Betting1T implements Betting{

	BettingRule rule;
	private HashMap<String, Float> mapJyoRate; 
	private HashMap<String, Float> mapRaceRate;
	
	
	public Betting1T(BettingRule rule) throws Exception {
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
		race.multiplyType = RaceEx.MULTIPLY_NORMAL;
		int betMoney = 0;
		
		String betType = rule.getString("type");
		String[] arrBetType2 = rule.getString("type2").split(",");
		
		List<String> keys = new ArrayList<>();
		for (int i = 0; i < arrBetType2.length; i++) {
			int wakuIndex = Integer.parseInt(arrBetType2[i]) - 1;
			String part1 = race.calculatedInfo.nationWiningRank.substring(0,6);
			String part2 = "";
			String key = betType + "_" + arrBetType2[i] + "_" +  
//					race.getJyoCd() +   
					part1  
					+  
					part2
//					+  
//					part3
					;
			keys.add(key);
		}
		
		List<StatResult> statResultList = StatManager.getInstance().getAll(keys);
		if (statResultList == null ) {
			race.skip = "パタン統計情報取得失敗";
			return race;
		} 

		betMoney = (int)(rule.getInt("baseBetAmount") * mapJyoRate.get(race.getJyoCd()) * mapRaceRate.get(race.getRaceNo()));
		for (StatResult statResult : statResultList) {
			Float odds = ResultOddsManager.getInstance().getOddsValue(race.getJyoCd(), race.getRaceNo(), betType, statResult.kumiban);
			if (odds == null || odds <=1.3) {
				race.skip = "ｵｯｽﾞ不足";
				return race;
			}
			
			if (!race.calculatedInfo.nationWiningRank.substring(0,1).equals("1")) {
				race.skip = "ｵｯｽﾞ不足";
				return race;
			}
			
			int betPopular = ResultOddsManager.getInstance().getPopular(race.getJyoCd(), race.getRaceNo(), betType, statResult.kumiban);
			
			//race.betlist.add(new Bet(betType, 0f, betMoney, 0, statResult.kumiban, betPopular, statResult.patternCnt));
		}
		
		return race;
	}
}

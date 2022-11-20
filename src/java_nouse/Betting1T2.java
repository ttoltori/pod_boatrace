package com.pengkong.boatrace.service.worker.betting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.StatResult;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.mybatis.entity.PowerAvgtimeBettype;
import com.pengkong.boatrace.mybatis.entity.PowerJyoMotorBettype;
import com.pengkong.boatrace.mybatis.entity.PowerRacerBettype;
import com.pengkong.boatrace.service.manager.PowerManagerAvgtime;
import com.pengkong.boatrace.service.manager.PowerManagerJyoMotor;
import com.pengkong.boatrace.service.manager.PowerManagerRacer;
import com.pengkong.boatrace.service.manager.StatManager;
import com.pengkong.boatrace.simulation.model.Bet;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.boatrace.util.BoatUtil;

public class Betting1T2 implements Betting{

	BettingRule rule;
	private HashMap<String, Float> mapJyoRate; 
	private HashMap<String, Float> mapRaceRate;
	
	public Betting1T2(BettingRule rule) throws Exception {
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
	
//	@Override
//	public RaceEx createAndSetBetList(RaceEx race) throws Exception {
//		race.multiplyType = RaceEx.MULTIPLY_NORMAL;
//		int betMoney = 0;
//		
//		String betType = rule.getString("type");
//		String[] arrBetType2 = rule.getString("type2").split(",");
//		
//		List<String> keys = new ArrayList<>();
//		for (int i = 0; i < arrBetType2.length; i++) {
//			int wakuIndex = Integer.parseInt(arrBetType2[i]) - 1;
//			String part1 = race.calculatedInfo.nationWiningRank.substring(0,6);
//			//String part1 = race.wakuList.get(0).entry;
//			//String part2 = race.calculatedInfo.localWiningRank.substring(0,2);
//			
//			String part2 = "";
//			//part2 = String.valueOf(race.raceInfo.no);
//			//String part2 = race.wakuList.get(1).entry;
//			//String part2 = race.wakuList.get(0).motorNo;
////			String part3 = race.calculatedInfo.exhibitRank.substring(0,2);
//			//String motorno = race.calculatedInfo.motor2Rank.substring(0,1);
//			String key = betType + "_" + arrBetType2[i] + "_" +  
//					race.getJyoCd() +   
//					part1  
//					+  
//					part2
////					+  
////					part3
//					;
//			keys.add(key);
//		}
//		
//		List<StatResult> statResultList = StatManager.getInstance().getMostAverageIncomeRate(keys);
//		if (statResultList == null ) {
//			race.skip = "パタン統計情報取得失敗";
//			return race;
//		} 
//
//		betMoney = (int)(rule.getInt("baseBetAmount") * mapJyoRate.get(race.getJyoCd()) * mapRaceRate.get(race.getRaceNo()));
//		for (StatResult statResult : statResultList) {
//			Float odds = OddsManager.getInstance().getOddsValue(race.getJyoCd(), race.getRaceNo(), betType, statResult.kumiban);
//			if (odds == null || odds <= 1.99) {
//				race.skip = "ｵｯｽﾞ不足";
//				return race;
//			}
//			race.betlist.add(new Bet(betType, 0f, betMoney, 0, statResult.kumiban));
//		}
//		
//		return race;
//	}

	
	@Override
	public RaceEx createAndSetBetList(RaceEx race) throws Exception {
		race.multiplyType = RaceEx.MULTIPLY_NORMAL;
		int betMoney = 0;
		
		
		String betType = rule.getString("type");
		String yesterday = BoatUtil.yesterdayYmd(race.raceInfo.ymd);
		String keyPrefix = yesterday + "_" + "1TF" + "_" + race.getJyoCd() + "_";
		String pattern = "";
		String key;
		for (int i = 0; i < 6; i++) {
			Waku waku = race.wakuList.get(i);
			key = keyPrefix + waku.entry + "_" + String.valueOf(i+1);
			PowerRacerBettype power = PowerManagerRacer.getInstance().getPowerRacerBettype(key);
			if (power == null) {
				race.skip = "選手能力値取得失敗";
				return race;
			}
			
			pattern += String.format("%02d", power.getPowerrank()) + "-";
		}
		pattern = pattern.substring(0, pattern.length() - 1);
		
		key = betType + "_" + race.getJyoCd() + pattern;
		List<String> keys = new ArrayList<>();
		keys.add(key);
		List<StatResult> statResultList = StatManager.getInstance().getAll(keys);
		if (statResultList == null) {
			race.skip = "パタン統計情報取得失敗";
			return race;
		}
		
		betMoney = (int)(rule.getInt("baseBetAmount") * mapJyoRate.get(race.getJyoCd()) * mapRaceRate.get(race.getRaceNo()));
		for (StatResult statResult : statResultList) {
//			int premium = 0;
//			int p1Waku = Integer.parseInt(statResult.kumiban.substring(0,1));
//			key = keyPrefix +  race.getJyoCd() + "_" +  race.wakuList.get(p1Waku-1).motorNo + "_" + String.valueOf(p1Waku);
//			PowerJyoMotorBettype p1Motor = motorManager.getPowerJyoMotorBettype(key);
//			
//			key = keyPrefix +
//			  Math.ceil( (double)((race.wakuList.get(p1Waku-1).exhibitTime + race.wakuList.get(p1Waku-1).averaeStart)) * 100) / 100 
//			  + "_" + String.valueOf(p1Waku);
//			PowerAvgtimeBettype p1Time = timeManager.getPowerAvgtimeBettype(key);
//			
//			if (p1Motor == null || p1Time == null) {
//				premium--;
//			} else {
//				if (p1Motor.getPowerrank() <= 1) {
//					premium += 1;
//					if (p1Time.getPowerrank() <= 1) {
//						premium += 1;
//					}
//				}
//				
//			}
//			
//			if (premium >= 1) {
//				betMoney = betMoney * 2 * premium;
				race.betlist.add(new Bet(betType, 0f, betMoney, 0, statResult.kumiban));
//			}
			
		}
		
		return race;
	}
}

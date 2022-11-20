package com.pengkong.boatrace.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.util.BoatUtil;

public class RaceEx extends Race {
	public static final int STATUS_WAIT = -1;
	public static final int STATUS_BETTING = 1;
	public static final int STATUS_CLOSED = 2;
	
	/** 倍率適用なし */
	public static final int MULTIPLY_NORMAL = 0;
	/** マーティン法 */
	public static final int MULTIPLY_MARTIN = 1;
	/** 損失回復用 */
	public static final int MULTIPLY_RECOVERY = 2;
	/** 通常ピークタイム */
	public static final int MULTIPLY_POPULAR = 3;
	/** ｵｯｽﾞ下限到達*/
	public static final int MULTIPLY_UNDER_MINIMUM = 4;
	
	/** 現在ステータス */
	public int status = RaceEx.STATUS_WAIT;
	
	/** 倍率適用種類 */
	public int multiplyType = MULTIPLY_NORMAL; 
	
	/**レースソート基準タイム (HHmmSS) */
	public int sortTime = 0;
	
	/** スキップ理由 */
	public String skip = "";
	
	public List<Bet> betlist = new ArrayList<>();
	
	/** 的中時払い戻し金額 */
	public int prize = 0;
	
//	public PopularItem popularItem;
//	
//	public List<PopularItem> popularItemList = new ArrayList<>();
	
	public int getBetMoney() {
		int money = 0;
		if (betlist.size() > 0) {
			for (Bet bet : betlist) {
				money += bet.money;
			}
		}
		return money;
	}
	
	public String getJyoCd() {
		return setu.jyoCd;
	}
	
	public String getRaceNo() {
		return String.valueOf(raceInfo.no);
	}
	
	public String getSimpleInfo() {
		String result = getJyoCd() + "," + setu.jyo + "," + getRaceNo();
		if (betlist.size() > 0) {
			result = result + betlist.get(0).toString();
		}
		
		return result;
	}
	
	public static String getJyoRaceTime(RaceEx race) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append("場:");
		sb.append(race.setu.jyoCd);
		sb.append(" " );
		sb.append(race.setu.jyo);
		sb.append(" レース:");
		sb.append(race.raceInfo.no);
		sb.append(" sortTime:");
		sb.append(String.format("%06d", race.sortTime));
		sb.append(")");
		
		return sb.toString();
	}
	
	public static HashMap<String, String> getWekaInputEntry(RaceEx race) {
		HashMap<String, String> result = new HashMap<>();
		for (int i = 0; i < race.wakuList.size(); i++) {
			String key = "entry" + (i+1);
			result.put(key, race.wakuList.get(i).entry);
		}
		
		return result;
	}
	
	public static HashMap<String, Object> getWekaInputOddsRankGap(String betType, String kumiban, RaceEx race) {
		HashMap<String,Object> map = new HashMap<>();
		OddsItemEx odds =  BeforeOddsManager.getInstance().getOddsItemEx(race.getJyoCd(), race.getRaceNo(), betType, kumiban);
		map.put("jyocd", race.getJyoCd());
		map.put("raceno", race.getRaceNo());
		map.put("turn", BoatUtil.getSetuNumber(race.setu.turn));
		map.put("alevelcount", new Integer(race.raceInfo2.aLevelCount));
		map.put("bet_odds", new Double(odds.value));
		map.put("bet_oddsrank", new Integer(odds.rank));
		
		return map;
	}
	
	
	public ResultInfoSimple getResultInfoSimple(String bettype) {
		ResultInfoSimple result = new ResultInfoSimple();
		result.prize = prize;
		result.odds = new BigDecimal((float)prize / 100f).setScale(2, RoundingMode.FLOOR);
		if (bettype.equals("1T")) {
			result.kumiban = raceResult.tansyoNo;
			result.oddsRank = raceResult.tansyoPopular;
		} else if (bettype.equals("2T")) {
			result.kumiban = raceResult.nirentanNo;
			result.oddsRank = raceResult.nirentanPopular;
		} else if (bettype.equals("2F")) {
			result.kumiban = raceResult.nirenhukuNo;
			result.oddsRank = raceResult.nirenhukuPopular;
		} else if (bettype.equals("3T")) {
			result.kumiban = raceResult.sanrentanNo;
			result.oddsRank = raceResult.sanrentanPopular;
		} else if (bettype.equals("3F")) {
			result.kumiban = raceResult.sanrenhukuNo;
			result.oddsRank = raceResult.sanrenhukuPopular;
		} 
		
		return result;
	}
}

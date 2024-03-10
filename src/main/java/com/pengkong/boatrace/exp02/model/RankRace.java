package com.pengkong.boatrace.exp02.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.exception.NullValueException;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;
import com.pengkong.boatrace.service.manager.JyoManager;

public class RankRace {
	/** RankPredictor와의 인터페이스를 위한 레이스정보 DTO */
	public RankDbRecord recRace;

	/** 레이스 투표/결과 시간정렬을 위한 시메키리시각 초기치는 sime */
	public int sortTime;
	
	/** rankEx에 정의된 레이스의 상태 */
	public int status = RaceEx.STATUS_WAIT; 
	
	/** 투표를 스킵한 이유 */
	public String skip;
	
	//public List<Bet> betlist = new ArrayList<Bet>();
	/** 투표 요청용 베팅맵 
	 * key=betType + "_" + betKumiban(기본포맷)
	 * ex) form3t8_23의 경우 "3T_123","3T_124"...8개 
	 * */
	public List<Bet> betList = new ArrayList<>();
	
	// key=betType + "_" + betKumiban(랭킹베팅포맷) 
	// ex) form3t8_23의 경우 "3T_1=="
	// public HashMapList<RankBet> rankbetList = new HashMapList<>();
	
	/** 해당 레이스의 상금 총액*/
	public int prize;
	
	private static Map<String, String> mapBettypeName = new HashMap<>();
	static {
		mapBettypeName.put("1T", "tansyo");
		mapBettypeName.put("2T", "nirentan");
		mapBettypeName.put("2F", "nirenhuku");
		mapBettypeName.put("3T", "sanrentan");
		mapBettypeName.put("3F", "sanrenhuku");
	}
	
	public RankRace(RankDbRecord rec) {
		this.recRace = rec;
		this.sortTime = Integer.parseInt(rec.getString(DBCol.SIME)) * 100;
	}
	
	public int getBetMoney() {
		int money = 0;
		for (Bet bet : betList) {
			money += bet.money;
		}

		return money;
	}
	
	public String getYmd() {
		return recRace.getString(DBCol.YMD);
	}
	
	public String getJyocd() {
		return recRace.getString(DBCol.JYOCD);
	}
	
	public int getRaceNo() {
		return Integer.parseInt(recRace.getString(DBCol.RACENO));
	}
	
	public int getSime() {
		return Integer.parseInt(recRace.getString(DBCol.SIME));
	}
	
	public String getType() {
		return recRace.getString(DBCol.BETTYPE);
	}
	
	public String getSimpleInfo() {
		return getYmd() + "_" + JyoManager.getJyoCd(getJyocd()) + "_" 
				+ getRaceNo() + "R ";  
	}
	
	public static String getResultKumiban(RankRace race, String betType) {
		 return race.recRace.getString(mapBettypeName.get(betType) + "no");
	}
	
	public static int getPrize(RankRace race, String betType) throws NullValueException {
		return race.recRace.getInt(mapBettypeName.get(betType) + "prize");
	}
}

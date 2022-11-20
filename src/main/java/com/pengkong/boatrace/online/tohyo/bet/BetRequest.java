package com.pengkong.boatrace.online.tohyo.bet;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


/**
 * レース当たりの投票要求。複数のBetを保持する
 * @author ttolt
 *
 */
@Getter
public class BetRequest {
	String jyoCd;
	String raceNo;
	List<Bet> betList;
	
	public BetRequest(String jyoCd, String raceNo) {
		super();
		this.jyoCd = jyoCd;
		this.raceNo = raceNo;
		betList = new ArrayList<>();
	}

	/** Bet追加 */
	public void add(Bet bet) {
		betList.add(bet);
	}

	/** 合計投票金額を取得する */
	public int getBetAmount() {
		int amount = 0;
		for (Bet bet : betList) {
			amount += bet.betAmt;
		}

		return amount;
	}
	
	public String toString() {
		return "BetRequest{" + jyoCd + ", " + raceNo + ", " + betList + "}";
	}
}

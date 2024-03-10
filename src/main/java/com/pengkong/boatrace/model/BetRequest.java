package com.pengkong.boatrace.model;

import java.util.List;

import com.pengkong.boatrace.model.betting.Bet;

public class BetRequest {
	// JSJ 20200201 복수개의 public String kachisiki;
	public String jyoCd;
	public String raceNo;
	public List<Bet> betList;
	
	public int getBetAmount() {
		int amount = 0;
		for (Bet bet : betList) {
			amount += bet.money;
		}
		return amount;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BetRequest{ ");
		sb.append("jyoCd:");
		sb.append(jyoCd);
		sb.append(" raceNo:");
		sb.append(raceNo);
		sb.append(" ");
		for (Bet bet : betList) {
			sb.append(bet.toString());
			sb.append(" ");
		}
		sb.append("totalBetAmount: " + getBetAmount());
		sb.append(" }");
		return sb.toString();
	}
}

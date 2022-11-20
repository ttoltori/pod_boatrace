package com.pengkong.boatrace.model;

import java.util.List;

public class DistamoItem {
	public String kumiban;
	public float odds;
	public int betAmount;
	
	public DistamoItem(String kumiban, float odds, int betAmount) {
		super();
		this.kumiban = kumiban;
		this.odds = odds;
		this.betAmount = betAmount;
	}
	
	public static int sumOfBetAmount(List<DistamoItem> distamoItemList) {
		int sum = 0;
		for (DistamoItem di : distamoItemList) {
			sum += di.betAmount;
		}
		
		return sum;
	}
//	public DistamoItem(String kumiban, float odds) {
//		super();
//		this.kumiban = kumiban;
//		this.odds = odds;
//	}
//	
//	public DistamoItem(OddsTItem oddsItem) {
//		super();
//		this.kumiban = oddsItem.kumiban;
//		this.odds = oddsItem.value;
//	}
}

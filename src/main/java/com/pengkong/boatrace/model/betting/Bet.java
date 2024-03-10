package com.pengkong.boatrace.model.betting;

/**
 * 베팅1개를 보관하는 베팅클래스
 * 
 * @author qwerty
 *
 */
public class Bet {
	// 3T ～ 1F
	public String type;
	public int money;
	public String kumiban;
	public Float odds= 0f;
	public int oddsRank = 0;
	public Bet(String type, String kumiban, int money, Float odds, int oddsRank) {
		this.type = type;
		this.kumiban = kumiban;
		this.odds = odds;
		this.money = money;
		this.oddsRank = oddsRank;
	}
	
	public Bet(String type, String kumiban, int money) {
		this.type = type;
		this.kumiban = kumiban;
		this.money = money;
	}
	
	public Bet() {
	}
	
	/**
	 * 투표요청API포맷의 구미방을 반환한다.
	 * @return
	 */
	public String getTohyoBetKumiban() {
		return String.join("-", kumiban.split(""));
	}
	
	public String getKey() {
		return type + "_" + kumiban;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bet{");
		sb.append(type); sb.append("/");
		sb.append(kumiban); sb.append("/");
		sb.append(money); sb.append("/");
		sb.append(odds); sb.append("/");
		sb.append(oddsRank); sb.append("}");
		return sb.toString();
	}
}

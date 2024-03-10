package com.pengkong.boatrace.online.tohyo.bet;

import lombok.Getter;

/**
 * betting 一個
 * @author ttolt
 *
 */
@Getter
public class Bet {
	String betType;
	String kumiban;
	int betAmt;
	//Odds odds;
	
	public Bet(String betType, String kumiban, int betAmt) {
		super();
		this.betType = betType;
		this.kumiban = kumiban;
		this.betAmt = betAmt;
	}
	
	/**
	 * 투표요청API포맷의 구미방을 반환한다.
	 * @return
	 */
	public String getTohyoBetKumiban() {
		return String.join("-", kumiban.split(""));
	}
	
	public String toString() {
		return "Bet{" + betType + ", " + kumiban + ", " + betAmt + "}";
	}
}

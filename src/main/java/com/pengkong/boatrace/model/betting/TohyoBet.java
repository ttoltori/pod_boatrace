package com.pengkong.boatrace.model.betting;

import java.util.ArrayList;
import java.util.List;

/**
 * 베팅1개를 보관하면서 근거가 된 RankBet를 복수개 보관하는 베팅클래스`
 * @author qwerty
 *
 */
public class TohyoBet extends Bet {
	
	public List<RankBet> rankbetList = new ArrayList<RankBet>();
	public TohyoBet(String type, String kumiban, int money, Float odds, int oddsRank) {
		super(type, kumiban, money, odds, oddsRank);
	}

	public TohyoBet(String type, String kumiban, int money, RankBet rankBet) {
		super(type, kumiban, money);
		this.rankbetList.add(rankBet);
	}
	
	/**
	 * 포함된 RankBet의 통상투표, 포메이션투표 갯수를 반환한다.
	 * @return
	 */
	public int[] getPatternCnt() {
		int[] result = {0,0}; 
		for (RankBet bet : rankbetList) {
			if (bet.rankType.startsWith("norm")) {
				result[0]++;
			} else {
				result[1]++;
			}
		}
		return result;
	}
	
	public TohyoBet() {
	}

}

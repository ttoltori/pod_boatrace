package com.pengkong.boatrace.model.betting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.common.RankType;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.common.PropertyUtil;

/**
 * rank123방식의 rank_filter레코드를 투표매니저에 넘기기위한 Bet리스트로 변환하는 클래스
 * @author qwerty
 *
 */
public class RankBetConverter {

	PropertyUtil prop = PropertyUtil.getInstance();
	BeforeOddsManager oddsManager = BeforeOddsManager.getInstance();
	private RankBet rankBet = null;
	
	public RankBetConverter() {
		// TODO Auto-generated constructor stub
	}
	
	public List<TohyoBet> convert(RankBet rankBet) {
		this.rankBet = rankBet;
		
		RankDbRecord recFilter = rankBet.recFilter; // 예측엔진을 거친 필터 정보
		List<TohyoBet> result = new ArrayList<>();
		String betType = rankBet.betType;
		String betKumiban = rankBet.betKumiban;
		String waku = recFilter.getString(DBCol.WAKU);
		
		String rankType = rankBet.rankType;
		// 투표금액을 랭크타입으로 취득 doBetting에서 베팅금액룰에 따라 투표금액은 바뀔 수 있다.
		// int money = prop.getInteger("betMoney_" + rankType);
		// 랭크타입으로 베팅금액을 설정하면 bettype에서 충돌이 나므로 RankWorker에서 bettype으로 베팅금액 설정한다.
		int money = 0;
		
		if (rankType.equals(RankType.FORM_2T2_23)) {
			result.addAll(createBetlistForm2t2_23(betType, betKumiban, money, waku));
		} else if (rankType.equals(RankType.FORM_3T4_3)) {
			result.addAll(createBetlistForm3t4_3(betType, betKumiban, money));
		} else if (rankType.equals(RankType.FORM_3T4_3)) {
			result.addAll(createBetlistForm3t8_23(betType, betKumiban, money, waku));
		} else {
			result.add(createBetlistNorm(betType, betKumiban, money));
		}
		
		return result;
	}
	
	private TohyoBet createBetlistNorm(String betType, String betKumiban, int money) {
		return new TohyoBet(betType, betKumiban, money, this.rankBet);
	}

	
	private List<TohyoBet> createBetlistForm2t2_23(String betType, String betKumiban, int money, String waku) {
		List<TohyoBet> result = new ArrayList<TohyoBet>();
		String kumiban;
		
		// bet 1
		kumiban = betKumiban.substring(0,1) + waku.substring(0,1);
		result.add(new TohyoBet(betType, kumiban, money, this.rankBet));
		
		// bet 2
		kumiban = betKumiban.substring(0,1) + waku.substring(1,2);
		result.add(new TohyoBet(betType, kumiban, money, this.rankBet));
		
		return result;
	}
	
	private List<TohyoBet> createBetlistForm3t4_3(String betType, String betKumiban, int money) {
		List<TohyoBet> result = new ArrayList<TohyoBet>();
		String kumiban;
		
		List<String> listWaku = new ArrayList<>(Arrays.asList("1","2","3","4","5","6"));
		listWaku.remove(betKumiban.substring(0,1));
		listWaku.remove(betKumiban.substring(1,2));
		
		// bet 1,2,3,4
		for (String waku : listWaku) {
			kumiban = betKumiban.substring(0,2) + waku;
			result.add(new TohyoBet(betType, kumiban, money, this.rankBet));
		}
		
		return result;
	}
	
	private List<TohyoBet> createBetlistForm3t8_23(String betType, String betKumiban, int money, String waku) {
		List<TohyoBet> result = new ArrayList<TohyoBet>();
		String kumiban;
		
		// rank1예측치+waku2->rank2로 베팅4개 생성
		String rank1 = betKumiban.substring(0,1);
		String rank2 = waku.substring(0,1);
		List<String> listWaku = new ArrayList<>(Arrays.asList("1","2","3","4","5","6"));
		listWaku.remove(rank1);
		listWaku.remove(rank2);

		// bet 1,2,3,4
		for (String rank3 : listWaku) {
			kumiban = rank1 + rank2 + rank3;
			result.add(new TohyoBet(betType, kumiban, money, this.rankBet));
		}

		// rank1예측치+waku3->rank2로 베팅4개 생성
		rank2 = waku.substring(1,2);
		listWaku = new ArrayList<>(Arrays.asList("1","2","3","4","5","6"));
		listWaku.remove(rank1);
		listWaku.remove(rank2);
		
		// bet 5,6,7,8
		for (String rank3 : listWaku) {
			kumiban = rank1 + rank2 + rank3;
			result.add(new TohyoBet(betType, kumiban, money, this.rankBet));
		}
		
		return result;
	}
}

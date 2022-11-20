package com.pengkong.boatrace.exp02.betting;

import java.util.List;

import com.pengkong.boatrace.exp02.model.RankRace;
import com.pengkong.boatrace.model.betting.RankBet;

public interface RankBetting {
	public List<RankBet> createRankBetList(RankRace race) throws Exception;
}

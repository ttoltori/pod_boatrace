package com.pengkong.boatrace.exp02.betting;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.exp02.RankPredictor;
import com.pengkong.boatrace.exp02.model.RankRace;
import com.pengkong.boatrace.model.betting.RankBet;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;

public class Rank123Betting implements RankBetting {

	RankPredictor predictor = RankPredictor.getInstance();

	public Rank123Betting() {
	}

	@Override
	public List<RankBet> createRankBetList(RankRace race) throws Exception {
		List<RankDbRecord> listRankFilter = predictor.createRankFilterList(race.recRace);
		List<RankBet> result = new ArrayList<RankBet>();
		for (RankDbRecord recFilter : listRankFilter) {
			result.add(new RankBet(recFilter));
		}

		return result;
	}
}

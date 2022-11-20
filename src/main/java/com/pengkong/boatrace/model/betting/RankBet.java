package com.pengkong.boatrace.model.betting;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;

/**
 * rank_filter의 한레코드를 보관하는 베팅클래스
 * 
 * @author qwerty
 *
 */
public class RankBet {
	public RankDbRecord recFilter;
	public String rankType;
	public String betType;
	public String betKumiban;
	public RankBet(RankDbRecord recFilter) {
		this.recFilter = recFilter;
		this.rankType = recFilter.getString(DBCol.RANKTYPE);
		this.betType = recFilter.getString(DBCol.BETTYPE);
		this.betKumiban = recFilter.getString(DBCol.BET_KUMIBAN);
	}
	
	public String getKey() {
		return betType + "_" + betKumiban;
	}
}

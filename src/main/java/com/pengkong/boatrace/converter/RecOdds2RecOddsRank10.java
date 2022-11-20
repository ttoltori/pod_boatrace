package com.pengkong.boatrace.converter;

import com.pengkong.boatrace.mybatis.entity.RecOdds;
import com.pengkong.boatrace.mybatis.entity.RecOddsRank10;

public class RecOdds2RecOddsRank10 {
	public static RecOddsRank10 convert(RecOdds recOdds) {
		RecOddsRank10 recOddsRank10 = new RecOddsRank10();
		
		recOddsRank10.setYmd(recOdds.getYmd());
		recOddsRank10.setJyocd(recOdds.getJyocd());
		recOddsRank10.setRaceno(recOdds.getRaceno());
		recOddsRank10.setBettype(recOdds.getBettype());
		
		return recOddsRank10;
		
	}
}

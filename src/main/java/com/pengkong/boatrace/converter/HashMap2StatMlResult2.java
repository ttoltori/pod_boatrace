package com.pengkong.boatrace.converter;

import java.math.BigDecimal;
import java.util.HashMap;

import com.pengkong.boatrace.mybatis.entity.StatMlResult2;

public class HashMap2StatMlResult2 {

	public static StatMlResult2 convert(HashMap<String, ?> map) {
		StatMlResult2 rec = new StatMlResult2();
		rec.setYmd((String)map.get("ymd"));
		rec.setSime((String)map.get("sime"));
		rec.setJyocd((String)map.get("jyocd"));
		rec.setRaceno(((Integer)map.get("raceno")).shortValue());
		rec.setDescription((String)map.get("description"));
		rec.setBettype((String)map.get("bettype"));
		rec.setBetKumiban((String)map.get("bet_kumiban"));
		rec.setBetOdds((BigDecimal)map.get("bet_odds"));
		rec.setBetOddsrank((Integer)map.get("bet_oddsrank"));
		rec.setResultKumiban((String)map.get("result_kumiban"));
		rec.setResultOdds((BigDecimal)map.get("result_odds"));
		rec.setResultOddsrank((Integer)map.get("result_oddsrank"));
		rec.setHity((Integer)map.get("hity"));
		rec.setHitn((Integer)map.get("hitn"));
		rec.setBetamt((Integer)map.get("betamt"));
		rec.setHitamt((Integer)map.get("hitamt"));

		return rec;
	}
}

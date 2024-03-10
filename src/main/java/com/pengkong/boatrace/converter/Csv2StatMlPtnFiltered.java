package com.pengkong.boatrace.converter;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.mybatis.entity.StatMlPtnFiltered;

public class Csv2StatMlPtnFiltered {

	public Csv2StatMlPtnFiltered() {
		// TODO Auto-generated constructor stub
	}

	public static StatMlPtnFiltered convert(String csvLine) {
		StatMlPtnFiltered spf = new StatMlPtnFiltered();
		String[] token = csvLine.split(BoatTemplate.CSV_DELIMITER);
		
		spf.setBettype(token[0]);
		spf.setKumiban(token[1]);
		spf.setPatternName(token[2]);
		spf.setBetcountDaily(Integer.parseInt(token[3]));
		spf.setBetamt(Integer.parseInt(token[4]));
		spf.setIncomeamt(Integer.parseInt(token[5]));
		spf.setHitrate(Double.parseDouble(token[6]));
		spf.setIncomerate(Double.parseDouble(token[7]));
		spf.setTotalrate(Double.parseDouble(token[8]));
		spf.setMinOddsrank(Integer.parseInt(token[9]));
		spf.setMaxOddsrank(Integer.parseInt(token[10]));
		spf.setMinOdds(Double.parseDouble(token[11]));
		spf.setMaxOdds(Double.parseDouble(token[12]));
		spf.setPatternValue(token[13]);
		spf.setPtnBetamt(Integer.parseInt(token[14]));
		spf.setPtnIncomeamt(Integer.parseInt(token[15]));
		spf.setPtnHitrate(Double.parseDouble(token[16]));
		spf.setPtnIncomerate(Double.parseDouble(token[17]));
		spf.setPtnTotalrate(Double.parseDouble(token[18]));
		spf.setPtnDailyBetCount(Integer.parseInt(token[19]));
		spf.setPtnDailyPlusRate(Double.parseDouble(token[20]));
		spf.setHitrateSlope(Double.parseDouble(token[21]));
		spf.setIncomerateSlope(Double.parseDouble(token[22]));
		spf.setTotalrateSlope(Double.parseDouble(token[23]));
	
		return spf;
	}
}

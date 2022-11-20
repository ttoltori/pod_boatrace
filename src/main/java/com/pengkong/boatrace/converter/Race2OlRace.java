package com.pengkong.boatrace.converter;

import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceInfo;
import com.pengkong.boatrace.model.RaceInfo2;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.StringUtil;

public class Race2OlRace {

	public static OlRace convert(Race race) {
		OlRace rec = new OlRace();
		
		RaceInfo ri = race.raceInfo;
		RaceInfo2 ri2 = race.raceInfo2;
		rec.setYmd(ri.ymd);
		rec.setJyocd(race.setu.jyoCd);
		rec.setRaceno(ri.no);
		rec.setSime(String.format("%04d", ri.sime));
		rec.setGrade(race.setu.grade);
		rec.setTimezone(StringUtil.isEmpty(race.setu.timezone) ? "Z" : race.setu.timezone); // 20181125
		rec.setTurn(getSetuNumber(race.setu.turn));
		rec.setFixedentrance( BoatUtil.getFixedEntranceSimple(ri2.fixedEntrance) );
		rec.setRacetype( BoatUtil.getRacetypeSimpleExp10(ri2.raceType));
		rec.setWakulevellist(ri2.wakuLevelList);
		rec.setAlevelcount(ri2.aLevelCount);
		rec.setFemalecount(ri2.femaleCount);
		rec.setComPredict( race.computerBetting.predict.replaceAll("-", "") );
		rec.setComConfidence(race.computerBetting.confidence);
		rec.setStatus(0);
		
		return rec;
	}
	
	private static String getSetuNumber(String setu) {
		if (setu.equals("初日")) {
			return "1";
		} else if (setu.equals("２日目")) {
			return "2";
		} else if (setu.equals("３日目")) {
			return "3";
		} else if (setu.equals("４日目")) {
			return "4";
		} else if (setu.equals("５日目")) {
			return "5";
		} else if (setu.equals("６日目")) {
			return "6";
		} else if (setu.equals("最終日")) {
			return "7";
		} else {
			return "0";
		}
	}
	
}

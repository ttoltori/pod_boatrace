package com.pengkong.boatrace.converter;

import java.math.BigDecimal;

import com.pengkong.boatrace.model.CalculatedInfo;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.RaceInfo;
import com.pengkong.boatrace.model.RaceInfo2;
import com.pengkong.boatrace.model.RaceResult;
import com.pengkong.boatrace.model.Setu;
import com.pengkong.boatrace.mybatis.entity.RecRace;
import com.pengkong.common.StringUtil;

public class RaceEx2RecRace {

	public static RecRace convert(RaceEx race) {
		RecRace rec = new RecRace();
		
		RaceInfo ri = race.raceInfo;
		rec.setRaceno((short)ri.no);
		rec.setYmd(ri.ymd);
		rec.setSime(String.format("%04d", ri.sime));
		rec.setTemparature(BigDecimal.valueOf(ri.temparature));
		rec.setWeather(ri.weather);
		rec.setWinddirection(Short.valueOf(ri.windDirection));
		rec.setWind(BigDecimal.valueOf(ri.wind));
		rec.setWatertemp(BigDecimal.valueOf(ri.waterTemparature));
		rec.setWave((short)(ri.wave));
		
		Setu setu = race.setu;
		rec.setJyocd(setu.jyoCd);
		rec.setGrade(setu.grade);
		rec.setIsvenus(StringUtil.isEmpty(setu.isVenus) ? "N" : setu.isVenus); // 20181125
		rec.setTimezone(StringUtil.isEmpty(setu.timezone) ? "Z" : setu.timezone); // 20181125
		rec.setTurn(getSetuNumber(setu.turn));
		
		RaceResult result = race.raceResult;
		rec.setSanrentanno(result.sanrentanNo);
		rec.setSanrentanprize(result.sanrentanPrize);
		rec.setSanrentanpopular((short)result.sanrentanPopular);
		rec.setSanrenhukuno(result.sanrenhukuNo);
		rec.setSanrenhukuprize(result.sanrenhukuPrize);
		rec.setSanrenhukupopular((short)result.sanrenhukuPopular);
		rec.setNirentanno(result.nirentanNo);
		rec.setNirentanprize(result.nirentanPrize);
		rec.setNirentanpopular((short)result.nirentanPopular);
		rec.setNirenhukuno(result.nirenhukuNo);
		rec.setNirenhukuprize(result.nirenhukuPrize);
		rec.setNirenhukupopular((short)result.nirenhukuPopular);
		rec.setTansyono(result.tansyoNo);
		rec.setTansyoprize(result.tansyoPrize);
		rec.setTansyopopular((short)result.tansyoPopular);
		rec.setKimarite(result.kimariTe);
		
		CalculatedInfo ci = race.calculatedInfo;
		rec.setWakurank(ci.wakuRank);
		rec.setLevelrank(ci.levelRank);
		rec.setResultlevelrank(ci.resultLevelRank);
		rec.setNationwiningrank(ci.nationWiningRank);
		rec.setNation2winingrank(ci.nation2WiningRank);
		rec.setNation3winingrank(ci.nation3WiningRank);
		rec.setLocalwiningrank(ci.localWiningRank);
		rec.setLocal2winingrank(ci.local2WiningRank);
		rec.setLocal3winingrank(ci.local3WiningRank);
		rec.setMotor2rank(ci.motor2Rank);
		rec.setMotor3rank(ci.motor3Rank);
		rec.setStartexhibitrank(ci.startExhibitRank);
		rec.setExhibitrank(ci.exhibitRank);
		rec.setAveragestartrank(ci.averageStartRank);
		
		RaceInfo2 ri2 = race.raceInfo2;
		rec.setFixedentrance(ri2.fixedEntrance);
		rec.setRacetype(ri2.raceType);
		rec.setWakulevellist(ri2.wakuLevelList);
		rec.setAlevelcount((short)ri2.aLevelCount);
		rec.setFemalecount((short)ri2.femaleCount);
		
		rec.setAvgstcondrank(ri2.avgstCondRank);
		rec.setSetuwinrank(ri2.setuwinRank);
		rec.setFlrank(ri2.flRank);
		// 20210712 added
		rec.setComPredict(race.computerBetting.predict);
		rec.setComConfidence((short)race.computerBetting.confidence);
		
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

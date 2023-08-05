package com.pengkong.boatrace.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.boatrace.util.BoatUtil;

public class RaceEx2RecRacerList {
	public static List<RecRacer> convert(RaceEx race) {
		List<RecRacer> result = new ArrayList<>();
		RecRacer rec;
		for (int i = 0; i < race.wakuList.size(); i++) {
			Waku waku = race.wakuList.get(i);
			rec = new RecRacer();
			rec.setJyocd(race.setu.jyoCd);
			rec.setRaceno((short)race.raceInfo.no);
			rec.setEntry(BoatUtil.toShort(waku.entry));
			rec.setYmd(race.raceInfo.ymd);
			rec.setSex(waku.sex);
			rec.setAge((short)waku.age);
			rec.setLevel(waku.level);
			rec.setWeight(BigDecimal.valueOf(waku.weight));
			rec.setBranch(waku.branch);
			rec.setExhibit(BigDecimal.valueOf(waku.exhibitTime));
			rec.setStartexhibit(BigDecimal.valueOf(waku.startExhibit));
			rec.setFlying((short)waku.flying);
			rec.setLate((short)waku.late);
			rec.setAveragestart(BigDecimal.valueOf(waku.averageStart));
			rec.setAvgtime(BigDecimal.valueOf(waku.exhibitTime + waku.averageStart));
			rec.setNationwiningrate(BigDecimal.valueOf(waku.nationWiningRate));
			rec.setNation2winingrate(BigDecimal.valueOf(waku.nation2WiningRate));
			rec.setNation3winingrate(BigDecimal.valueOf(waku.nation3WiningRate));
			rec.setLocalwiningrate(BigDecimal.valueOf(waku.localWiningRate));
			rec.setLocal2winingrate(BigDecimal.valueOf(waku.local2WiningRate));
			rec.setLocal3winingrate(BigDecimal.valueOf(waku.local3WiningRate));
			rec.setMotorno(BoatUtil.toShort(waku.motorNo));
			rec.setMotor2winingrate(BigDecimal.valueOf(waku.motor2WiningRate));
			rec.setMotor3winingrate(BigDecimal.valueOf(waku.motor3WiningRate));
			rec.setBoatno(BoatUtil.toShort(waku.boatNo));
			rec.setBoat2winingrate(BigDecimal.valueOf(waku.boat2WiningRate));
			rec.setBoat3winingrate(BigDecimal.valueOf(waku.boat3WiningRate));
			rec.setWaku((short)waku.no);
			rec.setRank((short)waku.resulRank);
			rec.setStartresult(BigDecimal.valueOf(waku.resultStart));
			rec.setGrade(race.setu.grade);
			
			// 20230608
			rec.setRacetype(RaceEx2RecRace.getRaceType(race.raceInfo2.raceType));
			
			result.add(rec);
		}
		
		return result;
	}
}

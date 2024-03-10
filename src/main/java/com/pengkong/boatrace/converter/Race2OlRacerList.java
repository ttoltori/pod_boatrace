package com.pengkong.boatrace.converter;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.mybatis.entity.OlRacer;
import com.pengkong.boatrace.util.BoatUtil;

public class Race2OlRacerList {
	public static List<OlRacer> convert(Race race) {
		List<OlRacer> result = new ArrayList<>();
		OlRacer rec;
		for (int i = 0; i < race.wakuList.size(); i++) {
			Waku waku = race.wakuList.get(i);
			rec = new OlRacer();
			rec.setJyocd(race.setu.jyoCd);
			rec.setRaceno(race.raceInfo.no);
			rec.setEntry(BoatUtil.toInt(waku.entry));
			rec.setYmd(race.raceInfo.ymd);
			rec.setSex(waku.sex);
			rec.setAge(waku.age);
			rec.setLevel(waku.level);
			rec.setWeight(Double.valueOf(waku.weight));
			rec.setBranch(waku.branch);
			rec.setExhibit(Double.valueOf(waku.exhibitTime));
			rec.setStartexhibit(Double.valueOf(waku.startExhibit));
			rec.setFlying(waku.flying);
			rec.setLate(waku.late);
			rec.setAveragestart(Double.valueOf(waku.averageStart));
			rec.setAvgtime(Double.valueOf(waku.exhibitTime + waku.averageStart));
			rec.setNationwiningrate(Double.valueOf(waku.nationWiningRate));
			rec.setNation2winingrate(Double.valueOf(waku.nation2WiningRate));
			rec.setNation3winingrate(Double.valueOf(waku.nation3WiningRate));
			rec.setLocalwiningrate(Double.valueOf(waku.localWiningRate));
			rec.setLocal2winingrate(Double.valueOf(waku.local2WiningRate));
			rec.setLocal3winingrate(Double.valueOf(waku.local3WiningRate));
			rec.setMotorno(BoatUtil.toInt(waku.motorNo));
			rec.setMotor2winingrate(Double.valueOf(waku.motor2WiningRate));
			rec.setMotor3winingrate(Double.valueOf(waku.motor3WiningRate));
			rec.setBoatno(BoatUtil.toInt(waku.boatNo));
			rec.setBoat2winingrate(Double.valueOf(waku.boat2WiningRate));
			rec.setBoat3winingrate(Double.valueOf(waku.boat3WiningRate));
			rec.setWaku(waku.no);
			rec.setGrade(race.setu.grade);
			
			// 20230608
			rec.setRacetype(RaceEx2RecRace.getRaceType(race.raceInfo2.raceType));
			
			result.add(rec);
		}
		
		return result;
	}
}

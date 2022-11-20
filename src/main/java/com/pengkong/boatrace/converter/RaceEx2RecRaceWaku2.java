package com.pengkong.boatrace.converter;

import java.math.BigDecimal;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.RaceInfo;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.mybatis.entity.RecRaceWaku2;

public class RaceEx2RecRaceWaku2 {

	public static RecRaceWaku2 convert(RaceEx race) {
		RecRaceWaku2 rec = new RecRaceWaku2();
		
		RaceInfo ri = race.raceInfo;
		rec.setYmd(ri.ymd);
		rec.setJyocd(race.getJyoCd());
		rec.setRaceno((short)ri.no);
		
		Waku waku1 = race.wakuList.get(0);
		Waku waku2 = race.wakuList.get(1);
		Waku waku3 = race.wakuList.get(2);
		Waku waku4 = race.wakuList.get(3);
		Waku waku5 = race.wakuList.get(4);
		Waku waku6 = race.wakuList.get(5);

		rec.setNationwiningrate1(BigDecimal.valueOf(waku1.nationWiningRate));
		rec.setNationwiningrate2(BigDecimal.valueOf(waku2.nationWiningRate));
		rec.setNationwiningrate3(BigDecimal.valueOf(waku3.nationWiningRate));
		rec.setNationwiningrate4(BigDecimal.valueOf(waku4.nationWiningRate));
		rec.setNationwiningrate5(BigDecimal.valueOf(waku5.nationWiningRate));
		rec.setNationwiningrate6(BigDecimal.valueOf(waku6.nationWiningRate));
		
		rec.setNation2winingrate1(BigDecimal.valueOf(waku1.nation2WiningRate));
		rec.setNation2winingrate2(BigDecimal.valueOf(waku2.nation2WiningRate));
		rec.setNation2winingrate3(BigDecimal.valueOf(waku3.nation2WiningRate));
		rec.setNation2winingrate4(BigDecimal.valueOf(waku4.nation2WiningRate));
		rec.setNation2winingrate5(BigDecimal.valueOf(waku5.nation2WiningRate));
		rec.setNation2winingrate6(BigDecimal.valueOf(waku6.nation2WiningRate));

		rec.setNation3winingrate1(BigDecimal.valueOf(waku1.nation3WiningRate));
		rec.setNation3winingrate2(BigDecimal.valueOf(waku2.nation3WiningRate));
		rec.setNation3winingrate3(BigDecimal.valueOf(waku3.nation3WiningRate));
		rec.setNation3winingrate4(BigDecimal.valueOf(waku4.nation3WiningRate));
		rec.setNation3winingrate5(BigDecimal.valueOf(waku5.nation3WiningRate));
		rec.setNation3winingrate6(BigDecimal.valueOf(waku6.nation3WiningRate));
		
		rec.setLocalwiningrate1(BigDecimal.valueOf(waku1.localWiningRate));
		rec.setLocalwiningrate2(BigDecimal.valueOf(waku2.localWiningRate));
		rec.setLocalwiningrate3(BigDecimal.valueOf(waku3.localWiningRate));
		rec.setLocalwiningrate4(BigDecimal.valueOf(waku4.localWiningRate));
		rec.setLocalwiningrate5(BigDecimal.valueOf(waku5.localWiningRate));
		rec.setLocalwiningrate6(BigDecimal.valueOf(waku6.localWiningRate));
		
		rec.setLocal2winingrate1(BigDecimal.valueOf(waku1.local2WiningRate));
		rec.setLocal2winingrate2(BigDecimal.valueOf(waku2.local2WiningRate));
		rec.setLocal2winingrate3(BigDecimal.valueOf(waku3.local2WiningRate));
		rec.setLocal2winingrate4(BigDecimal.valueOf(waku4.local2WiningRate));
		rec.setLocal2winingrate5(BigDecimal.valueOf(waku5.local2WiningRate));
		rec.setLocal2winingrate6(BigDecimal.valueOf(waku6.local2WiningRate));

		rec.setLocal3winingrate1(BigDecimal.valueOf(waku1.local3WiningRate));
		rec.setLocal3winingrate2(BigDecimal.valueOf(waku2.local3WiningRate));
		rec.setLocal3winingrate3(BigDecimal.valueOf(waku3.local3WiningRate));
		rec.setLocal3winingrate4(BigDecimal.valueOf(waku4.local3WiningRate));
		rec.setLocal3winingrate5(BigDecimal.valueOf(waku5.local3WiningRate));
		rec.setLocal3winingrate6(BigDecimal.valueOf(waku6.local3WiningRate));

		rec.setMotor2winingrate1(BigDecimal.valueOf(waku1.motor2WiningRate));
		rec.setMotor2winingrate2(BigDecimal.valueOf(waku2.motor2WiningRate));
		rec.setMotor2winingrate3(BigDecimal.valueOf(waku3.motor2WiningRate));
		rec.setMotor2winingrate4(BigDecimal.valueOf(waku4.motor2WiningRate));
		rec.setMotor2winingrate5(BigDecimal.valueOf(waku5.motor2WiningRate));
		rec.setMotor2winingrate6(BigDecimal.valueOf(waku6.motor2WiningRate));

		rec.setMotor3winingrate1(BigDecimal.valueOf(waku1.motor3WiningRate));
		rec.setMotor3winingrate2(BigDecimal.valueOf(waku2.motor3WiningRate));
		rec.setMotor3winingrate3(BigDecimal.valueOf(waku3.motor3WiningRate));
		rec.setMotor3winingrate4(BigDecimal.valueOf(waku4.motor3WiningRate));
		rec.setMotor3winingrate5(BigDecimal.valueOf(waku5.motor3WiningRate));
		rec.setMotor3winingrate6(BigDecimal.valueOf(waku6.motor3WiningRate));

		return rec;
	}
}

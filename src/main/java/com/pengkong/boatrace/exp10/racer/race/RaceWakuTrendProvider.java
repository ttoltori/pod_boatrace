package com.pengkong.boatrace.exp10.racer.race;

import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.mybatis.entity.RecRacerTrend;

public class RaceWakuTrendProvider {

	// key = entry
	HashMap<Short, RacerDayTrend> mapRacer;
	
	public RaceWakuTrendProvider(List<RecRacerTrend> records) {
		mapRacer = new HashMap<>();
		
		// 各racerにデータ受け渡し
		for (RecRacerTrend rec : records) {
			RacerDayTrend racer = mapRacer.get(rec.getEntry());
			if (racer == null) {
				racer = new RacerDayTrend(rec.getEntry());
				mapRacer.put(racer.entry, racer);
			}

			// racerにデータ追加
			racer.add(rec);;
		}
	}

	public RacerWakuTrend getRacerWakuTrend(Short entry, int wakuIdx) {
		return mapRacer.get(entry).getWakuTrend(wakuIdx);
	}
}

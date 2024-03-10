package com.pengkong.boatrace.exp10.racer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengkong.boatrace.exp10.racer.trend.RacerTrend;
import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.boatrace.mybatis.entity.RecRacerTrend;

@Deprecated
//com.pengkong.boatrace.online.batch.RecRacerTrendCreator
//online処理の一部として処理する
public class RecRacerTrendCreator {

	public List<RecRacerTrend> create(String ymd, int daysOfTrend, List<RecRacer> records) throws Exception {
		List<RecRacerTrend> results = new ArrayList<>();
		
		Map<Short, RacerTrend> mapRacer = new HashMap<>();
		
		// 各racerにデータ受け渡し
		for (RecRacer rec : records) {
			RacerTrend racer = mapRacer.get(rec.getEntry());
			if (racer == null) {
				racer = new RacerTrend(rec.getEntry(), ymd, daysOfTrend);
				mapRacer.put(racer.entry, racer);
			}

			// racerにデータ追加
			racer.add(ymd, rec);
		}
		
		for (RacerTrend racer : mapRacer.values()) {
			results.add(racer.createRecRacerTrend());
		}
		
		return results;
	}
}

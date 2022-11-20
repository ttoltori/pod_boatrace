package com.pengkong.boatrace.exp10.odds;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengkong.common.collection.HashMapList;

public class OddsHelper {

	/** odds listをHashMapListに変換する(map key = ymd_jyoCd_raceNo_betType) */
	public static HashMapList<Odds> listToHashMapList(List<Odds> list) {
		// key = ymd_jyoCd_raceNo_betType
		HashMapList<Odds> mapList = new HashMapList<>();
		String mapKey;
		for (Odds odds :list) {
			mapKey = String.join("_", odds.ymd, odds.jyoCd, odds.raceNo, odds.betType);
			mapList.addItem(mapKey, odds);
		}
		
		return mapList;
	}

	/** odds listをmapに変換する(map key = ymd_jyoCd_raceNo_betType_kumiban) */
	public static Map<String, Odds> listToMap(List<Odds> list) {
		// key = ymd_jyoCd_raceNo_betType_kumiban
		Map<String, Odds> mapOdds = new HashMap<>();
		for (Odds odds :list) {
			mapOdds.put(odds.getUniqueKey(), odds);
		}
		
		return mapOdds;
	}
	
	/**
	 * Oddsにrankingを設定する.
	 * @param mapOdds key=ymd + jyoCd + raceNo + betType value=[Odds...]
	 * @return
	 */
	public static HashMapList<Odds> applyRanking(HashMapList<Odds> mapOdds, Comparator<Odds> comparator) {
		for (String key : mapOdds.keySet()) {
			List<Odds> list = mapOdds.get(key);
			
			// 20191130 odds가 0이라는 것은 인기가 아예 없다는 것이므로
			// 아주 높은 옺즈를 대입주어야 한다. 0을 주면 인기가 최고로 높은 것으로 통계에 반영되어버린다.
			for (Odds odds : list) {
				if (odds.value <= 0)
					odds.value = 99999.0;
			}
			list.sort(comparator);

			int rank = 1;
			int rank2 = 1;
			Double lastOdds = list.get(0).value;
			for (Odds odds : list) {
				if (odds.value > lastOdds) {
					rank = rank2;
				}
				odds.rank = rank;
				lastOdds = odds.value;
				rank2++;

				// oddsが0だった場合元の値に戻す
				if (odds.value == 99999) {
					odds.value = 0.0;
				}
			}
		}
		
		return mapOdds;
	}

	/**
	 * 20191130 odds가 0이라는 것은 인기가 아예 없다는 것이므로
	 * 아주 높은 옺즈를 대입주어야 한다. 0을 주면 인기가 최고로 높은 것으로 통계에 반영되어버린다.
	 * @param value odds value
	 */
	public static Double preventZeroOdds(Double value) {
		if (value <= 0) {
			return 99999.0;
		}

		return value;
	}
}

package com.pengkong.boatrace.exp10.odds.provider;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.odds.Odds;

/**
 * 日付単位の直前オッズを累積的にキャッシュしながらオッズを提供する.
 * 
 * @author ttolt
 *
 */
public class BeforeOddsCachedProvider extends BeforeOddsProvider {
	Logger logger = LoggerFactory.getLogger(BeforeOddsCachedProvider.class);
	
	/** 日付毎のオッズmapのキャッシュ 
	 *   key=ymd value={key=ymd,jyo,race.bettype,kumiban value=Odds} 
	 */
	Map<String, Map<String, Odds>> cache = new HashedMap<>();
	
	@Override
	public Odds get(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws Exception {
		throw new IllegalAccessException("Not allowed cached odds access.");
	}
	
	/** 1日分のオッズを取得する */ 
	public Map<String, Odds> getDaily(String ymd) throws Exception {
		// 메모리부족으로 일시적 대처
//		if (ymd.equals("20220101") || ymd.equals("20220701") || ymd.equals("20230101")) {
//			cache = new HashedMap<>();
//		}
		
		// 当該日付のオッズをまだロードしていなければロードしてmapに追加する
		Map<String, Odds> dailyOddsMap = cache.get(ymd);
		if (dailyOddsMap == null) {
			dailyOddsMap = loadDailyOdds(ymd);
			cache.put(ymd,  dailyOddsMap);
			logger.info("Before Odds cached. " + ymd);
		}
		
		return dailyOddsMap;
	}
}

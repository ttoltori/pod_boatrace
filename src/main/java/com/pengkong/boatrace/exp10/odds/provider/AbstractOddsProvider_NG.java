package com.pengkong.boatrace.exp10.odds.provider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.loader.AbstractOddsFileLoader;
import com.pengkong.boatrace.exp10.odds.loader.OddsValueComparator;
import com.pengkong.common.collection.HashMapList;

/**
 * オッズをファイルから取得して提供する
 * @author ttolt
 *
 */
public abstract class AbstractOddsProvider_NG implements OddsProviderInterface {
	String ymd;

	public AbstractOddsProvider_NG() {
		initialize();
	}
	
	/** key = ymd + jyoCd + raceNo + betType + kumiban */
	Map<String, Odds> mapOdds = new HashMap<>();
	
	HashMapList<Odds> mapBettypeOdds = new HashMapList<>();
	
	/** 異なる形式のオッズファイルをロードするために */
	AbstractOddsFileLoader fileLoader;
	
	void initialize() {
		// nothing to do
	}
	
	@Override
	public Odds get(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws Exception {
		checkLoadedDaily(ymd);
		
		Odds odds = mapOdds.get(String.join("_", ymd, jyoCd, raceNo, betType, kumiban));

		return odds;
	}

	public List<Odds> getPopularList(String ymd, String jyoCd, String raceNo, String betType, int limit) throws Exception{
		checkLoadedDaily(ymd);
		
		List<Odds> list = mapBettypeOdds.get(String.join("_", ymd, jyoCd, raceNo, betType));
		if (list == null) {
			return null;
		}
		
		int max = (list.size() < limit) ? list.size() : limit;
		
		return list.subList(0, max);
	}
	
	public List<Odds> getList(String ymd, String jyoCd, String raceNo, String betType, List<String> kumibans) throws Exception{
		checkLoadedDaily(ymd);
		
		List<Odds> results = new ArrayList<>();
		for(String kumiban : kumibans) {
			Odds odds = mapOdds.get(String.join("_", ymd, jyoCd, raceNo, betType, kumiban));
			if (odds == null) {
				return null;
			}
			results.add(odds);
		}
		
		return results;
	}
	
	/** daily odds load処理 */
	protected void checkLoadedDaily(String ymd) throws Exception {
		if (!ymd.equals(this.ymd)) {
			loadDailyOdds(ymd);
			this.ymd = ymd;
		}
	}

	/** 1日分のオッズをファイルからロードする */
	protected Map<String, Odds> loadDailyOdds(String ymd) throws Exception {
		// 当該日付のオッズをまだロードしていなければロードしてmapに追加する
		HashMapList<Odds> mapList = getFileLoader().load(createOddsFilepath(ymd), createBettypes());
		mapOdds = new HashMap<>();
		mapBettypeOdds = new HashMapList<>();
		
		for (Odds odds : mapList.getAllItems()) {
			mapOdds.put(odds.getUniqueKey(), odds);
			mapBettypeOdds.addItem(odds.getBettypeKey(), odds);
		}
		sortByPopular();
		
		return mapOdds;
	}
	
	void sortByPopular() {
		Comparator<Odds> comp = new OddsValueComparator();
		for (String key : mapBettypeOdds.keySet()) {
			mapBettypeOdds.get(key).sort(comp);
		}
	}
	
	/** 対象odds file loaderを生成する */
	protected abstract AbstractOddsFileLoader getFileLoader();
	
	/** 対象odds file pathを生成する */ 
	protected abstract String createOddsFilepath(String ymd);
	
	/** 対象bettype listを生成する */
	protected abstract String[] createBettypes();
}

package com.pengkong.boatrace.exp10.odds.provider;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.loader.AbstractOddsFileLoader;
import com.pengkong.common.collection.HashMapList;

/**
 * オッズをファイルから取得して提供する
 * @author ttolt
 *
 */
public abstract class AbstractOddsProvider implements OddsProviderInterface {
	String ymd;

	public AbstractOddsProvider() {
		initialize();
	}

	/** key = ymd + jyoCd + raceNo + betType + kumiban */
	Map<String, Odds> mapOdds = new HashedMap<>();
	
	/** 異なる形式のオッズファイルをロードするために */
	AbstractOddsFileLoader fileLoader;
	
	@Override
	public Odds get(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws Exception {
		if (!ymd.equals(this.ymd)) {
			mapOdds = loadDailyOdds(ymd);
			this.ymd = ymd;
		}
		
		Odds odds = mapOdds.get(String.join("_", ymd, jyoCd, raceNo, betType, kumiban));

		return odds;
	}
	
	/** 初期化処理 */
	protected void initialize() {
		// do nothing
	}

	/** 1日分のオッズをファイルからロードする */
	protected Map<String, Odds> loadDailyOdds(String ymd) throws Exception {
		// 当該日付のオッズをまだロードしていなければロードしてmapに追加する
		return createMapOdds(getFileLoader().load(createOddsFilepath(ymd), createBettypes()));
	}
	
	Map<String, Odds> createMapOdds(HashMapList<Odds> mapList) {
		Map<String, Odds> result = new HashedMap<>();
		for (Odds odds : mapList.getAllItems()) {
			result.put(odds.getUniqueKey(), odds);
		}
		
		return result;
	}
	
	/** 対象odds file loaderを生成する */
	protected abstract AbstractOddsFileLoader getFileLoader();
	
	/** 対象odds file pathを生成する */ 
	protected abstract String createOddsFilepath(String ymd);
	
	/** 対象bettype listを生成する */
	protected abstract String[] createBettypes();
}

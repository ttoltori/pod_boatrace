package com.pengkong.boatrace.exp10.odds.provider;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.loader.AbstractOddsFileLoader;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * オッズをファイルから取得して提供する
 * @author ttolt
 *
 */
public abstract class AbstractOddsProvider implements OddsProviderInterface {
	String ymd;
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

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

	/** 対象bettype listを生成する */
	protected String[] createBettypes() {
		String betTypes = prop.getString("bettype");
		// 例）1T,3T,2T
		betTypes = betTypes.replace("3M", "3T");
		betTypes = betTypes.replace("2M", "2T");
		betTypes = betTypes.replace("3N", "2T");
		betTypes = betTypes.replace("2N", "3T");
		betTypes = betTypes.replace("3P", "3T");
		betTypes = betTypes.replace("3R", "3T");
		betTypes = betTypes.replace("3U", "3T");
		betTypes = betTypes.replace("3X", "3T");
		betTypes = betTypes.replace("3Y", "3T");
        betTypes = betTypes.replace("3A", "3T");
        betTypes = betTypes.replace("2A", "2T");
        betTypes = betTypes.replace("2G", "2F");
        betTypes = betTypes.replace("3G", "3F");
        betTypes = betTypes.replace("3B", "3T");
        betTypes = betTypes.replace("3C", "3T");
        betTypes = betTypes.replace("3D", "3T");
        betTypes = betTypes.replace("3E", "3T");

		return betTypes.split(Delimeter.COMMA.getValue());
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
}

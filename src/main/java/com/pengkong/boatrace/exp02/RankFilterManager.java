package com.pengkong.boatrace.exp02;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;
import com.pengkong.common.collection.HashMapList;

public class RankFilterManager {

	// ex)key = turn+level1::1_A2
	private HashMapList<RankDbRecord> mapFilter = new HashMapList<>();
	
	private static class InstanceHolder {
		private static final RankFilterManager INSTANCE = new RankFilterManager();
	}
	
	public static RankFilterManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public RankFilterManager() {
	}

	/**
	 * 필터리스트에 포함되는 모든 패턴들을 로딩한다.
	 * @param listRankFilter
	 * @throws Exception
	 */
	public void initialize(List<RankDbRecord> listRankFilter) throws Exception {
		for (RankDbRecord rec : listRankFilter) {
			String key =  rec.getString(DBCol.PATTERN_NAME) + "::" +  rec.getString(DBCol.PATTERN);
			mapFilter.addItem(key, rec);
		}
	}
	
	/**
	 * 지정 Race정보에 대해 RankFilter리스트를 반환한다.
	 * 
	 * @param bettype
	 * @return HashMapList<Kumiban, StatMlPtnFiltered[]>
	 */
	public List<RankDbRecord> getRankFilterList(RankDbRecord raceRec) throws Exception {
		List<RankDbRecord> results = new ArrayList<>();
		
		Iterator<String> it = mapFilter.keySet().iterator();
		while(it.hasNext()) {
			String mapPatternKey = it.next();
			String patternName = mapPatternKey.split("::")[0];
			String patternValue = mapPatternKey.split("::")[1];
			// 필터의 패턴명에 대한 패턴값이 레이스의 해당 패턴값과 일치한다면 통과
			if (patternValue.equals(RankDbRecord.getPatternValue(raceRec, patternName))) {
				results.addAll(mapFilter.get(mapPatternKey));
			}
		}
		
		return results;
	}
}

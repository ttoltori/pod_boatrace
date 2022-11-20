package com.pengkong.boatrace.server.db.dto;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.collection.TreeMapList;

import lombok.Getter;

/**
 * 전체 대상데이터 리스트에 대해 일정간격으로 전진하며 지정한 범위의 데이터를 취득할 때 발생하는
 * 신규데이터, 제외데이터를 취득하기위한 커스텀 리스트 클래스.
 * 
 * usecase) 특정기간동안의 기간별 선수별 메트릭정보를 취득하여 통계처리한다.
 * usage) 
 *   YmdRangeDbList list = getDbDataList(sql);
 *   while(list.next()) {
 *     // 제외 데이터 통계처리
 *     doProcessScopeOut(list.getScopeOut());
 *     
 *     // 신규 데이터 통계처리
 *     doProcessScopeOut(list.getScopeIn());
 *   }
 * 
 * @author qwerty
 *
 */
@SuppressWarnings("serial")
public class YmdRangeDbList extends TreeMapList<DBRecord> {
	@Getter
	private int interval = -1; // 키 증가분
	
	@Getter
	private int range = -1; // 취득 데이터 범위

	@Getter
	private String startKey; // 데이터 시작키
	
	@Getter
	private String endKey; // 데이터 종료키

	@Getter
	private String rangeStartKey; // 취득범위 시작키
	
	@Getter 
	private List<String> rangeKeys; // 취득범위 키 리스트

	@Getter
	private String rangeEndKey; // 취득범위 종료키

	@Getter
	private String lastRangeStartKey; // 직전 취득범위 시작키
	
	@Getter 
	private List<String> lastRangeKeys; // 직전 취득범위 키 리스트

	@Getter
	private String lastRangeEndKey; // 직전 취득범위 종료키

	@Getter
	private boolean isReachedEnd = false; // 취득 범위가 데이터 종료에 다다랐음.
	
	@Getter
	private int readCount = 0; // 취득횟수
	
	/**
	 *  생성자
	 * @param interval 데이터를 취득개시 간격 (1~N)
	 * @param range 데이터 취득 범위 (1~N)
	 * @param list 대상 전체 데이터
	 */
	public YmdRangeDbList(int interval, int range, List<DBRecord> list) {
		super();
		initialize(interval, range, list);
	}

	/**
	 * 처리를 초기화한다.
	 * 
	 * @param interval 데이터를 취득개시 간격 (1~N)
	 * @param range 데이터 취득 범위 (1~N)
	 * @param list 대상 데이터
	 */
	public void initialize(int interval, int range, List<DBRecord> list) {
		if (list == null || list.size() <= 0) {
			throw new IllegalStateException("data is null or empty");
		}
		this.interval = interval;
		this.range = range;

		// 직전 범위정보 초기화
		this.lastRangeStartKey = null;
		this.lastRangeKeys = null;
		this.lastRangeEndKey = null;
		this.readCount = 0;
		this.isReachedEnd = false;
		
		this.startKey = getDataKey(list.get(0));
		this.endKey = getDataKey(list.get(list.size() - 1));
		// 현재 범위정보설정
		setupRangeKeys(startKey);

		// 데이터 로드
		for (DBRecord rec : list) {
			super.addItem(getDataKey(rec), rec);
		}
	}

	/**
	 * 범위설정을 전진시킨다.
	 * 
	 * @return true=범위데이터가 있음. false=범위데이터를 전부 취득했음.
	 */
	public boolean next() {
		if (this.isReachedEnd) {
			return false;
		}
		
		readCount++;
		
		// 현재 범위설정의 데이터를 취득한다. 
		// this.currentData = getRangedData(this.rangeKeys);
		
		int end = Integer.parseInt(endKey);
		int rangeEnd = Integer.parseInt(rangeEndKey);
		
		// 마지막 범위라면
		if (rangeEnd >= end) {
			this.isReachedEnd = true;
			return true;
		}
		
		// 현재 범위정보를 직전정보로 카피한다.
		backupRangeKeys();
		// 다음 범위정보를 셋업한다.
		setupRangeKeys(getNextRangeStartKey());
		
		return true;
	}

	/**
	 * 취득 범위진전에 따라 제외된 데이터를 반환한다.
	 *  
	 * @return 데이터 리스트
	 */
	public List<DBRecord> getScopeOut() {
		// 아직 한번이상 취득되지 않은 경우
		if (readCount <= 1) {
			return new ArrayList<>();
		}
		
		// 현재 데이터의 범위시작키-1 을 구한다. 
		String endKey = getBeforeKey(rangeStartKey);
		// 키범위 = 직전 범위시작 키 ~ 현재 시작키의 직전키
		List<String> rangeKeys = getRangeKeys(lastRangeStartKey, endKey);
		
		return getRangedData(rangeKeys);
	}

	/**
	 * 취득 범위진전에 따라 추가된 데이터를 반환한다.
	 *  
	 * @return 데이터 리스트
	 */
	public List<DBRecord> getScopeIn() {
		List<String> rangeKeys;
		
		// 아직 한번도 취득되지 않은 경우
		if (readCount <= 0) {
			return new ArrayList<>();
		} else if (readCount == 1) {
			// 최초취득인 경우
			rangeKeys = getRangeKeys(lastRangeStartKey, lastRangeEndKey);
			return getRangedData(rangeKeys);
		}
		
		// 현재 데이터의 범위종료키+1 을 구한다. 
		String startKey = getNextKey(rangeEndKey);
		// 키범위 = 직전 범위시작 키 ~ 현재 시작키의 직전키
		rangeKeys = getRangeKeys(startKey, rangeEndKey);
		
		return getRangedData(rangeKeys);
	}
	
	/**
	 * 범위키 값들에 해당하는 데이터를 반환한다.
	 * 
	 * @param rangeKeys 범위키 리스트. ex) ["20190101", "20190102",,,]
	 * 
	 * @return 데이터 리스트
	 */
	private List<DBRecord> getRangedData(List<String> rangeKeys) {
		List<DBRecord> results = new ArrayList<DBRecord>();
		for (String key : rangeKeys) {
			if (!containsKey(key)) {
				continue;
			}
			results.addAll(get(key));
		}
		
		return results;
	}

	/**
	 * 현재 범위정보를 직전범위정보로 복사한다.
	 */
	private void backupRangeKeys() {
		this.lastRangeStartKey = this.rangeStartKey;
		this.lastRangeEndKey = this.rangeEndKey;
		this.lastRangeKeys = new ArrayList<>();
		lastRangeKeys.addAll(rangeKeys);
	}
	
	/**
	 * 범위 시작키를 기준으로 범위종료키 및 범위키 일람을 설정한다. 
	 * 
	 * @param rangeStartKey 범위 시작키
	 */
	private void setupRangeKeys(String rangeStartKey) {
		this.rangeStartKey = rangeStartKey;
		this.rangeEndKey = getRangeEndKey(rangeStartKey);
		this.rangeKeys = getRangeKeys(this.rangeStartKey, this.rangeEndKey);
	}
	
	/**
	 * 다음 범위진전 간격값을 기준으로 다음 범위 시작키를 반환한다. 
	 * 
	 * @return 범위 시작키
	 */
	private String getNextRangeStartKey() {
		return BoatUtil.daysAfterYmd(rangeStartKey, interval);		
	}

	/**
	 * 범위시작키와 범위종료키 사이의 모든 키값의 일람을 반환한다.
	 * 
	 * @param rangeStartKey 범위시작키
	 * @param rangeEndKey 범위종료키
	 * @return 범위내 키값 일람.
	 */
	private List<String> getRangeKeys(String rangeStartKey, String rangeEndKey) {
		List<String> ranges = new ArrayList<>();
		if (rangeStartKey.equals(rangeEndKey)) {
			ranges.add(rangeStartKey);
			
			return ranges;
		}
		
		String key = startKey;
		ranges.add(key);
		boolean isEnd = false;
		do {
			key = getNextKey(key);
			ranges.add(key);
			if (key.equals(rangeEndKey)) {
				isEnd = true;
			}
		} while (!isEnd);

		return ranges;
	}
	
	/**
	 * 해당키의 직전 키를 반환한다.
	 * @param key 해당키
	 * @return 직전키
	 */
	private String getBeforeKey(String key) {
		return BoatUtil.daysBeforeYmd(key, 1);
	}
	
	/**
	 * 해당키의 직후 키를 반환한다.
	 * @param key 해당키
	 * @return 직후키
	 */
	private String getNextKey(String key) {
		return BoatUtil.daysAfterYmd(key, 1);
	}
	
	/**
	 * 해당 범위시작키에 대해 범위설정에 따른 범위종료키를 반환한다.
	 * 
	 * @param rangeStartKey 범위시작키
	 * 
	 * @return 범위종료키
	 */
	private String getRangeEndKey(String rangeStartKey) {
		return BoatUtil.daysAfterYmd(rangeStartKey, range-1);
	}
	
	/**
	 * 격납된 데이터의 범위키 값을 반환한다. 
	 * @param 데이터
	 * @return 범위키
	 */
	private String getDataKey(DBRecord rec) {
		return rec.getString("ymd");
	}
}

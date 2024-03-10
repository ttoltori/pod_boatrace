package com.pengkong.boatrace.server.db.dto;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.collection.TreeMapList;

import lombok.Getter;
import lombok.Setter;

public class YmdBeforeRangeDbList {
	private TreeMapList<DBRecord> treeMapdata = new TreeMapList<>();
	
	@Getter
	private int interval = -1; // 키 증가분

	@Getter
	private int range = -1; // 취득 데이터 범위

	@Getter
	private String startKey; // 처리시작 키

	@Getter
	private String endKey; // 처리종료 키

	@Getter
	private String dataStartKey; // 데이터 시작키

	@Getter
	private String dataEndKey; // 데이터 종료키

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
	private int readCount = 0; // 취득횟수

	@Getter
	private boolean isReachedEnd = false; // 취득 범위가 데이터 종료에 다다랐음.
	
	@Getter
	private List<DBRecord> currentData;

	@Getter@Setter
	private boolean isUseRangeStartKey = true;
	
	public YmdBeforeRangeDbList() {
	}
	
	public YmdBeforeRangeDbList(int interval, int range, String startKey, String endKey, List<DBRecord> list) {
		initialize(interval, range, startKey, endKey, list);
	}

	public void initialize(int interval, int range, String startKey, String endKey, List<DBRecord> list) {
		if (list == null || list.size() <= 0) {
			throw new IllegalStateException("data is null or empty");
		}
		this.interval = interval;
		this.range = range;
		this.startKey = startKey;
		this.endKey = endKey;
		this.readCount = 0;
		this.isReachedEnd = false;
		this.dataStartKey = getDataKey(list.get(0));
		this.dataEndKey = getDataKey(list.get(list.size() - 1));
		setupRangeKeys(startKey);

		// 데이터 범위가 충분한지 체크한다.
		//checkSufficientData();
		
		// 데이터 로드
		treeMapdata.clear();
		for (DBRecord rec : list) {
			treeMapdata.addItem(getDataKey(rec), rec);
		}
	}
	
	public List<DBRecord> get() {
		if (this.isReachedEnd) {
			return null;
		}

		// 현재 범위설정의 데이터를 취득한다. 
		this.currentData = getRangedData(this.rangeKeys);
		
		int end = Integer.parseInt(this.endKey);
		int rangeEnd = Integer.parseInt(this.rangeEndKey);
		// 마지막 범위라면
		if (rangeEnd >= end) {
			this.isReachedEnd = true;
		}
		
		// 현재 범위정보를 직전정보로 카피한다.
		backupRangeKeys();
		// 다음 범위정보를 셋업한다.
		setupRangeKeys(getNextRangeEndKey());

		readCount++;
		return this.currentData;
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
		// 키범위 = 직전 범위시작키 ~ 현재시작키-1
		List<String> rangeKeys = getRangeKeys(lastRangeStartKey, endKey);
		
		return getRangedData(rangeKeys);
	}

	/**
	 * 취득 범위진전에 따라 추가된 데이터를 반환한다.
	 *  
	 * @return 데이터 리스트
	 */
	public List<DBRecord> getScopeIn() {		
		// 아직 한번도 취득되지 않은 경우
		if (readCount <= 0) {
			return new ArrayList<>();
		} else if (readCount == 1) {
			// 최초취득인 경우
			return this.currentData;
		}
		
		// 직전 데이터의 범위종료키+1 을 구한다. 
		String startKey = getNextKey(lastRangeEndKey);
		// 키범위 = 직전 범위종료키+1 ~ 현재종료키
		List<String> rangeKeys = getRangeKeys(startKey, rangeEndKey);
		
		return getRangedData(rangeKeys);
	}
	
	/**
	 * 충분한 크기의 대상데이터가 확보되어있는 지 체크한다.
	 */
	private void checkSufficientData() {
		int dataStart = Integer.parseInt(this.dataStartKey);
		int dataEnd = Integer.parseInt(this.dataEndKey);
		int rangeStart = Integer.parseInt(this.rangeStartKey);
		int end = Integer.parseInt(this.endKey);
		if (dataStart > rangeStart) {
			throw new IllegalStateException("Past data is insufficient. dataStart=" + this.dataStartKey + ", rangeStart=" + this.rangeStartKey);
		}
		
		if (dataEnd < end) {
			throw new IllegalStateException("data is insufficient. dataEnd=" + this.dataEndKey + ", end=" + this.endKey);
		}
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
	 * 범위종료키를 기준으로 범위시작키 및 범위키 일람을 설정한다. 
	 * 
	 * @param rangeStartKey 범위 시작키
	 */
	private void setupRangeKeys(String rangeEndKey) {
		int rangeEnd = Integer.parseInt(rangeEndKey);
		int end = Integer.parseInt(this.endKey);
		// 범위종료키가 처리종료키에 다다랐다면
		if (rangeEnd >= end) {
			rangeEndKey = this.endKey;
		}
		this.rangeEndKey = rangeEndKey;
		this.rangeStartKey = getRangeStartKey(rangeEndKey);
		this.rangeKeys = getRangeKeys(this.rangeStartKey, this.rangeEndKey);
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
			if (!treeMapdata.containsKey(key)) {
				continue;
			}
			results.addAll(treeMapdata.get(key));
		}
		
		return results;
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
		
		String key = rangeStartKey;
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
	 * 다음 범위진전 간격값을 기준으로 다음 범위종료키를 반환한다. 
	 * 
	 * @return 범위 시작키
	 */
	private String getNextRangeEndKey() {
		return BoatUtil.daysAfterYmd(this.rangeEndKey, interval);		
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
	 * 해당 범위종료키에 대해 범위설정에 따른 범위시작키를 반환한다.
	 * 
	 * @param rangeEndKey 범위종료키
	 * 
	 * @return 범위시작키
	 */
	private String getRangeStartKey(String rangeEndKey) {
		if (isUseRangeStartKey()) {
			return BoatUtil.daysBeforeYmd(rangeEndKey, range-1);	
		}
		
		// 예를 들어 범위시작은 20171231인데 dataStartKey는 20190410
		if (Integer.parseInt(rangeEndKey) < Integer.parseInt(dataStartKey)) {
			return rangeEndKey;
		}
		
		return dataStartKey;
	}

	/**
	 * 격납된 데이터의 범위키 값을 반환한다. 
	 * @param 데이터
	 * @return 범위키
	 */
	private String getDataKey(DBRecord rec) {
		return rec.getString("ymd");
	}
	
	public String toString() {
		return "{interval:" + interval + ", range:" + range + ", start:" + startKey + ", end:" + endKey + 
				", dataStart:" + dataStartKey + ", dataEnd:" + dataEndKey + ", rangeStart:" + rangeStartKey + 
				", rangeEnd:" + rangeEndKey + ", lastRangeStart:" + lastRangeStartKey + ", lastRangeEnd:" + lastRangeEndKey + 
				", readCount:" + readCount + ", isReachedEnd:" + isReachedEnd + "}";
	}
	
	public static void main(String[] args) {
		int max = 365 * 5;
		List<DBRecord> dbRecords = new ArrayList<>();
		DBRecord rec;
		String ymdStartData = "20150101";
		for (int i = 0; i < max; i++) {
			rec = new DBRecord();
			String ymd = BoatUtil.daysAfterYmd(ymdStartData, i);
			rec.put("ymd", ymd);
			rec.put("data", "1");
			dbRecords.add(rec);
			dbRecords.add(rec);
		}
		try {
			YmdBeforeRangeDbList list = new YmdBeforeRangeDbList(7, 365*2, "20170101", "20191230", dbRecords);
			List<DBRecord> listRanged;
			while((listRanged = list.get()) != null) {
				System.out.println(list);
				System.out.println("listRanged.size: " + listRanged.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.pengkong.boatrace.server.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.boatrace.server.manager.filter.FinalizeFilter;
import com.pengkong.boatrace.server.stat.StMonthly;
import com.pengkong.boatrace.server.stat.BoatStatObject;
import com.pengkong.boatrace.server.stat.StBetType;
import com.pengkong.boatrace.server.stat.StKumiban;
import com.pengkong.boatrace.server.stat.StPatternName;
import com.pengkong.boatrace.server.stat.StPatternValue;
import com.pengkong.boatrace.server.stat.StStatMlPtnFinal;
import com.pengkong.boatrace.server.stat.StStatMlPtnFinalMonthly;
import com.pengkong.boatrace.server.stat.StatObject;

public class PatternStatRunner {

	public String patternName;
	/** key = bettype + kumiban */
	public Map<String, FinalizeFilter> mapFinalizeFilter;
	
	/** hierarchy : bettype - kumiban - patternName - patternValue - monthly */
	public Map<String, BoatStatObject> map = new HashMap<String, BoatStatObject>();

	Logger logger = LoggerFactory.getLogger(PatternStatRunner.class);
	public PatternStatRunner(String patternName, Map<String, FinalizeFilter> mapFinalizeFilter) {
		super();
		this.patternName = patternName;
		this.mapFinalizeFilter = mapFinalizeFilter;
	}

	/**
	 * DB레코드 리스트를 추가한다.
	 * @param listDbRec
	 * @throws Exception
	 */
	public void addBoatDbRecordList(List<BoatDbRecord> listDbRec) throws Exception {
		int i = 0;
		for (BoatDbRecord rec : listDbRec) {
			BoatStatObject monthly = getMonthly(rec);
			
			// 추가와 동시에 계층구조 각 상위단계에서 통계계산이 실행된다.
			monthly.addRecord(rec);
			i++;
			if (i % 1000 == 0) {
				logger.info("added records:" + i);
			}
		}
	}

	/**
	 * 이 패턴명에 포함되어 있는 모든 StPatternValue를 StStatMlPtnFinal의 리스트로 변환하여 반환한다.
	 * @return
	 */
	public List<StStatMlPtnFinal> getStStatMlPtnFinalList() {
		List<StStatMlPtnFinal> result = new ArrayList<StStatMlPtnFinal>();
		
		for (StatObject stBetType : map.values()) { // bettype
			for (StatObject stKumiban : stBetType.getChildMap().values()) { // kumiban
				FinalizeFilter filter = mapFinalizeFilter.get(stBetType.key + "." + stKumiban.key);
				for (StatObject stPatternName : stKumiban.getChildMap().values()) { // pattern name
					for (StatObject stPatternValue : stPatternName.getChildMap().values()) { // pattern value
						if (filter.isValid((BoatStatObject)stPatternValue)) {
							StStatMlPtnFinal stFinal = StStatMlPtnFinal.createFromStPatternValue(new StStatMlPtnFinal(), (BoatStatObject)stPatternValue);
							result.add(stFinal);
						}
					}
				}
			}
		}
		
		return result;
	}

	public List<StStatMlPtnFinalMonthly> getStStatMlPtnFinalMonthlyList(List<StStatMlPtnFinal> listFinal) {
		List<StStatMlPtnFinalMonthly> result = new ArrayList<>();
		// sort by yyyyMm
		TreeMap<String, StMonthly> treeMap = new TreeMap<>();
		for (StStatMlPtnFinal stFinal : listFinal) {
			for (StatObject stMonthly : stFinal.stPatternValue.getChildMap().values()) {
				StStatMlPtnFinalMonthly stFinalMonthly = StStatMlPtnFinalMonthly.setFromStMonthly(new StStatMlPtnFinalMonthly(), stMonthly);
			}
		}
		
		
	}

	
	/**
	 * DB레코드로부터 대상 StPatternValue를 취득한다. 아직 없다면 필요한 계층구조를 모두 생성한다. 
	 * @param rec
	 * @return
	 * @throws Exception
	 */
	private BoatStatObject getMonthly(BoatDbRecord rec) throws Exception {
		String betType = rec.getBetType();
		String kumiban = rec.getKumiban();
		String patternValue = BoatDbRecord.getPatternValue(rec, this.patternName);
		String yyyyMm = rec.getYmd().substring(0,6);
		
		BoatStatObject stBetType =  map.get(betType);
		BoatStatObject stMonthly = (BoatStatObject)stBetType.getChildWithCreation(stBetType, 4, new String[] {kumiban, patternName, patternValue, yyyyMm});
		
		return stMonthly;
	}
	
	/**
	 * StBetType를 취득한다. 아직 없다면 계층구조에 추가한다.
	 * @param betType
	 * @param createIfNotExist
	 * @return StBetType
	 */
	private BoatStatObject getStBetType(String betType) {
		BoatStatObject stBetType = map.get(betType);
		if (stBetType == null) {
			stBetType = new BoatStatObject(betType);
			map.put(betType, stBetType);
		}
		
		return stBetType;
	}
	
	/**
	 * StKumiban를 취득한다. 아직 없다면 계층구조에 추가한다.
	 * @param stBetType
	 * @param kumiban
	 * @param createIfNotExist
	 * @return StKumiban
	 */
	private StKumiban getStKumiban(StBetType stBetType, String kumiban) {
		StKumiban stKumiban = (StKumiban) stBetType.getChild(kumiban);
		if (stKumiban == null) {
			stKumiban = new StKumiban(stBetType, kumiban);
		}
		
		return stKumiban;
	}
	
	/**
	 * StPatternName를 취득한다. 아직 없다면 계층구조에 추가한다.
	 * @param stKumiban
	 * @param patternName
	 * @param createIfNotExist
	 * @return StPatternName
	 */
	private StPatternName getStPatternName(StKumiban stKumiban, String patternName) {
		StPatternName stPatternName = (StPatternName) stKumiban.getChild(patternName);
		if (stPatternName == null) {
			stPatternName = new StPatternName(stKumiban, patternName);
		}
		
		return stPatternName;
	}
	
	/**
	 * StPatternValue를 취득한다. 아직 없다면 계층구조에 추가한다.
	 * @param stKumiban
	 * @param patternValue
	 * @return StPatternValue
	 */
	private StPatternValue getStPatternValue(StPatternName stKumiban, String patternValue) {
		StPatternValue stPatternValue = (StPatternValue) stKumiban.getChild(patternValue);
		if (stPatternValue == null) {
			stPatternValue = new StPatternValue(stKumiban, patternValue);
		}
		
		return stPatternValue;
	}
	
	/**
	 * Monthly를 취득한다. 아직 없다면 계층구조에 추가한다.
	 * @param StPatternValue
	 * @param patternValue
	 * @return Monthly
	 */
	private StMonthly getMonthly(StPatternValue stPatternValue, String yyyyMm) {
		StMonthly monthly = (StMonthly) stPatternValue.getChild(yyyyMm);
		if (monthly == null) {
			monthly = new StMonthly(stPatternValue, yyyyMm);
		}
		
		return monthly;
	}
 }

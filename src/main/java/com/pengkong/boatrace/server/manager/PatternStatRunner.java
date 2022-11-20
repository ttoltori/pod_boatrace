package com.pengkong.boatrace.server.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.boatrace.server.manager.filter.FinalizeFilter;
import com.pengkong.boatrace.server.stat.BoatStatObject;
import com.pengkong.boatrace.server.stat.StStatMlPtnFinal;
import com.pengkong.boatrace.server.stat.StatObject;

import lombok.Getter;
import lombok.Setter;

public class PatternStatRunner implements Callable<List<StStatMlPtnFinal>>{

	public String patternName;
	
	/** key = bettype + kumiban */
	@Setter
	@Getter
	private Map<String, FinalizeFilter> mapFinalizeFilter;
	
	/** hierarchy : bettype - kumiban - patternName - patternValue - monthly */
	public Map<String, BoatStatObject> map = new HashMap<String, BoatStatObject>();
	List<BoatDbRecord> listDbRec;

	Logger logger = LoggerFactory.getLogger(PatternStatRunner.class);
	/**
	 * 
	 * @param listBoatDbRecord 패턴통계를 실행할 디비레코드일람
	 * @param patternName 패턴명
	 * @param mapFinalizeFilter finalize filter맵
	 */
	public PatternStatRunner(List<BoatDbRecord> listBoatDbRecord, String patternName, Map<String, FinalizeFilter> mapFinalizeFilter) {
		super();
		this.listDbRec = listBoatDbRecord;
		this.patternName = patternName;
		this.mapFinalizeFilter = mapFinalizeFilter;
	}

	/**
	 * DB레코드에 대해 통계처리를 실행한다.
	 * @throws Exception
	 */
	private void processDbRecords() throws Exception {
		for (BoatDbRecord rec : listDbRec) {
			BoatStatObject monthly = getMonthly(rec);
			// 추가와 동시에 계층구조 각 상위단계에서 통계계산이 실행된다.
			monthly.addRecord(rec);
		}
	}

	/**
	 * 이 패턴명에 포함되어 있는 모든 StPatternValue를 StStatMlPtnFinal의 리스트로 변환하여 반환한다.
	 * @return
	 */
	public List<StStatMlPtnFinal> call() throws Exception {
		//logger.info(this.patternName + " processing start.");
		
		// 디비 레코드에 대해 통계처리를 실행한다.
		processDbRecords();
		
		// 통계결과를 반환한다.
		List<StStatMlPtnFinal> result = new ArrayList<StStatMlPtnFinal>();
		
		for (StatObject stBetType : map.values()) { // bettype
			for (StatObject stKumiban : stBetType.getChildMap().values()) { // kumiban
				FinalizeFilter filter = mapFinalizeFilter.get(stBetType.key + "." + stKumiban.key);
				for (StatObject stPatternName : stKumiban.getChildMap().values()) { // pattern name
					for (StatObject stPatternValue : stPatternName.getChildMap().values()) { // pattern value
						if (filter.isValid((BoatStatObject)stPatternValue)) {
							StStatMlPtnFinal stFinal = StStatMlPtnFinal.create((BoatStatObject)stPatternValue);
							result.add(stFinal);
						}
					}
				}
			}
		}
		//logger.info(this.patternName + " processing end.");
		
		return result;
	}

	/**
	 * DB레코드로부터 대상 StPatternValue를 취득한다. 아직 없다면 필요한 계층구조를 모두 생성한다. 
	 * @param rec
	 * @return
	 * @throws Exception
	 */
	private BoatStatObject getMonthly(BoatDbRecord rec) throws Exception {
		String betType = rec.getString(DBCol.BETTYPE);
		String kumiban = rec.getString(DBCol.BET_KUMIBAN);
		String patternValue = BoatDbRecord.getPatternValue(rec, this.patternName);
		String yyyyMm = rec.getString(DBCol.YMD).substring(0,6);
		
		BoatStatObject stBetType =  map.get(betType);
		if (stBetType == null) {
			stBetType = new BoatStatObject(betType); // 최상위 노드 생성
			map.put(betType, stBetType);
		}
		
		BoatStatObject stMonthly = (BoatStatObject)stBetType.getChildWithCreation(stBetType, 4, new String[] {kumiban, patternName, patternValue, yyyyMm});
		
		return stMonthly;
	}
 }

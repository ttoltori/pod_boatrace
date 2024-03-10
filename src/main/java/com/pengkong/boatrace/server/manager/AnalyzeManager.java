package com.pengkong.boatrace.server.manager;

import java.util.List;
import java.util.TreeMap;

import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.boatrace.server.stat.StStatMlPtnFinal;

public class AnalyzeManager {

	public AnalyzeManager() {
	}
	
	public void execute(List<StStatMlPtnFinal> listFinal) {
		
		// 포함되는 디비레코드를 타임라인순으로 정열한다.
		TreeMap<String, BoatDbRecord> treeMap = new TreeMap<>();
		for (StStatMlPtnFinal stFinal : listFinal) {
			for (BoatDbRecord dbRec : stFinal.stPatternValue.listDbRecord) {
				String key = BoatDbRecord.getSortKey(dbRec);
				treeMap.put(key, dbRec);
			}
		}
		
		
		
	}
}

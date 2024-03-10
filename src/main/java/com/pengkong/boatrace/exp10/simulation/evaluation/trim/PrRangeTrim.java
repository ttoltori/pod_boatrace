package com.pengkong.boatrace.exp10.simulation.evaluation.trim;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.range.AbstractRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.LRtrimRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.LtrimRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.SelectiveLRtrimRangeFinder;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public class PrRangeTrim {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	AbstractRangeFinder rangeFinder;
	
	String trimType;
	
	void initialize() throws Exception {
		
		this.trimType = prop.getString("pr_trim");
		if (trimType.equals("L")) { // LTrim
			rangeFinder = new LtrimRangeFinder();
		} else if (trimType.equals("R")) { // RTrim
			rangeFinder = new LRtrimRangeFinder();
		} else if (trimType.equals("B")) { // Best
			rangeFinder = new SelectiveLRtrimRangeFinder();
		} else {
			throw new IllegalStateException("Undefined pr_tirm property");
		}
	}
	
	/**
	 * Evaluationのprに設定する範囲文字列を結合する。
	 * @return pr文字列 例) 0~1=1,3=5=1
	 * @throws Exception
	 */
	public String createPrString(DBRecord rec) throws Exception {
		if (trimType == null) {
			initialize();
		}

		// 範囲
		if (!trimType.equals("L")) {
			if (rec.getDouble("lpr_bestmin") == null || rec.getDouble("lpr_bestmax") == null ) {
				return null;
			}
			return String.valueOf(rec.getDouble("lpr_bestmin")) + Delimeter.WAVE.getValue() + String.valueOf( rec.getDouble("lpr_bestmax")) + "=1";
		} else if (!trimType.equals("R")) { 
			if (rec.getDouble("rpr_bestmin") == null || rec.getDouble("rpr_bestmax") == null ) {
				return null;
			}
			return String.valueOf(rec.getDouble("rpr_bestmin")) + Delimeter.WAVE.getValue() + String.valueOf( rec.getDouble("rpr_bestmax")) + "=1";
		} else  { 
			if (rec.getDouble("pr_bestmin") == null || rec.getDouble("pr_bestmax") == null ) {
				return null;
			}
			return String.valueOf(rec.getDouble("pr_bestmin")) + Delimeter.WAVE.getValue() + String.valueOf( rec.getDouble("pr_bestmax")) + "=1";
		}
	}
}

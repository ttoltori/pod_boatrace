package com.pengkong.boatrace.exp10.simulation.step2;

import org.apache.commons.lang3.math.NumberUtils;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public class TermPair {
	DBRecord recTerm1;
	DBRecord recTerm2;
	public TermPair(DBRecord recTerm1, DBRecord recTerm2) {
		super();
		this.recTerm1 = recTerm1;
		this.recTerm2 = recTerm2;
	}

	public String getGroupSqlId() {
		return recTerm1.getString("id_sql");
	}
	public String getFactor() {
		return recTerm1.getString("id_factor");
	}
	public String getCustom() {
		return recTerm1.getString("id_custom");
	}
	public String getIncr() {
		return recTerm1.getString("id_incr");
	}
	public String getResultEvalId() {
		return recTerm1.getString("id_modelno") + "_6m";
	}
	public String getBonusBork(String key) {
		Double minT1 = recTerm1.getDouble("bork_bestmin");
		Double maxT1 = recTerm1.getDouble("bork_bestmax");
		Double minT2 = recTerm2.getDouble("bork_bestmin");
		Double maxT2 = recTerm2.getDouble("bork_bestmax");
		if (key.equals("and")) {
			return String.join(Delimeter.WAVE.getValue(), 
					String.valueOf(NumberUtils.max(minT1, minT2)),
					String.valueOf(NumberUtils.min(maxT1, maxT2))
			) + "=1";
		} if (key.equals("or")) {
			return String.join(Delimeter.WAVE.getValue(), 
					String.valueOf(NumberUtils.min(minT1, minT2)),
					String.valueOf(NumberUtils.max(maxT1, maxT2))
			) + "=1";
		} else {
			return "x";
		}
	}

	public String getBonusBor(String key) {
		Double minT1 = recTerm1.getDouble("bor_bestmin");
		Double maxT1 = recTerm1.getDouble("bor_bestmax");
		Double minT2 = recTerm2.getDouble("bor_bestmin");
		Double maxT2 = recTerm2.getDouble("bor_bestmax");
		if (key.equals("and")) {
			return String.join(Delimeter.WAVE.getValue(), 
					String.valueOf(NumberUtils.max(minT1, minT2)),
					String.valueOf(NumberUtils.min(maxT1, maxT2))
			) + "=1";
		} if (key.equals("or")) {
			return String.join(Delimeter.WAVE.getValue(), 
					String.valueOf(NumberUtils.min(minT1, minT2)),
					String.valueOf(NumberUtils.max(maxT1, maxT2))
			) + "=1";
		} else {
			return "x";
		}
	}
}

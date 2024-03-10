package com.pengkong.boatrace.exp10.simulation.data;

import java.util.List;

import com.pengkong.boatrace.server.db.dto.DBRecord;

public abstract class AbstractRaceDataLoader {

	public List<DBRecord> load(String fromYmd, String toYmd) throws Exception {
		return excute(fromYmd, toYmd);
	}
	
	protected abstract List<DBRecord> excute(String fromYmd, String toYmd) throws Exception;
}

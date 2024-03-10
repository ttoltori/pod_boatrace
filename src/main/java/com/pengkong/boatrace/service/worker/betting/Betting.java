package com.pengkong.boatrace.service.worker.betting;

import com.pengkong.boatrace.model.RaceEx;

public interface Betting {
	public RaceEx createAndSetBetList(RaceEx race) throws Exception;
}

package com.pengkong.boatrace.service.rules;

import com.pengkong.boatrace.model.Race;

public interface RaceRule {
	boolean isAllowed(Race race);
}

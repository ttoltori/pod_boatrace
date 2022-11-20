package com.pengkong.boatrace.service.rules.impl;

import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.service.rules.RaceRule;

public class RaceRuleBaseImpl implements RaceRule {

	@Override
	public boolean isAllowed(Race race) {
		return true;
	}

}

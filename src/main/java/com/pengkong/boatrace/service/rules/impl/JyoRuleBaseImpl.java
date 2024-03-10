package com.pengkong.boatrace.service.rules.impl;

import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.service.rules.JyoRule;

public class JyoRuleBaseImpl implements JyoRule {

	@Override
	public boolean isAllowed(Race race) {
		return true;
	}

}

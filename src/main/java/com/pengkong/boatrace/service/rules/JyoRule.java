package com.pengkong.boatrace.service.rules;

import com.pengkong.boatrace.model.Race;

public interface JyoRule {

	boolean isAllowed(Race race);
}

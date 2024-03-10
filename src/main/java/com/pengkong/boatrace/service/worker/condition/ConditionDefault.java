package com.pengkong.boatrace.service.worker.condition;

import com.pengkong.boatrace.model.RaceEx;

public class ConditionDefault implements Condition{

	public ConditionDefault() {
	}
	
	@Override
	public String getName() {
		return "レース条件";
	}

	@Override
	public boolean isPreConditionOk(RaceEx race) {
		return true;
	}

	@Override
	public boolean isBeforeConditionOk(RaceEx race) {
		return true;
	}

}

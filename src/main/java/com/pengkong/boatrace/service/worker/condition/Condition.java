package com.pengkong.boatrace.service.worker.condition;

import com.pengkong.boatrace.model.RaceEx;

public interface Condition {

	public boolean isPreConditionOk(RaceEx race);
	public boolean isBeforeConditionOk(RaceEx race);
	public String getName();
}

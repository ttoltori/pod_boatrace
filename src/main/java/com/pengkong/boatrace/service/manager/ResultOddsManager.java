package com.pengkong.boatrace.service.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Deprecated
public class ResultOddsManager extends AbstractOddsManager {
	Logger logger = LoggerFactory.getLogger(ResultOddsManager.class);
	private static ResultOddsManager instance = new ResultOddsManager();
	public static ResultOddsManager getInstance() {
		return instance;
	}

	@Override
	protected String getOddsFilepath(String ymd) {
		return prop.getString("DIRECTORY_ODDS") + "odds_" + ymd + ".csv";
	}
}

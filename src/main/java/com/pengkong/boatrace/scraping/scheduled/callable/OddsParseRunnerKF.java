package com.pengkong.boatrace.scraping.scheduled.callable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.exception.FatalException;
import com.pengkong.boatrace.scraping.parser.OddsKFPageParser;
import com.pengkong.boatrace.scraping.scheduled.OddsParseResult;

public class OddsParseRunnerKF extends AbstractOddsParseRunner {

	public OddsParseRunnerKF(String url) {
		super(url);
	}

	@Override
	public OddsParseResult execute() throws IOException, FatalException {
		OddsParseResult result = new OddsParseResult();

		OddsKFPageParser parser = new OddsKFPageParser();
		HashMap<String, Float[]> mapParsed = parser.parse(url);
		
		Map<BetType, Object> map = new HashMap<>();
		map.put(BetType._KF, mapParsed);
		result.setData(map);
		
		return result;
	}
}

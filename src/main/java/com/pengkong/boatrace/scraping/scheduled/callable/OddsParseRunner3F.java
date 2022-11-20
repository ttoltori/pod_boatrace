package com.pengkong.boatrace.scraping.scheduled.callable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.exception.FatalException;
import com.pengkong.boatrace.scraping.parser.Odds3FPageParser;
import com.pengkong.boatrace.scraping.scheduled.OddsParseResult;

public class OddsParseRunner3F extends AbstractOddsParseRunner {

	public OddsParseRunner3F(String url) {
		super(url);
	}

	@Override
	public OddsParseResult execute() throws IOException, FatalException {
		OddsParseResult result = new OddsParseResult();

		Odds3FPageParser parser = new Odds3FPageParser();
		HashMap<String, Float> mapParsed = parser.parse(url);
		
		Map<BetType, Object> map = new HashMap<>();
		map.put(BetType._3F, mapParsed);
		result.setData(map);
		
		return result;
	}
}

package com.pengkong.boatrace.scraping.scheduled.callable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.scraping.parser.Odds1TFPageParser;
import com.pengkong.boatrace.scraping.scheduled.OddsParseResult;

public class OddsParseRunner1TF extends AbstractOddsParseRunner {

	public OddsParseRunner1TF(String url) {
		super(url);
	}

	@Override
	public OddsParseResult execute() throws IOException {
		OddsParseResult result = new OddsParseResult();
		
		Odds1TFPageParser parser = new Odds1TFPageParser();
		parser.parse(url);
		
		Map<BetType, Object> map = new HashMap<>();
		map.put(BetType._1T, parser.getOdds1T());
		map.put(BetType._1F, parser.getOdds1F());
		result.setData(map);
		
		return result;
	}
}

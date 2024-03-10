package com.pengkong.boatrace.scraping.scheduled.callable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.scraping.parser.Odds2TFPageParser;
import com.pengkong.boatrace.scraping.scheduled.OddsParseResult;

public class OddsParseRunner2TF extends AbstractOddsParseRunner {

	public OddsParseRunner2TF(String url) {
		super(url);
	}

	@Override
	public OddsParseResult execute() throws IOException {
		OddsParseResult result = new OddsParseResult();

		Odds2TFPageParser parser = new Odds2TFPageParser();
		List<HashMap<String, Float>> list = parser.parse(url);
		
		Map<BetType, Object> map = new HashMap<>();
		map.put(BetType._2T, list.get(0));
		map.put(BetType._2F, list.get(1));
		result.setData(map);
		
		return result;
	}
}

package com.pengkong.boatrace.factory;

import com.pengkong.boatrace.scraping.scheduled.callable.AbstractOddsParseRunner;
import com.pengkong.boatrace.scraping.scheduled.callable.OddsParseRunner1TF;
import com.pengkong.boatrace.scraping.scheduled.callable.OddsParseRunner2TF;
import com.pengkong.boatrace.scraping.scheduled.callable.OddsParseRunner3F;
import com.pengkong.boatrace.scraping.scheduled.callable.OddsParseRunner3T;

public class OddsParserFactory {

	public static AbstractOddsParseRunner createParser1TF(String url) {
		return new OddsParseRunner1TF(url);
	}
	
	public static AbstractOddsParseRunner createParser2TF(String url) {
		return new OddsParseRunner2TF(url);
	}

	public static AbstractOddsParseRunner createParser3T(String url) {
		return new OddsParseRunner3T(url);
	}

	public static AbstractOddsParseRunner createParser3F(String url) {
		return new OddsParseRunner3F(url);
	}
}

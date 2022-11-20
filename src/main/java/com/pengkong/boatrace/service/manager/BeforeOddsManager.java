package com.pengkong.boatrace.service.manager;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.Odds;
import com.pengkong.boatrace.scraping.parser.Odds3TPageParser;

@Deprecated
public class BeforeOddsManager extends AbstractOddsManager {
	Logger logger = LoggerFactory.getLogger(BeforeOddsManager.class);
	private static BeforeOddsManager instance = new BeforeOddsManager();
	public static BeforeOddsManager getInstance() {
		return instance;
	}
	
	@Override
	protected String getOddsFilepath(String ymd) {
		return prop.getString("DIRECTORY_ODDSBEFORE") + "odds_" + ymd + ".before.csv";
	}
	
	/**
	 * 実践用
	 * @param yyyyMMdd
	 * @param jyoCd
	 * @param raceNo
	 * @param type
	 * @throws Exception
	 */
	public void loadCurrentOdds(String yyyyMMdd, String jyoCd, String raceNo, String type) throws Exception {
		String urlTemplate = BoatTemplate.URL_ODDS_BASE.replace("{raceNo}", raceNo)
				.replace("{jyoCd}", jyoCd)
				.replace("{yyyyMMdd}", yyyyMMdd);
		// 3連単
		String url = urlTemplate.replace("{oddsType}", "3t");
		HashMap<String, Float> oddsMap = new Odds3TPageParser().parse(url);
		String line = yyyyMMdd + "," + jyoCd + "," + raceNo + "," + type + "," + Odds.toCsv(oddsMap); 
		loadLine(line);
	}
}

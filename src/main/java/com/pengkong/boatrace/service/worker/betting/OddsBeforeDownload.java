package com.pengkong.boatrace.service.worker.betting;

import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.scraping.OddsBeforeDownloader;
import com.pengkong.boatrace.simulation.model.BettingRule;

public class OddsBeforeDownload implements Betting {

	BettingRule rule;
	
	OddsBeforeDownloader oddsBeforeDownloader = new OddsBeforeDownloader();
	
	public OddsBeforeDownload(BettingRule rule) throws Exception {
		this.rule = rule;
	}
	
	@Override
	public RaceEx createAndSetBetList(RaceEx race) throws Exception {
		oddsBeforeDownloader.download(race.raceInfo.ymd, race.getJyoCd(), race.getRaceNo());
		race.skip = "OddsBeforeDownload";
		
		return race;
	}
}

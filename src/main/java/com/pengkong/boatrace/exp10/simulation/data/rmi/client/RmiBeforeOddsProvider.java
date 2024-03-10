package com.pengkong.boatrace.exp10.simulation.data.rmi.client;

import java.util.Map;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.BeforeOddsProvider;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.data.rmi.server.RmiDataServerInterface;

public class RmiBeforeOddsProvider extends BeforeOddsProvider {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	RmiDataServerInterface rmiClient = DataServerClinet.getInstance();
	
	@Override
	protected Map<String, Odds> loadDailyOdds(String ymd) throws Exception {
		return rmiClient.getBeforeOdds(ymd);
	}
	
	@Override
	protected String[] createBettypes() {
		return prop.getString("dataserver_odds_bettypes").split(Delimeter.COMMA.getValue());
	}
}

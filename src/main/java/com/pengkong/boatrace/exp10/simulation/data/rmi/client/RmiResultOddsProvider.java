package com.pengkong.boatrace.exp10.simulation.data.rmi.client;

import java.util.Map;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.ResultOddsProvider;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.data.rmi.server.RmiDataServerInterface;

public class RmiResultOddsProvider extends ResultOddsProvider {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	RmiDataServerInterface rmiClient = DataServerClinet.getInstance();
	
	@Override
	protected Map<String, Odds> loadDailyOdds(String ymd) throws Exception {
		return rmiClient.getResultOdds(ymd);
	}
	
	@Override
	protected String[] createBettypes() {
		return prop.getString("dataserver_odds_bettypes").split(Delimeter.COMMA.getValue());
	}
}

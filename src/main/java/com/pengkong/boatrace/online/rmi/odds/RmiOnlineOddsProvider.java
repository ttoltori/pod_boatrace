package com.pengkong.boatrace.online.rmi.odds;

import java.rmi.Naming;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.OddsProviderInterface;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class RmiOnlineOddsProvider implements OddsProviderInterface {
	Logger logger = LoggerFactory.getLogger(RmiOnlineOddsProvider.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	RmiOnlineOddsServerInterface server = null;
	
	void initialize() throws Exception {
		String rmiUrl = prop.getString("odds_server_rmi_url");
		server = (RmiOnlineOddsServerInterface) Naming.lookup(rmiUrl);
	}
	
	void ensureInitialized() throws RemoteException{
		if (server == null) {
			try {
				initialize();
			} catch (Exception e) {
				logger.error("rmi online odds client initialization failed. ");
				throw new RemoteException("rmi online odds client initialization failed. ", e);
			}
		}
	}
	
	@Override
	public Odds get(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws Exception {
		ensureInitialized();
		
		return server.getOdds(ymd, jyoCd, raceNo, betType, kumiban);
	}
}

package com.pengkong.boatrace.online.rmi;

import java.rmi.Naming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.online.rmi.odds.RmiOnlineOddsServerInterface;
import com.pengkong.boatrace.online.rmi.tohyo.RmiOnlineTohyoServerInterface;

public class RmiOnlineClient {
	Logger logger = LoggerFactory.getLogger(RmiOnlineClient.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance(); 

	RmiOnlineOddsServerInterface oddsServer;
	
	RmiOnlineTohyoServerInterface tohyoServer;
	
	public RmiOnlineClient() {
	}

	public void stopTohyoServer() throws Exception {
		if (tohyoServer == null) {
			String rmiUrl = prop.getString("tohyo_server_rmi_url");
			tohyoServer = (RmiOnlineTohyoServerInterface) Naming.lookup(rmiUrl);
		}
		tohyoServer.stop();
	}

	public void stopOddsServer() throws Exception {
		if (oddsServer == null) {
			String rmiUrl = prop.getString("odds_server_rmi_url");
			oddsServer = (RmiOnlineOddsServerInterface) Naming.lookup(rmiUrl);
		}
		oddsServer.stop();
	}
	
	public boolean isAliveOddsServer() throws Exception {
		if (oddsServer == null) {
			String rmiUrl = prop.getString("odds_server_rmi_url");
			oddsServer = (RmiOnlineOddsServerInterface) Naming.lookup(rmiUrl);
		}
		
		return oddsServer.isAlive();
	}
}

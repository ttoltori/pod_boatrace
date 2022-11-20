package com.pengkong.boatrace.online.rmi.odds;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.online.odds.OnlineOddsServer;
import com.pengkong.common.PropertyUtil;

@SuppressWarnings("serial")
public class RmiOnlineOddsServer extends UnicastRemoteObject implements RmiOnlineOddsServerInterface {
	Logger logger = LoggerFactory.getLogger(RmiOnlineOddsServer.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance(); 

	OnlineOddsServer server;
	
	protected RmiOnlineOddsServer() throws RemoteException {
		server = new OnlineOddsServer();
	}
	
	@Override
	public Odds getOdds(String ymd, String jyoCd, String raceNo, String betType, String kumiban)
			throws RemoteException {
		try {
			return server.get(ymd, jyoCd, raceNo, betType, kumiban);
		} catch (Exception e) {
			logger.error("online odds server access failed.", e);
			throw new RemoteException("rmi online odds server error.");
		}
	}
	
	public void start() {
		
		String rmiUrl = prop.getString("odds_server_rmi_url");
		int port = prop.getInteger("odds_server_rmi_port"); 
		
		try {
			LocateRegistry.createRegistry(port);
			Naming.rebind(rmiUrl, this);
		} catch (Exception e) {
			logger.error("rmi rebind failed.", e);
			return;
		}
		
		server.start();
	}

	@Override
	public void stop() {
		server.stop(4);
	}

	@Override
	public boolean isAlive() throws RemoteException {
		return true;
	}
	
	public static void main(String[] args) {
		String propertyFilepath = args[0];
		// PropertyUtilを使っている箇所もあるので互換性確保のために
		try {
			PropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil.getInstance().addFile(propertyFilepath);
			
			new RmiOnlineOddsServer().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

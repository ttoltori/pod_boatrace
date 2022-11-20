package com.pengkong.boatrace.online.rmi.tohyo;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.online.tohyo.OnlineTohyoServer;
import com.pengkong.common.PropertyUtil;

@SuppressWarnings("serial")
public class RmiOnlineTohyoServer extends UnicastRemoteObject implements RmiOnlineTohyoServerInterface {
	
	Logger logger = LoggerFactory.getLogger(RmiOnlineTohyoServer.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance(); 
	
	OnlineTohyoServer server;
	
	protected RmiOnlineTohyoServer() throws RemoteException {
		server = new OnlineTohyoServer();
	}

	public void start() {
		String rmiUrl = prop.getString("tohyo_server_rmi_url");
		int port = prop.getInteger("tohyo_server_rmi_port"); 
		
		try {
			LocateRegistry.createRegistry(port);
			Naming.rebind(rmiUrl, this);
		} catch (Exception e) {
			logger.error("rmi rebind failed.");
			return;
		}
		
		server.start();
		
		logger.info("rmi online tohyo server started.");
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
			
			new RmiOnlineTohyoServer().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

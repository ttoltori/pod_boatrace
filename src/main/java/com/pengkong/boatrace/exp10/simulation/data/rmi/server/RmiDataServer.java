package com.pengkong.boatrace.exp10.simulation.data.rmi.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class RmiDataServer {
	MLPropertyUtil prop = MLPropertyUtil.getInstance(); 
	static Logger logger = LoggerFactory.getLogger(RmiDataServer.class);
	
	public void start() throws Exception {
		RmiDataServerInterface server = new DataServer();
		
		int port = prop.getInteger("dataserver.rmi.port"); 
		String rmiUrl = prop.getString("dataserver.rmi.url");
		
		LocateRegistry.createRegistry(port);
		
		Naming.rebind(rmiUrl, server);
		
	}
	
	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/github/pod_boatrace/properties/expr10/expr10.properties";
		
		//String propertyFilepath = args[0];
		
		try {
			MLPropertyUtil prop = MLPropertyUtil.getInstance();
			prop.addFile(propertyFilepath);
			
			new RmiDataServer().start();
		} catch (Exception e) {
			logger.error("RmiDataServer failed", e);
			e.printStackTrace();
		}
	}
}

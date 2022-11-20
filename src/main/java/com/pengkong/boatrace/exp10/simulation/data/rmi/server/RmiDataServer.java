package com.pengkong.boatrace.exp10.simulation.data.rmi.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class RmiDataServer {
	MLPropertyUtil prop = MLPropertyUtil.getInstance(); 
	
	public void start() throws Exception {
		RmiDataServerInterface server = new DataServer();
		
		int port = prop.getInteger("dataserver.rmi.port"); 
		String rmiUrl = prop.getString("dataserver.rmi.url");
		
		LocateRegistry.createRegistry(port);
		
		Naming.rebind(rmiUrl, server);
		
	}
	
	public static void main(String[] args) {
		String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		
		//String propertyFilepath = args[0];
		
		try {
			MLPropertyUtil prop = MLPropertyUtil.getInstance();
			prop.addFile(propertyFilepath);
			
			new RmiDataServer().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.pengkong.boatrace.server.rmi.arff;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.common.PropertyUtil;

public class ArffRmiServer {
	Logger logger = LoggerFactory.getLogger(ArffRmiServer.class);

	PropertyUtil prop = PropertyUtil.getInstance();
	
	public void start(String propFilePath) throws Exception{
		prop.addFile(propFilePath);
		
		ArffRmiInterface provider = new ArffProvider();
		int port = prop.getInteger("ArffProvider.rmi.port"); 
		String rmiUrl = prop.getString("ArffProvider.rmi.url");
		LocateRegistry.createRegistry(port);
		
		Naming.rebind(rmiUrl, provider);
		logger.info("ArffRmiServer started.");
	}
	public static void main(String[] args) {
		args = new String[] {
				"C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/output.properties" 
		};
		
		if (args.length != 1) {
			System.out.println("usage: ArffRmiServer {property file path}");
			System.exit(1);
		}
		String propFilePath = args[0];
		
		try {
			ArffRmiServer server = new ArffRmiServer();
			server.start(propFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

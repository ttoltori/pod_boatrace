package com.pengkong.boatrace.server.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import com.pengkong.common.PropertyUtil;

public class BoatDbRmiServer {

	public BoatDbRmiServer() {
	}
	
	public static void main(String[] args) {
//		args = new String[] {
//				"C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties" 
//		};
		
		if (args.length != 1) {
			System.out.println("usage: BoatDbRmiServer {property file path}");
			System.exit(1);
		}
		
		String propFilePath = args[0];
		
		try {
			PropertyUtil prop = PropertyUtil.getInstance();
			prop.addFile(propFilePath);
			
			BoatDbRmiInterface boatDbProvider = new BoatDbProvider();
			int port = prop.getInteger("BoatDbProvider.rmi.port"); 
			String rmiUrl = prop.getString("BoatDbProvider.rmi.url");
			LocateRegistry.createRegistry(port);
			
			Naming.rebind(rmiUrl, boatDbProvider);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.pengkong.boatrace.server.rmi.regression;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import com.pengkong.common.PropertyUtil;

public class RegressionDbRmiServer {

	public RegressionDbRmiServer() {
	}
	
	public static void main(String[] args) {
		args = new String[] {
				"C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties" 
		};
		
		if (args.length != 1) {
			System.out.println("usage: BoatDbRmiServer {property file path}");
			System.exit(1);
		}
		
		String propFilePath = args[0];
		RegressionDbRmiInterface boatDbProvider;
		try {
			PropertyUtil prop = PropertyUtil.getInstance();
			prop.addFile(propFilePath);
			
			boatDbProvider = new RegressionDbProvider();
			int port = prop.getInteger("RegressionDbProvider.rmi.port"); 
			String rmiUrl = prop.getString("RegressionDbProvider.rmi.url");
			LocateRegistry.createRegistry(port);
			
			Naming.rebind(rmiUrl, boatDbProvider);
			
			//boatDbProvider.createCsv("20180101", "20180831", "C:/Dev/workspace/Oxygen/pod_boatrace_test/wekamodels/regression_arff/test1T.csv", "1T");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}

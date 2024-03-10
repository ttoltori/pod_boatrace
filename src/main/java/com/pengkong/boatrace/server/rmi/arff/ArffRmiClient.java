package com.pengkong.boatrace.server.rmi.arff;

import java.rmi.Naming;
import java.rmi.RemoteException;

import com.pengkong.common.PropertyUtil;

public class ArffRmiClient implements ArffRmiInterface {

	PropertyUtil prop = PropertyUtil.getInstance();
	
	ArffRmiInterface arffProvider;
	
	public ArffRmiClient() throws Exception {
		String rmiUrl = prop.getString("ArffProvider.rmi.url");
		arffProvider = (ArffRmiInterface) Naming.lookup(rmiUrl);
	}

	@Override
	public void create(String defFileName, String fromYmd, String toYmd) throws RemoteException {
		arffProvider.create(defFileName, fromYmd, toYmd);
	}

	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/output.properties");
			ArffRmiClient client = new ArffRmiClient();
			client.create("214_2T_21_entry.def", "20170101", "20181231");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

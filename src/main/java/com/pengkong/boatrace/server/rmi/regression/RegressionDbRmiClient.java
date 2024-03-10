package com.pengkong.boatrace.server.rmi.regression;

import java.rmi.Naming;
import java.rmi.RemoteException;

import com.pengkong.common.PropertyUtil;

public class RegressionDbRmiClient implements RegressionDbRmiInterface {

	PropertyUtil prop = PropertyUtil.getInstance();
	
	RegressionDbRmiInterface boatDbProvider;
	
	public RegressionDbRmiClient(String rmiUrl) throws Exception {
		boatDbProvider = (RegressionDbRmiInterface) Naming.lookup(rmiUrl);
	}

	@Override
	public void createCsv(String fromYmd, String toYmd, String filePath, String betType) throws RemoteException {
		boatDbProvider.createCsv(fromYmd, toYmd, filePath, betType);
	}
}

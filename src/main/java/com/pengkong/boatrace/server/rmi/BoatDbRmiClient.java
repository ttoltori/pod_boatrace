package com.pengkong.boatrace.server.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.common.PropertyUtil;

public class BoatDbRmiClient implements BoatDbRmiInterface {

	PropertyUtil prop = PropertyUtil.getInstance();
	
	BoatDbRmiInterface boatDbProvider;
	
	public BoatDbRmiClient() throws Exception {
		String rmiUrl = prop.getString("BoatDbProvider.rmi.url");
		boatDbProvider = (BoatDbRmiInterface) Naming.lookup(rmiUrl);
	}

	@Override
	public List<BoatDbRecord> getBoatDbRecordList(String fromYmd, String toYmd, String bettype, String kumiban)
			throws RemoteException {
		return boatDbProvider.getBoatDbRecordList(fromYmd, toYmd, bettype, kumiban);
	}

	@Override
	public void loadDB(String fromYmd, String toYmd) throws RemoteException {
		boatDbProvider.loadDB(fromYmd, toYmd);
	}
}

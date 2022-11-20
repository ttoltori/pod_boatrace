package com.pengkong.boatrace.exp10.simulation.data.rmi.client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.data.rmi.server.RmiDataServerInterface;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public class DataServerClinet implements RmiDataServerInterface {
	Logger logger = LoggerFactory.getLogger(DataServerClinet.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	RmiDataServerInterface server = null;
	
	private static DataServerClinet ISNTANCE = new DataServerClinet();
	public static DataServerClinet getInstance() {
		return ISNTANCE;
	}
	public DataServerClinet() {
	}
	
	void initialize() throws Exception {
		String rmiUrl = prop.getString("dataserver.rmi.url");
		server = (RmiDataServerInterface) Naming.lookup(rmiUrl);
	}
	
	void ensureInitialized() throws RemoteException{
		if (server == null) {
			try {
				initialize();
			} catch (Exception e) {
				logger.error("rmi client initialization failed. ");
				throw new RemoteException("rmi client initialization failed. ", e);
			}
		}
	}
	
	@Override
	public List<DBRecord> getSimulationData(String fromYmd, String toYmd) throws RemoteException {
		ensureInitialized();
		
		return server.getSimulationData(fromYmd, toYmd);
		
	}

	@Override
	public List<MlClassification> getClassificationData(List<String> modelNoList) throws RemoteException {
		ensureInitialized();

		return server.getClassificationData(modelNoList);
	}
	@Override
	public Map<String, Odds> getBeforeOdds(String ymd) throws RemoteException {
		ensureInitialized();
		
		return server.getBeforeOdds(ymd);
	}

	@Override
	public Map<String, Odds> getResultOdds(String ymd) throws RemoteException {
		ensureInitialized();
		
		return server.getResultOdds(ymd);
	}
}

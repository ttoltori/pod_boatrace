package com.pengkong.boatrace.exp10.simulation.data.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.BeforeOddsCachedProvider;
import com.pengkong.boatrace.exp10.odds.provider.ResultOddsCachedProvider;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.server.db.dto.DBRecord;

@SuppressWarnings("serial")
public class DataServer extends UnicastRemoteObject implements RmiDataServerInterface {
	Logger logger = LoggerFactory.getLogger(DataServer.class);

	/** DB race data server */
	DBRaceDataServer raceServer = new DBRaceDataServer();

	/** 複数モデルのclassificationデータをまとめて取得する */
	MultiClassificationDataServer clfServer = new MultiClassificationDataServer();

	/** 直前オッズCache */
	BeforeOddsCachedProvider beforeOddsCache = new BeforeOddsCachedProvider();

	/** 確定オッズCache */
	ResultOddsCachedProvider resultOddsCache = new ResultOddsCachedProvider();

	protected DataServer() throws RemoteException {
		super();
		try {
			initialize();
		} catch (Exception e) {
			logger.error("DataServer initialization failed.", e);
			throw new RemoteException("DataServer initialization failed.");
		}
	}

	void initialize() throws Exception {
		raceServer.load();
		clfServer.load();
	}
	
	@Override
	public List<MlClassification> getClassificationData(List<String> modelNoList) throws RemoteException {
		logger.debug("classification data retrieving..." + modelNoList);
		try {
			return clfServer.getClassificationData(modelNoList);
		} catch (Exception e) {
			logger.error("multi classification data server failed. " + modelNoList, e);
			throw new RemoteException("multi classification data server failed.");
		}
	}
	
	@Override
	public List<DBRecord> getSimulationData(String fromYmd, String toYmd) throws RemoteException {
		logger.debug("simulation data retrieving..." + fromYmd + "~" + toYmd);
		try {
			return raceServer.getSimulationData(fromYmd, toYmd);
		} catch (Exception e) {
			logger.error("race data server failed. " + fromYmd + "~" + toYmd, e);
			throw new RemoteException("race data server failed.");
		}
	}


	@Override
	public Map<String, Odds> getBeforeOdds(String ymd) throws RemoteException {
		try {
			return beforeOddsCache.getDaily(ymd);
		} catch (Exception e) {
			logger.error("Before Odds Cache access error.", e);
			throw new RemoteException("rmi server error.");
		} 
	}

	@Override
	public Map<String, Odds> getResultOdds(String ymd) throws RemoteException {
		try {
			return resultOddsCache.getDaily(ymd);
		} catch (Exception e) {
			logger.error("Result Odds Cache access error.", e);
			throw new RemoteException("rmi server error.");
		} 
	}
}

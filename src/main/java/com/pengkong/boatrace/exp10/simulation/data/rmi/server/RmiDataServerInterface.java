package com.pengkong.boatrace.exp10.simulation.data.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public interface RmiDataServerInterface extends Remote {
	 /** 期間内のレース情報をDBから取得する */
	public List<DBRecord> getSimulationData(String fromYmd, String toYmd) throws RemoteException;

	/**
	 * 指定モデル番号リストに対してMlClassificationリストを取得する。
	 * @param modelNoList ex) [99089,99094]
	 */
	public List<MlClassification> getClassificationData(List<String> modelNoList) throws RemoteException;
	
	/** 指定日付の直前オッズデータを取得する（サーバーが保持しているデータ：対象bettypeをserver propertyeに依存）
	 * @param ymd 日付
	 * @return key = ymd_jyocd_raceno_bettype_kumiban  value=Odds
	 * @throws RemoteException
	 */
	public Map<String, Odds> getBeforeOdds(String ymd) throws RemoteException;

	public Map<String, Odds> getResultOdds(String ymd) throws RemoteException;
}

package com.pengkong.boatrace.server.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.pengkong.boatrace.server.db.dto.BoatDbRecord;

public interface BoatDbRmiInterface extends Remote {
	/**
	 * 디베 데이터를 지정한 범위의 날짜만큼 반환한다. 
	 * bettype, kumiban이 설정된 경우는 일치하는 데이터만 반환한다.
	 * 
	 * @param fromYmd
	 * @param toYmd
	 * @param bettype
	 * @param kumiban
	 * @return
	 * @throws RemoteException
	 */
	public List<BoatDbRecord> getBoatDbRecordList(String fromYmd, String toYmd, String bettype, String kumiban) throws RemoteException;
	public void loadDB(String fromYmd, String toYmd) throws RemoteException;
}

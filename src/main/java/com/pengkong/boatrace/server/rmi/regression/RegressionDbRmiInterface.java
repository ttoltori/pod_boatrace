package com.pengkong.boatrace.server.rmi.regression;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegressionDbRmiInterface extends Remote {
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
	public void createCsv(String fromYmd, String toYmd, String filePath, String betType) throws RemoteException;
}

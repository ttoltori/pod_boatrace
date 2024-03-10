package com.pengkong.boatrace.server.rmi.arff;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ArffRmiInterface extends Remote {
	public void create(String defFileName, String fromYmd, String toYmd) throws RemoteException;
}

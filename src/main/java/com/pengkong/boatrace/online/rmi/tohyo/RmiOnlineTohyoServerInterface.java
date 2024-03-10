package com.pengkong.boatrace.online.rmi.tohyo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiOnlineTohyoServerInterface extends Remote {
	public void stop() throws RemoteException;
	
	public boolean isAlive() throws RemoteException;
}

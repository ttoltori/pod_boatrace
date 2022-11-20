package com.pengkong.boatrace.online.rmi.odds;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.pengkong.boatrace.exp10.odds.Odds;

public interface RmiOnlineOddsServerInterface extends Remote {
	/** オッズ取得 returnがnullの場合はodds取得失敗*/
	public Odds getOdds(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws RemoteException;
	
	public void stop() throws RemoteException;
	
	public boolean isAlive() throws RemoteException;
}

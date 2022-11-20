package com.pengkong.boatrace.exp10.remote.client;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.exp10.remote.common.RemoteRequest;
import com.pengkong.boatrace.exp10.remote.common.RemoteResponse;

/**
 * 複数のRemoteRequestをまとめて送受信を行うための集合クラス。
 * @author ttolt
 *
 */
public class RemoteRequestSet {
	/** 通信状態 */
	public volatile ReqStatus status;
	
	/** 要求の処理残件数 */
	public volatile int remainCount = 0;
	
	public List<RemoteRequest> listReq = new ArrayList<>();
	public List<RemoteResponse> listRes = new ArrayList<>();

	public RemoteRequestSet() {
		super();
		this.status = ReqStatus.COMPLETED;
	}

	public void addRequest(RemoteRequest req) {
		listReq.add(req);
		remainCount++;
		this.status = ReqStatus.READY;
	}
	
	public synchronized void receivedResponse(RemoteResponse res) {
		listRes.add(res);
		
		remainCount--;  //残件数減少
		
		// 応答全受信完了
		if (remainCount <= 0) {
			// 通信完了へ状態変更
			status = ReqStatus.COMPLETED;
		}
	}
	
	/**
	 * @return true=要求件数分の応答件数を受信した。
	 * 各応答が正常かはわからない.
	 */
	public boolean isCompleted() {
		return (remainCount <= 0);
	}
	
	/** 通信状態 */
	enum ReqStatus {
		// 通信前
		READY,
		// 通信中
		REQUESTING,
		// 通信完了
		COMPLETED
	}
}

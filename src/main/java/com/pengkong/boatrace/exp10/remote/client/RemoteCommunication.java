package com.pengkong.boatrace.exp10.remote.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.factory.AbstractBoatWebSocketFactory;
import com.pengkong.boatrace.exp10.factory.BoatWebSocketFactory;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.client.RemoteRequestSet.ReqStatus;
import com.pengkong.boatrace.exp10.remote.common.RemoteRequest;
import com.pengkong.boatrace.exp10.remote.common.RemoteResponse;
import com.pengkong.boatrace.util.JsonParser;
import com.pengkong.common.Prop;

public class RemoteCommunication {
	Logger logger = LoggerFactory.getLogger(RemoteCommunication.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	AbstractBoatWebSocketFactory socketFactory = new BoatWebSocketFactory();
	
	/** 通信用WebSocket ex) key="weka" or "python"   value=BoatWebSocketClient */
	Map<String, BoatWebSocketClient> mapSocketClient;

	boolean initialized = false;
	
	public RemoteCommunication() {
	}

	private void initialize() {
		mapSocketClient = new HashMap<>();
		// "websocket_url_*"のプロパティ全てに対してsocketを生成する
		List<Prop> propList = prop.getProperties("websocket_url_"); //startsWith "websocket_url_"
		for (Prop p : propList) {
			String key = p.key.split("_")[2]; // websocket_url_weka -> weka
			try {
				mapSocketClient.put(key, socketFactory.create(p.value));
			} catch (URISyntaxException e) {
				logger.error("invalid uri." + p.value);
			}	
		}
		initialized = true;
	}

	private void checkInitialized() {
		if (!initialized) {
			initialize();
		}
	}

	public void setWebSocketFactory(AbstractBoatWebSocketFactory factory) {
		this.socketFactory = factory;
	}
	
	/** 
	 * 保持している全WebSocketをオープンする.
	 */
	public void connect() {
		checkInitialized();
		for (BoatWebSocketClient client : mapSocketClient.values()) {
			client.connect();
		}
	}
	
	/** 
	 * 保持している全WebSocketをクローズする.
	 */
	public void disconnect() {
		checkInitialized();
		for (BoatWebSocketClient client : mapSocketClient.values()) {
			client.close();
		}
	}

	public RemoteRequestSet send(RemoteRequestSet reqSet) throws Exception {
		checkInitialized();
		// 通信完了になるまでループ
		while (!reqSet.status.equals(ReqStatus.COMPLETED)) {
			if (reqSet.status.equals(ReqStatus.READY)) { // 通信前
				reqSet.status = ReqStatus.REQUESTING; // 通信中
				// 要求送信
				ResponseHandler handler = new RemoteResponseHandler(reqSet);
				for (RemoteRequest req : reqSet.listReq) {
					if (req.isReferentialRequest()) {
						continue;
					}
					String jsonStr = JsonParser.encode(req);
					getSocketClient(req).sendJson(jsonStr, handler);
				}
			}
		}
		
		return reqSet;
	}
	
	BoatWebSocketClient getSocketClient(RemoteRequest req) {
		if (RemoteRequest.isPython(req)) {
			return mapSocketClient.get("python");
		}
		return mapSocketClient.get("weka");
	}
	
	/**
	 * 個別requestのresponseを受信してRemoteRequestSetに結果を繁栄するクラス
	 * 
	 * @author ttolt
	 *
	 */
	public class RemoteResponseHandler implements ResponseHandler {
		Logger logger = LoggerFactory.getLogger(RemoteResponseHandler.class);
		
		/** 元となる要求集合 */
		RemoteRequestSet reqSet;
		
		public RemoteResponseHandler(RemoteRequestSet reqSet) {
			super();
			this.reqSet = reqSet;
		}

		@Override
		public void handle(String jsonStr) {
			
			RemoteResponse res = null;
			// json parsing
			try {
			res = (RemoteResponse) JsonParser.decodeObject(jsonStr, RemoteResponse.class);
				// 応答追加
				reqSet.receivedResponse(res);
			} catch (IOException e) {
				logger.error("invalid json response :" + jsonStr, e);
			}
		}
	}
}

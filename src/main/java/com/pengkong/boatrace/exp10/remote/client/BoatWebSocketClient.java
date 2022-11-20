package com.pengkong.boatrace.exp10.remote.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoatWebSocketClient extends WebSocketClient {
	Logger logger = LoggerFactory.getLogger(BoatWebSocketClient.class);
	
	/** JSONの受信メッセージハンドラ */
	ResponseHandler responseHandler;
	
	public BoatWebSocketClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public BoatWebSocketClient(URI serverURI) {
		super(serverURI);
	}

	public void sendJson(String jsonStr, ResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
		send(jsonStr);
	}
	
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		logger.debug("new connection opened");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		logger.debug("closed with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(String message) {
		// 受信ハンドラに処理を委任する
		responseHandler.handle(message);
	}

	@Override
	public void onMessage(ByteBuffer message) {
		logger.debug("received ByteBuffer");
	}

	@Override
	public void onError(Exception ex) {
		logger.error("an error occurred:" + ex.getMessage());
	}

	public static void main(String[] args) throws URISyntaxException {		
		WebSocketClient client = new BoatWebSocketClient(new URI("ws://localhost:8887"));
		client.connect();
	}
}
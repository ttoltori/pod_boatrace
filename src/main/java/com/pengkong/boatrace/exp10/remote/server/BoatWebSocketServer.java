package com.pengkong.boatrace.exp10.remote.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.remote.server.service.JsonRequestDispatcher;
import com.pengkong.boatrace.exp10.model.ModelInfoManager;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.remote.server.service.AbstractRequestDispatcher;

/**
 * WebSocket server for json-rpc
 * 
 * @author ttolt
 *
 */
public class BoatWebSocketServer extends WebSocketServer {
	Logger logger = LoggerFactory.getLogger(BoatWebSocketServer.class);

	/** サービス提供 */
	AbstractRequestDispatcher dispatcher;;

	public BoatWebSocketServer(InetSocketAddress address, AbstractRequestDispatcher dispatcher) {
		super(address);
		this.dispatcher = dispatcher;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// conn.send("Welcome to the server!"); //This method sends a message to the new
		// client
		// broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This
		// method sends a message to all clients connected
		logger.debug("new connection to " + conn.getRemoteSocketAddress());
		try {
			ModelInfoManager.getInstance().scan();
		} catch (Exception e) {
			logger.error("model files scan error" + conn.getRemoteSocketAddress(), e);
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		logger.debug(
				"closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		String response = dispatcher.dispatch(message);

		// send response
		conn.send(response);
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		logger.debug("received ByteBuffer from " + conn.getRemoteSocketAddress());
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		logger.error("an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
		// send response
		conn.send("{Server Error}");
	}

	@Override
	public void onStart() {
		logger.info("server started successfully");
	}

	public static void main(String[] args) {
		//String propertyFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties";
		String propertyFilepath = args[0];

		try {
			MLPropertyUtil prop = MLPropertyUtil.getInstance();
			prop.addFile(propertyFilepath);
			String url = prop.getString("websocket_url_weka");
			String[] token = url.split("//")[1].split(":");

			String host = token[0];
			int port = Integer.valueOf(token[1]);

			BoatWebSocketServer server = new BoatWebSocketServer(new InetSocketAddress(host, port), new JsonRequestDispatcher());
			
			server.run();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

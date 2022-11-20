package com.pengkong.boatrace.exp10.factory;

import java.net.URISyntaxException;

import com.pengkong.boatrace.exp10.remote.client.BoatWebSocketClient;

public abstract class AbstractBoatWebSocketFactory {
	public abstract BoatWebSocketClient create(String url) throws URISyntaxException;
}

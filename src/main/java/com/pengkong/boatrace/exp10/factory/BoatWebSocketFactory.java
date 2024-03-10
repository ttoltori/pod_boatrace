package com.pengkong.boatrace.exp10.factory;

import java.net.URI;
import java.net.URISyntaxException;

import com.pengkong.boatrace.exp10.remote.client.BoatWebSocketClient;

public class BoatWebSocketFactory extends AbstractBoatWebSocketFactory {

	@Override
	public BoatWebSocketClient create(String url) throws URISyntaxException{
		return new BoatWebSocketClient( new URI(url));
	}
}

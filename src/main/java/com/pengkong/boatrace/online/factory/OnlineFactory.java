package com.pengkong.boatrace.online.factory;

import com.pengkong.boatrace.online.api.AbstractApiProvider;

public class OnlineFactory {
	public static AbstractApiProvider createPcApiProvider() {
		return new MockPcApiProvider();
	}
}

package com.pengkong.boatrace.online.factory;

import com.pengkong.boatrace.online.api.AbstractApiProvider;
import com.pengkong.boatrace.online.api.pc.PcApiProvider;

public class OnlineFactory {
	public static AbstractApiProvider createPcApiProvider() {
		//return new MockPcApiProvider();
		//return new TestPcApiProvider();
		return new PcApiProvider();
	}
}

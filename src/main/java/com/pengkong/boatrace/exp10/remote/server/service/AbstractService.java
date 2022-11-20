package com.pengkong.boatrace.exp10.remote.server.service;

import com.pengkong.boatrace.exp10.remote.common.RemoteRequest;
import com.pengkong.boatrace.exp10.remote.common.RemoteResponse;

public abstract class AbstractService {
	public abstract RemoteResponse execute (RemoteRequest req) throws Exception;
}

package com.pengkong.boatrace.exp10.remote.common;

public class RemoteRequest {
	/** 要求識別子 responseとの連携のため*/
	public String id;
	
	/** ML algorithm */
	public String algorithmId;
	
	public Object param;

	public RemoteRequest() {
	}

	public RemoteRequest(String id, String algorithmId, Object param) {
		super();
		this.id = id;
		this.algorithmId = algorithmId;
		this.param = param;
	}
	
	/** true=python向けの要求 */
	public static boolean isPython(RemoteRequest req) {
		return req.algorithmId.endsWith("_py");
	}

	/**
	 * 当該要求がすでにあるモデルか判定する
	 * @param req
	 * @return true=他モデルへの参照
	 */
	public boolean isReferentialRequest() {
		RemoteRequestParam param = (RemoteRequestParam)this.param;
		return (!param.exNo.equals(param.modelNo));
	}
	
	
}

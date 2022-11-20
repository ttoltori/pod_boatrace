package com.pengkong.boatrace.exp10.remote.common;

public class RemoteResponse {
	/** 要求識別子 responseとの連携のため*/
	public String id;
	
	/** request type  @see RPCType */
	public String algorithmId;

	/** ml prediction 結果
	 * classificationの場合：各classの確率値が配列で設定される。
	 * regressionの場合：[0]=regression結果値のみ
	 */
	public double[] values;

	public String status;
	
	public RemoteResponse() {
	}

	public RemoteResponse(String id, String algorithmId, double[] values, String status) {
		super();
		this.id = id;
		this.algorithmId = algorithmId;
		this.values = values;
		this.status = status;
	}
}

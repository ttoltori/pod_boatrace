package com.pengkong.boatrace.common.enums;

/**
 * remote procedure call type
 * @author ttolt
 *
 */
public enum RemoteServiceStatus {
	OK("OK"),
	/** 他サービス結果参照要。(参照先モデルのclassificationをDBから取得する必要がある） */
	REF("REF"), 
	NG("NG");

	private final String status;
	
	private RemoteServiceStatus(String status) {
		this.status = status;
	}
	
	public String getValue() {
		return this.status;
	}

}

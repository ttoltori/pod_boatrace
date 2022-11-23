package com.pengkong.boatrace.exp10.enums;

public enum ResultType {
	/** result ip */
	_1("1"),
	
	/** result SG */
	_11("11"),
	
	/** simulation step 1 ip (個別実験） 実験毎にディレクトリ生成*/
	_2("2"),
	
	/** simulation step 1 SG (個別実験）*/
	_21("21"),

	/** simulation step 1 ip (個別実験） 実験毎にディレクトリ生成*/
	_3("3"),
	
	/** simulation step 1 SG (個別実験）*/
	_31("31"),

	/** simulation step 1 ip,SG  (一括実験） */
	_4("4"),

//	/** simulation step 1 SG  (一括実験、graph変更） */
//	_41("41"),
//
	/** simulation step 1 ip,SG (個別確認） */
	_5("5"),
	
	/** simulation step 2 最終確認 */
	_6("6");
	
	private final String featureType;
	
	private ResultType(String featureType) {
		this.featureType = featureType;
	}
	
	public String getValue() {
		return this.featureType;
	}
}

package com.pengkong.boatrace.exp10.remote.common;

public class RemoteRequestParam {
	/** 実験番号 */
	public String exNo;
	/** モデル番号。  通常は実験番号と一致。他モデルを参照している場合は参照先のモデル番号*/
	public String modelNo;
	public String rankNo;
	public String pattern;
	public String ymd;
	/** 拡張子を含む物理モデルファイル名 pythonでモデルファイル特定に使う
	 * （pythonサーバーにはModelInfoMangerを実現していないため）*/
	public String modelFileName;
	
	/** classification要求対象のデータ */
	public String[] values;

	public RemoteRequestParam() {
	}
}

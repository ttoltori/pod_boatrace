package com.pengkong.boatrace.exp10.model;

import com.pengkong.common.StringUtil;

/**
 * モデル情報。
 * @author ttolt
 *
 */
public class ModelInfo {
	/** 実験番号 */
	public String exNo;
	/** モデル番号。  通常は実験番号と一致。他モデルを参照している場合は参照先のモデル番号*/
	public String modelNo;
	public String rankNo;
	public String pattern;
	public int ymd;
	
	/** algoritm id ("_py"が含まれていればpythonモデルである) */
	public String algorithmId;
	
	/** モデルファイル名（拡張子含み）model file ロード時必要*/
	public String fileName;

	public String[] arffNames; /** weka classifier生成時に必要 */
	public String[] arffTypes; /** weka classifier生成時に必要 */
	public String[] classValues; /** weka classifier生成時に必要 */
	
	public ModelInfo(String exNo, String fileName) {
		this.exNo = exNo;
		this.fileName = fileName;
		parse(fileName);
	}

	/**
	 * ファイル名を分解する
	 * @param fileName
	 */
	void parse(String fileName) {
		// 拡張子分割 -> "_"分割
		String[] token = fileName.split("\\.")[0].split("_");
		modelNo = StringUtil.unpadZero(token[0]);  // ex) "00101" -> "101"
		pattern = token[1];
		ymd = Integer.valueOf(token[2]);
		rankNo = token[3].substring(4); // ex) "rank1"の1
	}

	/** true=python, false=weka*/
//	public boolean isPython() {
//		return algorithmId.endsWith("_py");
//	}

	public String toString(){
		return String.join("_", modelNo, rankNo, pattern, String.valueOf(ymd));
	}
}

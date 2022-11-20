package com.pengkong.boatrace.exp10.property;

/**
 * expr10_class.tsv
 * @author ttolt
 *
 */
public class Clazz {
	public String id;
	public String featuresSql;
	public String conditionSql;
	public String valuesArff;
	
	/**
	 * @reture true=class定義なし（関連モデルも存在しない）  
	 */
	public static boolean isUndefined(String classId) {
		return (classId.equals("x"));
	}

	/**
	 * @reture true=固定値のclassification  
	 */
	public static boolean isFixedClassification(String classId) {
		return (classId.startsWith("fixed"));
	}
}

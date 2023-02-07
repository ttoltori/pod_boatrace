package com.pengkong.boatrace.exp10.arff;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.enums.FeatureType;
import com.pengkong.boatrace.exp10.property.Clazz;
import com.pengkong.boatrace.exp10.property.Feature;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.weka.automation.NominalManager;
import com.pengkong.common.FileUtil;

/**
 * required properties: model_no
 * @author ttolt
 * @rev 20211126 python用csv対応
 *
 */
public class MLArffCreator {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	protected NominalManager nominalManager = NominalManager.getInstance();
	
	public MLArffCreator() {
	}

	/**
	 * arff파일을 생성한다. 
	 * @param listDb 대상 디비 데이터
	 * @param prefixFilepath arff파일풀패스 (뒤에 rank를 추가하여 완성시킨다.)
	 * @return 생성된 arff파일패스들의 리스트
	 * @throws Exception
	 */
	public void create(List<DBRecord> listDb, List<Feature> listFeature, Clazz clazz, String arffFilepath) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		// arffファイルならarffヘッダを生成する
		if (arffFilepath.endsWith(".arff")) {
			sb.append(createArffHeader(listFeature, clazz));
		}
		// csvデータ
		sb.append(createArffBody(listFeature, listDb));
		
		FileUtil.writeFile(arffFilepath, sb.toString());
	}
	
	/**
	 * Arffファイルのヘッダ部を生成する
	 * @param listFeature Featureのリスト (arffのAttributeパートの定義）
	 * @param clazz Clazz（arffのclassパートの定義）
	 * @return 
	 * @throws Exception
	 */
	protected StringBuilder createArffHeader(List<Feature> listFeature, Clazz clazz) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("% " + prop.getString("model_no")); sb.append(System.lineSeparator());
		sb.append("@RELATION expr_10"); sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		
		for (Feature feature : listFeature) {
			sb.append(convertFeature(feature));
			sb.append(System.lineSeparator());
		}
		
		if (clazz.valuesArff.equals(FeatureType.NUMERIC.getValue())) {
			sb.append("@ATTRIBUTE class " + FeatureType.NUMERIC.getValue());
		} else {
			sb.append("@ATTRIBUTE class {" + clazz.valuesArff + "}");
		}
		sb.append(System.lineSeparator());
		
		sb.append("@DATA");
		sb.append(System.lineSeparator());
		
		return sb;
	}
	
	protected StringBuilder createArffBody(List<Feature> listFeature, List<DBRecord> listDb) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		for (DBRecord rec : listDb) {
			for (Feature feature : listFeature) {
				sb.append(rec.getString(feature.arffName));
				sb.append(",");
			}
			sb.append(rec.getString("classes")); // class.tsv sql_class_features参照
			sb.append(System.lineSeparator());
		}
		
		return sb;
	}
	
	protected String convertFeature(Feature feature) {
		// nominal attribute
		if (feature.arffType.equals(FeatureType.NOMINAL.getValue())) {
			return "@ATTRIBUTE " + feature.arffName + " " + nominalManager.getNominalAttr(feature.arffName);
		} 
		
		// numeric attribute
		return "@ATTRIBUTE " + feature.arffName + " NUMERIC"; 
	}
	
	public static void main(String[] args) {
	}
}

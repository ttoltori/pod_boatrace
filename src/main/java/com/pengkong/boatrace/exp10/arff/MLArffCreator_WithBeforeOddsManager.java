package com.pengkong.boatrace.exp10.arff;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.enums.FeatureType;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.OddsProviderInterface;
import com.pengkong.boatrace.exp10.property.Clazz;
import com.pengkong.boatrace.exp10.property.Feature;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.data.rmi.client.RmiBeforeOddsProvider;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.weka.automation.NominalManager;
import com.pengkong.common.FileUtil;

/**
 * required properties: model_no
 * @author ttolt
 * @rev 20211126 python用csv対応
 *
 */
@Deprecated
public class MLArffCreator_WithBeforeOddsManager {
	Logger logger = LoggerFactory.getLogger(MLArffCreator_WithBeforeOddsManager.class);
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	NominalManager nominalManager = NominalManager.getInstance();
	
	/** 直前オッズprovider */
	protected OddsProviderInterface beforeOddsProvider = new RmiBeforeOddsProvider();
	public MLArffCreator_WithBeforeOddsManager() {
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
		
		boolean useBodds = false;
		String bOddsTarget = prop.getString("bodds_target");
		if (bOddsTarget != null && !bOddsTarget.equals("x")) {
			useBodds = true;
		}
		
		for (DBRecord rec : listDb) {
			for (Feature feature : listFeature) {
				if (useBodds && (feature.id.equals("bork") || feature.id.equals("bodds"))) {
					// ex) 2T-12
					String[] betInfo = bOddsTarget.split(Delimeter.DASH.getValue());
					Odds bOdds =  beforeOddsProvider.get(rec.getString("ymd"), rec.getString("jyocd"), rec.getStringForced("raceno"), betInfo[0], betInfo[1] );
					if (feature.id.equals("bork")) {
					    sb.append(String.valueOf(bOdds.rank));
					} else {
					    sb.append(String.valueOf(bOdds.value));
					}
	                sb.append(",");
					
	                continue;
				}
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

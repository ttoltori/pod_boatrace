package com.pengkong.boatrace.exp10.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.ClazzTemplate;
import com.pengkong.boatrace.exp10.property.Feature;
import com.pengkong.boatrace.exp10.property.FeatureSetTemplate;
import com.pengkong.boatrace.exp10.property.FeatureTemplate;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.util.MLUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.StringUtil;

/**
 * model_configを基に実際のモデルファイルを全てスキャンして保持する。
 * @author ttolt
 */
public class ModelInfoManager {
	Logger logger = LoggerFactory.getLogger(ModelInfoManager.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** feature set 定義 */
	FeatureSetTemplate featureSetTemplate = FeatureSetTemplate.getInstance();
	
	/** mode_config.tsvの設定値がx */
	private static String USE_NONE = "x";
	
	/** key = 実験番号 + rankNo + パタン, value=ModelFileSet */
	HashMap<String, ModelInfoSet> mapModelSet = new HashMap<>();
	
	/** scan済みフラグ */
	boolean isScanned = false;
	
	private static class InstanceHolder {
		private static final ModelInfoManager INSTANCE = new ModelInfoManager();
	}

	public static ModelInfoManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public ModelInfoManager() {
	}

	/**
	 * パラメータをキーにして利用するモデルファイルを返却する
	 * @param exNo 実験番号
	 * @param rankNo rankNo
	 * @param pattern パターン
	 * @param ymd 日付
	 * @return
	 */
	public ModelInfo get(String exNo, String rankNo, String pattern, String ymd) throws Exception {
		if (!isScanned) {
			scan();
		}
		
		// key = 実験番号 + rankNo + パタン
		String key = String.join("_", exNo, rankNo, pattern);
		ModelInfoSet mfs = mapModelSet.get(key);
		
		// （実験番号 + rankNo + パタン）以下の複数日付から適切な日付のモデルファイル情報を探して返却する.
		return mfs.get(Integer.valueOf(ymd));
	}

	/**
	 * プロパティ指定(dir_model_release)以下の全てのモデルファイル情報をスキャンする
	 * @throws Exception
	 */
	public synchronized void scan() throws Exception {
		HashMap<String, ModelInfoSet> mapModelSet = new HashMap<>();
		
		// ex) ["D:\Dev\experiment\expr10\model_release", "00001", "1", "nopattern"]
		String[] dirParts = new String[4];
		String dirModel = prop.getString("dir_model_release");
		dirParts[0] = dirModel.substring(0, dirModel.length() -1);
		
		prop.reset("file_model_config");
		while(prop.hasNext()) {
			prop.next();
			String exNo = prop.getString("model_no"); // 実験No
			
			// class_rank[1,2,3]のループ
			List<String> listClassId = getClassIdList();
			for (int i = 0; i < listClassId.size(); i++) {
				String refExno = getRefExnoFromClassId(listClassId.get(i));
				// classIdが他モデルを参照している場合 ex) 1=(r1-123456)
				if (refExno == null) {
					dirParts[1] = StringUtil.leftPad(exNo, BoatConst.LEFT_PAD, "0");
				} else {
					// classificationが固定値でモデルは存在しない
					if (refExno.equals("fixed")) {
						continue;
					}
					// 参照先のモデルnoを代入する
					dirParts[1] = StringUtil.leftPad(refExno, BoatConst.LEFT_PAD, "0");
				}

				String rankNo = String.valueOf(i+1);
				dirParts[2] = rankNo;
				
				// 実際のディレクトリからパタンのリストを取得する ("nopattern" 固定)
				List<String> listPattern = FileUtil.listDirName(String.join("/", dirParts[0], dirParts[1], dirParts[2]));
				// 存在しないディレクトリの場合は無視する
				if (listPattern == null) {
					logger.warn("No such directory exist. " + String.join("/", dirParts[0], dirParts[1], dirParts[2]));
					continue;
				}

				// patternループ
				for (String pattern : listPattern) {
					dirParts[3] = pattern;
					ModelInfoSet mfs = new ModelInfoSet(exNo, rankNo, pattern);
					
					List<String> listFilename = FileUtil.listFileName(String.join("/", dirParts), "model");
					// model file ループ
					List<ModelInfo> listModelFile = new ArrayList<>();
					for (String fileName : listFilename) {
						listModelFile.add(createModelInfo(exNo, rankNo, fileName));
					}
					
					// 最新日付順でソート
					listModelFile.sort((ModelInfo c1, ModelInfo c2) -> c2.ymd - c1.ymd);
					mfs.models = listModelFile;
					
					// key = 実験番号 + rankNo + パタン
					String key = String.join("_", exNo, rankNo, pattern);
					mapModelSet.put(key, mfs);
				}
			}
		}
		
		this.mapModelSet = mapModelSet;
		isScanned = true;
	}
	
	/**
	 * python ML server用モデル情報保存
	 * @throws Exception
	 */
	public void saveModelProperties() throws Exception {
		String filePath = prop.getString("file_model_info");
		
		StringBuilder sb = new StringBuilder();
		
		prop.reset("file_model_config");
		while(prop.hasNext()) {
			prop.next();
			String modelNo = prop.getString("model_no"); // 実験No
			
			// class_rank[1,2,3]のループ
			List<String> listClassId = getClassIdList();
			for (int i = 0; i < listClassId.size(); i++) {
				String rankNo = String.valueOf(i+1);
				// 他モデルを参照している場合はスキップする。pythonサーバでは実モデルファイルをロードするため参照情報は不要
				if (getRefExnoFromClassId(listClassId.get(i)) != null) {
					continue;
				}
				sb.append(createModelInfoStr(modelNo, rankNo));
				sb.append("\n");
			}
		}
		
		FileUtil.writeFile(filePath, sb.substring(0, sb.length()-1).toString());
	}
	
	/**
	 * pythonサーバ参照用のモデル情報を文字列で取得する
	 * @return ex) 00001_rank1=r1-123456::cf_lgbm-1_py::nw1,nw2,nw3,nw4,nw5,nw6::float,float,float,float,float,float
	 * @throws Exception
	 */
	String createModelInfoStr(String modelNo, String rankNo) throws Exception {
		FeatureSetTemplate fsTpl = FeatureSetTemplate.getInstance();
		FeatureTemplate featureTemplate = FeatureTemplate.getInstance();

		StringBuilder sb = new StringBuilder();
		
		sb.append(modelNo + "_" + "rank" + rankNo + "=");
		sb.append(prop.getString("class_rank" + rankNo) + "::");
		sb.append(prop.getString("algorithm_rank" + rankNo) + "::");
		
		String featureSetId = prop.getString("features_rank" + rankNo);
		String featuresIdString = fsTpl.getFeatureIdString(featureSetId);
		sb.append( featuresIdString  + "::"  );
		
		StringBuilder sb2 = new StringBuilder();
		String[] featureIds = fsTpl.getFeatureIds(featureSetId);
		List<Feature> featureList  = featureTemplate.getFeatureList(featureIds);
		for (Feature fe : featureList) {
			try {
				sb2.append(BoatConst.featureTypeMap.get(fe.arffType));
				sb2.append(",");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sb.append(sb2.substring(0, sb2.length()-1));
		
		return sb.toString();
	}
	
	/**
	 * model_config.tsv内容と合わせてモデル情報を取得する
	 * @param exNo 実験番号
	 * @param rankNo 順番
	 * @param fileName 物理ファイル名(拡張子含み)
	 * @return
	 */
	ModelInfo createModelInfo(String exNo, String rankNo, String fileName) throws Exception {
		FeatureTemplate featureTemplate = FeatureTemplate.getInstance();
		ClazzTemplate classTemplate = ClazzTemplate.getInstance();
		
		ModelInfo mi = new ModelInfo(exNo, fileName);
		mi.algorithmId = prop.getString("algorithm_rank" + rankNo);
		
		String featureSetId = prop.getString("features_rank" + rankNo);
		String[] featureIds = featureSetTemplate.getFeatureIds(featureSetId);

		List<Feature> listFeature = featureTemplate.getFeatureList(featureIds);
		String[] arffNames = new String[listFeature.size()];
		String[] arffTypes = new String[listFeature.size()];
		for (int i = 0; i < listFeature.size(); i++) {
			
			arffNames[i] =  listFeature.get(i).arffName;
			arffTypes[i] =  listFeature.get(i).arffType;
		}
		
		String classId = prop.getString("class_rank" + rankNo);
		if (MLUtil.isReferentialClassId(classId)) { // ex) 1=(r1-123456)  通常ならr1-123456
			classId = StringUtil.getStringBetween(classId.split(Delimeter.EQUAL.getValue())[1], "(", ")");
		}
		String[] classValues = (classTemplate.getClazz(classId).valuesArff).split(Delimeter.COMMA.getValue());

		mi.arffNames = arffNames;
		mi.arffTypes = arffTypes;
		mi.classValues = classValues;
		
		return mi;
	}
	
	/**
	 * classIdが参照型なら参照先のexNoを返却する
	 * @param classId
	 * @return null=参照型でない fixed=スキップ（モデル情報はない）  その他=参照先のexNo
	 */
	String getRefExnoFromClassId(String classId) {
		if (!MLUtil.isReferentialClassId(classId))
			return null;
		
		return classId.split("=")[0];
	}
	/**
	 * propertyに設定されているclass_rank*の値をすべて変換する. 値が"x"の場合はスキップする
	 * @return ex) [1=(r1-123456), r2-1-23456]
	 */
	List<String> getClassIdList() {
		List<String> result = new ArrayList<>();
		int rankCnt = prop.getInteger("rankcnt");
		for (int rankNo = 1; rankNo <= rankCnt; rankNo++) {
			String classId = prop.getString("class_rank" + rankNo);
			if (!classId.equals(USE_NONE)) {
				result.add(classId);
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		try {
			MLPropertyUtil.getInstance().addFile("C:/Dev/github/pod_boatrace/properties/expr10/expr10.properties");
			
			ModelInfoManager mfm = new ModelInfoManager();
			mfm.saveModelProperties();
			
//			mfm.scan();
//			System.out.println(mfm.get("2", "1", "nopattern", "20150101"));
//			System.out.println(mfm.get("2", "1", "nopattern", "20151230"));
//			System.out.println(mfm.get("2", "1", "nopattern", "20151231"));
//			System.out.println(mfm.get("2", "1", "nopattern", "20160101"));
//			System.out.println(mfm.get("2", "1", "nopattern", "20160130"));
//			System.out.println(mfm.get("2", "1", "nopattern", "20160201"));
//			System.out.println(mfm.get("2", "1", "nopattern", "20160229"));
//			System.out.println(mfm.get("2", "1", "nopattern", "20160301"));
//			System.out.println(mfm.get("2", "1", "nopattern", "20161231"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

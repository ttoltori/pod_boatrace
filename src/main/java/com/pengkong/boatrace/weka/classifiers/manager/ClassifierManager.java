package com.pengkong.boatrace.weka.classifiers.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.weka.classifiers.BoatClassifierDefault;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * 2019/5/19 classifier관리의 키를 bettype+ymd에서 bettype+classes+ymd로 변경
 * @author qwerty
 *
 */
public class ClassifierManager {
	// key = bettype + "_" + ymd JSJ キーを変更 2019/5/19
	// key = bettype + "_" + classes  + "_" + ymd
	HashMapList<ClassifierInfo> mapListMultiClassifier;
	
	List<ClassifierInfo> listMultiClassifierInfo;

	// key = bettype + "_" + classes , value = ymd
	private HashMap<String, String> mapcurrentYmdByBettypeClasses;

	private static ClassifierManager instance;
	static { 
		try {
			instance = new ClassifierManager();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public ClassifierManager() throws Exception {
		// model_release의 전 모델정보를 로드한다.
		loadClassifierInfo(PropertyUtil.getInstance().getString("DIRECTORY_WEKAMODELS"), "model_release", "_"); 
	}
	
	public static ClassifierManager getInstance() {
		return instance;
	}
	
	/**
	 * 모델파일들의 정보를 로드한다.
	 * @param dirWekaModelsHome 웨카모델 홈 디렉토리
	 * @param modelDirectory 모델파일 디렉토리명
	 * @param modelNoList (로딩할 모델들을 선별하기 위한 컴마구분 검색 문자열 (모델번호등) null은 전 모델 로드 
	 * @throws Exception
	 */
	public void loadClassifierInfo(String dirWekaModelsHome, String modelDirectory, String modelNoList) throws Exception {
		mapListMultiClassifier = new HashMapList<>();
		listMultiClassifierInfo = new ArrayList<>();
		mapcurrentYmdByBettypeClasses = new HashMap<>();
		
		String[] tokenNo = modelNoList.split(",");
		String fnameOnly;
		
		File[] files = FileUtil.listFilesByExtension(dirWekaModelsHome + modelDirectory, "model");
		for (File file : files) {
			fnameOnly = file.getName().split("\\.")[0];
			
			if (!isValid(tokenNo, fnameOnly)) {
				continue;
			}
				
			ClassifierInfo cInfo = ClassifierInfo.createInstance(fnameOnly);
			cInfo.modelFilepath = file.getPath();
			cInfo.defFilepath = dirWekaModelsHome + cInfo.no + "_" + cInfo.bettype + "_" + cInfo.classes + "_" + cInfo.attributes + ".def";
			
			// def file validation
			checkValidDefFilepath(cInfo);
			
			mapListMultiClassifier.addItem(cInfo.bettype + "_" + cInfo.classes + "_" + cInfo.trainToYmd, cInfo);
			listMultiClassifierInfo.add(cInfo);
		}
		
		// 最新日付順でソート
		listMultiClassifierInfo.sort((ClassifierInfo c1, ClassifierInfo c2) -> c2.trainToYmd - c1.trainToYmd);
	}

	/** 최초에 _multi접두사를 붙인 것들도 호환성유지를 위해 체크한다. */
	private void checkValidDefFilepath(ClassifierInfo cInfo) throws Exception {
		File file = new File(cInfo.defFilepath);
		if (file.exists()) {
			return;
		}
		
		cInfo.defFilepath = cInfo.defFilepath.replace(".def", "_multi.def");
		file = new File(cInfo.defFilepath);
		if (file.exists()) {
			return;
		}
		
		throw new Exception("invalid def file path. -> " + cInfo.defFilepath);
	}

	
	/**
	 * 
	 * @param bettype 勝式
	 * @param kumiban  classifierで予測する組番の範囲 (例) 1, 123, 123-156
	 * @param currentYmd　日付
	 * @return
	 * @throws Exception
	 */
	public List<BoatClassifierDefault> getClassifierList(String bettype, String classes, String currentYmd) throws Exception {
		return getMultiClassifierList(bettype, classes, currentYmd);
	}
	

	/**
	 * 日付別に複数のモデルを使う場合、勝式と組番と日付をキーとして直近のモデルリストを取得する
	 * 使い切った日付のモデルはすべてクリアする
	 * @param bettype 勝式
	 * @param currentYmd 実行日の日付
	 * @return
	 * @throws Exception
	 */
	private List<BoatClassifierDefault> getMultiClassifierList(String bettype, String classes, String currentYmd) throws Exception {
		
		DateTime currDate = DateTime.parse(currentYmd, DateTimeFormat.forPattern("yyyyMMdd"));
		String strBeforeDayYmd = DateTimeFormat.forPattern("yyyyMMdd").print(currDate.minusDays(1));
		// 一日前の日付を取得する
		int recentDay = Integer.parseInt(strBeforeDayYmd);
		
		// モデル情報リストから直近の日付を取得する
		String keyYmd = null;
		for (ClassifierInfo cInfo : listMultiClassifierInfo) {
			if (cInfo.bettype.equals(bettype) && cInfo. classes.equals(classes) && cInfo.trainToYmd <= recentDay) {
				keyYmd = String.valueOf(cInfo.trainToYmd);
				break;
			}
		}
		
		String keyBettypeClasses = bettype + "_" + classes;
		String currentKyeYmd = mapcurrentYmdByBettypeClasses.get(keyBettypeClasses); 
		// モデルマップのキーを生成する
		String newKey = keyBettypeClasses + "_" + keyYmd;
		
		// 取得した日付が現在利用中でない場合
		if (!keyYmd.equals(currentKyeYmd)) {
			// 最初でない
			if (currentKyeYmd != null) {
				// 過ぎた日付のモデルはクリアする
				destroyClassifiers(mapListMultiClassifier.get(keyBettypeClasses + "_" + currentKyeYmd));
			}
			// キー日付を更新
			mapcurrentYmdByBettypeClasses.put(keyBettypeClasses, keyYmd);
		}
		
		return ClassifierInfo.convertInfoList2ClassifierList(mapListMultiClassifier.get(newKey));
	}
	
	private boolean isValid(String[] token, String fileName) {
		for (String str : token) {
			if (fileName.contains(str)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * モデル情報リストの全モデルをクリアする
	 * @param listClassifierInfo
	 */
	private void destroyClassifiers(List<ClassifierInfo> listClassifierInfo) {
		for (ClassifierInfo cInfo : listClassifierInfo) {
			cInfo.classifier.destroy();
		}
	}

	public static void main(String[] args) {
		try {
			ClassifierManager cm = new ClassifierManager();
			// BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties");
			
			cm.loadClassifierInfo(PropertyUtil.getInstance().getString("DIRECTORY_WEKAMODELS"), "model_release", "_");
			//cm.loadClassifierInfo(BoatProperty.DIRECTORY_WEKAMODELS, "model_release", "102_2T_12-41_");
			
			List<BoatClassifierDefault> models = cm.getClassifierList("2T", "21", "20191229");
			System.out.println(models);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

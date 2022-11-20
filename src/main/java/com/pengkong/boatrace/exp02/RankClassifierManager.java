package com.pengkong.boatrace.exp02;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.pengkong.boatrace.weka.automation.DefineFileParser;
import com.pengkong.common.FileEx;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

/**
 * 모델번호 이하의 모델들을 관리한다.
 * @author qwerty
 *
 */
public class RankClassifierManager {
	PropertyUtil prop = PropertyUtil.getInstance();
	DefineFileParser def;
	
	/** key = pattern_trainToYmd */
	HashMap<String, RankBoatClassifierInfo> mapClsInfo;
	
	/** 랭크모델정보의 날짜순 리스트 */
	List<RankBoatClassifierInfo> listClsInfo;
	
	/** 랭크모델들의 날짜 리스트 */
	List<String> listTrainToYmd = new ArrayList<>();
	
	/** 모델정보 관리자 */
	RankModelManager modelMgr = RankModelManager.getInstance();
	
	/** 패턴 정보 관리자 */
	RankPatternManager patternMgr = RankPatternManager.getInstance();
	
	private static class InstanceHolder {
		private static final RankClassifierManager INSTANCE = new RankClassifierManager();
	}
	
	public static RankClassifierManager getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public RankClassifierManager() {
	}

	public void initialize(String defineFilepath) throws Exception {
		this.def = new DefineFileParser();
		def.parseFile(defineFilepath);
		
		// 모델번호로 모델들을 로딩한다.
		loadClassifierInfo(def.getValue("no"));
	}
	
	public void loadClassifierInfo(String modelNo) throws Exception {
		mapClsInfo = new HashMap<>();
		listClsInfo = new ArrayList<RankBoatClassifierInfo>();
		
		String dirModel = prop.getString("dir_model_release") + modelNo;
		List<File> files = FileUtil.listFilesByExtensionRecursively(dirModel, "model");

		for (File file : files) {
			// 결과생성제외 패턴은 스킵한다.
			if (!isValidForResult(file)) {
				continue;
			}
			
			BoatClassifierInfo cInfo = BoatClassifierInfo.create(file, this.def.defineFilepath);
			String key = cInfo.getParentKey();
			RankBoatClassifierInfo rankCInfo = mapClsInfo.get(key);
			if (rankCInfo == null) {
				rankCInfo = new RankBoatClassifierInfo(cInfo.no, cInfo.pattern, cInfo.trainToYmd);
				mapClsInfo.put(key, rankCInfo);
				listClsInfo.add(rankCInfo);
			}
			rankCInfo.add(cInfo);
		}
		
		// 最新日付順でソート
		listClsInfo.sort((RankBoatClassifierInfo c1, RankBoatClassifierInfo c2) -> c2.trainToYmd - c1.trainToYmd);
		TreeSet<String> setYmd = new TreeSet<>();
		for (RankBoatClassifierInfo rcInfo : listClsInfo) {
			setYmd.add(String.valueOf(rcInfo.trainToYmd));
		}
		listTrainToYmd.addAll(setYmd);
	}

	/**
	 * 결과 생성 대상 패턴인지를 판정한다.
	 * @return
	 */
	private boolean isValidForResult(File file) throws Exception {
		FileEx fx = new FileEx(file);
		if (fx.getPattern().endsWith("_exclude")) {
			return false;
		}
		
		return true;
//		String patternName = modelMgr.getPatternName(fx.getDescription());
//		return patternMgr.isValidForModel(patternName, fx.getPattern());
	}
	
	public RankBoatClassifier getClassifier(String pattern, String currentYmd) throws Exception {
		DateTime currDate = DateTime.parse(currentYmd, DateTimeFormat.forPattern("yyyyMMdd"));
		String strBeforeDayYmd = DateTimeFormat.forPattern("yyyyMMdd").print(currDate.minusDays(1));
		// 一日前の日付を取得する
		int recentDay = Integer.parseInt(strBeforeDayYmd);
		
		// モデル情報リストから直近の日付を取得する
		String keyYmd = null;
		for (RankBoatClassifierInfo cInfo : listClsInfo) {
			if (cInfo.pattern.equals(pattern) && cInfo.trainToYmd <= recentDay) {
				keyYmd = String.valueOf(cInfo.trainToYmd);
				break;
			}
		}
		
		RankBoatClassifierInfo rcInfo = null;
		// モデルマップのキーを生成する
		String mapKey = pattern + "_" + keyYmd;
		
		// 랭크모델정보 취득
		rcInfo = mapClsInfo.get(mapKey);
		if (rcInfo == null) {
			return null;
		}
		
		// 기존 사용중인 모델이 있다면 반환한다.
		if (rcInfo.rankClassifier != null) {
			return rcInfo.rankClassifier;
		}

		// 모델정보로부터 본체 생성
		rcInfo.rankClassifier =  RankBoatClassifier.create(mapClsInfo.get(mapKey));	
		
		// 해당 패턴에 대해 직전날짜의 모델본체가 로딩되어있다면 삭제한다.
		// 직전모델날짜 취득
		String beforeKeyYmd = null;
		for (int i=0; i < listTrainToYmd.size(); i++) {
			if (listTrainToYmd.get(i).equals(keyYmd) && i > 0) {
				beforeKeyYmd = listTrainToYmd.get(i - 1);
				break;
			}
		}
		
		// 해당 패턴의 직전 모델정보 취득
		RankBoatClassifierInfo beforeRcInfo = mapClsInfo.get(pattern + "_" + beforeKeyYmd);
		if (beforeRcInfo != null && beforeRcInfo.rankClassifier != null) {
			// 직전모델정보 삭제
			beforeRcInfo.destroy();
		}
		
		return rcInfo.rankClassifier;
	}
	
	public static void main(String[] args) {
		String defFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_test/experiment/expr02/0001_rank_nopattern_nation3rate.def";
		String propFilepath = "C:/Dev/workspace/Oxygen/pod_boatrace_test/experiment/expr02/exp02.properteis";
		
		try {
			PropertyUtil.getInstance().addFile(propFilepath);

			RankClassifierManager clsMgr = new RankClassifierManager();
			clsMgr.initialize(defFilepath);
			
			RankBoatClassifier cls = clsMgr.getClassifier("nopattern", "20180228");
			cls = clsMgr.getClassifier("nopattern", "20180302");
			cls = clsMgr.getClassifier("nopattern", "20180303");
			System.out.println(cls);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

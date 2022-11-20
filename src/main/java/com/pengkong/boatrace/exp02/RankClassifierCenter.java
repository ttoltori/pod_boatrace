package com.pengkong.boatrace.exp02;

import java.util.HashMap;
import java.util.List;

import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.exception.ClassifierNotExistException;
import com.pengkong.boatrace.server.db.dto.RankDbRecord;
import com.pengkong.boatrace.weka.automation.DefineFileParser;
import com.pengkong.common.PropertyUtil;

public class RankClassifierCenter {
	PropertyUtil prop = PropertyUtil.getInstance();
	DefineFileParser def;

	HashMap<String, RankClassifierManager> mapManager = new HashMap<>();
/*	
	private static class InstanceHolder {
		private static final RankClassifierCenter INSTANCE = new RankClassifierCenter();
	}
	
	public static RankClassifierCenter getInstance() {
		return InstanceHolder.INSTANCE;
	}
*/	
	public RankClassifierCenter() {
	}

	/**
	 * 필터리스트에 포함되는 모든 모델들의 관리자들을 생성하고 초기화한다.
	 * @param listRankFilter
	 * @throws Exception
	 */
	public void initialize(List<RankDbRecord> listRankFilter) throws Exception {
		for (RankDbRecord rec : listRankFilter) {
			String key = rec.getString(DBCol.DESCRIPTION) + "_" + rec.getString(DBCol.PATTERN);
			if (!mapManager.containsKey(key)) {
				String defineFilepath = getDefFilepath(rec);
				RankClassifierManager mgr = new RankClassifierManager();
				mgr.initialize(defineFilepath);
				mapManager.put(key, mgr);
			}
		}
	}
	
	public String predict(RankDbRecord rec) throws Exception {
		RankBoatClassifier cls = getClassifier(rec.getString(DBCol.DESCRIPTION), 
				rec.getString(DBCol.PATTERN), rec.getString(DBCol.YMD));
		
		return cls.predictFromHashmap(rec);
	}
	
	private RankBoatClassifier getClassifier(String description, String pattern, String ymd) throws Exception {
		String key = description + "_" + pattern;
		
		RankClassifierManager mgr = mapManager.get(key);
		if (mgr == null) {
			throw new ClassifierNotExistException("description=" + description + ", pattern=" + pattern);
		}
		
		return mgr.getClassifier(pattern, ymd);
	}
	
	/** 
	 * RankFilter의 모델정보로부터 def파일패스를 취득한다.
	 * @param rec RankFilter
	 * @return
	 */
	private String getDefFilepath(RankDbRecord rec) {
		String defFileName = rec.getString(DBCol.DESCRIPTION) + "_rank_" + rec.getString(DBCol.PATTERN_NAME) + "_"
				+ rec.getString(DBCol.ATTRIBUTES);
		if (rec.getString(DBCol.ALGORITHM).equals("filter_bayes")) {
			defFileName += "_filterbayes";
		}

		return prop.getString("dir_model_def") + defFileName + ".def";
	}
}

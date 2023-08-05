package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * evaluationリストをロードして保持する.
 * @author ttolt
 *
 */
public abstract class AbstractEvaluationLoader{
	/** 実験プロパティ */
	protected MLPropertyUtil prop = MLPropertyUtil.getInstance();

	protected Logger logger = LoggerFactory.getLogger(AbstractEvaluationLoader.class);
	
	/**
	 * key = bettype_kumiban_modelno,
	 */
	protected HashMapList<Evaluation> mapListEval;
	
	/** 最初に取得した順のデータを保持する */
	protected List<Evaluation> listEval = new ArrayList<>();

	protected SqlSession session = null;
	
	/** group定義ファイルロード */
	abstract void  initialize() throws Exception;
	
	/** 初期化処理 */
	protected void checkInitialized() throws Exception {
		if (mapListEval == null) {
			try {
				initialize();
			} catch (Exception e) {
				if (session != null) {
					DatabaseUtil.close(session);
				}
				throw e;
			}
		}
	}

	public void printConsole() {
		int cnt = 1;
		for (Evaluation eval : listEval) {
			System.out.println(StringUtil.leftPad(String.valueOf(cnt++), 3, " ") + " : " + eval);
		}
		System.out.println(listEval.size() + " evaluations are used.");
	}
	
	public String getPatternIdModelString() throws Exception{
		checkInitialized();
		
		Set<String> setPntidMod = new HashSet<>();
		List<String> listPtnidMod = new ArrayList<>();
		for (Evaluation eval : listEval) {
			String ptnidMod = eval.get("patternid") + "@" + eval.get("modelno"); 
			if (!setPntidMod.contains(ptnidMod)) {
				listPtnidMod.add(ptnidMod);
				setPntidMod.add(ptnidMod);
			}
		}
		
		return String.join(Delimeter.UNDERBAR.getValue(), listPtnidMod);
	}
	
	/**
	 * evaluationを取得する
	 * @param key=betType + "_" + kumiban + "_" + modelNo
	 */
	public List<Evaluation> getEvaluations(String key) throws Exception {
		checkInitialized();
		return mapListEval.get(key);
	}
	
	/**
	 * 全てのevaluationを取得する
	 */
	public List<Evaluation> getEvaluations() throws Exception {
		checkInitialized();
		
		List<Evaluation> result = new ArrayList<>();
		for (List<Evaluation> list : mapListEval.values()) {
			result.addAll(list);
		}
		
		return result;
	}
}

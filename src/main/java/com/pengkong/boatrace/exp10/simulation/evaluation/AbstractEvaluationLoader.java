package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.common.StringUtil;
import com.pengkong.common.collection.HashMapList;

/**
 * evaluationリストをロードして保持する.
 * @author ttolt
 *
 */
public abstract class AbstractEvaluationLoader{
	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	Logger logger = LoggerFactory.getLogger(AbstractEvaluationLoader.class);
	
	/**
	 * key = bettype_kumiban_modelno,
	 */
	protected HashMapList<Evaluation> mapListEval;
	
	/** 最初に取得した順のデータを保持する */
	protected List<Evaluation> listEval = new ArrayList<>();
	
	/** group定義ファイルロード */
	abstract void  initialize() throws Exception;
	
	/** 初期化処理 */
	void checkInitialized() throws Exception {
		if (mapListEval == null) {
			initialize();
		}
	}
	
	public void printConsole() {
		int cnt = 1;
		for (Evaluation eval : listEval) {
			System.out.println(StringUtil.leftPad(String.valueOf(cnt++), 3, " ") + " : " + eval);
		}
		System.out.println(listEval.size() + " evaluations are used.");
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

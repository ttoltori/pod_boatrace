package com.pengkong.boatrace.exp10.result.stat.split;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.ResultHelper;
import com.pengkong.boatrace.exp10.result.stat.ResultStat;
import com.pengkong.boatrace.exp10.result.stat.ResultStatBuilderHelper;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.common.collection.HashMapList;

import lombok.Getter;

public class ResultStatBuilderSplit {
	Logger logger = LoggerFactory.getLogger(ResultStatBuilderSplit.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	/** key=statbettype+kumiban  value=betcnt */
	@Getter
	HashMapList<Double> mapTotalBetCnt = new HashMapList<>();
	
	/** key = pattern+bettype+kumiban  value=ResultStat */
	@Getter
	HashMapList<ResultStat> mapListStat = new HashMapList<>();

	ResultStatBuilderHelper statHelper = new ResultStatBuilderHelper();

	TermSplitterIndexer termIndexer = new TermSplitterIndexer();
	/**
	 * 初期化処理.
	 */
	public void clear() {
		mapTotalBetCnt = new HashMapList<>();
		mapListStat = new HashMapList<>();
		statHelper = new ResultStatBuilderHelper();
	}
	
	/* 以下ファイル保存機能は必要な時にpd_boatrace_oldから復元すること
	 * 統計情報をファイルに保存する
	 * @param exNo 実験番号
	 *
	public void save() throws Exception {
	*/
	
	/**
	 * 累積統計にml_resultデータを追加して、統計データを設定して返却する
	 * @param result ml_result
	 * @return
	 */
	public MlResult add(MlResult result) throws Exception {
		String prediction = ResultHelper.getPrediction(result);
		int termIdx = termIndexer.getIndex(result.getYmd());
		
		// bettype, prediction別
		String key = statHelper.createKey(result.getStatBettype(), prediction);
		if (!mapTotalBetCnt.containsKey(key)) {
			mapTotalBetCnt.addItem(key, 0.0);
		}
		
		List<Double> listBetcnt = mapTotalBetCnt.get(key);
		if (listBetcnt.size() < termIdx + 1) {
			listBetcnt.add(0.0);
		}
		
		listBetcnt.set(termIdx, listBetcnt.get(termIdx)+1); // counting
		
		
		// bettype, prediction, pattern別
		key = statHelper.createKey(result.getStatBettype(), prediction, result.getPattern());
		if (!mapListStat.containsKey(key)) {
			mapListStat.addItem(key, statHelper.createResultStat(result.getStatBettype(), prediction, result.getPatternid(), result.getPattern()));
		}
		
		List<ResultStat> listStat = mapListStat.get(key);
		if (listStat.size() < termIdx + 1) {
			listStat.add(statHelper.createResultStat(result.getStatBettype(), prediction, result.getPatternid(), result.getPattern()));
		}
		
		ResultStat stat = mapListStat.get(key).get(termIdx);
		stat.add(result, listBetcnt.get(termIdx));
		
		return result;
	}

	/**
	 * 累積統計にml_resultデータを追加して、統計データを設定して返却する
	 * @param listResult ml_result list
	 * @return
	 */
	public List<MlResult> addAll(List<MlResult> listResult) throws Exception {
		for (MlResult mlResult : listResult) {
			mlResult = add(mlResult);
		}
		
		return listResult;
	}
}

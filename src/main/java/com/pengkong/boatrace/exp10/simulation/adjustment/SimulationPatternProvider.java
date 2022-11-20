package com.pengkong.boatrace.exp10.simulation.adjustment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.result.ResultHelper;
import com.pengkong.boatrace.exp10.simulation.evaluation.EvaluationSet;
import com.pengkong.boatrace.mybatis.entity.MlResult;

/**
 * 投票結果に対してsimulation特別のパタンを設定するクラス.
 * simulationで提供するパタン毎に統計結果を分けたい時にはここに当該処理を追加する。
 * @author ttolt
 *
 */
public class SimulationPatternProvider {
	Map<String, BiFunction<List<MlResult>, List<String>, List<MlResult>>> mapMethod;

	/** count or literal.  countはliteralのカウント */ 
	String patternType;
	
	/** ex) mod+ptid+ptn   mod->modelno  ptid->patternid  ptn->pattern */
	String[] patternLiterals;
	
	void ensureInitialized() {
		if (patternType == null) {
			// function 定義
			mapMethod = new HashMap<>();
			mapMethod.put("count", this::applyCount);
			mapMethod.put("literal", this::applyLiteral);
			
			// count or literal
			patternType = MLPropertyUtil.getInstance().getString("pattern_type");

			String strFields = MLPropertyUtil.getInstance().getString("pattern_fields");
			strFields = strFields.replaceAll("mod", "modelno");
			strFields = strFields.replaceAll("ptid", "patternid");
			strFields = strFields.replaceAll("ptn", "pattern");
			
			patternLiterals = strFields.split(Delimeter.PLUS.getValue());
		}
	}
	
	public List<MlResult> apply(List<MlResult> listResult, EvaluationSet evSet) throws Exception {
		ensureInitialized();

		List<String> idList = evSet.getUniqueList(patternLiterals);
		return mapMethod.get(patternType).apply(listResult, idList);
	}

	/**
	 *  交差パタンID数で統計する
	 * @param listSrc 投票結果
	 * @param patternCount 交差パタン数
	 */
	List<MlResult> applyCount(List<MlResult> listSrc, List<String> literals) {
		List<MlResult> listResult = new ArrayList<>();
		MlResult copy;
		
		int count = literals.size();
		// 交差パタン数=1に割り当てる = パタンが1個でもある全て(nopatternと同等の意味）
		for (MlResult result :  listSrc) {
			copy = ResultHelper.copyOf(result);
			
			copy.setPattern("!all");
			listResult.add(copy);
		}

		// #2022/7/30 simul_1확인시 시간절약을 위해 !all그래프 한개만 출력하게한다
		if (MLPropertyUtil.getInstance().getString("graph_only_count_all").equals("yes")) {
			return listResult;
		}
		
		// 交差パタン数=patternCount割り当てて追加する
		for (MlResult result :  listSrc) {
			copy = ResultHelper.copyOf(result);

			copy.setPattern(String.valueOf(count));
			listResult.add(copy);
		}
		
		return listResult;
	}

	/**
	 * 交差パタンのパタン内容で統計する
	 * @param listSrc 投票結果
	 * @param literals パタン内容リスト
	 * @return
	 */
	List<MlResult> applyLiteral(List<MlResult> listSrc, List<String> literals) {
		List<MlResult> listResult = new ArrayList<>();
		MlResult copy;
		// 交差パタン数=1に割り当てる = パタンが1個でもある全て(nopatternと同等の意味）
		for (MlResult result :  listSrc) {
			copy = ResultHelper.copyOf(result);
			
			copy.setPattern("!all");
			listResult.add(copy);
		}
		
		// 当たっているパタンそれぞれに全て集計する
		for (String pattern : literals) {
			for (MlResult result :  listSrc) {
				copy = ResultHelper.copyOf(result);
				
				copy.setPattern(pattern);
				listResult.add(copy);
			}
		}

		return listResult;
	}
}

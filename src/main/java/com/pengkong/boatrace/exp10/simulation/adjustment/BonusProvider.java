package com.pengkong.boatrace.exp10.simulation.adjustment;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.pengkong.boatrace.exp10.enums.RangeValidationType;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.mybatis.entity.MlResult;

public class BonusProvider {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	private String PR = "bonus_pr"; // probability bonus 
	private String BOR = "bonus_bor"; // before odds bonus
	private String BORK = "bonus_bork"; // before odds rank bonus
	
	/** key= PR, BOR, BORK */
	Map<String, AbstractBonusProvider> mapProvider;
	
	public BonusProvider(String bonusPr, String bonusBor, String bonusBork) {
	    this.PR = bonusPr;
        this.BOR = bonusBor;
        this.BORK = bonusBork;
	    
		mapProvider = new HashedMap<>();
		// 予想確率
		mapProvider.put(PR, new PrBonusProvider());
		// 直前オッズ
		mapProvider.put(BOR, new BorBonusProvider());
		// 直前オッズRANK
		mapProvider.put(BORK, new BorkBonusProvider());
	}
	
	public List<MlResult> apply(List<MlResult> list,  Evaluation evaluation) {
		// 予想確率bonusを適用する
		list = applyEach(PR, list, evaluation);
		
		// 直前オッズbonusを適用する
		list = applyEach(BOR, list, evaluation);
		
		// 直前オッズRANK bonusを適用する
		list = applyEach(BORK, list, evaluation);
		
		return list;
	}
	
	/** 
	 * 指定キーのbonus適用文字列を取得して、対象であれば適用する.
	 * bonus適用文字列の例) 0.5~3=1,1.1~2=2   "x"の場合適用しない
	 * @param factorStringKey ex) "bonus_pr" or "bonus_bor" or "bonus_bork" or "bonus_borkbor"
	 * @return
	 */
	List<MlResult> applyEach(String factorStringKey, List<MlResult> list, Evaluation evaluation) {
		// ex) 0.5~3=1
		String factorString = getBonusFactorString(factorStringKey, evaluation);
		if (!factorString.equals(RangeValidationType.NONE.getValue())) {
			return mapProvider.get(factorStringKey).apply(factorString, list);
		}
		
		return list;
	}

	/**
	 * bonus factor range stringを取得する
	 * @param key bonus適用対象のキー ex) bonus_pr or bonus_bor
	 * @param eval Evaluation (EvaluationLoaderdで各Evaluationに値を設定する
	 * @return ex) 1.0~4.3=1
	 */
	String getBonusFactorString(String key, Evaluation eval) {
		String factor;
		
		factor = eval.get(key);
		
		return (factor == null) ? RangeValidationType.NONE.getValue() : factor; 
	}
}

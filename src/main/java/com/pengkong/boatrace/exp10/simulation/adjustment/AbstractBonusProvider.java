package com.pengkong.boatrace.exp10.simulation.adjustment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.result.ResultHelper;
import com.pengkong.boatrace.mybatis.entity.MlResult;

/**
 * 直前オッズ、予想確率でbet金額を調整するクラス
 * 調整係数は設定から取得する。
 * @author ttolt
 *
 */
public abstract class AbstractBonusProvider {

	/** key= range  value=factor ex) key=1.1~1.6 value:1*/
	Map<Double[], Double> mapBonusFactor;
	
	/** true=設定が有効 */
	boolean isAvailable = true;
	
	String factorString = null;
	
	void initialize() {
		mapBonusFactor = new HashMap<>();
		if (factorString.equals("x")) {
			isAvailable = false;
			return;
		}

		// [1~2=0,2.1~4=2,4.1~6=3]  
		String[] factors = factorString.split(Delimeter.COMMA.getValue());
		for (String factor : factors) {
			String[] token = factor.split(Delimeter.EQUAL.getValue());
			String[] rangeToken = token[0].split(Delimeter.WAVE.getValue());
			Double[] range = new Double[2];
			range[0] = Double.valueOf(rangeToken[0]);
			range[1] = Double.valueOf(rangeToken[1]);
			mapBonusFactor.put(range, Double.valueOf(token[1]));
		}
	}
	
	void ensureInitialized(String factorString) {
		if (this.factorString == null || !this.factorString.equals(factorString)) {
			this.factorString = factorString;
			initialize();
		}
	}
	
	/**
	 * 交差パタン数に沿ってbettign金額にfactorを掛け算する。
	 * @param listResult 投票結果
	 * @param patternCount 交差パタン数
	 * @return
	 * @throws Exception
	 */
	public List<MlResult> apply(String factorString, List<MlResult> listResult) {
		ensureInitialized(factorString);

		List<MlResult> applied = new ArrayList<>();
		
		if (!isAvailable) {
			return listResult;
		}

		for (MlResult result : listResult) {
			Double factorValue = getFactorValue(result);
			if (factorValue == null) {
				continue;
			}
			
			Double factor = null;
			for (Double[] minmax : mapBonusFactor.keySet()) {
				// 最適範囲内の場合
				if (factorValue >= minmax[0] && factorValue <= minmax[1]) {
					factor = mapBonusFactor.get(minmax);
					break;
				}
			}
			
			// 未定義の場合skipする
			if (factor == null) {
				continue;
			}

			result.setBetamt((int)((double)result.getBetamt() * factor));
			
			result = ResultHelper.calculateIncome(result);
			applied.add(result);
		}
		
		return applied;
	}
	
	abstract Double getFactorValue(MlResult result) throws IllegalAccessError;
}

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
public abstract class AbstractDoubleRangeBonusProvider extends AbstractBonusProvider {

	/** key= double range  value=factor ex) key=1~2_5.5~8.3 value:1*/
	Map<Double[], Double> mapBonusFactor;
	
	/** true=設定が有効 */
	boolean isAvailable = true;
	
	String factorString = null;
	
	@Override
	void initialize() {
		mapBonusFactor = new HashMap<>();
		if (factorString.equals("x")) {
			isAvailable = false;
			return;
		}

		// [1~2_5~8=1,3~4_10~15=2]  
		String[] factors = factorString.split(Delimeter.COMMA.getValue());
		for (String factor : factors) {
			String[] token1;
			String[] token2;
			String[] token3;
			
			// factor = 1~2_5~8=1
			Double[] range = new Double[4];
			
			// token = [1~2_5~8, 1]
			String[] token = factor.split(Delimeter.EQUAL.getValue());
			
			// token1 = [1~2, 5~8]
			token1 = token[0].split(Delimeter.UNDERBAR.getValue());

			// token2 = [1, 2]
			token2 = token1[0].split(Delimeter.WAVE.getValue());
			// token3 = [5, 8]
			token3 = token1[1].split(Delimeter.WAVE.getValue());
			
			// range = [1, 2, 5, 8]
			range[0] = Double.valueOf(token2[0]);
			range[1] = Double.valueOf(token2[1]);
			range[2] = Double.valueOf(token3[0]);
			range[3] = Double.valueOf(token3[1]);
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
	@Override
	public List<MlResult> apply(String factorString, List<MlResult> listResult) {
		ensureInitialized(factorString);

		List<MlResult> applied = new ArrayList<>();
		
		if (!isAvailable) {
			return listResult;
		}

		for (MlResult result : listResult) {
			Double[] factorValues = getFactorValues(result);
			if (factorValues == null) {
				continue;
			}
			
			Double factor = null;
			for (Double[] doubleRange : mapBonusFactor.keySet()) {
				// 最適範囲内の場合
				if ( (factorValues[0] >= doubleRange[0] && factorValues[0] <= doubleRange[1]) &&
						(factorValues[1] >= doubleRange[2] && factorValues[1] <= doubleRange[3])
						) {
					factor = mapBonusFactor.get(doubleRange);
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
	
	abstract Double[] getFactorValues(MlResult result);
}

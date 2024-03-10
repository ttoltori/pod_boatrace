package com.pengkong.boatrace.exp10.simulation.adjustment.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.result.ResultHelper;
import com.pengkong.boatrace.exp10.simulation.evaluation.Evaluation;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * 直前オッズ、予想確率でbet金額を調整するクラス
 * 調整係数は設定から取得する。
 * @author ttolt
 *
 */
public abstract class AbstractBonusValidator {

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
	public boolean apply(String factorString, DBRecord rec) {
		ensureInitialized(factorString);

		if (!isAvailable) {
			return true;
		}

		Double factorValue = getFactorValue(rec);
		if (factorValue == null) {
			return false;
		}
			
		for (Double[] minmax : mapBonusFactor.keySet()) {
			// 最適範囲内の場合
			if (factorValue >= minmax[0] && factorValue <= minmax[1]) {
				return true;
			}
		}

		return false;
	}
	
	abstract Double getFactorValue(DBRecord rec) throws IllegalAccessError;
}

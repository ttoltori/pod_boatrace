package com.pengkong.boatrace.exp10.simulation.adjustment;

import com.pengkong.boatrace.mybatis.entity.MlResult;

/**
 * 直前オッズRank + 直前オッズでbet金額を調整するクラス
 * 調整係数は設定から取得する。
 * @author ttolt
 *
 */
public class BorkBorBonusProvider extends AbstractDoubleRangeBonusProvider {

	@Override
	Double[] getFactorValues(MlResult result) {
		if (result.getBetOddsrank() == null || result.getBetOdds() == null) {
			return null;
		}
		
		Double[] values = new Double[2];
		values[0] = Double.valueOf(result.getBetOddsrank());
		values[1] = result.getBetOdds();
		
		return values;
	}

	@Override
	Double getFactorValue(MlResult result) throws IllegalAccessError {
		throw new IllegalAccessError("not allowed method call");
	}
}

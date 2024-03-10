package com.pengkong.boatrace.exp10.simulation.adjustment;

import com.pengkong.boatrace.mybatis.entity.MlResult;

/**
 * 直前オッズRANKでbet金額を調整するクラス
 * 調整係数は設定から取得する。
 * @author ttolt
 *
 */
public class BorkBonusProvider extends AbstractBonusProvider {
	@Override
	Double getFactorValue(MlResult result) {
		if (result.getBetOddsrank() == null) {
			return null;
		}
		
		return result.getBetOddsrank().doubleValue();
	}
}

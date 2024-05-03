package com.pengkong.boatrace.exp10.simulation.adjustment;

import com.pengkong.boatrace.mybatis.entity.MlResult;

/**
 * 直前オッズrank期待値でbet金額を調整するクラス
 * 調整係数は設定から取得する。
 * @author ttolt
 *
 */
public class EBorkBonusProvider extends AbstractBonusProvider {
	@Override
	Double getFactorValue(MlResult result) {
		return result.getExpectBork();
	}
}

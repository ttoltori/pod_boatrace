package com.pengkong.boatrace.exp10.simulation.adjustment;

import com.pengkong.boatrace.mybatis.entity.MlResult;

/**
 * 予想確率でbet金額を調整するクラス
 * 調整係数は設定から取得する。
 * @author ttolt
 *
 */
public class PrBonusProvider extends AbstractBonusProvider {
	@Override
	Double getFactorValue(MlResult result) {
		return result.getProbability();
	}
}

package com.pengkong.boatrace.exp10.simulation.adjustment;

import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.MlResult;
import com.pengkong.common.collection.HashMapList;

@Deprecated
public class BonusMergeProvider {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	public List<MlResult> apply(List<MlResult> listProb, List<MlResult> listOdds) throws IllegalStateException {
		List<MlResult> applied = new ArrayList<>();
		
		String mergeType = prop.getString("merge_priority");
		
		List<MlResult> listConcat = new ArrayList<>();
		// 順番注意: probability -> odds
		listConcat.addAll(listProb);
		listConcat.addAll(listOdds);
		
		String key;
		HashMapList<MlResult> map = new HashMapList<>();
		for (MlResult result : listConcat) {
			// bettype, kumibanで集合すると必ず最大２個のMlResultが入るはず（listProbから１個、listOddsから１個）
			key = String.join("_", result.getBettype(), result.getBetKumiban());
			map.addItem(key, result);
		}

		for (List<MlResult> results : map.values()) {
			if (results.size() > 2) {
				throw new IllegalStateException("想定外のresults size -> " + results.size());
			}

			//両方存在してprobabilityを優先
			if (mergeType.equals(BonusMergeType.PROBABILITY.getValue())  && results.size() >= 2) {
				applied.add(results.get(0));
			} else if (mergeType.equals(BonusMergeType.ODDS.getValue())  && results.size() >= 2) {
				applied.add(results.get(1));
			} else if (mergeType.equals(BonusMergeType.OR.getValue())) {
				applied.add(results.get(0));
			}
		}
		
		return applied;
	}
	
	public enum BonusMergeType {
		ODDS("odds"),
		PROBABILITY("prob"),
		OR("x");

		private final String bonusMergeType;
		
		private BonusMergeType(String bonusMergeType) {
			this.bonusMergeType = bonusMergeType;
		}
		
		public String getValue() {
			return this.bonusMergeType;
		}
	}
}

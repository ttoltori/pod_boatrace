package com.pengkong.boatrace.exp02;

import java.util.HashMap;
import java.util.Map.Entry;

public class RankBoatClassifier {

	public HashMap<String, BoatClassifier> mapCls;

	public RankBoatClassifier() {
		mapCls = new HashMap<>();
	}

	/**
	 * 착순 1,2,3을 반환한다. ex) "213"
	 * 
	 * @param hashmap
	 *            예측대상 데이터
	 * @return 착순1,2,3
	 * @throws Exception
	 */
	public String predictFromHashmap(HashMap<String, ?> hashmap) throws Exception {
		StringBuilder sb = new StringBuilder();
		// 랭크1
		try {
			sb.append(mapCls.get("rank1").predictFromHashmap(hashmap));
			// 랭크2
			sb.append(mapCls.get("rank2").predictFromHashmap(hashmap));
			// 랭크3
			sb.append(mapCls.get("rank3").predictFromHashmap(hashmap));
			// 랭크4
			if (mapCls.containsKey("rank4")) {
				sb.append(mapCls.get("rank4").predictFromHashmap(hashmap));
			}
		} catch (Exception e) {
			return "xxx";
		}

		return sb.toString();
	}

	public static RankBoatClassifier create(RankBoatClassifierInfo rcInfo) throws Exception {
		RankBoatClassifier rCls = new RankBoatClassifier();

		for (Entry<String, BoatClassifierInfo> entry : rcInfo.mapClsInfo.entrySet()) {
			// 랭크1,2,3별 모델생성
			BoatClassifierInfo cInfo = entry.getValue();
			if (cInfo.classifier != null) {
				throw new IllegalStateException("classifier is exist. BoatClassifierInfo=" + cInfo);
			}
			cInfo.classifier = BoatClassifier.create(entry.getValue());
			rCls.mapCls.put(entry.getKey(), cInfo.classifier);
		}

		return rCls;
	}
}

package com.pengkong.boatrace.exp10.util;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.pengkong.boatrace.converter.MlExpectedRec;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

public class ResultExpHelper {
    /** 리스트에 담긴 MlExpectedRec들을  bexp값이 큰 순으로 소트하고 
     * 그 랭킹을 bexprank에 설정한다.
    */
    public static void setBexpRank(List<MlExpectedRec> list) {
        // bexp 값이 큰 순으로 정렬 (null 값은 마지막으로)
        list.sort(Comparator.comparing(
            (MlExpectedRec rec) -> rec.bexp != null ? rec.bexp : Double.NEGATIVE_INFINITY,
            Comparator.reverseOrder()
        ));
        
        // 랭킹 설정
        for (int i = 0; i < list.size(); i++) {
            list.get(i).bexprank = i + 1;
        }
    }

    /**
     * 期待値と確率に基づいてフィルタリングし、最適な組番を選択する
     * 
     * プロパティ:
     * - exp_3T_min: 期待値下限 (default: 1.0)
     * - exp_3T_max: 期待値上限 (default: 3.0)
     * - prob_3T_min: 確率下限 (default: 0.01)
     * - cnt_3T: 最大賭け数 (default: 5)
     * 
     * @param list bexp降順でソート済みのリスト
     * @return フィルタリング後のリスト（スコア順）
     */
    public static List<MlExpectedRec> filterByStrategy(List<MlExpectedRec> list) {
        MLPropertyUtil prop = MLPropertyUtil.getInstance();
        
        // プロパティから設定値を取得（デフォルト値付き）
        double minExp = getDoubleOrDefault(prop, "exp_3T_min", 1.0);
        double maxExp = getDoubleOrDefault(prop, "exp_3T_max", 3.0);
        double minProb = getDoubleOrDefault(prop, "prob_3T_min", 0.01);
        int maxBets = getIntOrDefault(prop, "cnt_3T", 5);
        
        return list.stream()
            // 期待値の範囲でフィルタリング
            .filter(r -> r.bexp != null && r.bexp >= minExp && r.bexp <= maxExp)
            // 確率の下限でフィルタリング
            .filter(r -> r.bprob != null && r.bprob >= minProb)
            // スコア（期待値×確率）の降順でソート
            .sorted(Comparator.comparing(
                (MlExpectedRec r) -> r.bexp * r.bprob,
                Comparator.reverseOrder()
            ))
            // 最大賭け数で制限
            .limit(maxBets)
            .collect(Collectors.toList());
    }

    private static double getDoubleOrDefault(MLPropertyUtil prop, String key, double defaultValue) {
        String value = prop.getString(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static int getIntOrDefault(MLPropertyUtil prop, String key, int defaultValue) {
        String value = prop.getString(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

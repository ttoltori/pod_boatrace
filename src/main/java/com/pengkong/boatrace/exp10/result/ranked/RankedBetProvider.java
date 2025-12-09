package com.pengkong.boatrace.exp10.result.ranked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.provider.BeforeOddsProvider;
import com.pengkong.boatrace.mybatis.entity.MlClassification;

/**
 * MlClassificationのランキングモデル予測結果から、各勝式の組番ごとの期待値を計算し、
 * 期待値の大きい順に保持するプロバイダー
 */
public class RankedBetProvider {
    
    /** key = ymd_jyoCd_raceNo_betType, value = 期待値降順ソート済みRankedBetリスト */
    private Map<String, List<RankedBet>> mapRankedBets = new HashMap<>();
    
    /** 直前オッズプロバイダー */
    private BeforeOddsProvider oddsProvider;
    
    public RankedBetProvider() {
        this.oddsProvider = new BeforeOddsProvider();
    }
    
    /**
     * MlClassificationリストを処理して内部マップに格納する
     * @param classifications MlClassificationリスト
     */
    public void load(List<MlClassification> classifications) throws Exception {
        for (MlClassification cls : classifications) {
            processClassification(cls);
        }
    }
    
    /**
     * 単一のMlClassificationを処理
     */
    private void processClassification(MlClassification cls) throws Exception {
        String ymd = cls.getYmd();
        String jyoCd = cls.getJyocd();
        String raceNo = String.valueOf(cls.getRaceno());
        
        // 各枠の強さスコアマップを作成 (枠番 -> 強さw)
        Map<String, Double> strengthMap = createStrengthMap(cls);
        
        // 各勝式について処理
        String[] betTypes = {"1T", "2T", "2F", "3T", "3F"};
        for (String betType : betTypes) {
            List<RankedBet> bets = calculateRankedBets(ymd, jyoCd, raceNo, betType, strengthMap);
            String key = createKey(ymd, jyoCd, raceNo, betType);
            mapRankedBets.put(key, bets);
        }
    }
    
    /**
     * MlClassificationから枠番→強さスコア(w)のマップを作成
     * prediction1~6は着順1~6の枠番、probability1~6はその強さスコア
     * 例: prediction1="6", probability1=0.6118 → 枠6の強さw6=0.6118
     */
    private Map<String, Double> createStrengthMap(MlClassification cls) {
        Map<String, Double> strengthMap = new HashMap<>();
        
        // prediction1~6は着順1~6の枠番、probability1~6はその強さスコア(w)
        // 枠番をキーにして強さスコアをマッピング
        strengthMap.put(cls.getPrediction1(), cls.getProbability1());
        strengthMap.put(cls.getPrediction2(), cls.getProbability2());
        strengthMap.put(cls.getPrediction3(), cls.getProbability3());
        strengthMap.put(cls.getPrediction4(), cls.getProbability4());
        strengthMap.put(cls.getPrediction5(), cls.getProbability5());
        strengthMap.put(cls.getPrediction6(), cls.getProbability6());
        
        return strengthMap;
    }
    
    /**
     * 指定勝式の全組番について的中確率と期待値を計算
     * Plackett-Luceモデルを使用
     */
    private List<RankedBet> calculateRankedBets(String ymd, String jyoCd, String raceNo, 
            String betType, Map<String, Double> strengthMap) throws Exception {
        
        List<RankedBet> bets = new ArrayList<>();
        List<String> kumibans = generateKumibans(betType);
        
        for (String kumiban : kumibans) {
            Double prob = calculateProbabilityPL(kumiban, betType, strengthMap);
            if (prob == null || prob <= 0) {
                continue;
            }
            
            // オッズ取得
            Odds odds = oddsProvider.get(ymd, jyoCd, raceNo, betType, kumiban);
            if (odds == null || odds.value == null || odds.value <= 0) {
                continue;
            }
            
            RankedBet bet = new RankedBet();
            bet.kumiban = kumiban;
            bet.bprob = prob;
            bet.bor = odds.value;
            bet.bexp = prob * odds.value;
            bets.add(bet);
        }
        
        // 期待値降順でソート
        Collections.sort(bets, Comparator.comparingDouble((RankedBet b) -> b.bexp).reversed());
        
        return bets;
    }
    
    /**
     * 勝式に応じた全組番を生成
     */
    private List<String> generateKumibans(String betType) {
        List<String> kumibans = new ArrayList<>();
        String[] waku = {"1", "2", "3", "4", "5", "6"};
        
        switch (betType) {
            case "1T":
                // 単勝: 6通り
                for (String w : waku) {
                    kumibans.add(w);
                }
                break;
                
            case "2T":
                // 2連単: 30通り (順序あり、重複なし)
                for (int i = 0; i < 6; i++) {
                    for (int j = 0; j < 6; j++) {
                        if (i != j) {
                            kumibans.add(waku[i] + waku[j]);
                        }
                    }
                }
                break;
                
            case "2F":
                // 2連複: 15通り (順序なし、重複なし)
                for (int i = 0; i < 6; i++) {
                    for (int j = i + 1; j < 6; j++) {
                        kumibans.add(waku[i] + waku[j]);
                    }
                }
                break;
                
            case "3T":
                // 3連単: 120通り (順序あり、重複なし)
                for (int i = 0; i < 6; i++) {
                    for (int j = 0; j < 6; j++) {
                        for (int k = 0; k < 6; k++) {
                            if (i != j && j != k && i != k) {
                                kumibans.add(waku[i] + waku[j] + waku[k]);
                            }
                        }
                    }
                }
                break;
                
            case "3F":
                // 3連複: 20通り (順序なし、重複なし)
                for (int i = 0; i < 6; i++) {
                    for (int j = i + 1; j < 6; j++) {
                        for (int k = j + 1; k < 6; k++) {
                            kumibans.add(waku[i] + waku[j] + waku[k]);
                        }
                    }
                }
                break;
        }
        
        return kumibans;
    }
    
    /**
     * Plackett-Luceモデルで的中確率を計算
     * 
     * P(1着=i) = w_i / Σw_all
     * P(2着=k|1着=i) = w_k / Σw_{j≠i}
     * P(3着=m|1着=i,2着=k) = w_m / Σw_{j≠i,k}
     * 
     * @param kumiban 組番 (例: "123", "12")
     * @param betType 勝式
     * @param strengthMap 枠番→強さスコア(w)マップ
     * @return 的中確率
     */
    private Double calculateProbabilityPL(String kumiban, String betType, Map<String, Double> strengthMap) {
        String[] digits = kumiban.split("");
        
        // 全艇の強さスコア合計
        double sumAll = strengthMap.values().stream().mapToDouble(Double::doubleValue).sum();
        
        switch (betType) {
            case "1T": {
                // 単勝: P(1着=i) = w_i / Σw_all
                double w1 = strengthMap.get(digits[0]);
                return w1 / sumAll;
            }
                
            case "2T": {
                // 2連単: P(1着=i) × P(2着=k|1着=i)
                double w1 = strengthMap.get(digits[0]);
                double w2 = strengthMap.get(digits[1]);
                double sumExclude1 = sumAll - w1;
                return (w1 / sumAll) * (w2 / sumExclude1);
            }
                
            case "2F": {
                // 2連複: P(i-k) + P(k-i)
                double w1 = strengthMap.get(digits[0]);
                double w2 = strengthMap.get(digits[1]);
                double sumExclude1 = sumAll - w1;
                double sumExclude2 = sumAll - w2;
                double prob_1_2 = (w1 / sumAll) * (w2 / sumExclude1);
                double prob_2_1 = (w2 / sumAll) * (w1 / sumExclude2);
                return prob_1_2 + prob_2_1;
            }
                
            case "3T": {
                // 3連単: P(1着=i) × P(2着=k|1着=i) × P(3着=m|1着=i,2着=k)
                double w1 = strengthMap.get(digits[0]);
                double w2 = strengthMap.get(digits[1]);
                double w3 = strengthMap.get(digits[2]);
                double sumExclude1 = sumAll - w1;
                double sumExclude12 = sumAll - w1 - w2;
                return (w1 / sumAll) * (w2 / sumExclude1) * (w3 / sumExclude12);
            }
                
            case "3F": {
                // 3連複: 3つの枠が1-2-3着に入る全6順列の確率の合計
                double w1 = strengthMap.get(digits[0]);
                double w2 = strengthMap.get(digits[1]);
                double w3 = strengthMap.get(digits[2]);
                
                // 6通りの順列をすべて計算
                double prob = 0.0;
                double[][] perms = {{w1, w2, w3}, {w1, w3, w2}, {w2, w1, w3}, 
                                    {w2, w3, w1}, {w3, w1, w2}, {w3, w2, w1}};
                for (double[] perm : perms) {
                    double sumEx1 = sumAll - perm[0];
                    double sumEx12 = sumAll - perm[0] - perm[1];
                    prob += (perm[0] / sumAll) * (perm[1] / sumEx1) * (perm[2] / sumEx12);
                }
                return prob;
            }
                
            default:
                return null;
        }
    }
    
    /**
     * キー生成
     */
    private String createKey(String ymd, String jyoCd, String raceNo, String betType) {
        return String.join("_", ymd, jyoCd, raceNo, betType);
    }
    
    /**
     * 指定年月日、場、レース、勝式のすべてのベッティング情報をcount分取得する
     * @param ymd 年月日
     * @param jyoCd 場コード
     * @param raceNo レース番号
     * @param betType 勝式
     * @param count 取得件数
     * @return 期待値降順のRankedBetリスト
     */
    public List<RankedBet> getAllBets(String ymd, String jyoCd, String raceNo, String betType, int count) {
        String key = createKey(ymd, jyoCd, raceNo, betType);
        List<RankedBet> bets = mapRankedBets.get(key);
        
        if (bets == null || bets.isEmpty()) {
            return new ArrayList<>();
        }
        
        int limit = Math.min(count, bets.size());
        return new ArrayList<>(bets.subList(0, limit));
    }

    /**
     * 指定年月日、場、レース、勝式の期待値を指定範囲内のベッティング情報をcount分取得する
     * @param ymd 年月日
     * @param jyoCd 場コード
     * @param raceNo レース番号
     * @param betType 勝式
     * @param minBexp 最小期待値
     * @param maxBexp 最大期待値
     * @param count 取得件数
     * @return 期待値降順のRankedBetリスト
     */
    public List<RankedBet> getBetsByBexp(String ymd, String jyoCd, String raceNo, String betType, 
            Double minBexp, Double maxBexp, int count) {
        String key = createKey(ymd, jyoCd, raceNo, betType);
        List<RankedBet> bets = mapRankedBets.get(key);
        
        if (bets == null || bets.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<RankedBet> filtered = new ArrayList<>();
        for (RankedBet bet : bets) {
            if (bet.bexp >= minBexp && bet.bexp <= maxBexp) {
                filtered.add(bet);
                if (filtered.size() >= count) {
                    break;
                }
            }
        }
        
        return filtered;
    }
    
    /**
     * 内部マップをクリア
     */
    public void clear() {
        mapRankedBets.clear();
    }
}

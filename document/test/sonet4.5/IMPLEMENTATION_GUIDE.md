# 投票戦略実装ガイド

## 概要

分析結果に基づいた最適な投票戦略のJava実装方法。

---

## 推奨戦略

### 戦略S（最高ROI）

```
条件:
- expect_bor: 1.3 ≤ x ≤ 1.5
- bet_oddsrank: ≤ 3
- probability: ≥ 0.05

実績: ROI +32.7%, 169ベット/年, 利益 ¥5,531
```

### 戦略A（バランス型）

```
条件:
- expect_bor: 1.3 ≤ x ≤ 1.5  
- bet_oddsrank: ≤ 5
- probability: ≥ 0.05

実績: ROI +19.2%, 433ベット/年, 利益 ¥8,316
```

### 戦略A+（グレードフィルター追加）

```
条件:
- 戦略Aの条件
- grade: G1 または G3

実績: ROI +104.2%, 49ベット/年, 利益 ¥5,107
```

---

## 実装コード

### 方法1: getBetsByConditionの修正

既存の`MlExpectedRecProvider.getBetsByCondition`に期待値下限パラメータを追加:

```java
// MlExpectedRecProvider.java
public List<MlExpectedRec> getBetsByCondition(
    DBRecord rec, 
    String betType,
    Double maxBexp,      // 期待値上限: 1.5
    Double minBprob,     // 確率下限: 0.05
    Integer maxBork,     // オッズ人気度上限: 5
    int count            // 取得件数: 1
)
```

### ステップ2: フィルタークラスの作成

新しいフィルタークラスを作成して戦略を実装します。

```java
package com.pengkong.boatrace.exp10.result.ranked.filter;

import com.pengkong.boatrace.converter.MlExpectedRec;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 戦略A+フィルター
 * 期待値1.3-1.5、オッズ人気度5位以内、確率5%以上、G1/G3のみ
 */
public class StrategyAPlusFilter {
    
    // 期待値範囲
    private static final double MIN_EXPECT = 1.3;
    private static final double MAX_EXPECT = 1.5;
    
    // 確率下限
    private static final double MIN_PROBABILITY = 0.05;
    
    // オッズ人気度上限
    private static final int MAX_ODDS_RANK = 5;
    
    // 対象グレード
    private static final List<String> TARGET_GRADES = Arrays.asList("G1", "G3");
    
    // オプション: 優良場のみに限定する場合
    private static final List<Integer> TOP_VENUES = Arrays.asList(6, 10, 13, 16, 17, 20);
    private static final boolean USE_VENUE_FILTER = false; // 場フィルターを使用するか
    
    /**
     * レースが戦略条件を満たすかチェック
     */
    public boolean isValidRace(DBRecord rec) {
        // グレードチェック
        String grade = rec.getString("grade");
        if (!TARGET_GRADES.contains(grade)) {
            return false;
        }
        
        // オプション: 場チェック
        if (USE_VENUE_FILTER) {
            int jyocd = rec.getInt("jyocd");
            if (!TOP_VENUES.contains(jyocd)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * ベット候補が戦略条件を満たすかチェック
     */
    public boolean isValidBet(MlExpectedRec bet) {
        // 期待値チェック
        if (bet.bexp == null || bet.bexp < MIN_EXPECT || bet.bexp > MAX_EXPECT) {
            return false;
        }
        
        // 確率チェック
        if (bet.bprob == null || bet.bprob < MIN_PROBABILITY) {
            return false;
        }
        
        // オッズ人気度チェック
        if (bet.bork == null || bet.bork > MAX_ODDS_RANK) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 投票金額を決定（グレード別）
     */
    public int getBetAmount(DBRecord rec) {
        String grade = rec.getString("grade");
        
        switch (grade) {
            case "G1":
                return 200; // G1は200円
            case "G3":
                return 150; // G3は150円
            default:
                return 100; // その他は100円
        }
    }
}
```

### ステップ3: メインロジックでの使用

```java
package com.pengkong.boatrace.exp10.simulation;

import com.pengkong.boatrace.exp10.result.ranked.MlExpectedRecProvider;
import com.pengkong.boatrace.exp10.result.ranked.filter.StrategyAPlusFilter;
import com.pengkong.boatrace.converter.MlExpectedRec;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import java.util.List;

public class StrategySimulator {
    
    private MlExpectedRecProvider provider;
    private StrategyAPlusFilter filter;
    
    public StrategySimulator() {
        this.provider = new MlExpectedRecProvider();
        this.filter = new StrategyAPlusFilter();
    }
    
    /**
     * レースを処理して投票候補を取得
     */
    public List<MlExpectedRec> processRace(DBRecord rec) throws Exception {
        // レースが戦略条件を満たすかチェック
        if (!filter.isValidRace(rec)) {
            return new ArrayList<>();
        }
        
        // MlExpectedRecProviderで候補を取得
        List<MlExpectedRec> candidates = provider.getBetsByCondition(
            rec,
            "3T",    // 3連単
            1.5,     // maxBexp
            0.05,    // minBprob
            5,       // maxBork
            10       // count: 上位10件取得
        );
        
        // さらに期待値下限でフィルター
        List<MlExpectedRec> validBets = new ArrayList<>();
        for (MlExpectedRec bet : candidates) {
            if (filter.isValidBet(bet)) {
                validBets.add(bet);
            }
        }
        
        return validBets;
    }
    
    /**
     * 投票を実行
     */
    public void placeBets(DBRecord rec, List<MlExpectedRec> bets) {
        if (bets.isEmpty()) {
            return;
        }
        
        // 最高スコアのベットのみ投票（または複数投票も可能）
        MlExpectedRec topBet = bets.get(0);
        int betAmount = filter.getBetAmount(rec);
        
        // 投票処理
        System.out.println(String.format(
            "投票: %s-%s R%d 組番:%s 期待値:%.2f 確率:%.4f 金額:%d円",
            topBet.ymd, topBet.jyocd, topBet.raceno,
            topBet.bkumiban, topBet.bexp, topBet.bprob, betAmount
        ));
        
        // 実際の投票ロジックをここに実装
        // executeBet(topBet, betAmount);
    }
}
```

---

## 実装オプション

### オプション1: 保守的戦略（推奨初期）

```java
// StrategyAPlusFilter の設定
MIN_EXPECT = 1.3
MAX_EXPECT = 1.5
MIN_PROBABILITY = 0.05
MAX_ODDS_RANK = 5
TARGET_GRADES = ["G1", "G3"]
USE_VENUE_FILTER = false

// 投票金額
G1: 100円
G3: 100円
```

**期待結果:**
- 年間ベット数: 約50回
- 期待ROI: +100%以上

### オプション2: 積極的戦略

```java
// StrategyAPlusFilter の設定
MIN_EXPECT = 1.3
MAX_EXPECT = 1.5
MIN_PROBABILITY = 0.05
MAX_ODDS_RANK = 5
TARGET_GRADES = ["G1", "G3"]
USE_VENUE_FILTER = false

// 投票金額（グレード別）
G1: 200円
G3: 150円
```

**期待結果:**
- 年間ベット数: 約50回
- 期待ROI: +100%以上
- 年間投資額増加

### オプション3: 超保守的戦略

```java
// StrategyAPlusFilter の設定
MIN_EXPECT = 1.3
MAX_EXPECT = 1.5
MIN_PROBABILITY = 0.05
MAX_ODDS_RANK = 3  // 3位以内に限定
TARGET_GRADES = ["G1", "G3"]
USE_VENUE_FILTER = true  // 優良場のみ

// 投票金額
G1: 200円
G3: 150円
```

**期待結果:**
- 年間ベット数: 約10-20回（少ない）
- 期待ROI: 非常に高い
- リスク最小

---

## テスト実装

### バックテスト用コード

```java
package com.pengkong.boatrace.exp10.test;

import com.pengkong.boatrace.exp10.simulation.StrategySimulator;
import com.pengkong.boatrace.converter.MlExpectedRec;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import java.util.List;

public class StrategyBacktest {
    
    public static void main(String[] args) throws Exception {
        StrategySimulator simulator = new StrategySimulator();
        
        // テストデータの読み込み（実装に応じて調整）
        List<DBRecord> testRaces = loadTestData();
        
        int totalBets = 0;
        int totalHits = 0;
        int totalInvestment = 0;
        int totalReturn = 0;
        
        for (DBRecord rec : testRaces) {
            // レース処理
            List<MlExpectedRec> bets = simulator.processRace(rec);
            
            if (!bets.isEmpty()) {
                totalBets++;
                MlExpectedRec bet = bets.get(0);
                
                // 投票金額
                int betAmount = getBetAmount(rec);
                totalInvestment += betAmount;
                
                // 結果確認（実装に応じて調整）
                boolean isHit = checkResult(rec, bet);
                if (isHit) {
                    totalHits++;
                    totalReturn += (int)(betAmount * bet.ror);
                }
            }
        }
        
        // 結果表示
        System.out.println("=== Backtest Results ===");
        System.out.println("Total Bets: " + totalBets);
        System.out.println("Total Hits: " + totalHits);
        System.out.println("Hit Rate: " + (totalHits * 100.0 / totalBets) + "%");
        System.out.println("Total Investment: ¥" + totalInvestment);
        System.out.println("Total Return: ¥" + totalReturn);
        System.out.println("Profit: ¥" + (totalReturn - totalInvestment));
        System.out.println("ROI: " + ((totalReturn * 100.0 / totalInvestment) - 100) + "%");
    }
    
    private static List<DBRecord> loadTestData() {
        // データ読み込み実装
        return null;
    }
    
    private static int getBetAmount(DBRecord rec) {
        String grade = rec.getString("grade");
        return grade.equals("G1") ? 200 : 150;
    }
    
    private static boolean checkResult(DBRecord rec, MlExpectedRec bet) {
        // 結果確認実装
        return false;
    }
}
```

---

## モニタリングとメンテナンス

### 日次チェック項目

1. **投票実行数**: 1日あたり0-2回程度が正常
2. **的中状況**: 10回に1回程度の的中が期待値
3. **累積収支**: 徐々に増加が理想

### 週次チェック項目

1. **週間ROI**: プラスを維持しているか
2. **連敗数**: 30回を超えていないか
3. **条件別パフォーマンス**: グレード別、場別の傾向

### 月次チェック項目

1. **月間ROI**: 目標+50%以上
2. **ベット機会**: 月4-5回程度
3. **戦略の有効性**: 継続的に黒字か

### 警告サイン

以下の場合は戦略の見直しが必要:

- 3ヶ月連続でROI < 0%
- 連敗30回以上
- 月間ベット機会が0回（データ不足）
- 的中率が5%を大きく下回る

---

## トラブルシューティング

### Q1: ベット機会が全くない

**原因:**
- G1/G3レースが少ない時期
- 条件が厳しすぎる

**対策:**
- 戦略Aに戻す（グレードフィルターなし）
- 期待値範囲を1.3-1.6に拡大

### Q2: 連敗が続く

**原因:**
- 短期的な運の偏り
- 市場環境の変化

**対策:**
- 30回以内なら継続
- 30回超えたら一時停止して分析

### Q3: ROIが期待より低い

**原因:**
- オッズ変動
- モデル精度の低下

**対策:**
- モデルの再訓練
- 条件の微調整
- 他の組番への拡張検討

---

## 次のステップ

### フェーズ1: 小規模テスト（1-2ヶ月）

- 資金: ¥10,000
- 戦略: 戦略A+（保守的）
- 目標: 戦略の有効性確認

### フェーズ2: 本格運用（3-6ヶ月）

- 資金: ¥50,000
- 戦略: 戦略A+（積極的）
- 目標: 安定した月次黒字

### フェーズ3: 拡張（6ヶ月以降）

- 他の組番への展開
- モデルの改善
- 自動化の強化

---

## まとめ

1. **StrategyAPlusFilter**クラスを実装
2. **MlExpectedRecProvider**と統合
3. **小規模テスト**から開始
4. **定期的なモニタリング**を実施
5. **継続的な改善**を行う

この実装により、年間+100%以上のROIを目指すことができます。

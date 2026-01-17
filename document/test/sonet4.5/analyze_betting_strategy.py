import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from datetime import datetime
import warnings
import sys
import io
warnings.filterwarnings('ignore')

# UTF-8出力設定
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# 日本語フォント設定
plt.rcParams['font.sans-serif'] = ['MS Gothic', 'Yu Gothic', 'Meiryo']
plt.rcParams['axes.unicode_minus'] = False

# データ読み込み
print("=" * 80)
print("3-Rentan 123 Betting Strategy Analysis")
print("=" * 80)

df = pd.read_csv(r'c:\Dev\github\pod_boatrace\document\test\result_3T_123.tsv', sep='\t')
print(f"\n総レコード数: {len(df):,}")
print(f"データ期間: {df['ymd'].min()} - {df['ymd'].max()}")

# 基本統計
total_bet = df['betamt'].sum()
total_return = df['hitamt'].sum()
total_roi = (total_return / total_bet - 1) * 100
hit_count = (df['hitamt'] > 0).sum()
hit_rate = hit_count / len(df) * 100

print(f"\n【全体統計】")
print(f"総投票額: ¥{total_bet:,}")
print(f"総払戻額: ¥{total_return:,}")
print(f"収支: ¥{total_return - total_bet:,}")
print(f"回収率: {total_roi:.2f}%")
print(f"的中率: {hit_rate:.2f}% ({hit_count}/{len(df)})")

# 的中フラグ追加
df['is_hit'] = df['hitamt'] > 0
df['profit'] = df['hitamt'] - df['betamt']

print("\n" + "=" * 80)
print("1. 期待値(expect_bor)による分析")
print("=" * 80)

# 期待値の範囲別分析
expect_bins = [0, 1.0, 1.1, 1.2, 1.3, 1.5, 2.0, 100]
expect_labels = ['~1.0', '1.0-1.1', '1.1-1.2', '1.2-1.3', '1.3-1.5', '1.5-2.0', '2.0+']
df['expect_range'] = pd.cut(df['expect_bor'], bins=expect_bins, labels=expect_labels)

expect_analysis = df.groupby('expect_range', observed=True).agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['sum', 'count', 'mean'],
    'profit': 'sum'
}).round(2)

expect_analysis.columns = ['投票額', '払戻額', '的中数', 'ベット数', '的中率', '収支']
expect_analysis['回収率%'] = (expect_analysis['払戻額'] / expect_analysis['投票額'] * 100).round(2)
expect_analysis['ROI%'] = ((expect_analysis['払戻額'] / expect_analysis['投票額'] - 1) * 100).round(2)

print("\n期待値範囲別パフォーマンス:")
print(expect_analysis)

# 最適期待値範囲の特定
profitable_expect = expect_analysis[expect_analysis['ROI%'] > 0]
if len(profitable_expect) > 0:
    print(f"\n✓ 黒字の期待値範囲: {', '.join(profitable_expect.index.tolist())}")
    best_expect = profitable_expect['ROI%'].idxmax()
    print(f"✓ 最高ROI期待値範囲: {best_expect} (ROI: {profitable_expect.loc[best_expect, 'ROI%']:.2f}%)")

print("\n" + "=" * 80)
print("2. オッズ人気度(bet_oddsrank)による分析")
print("=" * 80)

# オッズ人気度の範囲別分析
oddsrank_bins = [0, 5, 10, 20, 30, 50, 120]
oddsrank_labels = ['1-5位', '6-10位', '11-20位', '21-30位', '31-50位', '51位+']
df['oddsrank_range'] = pd.cut(df['bet_oddsrank'], bins=oddsrank_bins, labels=oddsrank_labels)

oddsrank_analysis = df.groupby('oddsrank_range', observed=True).agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['sum', 'count', 'mean'],
    'profit': 'sum'
}).round(2)

oddsrank_analysis.columns = ['投票額', '払戻額', '的中数', 'ベット数', '的中率', '収支']
oddsrank_analysis['回収率%'] = (oddsrank_analysis['払戻額'] / oddsrank_analysis['投票額'] * 100).round(2)
oddsrank_analysis['ROI%'] = ((oddsrank_analysis['払戻額'] / oddsrank_analysis['投票額'] - 1) * 100).round(2)

print("\nオッズ人気度範囲別パフォーマンス:")
print(oddsrank_analysis)

profitable_oddsrank = oddsrank_analysis[oddsrank_analysis['ROI%'] > 0]
if len(profitable_oddsrank) > 0:
    print(f"\n✓ 黒字のオッズ人気度範囲: {', '.join(profitable_oddsrank.index.tolist())}")
    best_oddsrank = profitable_oddsrank['ROI%'].idxmax()
    print(f"✓ 最高ROIオッズ人気度: {best_oddsrank} (ROI: {profitable_oddsrank.loc[best_oddsrank, 'ROI%']:.2f}%)")

print("\n" + "=" * 80)
print("3. 的中確率(probability)による分析")
print("=" * 80)

# 的中確率の範囲別分析
prob_bins = [0, 0.01, 0.02, 0.03, 0.05, 0.10, 1.0]
prob_labels = ['~1%', '1-2%', '2-3%', '3-5%', '5-10%', '10%+']
df['prob_range'] = pd.cut(df['probability'], bins=prob_bins, labels=prob_labels)

prob_analysis = df.groupby('prob_range', observed=True).agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['sum', 'count', 'mean'],
    'profit': 'sum'
}).round(2)

prob_analysis.columns = ['投票額', '払戻額', '的中数', 'ベット数', '的中率', '収支']
prob_analysis['回収率%'] = (prob_analysis['払戻額'] / prob_analysis['投票額'] * 100).round(2)
prob_analysis['ROI%'] = ((prob_analysis['払戻額'] / prob_analysis['投票額'] - 1) * 100).round(2)

print("\n的中確率範囲別パフォーマンス:")
print(prob_analysis)

profitable_prob = prob_analysis[prob_analysis['ROI%'] > 0]
if len(profitable_prob) > 0:
    print(f"\n✓ 黒字の確率範囲: {', '.join(profitable_prob.index.tolist())}")
    best_prob = profitable_prob['ROI%'].idxmax()
    print(f"✓ 最高ROI確率範囲: {best_prob} (ROI: {profitable_prob.loc[best_prob, 'ROI%']:.2f}%)")

print("\n" + "=" * 80)
print("4. レース条件による分析")
print("=" * 80)

# グレード別分析
print("\n【グレード別】")
grade_analysis = df.groupby('grade').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['count', 'mean'],
    'profit': 'sum'
}).round(2)
grade_analysis.columns = ['投票額', '払戻額', 'ベット数', '的中率', '収支']
grade_analysis['ROI%'] = ((grade_analysis['払戻額'] / grade_analysis['投票額'] - 1) * 100).round(2)
grade_analysis = grade_analysis.sort_values('ROI%', ascending=False)
print(grade_analysis)

profitable_grades = grade_analysis[grade_analysis['ROI%'] > 0]
if len(profitable_grades) > 0:
    print(f"\n✓ 黒字グレード: {', '.join(profitable_grades.index.tolist())}")

# 時間帯別分析
print("\n【時間帯別】")
timezone_analysis = df.groupby('timezone').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['count', 'mean'],
    'profit': 'sum'
}).round(2)
timezone_analysis.columns = ['投票額', '払戻額', 'ベット数', '的中率', '収支']
timezone_analysis['ROI%'] = ((timezone_analysis['払戻額'] / timezone_analysis['投票額'] - 1) * 100).round(2)
timezone_analysis = timezone_analysis.sort_values('ROI%', ascending=False)
print(timezone_analysis)

profitable_timezones = timezone_analysis[timezone_analysis['ROI%'] > 0]
if len(profitable_timezones) > 0:
    print(f"\n✓ 黒字時間帯: {', '.join(profitable_timezones.index.tolist())}")

# 場別分析
print("\n【場別】")
jyo_analysis = df.groupby('jyocd').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['count', 'mean'],
    'profit': 'sum'
}).round(2)
jyo_analysis.columns = ['投票額', '払戻額', 'ベット数', '的中率', '収支']
jyo_analysis['ROI%'] = ((jyo_analysis['払戻額'] / jyo_analysis['投票額'] - 1) * 100).round(2)
jyo_analysis = jyo_analysis.sort_values('ROI%', ascending=False)
print(jyo_analysis.head(10))

profitable_jyos = jyo_analysis[jyo_analysis['ROI%'] > 0]
if len(profitable_jyos) > 0:
    print(f"\n✓ 黒字の場: {', '.join(map(str, profitable_jyos.index.tolist()))}")

# A級選手数別分析
print("\n【A級選手数別】")
alevel_analysis = df.groupby('alevelcount').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['count', 'mean'],
    'profit': 'sum'
}).round(2)
alevel_analysis.columns = ['投票額', '払戻額', 'ベット数', '的中率', '収支']
alevel_analysis['ROI%'] = ((alevel_analysis['払戻額'] / alevel_analysis['投票額'] - 1) * 100).round(2)
alevel_analysis = alevel_analysis.sort_values('ROI%', ascending=False)
print(alevel_analysis)

profitable_alevel = alevel_analysis[alevel_analysis['ROI%'] > 0]
if len(profitable_alevel) > 0:
    print(f"\n✓ 黒字のA級選手数: {', '.join(map(str, profitable_alevel.index.tolist()))}")

print("\n" + "=" * 80)
print("5. 複合条件による最適戦略の探索")
print("=" * 80)

# 最適条件の組み合わせを探索
print("\n最適戦略候補を探索中...")

strategies = []

# 期待値 × オッズ人気度の組み合わせ
for expect_min in [1.0, 1.1, 1.2, 1.3]:
    for expect_max in [1.3, 1.5, 2.0, 3.0]:
        if expect_max <= expect_min:
            continue
        for oddsrank_max in [5, 10, 15, 20, 30]:
            for prob_min in [0.01, 0.02, 0.03, 0.05]:
                
                filtered = df[
                    (df['expect_bor'] >= expect_min) &
                    (df['expect_bor'] <= expect_max) &
                    (df['bet_oddsrank'] <= oddsrank_max) &
                    (df['probability'] >= prob_min)
                ]
                
                if len(filtered) < 50:  # 最低50ベット
                    continue
                
                bet_total = filtered['betamt'].sum()
                return_total = filtered['hitamt'].sum()
                roi = (return_total / bet_total - 1) * 100
                hit_rate = (filtered['is_hit'].sum() / len(filtered)) * 100
                
                if roi > 0:  # 黒字のみ
                    strategies.append({
                        '期待値下限': expect_min,
                        '期待値上限': expect_max,
                        'オッズ人気度上限': oddsrank_max,
                        '確率下限': prob_min,
                        'ベット数': len(filtered),
                        '投票額': bet_total,
                        '払戻額': return_total,
                        '収支': return_total - bet_total,
                        'ROI%': roi,
                        '的中率%': hit_rate
                    })

if len(strategies) > 0:
    strategies_df = pd.DataFrame(strategies)
    strategies_df = strategies_df.sort_values('ROI%', ascending=False)
    
    print(f"\n✓ 黒字戦略数: {len(strategies_df)}")
    print("\nTOP 20 最高ROI戦略:")
    print(strategies_df.head(20).to_string(index=False))
    
    # 安定性も考慮したTOP戦略（ベット数100以上）
    stable_strategies = strategies_df[strategies_df['ベット数'] >= 100]
    if len(stable_strategies) > 0:
        print(f"\n\nTOP 10 安定戦略（ベット数100以上）:")
        print(stable_strategies.head(10).to_string(index=False))
else:
    print("\n✗ 黒字戦略が見つかりませんでした")

print("\n" + "=" * 80)
print("6. 的中レースの特徴分析")
print("=" * 80)

hit_df = df[df['is_hit'] == True].copy()
miss_df = df[df['is_hit'] == False].copy()

print(f"\n的中ベット数: {len(hit_df)}")
print(f"不的中ベット数: {len(miss_df)}")

print("\n【的中時の特徴】")
print(f"平均期待値: {hit_df['expect_bor'].mean():.3f}")
print(f"平均オッズ人気度: {hit_df['bet_oddsrank'].mean():.1f}")
print(f"平均確率: {hit_df['probability'].mean():.4f}")
print(f"平均オッズ: {hit_df['bet_odds'].mean():.1f}")

print("\n【不的中時の特徴】")
print(f"平均期待値: {miss_df['expect_bor'].mean():.3f}")
print(f"平均オッズ人気度: {miss_df['bet_oddsrank'].mean():.1f}")
print(f"平均確率: {miss_df['probability'].mean():.4f}")
print(f"平均オッズ: {miss_df['bet_odds'].mean():.1f}")

print("\n" + "=" * 80)
print("7. 投票金額配分戦略の分析")
print("=" * 80)

# ケリー基準の計算
print("\n【ケリー基準による最適投資比率】")
print("ケリー基準: f* = (p * odds - 1) / (odds - 1)")
print("p: 的中確率, odds: オッズ")

df['kelly_fraction'] = ((df['probability'] * df['bet_odds'] - 1) / (df['bet_odds'] - 1)).clip(0, 1)
df['kelly_fraction'] = df['kelly_fraction'].fillna(0)

print(f"\n平均ケリー比率: {df['kelly_fraction'].mean():.4f}")
print(f"中央値ケリー比率: {df['kelly_fraction'].median():.4f}")

# ケリー比率別のパフォーマンス
kelly_bins = [0, 0.01, 0.02, 0.05, 0.10, 1.0]
kelly_labels = ['~1%', '1-2%', '2-5%', '5-10%', '10%+']
df['kelly_range'] = pd.cut(df['kelly_fraction'], bins=kelly_bins, labels=kelly_labels)

kelly_analysis = df.groupby('kelly_range', observed=True).agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['count', 'mean'],
    'profit': 'sum'
}).round(2)
kelly_analysis.columns = ['投票額', '払戻額', 'ベット数', '的中率', '収支']
kelly_analysis['ROI%'] = ((kelly_analysis['払戻額'] / kelly_analysis['投票額'] - 1) * 100).round(2)
print("\nケリー比率範囲別パフォーマンス:")
print(kelly_analysis)

print("\n" + "=" * 80)
print("8. 月別・時系列パフォーマンス")
print("=" * 80)

df['year_month'] = df['ymd'].astype(str).str[:6]
monthly = df.groupby('year_month').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'profit': 'sum',
    'is_hit': 'count'
})
monthly['ROI%'] = ((monthly['hitamt'] / monthly['betamt'] - 1) * 100).round(2)
monthly['累積収支'] = monthly['profit'].cumsum()

print("\n月別パフォーマンス:")
print(monthly)

profitable_months = monthly[monthly['ROI%'] > 0]
print(f"\n✓ 黒字月数: {len(profitable_months)}/{len(monthly)}")
print(f"✓ 最終累積収支: ¥{monthly['累積収支'].iloc[-1]:,.0f}")

print("\n" + "=" * 80)
print("分析完了")
print("=" * 80)

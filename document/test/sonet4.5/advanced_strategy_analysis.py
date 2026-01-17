import pandas as pd
import numpy as np
import sys
import io

# UTF-8出力設定
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

print("=" * 80)
print("Advanced Strategy Analysis - Detailed Breakdown")
print("=" * 80)

df = pd.read_csv(r'c:\Dev\github\pod_boatrace\document\test\result_3T_123.tsv', sep='\t')

# 的中フラグ追加
df['is_hit'] = df['hitamt'] > 0
df['profit'] = df['hitamt'] - df['betamt']

print("\n" + "=" * 80)
print("Strategy A Deep Dive: expect_bor [1.3-1.5], oddsrank <= 5, prob >= 0.05")
print("=" * 80)

strategy_a = df[
    (df['expect_bor'] >= 1.3) &
    (df['expect_bor'] <= 1.5) &
    (df['bet_oddsrank'] <= 5) &
    (df['probability'] >= 0.05)
].copy()

print(f"\nTotal bets: {len(strategy_a)}")
print(f"Hit rate: {(strategy_a['is_hit'].sum() / len(strategy_a) * 100):.2f}%")
print(f"Total profit: ¥{strategy_a['profit'].sum():,}")
print(f"ROI: {(strategy_a['hitamt'].sum() / strategy_a['betamt'].sum() - 1) * 100:.2f}%")

# グレード別の内訳
print("\n[Strategy A by Grade]")
grade_breakdown = strategy_a.groupby('grade').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['count', 'sum'],
    'profit': 'sum'
})
grade_breakdown.columns = ['Bet Amount', 'Return', 'Bets', 'Hits', 'Profit']
grade_breakdown['ROI%'] = ((grade_breakdown['Return'] / grade_breakdown['Bet Amount'] - 1) * 100).round(2)
grade_breakdown['Hit Rate%'] = (grade_breakdown['Hits'] / grade_breakdown['Bets'] * 100).round(2)
print(grade_breakdown.sort_values('ROI%', ascending=False))

# 場別の内訳
print("\n[Strategy A by Venue (jyocd)]")
jyo_breakdown = strategy_a.groupby('jyocd').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['count', 'sum'],
    'profit': 'sum'
})
jyo_breakdown.columns = ['Bet Amount', 'Return', 'Bets', 'Hits', 'Profit']
jyo_breakdown['ROI%'] = ((jyo_breakdown['Return'] / jyo_breakdown['Bet Amount'] - 1) * 100).round(2)
jyo_breakdown['Hit Rate%'] = (jyo_breakdown['Hits'] / jyo_breakdown['Bets'] * 100).round(2)
jyo_breakdown = jyo_breakdown[jyo_breakdown['Bets'] >= 10]  # 10ベット以上のみ
print(jyo_breakdown.sort_values('ROI%', ascending=False).head(15))

# A級選手数別
print("\n[Strategy A by A-level Count]")
alevel_breakdown = strategy_a.groupby('alevelcount').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['count', 'sum'],
    'profit': 'sum'
})
alevel_breakdown.columns = ['Bet Amount', 'Return', 'Bets', 'Hits', 'Profit']
alevel_breakdown['ROI%'] = ((alevel_breakdown['Return'] / alevel_breakdown['Bet Amount'] - 1) * 100).round(2)
alevel_breakdown['Hit Rate%'] = (alevel_breakdown['Hits'] / alevel_breakdown['Bets'] * 100).round(2)
print(alevel_breakdown.sort_values('ROI%', ascending=False))

print("\n" + "=" * 80)
print("Enhanced Strategy: Strategy A + Grade Filter (G1, G3)")
print("=" * 80)

enhanced_strategy = strategy_a[
    (strategy_a['grade'].isin(['G1', 'G3']))
].copy()

if len(enhanced_strategy) > 0:
    print(f"\nTotal bets: {len(enhanced_strategy)}")
    print(f"Hit rate: {(enhanced_strategy['is_hit'].sum() / len(enhanced_strategy) * 100):.2f}%")
    print(f"Total profit: ¥{enhanced_strategy['profit'].sum():,}")
    print(f"ROI: {(enhanced_strategy['hitamt'].sum() / enhanced_strategy['betamt'].sum() - 1) * 100:.2f}%")
    
    print("\n[By Grade]")
    print(enhanced_strategy.groupby('grade').agg({
        'betamt': 'sum',
        'hitamt': 'sum',
        'is_hit': ['count', 'sum'],
        'profit': 'sum'
    }))
else:
    print("\nNo bets found for this filter combination")

print("\n" + "=" * 80)
print("Super Enhanced Strategy: Strategy A + Grade (G1, G3) + Top Venues")
print("=" * 80)

top_venues = [6, 10, 13, 16, 20, 17]
super_strategy = strategy_a[
    (strategy_a['grade'].isin(['G1', 'G3'])) &
    (strategy_a['jyocd'].isin(top_venues))
].copy()

if len(super_strategy) > 0:
    print(f"\nTotal bets: {len(super_strategy)}")
    print(f"Hit rate: {(super_strategy['is_hit'].sum() / len(super_strategy) * 100):.2f}%")
    print(f"Total profit: ¥{super_strategy['profit'].sum():,}")
    print(f"ROI: {(super_strategy['hitamt'].sum() / super_strategy['betamt'].sum() - 1) * 100:.2f}%")
else:
    print("\nNo bets found for this filter combination")

print("\n" + "=" * 80)
print("Alternative Strategy Exploration")
print("=" * 80)

# 期待値範囲を変えて探索
print("\n[Varying Expected Value Ranges with oddsrank <= 5, prob >= 0.05]")
expect_ranges = [
    (1.2, 1.4),
    (1.2, 1.5),
    (1.3, 1.6),
    (1.4, 1.8),
    (1.5, 2.0)
]

alt_results = []
for exp_min, exp_max in expect_ranges:
    alt_df = df[
        (df['expect_bor'] >= exp_min) &
        (df['expect_bor'] <= exp_max) &
        (df['bet_oddsrank'] <= 5) &
        (df['probability'] >= 0.05)
    ]
    
    if len(alt_df) > 0:
        roi = (alt_df['hitamt'].sum() / alt_df['betamt'].sum() - 1) * 100
        hit_rate = alt_df['is_hit'].sum() / len(alt_df) * 100
        alt_results.append({
            'Expect Range': f'{exp_min}-{exp_max}',
            'Bets': len(alt_df),
            'Hits': alt_df['is_hit'].sum(),
            'Hit Rate%': round(hit_rate, 2),
            'Profit': alt_df['profit'].sum(),
            'ROI%': round(roi, 2)
        })

alt_df_results = pd.DataFrame(alt_results)
print(alt_df_results.to_string(index=False))

# オッズ人気度範囲を変えて探索
print("\n[Varying Odds Rank with expect_bor [1.3-1.5], prob >= 0.05]")
oddsrank_limits = [3, 5, 7, 10, 15]

odds_results = []
for max_rank in oddsrank_limits:
    odds_df = df[
        (df['expect_bor'] >= 1.3) &
        (df['expect_bor'] <= 1.5) &
        (df['bet_oddsrank'] <= max_rank) &
        (df['probability'] >= 0.05)
    ]
    
    if len(odds_df) > 0:
        roi = (odds_df['hitamt'].sum() / odds_df['betamt'].sum() - 1) * 100
        hit_rate = odds_df['is_hit'].sum() / len(odds_df) * 100
        odds_results.append({
            'Max Odds Rank': max_rank,
            'Bets': len(odds_df),
            'Hits': odds_df['is_hit'].sum(),
            'Hit Rate%': round(hit_rate, 2),
            'Profit': odds_df['profit'].sum(),
            'ROI%': round(roi, 2)
        })

odds_df_results = pd.DataFrame(odds_results)
print(odds_df_results.to_string(index=False))

# 確率下限を変えて探索
print("\n[Varying Probability Threshold with expect_bor [1.3-1.5], oddsrank <= 5]")
prob_thresholds = [0.03, 0.04, 0.05, 0.06, 0.07]

prob_results = []
for min_prob in prob_thresholds:
    prob_df = df[
        (df['expect_bor'] >= 1.3) &
        (df['expect_bor'] <= 1.5) &
        (df['bet_oddsrank'] <= 5) &
        (df['probability'] >= min_prob)
    ]
    
    if len(prob_df) > 0:
        roi = (prob_df['hitamt'].sum() / prob_df['betamt'].sum() - 1) * 100
        hit_rate = prob_df['is_hit'].sum() / len(prob_df) * 100
        prob_results.append({
            'Min Probability': min_prob,
            'Bets': len(prob_df),
            'Hits': prob_df['is_hit'].sum(),
            'Hit Rate%': round(hit_rate, 2),
            'Profit': prob_df['profit'].sum(),
            'ROI%': round(roi, 2)
        })

prob_df_results = pd.DataFrame(prob_results)
print(prob_df_results.to_string(index=False))

print("\n" + "=" * 80)
print("Win/Loss Pattern Analysis")
print("=" * 80)

# 連勝・連敗分析
strategy_a_sorted = strategy_a.sort_values(['ymd', 'jyocd', 'raceno'])
strategy_a_sorted['streak'] = (strategy_a_sorted['is_hit'] != strategy_a_sorted['is_hit'].shift()).cumsum()
strategy_a_sorted['streak_type'] = strategy_a_sorted['is_hit'].map({True: 'Win', False: 'Loss'})

streaks = strategy_a_sorted.groupby(['streak', 'streak_type']).size().reset_index(name='length')
win_streaks = streaks[streaks['streak_type'] == 'Win']['length']
loss_streaks = streaks[streaks['streak_type'] == 'Loss']['length']

print(f"\n[Winning Streaks]")
print(f"Max consecutive wins: {win_streaks.max() if len(win_streaks) > 0 else 0}")
print(f"Average win streak: {win_streaks.mean():.2f}" if len(win_streaks) > 0 else "N/A")

print(f"\n[Losing Streaks]")
print(f"Max consecutive losses: {loss_streaks.max() if len(loss_streaks) > 0 else 0}")
print(f"Average loss streak: {loss_streaks.mean():.2f}" if len(loss_streaks) > 0 else "N/A")

print("\n" + "=" * 80)
print("Monthly Performance of Strategy A")
print("=" * 80)

strategy_a['year_month'] = strategy_a['ymd'].astype(str).str[:6]
monthly_perf = strategy_a.groupby('year_month').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'is_hit': ['count', 'sum'],
    'profit': 'sum'
})
monthly_perf.columns = ['Bet Amount', 'Return', 'Bets', 'Hits', 'Profit']
monthly_perf['ROI%'] = ((monthly_perf['Return'] / monthly_perf['Bet Amount'] - 1) * 100).round(2)
monthly_perf['Hit Rate%'] = (monthly_perf['Hits'] / monthly_perf['Bets'] * 100).round(2)
monthly_perf['Cumulative Profit'] = monthly_perf['Profit'].cumsum()

print(monthly_perf)

profitable_months = len(monthly_perf[monthly_perf['ROI%'] > 0])
print(f"\nProfitable months: {profitable_months}/{len(monthly_perf)}")
print(f"Final cumulative profit: ¥{monthly_perf['Cumulative Profit'].iloc[-1]:,.0f}")

print("\n" + "=" * 80)
print("Risk Metrics for Strategy A")
print("=" * 80)

returns = strategy_a['profit'].values
print(f"\nMean return per bet: ¥{returns.mean():.2f}")
print(f"Std deviation: ¥{returns.std():.2f}")
print(f"Sharpe ratio (assuming 0 risk-free rate): {returns.mean() / returns.std():.4f}")
print(f"Max single loss: ¥{returns.min():.0f}")
print(f"Max single win: ¥{returns.max():.0f}")

# ドローダウン分析
cumulative_returns = strategy_a_sorted['profit'].cumsum()
running_max = cumulative_returns.expanding().max()
drawdown = cumulative_returns - running_max
max_drawdown = drawdown.min()

print(f"\nMax drawdown: ¥{max_drawdown:.0f}")
print(f"Max drawdown %: {(max_drawdown / strategy_a_sorted['betamt'].cumsum().max() * 100):.2f}%")

print("\n" + "=" * 80)
print("Analysis Complete")
print("=" * 80)

import pandas as pd
import numpy as np
import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

print("=" * 80)
print("3-Rentan 123 Betting Strategy Analysis")
print("=" * 80)

df = pd.read_csv(r'c:\Dev\github\pod_boatrace\document\test\result_3T_123.tsv', sep='\t')
print(f"\nTotal records: {len(df):,}")
print(f"Period: {df['ymd'].min()} - {df['ymd'].max()}")

# Basic stats
total_bet = df['betamt'].sum()
total_return = df['hitamt'].sum()
total_roi = (total_return / total_bet - 1) * 100
hit_count = (df['hitamt'] > 0).sum()
hit_rate = hit_count / len(df) * 100

print(f"\n[Overall Stats]")
print(f"Total Bet: {total_bet:,}")
print(f"Total Return: {total_return:,}")
print(f"P/L: {total_return - total_bet:,}")
print(f"ROI: {total_roi:.2f}%")
print(f"Hit Rate: {hit_rate:.2f}% ({hit_count}/{len(df)})")

df['is_hit'] = df['hitamt'] > 0
df['profit'] = df['hitamt'] - df['betamt']

print("\n" + "=" * 80)
print("1. Expected Value (expect_bor) Analysis")
print("=" * 80)

expect_bins = [0, 1.0, 1.1, 1.2, 1.3, 1.5, 2.0, 100]
expect_labels = ['~1.0', '1.0-1.1', '1.1-1.2', '1.2-1.3', '1.3-1.5', '1.5-2.0', '2.0+']
df['expect_range'] = pd.cut(df['expect_bor'], bins=expect_bins, labels=expect_labels)

expect_analysis = df.groupby('expect_range', observed=True).agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['sum', 'count', 'mean'], 'profit': 'sum'
}).round(2)
expect_analysis.columns = ['BetAmt', 'Return', 'Hits', 'Bets', 'HitRate', 'Profit']
expect_analysis['ROI%'] = ((expect_analysis['Return'] / expect_analysis['BetAmt'] - 1) * 100).round(2)
print(expect_analysis)

print("\n" + "=" * 80)
print("2. Odds Rank (bet_oddsrank) Analysis")
print("=" * 80)

oddsrank_bins = [0, 5, 10, 20, 30, 50, 120]
oddsrank_labels = ['1-5', '6-10', '11-20', '21-30', '31-50', '51+']
df['oddsrank_range'] = pd.cut(df['bet_oddsrank'], bins=oddsrank_bins, labels=oddsrank_labels)

oddsrank_analysis = df.groupby('oddsrank_range', observed=True).agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['sum', 'count', 'mean'], 'profit': 'sum'
}).round(2)
oddsrank_analysis.columns = ['BetAmt', 'Return', 'Hits', 'Bets', 'HitRate', 'Profit']
oddsrank_analysis['ROI%'] = ((oddsrank_analysis['Return'] / oddsrank_analysis['BetAmt'] - 1) * 100).round(2)
print(oddsrank_analysis)

print("\n" + "=" * 80)
print("3. Probability Analysis")
print("=" * 80)

prob_bins = [0, 0.01, 0.02, 0.03, 0.05, 0.10, 1.0]
prob_labels = ['~1%', '1-2%', '2-3%', '3-5%', '5-10%', '10%+']
df['prob_range'] = pd.cut(df['probability'], bins=prob_bins, labels=prob_labels)

prob_analysis = df.groupby('prob_range', observed=True).agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['sum', 'count', 'mean'], 'profit': 'sum'
}).round(2)
prob_analysis.columns = ['BetAmt', 'Return', 'Hits', 'Bets', 'HitRate', 'Profit']
prob_analysis['ROI%'] = ((prob_analysis['Return'] / prob_analysis['BetAmt'] - 1) * 100).round(2)
print(prob_analysis)

print("\n" + "=" * 80)
print("4. Race Condition Analysis")
print("=" * 80)

print("\n[By Grade]")
grade_analysis = df.groupby('grade').agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['count', 'mean'], 'profit': 'sum'
}).round(2)
grade_analysis.columns = ['BetAmt', 'Return', 'Bets', 'HitRate', 'Profit']
grade_analysis['ROI%'] = ((grade_analysis['Return'] / grade_analysis['BetAmt'] - 1) * 100).round(2)
print(grade_analysis.sort_values('ROI%', ascending=False))

print("\n[By Timezone]")
tz_analysis = df.groupby('timezone').agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['count', 'mean'], 'profit': 'sum'
}).round(2)
tz_analysis.columns = ['BetAmt', 'Return', 'Bets', 'HitRate', 'Profit']
tz_analysis['ROI%'] = ((tz_analysis['Return'] / tz_analysis['BetAmt'] - 1) * 100).round(2)
print(tz_analysis.sort_values('ROI%', ascending=False))

print("\n[By Venue (jyocd)]")
jyo_analysis = df.groupby('jyocd').agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['count', 'mean'], 'profit': 'sum'
}).round(2)
jyo_analysis.columns = ['BetAmt', 'Return', 'Bets', 'HitRate', 'Profit']
jyo_analysis['ROI%'] = ((jyo_analysis['Return'] / jyo_analysis['BetAmt'] - 1) * 100).round(2)
print(jyo_analysis.sort_values('ROI%', ascending=False).head(15))

print("\n[By A-level Count]")
alevel_analysis = df.groupby('alevelcount').agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['count', 'mean'], 'profit': 'sum'
}).round(2)
alevel_analysis.columns = ['BetAmt', 'Return', 'Bets', 'HitRate', 'Profit']
alevel_analysis['ROI%'] = ((alevel_analysis['Return'] / alevel_analysis['BetAmt'] - 1) * 100).round(2)
print(alevel_analysis.sort_values('ROI%', ascending=False))

print("\n" + "=" * 80)
print("5. Optimal Strategy Search")
print("=" * 80)

strategies = []
for expect_min in [1.0, 1.1, 1.2, 1.3]:
    for expect_max in [1.3, 1.5, 2.0, 3.0]:
        if expect_max <= expect_min:
            continue
        for oddsrank_max in [3, 5, 10, 15, 20]:
            for prob_min in [0.01, 0.02, 0.03, 0.05]:
                filtered = df[
                    (df['expect_bor'] >= expect_min) &
                    (df['expect_bor'] <= expect_max) &
                    (df['bet_oddsrank'] <= oddsrank_max) &
                    (df['probability'] >= prob_min)
                ]
                if len(filtered) < 30:
                    continue
                bet_total = filtered['betamt'].sum()
                return_total = filtered['hitamt'].sum()
                roi = (return_total / bet_total - 1) * 100
                hit_rate = (filtered['is_hit'].sum() / len(filtered)) * 100
                if roi > 0:
                    strategies.append({
                        'ExpMin': expect_min, 'ExpMax': expect_max,
                        'OddsRankMax': oddsrank_max, 'ProbMin': prob_min,
                        'Bets': len(filtered), 'Investment': bet_total,
                        'Return': return_total, 'Profit': return_total - bet_total,
                        'ROI%': round(roi, 2), 'HitRate%': round(hit_rate, 2)
                    })

if strategies:
    strategies_df = pd.DataFrame(strategies).sort_values('ROI%', ascending=False)
    print(f"\nProfitable Strategies Found: {len(strategies_df)}")
    print("\nTOP 20 by ROI:")
    print(strategies_df.head(20).to_string(index=False))
    
    stable = strategies_df[strategies_df['Bets'] >= 100]
    if len(stable) > 0:
        print(f"\n\nStable Strategies (100+ bets):")
        print(stable.head(10).to_string(index=False))

print("\n" + "=" * 80)
print("6. Strategy A Deep Analysis: exp[1.3-1.5], oddsrank<=5, prob>=0.05")
print("=" * 80)

strategy_a = df[
    (df['expect_bor'] >= 1.3) & (df['expect_bor'] <= 1.5) &
    (df['bet_oddsrank'] <= 5) & (df['probability'] >= 0.05)
].copy()

print(f"\nBets: {len(strategy_a)}")
print(f"ROI: {(strategy_a['hitamt'].sum() / strategy_a['betamt'].sum() - 1) * 100:.2f}%")
print(f"Hit Rate: {strategy_a['is_hit'].sum() / len(strategy_a) * 100:.2f}%")
print(f"Profit: {strategy_a['profit'].sum():,}")

print("\n[Strategy A by Grade]")
sa_grade = strategy_a.groupby('grade').agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['count', 'sum'], 'profit': 'sum'
})
sa_grade.columns = ['BetAmt', 'Return', 'Bets', 'Hits', 'Profit']
sa_grade['ROI%'] = ((sa_grade['Return'] / sa_grade['BetAmt'] - 1) * 100).round(2)
sa_grade['HitRate%'] = (sa_grade['Hits'] / sa_grade['Bets'] * 100).round(2)
print(sa_grade.sort_values('ROI%', ascending=False))

print("\n[Strategy A by Venue (top 10)]")
sa_jyo = strategy_a.groupby('jyocd').agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['count', 'sum'], 'profit': 'sum'
})
sa_jyo.columns = ['BetAmt', 'Return', 'Bets', 'Hits', 'Profit']
sa_jyo['ROI%'] = ((sa_jyo['Return'] / sa_jyo['BetAmt'] - 1) * 100).round(2)
sa_jyo = sa_jyo[sa_jyo['Bets'] >= 10]
print(sa_jyo.sort_values('ROI%', ascending=False).head(10))

print("\n[Strategy A by A-level Count]")
sa_alevel = strategy_a.groupby('alevelcount').agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['count', 'sum'], 'profit': 'sum'
})
sa_alevel.columns = ['BetAmt', 'Return', 'Bets', 'Hits', 'Profit']
sa_alevel['ROI%'] = ((sa_alevel['Return'] / sa_alevel['BetAmt'] - 1) * 100).round(2)
print(sa_alevel.sort_values('ROI%', ascending=False))

print("\n" + "=" * 80)
print("7. Enhanced Strategy: Strategy A + Grade Filter (G1, G3)")
print("=" * 80)

enhanced = strategy_a[strategy_a['grade'].isin(['G1', 'G3'])].copy()
if len(enhanced) > 0:
    print(f"\nBets: {len(enhanced)}")
    print(f"ROI: {(enhanced['hitamt'].sum() / enhanced['betamt'].sum() - 1) * 100:.2f}%")
    print(f"Hit Rate: {enhanced['is_hit'].sum() / len(enhanced) * 100:.2f}%")
    print(f"Profit: {enhanced['profit'].sum():,}")
    print(f"\nBy Grade:")
    print(enhanced.groupby('grade').agg({'betamt': 'sum', 'hitamt': 'sum', 'is_hit': 'count', 'profit': 'sum'}))

print("\n" + "=" * 80)
print("8. Monthly Performance (Strategy A)")
print("=" * 80)

strategy_a['ym'] = strategy_a['ymd'].astype(str).str[:6]
monthly = strategy_a.groupby('ym').agg({
    'betamt': 'sum', 'hitamt': 'sum', 'is_hit': ['count', 'sum'], 'profit': 'sum'
})
monthly.columns = ['BetAmt', 'Return', 'Bets', 'Hits', 'Profit']
monthly['ROI%'] = ((monthly['Return'] / monthly['BetAmt'] - 1) * 100).round(2)
monthly['CumProfit'] = monthly['Profit'].cumsum()
print(monthly)
print(f"\nProfitable Months: {len(monthly[monthly['ROI%'] > 0])}/{len(monthly)}")

print("\n" + "=" * 80)
print("9. Risk Metrics (Strategy A)")
print("=" * 80)

strategy_a_sorted = strategy_a.sort_values(['ymd', 'jyocd', 'raceno'])
strategy_a_sorted['streak'] = (strategy_a_sorted['is_hit'] != strategy_a_sorted['is_hit'].shift()).cumsum()
streaks = strategy_a_sorted.groupby(['streak', 'is_hit']).size().reset_index(name='length')
loss_streaks = streaks[streaks['is_hit'] == False]['length']
win_streaks = streaks[streaks['is_hit'] == True]['length']

print(f"\nMax Consecutive Losses: {loss_streaks.max() if len(loss_streaks) > 0 else 0}")
print(f"Avg Loss Streak: {loss_streaks.mean():.2f}" if len(loss_streaks) > 0 else "N/A")
print(f"Max Consecutive Wins: {win_streaks.max() if len(win_streaks) > 0 else 0}")

returns = strategy_a['profit'].values
print(f"\nMean Return/Bet: {returns.mean():.2f}")
print(f"Std Dev: {returns.std():.2f}")
print(f"Sharpe Ratio: {returns.mean() / returns.std():.4f}")
print(f"Max Single Win: {returns.max():,}")
print(f"Max Single Loss: {returns.min():,}")

cum = strategy_a_sorted['profit'].cumsum()
drawdown = cum - cum.expanding().max()
print(f"\nMax Drawdown: {drawdown.min():,}")

print("\n" + "=" * 80)
print("Analysis Complete")
print("=" * 80)

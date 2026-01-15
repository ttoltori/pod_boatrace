import pandas as pd
import numpy as np

df = pd.read_csv(r'c:\Dev\github\pod_boatrace\document\test\result_3T_123.tsv', sep='\t', encoding='utf-8')

print('=== Deep Analysis for Profitable Strategies ===\n')

# Best strategy found: Wind 0-2m AND A-level [0,5,6] AND oddsrank <= 5
print('--- BEST STRATEGY 1: Wind 0-2m AND A-level [0,5,6] AND oddsrank <= 5 ---')
best1 = df[(df['wind'] <= 2) & (df['alevelcount'].isin([0, 5, 6])) & (df['bet_oddsrank'] <= 5)]
print(f'Count: {len(best1)}, Bet: {best1["betamt"].sum()}, Hit: {best1["hitamt"].sum()}')
print(f'Profit: {best1["hitamt"].sum() - best1["betamt"].sum()}, Recovery: {best1["hitamt"].sum() / best1["betamt"].sum() * 100:.2f}%')
print(f'Hit rate: {(best1["hitamt"] > 0).sum() / len(best1) * 100:.2f}%')

# Best strategy 2: Level1=B1 AND Wind 0-2m AND oddsrank <= 5
print('\n--- BEST STRATEGY 2: Level1=B1 AND Wind 0-2m AND oddsrank <= 5 ---')
best2 = df[(df['level1'] == 'B1') & (df['wind'] <= 2) & (df['bet_oddsrank'] <= 5)]
print(f'Count: {len(best2)}, Bet: {best2["betamt"].sum()}, Hit: {best2["hitamt"].sum()}')
print(f'Profit: {best2["hitamt"].sum() - best2["betamt"].sum()}, Recovery: {best2["hitamt"].sum() / best2["betamt"].sum() * 100:.2f}%')
print(f'Hit rate: {(best2["hitamt"] > 0).sum() / len(best2) * 100:.2f}%')

# Explore more combinations
print('\n=== Additional Strategy Exploration ===')

# Wind 0-2m AND com_confidence == 2 AND oddsrank <= 5
print('\n--- Wind 0-2m AND com_confidence=2 AND oddsrank <= 5 ---')
s1 = df[(df['wind'] <= 2) & (df['com_confidence'] == 2) & (df['bet_oddsrank'] <= 5)]
if len(s1) > 0:
    print(f'Count: {len(s1)}, Bet: {s1["betamt"].sum()}, Hit: {s1["hitamt"].sum()}')
    print(f'Profit: {s1["hitamt"].sum() - s1["betamt"].sum()}, Recovery: {s1["hitamt"].sum() / s1["betamt"].sum() * 100:.2f}%')

# Wind 0-2m AND profitable oddsrank AND expect_bor >= 1.0
print('\n--- Wind 0-2m AND profitable oddsrank [1-7,12,13,16,26-28] AND expect_bor >= 1.0 ---')
profitable_ranks = [1,2,3,4,5,6,7,12,13,16,26,27,28]
s2 = df[(df['wind'] <= 2) & (df['bet_oddsrank'].isin(profitable_ranks)) & (df['expect_bor'] >= 1.0)]
if len(s2) > 0:
    print(f'Count: {len(s2)}, Bet: {s2["betamt"].sum()}, Hit: {s2["hitamt"].sum()}')
    print(f'Profit: {s2["hitamt"].sum() - s2["betamt"].sum()}, Recovery: {s2["hitamt"].sum() / s2["betamt"].sum() * 100:.2f}%')

# Grade analysis with wind
print('\n--- Grade G1/G2/G3/SG AND Wind 0-2m ---')
s3 = df[(df['grade'].isin(['G1', 'G2', 'G3', 'SG'])) & (df['wind'] <= 2)]
if len(s3) > 0:
    print(f'Count: {len(s3)}, Bet: {s3["betamt"].sum()}, Hit: {s3["hitamt"].sum()}')
    print(f'Profit: {s3["hitamt"].sum() - s3["betamt"].sum()}, Recovery: {s3["hitamt"].sum() / s3["betamt"].sum() * 100:.2f}%')

# Grade G1/G2/G3/SG AND Wind 0-2m AND oddsrank <= 5
print('\n--- Grade G1/G2/G3/SG AND Wind 0-2m AND oddsrank <= 5 ---')
s4 = df[(df['grade'].isin(['G1', 'G2', 'G3', 'SG'])) & (df['wind'] <= 2) & (df['bet_oddsrank'] <= 5)]
if len(s4) > 0:
    print(f'Count: {len(s4)}, Bet: {s4["betamt"].sum()}, Hit: {s4["hitamt"].sum()}')
    print(f'Profit: {s4["hitamt"].sum() - s4["betamt"].sum()}, Recovery: {s4["hitamt"].sum() / s4["betamt"].sum() * 100:.2f}%')

# By raceno
print('\n=== By Race Number ===')
raceno_stats = df.groupby('raceno').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
raceno_stats['profit'] = raceno_stats['hitamt'] - raceno_stats['betamt']
raceno_stats['recovery'] = raceno_stats['hitamt'] / raceno_stats['betamt'] * 100
print(raceno_stats.to_string())

# Profitable race numbers
profitable_races = raceno_stats[raceno_stats['recovery'] >= 100].index.tolist()
print(f'\nProfitable race numbers: {profitable_races}')

# Wind 0-2m AND profitable race numbers
print('\n--- Wind 0-2m AND profitable race numbers ---')
s5 = df[(df['wind'] <= 2) & (df['raceno'].isin(profitable_races))]
if len(s5) > 0:
    print(f'Count: {len(s5)}, Bet: {s5["betamt"].sum()}, Hit: {s5["hitamt"].sum()}')
    print(f'Profit: {s5["hitamt"].sum() - s5["betamt"].sum()}, Recovery: {s5["hitamt"].sum() / s5["betamt"].sum() * 100:.2f}%')

# Wind 0-2m AND profitable race numbers AND oddsrank <= 5
print('\n--- Wind 0-2m AND profitable race numbers AND oddsrank <= 5 ---')
s6 = df[(df['wind'] <= 2) & (df['raceno'].isin(profitable_races)) & (df['bet_oddsrank'] <= 5)]
if len(s6) > 0:
    print(f'Count: {len(s6)}, Bet: {s6["betamt"].sum()}, Hit: {s6["hitamt"].sum()}')
    print(f'Profit: {s6["hitamt"].sum() - s6["betamt"].sum()}, Recovery: {s6["hitamt"].sum() / s6["betamt"].sum() * 100:.2f}%')

# Monthly analysis
print('\n=== Monthly Analysis ===')
df['month'] = df['ymd'].astype(str).str[:6]
monthly = df.groupby('month').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
monthly['profit'] = monthly['hitamt'] - monthly['betamt']
monthly['recovery'] = monthly['hitamt'] / monthly['betamt'] * 100
print(monthly.to_string())

# Stability check for best strategy
print('\n=== Monthly Stability Check for Best Strategy (Wind 0-2m AND A-level [0,5,6] AND oddsrank <= 5) ===')
best_df = df[(df['wind'] <= 2) & (df['alevelcount'].isin([0, 5, 6])) & (df['bet_oddsrank'] <= 5)].copy()
best_df['month'] = best_df['ymd'].astype(str).str[:6]
best_monthly = best_df.groupby('month').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
best_monthly['profit'] = best_monthly['hitamt'] - best_monthly['betamt']
best_monthly['recovery'] = best_monthly['hitamt'] / best_monthly['betamt'] * 100
print(best_monthly.to_string())
print(f'\nMonths with profit: {(best_monthly["profit"] > 0).sum()} / {len(best_monthly)}')

# Stability check for strategy 2
print('\n=== Monthly Stability Check for Strategy 2 (Level1=B1 AND Wind 0-2m AND oddsrank <= 5) ===')
best2_df = df[(df['level1'] == 'B1') & (df['wind'] <= 2) & (df['bet_oddsrank'] <= 5)].copy()
best2_df['month'] = best2_df['ymd'].astype(str).str[:6]
best2_monthly = best2_df.groupby('month').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
best2_monthly['profit'] = best2_monthly['hitamt'] - best2_monthly['betamt']
best2_monthly['recovery'] = best2_monthly['hitamt'] / best2_monthly['betamt'] * 100
print(best2_monthly.to_string())
print(f'\nMonths with profit: {(best2_monthly["profit"] > 0).sum()} / {len(best2_monthly)}')

# Odds-based bet amount strategy
print('\n=== Odds-based Bet Amount Strategy Analysis ===')
print('If we bet proportionally to expected value (expect_bor)...')
best_df2 = df[(df['wind'] <= 2) & (df['alevelcount'].isin([0, 5, 6])) & (df['bet_oddsrank'] <= 5)].copy()
# Simulate betting 100 * expect_bor
best_df2['weighted_bet'] = 100 * best_df2['expect_bor']
best_df2['weighted_hit'] = best_df2.apply(lambda x: x['weighted_bet'] * x['bet_odds'] if x['hitamt'] > 0 else 0, axis=1)
print(f'Total weighted bet: {best_df2["weighted_bet"].sum():.0f}')
print(f'Total weighted hit: {best_df2["weighted_hit"].sum():.0f}')
print(f'Weighted profit: {best_df2["weighted_hit"].sum() - best_df2["weighted_bet"].sum():.0f}')
print(f'Weighted recovery: {best_df2["weighted_hit"].sum() / best_df2["weighted_bet"].sum() * 100:.2f}%')

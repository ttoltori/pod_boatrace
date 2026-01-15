import pandas as pd
import numpy as np

df = pd.read_csv(r'c:\Dev\github\pod_boatrace\document\test\result_3T_123.tsv', sep='\t', encoding='utf-8')

# Basic stats
total_bets = len(df)
total_betamt = df['betamt'].sum()
total_hitamt = df['hitamt'].sum()
hit_count = (df['hitamt'] > 0).sum()
hit_rate = hit_count / total_bets * 100
recovery_rate = total_hitamt / total_betamt * 100
profit = total_hitamt - total_betamt

print('=== Basic Statistics ===')
print(f'Total bets: {total_bets}')
print(f'Total bet amount: {total_betamt}')
print(f'Total hit amount: {total_hitamt}')
print(f'Profit/Loss: {profit}')
print(f'Hit count: {hit_count}')
print(f'Hit rate: {hit_rate:.2f}%')
print(f'Recovery rate: {recovery_rate:.2f}%')
print()
print(f'Date range: {df["ymd"].min()} - {df["ymd"].max()}')
print()

# By venue
print('=== By Venue (jyocd) - Sorted by Recovery ===')
venue_stats = df.groupby('jyocd').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
venue_stats['profit'] = venue_stats['hitamt'] - venue_stats['betamt']
venue_stats['recovery'] = venue_stats['hitamt'] / venue_stats['betamt'] * 100
print(venue_stats.sort_values('recovery', ascending=False).head(15).to_string())
print()

# By grade
print('=== By Grade ===')
grade_stats = df.groupby('grade').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
grade_stats['profit'] = grade_stats['hitamt'] - grade_stats['betamt']
grade_stats['recovery'] = grade_stats['hitamt'] / grade_stats['betamt'] * 100
print(grade_stats.sort_values('recovery', ascending=False).to_string())
print()

# Combined profitable conditions
print('=== Combined Profitable Conditions Analysis ===')

# Condition 1: Wind 0-2m
print('\n--- Wind 0-2m ---')
cond1 = df[df['wind'] <= 2]
print(f'Count: {len(cond1)}, Recovery: {cond1["hitamt"].sum() / cond1["betamt"].sum() * 100:.2f}%')

# Condition 2: Wind 0-2m AND bet_oddsrank <= 10
print('\n--- Wind 0-2m AND bet_oddsrank <= 10 ---')
cond2 = df[(df['wind'] <= 2) & (df['bet_oddsrank'] <= 10)]
print(f'Count: {len(cond2)}, Recovery: {cond2["hitamt"].sum() / cond2["betamt"].sum() * 100:.2f}%')

# Condition 3: Wind 0-2m AND bet_oddsrank <= 5
print('\n--- Wind 0-2m AND bet_oddsrank <= 5 ---')
cond3 = df[(df['wind'] <= 2) & (df['bet_oddsrank'] <= 5)]
print(f'Count: {len(cond3)}, Recovery: {cond3["hitamt"].sum() / cond3["betamt"].sum() * 100:.2f}%')

# Condition 4: alevelcount in [0, 5, 6]
print('\n--- A-level count in [0, 5, 6] ---')
cond4 = df[df['alevelcount'].isin([0, 5, 6])]
print(f'Count: {len(cond4)}, Recovery: {cond4["hitamt"].sum() / cond4["betamt"].sum() * 100:.2f}%')

# Condition 5: Wind 0-2m AND alevelcount in [0, 5, 6]
print('\n--- Wind 0-2m AND A-level count in [0, 5, 6] ---')
cond5 = df[(df['wind'] <= 2) & (df['alevelcount'].isin([0, 5, 6]))]
if len(cond5) > 0:
    print(f'Count: {len(cond5)}, Recovery: {cond5["hitamt"].sum() / cond5["betamt"].sum() * 100:.2f}%')

# Condition 6: com_confidence == 2
print('\n--- Computer Confidence = 2 ---')
cond6 = df[df['com_confidence'] == 2]
print(f'Count: {len(cond6)}, Recovery: {cond6["hitamt"].sum() / cond6["betamt"].sum() * 100:.2f}%')

# Condition 7: Wind 0-2m AND com_confidence == 2
print('\n--- Wind 0-2m AND Computer Confidence = 2 ---')
cond7 = df[(df['wind'] <= 2) & (df['com_confidence'] == 2)]
if len(cond7) > 0:
    print(f'Count: {len(cond7)}, Recovery: {cond7["hitamt"].sum() / cond7["betamt"].sum() * 100:.2f}%')

# Condition 8: level1 == B1
print('\n--- Level1 = B1 ---')
cond8 = df[df['level1'] == 'B1']
print(f'Count: {len(cond8)}, Recovery: {cond8["hitamt"].sum() / cond8["betamt"].sum() * 100:.2f}%')

# Condition 9: Wind 0-2m AND level1 == B1
print('\n--- Wind 0-2m AND Level1 = B1 ---')
cond9 = df[(df['wind'] <= 2) & (df['level1'] == 'B1')]
if len(cond9) > 0:
    print(f'Count: {len(cond9)}, Recovery: {cond9["hitamt"].sum() / cond9["betamt"].sum() * 100:.2f}%')

# Condition 10: bet_oddsrank in [1,2,3,4,5,6,7,12,13,16,26,27,28]
print('\n--- Profitable oddsrank [1-7,12,13,16,26-28] ---')
profitable_ranks = [1,2,3,4,5,6,7,12,13,16,26,27,28]
cond10 = df[df['bet_oddsrank'].isin(profitable_ranks)]
print(f'Count: {len(cond10)}, Recovery: {cond10["hitamt"].sum() / cond10["betamt"].sum() * 100:.2f}%')

# Condition 11: Wind 0-2m AND profitable oddsrank
print('\n--- Wind 0-2m AND Profitable oddsrank ---')
cond11 = df[(df['wind'] <= 2) & (df['bet_oddsrank'].isin(profitable_ranks))]
if len(cond11) > 0:
    print(f'Count: {len(cond11)}, Recovery: {cond11["hitamt"].sum() / cond11["betamt"].sum() * 100:.2f}%')

# Condition 12: expect_bor in 1.0-1.2 range
print('\n--- expect_bor 1.0-1.2 ---')
cond12 = df[(df['expect_bor'] >= 1.0) & (df['expect_bor'] < 1.2)]
print(f'Count: {len(cond12)}, Recovery: {cond12["hitamt"].sum() / cond12["betamt"].sum() * 100:.2f}%')

# Condition 13: Wind 0-2m AND expect_bor 1.0-1.2
print('\n--- Wind 0-2m AND expect_bor 1.0-1.2 ---')
cond13 = df[(df['wind'] <= 2) & (df['expect_bor'] >= 1.0) & (df['expect_bor'] < 1.2)]
if len(cond13) > 0:
    print(f'Count: {len(cond13)}, Recovery: {cond13["hitamt"].sum() / cond13["betamt"].sum() * 100:.2f}%')

# Best combination search
print('\n=== Best Combination Search ===')

# Wind 0-2m, bet_oddsrank <= 7, expect_bor >= 1.0
print('\n--- Wind 0-2m, oddsrank <= 7, expect_bor >= 1.0 ---')
best1 = df[(df['wind'] <= 2) & (df['bet_oddsrank'] <= 7) & (df['expect_bor'] >= 1.0)]
if len(best1) > 0:
    print(f'Count: {len(best1)}, Bet: {best1["betamt"].sum()}, Hit: {best1["hitamt"].sum()}, Profit: {best1["hitamt"].sum() - best1["betamt"].sum()}, Recovery: {best1["hitamt"].sum() / best1["betamt"].sum() * 100:.2f}%')

# Wind 0-2m, bet_oddsrank <= 5, expect_bor >= 1.0
print('\n--- Wind 0-2m, oddsrank <= 5, expect_bor >= 1.0 ---')
best2 = df[(df['wind'] <= 2) & (df['bet_oddsrank'] <= 5) & (df['expect_bor'] >= 1.0)]
if len(best2) > 0:
    print(f'Count: {len(best2)}, Bet: {best2["betamt"].sum()}, Hit: {best2["hitamt"].sum()}, Profit: {best2["hitamt"].sum() - best2["betamt"].sum()}, Recovery: {best2["hitamt"].sum() / best2["betamt"].sum() * 100:.2f}%')

# Wind 0-2m, bet_oddsrank <= 3
print('\n--- Wind 0-2m, oddsrank <= 3 ---')
best3 = df[(df['wind'] <= 2) & (df['bet_oddsrank'] <= 3)]
if len(best3) > 0:
    print(f'Count: {len(best3)}, Bet: {best3["betamt"].sum()}, Hit: {best3["hitamt"].sum()}, Profit: {best3["hitamt"].sum() - best3["betamt"].sum()}, Recovery: {best3["hitamt"].sum() / best3["betamt"].sum() * 100:.2f}%')

# Wind 0-2m, bet_oddsrank == 1
print('\n--- Wind 0-2m, oddsrank == 1 ---')
best4 = df[(df['wind'] <= 2) & (df['bet_oddsrank'] == 1)]
if len(best4) > 0:
    print(f'Count: {len(best4)}, Bet: {best4["betamt"].sum()}, Hit: {best4["hitamt"].sum()}, Profit: {best4["hitamt"].sum() - best4["betamt"].sum()}, Recovery: {best4["hitamt"].sum() / best4["betamt"].sum() * 100:.2f}%')

# Wind 0-2m, bet_oddsrank == 2
print('\n--- Wind 0-2m, oddsrank == 2 ---')
best5 = df[(df['wind'] <= 2) & (df['bet_oddsrank'] == 2)]
if len(best5) > 0:
    print(f'Count: {len(best5)}, Bet: {best5["betamt"].sum()}, Hit: {best5["hitamt"].sum()}, Profit: {best5["hitamt"].sum() - best5["betamt"].sum()}, Recovery: {best5["hitamt"].sum() / best5["betamt"].sum() * 100:.2f}%')

# alevelcount in [0,5,6] AND bet_oddsrank <= 5
print('\n--- A-level [0,5,6] AND oddsrank <= 5 ---')
best6 = df[(df['alevelcount'].isin([0, 5, 6])) & (df['bet_oddsrank'] <= 5)]
if len(best6) > 0:
    print(f'Count: {len(best6)}, Bet: {best6["betamt"].sum()}, Hit: {best6["hitamt"].sum()}, Profit: {best6["hitamt"].sum() - best6["betamt"].sum()}, Recovery: {best6["hitamt"].sum() / best6["betamt"].sum() * 100:.2f}%')

# Wind 0-2m AND alevelcount in [0,5,6] AND bet_oddsrank <= 5
print('\n--- Wind 0-2m AND A-level [0,5,6] AND oddsrank <= 5 ---')
best7 = df[(df['wind'] <= 2) & (df['alevelcount'].isin([0, 5, 6])) & (df['bet_oddsrank'] <= 5)]
if len(best7) > 0:
    print(f'Count: {len(best7)}, Bet: {best7["betamt"].sum()}, Hit: {best7["hitamt"].sum()}, Profit: {best7["hitamt"].sum() - best7["betamt"].sum()}, Recovery: {best7["hitamt"].sum() / best7["betamt"].sum() * 100:.2f}%')

# level1 == B1 AND wind <= 2 AND bet_oddsrank <= 5
print('\n--- Level1=B1 AND Wind 0-2m AND oddsrank <= 5 ---')
best8 = df[(df['level1'] == 'B1') & (df['wind'] <= 2) & (df['bet_oddsrank'] <= 5)]
if len(best8) > 0:
    print(f'Count: {len(best8)}, Bet: {best8["betamt"].sum()}, Hit: {best8["hitamt"].sum()}, Profit: {best8["hitamt"].sum() - best8["betamt"].sum()}, Recovery: {best8["hitamt"].sum() / best8["betamt"].sum() * 100:.2f}%')

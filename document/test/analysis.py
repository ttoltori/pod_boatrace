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
print('=== Date range ===')
print(f'From: {df["ymd"].min()} To: {df["ymd"].max()}')
print()

# Odds distribution
print('=== Bet Odds Distribution ===')
print(df['bet_odds'].describe())
print()

# Hit odds distribution
hit_df = df[df['hitamt'] > 0]
print('=== Hit Odds Distribution ===')
print(hit_df['bet_odds'].describe())
print()

# By venue
print('=== By Venue (jyocd) ===')
venue_stats = df.groupby('jyocd').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
venue_stats['profit'] = venue_stats['hitamt'] - venue_stats['betamt']
venue_stats['recovery'] = venue_stats['hitamt'] / venue_stats['betamt'] * 100
venue_stats['hit_rate'] = df.groupby('jyocd').apply(lambda x: (x['hitamt'] > 0).sum() / len(x) * 100)
print(venue_stats.sort_values('recovery', ascending=False).to_string())
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

# By timezone
print('=== By Timezone ===')
tz_stats = df.groupby('timezone').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
tz_stats['profit'] = tz_stats['hitamt'] - tz_stats['betamt']
tz_stats['recovery'] = tz_stats['hitamt'] / tz_stats['betamt'] * 100
print(tz_stats.sort_values('recovery', ascending=False).to_string())
print()

# By bet_oddsrank
print('=== By Bet Odds Rank ===')
oddsrank_stats = df.groupby('bet_oddsrank').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
oddsrank_stats['profit'] = oddsrank_stats['hitamt'] - oddsrank_stats['betamt']
oddsrank_stats['recovery'] = oddsrank_stats['hitamt'] / oddsrank_stats['betamt'] * 100
oddsrank_stats['hit_rate'] = df.groupby('bet_oddsrank').apply(lambda x: (x['hitamt'] > 0).sum() / len(x) * 100)
print(oddsrank_stats.to_string())
print()

# By expect_bor ranges
print('=== By Expected Value (expect_bor) Ranges ===')
df['expect_bor_range'] = pd.cut(df['expect_bor'], bins=[0, 0.5, 0.8, 1.0, 1.2, 1.5, 2.0, 100], labels=['0-0.5', '0.5-0.8', '0.8-1.0', '1.0-1.2', '1.2-1.5', '1.5-2.0', '2.0+'])
exp_stats = df.groupby('expect_bor_range').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
exp_stats['profit'] = exp_stats['hitamt'] - exp_stats['betamt']
exp_stats['recovery'] = exp_stats['hitamt'] / exp_stats['betamt'] * 100
exp_stats['hit_rate'] = df.groupby('expect_bor_range').apply(lambda x: (x['hitamt'] > 0).sum() / len(x) * 100)
print(exp_stats.to_string())
print()

# By probability ranges
print('=== By Probability Ranges ===')
df['prob_range'] = pd.cut(df['probability'], bins=[0, 0.05, 0.1, 0.15, 0.2, 0.3, 1.0], labels=['0-5%', '5-10%', '10-15%', '15-20%', '20-30%', '30%+'])
prob_stats = df.groupby('prob_range').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
prob_stats['profit'] = prob_stats['hitamt'] - prob_stats['betamt']
prob_stats['recovery'] = prob_stats['hitamt'] / prob_stats['betamt'] * 100
prob_stats['hit_rate'] = df.groupby('prob_range').apply(lambda x: (x['hitamt'] > 0).sum() / len(x) * 100)
print(prob_stats.to_string())
print()

# By A-level count
print('=== By A-Level Count ===')
alevel_stats = df.groupby('alevelcount').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
alevel_stats['profit'] = alevel_stats['hitamt'] - alevel_stats['betamt']
alevel_stats['recovery'] = alevel_stats['hitamt'] / alevel_stats['betamt'] * 100
print(alevel_stats.to_string())
print()

# By weather
print('=== By Weather ===')
weather_stats = df.groupby('weather').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
weather_stats['profit'] = weather_stats['hitamt'] - weather_stats['betamt']
weather_stats['recovery'] = weather_stats['hitamt'] / weather_stats['betamt'] * 100
print(weather_stats.sort_values('recovery', ascending=False).to_string())
print()

# By wind speed ranges
print('=== By Wind Speed Ranges ===')
df['wind_range'] = pd.cut(df['wind'], bins=[-1, 2, 4, 6, 100], labels=['0-2m', '3-4m', '5-6m', '7m+'])
wind_stats = df.groupby('wind_range').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
wind_stats['profit'] = wind_stats['hitamt'] - wind_stats['betamt']
wind_stats['recovery'] = wind_stats['hitamt'] / wind_stats['betamt'] * 100
print(wind_stats.to_string())
print()

# By wave height
print('=== By Wave Height ===')
df['wave_range'] = pd.cut(df['wave'], bins=[-1, 3, 6, 10, 100], labels=['0-3cm', '4-6cm', '7-10cm', '10cm+'])
wave_stats = df.groupby('wave_range').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
wave_stats['profit'] = wave_stats['hitamt'] - wave_stats['betamt']
wave_stats['recovery'] = wave_stats['hitamt'] / wave_stats['betamt'] * 100
print(wave_stats.to_string())
print()

# By com_confidence
print('=== By Computer Confidence ===')
conf_stats = df.groupby('com_confidence').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
conf_stats['profit'] = conf_stats['hitamt'] - conf_stats['betamt']
conf_stats['recovery'] = conf_stats['hitamt'] / conf_stats['betamt'] * 100
print(conf_stats.to_string())
print()

# By level1 (1st lane player level)
print('=== By Level1 (1st Lane Player Level) ===')
level1_stats = df.groupby('level1').agg({
    'betamt': 'sum',
    'hitamt': 'sum',
    'ymd': 'count'
}).rename(columns={'ymd': 'count'})
level1_stats['profit'] = level1_stats['hitamt'] - level1_stats['betamt']
level1_stats['recovery'] = level1_stats['hitamt'] / level1_stats['betamt'] * 100
print(level1_stats.to_string())
print()

# Combination analysis: expect_bor >= 1.0 AND probability >= 0.1
print('=== Filtered: expect_bor >= 1.0 AND probability >= 0.1 ===')
filtered = df[(df['expect_bor'] >= 1.0) & (df['probability'] >= 0.1)]
print(f'Count: {len(filtered)}')
print(f'Total bet: {filtered["betamt"].sum()}')
print(f'Total hit: {filtered["hitamt"].sum()}')
print(f'Profit: {filtered["hitamt"].sum() - filtered["betamt"].sum()}')
print(f'Recovery: {filtered["hitamt"].sum() / filtered["betamt"].sum() * 100:.2f}%')
print(f'Hit rate: {(filtered["hitamt"] > 0).sum() / len(filtered) * 100:.2f}%')
print()

# More aggressive filter
print('=== Filtered: expect_bor >= 1.2 AND probability >= 0.12 ===')
filtered2 = df[(df['expect_bor'] >= 1.2) & (df['probability'] >= 0.12)]
print(f'Count: {len(filtered2)}')
if len(filtered2) > 0:
    print(f'Total bet: {filtered2["betamt"].sum()}')
    print(f'Total hit: {filtered2["hitamt"].sum()}')
    print(f'Profit: {filtered2["hitamt"].sum() - filtered2["betamt"].sum()}')
    print(f'Recovery: {filtered2["hitamt"].sum() / filtered2["betamt"].sum() * 100:.2f}%')
    print(f'Hit rate: {(filtered2["hitamt"] > 0).sum() / len(filtered2) * 100:.2f}%')

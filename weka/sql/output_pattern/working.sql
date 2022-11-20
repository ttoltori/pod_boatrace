select 
  
from stat_ml_ptn_analyze anal
where anal.description = 'multi_6month_87_3T_132_entry_2014-2019' and pattern_name = 'jyocd_raceno_level123'
  
;
delete from stat_ml_ptn_analyze where isfinal = 'n';
select count(*) from stat_ml_ptn_final where totalrate_slope < 0 ;


-------------------------------------------------------- save pattern to file
copy (
  select * from stat_ml_ptn_filtered
) to 'C:\Dev\workspace\Oxygen\pod_boatrace_test\properties\weka_patterns.csv' with csv 
;


truncate stat_ml_ptn_filtered;

insert into stat_ml_ptn_filtered
select
  a.bettype,
  a.kumiban,
  a.pattern_name,
  a.betcount_daily,
  a.betamt,
  a.incomeamt,
  a.hitrate,
  a.incomerate,
  a.totalrate,
  a.range_oddsrank[1],
  a.range_oddsrank[2],
  a.range_odds[1],
  a.range_odds[2],
  f.pattern_value,
  f.betamt ptn_betamt,
  f.incomeamt ptn_incomeamt,
  f.hitrate ptn_hitrate,
  f.incomerate ptn_incomerate,
  f.totalrate ptn_totalrate,
  f.daily_bet_count ptn_daily_bet_count,
  f.daily_plus_rate ptn_daily_plus_rate,
  f.hitrate_slope,
  f.incomerate_slope,
  f.totalrate_slope
from stat_ml_ptn_analyze a, stat_ml_ptn_final f
where a.description = f.description and a.pattern_name = f.pattern_name 
  and (f.totalrate >= a.range_totalrate[1] and f.totalrate <= a.range_totalrate[2])
  and (f.hitrate >= a.range_hitrate[1] and f.hitrate <= a.range_hitrate[2])
  and (f.incomerate >= a.range_incomerate[1] and f.incomerate <= a.range_incomerate[2])
  and a.isfinal = 'y' and a.yyyy = '2018'
order by a.bettype, a.kumiban, a.pattern_name, f.pattern_value
;


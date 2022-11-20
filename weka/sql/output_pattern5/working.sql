20190623
select distinct description from stat_ml_result;

통합모델용(한 모델로 복수 구미방 예측) sql

-------------------------------------------------------- execute
-- 기본 결과가 수익률 100%미만일 때 기본결과에 대한 분석 (파라미터 조정을 위해)
-- 결과를 엑셀로 가져가서 분석한다.
select analyze_weka_pattern('2T', '12', '',
  array['20190101', '20190430'], 
  array[0,1000], -- oddsrank 0,1000 3,4
  array[0,1000], -- odds 0,1000
  array[0,100], -- totalrate 0,100  
  array[0,100], -- hitrate 0,100
  array[100,1000], -- incomerate 100, 1000
  'n', -- isfinal
  'n'  -- iscustom
);




delete from stat_ml_ptn_analyze 
where bettype = '2T' and kumiban = '12' and pattern_name = 'jyocd_alcount_level12' 
and isfinal = 'n' and iscustom = 'y'; 

select analyze_weka_pattern('2T', '12', 'jyocd_alcount_level12',
  array['20190101', '20190430'], 
  array[0,1000], -- oddsrank 0,1000 3,4
  array[0,1000], -- odds 0,1000
  array[0,100], -- totalrate 0,100  
  array[0,100], -- hitrate 0,100
  array[100,1000], -- incomerate 100, 1000
  'n', -- isfinal
  'n'  -- iscustom
);

select bettype, kumiban, betcount_daily, betamt, incomeamt, hitrate, incomerate, totalrate,
linear_hitrate_slope::text, linear_incomerate_slope::text, linear_incomeamt_slope::text,
range_oddsrank::text, range_odds::text, range_totalrate::text, range_hitrate::text, range_incomerate::text, description, pattern_name
from stat_ml_ptn_analyze
where bettype = '2T' and kumiban = '12' and pattern_name = 'jyocd_alcount_level12'
and isfinal = 'n' and iscustom = 'n'
order by updatetime desc, bettype, kumiban;


-------------------------------------------------------- save pattern to file
copy (
  select * from stat_ml_ptn_filtered
) to 'C:\Dev\workspace\Oxygen\pod_boatrace_test\properties\weka_patterns.csv' with csv 
;

-------------------------------------------------------- create pattern table
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
  a.linear_hitrate_slope,
  a.linear_incomerate_slope,
  a.linear_incomeamt_slope,
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
  f.days_bet ptn_days_bet,
  f.days_plus_rate ptn_days_plus_rate,
  f.linear_hitrate_slope ptn_hitrate_slope,
  f.linear_incomerate_slope ptn_incomerate_slope,
  f.linear_incomeamt_slope ptn_incomeamt_slope
from stat_ml_ptn_analyze a, stat_ml_ptn_final f
where a.description = f.description and a.pattern_name = f.pattern_name 
  and (f.totalrate >= a.range_totalrate[1] and f.totalrate <= a.range_totalrate[2])
  and (f.hitrate >= a.range_hitrate[1] and f.hitrate <= a.range_hitrate[2])
  and (f.incomerate >= a.range_incomerate[1] and f.incomerate <= a.range_incomerate[2])
  and a.isfinal = 'y'
order by a.bettype, a.kumiban, a.pattern_name, f.pattern_value
;


-------------------------------------------------------- execute
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



-------------------------------------------------------- create procedure
-- ！！！ output_weka_patternが必ず事前に実行されていること
DROP FUNCTION IF EXISTS analyze_weka_pattern(VARCHAR(400), VARCHAR(400), VARCHAR(1), VARCHAR(8)[], int[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], VARCHAR(1));
CREATE OR REPLACE FUNCTION analyze_weka_pattern(
paramDescription VARCHAR(400),
paramPatternName VARCHAR(400),
paramIsMonthly VARCHAR(1),
paramArrYmd VARCHAR(8)[],
paramArrOddsRank int[],
paramArrOdds numeric(7,2)[],
paramArrTotalRate numeric(7,2)[],
paramArrHitRate numeric(7,2)[],
paramArrIncomeRate numeric(7,2)[], 
paramIsFinal VARCHAR(1) 
) RETURNS SETOF stat_ml_ptn_analyze AS $$
  DECLARE
    rec RECORD;
    bettype VARCHAR(2);
    kumiban VARCHAR(4);
    modelno VARCHAR(4);
    modelattr VARCHAR(40);
    mm VARCHAR(40);
  BEGIN
  
  delete from stat_ml_ptn_analyze
  where description = paramDescription and pattern_name = paramPatternName 
    and ismonthly = paramIsMonthly
    and range_oddsrank[1] = paramArrOddsRank[1] and range_oddsrank[2] = paramArrOddsRank[2]
    and range_odds[1] = paramArrOdds[1] and range_odds[2] = paramArrOdds[2]
    and range_totalrate[1] = paramArrTotalRate[1] and range_totalrate[2] = paramArrTotalRate[2]
    and range_hitrate[1] = paramArrHitRate[1] and range_hitrate[2] = paramArrHitRate[2]
    and range_incomerate[1] = paramArrIncomeRate[1] and range_incomerate[2] = paramArrIncomeRate[2]
    and isfinal = paramIsFinal
  ;
  
  bettype := split_part(paramDescription, '_', 4);
  kumiban := split_part(paramDescription, '_', 5);
  modelno := split_part(paramDescription, '_', 3);
  modelattr := split_part(paramDescription, '_', 6);
  if paramIsMonthly = 'y' then
    mm := 'substring(stat.ymd from 5 for 2)';
  else 
    mm := '0';
  end if;
  
  EXECUTE format ('
    insert into stat_ml_ptn_analyze
    select
      ''%s'' bettype,
      ''%s'' kumiban,
      ''%s'' modelno,
      ''%s'' modelattr,
      ''%s'' pattern_name,
      yyyy,
      mm,
      (case when yyyy <= ''2018'' then (betcnt / 365) else (betcnt / 90) end ) betcount_daily,
      betamt, 
      (hitamt - betamt) incomeamt, 
      cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(7,2)) hitrate, 
      cast( (cast(hitamt as float)/ cast(betamt as float) *100) as numeric(7,2)) incomerate, 
      totalrate,
      ''%s'' range_oddsrank,
      ''%s'' range_odds,
      ''%s'' range_totalrate,
      ''%s'' range_hitrate,
      ''%s'' range_incomerate,
      ''%s'' description,
      ''%s'' isfinal,
      ''%s'' ismonthly 
    from
    (
        select
          substring(stat.ymd from 1 for 4) yyyy,
          %s mm,
          sum(1) betcnt, sum(hity) hitcnt, sum(stat.betamt) betamt, sum (stat.hitamt) hitamt, avg(final.totalrate) totalrate
        from stat_ml_result_ptn stat, stat_ml_ptn_final final
        where stat.ymd >= ''%s'' and stat.ymd <= ''%s''
          and stat.description = ''%s'' and stat.pattern_name = ''%s''
          and stat.bet_oddsrank >= %s and stat.bet_oddsrank <= %s
          and stat.bet_odds >= %s and stat.bet_odds <= %s
          and stat.description = final.description and stat.pattern_name = final.pattern_name and stat.pattern_value = final.pattern_value
          and totalrate >= %s and totalrate <= %s 
          and hitrate >= %s and hitrate <= %s
          and incomerate >= %s and incomerate <= %s
        group by yyyy, mm
    ) tmp
  ' , bettype, kumiban, modelno, modelattr, paramPatternName
    , paramArrOddsRank, paramArrOdds, paramArrTotalRate, paramArrHitRate, paramArrIncomeRate 
    , paramDescription, paramIsFinal, paramIsMonthly, mm, paramArrYmd[1], paramArrYmd[2]
    , paramDescription, paramPatternName 
    , paramArrOddsRank[1], paramArrOddsRank[2] 
    , paramArrOdds[1], paramArrOdds[2]
    , paramArrTotalRate[1], paramArrTotalRate[2]
    , paramArrHitRate[1], paramArrHitRate[2]
    , paramArrIncomeRate[1], paramArrIncomeRate[2]
  );
  
  return query 
    select * from stat_ml_ptn_analyze
    where description = paramDescription and pattern_name = paramPatternName;
  
  END;
$$ LANGUAGE plpgsql;

-------------------------------------------------------- create procedure
DROP FUNCTION IF EXISTS output_weka_pattern(VARCHAR(400), VARCHAR(400), VARCHAR(400), VARCHAR(8), VARCHAR(8));
CREATE OR REPLACE FUNCTION output_weka_pattern(
paramDescription VARCHAR(400),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400),
paramFromYmd VARCHAR(8),
paramToYmd VARCHAR(8)
)
RETURNS VOID AS $$
  BEGIN
	truncate stat_ml_result_ptn;
	-- delete from stat_ml_result_ptn where description = paramDescription; -- 20190501 stat_ml_resultと同一のCRUDにする。 and pattern_name = paramPatternName;
	truncate stat_ml_ptn_daily;
	truncate stat_ml_ptn_monthly;
	truncate stat_ml_ptn_yearly;
	truncate stat_ml_ptn_totally;

    raise info 'insert stat_ml_result_ptn';
    EXECUTE format ('
		insert into stat_ml_result_ptn
		select
		   stat.*,
		   ''%s'' pattern_name,
		   %s pattern_value
		from stat_ml_result stat, rec_race race, stat_race srace
		where race.ymd = stat.ymd and race.jyocd = stat.jyocd and race.raceno = stat.raceno
		  and race.ymd = srace.ymd and race.jyocd = srace.jyocd and race.raceno = srace.raceno
			and description = ''%s''
	', paramPatternName, paramPatternValue, paramDescription);
--			and race.ymd >= ''%s'' and race.ymd <= ''%s'' 

    raise info 'create temporary table (daily summary)';
	-- create temporary table (daily summary)
	drop table if exists tmp_stat_ml_ptn_daily;
	create table tmp_stat_ml_ptn_daily as
	select 
		description, 
		ymd,
		bettype, 
		bet_kumiban kumiban, 
		pattern_name, 
		pattern_value, 
		sum(1) betcnt, 
		sum(hity) hitcnt, 
		sum(100) betamt, 
		sum ( case hity when 1 then (result_odds * 100) else 0 end ) hitamt 
	from stat_ml_result_ptn stat
	where stat.ymd >= paramFromYmd and stat.ymd <= paramToYmd and stat.description = paramDescription 
	group by description, ymd, bettype, kumiban, pattern_name, pattern_value
	order by description, ymd, bettype, kumiban, pattern_name, pattern_value;
	
    raise info 'insert stat_ml_ptn_daily';
	------- insert stat_ml_ptn_daily
	insert into stat_ml_ptn_daily
	select
	  tmp1.description, tmp1.ymd, tmp1.bettype, tmp1.kumiban, tmp1.pattern_name, tmp1.pattern_value, 
	  tmp1.betcnt, tmp1.hitcnt, tmp1.betamt, tmp1.hitamt, tmp1.incomeamt, 
	  cast( (cast(betcnt as float)/ cast(betcnt_sum as float) *100) as numeric(7,3)) betrate,  
	  tmp1.hitrate, tmp1.incomerate, 
	  cast( ((tmp1.hitrate * tmp1.incomerate) * (cast(betcnt as float)/ cast(betcnt_sum as float) * 100) / 100) as numeric(7,2)) totalrate -- totalrate_slopeを求めるために必要
	from 
	  (
	    select
	      description, ymd, bettype, kumiban, pattern_name, pattern_value, betcnt, hitcnt, betamt, hitamt, 
	      (hitamt - betamt) incomeamt,
	      cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(7,2)) hitrate,
	      cast( (cast(hitamt as float)/ cast(betamt as float) *100) as numeric(7,2)) incomerate
	    from tmp_stat_ml_ptn_daily
	  ) tmp1, 
	  (
	    select
	      description, ymd, bettype, kumiban, pattern_name, sum(betcnt) betcnt_sum
	    from tmp_stat_ml_ptn_daily
	    group by description, ymd, bettype, kumiban, pattern_name
	  ) tmp2
	where tmp1.description = tmp2.description
	  and tmp1.ymd = tmp2.ymd
	  and tmp1.bettype = tmp2.bettype 
	  and tmp1.kumiban = tmp2.kumiban
	  and tmp1.pattern_name = tmp2.pattern_name
	order by description, ymd, bettype, kumiban, pattern_name, pattern_value;


    raise info 'create temporary table (monthly summary)';
	-- create temporary table (monthly summary)
	drop table if exists tmp_stat_ml_ptn_monthly;
	create table tmp_stat_ml_ptn_monthly as
	select 
		description, 
		substring(ymd from 1 for 4) yyyy, 
		substring(ymd from 5 for 2) mm, 
		bettype, 
		bet_kumiban kumiban, 
		pattern_name, 
		pattern_value, 
		sum(1) betcnt, 
		sum(hity) hitcnt, 
		sum(100) betamt, 
		sum ( case hity when 1 then (result_odds * 100) else 0 end ) hitamt 
	from stat_ml_result_ptn stat
	where stat.ymd >= paramFromYmd and stat.ymd <= paramToYmd and stat.description = paramDescription 
	group by description, yyyy, mm, bettype, kumiban, pattern_name, pattern_value
	order by description, yyyy, mm, bettype, kumiban, pattern_name, pattern_value;
	
    raise info 'insert stat_ml_ptn_monthly';
	------- insert stat_ml_ptn_monthly
	insert into stat_ml_ptn_monthly
	select
	  tmp1.description, tmp1.yyyy, tmp1.mm, tmp1.bettype, tmp1.kumiban, tmp1.pattern_name, tmp1.pattern_value, 
	  tmp1.betcnt, tmp1.hitcnt, tmp1.betamt, tmp1.hitamt, tmp1.incomeamt, 
	  cast( (cast(betcnt as float)/ cast(betcnt_sum as float) *100) as numeric(7,3)) betrate,  
	  tmp1.hitrate, tmp1.incomerate
	from 
	  (
	    select
	      description, yyyy, mm, bettype, kumiban, pattern_name, pattern_value, betcnt, hitcnt, betamt, hitamt, 
	      (hitamt - betamt) incomeamt,
	      cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(7,2)) hitrate,
	      cast( (cast(hitamt as float)/ cast(betamt as float) *100) as numeric(7,2)) incomerate
	    from tmp_stat_ml_ptn_monthly
	  ) tmp1, 
	  (
	    select
	      description, yyyy, mm, bettype, kumiban, pattern_name, sum(betcnt) betcnt_sum
	    from tmp_stat_ml_ptn_monthly
	    group by description, yyyy, mm, bettype, kumiban, pattern_name
	  ) tmp2
	where tmp1.description = tmp2.description
	  and tmp1.yyyy = tmp2.yyyy 
	  and tmp1.mm = tmp2.mm
	  and tmp1.bettype = tmp2.bettype 
	  and tmp1.kumiban = tmp2.kumiban
	  and tmp1.pattern_name = tmp2.pattern_name
	order by description, yyyy, mm, bettype, kumiban, pattern_name, pattern_value;

    raise info 'create temporary table (yearly summary)';
	-- create temporary table (yearly summary)
	drop table if exists tmp_stat_ml_ptn_yearly;
	create table tmp_stat_ml_ptn_yearly as
	select 
		description, 
		substring(ymd from 1 for 4) yyyy, 
		bettype, 
		bet_kumiban kumiban, 
		pattern_name, 
		pattern_value, 
		sum(1) betcnt, 
		sum(hity) hitcnt, 
		sum(100) betamt, 
		sum ( case hity when 1 then (result_odds * 100) else 0 end ) hitamt 
	from stat_ml_result_ptn stat
	where stat.ymd >= paramFromYmd and stat.ymd <= paramToYmd and stat.description = paramDescription 
	group by description, yyyy, bettype, kumiban, pattern_name, pattern_value
	order by description, yyyy, bettype, kumiban, pattern_name, pattern_value;
	
    raise info 'insert stat_ml_ptn_yearly';
	------- insert stat_ml_ptn_yearly
	insert into stat_ml_ptn_yearly
	select
	  tmp1.description, tmp1.yyyy, tmp1.bettype, tmp1.kumiban, tmp1.pattern_name, tmp1.pattern_value, 
	  tmp1.betcnt, tmp1.hitcnt, tmp1.betamt, tmp1.hitamt, tmp1.incomeamt, 
	  cast( (cast(betcnt as float)/ cast(betcnt_sum as float) *100) as numeric(7,3)) betrate,  
	  tmp1.hitrate, tmp1.incomerate
	from 
	  (
	    select
	      description, yyyy, bettype, kumiban, pattern_name, pattern_value, betcnt, hitcnt, betamt, hitamt, 
	      (hitamt - betamt) incomeamt,
	      cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(7,2)) hitrate,
	      cast( (cast(hitamt as float)/ cast(betamt as float) *100) as numeric(7,2)) incomerate
	    from tmp_stat_ml_ptn_yearly
	  ) tmp1, 
	  (
	    select
	      description, yyyy, bettype, kumiban, pattern_name, sum(betcnt) betcnt_sum
	    from tmp_stat_ml_ptn_yearly
	    group by description, yyyy, bettype, kumiban, pattern_name
	  ) tmp2
	where tmp1.description = tmp2.description
	  and tmp1.yyyy = tmp2.yyyy 
	  and tmp1.bettype = tmp2.bettype 
	  and tmp1.kumiban = tmp2.kumiban
	  and tmp1.pattern_name = tmp2.pattern_name
	order by description, yyyy, bettype, kumiban, pattern_name, pattern_value;

    raise info 'create temporary table (totally summary)';
	-- create temporary table (totally summary)
	drop table if exists tmp_stat_ml_ptn_totally;
	create table tmp_stat_ml_ptn_totally as
	select 
		description, 
		bettype, 
		bet_kumiban kumiban, 
		pattern_name, 
		pattern_value, 
		sum(1) betcnt, 
		sum(hity) hitcnt, 
		sum(100) betamt, 
		sum ( case hity when 1 then (result_odds * 100) else 0 end ) hitamt 
	from stat_ml_result_ptn stat
	where stat.ymd >= paramFromYmd and stat.ymd <= paramToYmd and stat.description = paramDescription 
	group by description, bettype, kumiban, pattern_name, pattern_value
	order by description, bettype, kumiban, pattern_name, pattern_value;
	
	------- delete stat_ml_ptn_totally
	delete from stat_ml_ptn_totally where description = paramDescription and pattern_name = paramPatternName;

    raise info 'insert stat_ml_ptn_totally';
	------- insert stat_ml_ptn_totally
	insert into stat_ml_ptn_totally
	select
	  tmp1.description, tmp1.bettype, tmp1.kumiban, tmp1.pattern_name, tmp1.pattern_value, 
	  tmp1.betcnt, tmp1.hitcnt, tmp1.betamt, tmp1.hitamt, tmp1.incomeamt, 
	  cast( (cast(betcnt as float)/ cast(betcnt_sum as float) *100) as numeric(7,3)) betrate,  
	  tmp1.hitrate, tmp1.incomerate,
	  -- cast( ((tmp1.hitrate * tmp1.incomerate) / 100) as numeric(7,2)) totalrate
	  cast( ((tmp1.hitrate * tmp1.incomerate * ((cast(betcnt as float)/ cast(betcnt_sum as float) * 100))) / 100) as numeric(7,2)) totalrate
	from 
	  (
	    select
	      description, bettype, kumiban, pattern_name, pattern_value, betcnt, hitcnt, betamt, hitamt, 
	      (hitamt - betamt) incomeamt,
	      cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(7,2)) hitrate,
	      cast( (cast(hitamt as float)/ cast(betamt as float) *100) as numeric(7,2)) incomerate
	    from tmp_stat_ml_ptn_totally
	    -- where (hitamt - betamt) > 0
	  ) tmp1, 
	  (
	    select
	      description, bettype, kumiban, pattern_name, sum(betcnt) betcnt_sum
	    from tmp_stat_ml_ptn_totally
	    -- where (hitamt - betamt) > 0  2019/4/28 comment out이 안되어있어서 추가
	    group by description, bettype, kumiban, pattern_name
	  ) tmp2
	where tmp1.description = tmp2.description
	  and tmp1.bettype = tmp2.bettype 
	  and tmp1.kumiban = tmp2.kumiban
	  and tmp1.pattern_name = tmp2.pattern_name
	  and tmp1.incomerate >= 100  -- 수익률 100%이상인 패턴만 추출한다.
	order by description, bettype, kumiban, pattern_name, pattern_value;

	------- delete stat_ml_final
	delete from stat_ml_ptn_final where description = paramDescription and pattern_name = paramPatternName;

    raise info 'insert stat_ml_final';
	------- insert stat_ml_final
	insert into stat_ml_ptn_final
	select
	  totally.description, 
	  totally.bettype, 
	  totally.kumiban, 
	  totally.pattern_name, 
	  totally.pattern_value, 
	  betcnt, 
	  hitcnt, 
	  betamt, 
	  hitamt, 
	  incomeamt, 
	  betrate, 
	  hitrate,
	  incomerate,
	  totalrate,
	  0, --betrate_slope,
	  0, --hitrate_slope,
	  0, --incomerate_slope,
	  0, --totalrate_slope,
	  daily_bet_count, 
	  daily_plus_count, 
	  daily_plus_rate,
	  monthly_bet_count, 
	  monthly_plus_count, 
	  monthly_plus_rate,
  	  stable_years_count 
	from 
	  (
	    select
	      description, bettype, kumiban, pattern_name, pattern_value, betcnt, hitcnt, betamt, hitamt, incomeamt,
	      betrate, hitrate, incomerate, totalrate
	    from stat_ml_ptn_totally
	  ) totally, 
	  (
	    select
	      description, bettype, kumiban, pattern_name, sum(betcnt) betcnt_sum
	    from stat_ml_ptn_totally
	    group by description, bettype, kumiban, pattern_name
	  ) totally_betsum,
	  (
	    select
	      description, bettype, kumiban, pattern_name, pattern_value, count(yyyy) stable_years_count 
	    from stat_ml_ptn_yearly
	    where incomeamt > 0
	    group by description, bettype, kumiban, pattern_name, pattern_value
	  ) yearly_count,
--	  (
--	    select
--	      description, bettype, kumiban, pattern_name, pattern_value, 
--	      regr_slope(betrate, ymd::integer) betrate_slope,
--	      regr_slope(hitrate, ymd::integer) hitrate_slope,
--	      regr_slope(incomerate, ymd::integer) incomerate_slope,
--	      regr_slope(totalrate, ymd::integer) totalrate_slope 
--	    from stat_ml_ptn_daily
--	    group by description, bettype, kumiban, pattern_name, pattern_value
--	  ) daily_stat,
      (
        select 
          description, bettype, kumiban, pattern_name, pattern_value, 
          daily_bet_count, 
          daily_plus_count, 
          cast( (cast(daily_plus_count as float)/ cast(daily_bet_count as float) * 100) as numeric(7,2)) daily_plus_rate
        from 
        (
          select
            description, bettype, kumiban, pattern_name, pattern_value, 
            sum(1) daily_bet_count,
            sum( case when incomeamt > 0 then 1 else 0 end) daily_plus_count 
          from stat_ml_ptn_daily
          group by description, bettype, kumiban, pattern_name, pattern_value
        ) tmp
      ) daily_plus_cnt,
      (
        select 
          description, bettype, kumiban, pattern_name, pattern_value, 
          monthly_bet_count, 
          monthly_plus_count, 
          cast( (cast(monthly_plus_count as float)/ cast(monthly_bet_count as float) * 100) as numeric(7,2)) monthly_plus_rate
        from 
        (
          select
            description, bettype, kumiban, pattern_name, pattern_value, 
            sum(1) monthly_bet_count,
            sum( case when incomeamt > 0 then 1 else 0 end) monthly_plus_count 
          from stat_ml_ptn_monthly
          group by description, bettype, kumiban, pattern_name, pattern_value
        ) tmp
      ) monthly_plus_cnt
	where 
	  totally.description = totally_betsum.description 
	  and totally.bettype = totally_betsum.bettype and totally.kumiban = totally_betsum.kumiban 
	  and totally.pattern_name = totally_betsum.pattern_name 
	  and totally.description = yearly_count.description 
	  and totally.bettype = yearly_count.bettype and totally.kumiban = yearly_count.kumiban 
	  and totally.pattern_name = yearly_count.pattern_name 
	  and totally.pattern_value = yearly_count.pattern_value 
--	  and totally.description = daily_stat.description 
--	  and totally.bettype = daily_stat.bettype and totally.kumiban = daily_stat.kumiban 
--	  and totally.pattern_name = daily_stat.pattern_name 
--	  and totally.pattern_value = daily_stat.pattern_value 
	  and totally.description = daily_plus_cnt.description 
	  and totally.bettype = daily_plus_cnt.bettype and totally.kumiban = daily_plus_cnt.kumiban 
	  and totally.pattern_name = daily_plus_cnt.pattern_name 
	  and totally.pattern_value = daily_plus_cnt.pattern_value 
	  and totally.description = monthly_plus_cnt.description 
	  and totally.bettype = monthly_plus_cnt.bettype and totally.kumiban = monthly_plus_cnt.kumiban 
	  and totally.pattern_name = monthly_plus_cnt.pattern_name 
	  and totally.pattern_value = monthly_plus_cnt.pattern_value 
	order by description, bettype, kumiban, pattern_name, pattern_value;

  END;
$$ LANGUAGE plpgsql;


-------------------------------------------------------- working -----------------------------------------------

select * from stat_ml_ptn_final
where stable_years_count = 2 
  and betrate >= 0.2
  and hitrate >= 63
  and incomerate >= 10.5
order by description, bettype, kumiban, pattern_name, pattern_value
;

----------------- DDL -----------------------------------
drop table if exists stat_ml_result_ptn;
create table stat_ml_result_ptn (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
bet_odds numeric(7,2),
bet_oddsrank int,
result_kumiban varchar(4),
result_odds numeric(7,2),
result_oddsrank int,
hity int, 
hitn int, 
betamt int,
hitamt int,
pattern_name varchar(400),
pattern_value varchar(400) 
);
drop index if exists indexes_stat_ml_result_ptn;
create index indexes_stat_ml_result_ptn on stat_ml_result_ptn (description, ymd, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_daily;
create table stat_ml_ptn_daily (
description varchar(200),
ymd varchar(8),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2), 
totalrate numeric(7,2) -- totalrate_slopeを求めるために必要
);
create index indexes_stat_ml_ptn_daily on stat_ml_ptn_daily (description, bettype, kumiban, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_monthly;
create table stat_ml_ptn_monthly (
description varchar(200),
yyyy varchar(4),
mm varchar(2),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2)
);
create index indexes_stat_ml_ptn_monthly on stat_ml_ptn_monthly (description, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_yearly;
create table stat_ml_ptn_yearly (
description varchar(200),
yyyy varchar(4),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2)
);
create index indexes_stat_ml_ptn_yearly on stat_ml_ptn_yearly (description, bettype, kumiban, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_totally;
create table stat_ml_ptn_totally (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2)
);
create index indexes_stat_ml_ptn_totally on stat_ml_ptn_totally (description, bettype, kumiban, pattern_name, pattern_value);


delete from stat_ml_ptn_final where description = '' and bettype = '' and kumiban = '' and patern_name=''
drop table if exists stat_ml_ptn_final;
create table stat_ml_ptn_final (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2),
betrate_slope numeric(7,3),
hitrate_slope numeric(7,3),
incomerate_slope numeric(7,3),
totalrate_slope numeric(7,3),
daily_bet_count int,
daily_plus_count int,
daily_plus_rate numeric(7,2),
monthly_bet_count int,
monthly_plus_count int,
monthly_plus_rate numeric(7,2),
stable_years_count int 
);
drop index if exists indexes_stat_ml_ptn_final;
create index indexes_stat_ml_ptn_final on stat_ml_ptn_final (description, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_analyze cascade;
create table stat_ml_ptn_analyze2 (
bettype varchar(2),
kumiban varchar(4),
modelno varchar(4),
modelattr varchar(40),
pattern_name varchar(40),
pattern_value varchar(400),
yyyy varchar(4),
mm varchar(2),
betcount_daily int,
betamt int,
incomeamt int,
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2),
range_oddsrank int[],
range_odds numeric(7,2)[],
range_totalrate numeric(7,2)[],
range_hitrate numeric(7,2)[],
range_incomerate numeric(7,2)[],
description varchar(400),
isfinal varchar(1),
ismonthly varchar(1),
updatetime timestamp default CURRENT_TIMESTAMP
);
create index indexes_stat_ml_ptn_analyze on stat_ml_ptn_analyze (bettype, kumiban, modelno, modelattr, pattern_name);

drop table if exists stat_ml_ptn_filtered cascade;
create table stat_ml_ptn_filtered (
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(40),
betcount_daily int,
betamt int,
incomeamt int,
hitrate double precision,
incomerate double precision,
totalrate double precision,
min_oddsrank int,
max_oddsrank int,
min_odds  double precision,
max_odds  double precision,
pattern_value varchar(400),
ptn_betamt int,
ptn_incomeamt int,
ptn_hitrate double precision,
ptn_incomerate double precision,
ptn_totalrate double precision,
ptn_daily_bet_count int,
ptn_daily_plus_rate double precision,
hitrate_slope double precision,
incomerate_slope double precision,
totalrate_slope double precision
);
create index indexes_stat_ml_ptn_filtered on stat_ml_ptn_filtered (bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_result_weka;
create table stat_ml_result_weka (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
bet_odds numeric(7,2),
bet_oddsrank int,
result_kumiban varchar(4),
result_odds numeric(7,2),
result_oddsrank int,
hity int, 
hitn int, 
betamt int,
hitamt int,
pattern_name_count int,
pattern_value_count int 
);
create index indexes_stat_ml_result_weka on stat_ml_result_weka (ymd, jyocd, raceno, description);

drop table if exists stat_ml_result_wekaptn;
create table stat_ml_result_wekaptn (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
pattern_name varchar(40),
pattern_value varchar(400)
);
create index indexes_stat_ml_result_wekaptn on stat_ml_result_wekaptn (ymd, jyocd, raceno, description,bettype,bet_kumiban );

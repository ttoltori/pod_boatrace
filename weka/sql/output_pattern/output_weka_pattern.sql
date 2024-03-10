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
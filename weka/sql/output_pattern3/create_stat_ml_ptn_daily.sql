DROP FUNCTION IF EXISTS create_stat_ml_ptn_daily (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400));
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_daily (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400)
) RETURNS VOID AS $$
DECLARE
	params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    raise info 'delete stat_ml_ptn_daily';
	delete from stat_ml_ptn_daily
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;

    raise info 'create temporary table (daily summary)';
	-- create temporary table (daily summary)
	drop table if exists tmp_stat_ml_ptn_daily;
	create table tmp_stat_ml_ptn_daily as
	select 
		stat.description, 
		stat.ymd,
		stat.bettype, 
		stat.bet_kumiban kumiban, 
		stat.pattern_name, 
		stat.pattern_value, 
		sum(1) betcnt, 
		sum(stat.hity) hitcnt, 
		sum(100) betamt, 
		sum ( case stat.hity when 1 then (stat.result_odds * 100) else 0 end ) hitamt 
	from stat_ml_ptn_linear linear, stat_ml_result_ptn stat
	where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban and linear.pattern_name = paramPatternName
	  and stat.description = linear.description and stat.bettype = linear.bettype and stat.bet_kumiban = linear.kumiban
	  and stat.pattern_name = linear.pattern_name and stat.pattern_value = linear.pattern_value
	  and stat.ymd = linear.ymd and stat.jyocd = linear.jyocd and stat.raceno = linear.raceno 
	group by stat.description, stat.ymd, stat.bettype, stat.bet_kumiban, stat.pattern_name, stat.pattern_value
	order by description, ymd, bettype, kumiban, pattern_name, pattern_value;
	
    raise info 'insert stat_ml_ptn_daily %', params;
	------- insert stat_ml_ptn_daily
	insert into stat_ml_ptn_daily
	select
	  tmp1.description, tmp1.ymd, tmp1.bettype, tmp1.kumiban, tmp1.pattern_name, tmp1.pattern_value, 
	  tmp1.betcnt, tmp1.hitcnt, tmp1.betamt, tmp1.hitamt, tmp1.incomeamt, 
	  cast( (cast(betcnt as float)/ cast(betcnt_sum as float) *100) as numeric(7,3)) betrate,  
	  tmp1.hitrate, tmp1.incomerate, 
	  cast( ((tmp1.hitrate * tmp1.incomerate) * (cast(betcnt as float)/ cast(betcnt_sum as float) * 100) / 100) as numeric(10,2)) totalrate -- totalrate_slopeを求めるために必要
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
END
$$ LANGUAGE plpgsql;

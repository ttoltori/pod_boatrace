DROP FUNCTION IF EXISTS create_stat_ml_ptn_final (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), int, VARCHAR(4));
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_final (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramMinPtnCnt int, -- 기록보존용,
paramYYYY VARCHAR(4) -- 년도
) RETURNS VOID AS $$
DECLARE
	params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName || '_' ||  paramMinPtnCnt;
    
    raise info 'delete stmp_stat_ml_ptn_final';
	delete from stmp_stat_ml_ptn_final
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban  
	and pattern_name = paramPatternName and yyyy = paramYYYY; -- 20190818 yyyy추가
	-- and betcnt_limit = paramMinPtnCnt; 
	-- 이게 누적되면 analyze에도 넘겨줘서 같이 누적되어야 한다.
	-- 너무 복잡해지므로 일단 보류

    raise info 'insert stmp_stat_ml_ptn_final %', params;
	------- insert stat_ml_final
	insert into stmp_stat_ml_ptn_final
	select
	  paramYYYY, -- 20190818 년도를 추가 (manual finalize를 위해)
	  totally.description, 
	  totally.bettype, 
	  totally.kumiban, 
	  totally.pattern_name, 
	  totally.pattern_value, 
	  totally.betcnt, 
	  totally.hitcnt, 
	  totally.betamt, 
	  totally.hitamt, 
	  totally.incomeamt, 
	  totally.betrate, 
	  totally.hitrate,
	  totally.incomerate,
	  totally.totalrate,
	  hitamt_bias_rate,
	  hit_bet_slope_rate,
	  minus_days,
	  plus_days,
	  minus_changed_count,
	  plus_changed_count,
	  minus_days_rate,
	  linear_incomeamt_slope,
	  recover_success_rate,
	  recover_fail_rate,
	  recover_plus_rate,
	  recover_minus_rate,
	  hitamt_sum_under,
	  hitamt_sum_over,
	  hitamt_mod,
	  hitamt_percent,
	  0, -- daily_bet_count, 
	  0, -- daily_plus_count, 
	  0, -- daily_plus_rate
	  paramMinPtnCnt
	from stat_ml_ptn_totally totally, stat_ml_ptn_metric metric
	where totally.description = paramDescription and totally.bettype = paramBettype and totally.kumiban = paramKumiban  and totally.pattern_name = paramPatternName
	  and totally.description = metric.description 
	  and totally.bettype = metric.bettype and totally.kumiban = metric.kumiban 
	  and totally.pattern_name = metric.pattern_name 
	  and totally.pattern_value = metric.pattern_value 
	order by description, bettype, kumiban, pattern_name, pattern_value;

	-- 다음번 output_weka_pattern.sql을 실행할때는 이번 실행시 수익률 95%이상의 패턴들만을 대상으로 한다. 
	-- delete from stmp_ptn_names_over95;
	
	-- insert into stmp_ptn_names_over95
	-- select 
	--   description, bettype, kumiban, pattern_name, avg(incomerate) incomerate
	-- from stmp_stat_ml_ptn_final
	-- where incomerate > 95
	-- group by description, bettype, kumiban, pattern_name
	-- order by bettype, kumiban, pattern_name
	-- ;
	
	-- delete from stmp_ptn_values_over95;
	
	-- insert into stmp_ptn_values_over95
	-- select 
	--   description, bettype, kumiban, pattern_name, pattern_value, incomerate
	-- from stmp_stat_ml_ptn_final
	-- where incomerate > 95
	-- group by description, bettype, kumiban, pattern_name, pattern_value, incomerate
	-- order by bettype, kumiban, pattern_name, pattern_value
	-- ;
END
$$ LANGUAGE plpgsql;

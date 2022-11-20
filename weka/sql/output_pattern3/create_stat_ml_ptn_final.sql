DROP FUNCTION IF EXISTS create_stat_ml_ptn_final (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400));
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_final (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400)
) RETURNS VOID AS $$
DECLARE
	params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    raise info 'delete stat_ml_ptn_final';
	delete from stat_ml_ptn_final
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban  and pattern_name = paramPatternName;

    raise info 'insert stat_ml_ptn_final %', params;
	------- insert stat_ml_final
	insert into stat_ml_ptn_final
	select
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
	  linear_betamt_slope,
	  linear_hitamt_slope,
	  linear_incomeamt_slope,
	  linear_hitrate_slope,
	  linear_incomerate_slope,
	  hitamt_sum_under,
	  hitamt_sum_over,
	  hitamt_mod,
	  hitamt_percent,
	  0, -- daily_bet_count, 
	  0, -- daily_plus_count, 
	  0 -- daily_plus_rate
	from stat_ml_ptn_totally totally, stat_ml_ptn_metric metric
	where 
	  totally.description = metric.description 
	  and totally.bettype = metric.bettype and totally.kumiban = metric.kumiban 
	  and totally.pattern_name = metric.pattern_name 
	  and totally.pattern_value = metric.pattern_value 
	order by description, bettype, kumiban, pattern_name, pattern_value;

END
$$ LANGUAGE plpgsql;

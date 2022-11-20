DROP FUNCTION IF EXISTS create_stat_ml_ptn_metric (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), float);
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_metric (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPercentile float
) RETURNS VOID AS $$
DECLARE
	params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName || '_' ||  paramPercentile;
    
    raise info 'delete stat_ml_ptn_metric';
	delete from stat_ml_ptn_metric
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;

	insert into stat_ml_ptn_metric
    select 
      slope.*, 
      bias.hitamt_mod, bias.hitamt_percnt, hitamt_sum_over, hitamt_sum_under,
      cast( cast(hitamt_sum_over as float) / cast(hitamt_sum_under as float) as numeric(19,2)) hitamt_bias_rate,
      cast( (linear_hitamt_slope / linear_betamt_slope) as numeric(19,2)) hit_bet_slope_rate
    from
    (
	    select 
	        description, bettype, kumiban, pattern_name, pattern_value,
			cast(regr_slope(betamt, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_betamt_slope,
			cast(regr_slope(hitamt, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_hitamt_slope,
			cast(regr_slope(incomeamt, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_incomeamt_slope,
			cast(regr_slope(hitrate, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_hitrate_slope,
			cast(regr_slope(incomerate, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_incomerate_slope
	    from stat_ml_ptn_linear linear
	    where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban and linear.pattern_name = paramPatternName
	    group by description, bettype, kumiban, pattern_name, pattern_value
    ) slope, 
    (
    	select
   		  linear.description, linear.bettype, linear.kumiban, linear.pattern_name, linear.pattern_value,
   		  tmp.hitamt_mod,
   		  tmp.hitamt_percnt,
		  sum(case when hitamt_nolinear > tmp.hitamt_percnt then hitamt_nolinear else 0 end) hitamt_sum_over,
   		  sum(case when hitamt_nolinear <= tmp.hitamt_percnt then hitamt_nolinear else 0 end) hitamt_sum_under
    	from stat_ml_ptn_linear linear, 
    	(
	    	select
	    		description, bettype, kumiban, pattern_name, pattern_value,
				mode() within group (order by hitamt_nolinear) hitamt_mod, --最頻値
				percentile_disc(paramPercentile) within group (order by hitamt_nolinear) hitamt_percnt
		    from stat_ml_ptn_linear linear
		    where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban and linear.pattern_name = paramPatternName
		      and hitamt_nolinear > 0
		    group by description, bettype, kumiban, pattern_name, pattern_value
		) tmp
		where linear.description = tmp.description and linear.bettype = tmp.bettype and linear.kumiban = tmp.kumiban and linear.pattern_name = tmp.pattern_name 
		  and linear.pattern_value = tmp.pattern_value
		group by linear.description, linear.bettype, linear.kumiban, linear.pattern_name, linear.pattern_value, tmp.hitamt_mod, tmp.hitamt_percnt
    ) bias
    where slope.description = bias.description and slope.bettype = bias.bettype and slope.kumiban = bias.kumiban and slope.pattern_name = bias.pattern_name 
    and slope.pattern_value = bias.pattern_value ;
    
END
$$ LANGUAGE plpgsql;

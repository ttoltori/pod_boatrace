DROP FUNCTION IF EXISTS create_stat_ml_kumiban_metric_analyze (VARCHAR(200), VARCHAR(2), VARCHAR(4), float);
CREATE OR REPLACE FUNCTION create_stat_ml_kumiban_metric_analyze (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPercentile float
) RETURNS VOID AS $$
DECLARE
	params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPercentile;
    
    raise info 'delete stat_ml_kumiban_metric_analyze';
	delete from stat_ml_kumiban_metric_analyze
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban;

	insert into stat_ml_kumiban_metric_analyze
    select 
      slope.description, slope.bettype, slope.kumiban, 
      pattern_cnt_avg, minus_days, plus_days, minus_changed_count, plus_changed_count,
      cast( cast(minus_days as float) / cast((minus_days + plus_days) as float) as numeric(7,2)) minus_days_rate,
      -- slope.linear_betamt_slope, slope.linear_hitamt_slope, 
      slope.linear_incomeamt_slope, 
      cast(cast(recover.recover_success_cnt as float) / cast(recover.betcnt as float) as numeric(7,2)) recover_success_rate,
      cast(cast(recover.recover_fail_cnt as float) / cast(recover.betcnt as float) as numeric(7,2)) recover_fail_rate,
      cast(cast(recover.recover_plus_cnt as float) / cast(recover.betcnt as float) as numeric(7,2)) recover_plus_rate,
      cast(cast(recover.recover_minus_cnt as float) / cast(recover.betcnt as float) as numeric(7,2)) recover_minus_rate,
      -- slope.linear_hitrate_slope, slope.linear_incomerate_slope,
      bias.hitamt_mod, bias.hitamt_percnt, hitamt_sum_over, hitamt_sum_under,
      cast( cast(hitamt_sum_over as float) / cast(hitamt_sum_under as float) as numeric(7,2)) hitamt_bias_rate,
      0 hit_bet_slope_rate -- cast( (linear_hitamt_slope / linear_betamt_slope) as numeric(7,2)) hit_bet_slope_rate
    from
    (
	    select 
	        description, bettype, kumiban, 
	        cast(avg(pattern_cnt) as numeric(7,2)) pattern_cnt_avg,
			sum(case when incomeamt < 0 then 1 else 0 end) minus_days,
			sum(case when incomeamt >= 0 then 1 else 0 end) plus_days,
			sum(case balance_changed when -1 then 1 else 0 end) minus_changed_count,
			sum(case balance_changed when 1 then 1 else 0 end) plus_changed_count,
			--cast(regr_slope(betamt, (ymd || sime)::bigint) as numeric(10,5)) linear_betamt_slope,
			-- cast(regr_slope(hitamt, (ymd || sime)::bigint) as numeric(10,5)) linear_hitamt_slope,
			cast(regr_slope(incomeamt, (ymd || sime)::bigint) as numeric(10,5)) linear_incomeamt_slope
			-- cast(regr_slope(hitrate, (ymd || sime)::bigint) as numeric(10,5)) linear_hitrate_slope,
			-- cast(regr_slope(incomerate, (ymd || sime)::bigint) as numeric(10,5)) linear_incomerate_slope
	    from stat_ml_kumiban_linear_analyze linear
	    where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban
	    group by description, bettype, kumiban
    ) slope, 
    (
    	select
   		  linear.description, linear.bettype, linear.kumiban,
   		  tmp.hitamt_mod,
   		  tmp.hitamt_percnt,
		  sum(case when hitamt_nolinear > tmp.hitamt_percnt then hitamt_nolinear else 0 end) hitamt_sum_over,
   		  sum(case when hitamt_nolinear <= tmp.hitamt_percnt then hitamt_nolinear else 0 end) hitamt_sum_under
    	from stat_ml_kumiban_linear_analyze linear, 
    	(
	    	select
	    		description, bettype, kumiban,
				mode() within group (order by hitamt_nolinear) hitamt_mod, --最頻値
				percentile_disc(paramPercentile) within group (order by hitamt_nolinear) hitamt_percnt
		    from stat_ml_kumiban_linear_analyze linear
		    where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban
		      and hitamt_nolinear > 0
		    group by description, bettype, kumiban
		) tmp
		where linear.description = tmp.description and linear.bettype = tmp.bettype and linear.kumiban = tmp.kumiban
		  and linear.hitamt_nolinear > 0
		group by linear.description, linear.bettype, linear.kumiban, tmp.hitamt_mod, tmp.hitamt_percnt
    ) bias,
    (
	    select 
	        description, bettype, kumiban,
			sum(1) betcnt,
	        sum(case when recover = 4 then 1 else 0 end) recover_success_cnt,
			sum(case when recover = 3 then 1 else 0 end) recover_fail_cnt,
			sum(case when recover = 6 then 1 else 0 end) recover_plus_cnt,
			sum(case when recover = 5 then 1 else 0 end) recover_minus_cnt
	    from stat_ml_ptn_linear_analyze linear
	    where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban
	    group by description, bettype, kumiban
    ) recover 
    where slope.description = bias.description and slope.bettype = bias.bettype and slope.kumiban = bias.kumiban
      and slope.description = recover.description and slope.bettype = recover.bettype and slope.kumiban = recover.kumiban;
    
END
$$ LANGUAGE plpgsql;

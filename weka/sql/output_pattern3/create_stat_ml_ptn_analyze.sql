DROP FUNCTION IF EXISTS create_stat_ml_ptn_analyze (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), 
  int[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], 
  numeric(7,2)[], VARCHAR(1), VARCHAR(1));
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_analyze(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400), -- 식별용 참고항목
paramArrOddsRank int[],
paramArrOdds numeric(7,2)[],
paramArrTotalRate numeric(7,2)[],
paramArrHitRate numeric(7,2)[],
paramArrIncomeRate numeric(7,2)[], 
paramArrBiasRate numeric(7,2)[], 
paramIsFinal VARCHAR(1),
paramIsCustom VARCHAR(1)
) RETURNS VOID AS $$
  DECLARE
    params text;
    betcount int; -- 투표횟수
    dayscount int; -- 날자수
  BEGIN
  
    raise info 'delete stat_ml_ptn_analyze';
    delete from stat_ml_ptn_analyze
    where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName
      and range_oddsrank[1] = paramArrOddsRank[1] and range_oddsrank[2] = paramArrOddsRank[2]
      and range_odds[1] = paramArrOdds[1] and range_odds[2] = paramArrOdds[2]
      and range_totalrate[1] = paramArrTotalRate[1] and range_totalrate[2] = paramArrTotalRate[2]
      and range_hitrate[1] = paramArrHitRate[1] and range_hitrate[2] = paramArrHitRate[2]
      and range_incomerate[1] = paramArrIncomeRate[1] and range_incomerate[2] = paramArrIncomeRate[2]
      and range_biasrate[1] = paramArrBiasRate[1] and range_biasrate[2] = paramArrBiasRate[2]
      and isfinal = paramIsFinal and iscustom = paramIsCustom;
    
    select into dayscount count(distinct ymd) from stat_ml_ptn_linear_analyze linear
      where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban and linear.pattern_name = paramPatternName;
    select into betcount count(*) from stat_ml_ptn_linear_analyze linear
      where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban and linear.pattern_name = paramPatternName;
    
    EXECUTE format ('
      insert into stat_ml_ptn_analyze
      select
        linear.description,
        linear.bettype,
        linear.kumiban,
        linear.pattern_name,
        %s betcount_daily,
        sum(1) betcnt,
        sum(stat.hity) hitcnt,
        sum(stat.betamt) betamt, 
        sum (stat.hitamt) hitamt, 
        sum (stat.hitamt) - sum(stat.betamt) incomeamt,
        cast( (cast(sum(stat.hity) as float)/ cast(sum(1) as float) *100) as numeric(7,2)) hitrate, 
        cast( (cast(sum (stat.hitamt) as float)/ cast(sum(stat.betamt) as float) *100) as numeric(7,2)) incomerate, 
        cast(avg(final.totalrate) as numeric(7,2)) totalrate,
        metric.hitamt_bias_rate,
        metric.hit_bet_slope_rate,
        metric.linear_betamt_slope,
        metric.linear_hitamt_slope,
        metric.linear_incomeamt_slope,
        metric.linear_hitrate_slope,
        metric.linear_incomerate_slope,
        metric.hitamt_sum_under,
        metric.hitamt_sum_over,
        metric.hitamt_mod,
        metric.hitamt_percent,
        ''%s'' range_oddsrank,
        ''%s'' range_odds,
        ''%s'' range_totalrate,
        ''%s'' range_hitrate,
        ''%s'' range_incomerate,
        ''%s'' range_biasrate,
        ''%s'' isfinal,
        ''%s'' isCustom
      from stat_ml_ptn_linear_analyze linear, stat_ml_ptn_metric_analyze metric, stat_ml_result_ptn_analyze stat, stat_ml_ptn_final final
      where 
            linear.description = stat.description and linear.bettype = stat.bettype and linear.kumiban = stat.bet_kumiban
        and linear.pattern_name = stat.pattern_name and linear.pattern_value = stat.pattern_value
        and linear.description = metric.description and linear.bettype = metric.bettype and linear.kumiban = metric.kumiban
        and linear.pattern_name = metric.pattern_name
        and linear.ymd = stat.ymd and linear.jyocd = stat.jyocd and linear.raceno = stat.raceno and linear.sime = stat.sime
        and linear.description = final.description and linear.bettype = final.bettype and linear.kumiban = final.kumiban
        and linear.pattern_name = final.pattern_name and linear.pattern_value = final.pattern_value
        and metric.hitamt_bias_rate >= %s and metric.hitamt_bias_rate <= %s
        and linear.pattern_name = ''%s''
      group by linear.description, linear.bettype, linear.kumiban, linear.pattern_name,
        metric.hitamt_bias_rate,
        metric.hit_bet_slope_rate,
        metric.linear_betamt_slope,
        metric.linear_hitamt_slope,
        metric.linear_incomeamt_slope,
        metric.linear_hitrate_slope,
        metric.linear_incomerate_slope,
        metric.hitamt_sum_under,
        metric.hitamt_sum_over,
        metric.hitamt_mod,
        metric.hitamt_percent
    ' , cast(cast(betcount as float) / cast(dayscount as float) as numeric(7,1))
      , paramArrOddsRank
      , paramArrOdds
      , paramArrTotalRate
      , paramArrHitRate
      , paramArrIncomeRate
      , paramArrBiasRate
      , paramIsFinal
      , paramIsCustom
      , paramArrBiasRate[1], paramArrBiasRate[2]
      , paramPatternName
    );
    
  END;
$$ LANGUAGE plpgsql;

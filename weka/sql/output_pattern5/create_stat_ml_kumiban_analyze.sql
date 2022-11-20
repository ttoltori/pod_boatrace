DROP FUNCTION IF EXISTS create_stat_ml_kumiban_analyze (VARCHAR(200), VARCHAR(2), VARCHAR(4),  
  int[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], 
  numeric(7,2)[], numeric(7,2)[], int, int[], 
  numeric(7,2), numeric(7,2), numeric(7,2), numeric(7,2), numeric(7,2), numeric(7,2), 
  VARCHAR(1), VARCHAR(1));
CREATE OR REPLACE FUNCTION create_stat_ml_kumiban_analyze(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramArrOddsRank int[],
paramArrOdds numeric(7,2)[],
paramArrTotalRate numeric(7,2)[],
paramArrHitRate numeric(7,2)[],
paramArrIncomeRate numeric(7,2)[], 
paramArrBiasRate numeric(7,2)[],
paramArrMinusDaysRate numeric(7,2)[],
paramMinusChangedCount int,
paramArrBetCount int[],
paramPercentIncomeAmt numeric(7,2),  -- final조건 투표회수 상위 비율
paramPercentHitRate numeric(7,2), -- final조건 적중률 상위 비율
paramPercentIncomeRate numeric(7,2), -- final조건 수익률 상위 비율
paramPercentMinusDaysRate numeric(7,2), -- final조건 적자일수 상위 비율
paramPercentIncomeAmtSlope numeric(7,2), -- final조건 수익금액 상승기울기 상위 비율
paramPercentRecoverPlusRate numeric(7,2), -- final조건 적중흑자증가투표수 비율 상위 비율
paramIsFinal VARCHAR(1),
paramIsCustom VARCHAR(1)
) RETURNS VOID AS $$
  DECLARE
    params text;
    betcount int; -- 투표횟수
    dayscount int; -- 날자수
    paramCondition VARCHAR(400); -- 条件のサマリー（検索用）
  BEGIN
  
    paramCondition := 
    	paramPercentIncomeAmtSlope || '_' ||  paramPercentRecoverPlusRate || '_' ||
        paramPercentIncomeAmt || '_' || paramPercentHitRate  || '_' || paramPercentIncomeRate || '_' || paramPercentMinusDaysRate || '_' || 
        paramArrOddsRank[1] || '~' || paramArrOddsRank[2] || '_' ||
        paramArrOdds[1] || '~' || paramArrOdds[2] || '_' ||
        paramArrTotalRate[1] || '~' || paramArrTotalRate[2] || '_' ||
        paramArrHitRate[1] || '~' || paramArrHitRate[2] || '_' ||
        paramArrIncomeRate[1] || '~' || paramArrIncomeRate[2] || '_' ||
        paramArrBiasRate[1] || '~' || paramArrBiasRate[2] || '_' ||
        paramArrMinusDaysRate[1] || '~' || paramArrMinusDaysRate[2] || '_' ||
        paramMinusChangedCount || '_' ||
        paramArrBetCount[1] || '~' || paramArrBetCount[2];
  
    raise info 'delete stat_ml_kumiban_analyze';
    delete from stat_ml_kumiban_analyze
    where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban
      and isfinal = paramIsFinal and iscustom = paramIsCustom
      and condition = paramCondition; 
    
    select into dayscount (max(ymd)::date - min(ymd)::date) from stat_ml_kumiban_linear_analyze linear
      where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban;
    select into betcount count(*) from stat_ml_kumiban_linear_analyze linear
      where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban;
    
    -- 분석 결과 데이터가 없다면 그냥 리턴한다.
    if dayscount = 0 or betcount = 0 then
      raise info 'no result. dayscount=%, betcount=%', dayscount, betcount;
      return;
    end if;

    EXECUTE format ('
      insert into stat_ml_kumiban_analyze
      select
        linear.description,
        linear.bettype,
        linear.kumiban,
        %s betcount_daily,
        sum(1) betcnt,
        sum(linear.hity_nolinear) hitcnt,
        sum(linear.betamt_nolinear) betamt, 
        sum(linear.hitamt_nolinear) hitamt, 
        sum(linear.hitamt_nolinear) - sum(linear.betamt_nolinear) incomeamt,
        cast( (cast(sum(linear.hity_nolinear) as float)/ cast(sum(1) as float) *100) as numeric(7,2)) hitrate, 
        cast( (cast(sum(linear.hitamt_nolinear) as float)/ cast(sum(linear.betamt_nolinear) as float) *100) as numeric(7,2)) incomerate, 
        0 totalrate,
		metric.pattern_cnt_avg,
        metric.hitamt_bias_rate,
        metric.hit_bet_slope_rate,
        metric.minus_days,
        metric.plus_days,
        metric.minus_changed_count,
        metric.plus_changed_count,
        metric.minus_days_rate,
		%s incomeamtslpoe_percent,
		%s recoverplus_percent,
		%s betcnt_percent,
		%s hitrate_percent,
		%s incomerate_percent,
		%s minus_days_percent,
        metric.linear_incomeamt_slope,
        metric.recover_success_rate,
        metric.recover_fail_rate,
        metric.recover_plus_rate,
        metric.recover_minus_rate,
		''%s'' condition,
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
        ''%s'' range_minusdaysrate,
        ''%s'' max_minuschangedcount,
        ''%s'' range_betcntlimit,
        ''%s'' isfinal,
        ''%s'' isCustom
      from stat_ml_kumiban_linear_analyze linear, stat_ml_kumiban_metric_analyze metric
      where linear.description = ''%s'' and linear.bettype = ''%s'' and linear.kumiban = ''%s''   
        and linear.description = metric.description and linear.bettype = metric.bettype and linear.kumiban = metric.kumiban
      group by linear.description, linear.bettype, linear.kumiban,
		metric.pattern_cnt_avg,
        metric.hitamt_bias_rate,
        metric.hit_bet_slope_rate,
        metric.minus_days,
        metric.plus_days,
        metric.minus_changed_count,
        metric.plus_changed_count,
        metric.minus_days_rate,
        metric.linear_incomeamt_slope,
        metric.recover_success_rate,
        metric.recover_fail_rate,
        metric.recover_plus_rate,
        metric.recover_minus_rate,
        metric.hitamt_sum_under,
        metric.hitamt_sum_over,
        metric.hitamt_mod,
        metric.hitamt_percent
    ' , cast(cast(betcount as float) / cast(dayscount as float) as numeric(7,2))
	  , paramPercentIncomeAmtSlope -- final조건 수익금액상승기울기 상위비율
	  , paramPercentRecoverPlusRate -- final조건 적중흑자증가투표비율 상위비율
	  ,	paramPercentIncomeAmt  -- final조건 투표회수 상위 비율
	  ,	paramPercentHitRate -- final조건 적중률 상위 비율
	  ,	paramPercentIncomeRate -- final조건 수익률 상위 비율
	  ,	paramPercentMinusDaysRate -- final조건 적자일수 상위 비율
      , paramCondition
      , paramArrOddsRank
      , paramArrOdds
      , paramArrTotalRate
      , paramArrHitRate
      , paramArrIncomeRate
      , paramArrBiasRate
	  , paramArrMinusDaysRate
	  , paramMinusChangedCount
	  , paramArrBetCount
      , paramIsFinal
      , paramIsCustom
      , paramDescription
      , paramBettype
      , paramKumiban
    );
    
  END;
$$ LANGUAGE plpgsql;

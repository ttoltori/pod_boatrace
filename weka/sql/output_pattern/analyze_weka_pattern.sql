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

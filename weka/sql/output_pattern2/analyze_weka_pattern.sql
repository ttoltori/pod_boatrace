DROP FUNCTION IF EXISTS analyze_weka_pattern(VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(8)[], int[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], VARCHAR(1), VARCHAR(1));
CREATE OR REPLACE FUNCTION analyze_weka_pattern(
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400), -- 식별용 참고항목
paramArrYmd VARCHAR(8)[],
paramArrOddsRank int[],
paramArrOdds numeric(7,2)[],
paramArrTotalRate numeric(7,2)[],
paramArrHitRate numeric(7,2)[],
paramArrIncomeRate numeric(7,2)[], 
paramIsFinal VARCHAR(1),
paramIsCustom VARCHAR(1)
) RETURNS SETOF stat_ml_ptn_analyze AS $$
  DECLARE
    custat refcursor; -- 패턴결과테이블 커서
    recstat record; -- 패턴결과테이블 레코드
    counter int; -- 행카운터
    lr stat_ml_ptn_linear%ROWTYPE; -- 축적결과테이블 행
    betcount int; -- 투표횟수
    dayscount int; -- 날자수
  BEGIN
  
    truncate stat_ml_ptn_linear;
    
    counter := 1;
    open custat for 
      select 
        stat.*
      from stat_ml_result_ptn stat, stat_ml_ptn_final final
      where stat.description = final.description and stat.pattern_name = final.pattern_name and stat.pattern_value = final.pattern_value
        and stat.bettype = final.bettype and stat.bet_kumiban = final.kumiban
        and stat.ymd >= paramArrYmd[1] and stat.ymd <= paramArrYmd[2]
        and stat.bet_oddsrank >= paramArrOddsRank[1] and stat.bet_oddsrank <= paramArrOddsRank[2]
        and stat.bet_odds >= paramArrOdds[1] and stat.bet_odds <= paramArrOdds[2]
        and totalrate >= paramArrTotalRate[1] and totalrate <= paramArrTotalRate[2]
        and hitrate >= paramArrHitRate[1] and hitrate <= paramArrHitRate[2]
        and incomerate >= paramArrIncomeRate[1] and incomerate <= paramArrIncomeRate[2]
        and stat.bettype = paramBettype and stat.bet_kumiban = paramKumiban and stat.pattern_name = paramPatternName
      order by stat.ymd, stat.sime;
    loop
	    fetch custat into recstat;
	    if not found then 
	      exit;
	    end if;
	    
        lr.description := recstat.description;
        lr.ymd := recstat.ymd;
        lr.sime := recstat.sime;
        lr.jyocd := recstat.jyocd;
        lr.raceno := recstat.raceno;
        lr.bettype := recstat.bettype;
        lr.kumiban := recstat.bet_kumiban;
        lr.pattern_name := recstat.pattern_name;
        lr.pattern_value := recstat.pattern_value;
        lr.betcnt := counter;
	    if counter = 1 then
	      lr.hitcnt := recstat.hity;
	      lr.betamt := recstat.betamt;
	      lr.hitamt := recstat.hitamt;
	      lr.incomeamt := recstat.hitamt - recstat.betamt;
	    else
	      lr.hitcnt := lr.hitcnt + recstat.hity;
	      lr.betamt := lr.betamt + recstat.betamt;
	      lr.hitamt := lr.hitamt + recstat.hitamt;
	      lr.incomeamt := lr.incomeamt + (recstat.hitamt - recstat.betamt);
	    end if;
        lr.hitrate := cast( (cast(lr.hitcnt as float)/ cast(lr.betcnt as float) *100) as numeric(7,2));
        lr.incomerate := cast( (cast(lr.hitamt as float)/ cast(lr.betamt as float) *100) as numeric(7,2));
	    
	    insert into stat_ml_ptn_linear values 
	      (lr.description, lr.ymd, lr.sime, lr.jyocd, lr.raceno, lr.bettype, lr.kumiban, lr.pattern_name, lr.pattern_value,
	       lr.betcnt, lr.hitcnt, lr.betamt, lr.hitamt, lr.incomeamt, lr.hitrate, lr.incomerate);
	    counter := counter + 1;
	  end loop;
	  close custat;
	
    delete from stat_ml_ptn_analyze
    where bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName
      and range_oddsrank[1] = paramArrOddsRank[1] and range_oddsrank[2] = paramArrOddsRank[2]
      and range_odds[1] = paramArrOdds[1] and range_odds[2] = paramArrOdds[2]
      and range_totalrate[1] = paramArrTotalRate[1] and range_totalrate[2] = paramArrTotalRate[2]
      and range_hitrate[1] = paramArrHitRate[1] and range_hitrate[2] = paramArrHitRate[2]
      and range_incomerate[1] = paramArrIncomeRate[1] and range_incomerate[2] = paramArrIncomeRate[2]
      and isfinal = paramIsFinal;
    
    select into dayscount count(distinct ymd) from stat_ml_ptn_linear where pattern_name = paramPatternName;
    select into betcount count(*) from stat_ml_ptn_linear where pattern_name = paramPatternName;
    
    EXECUTE format ('
      insert into stat_ml_ptn_analyze
      select
        stat.bettype bettype,
        stat.bet_kumiban kumiban,
        stat.pattern_name pattern_name,
        %s betcount_daily,
        sum(1) betcnt,
        sum(stat.hity) hitcnt,
        sum(stat.betamt) betamt, 
        sum (stat.hitamt) hitamt, 
        sum (stat.hitamt) - sum(stat.betamt) incomeamt,
        cast( (cast(sum(stat.hity) as float)/ cast(sum(1) as float) *100) as numeric(7,2)) hitrate, 
        cast( (cast(sum (stat.hitamt) as float)/ cast(sum(stat.betamt) as float) *100) as numeric(7,2)) incomerate, 
        cast(avg(final.totalrate) as numeric(7,2)) totalrate,
        cast(regr_slope(linear.hitrate, (linear.ymd || linear.sime)::bigint) * 1000000 as numeric(19,2)) linear_hitrate_slope,
        cast(regr_slope(linear.incomerate, (linear.ymd || linear.sime)::bigint) * 1000000 as numeric(19,2)) linear_incomerate_slope,
        cast(regr_slope(linear.incomeamt, (linear.ymd || linear.sime)::bigint) * 1000000 as numeric(19,2)) linear_incomeamt_slope, 
        ''%s'' range_oddsrank,
        ''%s'' range_odds,
        ''%s'' range_totalrate,
        ''%s'' range_hitrate,
        ''%s'' range_incomerate,
        stat.description,
        ''%s'' isfinal,
        ''%s'' isCustom
      from stat_ml_ptn_linear linear, stat_ml_result_ptn stat, stat_ml_ptn_final final
      where stat.ymd >= ''%s'' and stat.ymd <= ''%s''
        and linear.ymd = stat.ymd and linear.jyocd = stat.jyocd and linear.raceno = stat.raceno and linear.sime = stat.sime
        and linear.pattern_name = stat.pattern_name
        and linear.pattern_value = stat.pattern_value
        and linear.pattern_name = final.pattern_name
        and linear.pattern_value = final.pattern_value
        and linear.bettype = final.bettype
        and linear.kumiban = final.kumiban
        and linear.pattern_name = ''%s''
      group by stat.bettype, stat.bet_kumiban, stat.pattern_name, stat.description
    ' , cast(cast(betcount as float) / cast(dayscount as float) as numeric(7,1))
      , paramArrOddsRank
      , paramArrOdds
      , paramArrTotalRate
      , paramArrHitRate
      , paramArrIncomeRate
      , paramIsFinal
      , paramIsCustom
      , paramArrYmd[1], paramArrYmd[2]
      , paramPatternName
    );
    
    -- bakcup
    drop table if exists stat_ml_ptn_analyze_bak;
	create table stat_ml_ptn_analyze_bak as
	select * from stat_ml_ptn_analyze;
    
    return query 
      select * from stat_ml_ptn_analyze 
      where bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName
        and isfinal = paramIsFinal and iscustom = paramIsCustom;
  END;
$$ LANGUAGE plpgsql;

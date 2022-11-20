통합모델용(한 모델로 복수 구미방 예측) sql

-------------------------------------------------------- execute
-- 기본 결과가 수익률 100%미만일 때 기본결과에 대한 분석 (파라미터 조정을 위해)
-- 결과를 엑셀로 가져가서 분석한다.
select analyze_weka_pattern('2T', '12', '',
  array['20190101', '20190430'], 
  array[0,1000], -- oddsrank 0,1000 3,4
  array[0,1000], -- odds 0,1000
  array[0,100], -- totalrate 0,100  
  array[0,100], -- hitrate 0,100
  array[100,1000], -- incomerate 100, 1000
  'n', -- isfinal
  'n'  -- iscustom
);




delete from stat_ml_ptn_analyze 
where bettype = '2T' and kumiban = '12' and pattern_name = 'jyocd_alcount_level12' 
and isfinal = 'n' and iscustom = 'y'; 

select analyze_weka_pattern('2T', '12', 'jyocd_alcount_level12',
  array['20190101', '20190430'], 
  array[0,1000], -- oddsrank 0,1000 3,4
  array[0,1000], -- odds 0,1000
  array[0,100], -- totalrate 0,100  
  array[0,100], -- hitrate 0,100
  array[100,1000], -- incomerate 100, 1000
  'n', -- isfinal
  'n'  -- iscustom
);

select bettype, kumiban, betcount_daily, betamt, incomeamt, hitrate, incomerate, totalrate,
linear_hitrate_slope::text, linear_incomerate_slope::text, linear_incomeamt_slope::text,
range_oddsrank::text, range_odds::text, range_totalrate::text, range_hitrate::text, range_incomerate::text, description, pattern_name
from stat_ml_ptn_analyze
where bettype = '2T' and kumiban = '12' and pattern_name = 'jyocd_alcount_level12'
and isfinal = 'n' and iscustom = 'n'
order by updatetime desc, bettype, kumiban;


-------------------------------------------------------- save pattern to file
copy (
  select * from stat_ml_ptn_filtered
) to 'C:\Dev\workspace\Oxygen\pod_boatrace_test\properties\weka_patterns.csv' with csv 
;

-------------------------------------------------------- create pattern table
truncate stat_ml_ptn_filtered;

insert into stat_ml_ptn_filtered
select
  a.bettype,
  a.kumiban,
  a.pattern_name,
  a.betcount_daily,
  a.betamt,
  a.incomeamt,
  a.hitrate,
  a.incomerate,
  a.totalrate,
  a.linear_hitrate_slope,
  a.linear_incomerate_slope,
  a.linear_incomeamt_slope,
  a.range_oddsrank[1],
  a.range_oddsrank[2],
  a.range_odds[1],
  a.range_odds[2],
  f.pattern_value,
  f.betamt ptn_betamt,
  f.incomeamt ptn_incomeamt,
  f.hitrate ptn_hitrate,
  f.incomerate ptn_incomerate,
  f.totalrate ptn_totalrate,
  f.days_bet ptn_days_bet,
  f.days_plus_rate ptn_days_plus_rate,
  f.linear_hitrate_slope ptn_hitrate_slope,
  f.linear_incomerate_slope ptn_incomerate_slope,
  f.linear_incomeamt_slope ptn_incomeamt_slope
from stat_ml_ptn_analyze a, stat_ml_ptn_final f
where a.description = f.description and a.pattern_name = f.pattern_name 
  and (f.totalrate >= a.range_totalrate[1] and f.totalrate <= a.range_totalrate[2])
  and (f.hitrate >= a.range_hitrate[1] and f.hitrate <= a.range_hitrate[2])
  and (f.incomerate >= a.range_incomerate[1] and f.incomerate <= a.range_incomerate[2])
  and a.isfinal = 'y'
order by a.bettype, a.kumiban, a.pattern_name, f.pattern_value
;



-------------------------------------------------------- create procedure
-- ！！！ output_weka_patternが必ず事前に実行されていること
DROP FUNCTION IF EXISTS analyze_weka_pattern(VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), 
  VARCHAR(8)[], int[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], 
  float, numeric(7,2)[], VARCHAR(1), VARCHAR(1));
CREATE OR REPLACE FUNCTION analyze_weka_pattern(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400), -- 식별용 참고항목
paramPatternValue VARCHAR(400), -- 식별용 참고항목
paramArrYmd VARCHAR(8)[],
paramArrOddsRank int[],
paramArrOdds numeric(7,2)[],
paramArrTotalRate numeric(7,2)[],
paramArrHitRate numeric(7,2)[],
paramArrIncomeRate numeric(7,2)[], 
paramMetricBiasPercent float,
paramArrBiasRate numeric(7,2)[], 
paramIsFinal VARCHAR(1),
paramIsCustom VARCHAR(1)
) RETURNS SETOF stat_ml_ptn_analyze AS $$
  DECLARE
    params text;
  BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    perform create_stat_ml_result_ptn_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, paramPatternValue, paramArrYmd[1], paramArrYmd[2]);
  
	perform create_stat_ml_ptn_linear_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, 
			paramArrOddsRank, paramArrOdds, paramArrTotalRate, paramArrHitRate, paramArrIncomeRate);
	
	perform create_stat_ml_ptn_metric_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, paramMetricBiasPercent);
	
	perform create_stat_ml_ptn_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, 
			paramArrOddsRank, paramArrOdds, paramArrTotalRate, paramArrHitRate, paramArrIncomeRate, paramArrBiasRate, paramIsFinal, paramIsCustom);
	
    -- bakcup
    drop table if exists stat_ml_ptn_analyze_bak;
	create table stat_ml_ptn_analyze_bak as
	select * from stat_ml_ptn_analyze;
    
    return query 
      select * from stat_ml_ptn_analyze 
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName
        and isfinal = paramIsFinal and iscustom = paramIsCustom;
  END;
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS create_stat_ml_result_ptn_analyze (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400),  VARCHAR(8), VARCHAR(8));
CREATE OR REPLACE FUNCTION create_stat_ml_result_ptn_analyze (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400),
paramFromYmd VARCHAR(8),
paramToYmd VARCHAR(8)
) RETURNS VOID AS $$
DECLARE
  params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' || paramPatternName || '_' || paramPatternValue
       || '_' || paramFromYmd || '_' || paramToYmd;
    
    raise info 'delete stat_ml_result_ptn_analyze';
	delete from stat_ml_result_ptn_analyze 
	where description = paramDescription and bettype = paramBettype and bet_kumiban = paramKumiban and pattern_name = paramPatternName;

	  
    raise info 'insert stat_ml_result_ptn_analyze. %', params;
    EXECUTE format ('
		insert into stat_ml_result_ptn_analyze
		select
		   stat.*,
		   ''%s'' pattern_name,
		   %s pattern_value
		from stat_ml_result stat, rec_race race, rec_race_metric metric
		where race.ymd = stat.ymd and race.jyocd = stat.jyocd and race.raceno = stat.raceno
		  and race.ymd = metric.ymd and race.jyocd = metric.jyocd and race.raceno = metric.raceno
		  and stat.description = ''%s'' and stat.bettype = ''%s'' and stat.bet_kumiban = ''%s''
		  and stat.ymd >= ''%s'' and stat.ymd <= ''%s''
	', paramPatternName, paramPatternValue, paramDescription, paramBettype, paramKumiban, paramFromYmd, paramToYmd);
END
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS create_stat_ml_ptn_linear_analyze (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400),
  int[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[]);
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_linear_analyze (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramArrOddsRank int[],
paramArrOdds numeric(7,2)[],
paramArrTotalRate numeric(7,2)[],
paramArrHitRate numeric(7,2)[],
paramArrIncomeRate numeric(7,2)[]
) RETURNS VOID AS $$
DECLARE
    params text;
    custat refcursor; -- 패턴결과테이블 커서
    recstat record; -- 패턴결과테이블 레코드
    counter int; -- 행카운터
    lr stat_ml_ptn_linear_analyze%ROWTYPE; -- 축적결과테이블 행
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    raise info 'delete stat_ml_ptn_linear_analyze %', params;
    delete from stat_ml_ptn_linear_analyze
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;
      
    counter := 1;
    open custat for 
      select 
        stat.*
      from stat_ml_result_ptn_analyze stat, stat_ml_ptn_final final
      where stat.description = paramDescription and stat.bettype = paramBettype and stat.bet_kumiban = paramKumiban and stat.pattern_name = paramPatternName
        and stat.description = final.description and stat.bettype = final.bettype and stat.bet_kumiban = final.kumiban
        and stat.pattern_name = final.pattern_name and stat.pattern_value = final.pattern_value
        and stat.bet_oddsrank >= paramArrOddsRank[1] and stat.bet_oddsrank <= paramArrOddsRank[2]
        and stat.bet_odds >= paramArrOdds[1] and stat.bet_odds <= paramArrOdds[2]
        and totalrate >= paramArrTotalRate[1] and totalrate <= paramArrTotalRate[2]
        and hitrate >= paramArrHitRate[1] and hitrate <= paramArrHitRate[2]
        and incomerate >= paramArrIncomeRate[1] and incomerate <= paramArrIncomeRate[2]
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
        lr.hitamt_nolinear = recstat.hitamt;
	    
	    insert into stat_ml_ptn_linear_analyze values 
	      (lr.description, lr.ymd, lr.sime, lr.jyocd, lr.raceno, lr.bettype, lr.kumiban, lr.pattern_name, lr.pattern_value,
	       lr.betcnt, lr.hitcnt, lr.betamt, lr.hitamt, lr.incomeamt, lr.hitrate, lr.incomerate, lr.hitamt_nolinear);
	    counter := counter + 1;
	end loop;
	close custat;

END
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS create_stat_ml_ptn_metric_analyze (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), float);
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_metric_analyze (
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
    
    raise info 'delete stat_ml_ptn_metric_analyze';
	delete from stat_ml_ptn_metric_analyze
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;

	insert into stat_ml_ptn_metric_analyze
    select 
      slope.*, 
      bias.hitamt_mod, bias.hitamt_percnt, hitamt_sum_over, hitamt_sum_under,
      cast( cast(hitamt_sum_over as float) / cast(hitamt_sum_under as float) as numeric(19,2)) hitamt_bias_rate,
      cast( (linear_hitamt_slope / linear_betamt_slope) as numeric(19,2)) hit_bet_slope_rate
    from
    (
	    select 
	        description, bettype, kumiban, pattern_name, 
			cast(regr_slope(betamt, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_betamt_slope,
			cast(regr_slope(hitamt, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_hitamt_slope,
			cast(regr_slope(incomeamt, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_incomeamt_slope,
			cast(regr_slope(hitrate, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_hitrate_slope,
			cast(regr_slope(incomerate, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_incomerate_slope
	    from stat_ml_ptn_linear_analyze linear
	    where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban and linear.pattern_name = paramPatternName
	    group by description, bettype, kumiban, pattern_name
    ) slope, 
    (
    	select
   		  linear.description, linear.bettype, linear.kumiban, linear.pattern_name,
   		  tmp.hitamt_mod,
   		  tmp.hitamt_percnt,
		  sum(case when hitamt_nolinear > tmp.hitamt_percnt then hitamt_nolinear else 0 end) hitamt_sum_over,
   		  sum(case when hitamt_nolinear <= tmp.hitamt_percnt then hitamt_nolinear else 0 end) hitamt_sum_under
    	from stat_ml_ptn_linear_analyze linear, 
    	(
	    	select
	    		description, bettype, kumiban, pattern_name, 
				mode() within group (order by hitamt_nolinear) hitamt_mod, --最頻値
				percentile_disc(paramPercentile) within group (order by hitamt_nolinear) hitamt_percnt
		    from stat_ml_ptn_linear_analyze linear
		    where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban and linear.pattern_name = paramPatternName
		      and hitamt_nolinear > 0
		    group by description, bettype, kumiban, pattern_name
		) tmp
		where linear.description = tmp.description and linear.bettype = tmp.bettype and linear.kumiban = tmp.kumiban and linear.pattern_name = tmp.pattern_name 
		  and linear.hitamt_nolinear > 0
		group by linear.description, linear.bettype, linear.kumiban, linear.pattern_name, tmp.hitamt_mod, tmp.hitamt_percnt
    ) bias
    where slope.description = bias.description and slope.bettype = bias.bettype and slope.kumiban = bias.kumiban and slope.pattern_name = bias.pattern_name;
    
END
$$ LANGUAGE plpgsql;

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

====================================================================================================================================================
DROP FUNCTION IF EXISTS output_weka_pattern(VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), int, float, VARCHAR(8), VARCHAR(8));
CREATE OR REPLACE FUNCTION output_weka_pattern(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400),
paramMinimumBetCount int, -- bet minimum
paramMetricBiasPercent float, -- hitamt_nolinear bias percentage limit
paramFromYmd VARCHAR(8),
paramToYmd VARCHAR(8)
)
RETURNS VOID AS $$
  DECLARE
    finaltotal int; -- 최종 패턴 총수
  BEGIN

	perform create_stat_ml_result_ptn(paramDescription, paramBettype, paramKumiban, paramPatternName, paramPatternValue, paramFromYmd, paramToYmd);
	
	perform create_stat_ml_ptn_cnt(paramDescription, paramBettype, paramKumiban, paramPatternName);
	
	perform create_stat_ml_ptn_linear(paramDescription, paramBettype, paramKumiban, paramPatternName, paramMinimumBetCount);

-- daily 집계시간이 너무 많이 걸리므로 생략한다.
--	perform create_stat_ml_ptn_daily(paramDescription, paramBettype, paramKumiban, paramPatternName);
	
	perform create_stat_ml_ptn_totally(paramDescription, paramBettype, paramKumiban, paramPatternName);

	perform create_stat_ml_ptn_metric(paramDescription, paramBettype, paramKumiban, paramPatternName, paramMetricBiasPercent);
	
	perform create_stat_ml_ptn_final(paramDescription, paramBettype, paramKumiban, paramPatternName);

    select into finaltotal count(*) from stat_ml_ptn_final
    where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;
    
    raise info 'total final pattern nums = %', finaltotal;
  END;
$$ LANGUAGE plpgsql;

-------------------------------------------------------- create procedure
DROP FUNCTION IF EXISTS create_stat_ml_result_ptn (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400),  VARCHAR(8), VARCHAR(8));
CREATE OR REPLACE FUNCTION create_stat_ml_result_ptn (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400),
paramFromYmd VARCHAR(8),
paramToYmd VARCHAR(8)
) RETURNS VOID AS $$
DECLARE
  params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' || paramPatternName || '_' || paramPatternValue
       || '_' || paramFromYmd || '_' || paramToYmd;
    
    raise info 'delete stat_ml_result_ptn';
	delete from stat_ml_result_ptn 
	where description = paramDescription and bettype = paramBettype and bet_kumiban = paramKumiban and pattern_name = paramPatternName;
	  
    raise info 'insert stat_ml_result_ptn. %', params;
    EXECUTE format ('
		insert into stat_ml_result_ptn
		select
		   stat.*,
		   ''%s'' pattern_name,
		   %s pattern_value
		from stat_ml_result stat, rec_race race, rec_race_metric metric
		where race.ymd = stat.ymd and race.jyocd = stat.jyocd and race.raceno = stat.raceno
		  and race.ymd = metric.ymd and race.jyocd = metric.jyocd and race.raceno = metric.raceno
		  and stat.description = ''%s'' and stat.bettype = ''%s'' and stat.bet_kumiban = ''%s''
		  and stat.ymd >= ''%s'' and stat.ymd <= ''%s''
	', paramPatternName, paramPatternValue, paramDescription, paramBettype, paramKumiban, paramFromYmd, paramToYmd);
END
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS create_stat_ml_ptn_cnt (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400));
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_cnt (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400)
) RETURNS VOID AS $$
DECLARE
  params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    raise info 'delete stat_ml_ptn_cnt';
	delete from stat_ml_ptn_cnt 
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;
	
	raise info 'insert stat_ml_ptn_cnt. %', params;
	insert into stat_ml_ptn_cnt
	select * from 
	(
      select 
         description, bettype, bet_kumiban kumiban, pattern_name, pattern_value, count(*) pattern_cnt
      from stat_ml_result_ptn stat
      where description =  paramDescription and bettype = paramBettype and bet_kumiban = paramKumiban and pattern_name = paramPatternName
      group by description, bettype, bet_kumiban, pattern_name, pattern_value
      order by description, bettype, bet_kumiban, pattern_cnt desc
	) tmp;
END
$$ LANGUAGE plpgsql;

-- linear에 패턴갯수의 제한이 걸린 것에 주의할 것!!!
-- 이후의 lienear와 join하는 경우 stat가 아니라 linear의 필드와 파라미터를 join해야한다. 
DROP FUNCTION IF EXISTS create_stat_ml_ptn_linear (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), int);
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_linear (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramMinPtnCnt int
) RETURNS VOID AS $$
DECLARE
	params text;
    cuptn refcursor; -- 패턴 커서
    recptn record; -- 패턴 레코드
    custat refcursor; -- 패턴결과테이블 커서
    recstat record; -- 패턴결과테이블 레코드
    ptncounter int; -- 패턴카운터
    lr stat_ml_ptn_linear%ROWTYPE; -- 축적결과테이블 행
    counter int; -- 행카운터
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    raise info 'delete stat_ml_ptn_linear';
	delete from stat_ml_ptn_linear
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;
	
	raise info 'insert stat_ml_ptn_linear. %', params;
    -- 패턴에 대한 커서를 오픈해서 루프한다.
    ptncounter := 1;
    open cuptn for 
      select * from stat_ml_ptn_cnt
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName
        and pattern_cnt >= paramMinPtnCnt;
	loop
	  fetch cuptn into recptn;
	  if not found then 
	    exit;
	  end if;
	  
	  -- 각 패턴값에 대한 투표결과를 시간순으로 루프한다.
	  counter := 1;
      open custat for 
	    select * from stat_ml_result_ptn statptn
	    where statptn.description = recptn.description 
	      and statptn.bettype = recptn.bettype 
	      and statptn.bet_kumiban = recptn.kumiban
	      and statptn.pattern_name = recptn.pattern_name
	      and statptn.pattern_value = recptn.pattern_value
	    order by statptn.ymd, statptn.sime;
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
        lr.hitamt_nolinear = recstat.hitamt;
	    
	    insert into stat_ml_ptn_linear values 
	      (lr.description, lr.ymd, lr.sime, lr.jyocd, lr.raceno, lr.bettype, lr.kumiban, lr.pattern_name, lr.pattern_value,
	       lr.betcnt, lr.hitcnt, lr.betamt, lr.hitamt, lr.incomeamt, lr.hitrate, lr.incomerate, lr.hitamt_nolinear);
	    counter := counter + 1;
	  end loop;
	  close custat;
	  
      ptncounter := ptncounter + 1;
	end loop;
	close cuptn;
END
$$ LANGUAGE plpgsql;


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

DROP FUNCTION IF EXISTS create_stat_ml_ptn_totally (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400));
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_totally (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400)
) RETURNS VOID AS $$
DECLARE
	params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    raise info 'delete stat_ml_ptn_totally';
	delete from stat_ml_ptn_totally
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;

    raise info 'create temporary table (totally summary)';
	-- create temporary table (totally summary)
	drop table if exists tmp_stat_ml_ptn_totally;
	create table tmp_stat_ml_ptn_totally as
	select 
		description, 
		bettype, 
		kumiban, 
		pattern_name, 
		pattern_value, 
		sum(1) betcnt, 
		sum(case hitamt_nolinear when 0 then 0 else 1 end) hitcnt, 
		sum(100) betamt, 
		sum (hitamt_nolinear) hitamt 
	from stat_ml_ptn_linear linear
	where linear.description = paramDescription and linear.bettype = paramBettype and linear.kumiban = paramKumiban and linear.pattern_name = paramPatternName
	group by description, bettype, kumiban, pattern_name, pattern_value
	order by description, bettype, kumiban, pattern_name, pattern_value;
	
    raise info 'insert stat_ml_ptn_totally %', params;
	------- insert stat_ml_ptn_totally
	insert into stat_ml_ptn_totally
	select
	  tmp1.description, tmp1.bettype, tmp1.kumiban, tmp1.pattern_name, tmp1.pattern_value, 
	  tmp1.betcnt, tmp1.hitcnt, tmp1.betamt, tmp1.hitamt, tmp1.incomeamt, 
	  cast( (cast(betcnt as float)/ cast(betcnt_sum as float) *100) as numeric(7,3)) betrate,  
	  tmp1.hitrate, tmp1.incomerate,
	  cast( ((tmp1.hitrate * tmp1.incomerate * ((cast(betcnt as float)/ cast(betcnt_sum as float) * 100))) / 100) as numeric(7,2)) totalrate
	from 
	  (
	    select
	      description, bettype, kumiban, pattern_name, pattern_value, betcnt, hitcnt, betamt, hitamt, 
	      (hitamt - betamt) incomeamt,
	      cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(7,2)) hitrate,
	      cast( (cast(hitamt as float)/ cast(betamt as float) *100) as numeric(7,2)) incomerate
	    from tmp_stat_ml_ptn_totally
	  ) tmp1, 
	  (
	    select
	      description, bettype, kumiban, pattern_name, sum(betcnt) betcnt_sum
	    from tmp_stat_ml_ptn_totally
	    group by description, bettype, kumiban, pattern_name
	  ) tmp2
	where tmp1.description = tmp2.description
	  and tmp1.bettype = tmp2.bettype 
	  and tmp1.kumiban = tmp2.kumiban
	  and tmp1.pattern_name = tmp2.pattern_name
	order by description, bettype, kumiban, pattern_name, pattern_value;
END
$$ LANGUAGE plpgsql;


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

-------------------------------------------------------- working -----------------------------------------------

select * from stat_ml_ptn_final
where stable_years_count = 2 
  and betrate >= 0.2
  and hitrate >= 63
  and incomerate >= 10.5
order by description, bettype, kumiban, pattern_name, pattern_value
;

----------------- DDL -----------------------------------
drop table if exists stat_ml_result_ptn;
create table stat_ml_result_ptn (
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
bet_odds numeric(7,2),
bet_oddsrank int,
result_kumiban varchar(4),
result_odds numeric(7,2),
result_oddsrank int,
hity int, 
hitn int, 
betamt int,
hitamt int,
pattern_name varchar(400),
pattern_value varchar(400) 
);
create index indexes_stat_ml_result_ptn on stat_ml_result_ptn (description, ymd, jyocd, raceno, pattern_name, pattern_value);

drop table if exists stat_ml_result_ptn_analyze;
create table stat_ml_result_ptn_analyze (
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
bet_odds numeric(7,2),
bet_oddsrank int,
result_kumiban varchar(4),
result_odds numeric(7,2),
result_oddsrank int,
hity int, 
hitn int, 
betamt int,
hitamt int,
pattern_name varchar(400),
pattern_value varchar(400) 
);
create index indexes_stat_ml_result_ptn_analyze on stat_ml_result_ptn_analyze (description, ymd, jyocd, raceno, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_cnt;
create table stat_ml_ptn_cnt (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
pattern_cnt int
);
create index indexes_stat_ml_ptn_cnt on stat_ml_ptn_cnt (description, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_linear;
create table stat_ml_ptn_linear (
description varchar(200),
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
hitrate numeric(7,2),
incomerate numeric(7,2),
hitamt_nolinear int
);
create index indexes_stat_ml_ptn_linear on stat_ml_ptn_linear (description, ymd, sime, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_linear_analyze;
create table stat_ml_ptn_linear_analyze (
description varchar(200),
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
hitrate numeric(7,2),
incomerate numeric(7,2),
hitamt_nolinear int
);
create index indexes_stat_ml_ptn_linear_analyze on stat_ml_ptn_linear_analyze (description, ymd, sime, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_daily;
create table stat_ml_ptn_daily (
description varchar(200),
ymd varchar(8),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2), 
totalrate numeric(10,2) -- totalrate_slopeを求めるために必要
);
create index indexes_stat_ml_ptn_daily on stat_ml_ptn_daily (description, bettype, kumiban, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_totally;
create table stat_ml_ptn_totally (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2)
);
create index indexes_stat_ml_ptn_totally on stat_ml_ptn_totally (description, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_metric cascade;
create table stat_ml_ptn_metric (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
linear_betamt_slope numeric(19,2),
linear_hitamt_slope numeric(19,2),
linear_incomeamt_slope numeric(19,2),
linear_hitrate_slope numeric(19,2),
linear_incomerate_slope numeric(19,2),
hitamt_mod int,
hitamt_percent int,
hitamt_sum_over int,
hitamt_sum_under int,
hitamt_bias_rate numeric(19,2),
hit_bet_slope_rate numeric(19,2)
);
create index indexes_stat_ml_ptn_metric on stat_ml_ptn_metric (description, bettype, kumiban, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_metric_analyze cascade;
create table stat_ml_ptn_metric_analyze (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
linear_betamt_slope numeric(19,2),
linear_hitamt_slope numeric(19,2),
linear_incomeamt_slope numeric(19,2),
linear_hitrate_slope numeric(19,2),
linear_incomerate_slope numeric(19,2),
hitamt_mod int,
hitamt_percent int,
hitamt_sum_over int,
hitamt_sum_under int,
hitamt_bias_rate numeric(19,2),
hit_bet_slope_rate numeric(19,2)
);
create index indexes_stat_ml_ptn_metric_analyze on stat_ml_ptn_metric_analyze (description, bettype, kumiban, pattern_name);

drop table if exists stat_ml_ptn_final;
create table stat_ml_ptn_final (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2),
hitamt_bias_rate numeric(19,2),
hit_bet_slope_rate numeric(19,2),
linear_betamt_slope numeric(19,2),
linear_hitamt_slope numeric(19,2),
linear_incomeamt_slope numeric(19,2),
linear_hitrate_slope numeric(19,2),
linear_incomerate_slope numeric(19,2),
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
days_bet int,
days_plus int,
days_plus_rate numeric(7,2)
);
create index indexes_stat_ml_ptn_final on stat_ml_ptn_final (description, bettype, kumiban, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_analyze cascade;
create table stat_ml_ptn_analyze (
description varchar(400),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(40),
betcount_daily int,
betcnt int,
hitcnt int,
betamt int,
hitamt int,
incomeamt int,
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2),
hitamt_bias_rate numeric(19,2),
hit_bet_slope_rate numeric(19,2),
linear_betamt_slope numeric(19,2),
linear_hitamt_slope numeric(19,2),
linear_incomeamt_slope numeric(19,2),
linear_hitrate_slope numeric(19,2),
linear_incomerate_slope numeric(19,2),
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
range_oddsrank int[],
range_odds numeric(7,2)[],
range_totalrate numeric(7,2)[],
range_hitrate numeric(7,2)[],
range_incomerate numeric(7,2)[],
range_biasrate numeric(7,2)[],
isfinal varchar(1),
isCustom varchar(1),
updatetime timestamp default CURRENT_TIMESTAMP
);
create index indexes_stat_ml_ptn_analyze on stat_ml_ptn_analyze (bettype, kumiban, pattern_name);

drop table if exists stat_ml_ptn_filtered cascade;
create table stat_ml_ptn_filtered (
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(40),
betcount_daily int,
betamt int,
incomeamt int,
hitrate double precision,
incomerate double precision,
totalrate double precision,
hitrate_slope double precision,
incomerate_slope double precision,
incomeamt_slope double precision,
min_oddsrank int,
max_oddsrank int,
min_odds double precision,
max_odds double precision,
pattern_value varchar(400),
ptn_betamt int,
ptn_incomeamt int,
ptn_hitrate double precision,
ptn_incomerate double precision,
ptn_totalrate double precision,
ptn_days_bet int,
ptn_days_plus_rate double precision,
ptn_hitrate_slope double precision,
ptn_incomerate_slope double precision,
ptn_incomeamt_slope double precision
);
create index indexes_stat_ml_ptn_filtered on stat_ml_ptn_filtered (bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_result_weka;
create table stat_ml_result_weka (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
bet_odds numeric(7,2),
bet_oddsrank int,
result_kumiban varchar(4),
result_odds numeric(7,2),
result_oddsrank int,
hity int, 
hitn int, 
betamt int,
hitamt int,
pattern_name_count int,
pattern_value_count int 
);
create index indexes_stat_ml_result_weka on stat_ml_result_weka (ymd, jyocd, raceno, description);

drop table if exists stat_ml_result_wekaptn;
create table stat_ml_result_wekaptn (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
pattern_name varchar(40),
pattern_value varchar(400)
);
create index indexes_stat_ml_result_wekaptn on stat_ml_result_wekaptn (ymd, jyocd, raceno, description,bettype,bet_kumiban );

drop table if exists stat_ml_result_2;
create table stat_ml_result_2 (
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
description varchar(200),
bettype varchar(2),
bet_kumiban varchar(4),
bet_odds numeric(7,2),
bet_oddsrank int,
result_kumiban varchar(4),
result_odds numeric(7,2),
result_oddsrank int,
hity int, 
hitn int, 
betamt int,
hitamt int
);
create index indexes_stat_ml_result_2 on stat_ml_result_2 (ymd, jyocd, raceno, description, bettype);

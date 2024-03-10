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


-------------------------------------------------------- create procedure
DROP FUNCTION IF EXISTS check_pattern_count(VARCHAR(2), VARCHAR(4), VARCHAR(400));
CREATE OR REPLACE FUNCTION check_pattern_count(
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400)
) RETURNS int AS
$$
BEGIN
	truncate stat_ml_result_ptn;
	
    EXECUTE format ('
		insert into stat_ml_result_ptn
		select
		   stat.*,
		   ''%s'' pattern_name,
		   %s pattern_value
		from stat_ml_result stat, rec_race race
		where race.ymd = stat.ymd and race.jyocd = stat.jyocd and race.raceno = stat.raceno
		  and stat.bettype = ''%s'' and stat.bet_kumiban = ''%s''
	', paramPatternName, paramPatternValue, paramBettype, paramKumiban);
    return (
	  select count(distinct (pattern_name || pattern_value)) from stat_ml_result_ptn
    );
END
$$ LANGUAGE plpgsql;
-------------------------------------------------------- create procedure

DROP FUNCTION IF EXISTS output_weka_pattern(VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), VARCHAR(8), VARCHAR(8));
CREATE OR REPLACE FUNCTION output_weka_pattern(
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400),
paramFromYmd VARCHAR(8),
paramToYmd VARCHAR(8)
)
RETURNS VOID AS $$
  DECLARE
    cuptn refcursor; -- 패턴 커서
    recptn record; -- 패턴 레코드
    custat refcursor; -- 패턴결과테이블 커서
    recstat record; -- 패턴결과테이블 레코드
    lr stat_ml_ptn_linear%ROWTYPE; -- 축적결과테이블 행
    counter int; -- 행카운터
    ptncounter int; -- 패턴카운터
    ptntotal int;  -- 패턴총수
    finaltotal int; -- 최종 패턴 총수
  BEGIN
	truncate stat_ml_result_ptn;
	truncate stat_ml_ptn_linear;
	truncate stat_ml_ptn_daily;
	truncate stat_ml_ptn_totally;

    raise info 'insert stat_ml_result_ptn';
    EXECUTE format ('
		insert into stat_ml_result_ptn
		select
		   stat.*,
		   ''%s'' pattern_name,
		   %s pattern_value
		from stat_ml_result stat, rec_race race, rec_race_waku waku
		where race.ymd = stat.ymd and race.jyocd = stat.jyocd and race.raceno = stat.raceno
		  and race.ymd = waku.ymd and race.jyocd = waku.jyocd and race.raceno = waku.raceno
		  and stat.bettype = ''%s'' and stat.bet_kumiban = ''%s''
	', paramPatternName, paramPatternValue, paramBettype, paramKumiban);
	
	select into ptntotal count(distinct (pattern_name || pattern_value)) from stat_ml_result_ptn;
	
	raise info 'total pattern nums = %', ptntotal;
    -- 패턴에 대한 커서를 오픈해서 루프한다.
    ptncounter := 1;
    open cuptn for 
      select pattern_name, pattern_value 
      from stat_ml_result_ptn stat
      where stat.ymd >= paramFromYmd and stat.ymd <= paramToYmd
      group by pattern_name, pattern_value;
	loop
	  fetch cuptn into recptn;
	  if not found then 
	    exit;
	  end if;
	  
	  -- raise info 'processing patterns = % / %', ptncounter, ptntotal;
	  -- 각 패턴값에 대한 투표결과를 시간순으로 루프한다.
	  counter := 1;
      open custat for 
	    select * from stat_ml_result_ptn statptn
	    where statptn.pattern_name = recptn.pattern_name
	      and statptn.pattern_value = recptn.pattern_value
	      and statptn.ymd >= paramFromYmd and statptn.ymd <= paramToYmd
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
	    
	    insert into stat_ml_ptn_linear values 
	      (lr.description, lr.ymd, lr.sime, lr.jyocd, lr.raceno, lr.bettype, lr.kumiban, lr.pattern_name, lr.pattern_value,
	       lr.betcnt, lr.hitcnt, lr.betamt, lr.hitamt, lr.incomeamt, lr.hitrate, lr.incomerate);
	    counter := counter + 1;
	  end loop;
	  close custat;
	  
      ptncounter := ptncounter + 1;
	end loop;
	close cuptn;

    raise info 'create temporary table (daily summary)';
	-- create temporary table (daily summary)
	drop table if exists tmp_stat_ml_ptn_daily;
	create table tmp_stat_ml_ptn_daily as
	select 
		description, 
		ymd,
		bettype, 
		bet_kumiban kumiban, 
		pattern_name, 
		pattern_value, 
		sum(1) betcnt, 
		sum(hity) hitcnt, 
		sum(100) betamt, 
		sum ( case hity when 1 then (result_odds * 100) else 0 end ) hitamt 
	from stat_ml_result_ptn stat
	where stat.ymd >= paramFromYmd and stat.ymd <= paramToYmd
	group by description, ymd, bettype, kumiban, pattern_name, pattern_value
	order by description, ymd, bettype, kumiban, pattern_name, pattern_value;
	
    raise info 'insert stat_ml_ptn_daily';
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

    raise info 'create temporary table (totally summary)';
	-- create temporary table (totally summary)
	drop table if exists tmp_stat_ml_ptn_totally;
	create table tmp_stat_ml_ptn_totally as
	select 
		description, 
		bettype, 
		bet_kumiban kumiban, 
		pattern_name, 
		pattern_value, 
		sum(1) betcnt, 
		sum(hity) hitcnt, 
		sum(100) betamt, 
		sum ( case hity when 1 then (result_odds * 100) else 0 end ) hitamt 
	from stat_ml_result_ptn stat
	where stat.ymd >= paramFromYmd and stat.ymd <= paramToYmd
	group by description, bettype, kumiban, pattern_name, pattern_value
	order by description, bettype, kumiban, pattern_name, pattern_value;
	
    raise info 'insert stat_ml_ptn_totally';
	------- insert stat_ml_ptn_totally
	insert into stat_ml_ptn_totally
	select
	  tmp1.description, tmp1.bettype, tmp1.kumiban, tmp1.pattern_name, tmp1.pattern_value, 
	  tmp1.betcnt, tmp1.hitcnt, tmp1.betamt, tmp1.hitamt, tmp1.incomeamt, 
	  cast( (cast(betcnt as float)/ cast(betcnt_sum as float) *100) as numeric(7,3)) betrate,  
	  tmp1.hitrate, tmp1.incomerate,
	  -- cast( ((tmp1.hitrate * tmp1.incomerate) / 100) as numeric(7,2)) totalrate
	  cast( ((tmp1.hitrate * tmp1.incomerate * ((cast(betcnt as float)/ cast(betcnt_sum as float) * 100))) / 100) as numeric(7,2)) totalrate
	from 
	  (
	    select
	      description, bettype, kumiban, pattern_name, pattern_value, betcnt, hitcnt, betamt, hitamt, 
	      (hitamt - betamt) incomeamt,
	      cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(7,2)) hitrate,
	      cast( (cast(hitamt as float)/ cast(betamt as float) *100) as numeric(7,2)) incomerate
	    from tmp_stat_ml_ptn_totally
	    -- where (hitamt - betamt) > 0
	  ) tmp1, 
	  (
	    select
	      description, bettype, kumiban, pattern_name, sum(betcnt) betcnt_sum
	    from tmp_stat_ml_ptn_totally
	    -- where (hitamt - betamt) > 0  2019/4/28 comment out이 안되어있어서 추가
	    group by description, bettype, kumiban, pattern_name
	  ) tmp2
	where tmp1.description = tmp2.description
	  and tmp1.bettype = tmp2.bettype 
	  and tmp1.kumiban = tmp2.kumiban
	  and tmp1.pattern_name = tmp2.pattern_name
	  -- and tmp1.incomerate >= 100  -- 수익률 100%이상인 패턴만 추출한다.
	order by description, bettype, kumiban, pattern_name, pattern_value;

	------- delete stat_ml_final
	delete from stat_ml_ptn_final 
	  where bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;

    raise info 'insert stat_ml_ptn_final';
	------- insert stat_ml_final
	insert into stat_ml_ptn_final
	select
	  totally.description, 
	  totally.bettype, 
	  totally.kumiban, 
	  totally.pattern_name, 
	  totally.pattern_value, 
	  betcnt, 
	  hitcnt, 
	  betamt, 
	  hitamt, 
	  incomeamt, 
	  betrate, 
	  hitrate,
	  incomerate,
	  totalrate,
	  linear_hitrate_slope,
	  linear_incomerate_slope,
	  linear_incomeamt_slope,
	  daily_bet_count, 
	  daily_plus_count, 
	  daily_plus_rate
	from 
	  (
	    select
	      description, bettype, kumiban, pattern_name, pattern_value, betcnt, hitcnt, betamt, hitamt, incomeamt,
	      betrate, hitrate, incomerate, totalrate
	    from stat_ml_ptn_totally
	  ) totally, 
	  (
	    select
	      description, bettype, kumiban, pattern_name, pattern_value, 
	      cast(regr_slope(hitrate, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_hitrate_slope,
	      cast(regr_slope(incomerate, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_incomerate_slope,
	      cast(regr_slope(incomeamt, (ymd || sime)::bigint) * 10000000 as numeric(19,5)) linear_incomeamt_slope 
	    from stat_ml_ptn_linear
	    group by description, bettype, kumiban, pattern_name, pattern_value
	  ) linear_stat,
      (
        select 
          description, bettype, kumiban, pattern_name, pattern_value, 
          daily_bet_count, 
          daily_plus_count, 
          cast( (cast(daily_plus_count as float)/ cast(daily_bet_count as float) * 100) as numeric(7,2)) daily_plus_rate
        from 
        (
          select
            description, bettype, kumiban, pattern_name, pattern_value, 
            sum(1) daily_bet_count,
            sum( case when incomeamt > 0 then 1 else 0 end) daily_plus_count 
          from stat_ml_ptn_daily
          group by description, bettype, kumiban, pattern_name, pattern_value
        ) tmp
      ) daily_plus_cnt
	where 
	  totally.description = linear_stat.description 
	  and totally.bettype = linear_stat.bettype and totally.kumiban = linear_stat.kumiban 
	  and totally.pattern_name = linear_stat.pattern_name 
	  and totally.pattern_value = linear_stat.pattern_value 
	  and totally.description = daily_plus_cnt.description 
	  and totally.bettype = daily_plus_cnt.bettype and totally.kumiban = daily_plus_cnt.kumiban 
	  and totally.pattern_name = daily_plus_cnt.pattern_name 
	  and totally.pattern_value = daily_plus_cnt.pattern_value 
	  and totally.incomerate > 100 -- and totally.incomerate < 130-- 수익률 100-150%
	  -- and linear_stat.linear_incomeamt_slope > 0 -- 수익금 추이 plus
	  -- and linear_stat.linear_incomerate_slope > 0 -- 수익률 추이 plus
	order by description, bettype, kumiban, pattern_name, pattern_value;
	
    select into finaltotal count(*) from stat_ml_ptn_final
    where bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;
    
    raise info 'total final pattern nums = %', finaltotal;
  END;
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
create index indexes_stat_ml_result_ptn on stat_ml_result_ptn (description, ymd, pattern_name, pattern_value);

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
incomerate numeric(7,2)
);
create index indexes_sstat_ml_ptn_linear on stat_ml_ptn_linear (description, ymd, sime, bettype, kumiban, pattern_name, pattern_value);

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
linear_hitrate_slope numeric(19,5),
linear_incomerate_slope numeric(19,5),
linear_incomeamt_slope numeric(19,5),
days_bet int,
days_plus int,
days_plus_rate numeric(7,2)
);
create index indexes_stat_ml_ptn_final on stat_ml_ptn_final (description, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_analyze cascade;
create table stat_ml_ptn_analyze (
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
linear_hitrate_slope numeric(19,2),
linear_incomerate_slope numeric(19,2),
linear_incomeamt_slope numeric(19,2),
range_oddsrank int[],
range_odds numeric(7,2)[],
range_totalrate numeric(7,2)[],
range_hitrate numeric(7,2)[],
range_incomerate numeric(7,2)[],
description varchar(400),
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

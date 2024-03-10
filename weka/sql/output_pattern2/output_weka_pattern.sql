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


-- ！！！ output_weka_patternが必ず事前に実行されていること
DROP FUNCTION IF EXISTS finalize_manual_pattern (VARCHAR(200), VARCHAR(2), VARCHAR(4)
);
CREATE OR REPLACE FUNCTION finalize_manual_pattern (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4)
) RETURNS VOID AS $$
DECLARE
    params text;
BEGIN
  -- TODO   stat_ml_ptn_final테이블 재생성할 것
  -- 직전 2년치 final을 상호 비교하여 안정성, (향상성은 좀 신뢰성이 떨어진다)을 필터링한다.
  -- 2년치만으로 하는 이유는 선수기량이 판단기준이므로 최신성이 중요할 것으로 추정하기 때문이다.

  params := paramDescription || '_' || paramBettype || '_' || paramKumiban;
  
  raise info 'finalize_manual_pattern start %', params;
  raise info '  delete stat_ml_ptn_final %', params;
  delete from stat_ml_ptn_final 
  where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban;
  
  -- 직전2년치의 년도별 일시 테이블을 생성한다.
  drop table if exists tmp_final_last1; 
  drop table if exists tmp_final_last2;
  
  create table tmp_final_last1 as select * from stmp_stat_ml_ptn_final where yyyy = '2018';
  create table tmp_final_last2 as select * from stmp_stat_ml_ptn_final where yyyy = '2017';
  
  --1차 릴리즈 타겟
  -- 3T-321
  if paramBetType = '3T' and paramKumiban = '321' then
	  raise info '  insert stat_ml_ptn_final 3T-321';
  -- 3T-312
  elsif paramBetType = '3T' and paramKumiban = '312' then
	  raise info '  insert stat_ml_ptn_final 3T-312';
  -- 3T-132
  elsif paramBetType = '3T' and paramKumiban = '132' then
	  raise info '  insert stat_ml_ptn_final 3T-132';
  -- 3T-213
  elsif paramBetType = '3T' and paramKumiban = '213' then
	  raise info '  insert stat_ml_ptn_final 3T-213';
  -- 3T-123
  elsif paramBetType = '3T' and paramKumiban = '123' then
	  raise info '  insert stat_ml_ptn_final 3T-123';
  -- 2T-31
  elsif paramBetType = '2T' and paramKumiban = '31' then
	  raise info '  insert stat_ml_ptn_final 2T-31';
  -- 2T-21
  elsif paramBetType = '2T' and paramKumiban = '21' then
	  raise info '  insert stat_ml_ptn_final 2T-21';
	  insert into stat_ml_ptn_final
	  select f1.*, 1 
	  from tmp_final_last2 f2, 
	  (  select * from tmp_final_last1 where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban
		 order by (betcnt * hitrate) desc limit 10000 -- (투표회수 * 적중률) 순위의 절대 기준치
	  ) f1
	  where f1.description = paramDescription and f1.bettype = paramBettype and f1.kumiban = paramKumiban
	     and f1.description = f2.description and f1.bettype = f2.bettype and f1.kumiban = f2.kumiban
	     and f1.pattern_name = f2.pattern_name and f1.pattern_value = f2.pattern_value
	     ;
  -- 1T-2
  elsif paramBetType = '1T' and paramKumiban = '2' then
	  raise info '  insert stat_ml_ptn_final 1T-2';
	  insert into stat_ml_ptn_final
	  select f1.*, 1 
	  from tmp_final_last2 f2, 
	  (  select * from tmp_final_last1 where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban
		 order by (betcnt * hitrate) desc limit 10000 -- (투표회수 * 적중률) 순위의 절대 기준치
	  ) f1
	  where f1.description = paramDescription and f1.bettype = paramBettype and f1.kumiban = paramKumiban
	     and f1.description = f2.description and f1.bettype = f2.bettype and f1.kumiban = f2.kumiban
	     and f1.pattern_name = f2.pattern_name and f1.pattern_value = f2.pattern_value
	     and ( -- 수익률 일관성
	       (f1.incomerate > 100 and f2.incomerate > 97)
	     )
	     and ( -- 최근 적자일수비율의 전기간 적자일수비율에 대한 건전성 상대기준치 등급 1
	       (f2.minus_days_rate = 0) or 
	       (f1.minus_days_rate / f2.minus_days_rate) <= 1
	     )
	     and ( -- 최근 적자일수의 건전성 절대 기준치 
	       (f1.minus_days_rate < 0.05)
	     )
	     and ( -- 최근 투표회수의 절대 기준치
	       (f1.betcnt > 5)
	     );
  -- 1T-1
  elsif paramBetType = '1T' and paramKumiban = '1' then
	  raise info '  insert stat_ml_ptn_final 1T-1';
	  insert into stat_ml_ptn_final
	  select f1.*, 1 
	  from tmp_final_last2 f2, 
	  (  select * from tmp_final_last1 where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban
		 order by (betcnt * hitrate) desc limit 10000 -- (투표회수 * 적중률) 순위의 절대 기준치
	  ) f1
	  where f1.description = paramDescription and f1.bettype = paramBettype and f1.kumiban = paramKumiban
	     and f1.description = f2.description and f1.bettype = f2.bettype and f1.kumiban = f2.kumiban
	     and f1.pattern_name = f2.pattern_name and f1.pattern_value = f2.pattern_value
	     and ( -- 수익률 일관성
	       (f1.incomerate > 97 and f2.incomerate > 97)
	     )
	     and ( -- 최근 적자일수비율의 전기간 적자일수비율에 대한 건전성 상대기준치 등급 1
	       (f2.minus_days_rate = 0) or 
	       (f1.minus_days_rate / f2.minus_days_rate) <= 1
	     )
	     and ( -- 최근 적자일수의 건전성 절대 기준치 
	       (f1.minus_days_rate < 0.05)
	     )
	     and ( -- 최근 투표회수의 절대 기준치
	       (f1.betcnt > 10)
	     );
  end if;
END;
$$ LANGUAGE plpgsql;

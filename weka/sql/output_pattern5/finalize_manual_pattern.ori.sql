-- ！！！ output_weka_patternが必ず事前に実行されていること
DROP FUNCTION IF EXISTS finalize_manual_pattern(VARCHAR(200), VARCHAR(2), VARCHAR(4),  
  VARCHAR(10), VARCHAR(10), VARCHAR(10), VARCHAR(10), VARCHAR(10), VARCHAR(10));
CREATE OR REPLACE FUNCTION finalize_manual_pattern (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4)
) RETURNS VOID AS $$
DECLARE
    params text;
BEGIN
  -- TODO   stat_ml_ptn_final테이블 재생성할 것

  params := paramDescription || '_' || paramBettype || '_' || paramKumiban;
  
  raise info 'finalize_manual_pattern start %', params;
  raise info '  delete stat_ml_ptn_final %', params;
  delete from stat_ml_ptn_final 
  where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban;
  
  drop table if exist tmp_final;
  
  select * into tmp_final from stmp_stat_ml_ptn_final
  where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban
  order by (betcnt * hitrate) desc
  limit 1000;  -- (투표회수 * 적중률) 순위의 절대 기준치
  
  -- 수익성 전망 등급 1
  raise info '  insert stat_ml_ptn_final level1 %', params;
  insert into stat_ml_ptn_final
  select f2018.*, 1 
  from tmp_final f2013, stmp_ml_ptn_final_2018 f2018
  where f2013.description = paramDescription and f2013.bettype = paramBettype and f2013.kumiban = paramKumiban
     and f2013.description = f2018.description and f2013.bettype = f2018.bettype and f2013.kumiba = f2018.kumiban
     and f2013.pattern_name = f2018.pattern_name and f2013.pattern_value = f2018.pattern_value
     and ( -- 수익률 일관성
       f2018.incomerate > 100 and f2013.incomerate > 100 
     )
     and ( -- 최근 적자일수비율의 전기간 적자일수비율에 대한 건전성 상대기준치 등급 1
       (f2018.minus_days_rate / f2013.minus_days_rate) < 0.5
     )
     and ( -- 최근 적자일수의 건전성 절대 기준치 
       (f2018.minus_days_rate < 0.5)
     )
     and ( -- 최근 투표회수의 절대 기준치
       (f2018.betcnt > 365)
     );
  
  -- 수익성 전망 등급 2
  raise info '  insert stat_ml_ptn_final level2 %', params;
  insert into stat_ml_ptn_final
  select f2018.*, 2
  from tmp_final f2013, stmp_ml_ptn_final_2018 f2018
  where f2013.description = paramDescription and f2013.bettype = paramBettype and f2013.kumiban = paramKumiban
     and f2013.description = f2018.description and f2013.bettype = f2018.bettype and f2013.kumiba = f2018.kumiban
     and f2013.pattern_name = f2018.pattern_name and f2013.pattern_value = f2018.pattern_value
     and ( -- 수익률 일관성
       f2018.incomerate > 100 and f2013.incomerate > 100 
     )
     and ( -- 최근 적자일수비율의 전기간 적자일수비율에 대한 건전성 상대기준치 등급 2
           (f2018.minus_days_rate / f2013.minus_days_rate) >= 0.5 
       and (f2018.minus_days_rate / f2013.minus_days_rate) < 1.5
     )
     and ( -- 최근 적자일수의 건전성 절대 기준치 
       (f2018.minus_days_rate < 0.5)
     )
     and ( -- 최근 투표회수의 절대 기준치
       (f2018.betcnt > 365)
     );
     
  -- 수익성 전망 등급 3
  raise info '  insert stat_ml_ptn_final level3 %', params;
  insert into stat_ml_ptn_final
  select f2018.*, 3
  from tmp_final f2013, stmp_ml_ptn_final_2018 f2018
  where f2013.description = paramDescription and f2013.bettype = paramBettype and f2013.kumiban = paramKumiban
     and f2013.description = f2018.description and f2013.bettype = f2018.bettype and f2013.kumiba = f2018.kumiban
     and f2013.pattern_name = f2018.pattern_name and f2013.pattern_value = f2018.pattern_value
     and ( -- 수익률 일관성
       f2018.incomerate > 100 and f2013.incomerate <= 100 
     )
     and ( -- 최근 적자일수비율의 전기간 적자일수비율에 대한 건전성 상대기준치 등급 3 
       (f2018.minus_days_rate / f2013.minus_days_rate) < 0.2
     )
     and ( -- 최근 적자일수의 건전성 절대 기준치 
       (f2018.minus_days_rate < 0.5)
     )
     and ( -- 최근 투표회수의 절대 기준치
       (f2018.betcnt > 365)
     );
     
  -- 수익성 전망 등급 4
  raise info '  insert stat_ml_ptn_final level3 %', params;
  insert into stat_ml_ptn_final
  select f2018.*, 3
  from tmp_final f2013, stmp_ml_ptn_final_2018 f2018
  where f2013.description = paramDescription and f2013.bettype = paramBettype and f2013.kumiban = paramKumiban
     and f2013.description = f2018.description and f2013.bettype = f2018.bettype and f2013.kumiba = f2018.kumiban
     and f2013.pattern_name = f2018.pattern_name and f2013.pattern_value = f2018.pattern_value
     and ( -- 수익률 일관성
           f2018.incomerate <= 100
       and (f2018.incomerate >= 97) 
     )
     and ( -- 최근 적자일수비율의 전기간 적자일수비율에 대한 건전성 상대기준치 등급 3 
       (f2018.minus_days_rate / f2013.minus_days_rate) < 0.2
     )
     and ( -- 최근 적자일수의 건전성 절대 기준치 
       (f2018.minus_days_rate < 0.5)
     )
     and ( -- 최근 투표회수의 절대 기준치
       (f2018.betcnt > 365)
     );
END;
$$ LANGUAGE plpgsql;

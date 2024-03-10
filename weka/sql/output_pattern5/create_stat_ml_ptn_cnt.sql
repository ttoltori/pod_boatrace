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
	
	-- 전회 실행결과 final수익률이 95이하였던 패턴값들은 제외시킨다.
	insert into stat_ml_ptn_cnt
	select * from 
	(
      select 
         description, bettype, bet_kumiban kumiban, pattern_name, pattern_value, count(*) pattern_cnt
      from stat_ml_result_ptn stat
      -- , stmp_ptn_values_over95 over95
      where description =  paramDescription and bettype = paramBettype and bet_kumiban = paramKumiban and pattern_name = paramPatternName
        -- and stat.description = over95.description and stat.bettype = over95.bettype 
        -- and stat.bet_kumiban = over95.kumiban and stat.pattern_name = over95.pattern_name 
        -- and stat.pattern_value = over95.pattern_value
      group by description, bettype, bet_kumiban, pattern_name, pattern_value
      order by description, bettype, bet_kumiban, pattern_cnt desc
	) tmp;
END
$$ LANGUAGE plpgsql;

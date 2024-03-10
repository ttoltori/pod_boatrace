DROP FUNCTION IF EXISTS output_weka_pattern(VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), 
  int, float, VARCHAR(8), VARCHAR(8));
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
    filepath text; -- 최종패턴내용 파일로 백업
  BEGIN
	-- 일회성 테이블의 같은 승식,구미방에 대해서 기존 데이터를 삭제한다. (성능이유)
	-- delete from stat_ml_result_ptn where description = paramDescription and bettype = paramBettype and bet_kumiban = paramKumiban; 
	-- delete from stat_ml_ptn_cnt where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban;
	-- delete from stat_ml_ptn_linear where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban;
	-- delete from stat_ml_ptn_totally where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban;
  
	delete from stat_ml_result_ptn;
	delete from stat_ml_ptn_cnt;
	-- 성능문제 insert into stat_ml_ptn_linear_bak select * from stat_ml_ptn_linear;
	delete from stat_ml_ptn_linear;
	delete from stat_ml_ptn_totally;
	delete from stat_ml_ptn_totally_stmp;
	delete from stat_ml_ptn_metric;
	
	perform create_stat_ml_result_ptn(paramDescription, paramBettype, paramKumiban, paramPatternName, paramPatternValue, paramFromYmd, paramToYmd);

	perform create_stat_ml_ptn_cnt(paramDescription, paramBettype, paramKumiban, paramPatternName);

	perform create_stat_ml_ptn_linear(paramDescription, paramBettype, paramKumiban, paramPatternName, paramMinimumBetCount);

	perform create_stat_ml_ptn_totally(paramDescription, paramBettype, paramKumiban, paramPatternName);

	perform create_stat_ml_ptn_metric(paramDescription, paramBettype, paramKumiban, paramPatternName, paramMetricBiasPercent);
	
	perform create_stat_ml_ptn_final(paramDescription, paramBettype, paramKumiban, paramPatternName, paramMinimumBetCount);

--    select into finaltotal count(*) from stat_ml_ptn_final
--    where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;
--    raise info 'total final pattern nums = %', finaltotal;
    
--    EXECUTE format ('
--		copy (
--		  select * from stat_ml_ptn_final
--		  where description = ''%s'' and bettype = ''%s'' and kumiban = ''%s'' and betcnt_limit = %s and pattern_name = ''%s''
--		) to ''%s'' with binary
--	', paramDescription, paramBettype, paramKumiban, paramMinimumBetCount, paramPatternName, filepath);
  END;
$$ LANGUAGE plpgsql;

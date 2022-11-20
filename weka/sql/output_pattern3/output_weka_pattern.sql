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

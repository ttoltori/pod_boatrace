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
    ptncnt int; 
  BEGIN
    -- !!! 기간지정은 년단위로 해야한다. TODO 향후 년단위로 입력받을수 있도록 수정 
    
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
	
	-- 일단은 범위 to 날짜로부터 년도를 취득하여 넘긴다. 
	perform create_stat_ml_ptn_final(paramDescription, paramBettype, paramKumiban, paramPatternName, paramMinimumBetCount, 
	  substring(paramToYmd from 1 for 4) );

  END;
$$ LANGUAGE plpgsql;

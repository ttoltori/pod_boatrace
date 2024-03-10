-- ！！！ finalize_weka_patternが必ず事前に実行されていること
DROP FUNCTION IF EXISTS analyze_weka_pattern(VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), 
  VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), float, 
  VARCHAR(50), VARCHAR(50), int, VARCHAR(50), VARCHAR(1), VARCHAR(1));
CREATE OR REPLACE FUNCTION analyze_weka_pattern(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400), 
paramPatternValue VARCHAR(400),
paramArrYmdStr VARCHAR(50),
paramArrOddsRankStr VARCHAR(50),
paramArrOddsStr VARCHAR(50),
paramArrTotalRateStr VARCHAR(50), -- default
paramArrHitRateStr VARCHAR(50), -- default
paramArrIncomeRateStr VARCHAR(50),  -- default
paramMetricBiasPercent float, -- default
paramArrBiasRateStr VARCHAR(50),  -- default
paramArrMinusDaysRateStr VARCHAR(50),  -- default
paramMinusChangedCount int, -- default
paramArrBetCountStr VARCHAR(50),  -- default
paramIsFinal VARCHAR(1),
paramIsCustom VARCHAR(1)
) RETURNS SETOF stat_ml_ptn_analyze AS $$
  DECLARE
    params text;
	paramArrYmd VARCHAR(8)[];
	paramArrOddsRank int[];
	paramArrOdds numeric(7,2)[];
	paramArrTotalRate numeric(7,2)[];
	paramArrHitRate numeric(7,2)[];
	paramArrIncomeRate numeric(7,2)[]; 
	paramArrBiasRate numeric(7,2)[];
	paramArrMinusDaysRate numeric(7,2)[];
	paramArrBetCount int[];
  BEGIN
  
	-- 일회성 테이블의 같은 승식,구미방에 대해서 기존 데이터를 삭제한다. (성능이유)
	-- delete from stat_ml_result_ptn_analyze where description = paramDescription and bettype = paramBettype and bet_kumiban = paramKumiban;
	-- delete from stat_ml_ptn_linear_analyze where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban;
	
	truncate stat_ml_result_ptn_analyze;
	
	select into paramArrYmd string_to_array(paramArrYmdStr, '~');
	select into paramArrOddsRank string_to_array(paramArrOddsRankStr, '~')::int[];
	select into paramArrOdds string_to_array(paramArrOddsStr, '~')::float[];
	select into paramArrTotalRate string_to_array(paramArrTotalRateStr, '~')::float[];
	select into paramArrHitRate string_to_array(paramArrHitRateStr, '~')::float[];
	select into paramArrIncomeRate string_to_array(paramArrIncomeRateStr, '~')::float[];
	select into paramArrBiasRate string_to_array(paramArrBiasRateStr, '~')::float[];
	select into paramArrMinusDaysRate string_to_array(paramArrMinusDaysRateStr, '~')::float[];
	select into paramArrBetCount string_to_array(paramArrBetCountStr, '~')::int[];
	
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    perform create_stat_ml_result_ptn_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, paramPatternValue, paramArrYmd[1], paramArrYmd[2]);
  
	perform create_stat_ml_ptn_linear_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, 
			paramArrOddsRank, paramArrOdds, paramArrTotalRate, paramArrHitRate, paramArrIncomeRate, paramArrBiasRate,
			paramArrMinusDaysRate, paramMinusChangedCount, paramArrBetCount);
	
	perform create_stat_ml_ptn_metric_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, paramMetricBiasPercent);
	
	perform create_stat_ml_ptn_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, 
			paramArrOddsRank, paramArrOdds, paramArrTotalRate, paramArrHitRate, paramArrIncomeRate, paramArrBiasRate, 
			paramArrMinusDaysRate, paramMinusChangedCount, paramArrBetCount, paramIsFinal, paramIsCustom);
	
    return query 
      select * from stat_ml_ptn_analyze 
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName
        and isfinal = paramIsFinal and iscustom = paramIsCustom;
			
  END;
$$ LANGUAGE plpgsql;

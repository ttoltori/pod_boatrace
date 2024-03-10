DROP FUNCTION IF EXISTS weka_output_analyze(
  VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), int, float, VARCHAR(50), VARCHAR(50),
  VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), 
  VARCHAR(1), VARCHAR(1));
CREATE OR REPLACE FUNCTION weka_output_analyze(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400),
paramMinimumBetCount int, -- bet minimum
paramMetricBiasPercent float, -- hitamt_nolinear bias percentage limit
paramArrPatternYmdStr VARCHAR(50),
paramArrAnalyzeYmdStr VARCHAR(50),
paramArrOddsRankStr VARCHAR(50),
paramArrOddsStr VARCHAR(50),
paramArrTotalRateStr VARCHAR(50),
paramArrHitRateStr VARCHAR(50),
paramArrIncomeRateStr VARCHAR(50), 
paramArrBiasRateStr VARCHAR(50), 
paramIsFinal VARCHAR(1),
paramIsCustom VARCHAR(1)
)
RETURNS VOID AS $$
  DECLARE
    params text;
    qualifiedTotal int; -- 수익률이 합격한 패턴의 충수
	paramArrPatternYmd VARCHAR(8)[];
	paramArrIncomeRate numeric(7,2)[];
  BEGIN
	
  	select into paramArrPatternYmd string_to_array(paramArrPatternYmdStr, '~');
  	select into paramArrIncomeRate string_to_array(paramArrIncomeRateStr, '~')::float[];
  	
    perform output_weka_pattern(
      paramDescription, paramBettype, paramKumiban, paramPatternName, paramPatternValue, 
      paramMinimumBetCount, paramMetricBiasPercent, paramArrPatternYmd[1], paramArrPatternYmd[2]);
    
    select into qualifiedTotal count(*) from stat_ml_ptn_final
    where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName
	  and incomerate >= paramArrIncomeRate[1] and incomerate <= paramArrIncomeRate[2];
	
	raise info 'qualified total final pattern nums = %', qualifiedTotal;
	
	if qualifiedTotal > 0 then
	    perform analyze_weka_pattern(
	      paramDescription, paramBettype, paramKumiban, paramPatternName, paramPatternValue,
	      paramArrAnalyzeYmdStr, paramArrOddsRankStr, paramArrOddsStr, paramArrTotalRateStr, paramArrHitRateStr, paramArrIncomeRateStr,
	      paramMetricBiasPercent, paramArrBiasRateStr, paramIsFinal, paramIsCustom);
	end if;
    
  END;
$$ LANGUAGE plpgsql;

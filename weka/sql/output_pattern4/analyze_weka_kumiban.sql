-- ！！！ analyze_weka_patternが必ず事前に実行されていること
DROP FUNCTION IF EXISTS analyze_weka_kumiban(VARCHAR(200), VARCHAR(2), VARCHAR(4),  
  VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), float, 
  VARCHAR(50), VARCHAR(50), int, VARCHAR(50), VARCHAR(1), VARCHAR(1));
CREATE OR REPLACE FUNCTION analyze_weka_kumiban(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramArrYmdStr VARCHAR(50),
paramArrOddsRankStr VARCHAR(50),
paramArrOddsStr VARCHAR(50),
paramArrTotalRateStr VARCHAR(50),
paramArrHitRateStr VARCHAR(50),
paramArrIncomeRateStr VARCHAR(50), 
paramMetricBiasPercent float,
paramArrBiasRateStr VARCHAR(50), 
paramArrMinusDaysRateStr VARCHAR(50), 
paramMinusChangedCount int,
paramArrBetCountStr VARCHAR(50), 
paramIsFinal VARCHAR(1),
paramIsCustom VARCHAR(1)
) RETURNS SETOF stat_ml_kumiban_analyze AS $$
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
  
	select into paramArrYmd string_to_array(paramArrYmdStr, '~');
	select into paramArrOddsRank string_to_array(paramArrOddsRankStr, '~')::int[];
	select into paramArrOdds string_to_array(paramArrOddsStr, '~')::float[];
	select into paramArrTotalRate string_to_array(paramArrTotalRateStr, '~')::float[];
	select into paramArrHitRate string_to_array(paramArrHitRateStr, '~')::float[];
	select into paramArrIncomeRate string_to_array(paramArrIncomeRateStr, '~')::float[];
	select into paramArrBiasRate string_to_array(paramArrBiasRateStr, '~')::float[];
	select into paramArrMinusDaysRate string_to_array(paramArrMinusDaysRateStr, '~')::float[];
	select into paramArrBetCount string_to_array(paramArrBetCountStr, '~')::int[];
	
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban;
    
	perform create_stat_ml_kumiban_linear_analyze(paramDescription, paramBettype, paramKumiban);
	
	perform create_stat_ml_kumiban_metric_analyze(paramDescription, paramBettype, paramKumiban, paramMetricBiasPercent);
	
	perform create_stat_ml_kumiban_analyze(paramDescription, paramBettype, paramKumiban, 
			paramArrOddsRank, paramArrOdds, paramArrTotalRate, paramArrHitRate, paramArrIncomeRate, paramArrBiasRate, 
			paramArrMinusDaysRate, paramMinusChangedCount, paramArrBetCount, paramIsFinal, paramIsCustom);
			
  	truncate stat_ml_ptn_linear_analyze;
	
    return query 
      select * from stat_ml_kumiban_analyze 
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban 
        and isfinal = paramIsFinal and iscustom = paramIsCustom;
			
  END;
$$ LANGUAGE plpgsql;

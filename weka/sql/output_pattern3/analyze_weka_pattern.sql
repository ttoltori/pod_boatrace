-- ！！！ output_weka_patternが必ず事前に実行されていること
DROP FUNCTION IF EXISTS analyze_weka_pattern(VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), 
  VARCHAR(8)[], int[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], 
  float, numeric(7,2)[], VARCHAR(1), VARCHAR(1));
CREATE OR REPLACE FUNCTION analyze_weka_pattern(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400), -- 식별용 참고항목
paramPatternValue VARCHAR(400), -- 식별용 참고항목
paramArrYmd VARCHAR(8)[],
paramArrOddsRank int[],
paramArrOdds numeric(7,2)[],
paramArrTotalRate numeric(7,2)[],
paramArrHitRate numeric(7,2)[],
paramArrIncomeRate numeric(7,2)[], 
paramMetricBiasPercent float,
paramArrBiasRate numeric(7,2)[], 
paramIsFinal VARCHAR(1),
paramIsCustom VARCHAR(1)
) RETURNS SETOF stat_ml_ptn_analyze AS $$
  DECLARE
    params text;
  BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    perform create_stat_ml_result_ptn_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, paramPatternValue, paramArrYmd[1], paramArrYmd[2]);
  
	perform create_stat_ml_ptn_linear_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, 
			paramArrOddsRank, paramArrOdds, paramArrTotalRate, paramArrHitRate, paramArrIncomeRate);
	
	perform create_stat_ml_ptn_metric_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, paramMetricBiasPercent);
	
	perform create_stat_ml_ptn_analyze(paramDescription, paramBettype, paramKumiban, paramPatternName, 
			paramArrOddsRank, paramArrOdds, paramArrTotalRate, paramArrHitRate, paramArrIncomeRate, paramArrBiasRate, paramIsFinal, paramIsCustom);
	
    -- bakcup
    drop table if exists stat_ml_ptn_analyze_bak;
	create table stat_ml_ptn_analyze_bak as
	select * from stat_ml_ptn_analyze;
    
    return query 
      select * from stat_ml_ptn_analyze 
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName
        and isfinal = paramIsFinal and iscustom = paramIsCustom;
  END;
$$ LANGUAGE plpgsql;

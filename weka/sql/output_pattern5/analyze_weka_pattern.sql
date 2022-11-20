-- ！！！ output_weka_patternが必ず事前に実行されていること
DROP FUNCTION IF EXISTS analyze_weka_pattern(VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), 
  VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), VARCHAR(50), float, 
  VARCHAR(50), VARCHAR(50), VARCHAR(2), VARCHAR(50), VARCHAR(10), VARCHAR(10), VARCHAR(10), VARCHAR(10), VARCHAR(10), VARCHAR(10), 
  VARCHAR(1), VARCHAR(1));
CREATE OR REPLACE FUNCTION analyze_weka_pattern(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400), -- 식별용 참고항목
paramPatternValue VARCHAR(400), -- 식별용 참고항목
paramArrYmdStr VARCHAR(50),
paramArrOddsRankStr VARCHAR(50),
paramArrOddsStr VARCHAR(50),
paramArrTotalRateStr VARCHAR(50),
paramArrHitRateStr VARCHAR(50),
paramArrIncomeRateStr VARCHAR(50), 
paramMetricBiasPercent float,
paramArrBiasRateStr VARCHAR(50), 
paramArrMinusDaysRateStr VARCHAR(50), 
paramMinusChangedCountStr VARCHAR(2),
paramArrBetCountStr VARCHAR(50), 
paramPercentIncomeAmtStr VARCHAR(10),  -- final조건 투표회수 상위 비율
paramPercentHitRateStr VARCHAR(10), -- final조건 적중률 상위 비율
paramPercentIncomeRateStr VARCHAR(10), -- final조건 수익률 상위 비율
paramPercentMinusDaysRateStr VARCHAR(10), -- final조건 적자일수 상위 비율
paramPercentIncomeAmtSlopeStr VARCHAR(10), -- final조건 수익금액상승기울기
paramPercentRecoverPlusRateStr VARCHAR(10), -- final조건 적중흑자증가투표 비율
paramIsFinal VARCHAR(1),
paramIsCustom VARCHAR(1)
) RETURNS VOID AS $$
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
	paramMinusChangedCount int;
	paramPercentIncomeAmt numeric(7,2);
	paramPercentHitRate numeric(7,2);
	paramPercentIncomeRate numeric(7,2);
	paramPercentMinusDaysRate numeric(7,2);
	paramPercentIncomeAmtSlope numeric(7,2);
	paramPercentRecoverPlusRate numeric(7,2);
	pattern_count int;
  BEGIN
    -- 대상패턴이 없으면 리턴한다.
    select into pattern_count count(*) from stat_ml_ptn_final
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban 
        and pattern_name = paramPatternName;
    
    if pattern_count = 0 then
      return;
    end if;
  
    paramMinusChangedCount := cast(paramMinusChangedCountStr as int);
	paramPercentIncomeAmt := cast(paramPercentIncomeAmtStr as numeric(7,2));
	paramPercentHitRate := cast(paramPercentHitRateStr as numeric(7,2));
	paramPercentIncomeRate := cast(paramPercentIncomeRateStr as numeric(7,2));
	paramPercentMinusDaysRate := cast(paramPercentMinusDaysRateStr as numeric(7,2));
	paramPercentIncomeAmtSlope := cast(paramPercentIncomeAmtSlopeStr as numeric(7,2));
	paramPercentRecoverPlusRate := cast(paramPercentRecoverPlusRateStr as numeric(7,2));
  
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
			paramArrMinusDaysRate, paramMinusChangedCount, paramArrBetCount,
			paramPercentIncomeAmt, paramPercentHitRate, paramPercentIncomeRate, paramPercentMinusDaysRate,
			paramPercentIncomeAmtSlope, paramPercentRecoverPlusRate,
			paramIsFinal, paramIsCustom);
  END;
$$ LANGUAGE plpgsql;

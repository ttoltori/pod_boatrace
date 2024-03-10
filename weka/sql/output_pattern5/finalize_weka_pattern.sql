-- ！！！ output_weka_patternが必ず事前に実行されていること
DROP FUNCTION IF EXISTS finalize_weka_kumiban(VARCHAR(200), VARCHAR(2), VARCHAR(4),  
  VARCHAR(10), VARCHAR(10), VARCHAR(10), VARCHAR(10), VARCHAR(10), VARCHAR(10));
CREATE OR REPLACE FUNCTION finalize_weka_kumiban(
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPercentIncomeAmtStr VARCHAR(10),
paramPercentHitRateStr VARCHAR(10),
paramPercentIncomeRateStr VARCHAR(10),
paramPercentMinusDaysRateStr VARCHAR(10),
paramPercentIncomeAmtSlopeStr VARCHAR(10),
paramPercentRecoverPlusRateStr VARCHAR(10)
) RETURNS VOID AS $$
DECLARE
    params text;
	paramPercentIncomeAmt float;
	paramPercentHitRate float;
	paramPercentIncomeRate float;
	paramPercentMinusDaysRate float;
    paramPercentIncomeAmtSlope float;
    paramPercentRecoverPlusRate float;
    paramValueIncomeAmt float;
    paramValueHitRate float;
    paramValueIncomeRate float;
    paramValueMinusDaysRate float;
    paramValueIncomeAmtSlope float;
    paramValueRecoverPlusRate float;
BEGIN
  paramPercentIncomeAmt := cast(paramPercentIncomeAmtStr as float);
  paramPercentHitRate := cast(paramPercentHitRateStr as float);
  paramPercentIncomeRate := cast(paramPercentIncomeRateStr as float);
  paramPercentMinusDaysRate := cast(paramPercentMinusDaysRateStr as float);
  paramPercentIncomeAmtSlope := cast(paramPercentIncomeAmtSlopeStr as float);
  paramPercentRecoverPlusRate := cast(paramPercentRecoverPlusRateStr as float);
  
  params := paramDescription || '_' || paramBettype || '_' || paramKumiban
  			 || '_' || paramPercentIncomeAmtStr || '_' ||  paramPercentHitRateStr || '_' || paramPercentIncomeRateStr
  			 || '_' || paramPercentMinusDaysRateStr || '_' ||  paramPercentIncomeAmtSlopeStr || '_' || paramPercentRecoverPlusRateStr;
  
  raise info 'finalize_weka_pattern start %', params;
  raise info '  delete stat_ml_ptn_final %', params;
  delete from stat_ml_ptn_final 
  where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban;
  
  select 
    percentile_disc(paramPercentIncomeAmt) within group (order by incomeamt desc),
    percentile_disc(paramPercentHitRate) within group (order by hitrate desc),
    percentile_disc(paramPercentIncomeRate) within group (order by incomerate desc),
    percentile_disc(paramPercentMinusDaysRate) within group (order by minus_days_rate asc),
    percentile_disc(paramPercentIncomeAmtSlope) within group (order by linear_incomeamt_slope desc),
    percentile_disc(paramPercentRecoverPlusRate) within group (order by recover_plus_rate desc)
  into paramValueIncomeAmt, paramValueHitRate, paramValueIncomeRate, paramValueMinusDaysRate, paramValueIncomeAmtSlope, paramValueRecoverPlusRate
  from stmp_stat_ml_ptn_final
  where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban
  ;
  -- and incomerate > 90;
  
  raise info '  finalize conditions: incomeamt>=%, hitrate>=% incomerate>=% minus_days_rate<=% linear_incomeamt_slope>=% recover_plus_rate>=%', 
    paramValueIncomeAmt, paramValueHitRate, paramValueIncomeRate, paramValueMinusDaysRate, paramValueIncomeAmtSlope, paramValueRecoverPlusRate;
    
  raise info '  insert stat_ml_ptn_final';
  insert into stat_ml_ptn_final
  select
     *
  from stmp_stat_ml_ptn_final final
  where final.description = paramDescription and final.bettype = paramBettype and final.kumiban = paramKumiban
    and incomeamt >= paramValueIncomeAmt
    and hitrate >= paramValueHitRate
    and incomerate >= paramValueIncomeRate
    and minus_days_rate <= paramValueMinusDaysRate
    and linear_incomeamt_slope >= paramValueIncomeAmtSlope
    and recover_plus_rate >= paramValueRecoverPlusRate
  order by minus_days_rate asc, hitrate desc, incomeamt desc, incomerate desc, linear_incomeamt_slope desc, recover_plus_rate desc
  ;
  
END;
$$ LANGUAGE plpgsql;

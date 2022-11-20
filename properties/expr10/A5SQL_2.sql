-- evaluation 
select
  -- 'none' grp,
  ev.bettype, ev.kumiban, 
  ev.resultno, ev.modelno, ev.patternid, ev.pattern, 
  mc.features_rank1 || '::' || mc.features_rank2 || '::' || mc.features_rank3 features123,
  ev.betcnt, ev.betamt, ev.hitcnt, ev.hitamt, ev.betrate, (ev.hitamt - ev.betamt) incamt, ev.hitrate, ev.incomerate, 
  -- probability range
  range_betcnt best_betcnt, range_betrate best_betrate, range_worstmin worst_min, range_worstmax worst_max, range_bestmin best_min, range_bestmax best_max,
  -- hitodds range
  rangeodds_betcnt bestodds_betcnt, rangeodds_betrate bestodds_betrate, rangeodds_worstmin worstodds_min, rangeodds_worstmax worstodds_max, rangeodds_bestmin bestodds_min, rangeodds_bestmax bestodds_max,
  -- hitodds stat  
  hitodds_median, hitodds_mean, hitodds_stddev, hitodds_min, hitodds_max, 
  ev.balance[0], ev.balance[1],ev.balance[2],ev.bal_slope[0],ev.bal_slope[1],ev.bal_slope[2]
from ml_evaluation ev, ml_model_config mc 
where ev.modelno = mc.modelno
  and ev.bettype = '1T' and ev.kumiban = '1'  and ev.resultno::int between 731 and 59999  --1,2,단계모두
    and ( balance[0] > 98000 and  balance[1] > 98000 and balance[2] > 98000 )
   and (ev.hitodds_mean - ev.hitodds_stddev) > 1
   and ev.range_betrate > 0.5 
   and ev.betcnt > 1000
   --and (bal_slope[0] > -2 and  bal_slope[1] > -2 and bal_slope[2] > -2)
   --and ev.hitodds_max < 10
  -- and (bal_slope[0] > 5 and  bal_slope[1] > 5 and bal_slope[2] > 5)
  -- and ev.betrate >= 0.005
   -- and (balance[0] > 105000 and  balance[1] > 105000 and balance[2] > 105000)
   --and ev.range_betrate > 0.5
  --and ev.patternid = 'probr1-1dig'
  --and probability_median between 0.8 and 1
  --and ev.betcnt > 5000
  -- and ev.hitrate > 0.5
   --and ev.betcnt >= 10000
   -- and ev.incomerate > 1
   --and (ev.range_worstmax < ev.range_bestmin)
    -- and (balance[0] + balance[1] + balance[2]) > 330000
  -- and (bal_slope[0] > 5 and  bal_slope[1] > 5 and bal_slope[2] > 5)
  -- and ev.hitodds_max < 50
  -- and pt_fmeasure >= 0.5
  -- and ev.hitrate between 0.4 and 0.79
  --and ev.hitrate >= 0.8
  -- and (ev.hitodds_mean - ev.hitodds_stddev) >= 0
  -- and ev.hitodds_stddev < 3
    -- and ev.hitodds_max < 50
  --and ev.incomerate >= 1.05 and ev.betcnt >= 1000
  -- and ev.hitrate >= 0.8 and ev.betcnt >= 1000
   --and ev.betrate >= 0.05
  -- and (balance[0] > 100000 and  balance[1] > 100000 and balance[2] > 100000)
  --and (ev.hitamt - ev.betamt) > 0
  --and ev.betrate >= 0.1 
  --and ev.hitrate > 0.8
  -- and (balance[0] > 105000 and  balance[1] > 105000 and balance[2] > 105000)
  -- and patternid not like 'waku%'  
   --and mc.algorithm_rank1 = 'cf_lgbm-1_py'
--  and ev.pattern not like '%-A1%' 
--  and ev.hitrate::float >= 0.8
  -- and (ev.hitamt - ev.betamt) > 0
  -- and (balance[0] > 100000 and  balance[1] > 100000 and balance[2] > 100000)
  -- and mc.algorithm_rank1 LIKE 'cf_bayesnet%'
  -- and mc.features_rank1 like 'nw_%'
  -- and mc.features_rank1 in ('nw_ext_8', 'nw_ext_9')
--  and mc.modelno::int in (99065,99066,99067)
--  and (rc.resultno::int between 543 and 999 )
--  and (ev.hitamt - ev.betamt) > 0 and ev.betcnt > 50
  -- and rc.pattern_id = 'nopattern'
  -- and ev.bettype = '3T'
order by 
--  ev.bettype, 
--  ev.kumiban, 
--  betcnt desc,
  --ev.range_betrate desc,
  ev.modelno, 
  ev.patternid,
  ev.pattern,
  (ev.probability_median-ev.hitrate),
  ev.betcnt desc,
  ev.resultno::int 
;

  -- mc.algorithm_rank1 || '::' || mc.algorithm_rank2 || '::' || mc.algorithm_rank3 algorithm123,
  -- probability_mean prob_mean, probability_median prob_medi,  
  -- ev.hmeanrate, range_hmeanrate range_hmeanrate,
  -- probability_median prob_med, probability_mean prob_mean, probability_stddev prob_stddev, probability_min prob_min, probability_max prob_max, 
  -- inc_mean, inc_stddev, inc_min, inc_max,  pt_precision, pt_recall, pt_fmeasure 


-- evaluation 
select
  -- 'none' grp,
  ev.bettype, ev.kumiban, 
  ev.resultno, ev.modelno, ev.patternid, ev.pattern, 
  ev.betcnt, ev.betamt, ev.hitcnt, ev.hitamt, ev.betrate, (ev.hitamt - ev.betamt) incamt, ev.hitrate, ev.incomerate, 
  -- probability range
  range_betcnt best_betcnt, range_betrate best_betrate, range_worstmin worst_min, range_worstmax worst_max, range_bestmin best_min, range_bestmax best_max,
  -- hitodds range
  rangeodds_betcnt bestodds_betcnt, rangeodds_betrate bestodds_betrate, rangeodds_worstmin worstodds_min, rangeodds_worstmax worstodds_max, rangeodds_bestmin bestodds_min, rangeodds_bestmax bestodds_max,
  -- hitodds stat  
  hitodds_median, hitodds_mean, hitodds_stddev, hitodds_min, hitodds_max, 
  ev.balance[0], ev.balance[1],ev.balance[2],ev.bal_slope[0],ev.bal_slope[1],ev.bal_slope[2]
from ml_evaluation ev 
where 
  ev.bettype = '1T' and ev.kumiban = '1'  and ev.resultno::int between 70001 and 79999  and ev.patternid = 'sim+each' --1,2,단계모두
order by 
--  ev.bettype, 
--  ev.kumiban, 
--  betcnt desc,
  --ev.range_betrate desc,
  ev.rangeodds_betrate desc,
  ev.modelno, 
  ev.patternid,
  ev.pattern,
  (ev.probability_median-ev.hitrate),
  ev.betcnt desc,
  ev.resultno::int 
;

--   --mc.features_rank1 || '::' || mc.features_rank2 || '::' || mc.features_rank3 features123,


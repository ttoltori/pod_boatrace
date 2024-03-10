DROP FUNCTION IF EXISTS stat_export(VARCHAR(8), VARCHAR(3), VARCHAR(120),
int, int, numeric(5,2), numeric(5,2), numeric(5,2), numeric(5,2));
CREATE OR REPLACE FUNCTION stat_export(
paramYmd VARCHAR(8), 
paramBettype VARCHAR(3), 
paramPath VARCHAR(120),
paramMinPatternCnt int, 
paramMaxPatternCnt int, 
paramMinHitRate numeric(5,2), 
paramMaxHitRate numeric(5,2), 
paramMinIncomeRate numeric(5,2),
paramMaxIncomeRate numeric(5,2))
RETURNS VOID AS $$
  BEGIN
  
    raise notice 'stat_export(%s,%s,%s...) start', paramYmd, paramBettype, paramPath;

    EXECUTE format('copy 
      (select * from stat_result 
        where bettype = ''%s''
          and (patterncnt >= %s and patterncnt <= %s)
          and (hitrate >= %s and hitrate <= %s )
          and (incomerate >= %s and incomerate <= %s )
      ) 
      to ''%s'' with csv'
     ,paramBettype 
     ,paramMinPatternCnt, paramMaxPatternCnt
     ,paramMinHitRate, paramMaxHitRate
     ,paramMinIncomeRate, paramMaxIncomeRate
     ,paramPath || paramYmd || '_stat_bettype_' || paramBettype || '.csv' );

    raise notice 'stat_export(%s,%s,%s...) end', paramYmd, paramBettype, paramPath;
  END;
$$ LANGUAGE plpgsql;

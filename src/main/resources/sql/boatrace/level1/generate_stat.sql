DROP FUNCTION IF EXISTS generate_stat(VARCHAR(8), VARCHAR(3), 
int, int, numeric(5,2), numeric(5,2), numeric(5,2), numeric(5,2), VARCHAR(120));
CREATE OR REPLACE FUNCTION generate_stat(
paramYmd VARCHAR(8), 
paramBettype VARCHAR(3), 
paramMinPatternCnt int, 
paramMaxPatternCnt int, 
paramMinHitRate numeric(5,2), 
paramMaxHitRate numeric(5,2), 
paramMinIncomeRate numeric(5,2),
paramMaxIncomeRate numeric(5,2),
paramPath VARCHAR(120))
RETURNS VOID AS $$
  BEGIN
  
    raise notice 'generate_stat(%s,%s...) start', paramYmd, paramBettype;

    perform stat_make_result(paramBettype);

    perform stat_export(paramYmd, paramBettype, paramPath,
      paramMinPatternCnt, 
      paramMaxPatternCnt, 
      paramMinHitRate, 
      paramMaxHitRate, 
      paramMinIncomeRate, 
      paramMaxIncomeRate
      );

    raise notice 'generate_stat(%s,%s...) end', paramYmd, paramBettype;
  END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS calculate_power_avgtime(VARCHAR(8), smallint);

CREATE OR REPLACE FUNCTION calculate_power_avgtime(paramYmd VARCHAR(8), section smallint)
RETURNS VOID AS $$
  BEGIN
    -- 平均タイム毎の枠→能力値
    delete from power_avgtime;
    
    insert into power_avgtime
      select 
        paramYmd,
        avgtime, 
        wakurank, 
        wakurankcnt,
        wakurankrate,
        tmp.cntrank, 
        tmp.raterank, 
        ((tmp.cntrank+tmp.raterank) / 2) as totalrank
      from (
        select avgtime, wakurank, wakurankcnt, wakurankrate,
          ntile(section) over (partition by wakurank order by max(wakurankcnt) desc) as cntrank,
          ntile(section) over (partition by wakurank order by max(wakurankrate) desc) as raterank
        from wakurank_avgtime
        group by paramYmd, avgtime, wakurank, wakurankcnt, wakurankrate
        ) tmp
      order by paramYmd, avgtime, wakurank
    ;
      
  END;
$$ LANGUAGE plpgsql;

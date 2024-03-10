DROP FUNCTION IF EXISTS calculate_power_racer(VARCHAR(8), smallint);

CREATE OR REPLACE FUNCTION calculate_power_racer(paramYmd VARCHAR(8), section smallint)
RETURNS VOID AS $$
  BEGIN
    -- 選手毎の枠→能力値
    delete from power_racer;
    
    insert into power_racer
      select 
        paramYmd,
        jyocd, 
        entry, 
        wakurank, 
        wakurankcnt,
        wakurankrate,
        tmp.cntrank, 
        tmp.raterank, 
        ((tmp.cntrank+tmp.raterank) / 2) as totalrank
      from (
        select jyocd, entry, wakurank, wakurankcnt, wakurankrate,
          ntile(section) over (partition by wakurank order by max(wakurankcnt) desc) as cntrank,
          ntile(section) over (partition by wakurank order by max(wakurankrate) desc) as raterank
        from wakurank_racer
        group by paramYmd, jyocd, entry, wakurank, wakurankcnt, wakurankrate
        ) tmp
      order by paramYmd, jyocd, entry, wakurank, wakurankcnt, wakurankrate
    ;
      
  END;
$$ LANGUAGE plpgsql;

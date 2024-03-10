DROP FUNCTION IF EXISTS calculate_power_jyo_motor(VARCHAR(8), smallint);

CREATE OR REPLACE FUNCTION calculate_power_jyo_motor(paramYmd VARCHAR(8), section smallint)
RETURNS VOID AS $$
  BEGIN
    -- 場毎モータ毎の枠→能力値
    delete from power_jyo_motor;
    
    insert into power_jyo_motor
      select 
        paramYmd,
        jyocd,
        motorno, 
        wakurank, 
        wakurankcnt,
        wakurankrate,
        tmp.cntrank, 
        tmp.raterank, 
        ((tmp.cntrank+tmp.raterank) / 2) as totalrank
      from (
        select jyocd, motorno, wakurank, wakurankcnt, wakurankrate,
          ntile(section) over (partition by wakurank order by max(wakurankcnt) desc) as cntrank,
          ntile(section) over (partition by wakurank order by max(wakurankrate) desc) as raterank
        from wakurank_jyo_motor
        group by paramYmd, jyocd, motorno, wakurank, wakurankcnt, wakurankrate
        ) tmp
      order by paramYmd, jyocd, motorno, wakurank, wakurankcnt, wakurankrate
      ;
  END;
$$ LANGUAGE plpgsql;

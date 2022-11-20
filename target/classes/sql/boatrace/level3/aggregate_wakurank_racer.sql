DROP FUNCTION IF EXISTS aggregate_wakurank_racer(VARCHAR(8));

CREATE OR REPLACE FUNCTION aggregate_wakurank_racer(paramYmd VARCHAR(8))
RETURNS VOID AS $$
  BEGIN
    delete from wakurank_racer;
    
    insert into wakurank_racer
      select 
        paramYmd,
        tmp.jyocd,
        tmp.entry, 
        tmp.wakurank,
        tmp.wakurankcnt,
        cast(cast(tmp.wakurankcnt as float) / 
          cast(
            case when substring(wakurank from 1 for 1) = '1' then tr.waku1cnt
                 when substring(wakurank from 1 for 1) = '2' then tr.waku2cnt
                 when substring(wakurank from 1 for 1) = '3' then tr.waku3cnt
                 when substring(wakurank from 1 for 1) = '4' then tr.waku4cnt
                 when substring(wakurank from 1 for 1) = '5' then tr.waku5cnt
                 when substring(wakurank from 1 for 1) = '6' then tr.waku6cnt
                 end
          as float)
           * 100 as numeric(7,2)) wakurankrate
      from (
        select 
          jyocd,
          entry,
          (waku::text || rank::text) as wakurank,
          count((waku::text || rank::text)) as wakurankcnt
        from rec_racer
        where
          (ymd >= '20090101' and ymd <= paramYmd)
        group by jyocd, entry, wakurank
      ) tmp, 
        total_racer tr
      where tr.jyocd = tmp.jyocd and tr.entry = tmp.entry
      order by paramYmd, tmp.jyocd, tmp.entry
      ;
  END;
$$ LANGUAGE plpgsql;

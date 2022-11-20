DROP FUNCTION IF EXISTS aggregate_total_racer(VARCHAR(8));

CREATE OR REPLACE FUNCTION aggregate_total_racer(paramYmd VARCHAR(8))
RETURNS VOID AS $$
  BEGIN
    delete from total_racer;
    
    insert into total_racer
      select 
        paramYmd,
        jyocd,
        entry,
        racecnt,
        flyingcnt,
        latecnt,
        waku1cnt,
        waku2cnt,
        waku3cnt,
        waku4cnt,
        waku5cnt,
        waku6cnt,
        rank1cnt,
        rank2cnt,
        rank3cnt,
        rank4cnt,
        rank5cnt,
        rank6cnt,
        cast(cast(tmp.rank1cnt as float) / cast(racecnt as float) * 100 as numeric(5,2))  rank1rate,
        cast(cast(tmp.rank2cnt as float) / cast(racecnt as float) * 100 as numeric(5,2))  rank2rate,
        cast(cast(tmp.rank3cnt as float) / cast(racecnt as float) * 100 as numeric(5,2))  rank3rate,
        cast(cast(tmp.rank4cnt as float) / cast(racecnt as float) * 100 as numeric(5,2))  rank4rate,
        cast(cast(tmp.rank5cnt as float) / cast(racecnt as float) * 100 as numeric(5,2))  rank5rate,
        cast(cast(tmp.rank6cnt as float) / cast(racecnt as float) * 100 as numeric(5,2))  rank6rate
      from 
      ( select 
            jyocd,
            entry,
            count(entry) racecnt,
            count(flying) flyingcnt,
            count(late) latecnt,
            sum(case when waku = 1 then 1 else 0 end) waku1cnt,
            sum(case when waku = 2 then 1 else 0 end) waku2cnt,
            sum(case when waku = 3 then 1 else 0 end) waku3cnt,
            sum(case when waku = 4 then 1 else 0 end) waku4cnt,
            sum(case when waku = 5 then 1 else 0 end) waku5cnt,
            sum(case when waku = 6 then 1 else 0 end) waku6cnt,
            sum(case when rank = 1 then 1 else 0 end) rank1cnt,
            sum(case when rank = 2 then 1 else 0 end) rank2cnt,
            sum(case when rank = 3 then 1 else 0 end) rank3cnt,
            sum(case when rank = 4 then 1 else 0 end) rank4cnt,
            sum(case when rank = 5 then 1 else 0 end) rank5cnt,
            sum(case when rank = 6 then 1 else 0 end) rank6cnt
          from rec_racer
          where
            (ymd >= '20090101' and ymd <= paramYmd)
          group by jyocd, entry
          order by jyocd, entry
      ) tmp
    order by entry
  ;

  END;
$$ LANGUAGE plpgsql;

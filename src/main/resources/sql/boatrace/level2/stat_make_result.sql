DROP FUNCTION IF EXISTS stat_make_result(VARCHAR(3));
CREATE OR REPLACE FUNCTION stat_make_result(paramBettype VARCHAR(3)
)
RETURNS VOID AS $$
  BEGIN
  
    raise notice 'stat_make_result(%s) start', paramBettype;
    raise notice '  delete from stat_result where bettype = %s', paramBettype;
  
    delete from stat_result where bettype = paramBettype;
    
    raise notice '  insert into  stat_result...';
    EXECUTE format('
      insert into  stat_result
        select 
          ''ptnracer'',
          ''%s'',
          stattmp.jyocd,
          ptntmp.ptnracer, 
          ptntmp.ptncnt, 
          (ptntmp.ptncnt * 100) bet, 
          stattmp.kumiban,
          stattmp.betcnt,
          stattmp.prize,
          cast( (cast(stattmp.betcnt as float) / cast(ptntmp.ptncnt as float) ) as numeric(5,2) ) hitrate,
          cast( (cast(stattmp.prize as float) / cast((ptntmp.ptncnt * 100) as float) ) as numeric(7,2) ) incomerate
        from
          (
            select
              stat.jyocd,
              stat.ptnracer, 
              ( 
                case ''%s''
                  when ''1T'' then race.tansyono
                  when ''2T'' then race.nirentanno
                  when ''2F'' then race.nirenhukuno
                  when ''3T'' then race.sanrentanno
                  when ''3F'' then race.sanrenhukuno
                 end
              ) kumiban,
              count(stat.ptnracer) betcnt, 
              sum(
                case ''%s''
                  when ''1T'' then race.tansyoprize
                  when ''2T'' then race.nirentanprize
                  when ''2F'' then race.nirenhukuprize
                  when ''3T'' then race.sanrentanprize
                  when ''3F'' then race.sanrenhukuprize
                 end
                ) prize
            from rec_race race, stat_pattern stat
            where race.ymd = stat.ymd
              and race.jyocd = stat.jyocd
              and race.raceno = stat.raceno
            group by stat.jyocd, stat.ptnracer, kumiban
          ) stattmp,
          (
            select 
              ptnracer, count(ptnracer) ptncnt
            from stat_pattern
            group by ptnracer
            order by ptncnt desc
          ) ptntmp
        where stattmp.ptnracer = ptntmp.ptnracer
        order by incomerate desc, hitrate desc, ptncnt desc
      ', paramBettype, paramBettype, paramBettype);

    raise notice 'stat_make_result(%s) end', paramBettype;
    
  END;
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS generate_stat_levelrank(VARCHAR(8), VARCHAR(3), VARCHAR(8),
int, int, numeric(5,2), numeric(5,2), numeric(5,2), numeric(5,2), VARCHAR(120));
CREATE OR REPLACE FUNCTION generate_stat_levelrank(
paramYmd VARCHAR(8), 
paramBettype VARCHAR(3),
paramStatStartYmd  VARCHAR(8),
paramMinPatternCnt int, 
paramMaxPatternCnt int, 
paramMinHitRate numeric(5,2), 
paramMaxHitRate numeric(5,2), 
paramMinAvgIncomeRate numeric(5,2),
paramMaxAvgIncomeRate numeric(5,2),
paramPath VARCHAR(120))
RETURNS VOID AS $$
  BEGIN
    raise notice 'generate_stat_levelrank(%s,%s...) start', paramYmd, paramBettype;
  
    delete from zen_pattern where bettype = paramBettype;
    delete from zen_race where bettype = paramBettype;
    delete from zen_stat where bettype = paramBettype;
    
	insert into zen_pattern
	select 
	  paramBettype, 
	  race.levelrank pattern,
	  count(race.levelrank) patterncnt,
	  (count(race.levelrank) * 100) betamount
	from rec_race race
	where race.ymd >= paramStatStartYmd and race.ymd <= paramYmd
	group by paramBettype, pattern
	order by paramBettype, pattern;
    
	EXECUTE format ('
		insert into zen_race
		select 
		  ''%s'' bettype, 
		  race.levelrank pattern,
	      ( 
	        case ''%s''
	          when ''1T'' then race.tansyono
	          when ''2T'' then race.nirentanno
	          when ''2F'' then race.nirenhukuno
	          when ''3T'' then race.sanrentanno
	          when ''3F'' then race.sanrenhukuno
	         end
	      ) kumiban,
		  count(race.levelrank) hitcnt,
          sum(
            case ''%s''
              when ''1T'' then race.tansyoprize
              when ''2T'' then race.nirentanprize
              when ''2F'' then race.nirenhukuprize
              when ''3T'' then race.sanrentanprize
              when ''3F'' then race.sanrenhukuprize
             end
          ) prize
		from rec_race race
		where race.ymd >= ''%s'' and race.ymd <= ''%s''
        group by bettype, pattern, kumiban
		order by bettype, pattern, kumiban
    ', paramBettype, paramBettype, paramBettype, paramStatStartYmd, paramYmd);

    insert into zen_stat
    select *, cast( (hitrate * incomerate) as numeric(5,2) ) avgincomerate
    from 
    (
	    select
	      r.bettype,
	      r.pattern,
	      r.kumiban,
	      p.patterncnt,
	      p.betamount,
	      r.hitcount,
	      r.prize,
	      cast( (cast(r.hitcount as float) / cast(p.patterncnt as float) ) as numeric(5,2) ) hitrate,
	      cast( (cast(r.prize as float) / cast(p.betamount as float) ) as numeric(5,2) ) incomerate
	    from
	      zen_pattern p, zen_race r
	    where
	      p.bettype = r.bettype
	      and p.pattern = r.pattern
    ) zen_tmp
    order by avgincomerate desc, hitrate desc; 
    
    
--           and (avgincomerate >= %s and avgincomerate <= %s )

    EXECUTE format('copy 
      (select * from zen_stat 
        where bettype = ''%s''
          and (patterncnt >= %s and patterncnt <= %s)
          and (hitrate >= %s and hitrate <= %s )
          and (incomerate >= %s and incomerate <= %s )
		order by avgincomerate desc, hitrate desc
      ) 
      to ''%s'' with csv'
     ,paramBettype 
     ,paramMinPatternCnt, paramMaxPatternCnt
     ,paramMinHitRate, paramMaxHitRate
     ,paramMinAvgIncomeRate, paramMaxAvgIncomeRate
     ,paramPath || paramYmd || '_stat_bettype_' || paramBettype || '.csv' );
     
    raise notice 'generate_stat_levelrank(%s,%s...) end', paramYmd, paramBettype;
  END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS generate_stat_vars(VARCHAR(8), VARCHAR(8), VARCHAR(3), VARCHAR(3), VARCHAR(120), 
int, int, numeric(5,2), numeric(5,2), numeric(5,2), numeric(5,2), VARCHAR(120));
CREATE OR REPLACE FUNCTION generate_stat_vars(
paramFromYmd  VARCHAR(8),
paramToYmd VARCHAR(8), 
paramBettype VARCHAR(3),
paramBettype2 VARCHAR(3),
paramPattern VARCHAR(120),
paramMinPatternCnt int, 
paramMaxPatternCnt int, 
paramMinHitRate numeric(5,2), 
paramMaxHitRate numeric(5,2), 
paramMinIncomeRate numeric(5,2),
paramMaxIncomeRate numeric(5,2),
paramPath VARCHAR(120))
RETURNS VOID AS $$
  BEGIN
    raise notice 'generate_stat_vars2(%s,%s...) start', paramToYmd, paramBettype;
  
    truncate zen_pattern;
    truncate zen_race;
    truncate zen_stat;
    
    EXECUTE format ('
		insert into zen_pattern
		select 
		  ''%s'' || ''_'' || ''%s'' bettype, 
		  %s pattern,
		  count(%s) patterncnt,
		  (count(%s) * 100) betamount
		from rec_race race, rec_race_waku rwaku
		where race.ymd >= ''%s'' and race.ymd <= ''%s''
		  and race.ymd = rwaku.ymd
		  and race.jyocd = rwaku.jyocd
		  and race.raceno = rwaku.raceno
          and race.nirentanno in ( ''12'')
--          and substring(race.nationwiningrank from 1 for 2) = ''12''
-- and ( (alevelcount <= 2 and substring(wakulevellist from 1 for 1) = ''A'' )
--      or (racetype like ''%%準優勝%%'')
--      or (fixedentrance = ''進入固定'')
--  )
		group by bettype, pattern
		order by bettype, pattern
	', paramBettype, paramBettype2, paramPattern, paramPattern, paramPattern,  paramFromYmd, paramToYmd);
	
    
	EXECUTE format ('
		insert into zen_race
		select 
		  ''%s'' || ''_'' || ''%s'' bettype, 
		  %s pattern,
	      ( 
	        case ''%s''
	          when ''1T'' then race.tansyono
	          when ''2T'' then race.nirentanno
	          when ''2F'' then race.nirenhukuno
	          when ''3T'' then race.sanrentanno
	          when ''3F'' then race.sanrenhukuno
	         end
	      ) kumiban,
		  count(%s) hitcnt,
          sum(
            case ''%s''
              when ''1T'' then race.tansyoprize
              when ''2T'' then race.nirentanprize
              when ''2F'' then race.nirenhukuprize
              when ''3T'' then race.sanrentanprize
              when ''3F'' then race.sanrenhukuprize
             end
          ) prize
		from rec_race race, rec_race_waku rwaku
		where race.ymd >= ''%s'' and race.ymd <= ''%s''
		  and race.ymd = rwaku.ymd
		  and race.jyocd = rwaku.jyocd
		  and race.raceno = rwaku.raceno
          and race.nirentanno in ( ''12'')
--          and substring(race.nationwiningrank from 1 for 2) = ''12''
--  and ( (alevelcount <= 2 and substring(wakulevellist from 1 for 1) = ''A'' )
--      or (racetype like ''%%準優勝%%'')
--      or (fixedentrance = ''進入固定'')
--  )
        group by bettype, pattern, kumiban
		order by bettype, pattern, kumiban
    ', paramBettype, paramBettype2, paramPattern, paramBettype, paramPattern, paramBettype, paramFromYmd, paramToYmd);

    insert into zen_stat
    select *, cast( (hitrate * incomerate) as numeric(7,2) ) avgincomerate
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
	      cast( (cast(r.hitcount as float) / cast(p.patterncnt as float) ) as numeric(7,2) ) hitrate,
	      cast( (cast(r.prize as float) / cast(p.betamount as float) ) as numeric(7,2) ) incomerate
	    from
	      zen_pattern p, zen_race r
	    where
	      p.bettype = r.bettype
	      and p.pattern = r.pattern
    ) zen_tmp
    order by avgincomerate desc, hitrate desc; 
    
    
--          and (incomerate >= %s and incomerate <= %s )
--          and (avgincomerate >= %s and avgincomerate <= %s )

    EXECUTE format('copy 
      (select * from zen_stat 
        where bettype = ''%s'' || ''_'' || ''%s''
          and (patterncnt >= %s and patterncnt <= %s)
          and (hitrate >= %s and hitrate <= %s )
          and (avgincomerate >= %s and avgincomerate <= %s )
		order by avgincomerate desc, hitrate desc
      ) 
      to ''%s'' with csv'
     ,paramBettype, paramBettype2
     ,paramMinPatternCnt, paramMaxPatternCnt
     ,paramMinHitRate, paramMaxHitRate
     ,paramMinIncomeRate, paramMaxIncomeRate
     ,paramPath || paramToYmd || '_stat_bettype_' || paramBettype || '_' || paramBettype2 || '.csv' );
     
    raise notice 'generate_stat_vars(%s,%s...) end', paramToYmd, paramBettype;
  END;
$$ LANGUAGE plpgsql;


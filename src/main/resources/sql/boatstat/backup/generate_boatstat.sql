DROP FUNCTION IF EXISTS generate_boatstat(
VARCHAR(8)
, VARCHAR(8)
, VARCHAR(3)
, VARCHAR(20)
, VARCHAR(3)
, VARCHAR(120)
, VARCHAR(20)
, int
, numeric(5,2)
, numeric(5,2)
, VARCHAR(120)
);

CREATE OR REPLACE FUNCTION generate_boatstat(
paramFromYmd  VARCHAR(8),
paramToYmd VARCHAR(8), 
paramBettype VARCHAR(3),
paramBetTypeName VARCHAR(20),
paramKumiban VARCHAR(3),
paramPattern VARCHAR(120),
paramTable VARCHAR(20),
paramMinPatternCnt int, 
paramMinIncomeRate numeric(5,2),
paramMaxIncomeRate numeric(5,2),
paramPath VARCHAR(120))
RETURNS VOID AS $$
  BEGIN
  
    truncate zen_pattern;
    truncate zen_race;
    truncate zen_stat;
    truncate zen_filter;
    
    EXECUTE format ('
		insert into zen_pattern
		select 
		  ''%s'' || ''_'' || ''%s'' bettype, 
		  %s pattern,
		  count(%s) patterncnt,
		  (count(%s) * 100) betamount
		from %s race
		where race.ymd >= ''%s'' and race.ymd <= ''%s''
          and race.%sno = ( ''%s'')
		group by bettype, pattern
		order by bettype, pattern '
    	, paramBettype, paramKumiban
		, paramPattern, paramPattern, paramPattern
		, paramTable
		, paramFromYmd, paramToYmd
		, paramBetTypeName, paramKumiban
	);
	
    
	EXECUTE format ('
		insert into zen_race
		select 
		  ''%s'' || ''_'' || ''%s'' bettype, 
		  %s pattern,
	      %sno kumiban, 
		  count(%s) hitcnt,
          sum(%sprize) prize
		from %s race
		where race.ymd >= ''%s'' and race.ymd <= ''%s''
          and %sno = ( ''%s'')
        group by bettype, pattern, kumiban
		order by bettype, pattern, kumiban '
		, paramBettype, paramKumiban
		, paramPattern
		, paramBetTypeName
		, paramPattern
		, paramBetTypeName
		, paramTable
		, paramFromYmd, paramToYmd
		, paramBetTypeName, paramKumiban);

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
    
    EXECUTE format('insert into zen_filter 
      (select * from zen_stat 
        where bettype = ''%s'' || ''_'' || ''%s''
          and (patterncnt >= %s)
          and (incomerate >= %s and incomerate <= %s)
		order by pattern, patterncnt desc
      ) '
     , paramBettype, paramKumiban
     , paramMinPatternCnt
     , paramMinIncomeRate, paramMaxIncomeRate
     );
     
    EXECUTE format('copy 
      (select * from zen_filter 
      ) 
      to ''%s'' with csv'
     ,paramPath || paramToYmd || '_stat_' || paramBettype || '_' || paramKumiban || '_' ||  
       paramMinPatternCnt || '_' || paramMinIncomeRate || '_' || paramMaxIncomeRate || '.csv' );
     
  END;
$$ LANGUAGE plpgsql;

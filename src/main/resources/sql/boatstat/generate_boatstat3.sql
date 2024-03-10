DROP FUNCTION IF EXISTS generate_boatstat3(VARCHAR(8), VARCHAR(8), VARCHAR(3), VARCHAR(20), VARCHAR(120),  VARCHAR(120),  
int, numeric(5,2), numeric(5,2), numeric(5,2), numeric(5,2), VARCHAR(120));
CREATE OR REPLACE FUNCTION generate_boatstat3(
paramFromYmd  VARCHAR(8),
paramToYmd VARCHAR(8), 
paramBettype VARCHAR(3),
paramBetTypeName VARCHAR(20),
paramPattern1 VARCHAR(120),
paramPattern2 VARCHAR(120),
paramMinPatternCnt int, 
paramMinHitRate numeric(5,2), 
paramMaxHitRate numeric(5,2), 
paramMinIncomeRate numeric(5,2),
paramMaxIncomeRate numeric(5,2),
paramPath VARCHAR(120))
RETURNS VOID AS $$
  DECLARE
    fname varchar(200);
    fnamepath varchar(200);
  BEGIN
  
    fname := paramFromYmd || '_' || paramToYmd || '_' || paramBettype || '_' || paramPattern1 || '_' || paramPattern2 || '_' ||  
       paramMinPatternCnt || '_' || paramMinHitRate || '_' || paramMaxHitRate || '_' || paramMinIncomeRate || '_' || paramMaxIncomeRate;
       
    fnamepath := paramPath || fname || '.csv' ;
    
    truncate zen_pattern;
    truncate zen_race;
    truncate zen_stat;
    truncate zen_filter;
    delete from zen_overall where filename = fname;
    
    EXECUTE format ('
		insert into zen_pattern
		select 
		  ''%s'' bettype, 
		  %s pattern1,
		  %s pattern2,
		  count(*) patterncnt,
		  (count(*) * 100) betamount
		from rec_race race, wrwaku_fame_rank3 rank3
		where race.ymd >= ''%s'' and race.ymd <= ''%s''
			and race.ymd = rank3.ymd and race.jyocd = rank3.jyocd and race.raceno = rank3.raceno
		group by bettype, pattern1, pattern2
		order by bettype, pattern1, pattern2
	', paramBettype, paramPattern1, paramPattern2, paramFromYmd, paramToYmd);
	
    
	EXECUTE format ('
		insert into zen_race
		select 
		  ''%s'' bettype, 
		  %s pattern1,
		  %s pattern2,
	      %sno kumiban, 
		  count(*) hitcnt,
          sum(%sprize) prize
		from rec_race race, wrwaku_fame_rank3 rank3
		where race.ymd >= ''%s'' and race.ymd <= ''%s''
			and race.ymd = rank3.ymd and race.jyocd = rank3.jyocd and race.raceno = rank3.raceno
        group by bettype, pattern1, pattern2, kumiban
		order by bettype, pattern1, pattern2, kumiban
    ', paramBettype, paramPattern1, paramPattern2, paramBetTypeName, paramBetTypeName, paramFromYmd, paramToYmd);

    insert into zen_stat
    select *, cast( (hitrate * incomerate) as numeric(7,2) ) avgincomerate
    from 
    (
	    select
	      r.bettype,
	      r.pattern1,
	      r.pattern2,
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
	      and p.pattern1 = r.pattern1
	      and p.pattern2 = r.pattern2
    ) zen_tmp
    order by bettype, pattern1, pattern2;
    
    EXECUTE format('insert into zen_filter 
      (select * from zen_stat 
        where bettype = ''%s''
          and (patterncnt >= %s)
          and (hitrate >= %s) and (hitrate <= %s)
          and (incomerate >= %s) and (incomerate <= %s)
		order by bettype, pattern1, pattern2, patterncnt desc
      ) '
     ,paramBettype
     ,paramMinPatternCnt
     ,paramMinHitRate,paramMaxHitRate
     ,paramMinIncomeRate,paramMaxIncomeRate
	);
    
    insert into zen_overall
    select 
      fname,
      sum(patterncnt),
      max(patterncnt),
      sum(patterncnt) / (365 * 7),
      sum(hitcount),
      max(hitcount),
      sum(hitcount) / (365 * 7),
      sum(betamount),
      avg(betamount),
      sum(betamount) / (365 * 7),
      sum(prize),
      avg(prize),
      sum(prize) / (365 * 7),
      cast( max(hitrate) as numeric(10,2)),
      cast( avg(hitrate) as numeric(10,2)),
      cast( max(incomerate) as numeric(10,2)),
      cast( avg(incomerate) as numeric(10,2)),
      sum(prize - betamount),
      avg(prize - betamount),
      sum(prize - betamount) / (365 * 7)
    from zen_filter;
    
    EXECUTE format('copy 
      (select * from zen_filter 
      ) 
      to ''%s'' with csv'
     , replace(fnamepath, '||', '+'));
     
    EXECUTE format('copy 
      (select * from zen_overall 
      ) 
      to ''%s'' with csv'
     , paramPath || 'overall.csv');
       
  END;
$$ LANGUAGE plpgsql;

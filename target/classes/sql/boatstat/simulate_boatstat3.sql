DROP FUNCTION IF EXISTS simulate_boatstat3(VARCHAR(8), VARCHAR(8), VARCHAR(3), VARCHAR(120), VARCHAR(120), VARCHAR(120));
CREATE OR REPLACE FUNCTION simulate_boatstat3(
paramFromYmd  VARCHAR(8),
paramToYmd VARCHAR(8), 
paramBettype VARCHAR(3),
paramPattern1 VARCHAR(120),
paramPattern2 VARCHAR(120),
paramDescription VARCHAR(20)
)
RETURNS VOID AS $$
  DECLARE
    cu refcursor;
    rec record;
  BEGIN
    
    delete from stat_ml_result where description = paramDescription;
    
	open cu for execute format('
		select
		  ymd, jyocd, raceno, kumiban
		from 
	    ( select 
	        race.ymd, race.jyocd, race.raceno, %s pattern1, %s pattern2
	      from rec_race race, wrwaku_fame_rank3 rank3
		  where race.ymd >= ''%s'' and race.ymd <= ''%s''
		    and race.ymd = rank3.ymd and race.jyocd = rank3.jyocd and race.raceno = rank3.raceno
	    ) tmp, zen_filter zen
	    where zen.bettype = ''%s''
	      and zen.pattern1 = tmp.pattern1 and zen.pattern2 = tmp.pattern2
	    order by zen.incomerate
	', paramPattern1, paramPattern2, paramFromYmd, paramToYmd, paramBettype);
	
	loop
	  fetch cu into rec;
	  if not found then 
	    exit;
	  end if;
	  
	  insert into stat_ml_result values 
	    (rec.ymd, rec.jyocd, rec.raceno, paramDescription, paramBettype, rec.kumiban);
	end loop;
	close cu;
	return;
  END;
$$ LANGUAGE plpgsql;

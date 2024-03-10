DROP FUNCTION IF EXISTS generate_dataset_49(
VARCHAR(8)
, VARCHAR(8)
, VARCHAR(3)
, VARCHAR(20)
, VARCHAR(10)
, VARCHAR(120)
);

CREATE OR REPLACE FUNCTION generate_dataset_49(
paramFromYmd  VARCHAR(8),
paramToYmd VARCHAR(8), 
paramBetType VARCHAR(3),
paramBetTypeName VARCHAR(20),
paramWrWaku VARCHAR(10),
paramDatasetPath VARCHAR(120))
RETURNS VOID AS $$
  BEGIN
  
    Execute format('
	  copy (
		  select 
		    -- rec_race_waku2
		    nationwiningrate1, nationwiningrate2, nationwiningrate3, nationwiningrate4, nationwiningrate5, nationwiningrate6,
		    localwiningrate1, localwiningrate2, localwiningrate3, localwiningrate4, localwiningrate5, localwiningrate6, 
		    -- stat_waku_win
		    racerwiningrate1,  racerwiningrate2, racerwiningrate3, racerwiningrate4, racerwiningrate5, racerwiningrate6,
		    -- rec_race_gap
		    nationwining12, nationwining13, nationwining14, nationwining23, nationwining24, nationwining34,
		    localwining12, localwining13, localwining14, localwining23, localwining24, localwining34,
		    racerwining12, racerwining13, racerwining14, racerwining23, racerwining24, racerwining34,
		    ( case %sno
		      when ''12'' then ''12'' 
		      when ''13'' then ''13'' 
		      when ''14'' then ''14'' 
		      when ''15'' then ''15'' 
		      when ''16'' then ''16'' 
		      else ''else'' end )
		  from 
		  (
			  select 
			    race.ymd ymd1, race.jyocd jyocd1, race.raceno raceno1, race.*, waku2.*, gap.*, wakuwin.*
			  from rec_race race, rec_race_waku2 waku2, rec_race_gap gap, stat_waku_win wakuwin, wrwaku_fame_rank3 rank3 
			  where race.ymd >= ''%s'' and race.ymd <= ''%s''
			    and %sno not in (''特払'',''不成立'',''発売無'')
			    and race.ymd = waku2.ymd and race.jyocd = waku2.jyocd and race.raceno = waku2.raceno
			    and race.ymd = gap.ymd and race.jyocd = gap.jyocd and race.raceno = gap.raceno
			    and race.ymd = wakuwin.ymd and race.jyocd = wakuwin.jyocd and race.raceno = wakuwin.raceno
			    and race.ymd = rank3.ymd and race.jyocd = rank3.jyocd and race.raceno = rank3.raceno
			    and wrwaku = ''%s''
		  ) tmp
	  ) to ''%s'' with encoding ''UTF8'' csv
	'
	, paramBetTypeName, paramFromYmd, paramToYmd, paramBetTypeName, paramWrWaku, 
	  paramDatasetPath || '49_' || paramBetType || '_12_16_rank3ptn-' || paramWrWaku || '.arff'
	);
	
  END;
$$ LANGUAGE plpgsql;

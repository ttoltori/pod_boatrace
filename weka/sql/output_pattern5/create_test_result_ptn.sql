DROP FUNCTION IF EXISTS create_test_result_ptn (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), VARCHAR(400), VARCHAR(8), VARCHAR(8));
CREATE OR REPLACE FUNCTION create_test_result_ptn (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400),
paramPatternValueReal VARCHAR(400),
paramFromYmd VARCHAR(8),
paramToYmd VARCHAR(8)
) RETURNS VOID AS $$
DECLARE
  params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' || paramPatternName || '_' || paramPatternValue
       || '_' || paramPatternValueReal || '_' || paramFromYmd || '_' || paramToYmd;
    
    raise info 'delete stat_ml_result_ptn';
	delete from stat_ml_result_ptn 
	where description = paramDescription and bettype = paramBettype and bet_kumiban = paramKumiban and pattern_name = paramPatternName;
	  
    raise info 'insert stat_ml_result_ptn. %', params;
    EXECUTE format ('
		insert into stat_ml_result_ptn
		select * from 
		(
			select
			   stat.*,
			   ''%s'' pattern_name,
			   %s pattern_value
			from stat_ml_result stat, rec_race race, rec_race_metric metric, rec_race_waku2 waku2, rec_racer_arr racerarr
			where race.ymd = stat.ymd and race.jyocd = stat.jyocd and race.raceno = stat.raceno
			  and race.ymd = metric.ymd and race.jyocd = metric.jyocd and race.raceno = metric.raceno
			  and race.ymd = waku2.ymd and race.jyocd = waku2.jyocd and race.raceno = waku2.raceno
			  and race.ymd = racerarr.ymd and race.jyocd = racerarr.jyocd and race.raceno = racerarr.raceno
			  and stat.description = ''%s'' and stat.bettype = ''%s'' and stat.bet_kumiban = ''%s''
			  and stat.ymd >= ''%s'' and stat.ymd <= ''%s''
		) tmp
		where pattern_value = ''%s''
	', paramPatternName, paramPatternValue, paramDescription, paramBettype, paramKumiban, paramFromYmd, paramToYmd 
	 , paramPatternValueReal);
END
$$ LANGUAGE plpgsql;

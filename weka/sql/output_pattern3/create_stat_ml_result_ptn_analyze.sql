DROP FUNCTION IF EXISTS create_stat_ml_result_ptn_analyze (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400),  VARCHAR(8), VARCHAR(8));
CREATE OR REPLACE FUNCTION create_stat_ml_result_ptn_analyze (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400),
paramFromYmd VARCHAR(8),
paramToYmd VARCHAR(8)
) RETURNS VOID AS $$
DECLARE
  params text;
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' || paramPatternName || '_' || paramPatternValue
       || '_' || paramFromYmd || '_' || paramToYmd;
    
    raise info 'delete stat_ml_result_ptn_analyze';
	delete from stat_ml_result_ptn_analyze 
	where description = paramDescription and bettype = paramBettype and bet_kumiban = paramKumiban and pattern_name = paramPatternName;

	  
    raise info 'insert stat_ml_result_ptn_analyze. %', params;
    EXECUTE format ('
		insert into stat_ml_result_ptn_analyze
		select
		   stat.*,
		   ''%s'' pattern_name,
		   %s pattern_value
		from stat_ml_result stat, rec_race race, rec_race_metric metric
		where race.ymd = stat.ymd and race.jyocd = stat.jyocd and race.raceno = stat.raceno
		  and race.ymd = metric.ymd and race.jyocd = metric.jyocd and race.raceno = metric.raceno
		  and stat.description = ''%s'' and stat.bettype = ''%s'' and stat.bet_kumiban = ''%s''
		  and stat.ymd >= ''%s'' and stat.ymd <= ''%s''
	', paramPatternName, paramPatternValue, paramDescription, paramBettype, paramKumiban, paramFromYmd, paramToYmd);
END
$$ LANGUAGE plpgsql;

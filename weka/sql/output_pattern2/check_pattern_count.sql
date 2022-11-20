DROP FUNCTION IF EXISTS check_pattern_count(VARCHAR(2), VARCHAR(4), VARCHAR(400));
CREATE OR REPLACE FUNCTION check_pattern_count(
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400)
) RETURNS int AS
$$
BEGIN
	truncate stat_ml_result_ptn;
	
    EXECUTE format ('
		insert into stat_ml_result_ptn
		select
		   stat.*,
		   ''%s'' pattern_name,
		   %s pattern_value
		from stat_ml_result stat, rec_race race
		where race.ymd = stat.ymd and race.jyocd = stat.jyocd and race.raceno = stat.raceno
		  and stat.bettype = ''%s'' and stat.bet_kumiban = ''%s''
	', paramPatternName, paramPatternValue, paramBettype, paramKumiban);
    return (
	  select count(distinct (pattern_name || pattern_value)) from stat_ml_result_ptn
    );
END
$$ LANGUAGE plpgsql;
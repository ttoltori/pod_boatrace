DROP FUNCTION IF EXISTS test_ptn_linear(VARCHAR(400), VARCHAR(2), VARCHAR(4), VARCHAR(400), VARCHAR(400), VARCHAR(400),
  VARCHAR(8), VARCHAR(8));
CREATE OR REPLACE FUNCTION test_ptn_linear(
paramDescription  VARCHAR(400),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramPatternValue VARCHAR(400),
paramPatternValueReal VARCHAR(400),
paramFromYmd VARCHAR(8),
paramToYmd VARCHAR(8)
)
RETURNS VOID AS $$
  DECLARE
    finaltotal int; -- 최종 패턴 총수
    filepath text; -- 최종패턴내용 파일로 백업
    ptncnt int; 
  BEGIN
	delete from stat_ml_result_ptn;
	delete from stat_ml_ptn_cnt;
	delete from stat_ml_ptn_linear;
	
	perform create_test_result_ptn(paramDescription, paramBettype, paramKumiban, paramPatternName, paramPatternValue, paramPatternValueReal, paramFromYmd, paramToYmd);

	perform create_stat_ml_ptn_cnt(paramDescription, paramBettype, paramKumiban, paramPatternName);

	perform create_stat_ml_ptn_linear(paramDescription, paramBettype, paramKumiban, paramPatternName, 0);

  END;
$$ LANGUAGE plpgsql;

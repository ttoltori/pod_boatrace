-- kumibanl単位生成
DROP FUNCTION IF EXISTS test(VARCHAR(40), VARCHAR(250), VARCHAR(200), VARCHAR(1), VARCHAR(2)[], VARCHAR(3)); 
CREATE OR REPLACE FUNCTION test(
pFactorName  VARCHAR(40),
pFactorValue VARCHAR(250), 
pCond VARCHAR(200),
pResultType VARCHAR(1),
pBettypeList VARCHAR(2)[],
pKumiban VARCHAR(3)
)
RETURNS VOID AS $$
  DECLARE
    factorName VARCHAR(40); 
  BEGIN
	
  	factorName := pFactorName || '777-20_' || pBettypeList || '_' || pKumiban;  

	raise notice 'delete ptn_ranking %', factorName;
	
	raise notice 'table 0 %', pBettypeList[0];
	
  END;
$$ LANGUAGE plpgsql;

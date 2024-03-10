DROP FUNCTION IF EXISTS aggregate_wakurank(VARCHAR(8));

CREATE OR REPLACE FUNCTION aggregate_wakurank(paramYmd VARCHAR(8))
RETURNS VOID AS $$
  BEGIN
    perform aggregate_wakurank_racer(paramYmd);
    
    perform aggregate_wakurank_avgtime(paramYmd);
    
    perform aggregate_wakurank_jyo_motor(paramYmd);
    
  END;
$$ LANGUAGE plpgsql;

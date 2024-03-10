DROP FUNCTION IF EXISTS aggregate_total(VARCHAR(8));

CREATE OR REPLACE FUNCTION aggregate_total(paramYmd VARCHAR(8))
RETURNS VOID AS $$
  BEGIN
    perform aggregate_total_racer(paramYmd);
    
    perform aggregate_total_avgtime(paramYmd);
    
    perform aggregate_total_jyo_motor(paramYmd);
    
  END;
$$ LANGUAGE plpgsql;

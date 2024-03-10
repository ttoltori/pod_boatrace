DROP FUNCTION IF EXISTS calculate_power_bettype(VARCHAR(8), VARCHAR(3));

CREATE OR REPLACE FUNCTION calculate_power_bettype(paramYmd VARCHAR(8), paramBettype VARCHAR(3))
RETURNS VOID AS $$
  BEGIN
    perform calculate_power_racer_bettype(paramYmd, paramBettype);
    
    perform calculate_power_avgtime_bettype(paramYmd, paramBettype);
    
    perform calculate_power_jyo_motor_bettype(paramYmd, paramBettype);
    
  END;
$$ LANGUAGE plpgsql;

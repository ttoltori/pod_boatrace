DROP FUNCTION IF EXISTS calculate_power(VARCHAR(8), smallint, smallint, smallint);

CREATE OR REPLACE FUNCTION calculate_power(paramYmd VARCHAR(8), sectionRacer smallint, sectionAvgtime smallint, sectionJyoMotor smallint)
RETURNS VOID AS $$
  BEGIN
    perform calculate_power_racer(paramYmd, sectionRacer);
    
    perform calculate_power_avgtime(paramYmd, sectionAvgtime);
    
    perform calculate_power_jyo_motor(paramYmd, sectionJyoMotor);
    
  END;
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS generate_power(VARCHAR(8), smallint, smallint, smallint, VARCHAR(120));

CREATE OR REPLACE FUNCTION generate_power(
  paramYmd VARCHAR(8), 
  paramSectionRacer smallint,
  paramSectionAvgtime smallint,
  paramSectionJyoMotor smallint,
  paramPath VARCHAR(120)
)
RETURNS VOID AS $$
  BEGIN
    perform aggregate_total(paramYmd);
    
    perform aggregate_wakurank(paramYmd);
    
    perform calculate_power(paramYmd, paramSectionRacer, paramSectionAvgtime, paramSectionJyoMotor);
    
    perform calculate_power_bettype(paramYmd, '1TF');
    -- perform calculate_power_bettype(paramYmd, '2TF');
    -- perform calculate_power_bettype(paramYmd, '3TF');
    
    perform export_power(paramYmd, paramPath);
    
  END;
$$ LANGUAGE plpgsql;

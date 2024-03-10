DROP FUNCTION IF EXISTS export_power(VARCHAR(8), VARCHAR(120));
CREATE OR REPLACE FUNCTION export_power(paramYmd VARCHAR(8), paramPath VARCHAR(120))
RETURNS VOID AS $$
  BEGIN
    EXECUTE format('copy (select * from power_racer where ymd = ''%s'') to ''%s'' with csv'
     ,paramYmd ,paramPath || paramYmd || '_power_racer.csv' );
     
    EXECUTE format('copy (select * from power_avgtime where ymd = ''%s'') to ''%s'' with csv'
     ,paramYmd ,paramPath || paramYmd || '_power_avgtime.csv' );
     
    EXECUTE format('copy (select * from power_jyo_motor where ymd = ''%s'') to ''%s'' with csv'
     ,paramYmd ,paramPath || paramYmd || '_power_jyo_motor.csv' );
     
    EXECUTE format('copy (select * from power_racer_bettype where ymd = ''%s'') to ''%s'' with csv'
     ,paramYmd ,paramPath || paramYmd || '_power_racer_bettype.csv' );
     
    EXECUTE format('copy (select * from power_avgtime_bettype where ymd = ''%s'') to ''%s'' with csv'
     ,paramYmd ,paramPath || paramYmd || '_power_avgtime_bettype.csv' );

    EXECUTE format('copy (select * from power_jyo_motor_bettype where ymd = ''%s'') to ''%s'' with csv'
     ,paramYmd ,paramPath || paramYmd || '_power_jyo_motor_bettype.csv' );
  END;
$$ LANGUAGE plpgsql;

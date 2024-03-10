
DROP FUNCTION IF EXISTS get_wind(
numeric(4,2)
);

CREATE OR REPLACE FUNCTION get_wind(
paramWind numeric(4,2))
RETURNS numeric(4,2) AS $$
  BEGIN
      if paramWind >= 8.0 then
        RETURN 8.0;
      else
        RETURN paramWind;
      end if;

  END;
$$ LANGUAGE plpgsql;

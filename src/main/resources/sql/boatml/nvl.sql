CREATE OR REPLACE FUNCTION nvlint(val1 int, val2 int)
RETURNS int
LANGUAGE plpgsql
AS $$
BEGIN
    IF val1 IS NOT NULL AND val1 != 'NaN' THEN
        RETURN val1;
    ELSE
        RETURN val2;
    END IF;
END;
$$;
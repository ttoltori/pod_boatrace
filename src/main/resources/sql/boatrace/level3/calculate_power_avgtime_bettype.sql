DROP FUNCTION IF EXISTS calculate_power_avgtime_bettype(VARCHAR(8), VARCHAR(3));

CREATE OR REPLACE FUNCTION calculate_power_avgtime_bettype(paramYmd VARCHAR(8), paramBettype VARCHAR(3))
RETURNS VOID AS $$
  BEGIN
    delete from power_avgtime_bettype where bettype = paramBettype;
    
    IF paramBettype = '1TF' THEN
      insert into power_avgtime_bettype
        select 
          paramYmd,
          paramBettype,
          avgtime, 
          substring(wakurank from 1 for 1) waku, 
          sum(totalrank) / count(totalrank) as powerrank
        from power_avgtime
        where
          wakurank = any (
            case 
                 when substring(wakurank from 1 for 1) = '1' then array['11']
                 when substring(wakurank from 1 for 1) = '2' then array['21']
                 when substring(wakurank from 1 for 1) = '3' then array['31']
                 when substring(wakurank from 1 for 1) = '4' then array['41']
                 when substring(wakurank from 1 for 1) = '5' then array['51']
                 when substring(wakurank from 1 for 1) = '6' then array['61']
            end
          )
        group by paramYmd, paramBettype, avgtime, substring(wakurank from 1 for 1)
        order by paramYmd, paramBettype, avgtime, substring(wakurank from 1 for 1);
    ELSIF paramBettype = '2TF' THEN
      insert into power_avgtime_bettype
        select 
          paramYmd,
          paramBettype,
          avgtime, 
          substring(wakurank from 1 for 1) waku, 
          sum(totalrank) / count(totalrank) as powerrank
        from power_avgtime
        where
          wakurank = any (
            case 
                 when substring(wakurank from 1 for 1) = '1' then array['11']
                 when substring(wakurank from 1 for 1) = '2' then array['21', '22']
                 when substring(wakurank from 1 for 1) = '3' then array['31', '32']
                 when substring(wakurank from 1 for 1) = '4' then array['41', '42']
                 when substring(wakurank from 1 for 1) = '5' then array['51', '52']
                 when substring(wakurank from 1 for 1) = '6' then array['61', '62']
            end
          )
        group by paramYmd, paramBettype, avgtime, substring(wakurank from 1 for 1)
        order by paramYmd, paramBettype, avgtime, substring(wakurank from 1 for 1);
    ELSIF paramBettype = '3TF' THEN
      insert into power_avgtime_bettype
        select 
          paramYmd,
          paramBettype,
          avgtime, 
          substring(wakurank from 1 for 1) waku, 
          sum(totalrank) / count(totalrank) as powerrank
        from power_avgtime
        where
          wakurank = any (
            case 
                 when substring(wakurank from 1 for 1) = '1' then array['11']
                 when substring(wakurank from 1 for 1) = '2' then array['21', '22']
                 when substring(wakurank from 1 for 1) = '3' then array['31', '32', '33']
                 when substring(wakurank from 1 for 1) = '4' then array['41', '42', '43']
                 when substring(wakurank from 1 for 1) = '5' then array['51', '52', '53']
                 when substring(wakurank from 1 for 1) = '6' then array['61', '62', '63']
            end
          )
        group by paramYmd, paramBettype, avgtime, substring(wakurank from 1 for 1)
        order by paramYmd, paramBettype, avgtime, substring(wakurank from 1 for 1);
    END IF;
  END;
$$ LANGUAGE plpgsql;

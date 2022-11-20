DROP FUNCTION IF EXISTS stat_make_pattern();
CREATE OR REPLACE FUNCTION stat_make_pattern()
RETURNS VOID AS $$
  BEGIN
    raise notice 'stat_make_pattern() start';
    raise notice '  delete from stat_pattern';
    
    delete from stat_pattern;
    
    raise notice '  insert into stat_pattern...';
    
    insert into stat_pattern
      select 
        stat.ymd, stat.jyocd, stat.raceno, stat.bettype,
        '3ptn' as patterntype,
        concat_ws('-', 
          lpad(entry1rank::text, 2, '0'),
          lpad(entry2rank::text, 2, '0'), 
          lpad(entry3rank::text, 2, '0'),
          lpad(entry4rank::text, 2, '0'),
          lpad(entry5rank::text, 2, '0'),
          lpad(entry6rank::text, 2, '0')
        ) ptnracer, 
        concat_ws('-', 
          lpad(motorno1rank::text, 2, '0'),
          lpad(motorno2rank::text, 2, '0'), 
          lpad(motorno3rank::text, 2, '0'), 
          lpad(motorno4rank::text, 2, '0'), 
          lpad(motorno5rank::text, 2, '0'),
          lpad(motorno6rank::text, 2, '0')
        ) ptnmotor, 
        concat_ws('-', 
          lpad(avgtime1rank::text, 2, '0'),
          lpad(avgtime2rank::text, 2, '0'),
          lpad(avgtime3rank::text, 2, '0'),
          lpad(avgtime4rank::text, 2, '0'),
          lpad(avgtime5rank::text, 2, '0'),
          lpad(avgtime6rank::text, 2, '0')
        ) ptntime
      from stat_race stat, rec_race rec
      where stat.ymd = rec.ymd
        and stat.jyocd = rec.jyocd
        and stat.raceno = rec.raceno
-- フィルタ
-------------------------------------------------------
--		and stat.entry1rank not in ('01','02' ) and stat.entry2rank not in ('01', '02')
--		and rec.alevelcount >= 2 and substring(rec.wakulevellist from 1 for 2) = 'A1'      
--		and stat.entry6rank not in ('01', '02', '03')
--        and (rec.femalecount = 0 or rec.femalecount = 6)
--        and stat.entry5rank not in ('01', '02', '03') and stat.entry6rank not in ('01', '02', '03')
--        and rec.grade in ('ip', 'G3')
-- フィルタ        
      order by ymd, jyocd, raceno, bettype, patterntype
    ;
    
    raise notice 'stat_make_pattern() end';

  END;
$$ LANGUAGE plpgsql;

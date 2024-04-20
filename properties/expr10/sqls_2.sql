-- {..} : javaで埋める変数
-- {{...}} : SqlTemplateで埋める部分SQL
-- 指定組番のオッズを予測するして投票するため
-- BEGIN arff_rg_sample
-- arff_1 with odds_monitor BEGIN arff_2 BEGIN arff_9_1  END
-- wk1이 B1인것만 학습해본다. ip,G3,G2대상 -- BEGIN arff_3 BEGIN arff_9_2 END
-- 指定組番のオッズを予測するして投票するため BEGIN arff_rgbet_1
-- 指定組番のオッズを予測するため BEGIN arff_rg_1
-- clf_1 with before odds -- BEGIN clf_2 BEGIN clf_9_1
-- wk1이 B1인것만 학습해본다. ip,G3,G2대상 -- BEGIN clf_3 BEGIN clf_9_2
-- BEGIN rg-1T-1 
-- BEGIN rg-2T-12
-- BEGIN rg-3T-123
-- BEGIN result_partial_select_with_rodds
-- BEGIN simulation_partial_select_with_odds
-- BEGIN s-odds_1T1
-- BEGIN groups-partial
-- BEGIN groups-1
-- BEGIN groups-bork
-- BEGIN groups-bork2
-- BEGIN groups-bork3
-- BEGIN arff_rgsim_1
-- BEGIN grp_3
-- factor = i09... grp_4 BEGIN fact_betc
-- factor = incrate  grp_5 BEGIN incr_betc
-- BEGIN fact_rang
-- BEGIN fact_hitr
-- BEGIN fact_inca
BEGIN arff_rk_1
  select 'nopattern' pattern, race.ymd, 
    {features}, 
    {class_features} 
  from rec_race race, rec_racer racer
  where race.ymd = racer.ymd and race.jyocd = racer.jyocd and race.raceno = racer.raceno 
    and sanrentanno <> '不成立' 
    and race.grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by race.ymd, race.jyocd, race.raceno, racer.waku
END

-- classification data sql
-- {class_condition}は使わない
BEGIN clf_rk_1
  select 'nopattern' pattern, race.ymd,  race.sime, 
  {features} 
  from rec_race race, rec_racer racer 
  where race.ymd = racer.ymd and race.jyocd = racer.jyocd and race.raceno = racer.raceno 
    and sanrentanno <> '不成立' 
    and race.grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by race.ymd, race.jyocd, race.raceno, racer.waku
END
BEGIN arff_rk_2
  select 'nopattern' pattern, race.ymd, 
    {features}, 
    {class_features} 
  from rec_race race, rec_racer racer, rec_racer2 racer2
  where race.ymd = racer.ymd and race.jyocd = racer.jyocd and race.raceno = racer.raceno 
    and racer.ymd = racer2.ymd and racer.jyocd = racer2.jyocd and racer.raceno = racer2.raceno and racer.waku = racer2.waku
    and sanrentanno <> '不成立' 
    and race.grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by race.ymd, race.jyocd, race.raceno, racer.waku
END

-- classification data sql
-- {class_condition}は使わない
BEGIN clf_rk_2
  select 'nopattern' pattern, race.ymd,  race.sime, 
  {features} 
  from rec_race race, rec_racer racer, rec_racer2 racer2
  where race.ymd = racer.ymd and race.jyocd = racer.jyocd and race.raceno = racer.raceno 
    and racer.ymd = racer2.ymd and racer.jyocd = racer2.jyocd and racer.raceno = racer2.raceno and racer.waku = racer2.waku
    and sanrentanno <> '不成立' 
    and race.grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by race.ymd, race.jyocd, race.raceno, racer.waku
END
-- classification data sql
-- {class_condition}사용
BEGIN clf_rk_B1
  select 'nopattern' pattern, race.ymd,  race.sime, 
  {features} 
  from rec_race race, rec_racer racer, rec_racer2 racer2
  where race.ymd = racer.ymd and race.jyocd = racer.jyocd and race.raceno = racer.raceno 
    and racer.ymd = racer2.ymd and racer.jyocd = racer2.jyocd and racer.raceno = racer2.raceno and racer.waku = racer2.waku
    and sanrentanno <> '不成立' 
    and race.grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and (substring(race.wakulevellist from 1 for 2) = 'B1') 
  order by race.ymd, race.jyocd, race.raceno, racer.waku
END
BEGIN arff_rk_3
  select 'nopattern' pattern, race.ymd, 
    {features}, 
    {class_features} 
  from rec_race race, rec_racer racer, rec_racer2 racer2, rec_bodds bo
  where race.ymd = racer.ymd and race.jyocd = racer.jyocd and race.raceno = racer.raceno 
    and race.ymd = bo.ymd and race.jyocd = bo.jyocd and race.raceno = bo.raceno
    and racer.ymd = racer2.ymd and racer.jyocd = racer2.jyocd and racer.raceno = racer2.raceno and racer.waku = racer2.waku
    and sanrentanno <> '不成立' 
    and race.grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by race.ymd, race.jyocd, race.raceno, racer.waku
END
BEGIN clf_rk_3
  select 'nopattern' pattern, race.ymd,  race.sime, 
  {features} 
  from rec_race race, rec_racer racer, rec_racer2 racer2, rec_bodds bo
  where race.ymd = racer.ymd and race.jyocd = racer.jyocd and race.raceno = racer.raceno 
    and race.ymd = bo.ymd and race.jyocd = bo.jyocd and race.raceno = bo.raceno
    and racer.ymd = racer2.ymd and racer.jyocd = racer2.jyocd and racer.raceno = racer2.raceno and racer.waku = racer2.waku
    and sanrentanno <> '不成立' 
    and race.grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by race.ymd, race.jyocd, race.raceno, racer.waku
END

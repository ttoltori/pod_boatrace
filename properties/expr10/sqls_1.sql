-- {..} : javaで埋める変数
-- {{...}} : SqlTemplateで埋める部分SQL
-- 指定組番のオッズを予測するして投票するため
BEGIN arff_rg_sample
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, race.jyocd, race.raceno,
  entry[1], entry[2], -- {features}, 
  -- (oddsr.odds) classes -- {class_features} 
  (case when tansyono = '1' then oddsr.odds else 0 end) classes -- {class_features} 
  from rec_race race, rec_racer_arr arr, odds_result oddsr
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = oddsr.ymd and race.jyocd = oddsr.jyocd and race.raceno = oddsr.raceno 
    and sanrentanno <> '不成立' 
    -- and grade in ({grade_condition}) 
    -- and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and race.ymd >= '20180601' and race.ymd <= '20211224' 
    and (oddsr.bettype = '1T' and oddsr.kumiban = '1') -- ({class_condition}) 
  -- order by pattern, race.ymd, race.sime
  order by pattern, race.ymd, race.jyocd, race.raceno
END

BEGIN arff_1
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr 
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END

-- arff_1 with odds_monitor
--BEGIN arff_2
BEGIN arff_9_1
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, odds_monitor om
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = om.ymd and race.jyocd = om.jyocd and race.raceno = om.raceno 
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END

-- wk1이 B1인것만 학습해본다. ip,G3,G2대상
-- BEGIN arff_3
BEGIN arff_9_2
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, odds_monitor om
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = om.ymd and race.jyocd = om.jyocd and race.raceno = om.raceno 
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
    and substring(wakulevellist from 1 for 2) = 'B1'
  order by pattern, race.ymd, race.sime
END

-- 2차적으로 99100의 ml_classification을 학습시키는 모델을 만들어보자
BEGIN arff_2
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and clf.modelno = '99100'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END

--2차적으로 90100의ml_classification을 학습시키는 모델을 만들어보자
BEGIN arff_2_2
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and clf.modelno = '90100'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END

--2차적으로 90103의 ml_classification을 학습시키는 모델을 만들어보자
BEGIN arff_2_3
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and clf.modelno = '90103'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END

--3차적으,로 79101의 ml_classification에 더해 직전옺즈를 학습시키는 모델을 만들어보자
BEGIN arff_3
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf, rec_bodds rb
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and race.ymd = rb.ymd and race.jyocd = rb.jyocd and race.raceno = rb.raceno 
    and clf.modelno = '79101'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END
#3차적으,로 70103의 ml_classification에 더해 직전옺즈를 학습시키는 모델을 만들어보자
BEGIN arff_3_2
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf, rec_bodds rb
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and race.ymd = rb.ymd and race.jyocd = rb.jyocd and race.raceno = rb.raceno 
    and clf.modelno = '70103'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END
#3차적으,로 70101의 ml_classification에 더해 직전옺즈를 학습시키는 모델을 만들어보자
BEGIN arff_3_3
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf, rec_bodds rb
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and race.ymd = rb.ymd and race.jyocd = rb.jyocd and race.raceno = rb.raceno 
    and clf.modelno = '70101'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END

-- 指定組番のオッズを予測するため
BEGIN arff_rg_1
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, odds_monitor odds
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno 
    and odds.lastindex = 40
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END

-- 指定組番のオッズを予測するして投票するため
BEGIN arff_rgbet_1
  select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision, 
  {features}, 
  {class_features} 
  from rec_race race, rec_racer_arr arr, odds_monitor odds
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno 
    and odds.lastindex = 40
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and ({class_condition}) 
  order by pattern, race.ymd, race.sime
END

-- classification data sql
-- {class_condition}は使わない
BEGIN clf_1
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, rec_racer_arr arr 
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by pattern, race.ymd, race.sime
END

-- clf_1 with before odds
-- BEGIN clf_2
BEGIN clf_9_1
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, rec_racer_arr arr, odds_monitor om
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = om.ymd and race.jyocd = om.jyocd and race.raceno = om.raceno 
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by pattern, race.ymd, race.sime
END

-- wk1이 B1인것만 학습해본다. ip,G3,G2대상
-- BEGIN clf_3
BEGIN clf_9_2
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, rec_racer_arr arr, odds_monitor om
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = om.ymd and race.jyocd = om.jyocd and race.raceno = om.raceno 
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by pattern, race.ymd, race.sime
END

-- 2차적으,로 ml_classification을 학습시키는 모델을 만들어보자
BEGIN clf_2
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and clf.modelno = '99100'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by pattern, race.ymd, race.sime
END
-- 2차적으,로 ml_classification을 학습시키는 모델을 만들어보자
BEGIN clf_2_2
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and clf.modelno = '90100'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by pattern, race.ymd, race.sime
END
-- 2차적으,로 ml_classification을 학습시키는 모델을 만들어보자
BEGIN clf_2_3
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and clf.modelno = '90103'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by pattern, race.ymd, race.sime
END

-- 3차적으,로 79101의 ml_classification에 더해 직전옺즈를 학습시키는 모델을 만들어보자
BEGIN clf_3
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf, rec_bodds rb
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and race.ymd = rb.ymd and race.jyocd = rb.jyocd and race.raceno = rb.raceno 
    and clf.modelno = '79101'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by pattern, race.ymd, race.sime
END

-- 3차적으,로 70103의 ml_classification에 더해 직전옺즈를 학습시키는 모델을 만들어보자
BEGIN clf_3_2
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, rec_racer_arr arr, ml_classification clf, rec_bodds rb
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and race.ymd = rb.ymd and race.jyocd = rb.jyocd and race.raceno = rb.raceno 
    and clf.modelno = '70103'
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by pattern, race.ymd, race.sime
END

-- classification data sql
-- {class_condition}を使う。odds_monitorのJOINのため
BEGIN regression_partial_select
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, rec_racer_arr arr, odds_monitor odds 
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno 
    and odds.lastindex = 40
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
END

BEGIN rg-1T-1
  {{regression_partial_select}} and (odds.bettype = '1T' and odds.kumiban = '1') order by pattern, race.ymd, race.sime
END
BEGIN rg-2T-12
  {{regression_partial_select}} and (odds.bettype = '2T' and odds.kumiban = '12') order by pattern, race.ymd, race.sime
END
BEGIN rg-3T-123
  {{regression_partial_select}} and (odds.bettype = '3T' and odds.kumiban = '123') order by pattern, race.ymd, race.sime
END

-- result partial sql
BEGIN result_partial_select
  select 
    '{pattern_id}' patternid, {pattern_sql} pattern, 
    cls.modelno, race.ymd, race.jyocd, race.raceno, race.sime, 
    tansyono, nirentanno, nirenhukuno, sanrentanno, sanrenhukuno, 
    tansyoprize, nirentanprize, nirenhukuprize, sanrentanprize, sanrenhukuprize,
    tansyopopular, nirentanpopular, nirenhukupopular, sanrentanpopular, sanrenhukupopular,
    prediction1, prediction2, prediction3, probability1, probability2, probability3,
    fixedentrance, timezone, com_predict
  from rec_race race, ml_classification cls, rec_racer_arr arr
  where race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno 
    and race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and modelno = '{used_model_no}'
END

-- result data sql ml_classification分析後確定する
BEGIN r-all
 {{result_partial_select}} 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
 order by pattern, race.ymd, race.sime
END
-- debug用期間制限
BEGIN r-debug
 {{result_partial_select}} 
    and race.ymd >= '20210602' and race.ymd <= '20211130' 
 order by pattern, race.ymd, race.sime
END


-- simulation partial sql
-- pattern matchingに必要なカラムも含める。(실험10.xlsx/pattern/参照)
BEGIN simulation_partial_select
  select 
    '{pattern_id}' patternid, {pattern_sql} pattern, 
    race.ymd, race.jyocd, race.raceno, race.sime, 
    tansyono, nirentanno, nirenhukuno, sanrentanno, sanrenhukuno, 
    tansyoprize, nirentanprize, nirenhukuprize, sanrentanprize, sanrenhukuprize,
    tansyopopular, nirentanpopular, nirenhukupopular, sanrentanpopular, sanrenhukupopular,
    alevelcount, com_confidence, com_predict, grade, turn, racetype, wakulevellist, arr.nationwiningrate::double precision[],
    fixedentrance, timezone
  from rec_race race, rec_racer_arr arr
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and sanrentanno <> '不成立' 
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
END

BEGIN s-all
 {{simulation_partial_select}} 
 order by pattern, race.ymd, race.sime
END

-------------------------------------
BEGIN result_partial_select_with_rodds
  select 
    '{pattern_id}' patternid, {pattern_sql} pattern, 
    cls.modelno, race.ymd, race.jyocd, race.raceno, race.sime,
    tansyono, nirentanno, nirenhukuno, sanrentanno, sanrenhukuno, 
    tansyoprize, nirentanprize, nirenhukuprize, sanrentanprize, sanrenhukuprize,
    tansyopopular, nirentanpopular, nirenhukupopular, sanrentanpopular, sanrenhukupopular,
    prediction1, prediction2, prediction3, probability1, probability2, probability3, odds.rodds
  from rec_race race, ml_classification cls, odds_result odds  
  where race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno 
    and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno 
    and sanrentanno <> '不成立' 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
    and modelno = '{used_model_no}'
END

BEGIN simulation_partial_select_with_odds
  select 
    '{pattern_id}' patternid, {pattern_sql} pattern, 
    race.ymd, race.jyocd, race.raceno, race.sime,
    tansyono, nirentanno, nirenhukuno, sanrentanno, sanrenhukuno, 
    tansyoprize, nirentanprize, nirenhukuprize, sanrentanprize, sanrenhukuprize,
    tansyopopular, nirentanpopular, nirenhukupopular, sanrentanpopular, sanrenhukupopular,
    alevelcount, com_confidence, com_predict, grade, turn, racetype, wakulevellist, arr.nationwiningrate::double precision[],
    odds.beforeodds odds
  from rec_race race, rec_racer_arr arr, odds_monitor odds
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno 
    and sanrentanno <> '不成立' 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
END
-------------------------------------
BEGIN s-odds_1T1
 {{simulation_partial_select_with_odds}} 
 and odds.bettype = '1T' and odds.kumiban = '1'
 order by pattern, race.ymd, race.sime
END


-------------------------------------
BEGIN groups-partial
select
  '{grades}' grades,
  ev.bettype, ev.kumiban,   
  ev.resultno, ev.modelno, ev.patternid, ev.pattern, 
  'x' range_selector, 'x' bonus_pr, 'x' bonus_bor, 'x' bonus_bork,
  betcnt, betrate, (hitamt - betamt) incamt, hitrate, incomerate, 
  pr_bestmin, pr_bestmax, bor_bestmin, bor_bestmax, bork_bestmin, bork_bestmax, 
  pr_betcnt, pr_betrate, pr_hitrate, (pr_hitamt-pr_betamt) pr_income, pr_incomerate, 
  bor_betcnt, bor_betrate, bor_hitrate, (bor_hitamt-bor_betamt) bor_income, bor_incomerate, 
  bork_betcnt, bork_betrate, bork_hitrate, (rork_hitamt-rork_betamt) bork_income, bork_incomerate, 
  (hodds_mean - hodds_median)::numeric(5,2)::double precision hodds_stable,
  hodds_median, hodds_mean, hodds_stddev, hodds_min, hodds_max, 
  balance[0] bal1, balance[1] bal2, balance[2] bal3, bal_slope[0] bal1_slope, bal_slope[1] bal2_slope, bal_slope[2] bal3_slope
from ml_evaluation ev
where ev.bettype = '{bettype}' and ev.kumiban = '{prediction}'  and ev.result_type = '{result_type}'
END

BEGIN groups-1
  {{groups-partial}}
  and (     (balance[0] between {bal123_min} and {bal123_max})
        and (balance[1] between {bal123_min} and {bal123_max})
        and (balance[2] between {bal123_min} and {bal123_max})
      )
  and (     (bal_slope[0] between {slope123_min} and {slope123_max})
        and (bal_slope[1] between {slope123_min} and {slope123_max})
        and (bal_slope[2] between {slope123_min} and {slope123_max})
      )
  and ( betcnt between {betcnt_min} and {betcnt_max} )
  and ( (hitamt - betamt) between {income_min} and {income_max} )
  and ( hitrate between {hitrate_min} and {hitrate_max} )
  and ( modelno::int in ({modelnos}) )
  and ({patternid_condition})
  and ({custom})
  order by ev.modelno, ev.patternid, ev.pattern, ev.resultno
END

BEGIN groups-bork
select
  bork.ranking, ev.*
from (
	select
	  '{grades}' grades,
	  resultno, result_type, bettype, kumiban, modelno, patternid, pattern, 
	  'x' range_selector, 'x' bonus_pr, 'x' bonus_bor, 'x' bonus_bork,
	  betcnt, betrate, (hitamt - betamt) incamt, hitrate, incomerate, 
	  pr_bestmin, pr_bestmax, bor_bestmin, bor_bestmax, bork_bestmin, bork_bestmax, 
	  pr_betcnt, pr_betrate, pr_hitrate, (pr_hitamt-pr_betamt) pr_income, pr_incomerate, 
	  bor_betcnt, bor_betrate, bor_hitrate, (bor_hitamt-bor_betamt) bor_income, bor_incomerate, 
	  bork_betcnt, bork_betrate, bork_hitrate, (rork_hitamt-rork_betamt) bork_income, bork_incomerate, 
	  (hodds_mean - hodds_median)::numeric(5,2)::double precision hodds_stable,
	  hodds_median, hodds_mean, hodds_stddev, hodds_min, hodds_max, 
	  balance[0] bal1, balance[1] bal2, balance[2] bal3, bal_slope[0] bal1_slope, bal_slope[1] bal2_slope, bal_slope[2] bal3_slope
	from ml_evaluation
	) ev,
	(
	 select
	   resultno, result_type, bettype, kumiban, modelno, patternid, pattern,
   	   row_number() over (partition by result_type, bettype, kumiban order by betcnt[{bork_idx}] desc ) as ranking
	 from ml_bork_evaluation
	 where
	   result_type = '{result_type}'
	   and bettype = '{bettype}' 
	   and kumiban = '{prediction}'
	   and incamt[{bork_idx}] > 0
       and ({custom})
	) bork
where ev.result_type = bork.result_type and ev.bettype = bork.bettype and ev.kumiban = bork.kumiban 
  and ev.modelno = bork.modelno and ev.patternid  = bork.patternid and ev.pattern = bork.pattern
  and bork.ranking between {ranking_min} and {ranking_max}
order by ranking
END

BEGIN groups-bork2
select
*
from (
select
  row_number() over (partition by ev.result_type, ev.bettype, ev.kumiban order by bork.betcnt[{bork_idx}] desc ) as ranking,
  ev.*
from (
	select
	  '{grades}' grades,
	  resultno, result_type, bettype, kumiban, modelno, patternid, pattern, 
	  'x' range_selector, 'x' bonus_pr, 'x' bonus_bor, 'x' bonus_bork,
	  betcnt, betrate, (hitamt - betamt) incamt, hitrate, incomerate, 
	  pr_bestmin, pr_bestmax, bor_bestmin, bor_bestmax, bork_bestmin, bork_bestmax, 
	  pr_betcnt, pr_betrate, pr_hitrate, (pr_hitamt-pr_betamt) pr_income, pr_incomerate, 
	  bor_betcnt, bor_betrate, bor_hitrate, (bor_hitamt-bor_betamt) bor_income, bor_incomerate, 
	  bork_betcnt, bork_betrate, bork_hitrate, (rork_hitamt-rork_betamt) bork_income, bork_incomerate, 
	  (hodds_mean - hodds_median)::numeric(5,2)::double precision hodds_stable,
	  hodds_median, hodds_mean, hodds_stddev, hodds_min, hodds_max, 
	  balance[0] bal1, balance[1] bal2, balance[2] bal3, bal_slope[0] bal1_slope, bal_slope[1] bal2_slope, bal_slope[2] bal3_slope
	from ml_evaluation
	where incomerate >= {incomerate_min}
	) ev,
	(
	 select
	   resultno, result_type, bettype, kumiban, modelno, patternid, pattern, betcnt
 	 from ml_bork_evaluation
	 where
	   result_type = '{result_type}'
	   and bettype = '{bettype}' 
	   and kumiban = '{prediction}'
	   and incamt[{bork_idx}] > 0
       and ({custom})
	) bork
where ev.result_type = bork.result_type and ev.bettype = bork.bettype and ev.kumiban = bork.kumiban 
  and ev.modelno = bork.modelno and ev.patternid  = bork.patternid and ev.pattern = bork.pattern
order by ranking
) tblset 
where ranking between {ranking_min} and {ranking_max}

END

BEGIN groups-bork3
select
*
from (
select
  row_number() over (partition by ev.result_type, ev.bettype, ev.kumiban order by bork.incamt[{bork_idx}] desc ) as ranking,
  ev.*
from (
	select
	  '{grades}' grades,
	  resultno, result_type, bettype, kumiban, modelno, patternid, pattern, 
	  'x' range_selector, 'x' bonus_pr, 'x' bonus_bor, 'x' bonus_bork,
	  betcnt, betrate, (hitamt - betamt) incamt, hitrate, incomerate, 
	  pr_bestmin, pr_bestmax, bor_bestmin, bor_bestmax, bork_bestmin, bork_bestmax, 
	  pr_betcnt, pr_betrate, pr_hitrate, (pr_hitamt-pr_betamt) pr_income, pr_incomerate, 
	  bor_betcnt, bor_betrate, bor_hitrate, (bor_hitamt-bor_betamt) bor_income, bor_incomerate, 
	  bork_betcnt, bork_betrate, bork_hitrate, (rork_hitamt-rork_betamt) bork_income, bork_incomerate, 
	  (hodds_mean - hodds_median)::numeric(5,2)::double precision hodds_stable,
	  hodds_median, hodds_mean, hodds_stddev, hodds_min, hodds_max, 
	  balance[0] bal1, balance[1] bal2, balance[2] bal3, bal_slope[0] bal1_slope, bal_slope[1] bal2_slope, bal_slope[2] bal3_slope
	from ml_evaluation
	where  (hitamt - betamt) > 0
	) ev,
	(
	 select
	   resultno, result_type, bettype, kumiban, modelno, patternid, pattern, betcnt, incamt
 	 from ml_bork_evaluation
	 where
	   result_type = '{result_type}'
	   and bettype = '{bettype}' 
	   and kumiban = '{prediction}'
       and ({custom})
	) bork
where ev.result_type = bork.result_type and ev.bettype = bork.bettype and ev.kumiban = bork.kumiban 
  and ev.modelno = bork.modelno and ev.patternid  = bork.patternid and ev.pattern = bork.pattern
  and ev.modelno::int = 99100
order by ranking
) tblset 
where ranking between {ranking_min} and {ranking_max}

END

-------------------------------------
BEGIN arff_rgsim_1
  select 'nopattern' pattern, 
    race.ymd, 
    {features}, 
    {class_features} 
  from ml_result res, rec_racer_arr race, ml_classification mc 
  where race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno
    and race.ymd = mc.ymd and race.jyocd = mc.jyocd and race.raceno = mc.raceno and res.modelno = mc.modelno 
    and race.ymd::int between {fromYmd} and {toYmd}
    and res.resultno::int = 946
  order by pattern, race.ymd
END

------------------------------------- online -------------------------------------
-- classification data sql
BEGIN clf_online
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  race.grade, 
  {features} 
  from ol_race race, ol_racer arr 
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by pattern, race.ymd, race.sime
END

-- simulation sql
BEGIN s-online
  select 
    race.ymd, race.jyocd, race.raceno, race.sime, 
    alevelcount, grade, turn, racetype, wakulevellist, arr.nationwiningrate::double precision[]
  from ol_race race, ol_racer arr
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = '{ymd}' and race.jyocd = '{jyocd}' and race.raceno = {raceno}
END

BEGIN s-report
  select
    race.ymd, race.jyocd, race.raceno::text, race.sime, race.grade, race.status,
    bettype, bet_kumiban, result_kumiban, betamt, hitamt
  from ol_race race left outer join ml_result result on (race.ymd = result.ymd and race.jyocd = result.jyocd and race.raceno = result.raceno)
  where race.ymd = '{ymd}'
    -- and race.status in (0,2,3)
  order by race.sime, result.bettype
END

------------------------------------- simulation -------------------------------------
BEGIN grp_3
select
  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor} factor, incamt, betcnt, bal_pluscnt, 
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from stat_bork3
where bettype = '{bettype}' and kumiban = '{kumiban}'
  and result_type = '{result_type}' 
  and modelno in ({models})
order by {factor} desc, betcnt desc limit {limit}
END

-- factor = i09... grp_4
BEGIN fact_betc
select *
from (
	select
	  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor}::double precision factor, 
	  incamt, betcnt, hitrate::double precision, bal_pluscnt, 
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from stat_bork4
	where bettype = '{bettype}' and kumiban = '{kumiban}'
	  and result_type = '{result_type}' 
	  and modelno in ({models})
	order by {factor} desc, betcnt desc limit {limit2}
) tblset
order by betcnt desc limit {limit}
END

-- factor = incrate  grp_5
BEGIN incr_betc
select *
from (
	select
	  '~' sel, bk.grades, bk.bettype, bk.kumiban, bk.resultno, bk.modelno, bk.patternid, bk.pattern, bk.incamt, bk.betcnt, 
	  bk.incrate::double precision factor, bk.hitrate::double precision, bk.bal_pluscnt, 
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from stat_bork4 bk, ml_evaluation me
	where me.resultno = bk.resultno and me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern
	  and bk.bettype = '{bettype}' and bk.kumiban = '{kumiban}'
	  and bk.result_type = '{result_type}' 
	  and bk.incrate >= {factor}
	  and bk.modelno in ({models})
	order by betcnt desc limit {limit2}
) tblset
order by betcnt desc limit {limit}
END

-- grp_6 
BEGIN fact_rang
select
  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor}::double precision factor, incamt, betcnt, hitrate::double precision, bal_pluscnt, 
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from stat_range
where bettype = '{bettype}' and kumiban = '{kumiban}'
  and result_type = '{result_type}' 
  and modelno in ({models})
  and {factor} is not null
order by betcnt desc limit {limit}
END


BEGIN fact_hitr
select
  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor}::double precision factor, incamt, betcnt, hitrate::double precision, bal_pluscnt, 
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from stat_bork4
where bettype = '{bettype}' and kumiban = '{kumiban}'
  and result_type = '{result_type}' 
  and modelno in ({models})
  and {factor} is not null
order by {factor} desc, hitrate desc limit {limit}
END

BEGIN fact_inca
select
  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor}::double precision factor, incamt, betcnt, hitrate::double precision, bal_pluscnt, 
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from stat_bork4
where bettype = '{bettype}' and kumiban = '{kumiban}'
  and result_type = '{result_type}' 
  and modelno in ({models})
  and {factor} is not null
order by {factor} desc, incamt desc limit {limit}
END

-- fact-incr-betc
BEGIN FIB
select *
from (
	select
	  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor}::double precision factor, 
	  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt, 
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from stat_bork4
	where bettype = '{bettype}' and kumiban = '{kumiban}'
	  and result_type = '{result_type}' 
	  and modelno in ({models})
	  and incrate >= {incr}
	  and {factor} is not null
	order by {factor} desc, betcnt desc limit {limit}
) tblForReorder
order by bettype, kumiban, modelno, patternid, pattern
END

-- fact-incr-hitr
BEGIN FIH
select *
from (
	select
	  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor}::double precision factor, 
	  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt, 
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from stat_bork4
	where bettype = '{bettype}' and kumiban = '{kumiban}'
	  and result_type = '{result_type}' 
	  and modelno in ({models})
	  and incrate >= {incr}
	  and {factor} is not null
	order by {factor} desc, hitrate desc limit {limit}
) tblForReorder
order by bettype, kumiban, modelno, patternid, pattern
END

BEGIN FIC
select *
from (
	select
	  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor}::double precision factor, 
	  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt, 
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from stat_bork4
	where bettype = '{bettype}' and kumiban = '{kumiban}'
	  and result_type = '{result_type}' 
	  and modelno in ({models})
	  and {factor} is not null 
	  and {factor} > 0
	  and incrate >= {incr} 
	  and ( {custom} )
order by {factor} desc, hitrate desc limit {limit}
) tblForReorder
order by bettype, kumiban, modelno, patternid, pattern
END

BEGIN FPH
select *
from (
	select
	  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor}::double precision factor, 
	  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt, 
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from stat_pr
	where bettype = '{bettype}' and kumiban = '{kumiban}'
	  and result_type = '{result_type}' 
	  and modelno in ({models})
	  and {factor} is not null
	  and {factor} > 0
	  and incrate >= {incr}
	  and ( {custom} )
	order by {factor} desc, hitrate desc limit {limit}
) tblForReorder
order by bettype, kumiban, modelno, patternid, pattern
END

-- fact-incr-hitr stat_bork5
-- BEGIN FPH2 2022/9/24 Deprecated FPH3へ統合
-- select *
-- from (
-- 	select
-- 	  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, {factor}::double precision factor, 
-- 	  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt, 
-- 	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
-- 	from stat_bork5
-- 	where bettype = '{bettype}' and kumiban = '{kumiban}'
-- 	  and result_type = '{result_type}' 
-- 	  and modelno in ({models})
-- 	  and incrate >= {incr}
-- 	  and {factor} is not null
-- 	  and ( {custom} )
-- 	order by {factor} desc, hitrate desc limit {limit}
-- ) tblForReorder
-- order by bettype, kumiban, modelno, patternid, pattern
-- END

-- fact-incr-hitr stat_bork5
BEGIN FPH3
select *
from (
	select
	  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, ({factor})::double precision factor, 
	  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt, 
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from stat_bork5
	where bettype = '{bettype}' and kumiban = '{kumiban}'
	  and result_type = '{result_type}' 
	  and modelno in ({models})
	  and incrate between {incrmin} and {incrmax}
	  and ({factor}) is not null
	  and ( {custom} )
	order by ({factor}) desc, hitrate desc limit {limit}
) tblForReorder
END

-- fact-incr-hitr stat_bork5
BEGIN FPB3
select *
from (
	select
	  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, ({factor})::double precision factor, 
	  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt, 
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from stat_bork5
	where bettype = '{bettype}' and kumiban = '{kumiban}'
	  and result_type = '{result_type}' 
	  and modelno in ({models})
	  and incrate between {incrmin} and {incrmax}
	  and ({factor}) is not null
	  and ( {custom} )
	order by {factor} desc, betcnt desc limit {limit}
) tblForReorder
END

------------------------------------- bork로 rork를 예측한다. (pork)-------------------------------------
BEGIN arff_prok_99100
  select 'nopattern' pattern, 
    race.ymd, 
    {features}, 
    {class_features} 
  from rec_race race, ml_result mr, rec_racer_arr rra 
  where race.ymd = mr.ymd and race.jyocd = mr.jyocd and race.raceno = mr.raceno
    and race.ymd = rra.ymd and race.jyocd = rra.jyocd and race.raceno = rra.raceno
    and mr.modelno = '99100'
    and mr.stat_bettype in ('1T','2T','3T','2F','3F')
    and mr.bet_oddsrank is not null
    and race.ymd::int between {fromYmd} and {toYmd}
  order by race.ymd, race.jyocd, race.raceno, mr.stat_bettype, mr.bet_kumiban
END

BEGIN clf_prok_99100
  select 'nopattern' pattern, 
  race.ymd, race.jyocd, race.raceno, race.sime,  
  {features} 
  from rec_race race, ml_result mr, rec_racer_arr rra 
  where race.ymd = mr.ymd and race.jyocd = mr.jyocd and race.raceno = mr.raceno
    and race.ymd = rra.ymd and race.jyocd = rra.jyocd and race.raceno = rra.raceno
    and mr.modelno = '99100'
    and mr.stat_bettype in ('1T','2T','3T','2F','3F')
    and mr.bet_oddsrank is not null
    and grade in ({grade_condition}) 
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' 
  order by race.ymd, race.jyocd, race.raceno, mr.stat_bettype, mr.bet_kumiban
END


------------------------------------- bork를 trimming한다.-------------------------------------
BEGIN FBI
  select
    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
    me.hitrate::double precision, me.bal_pluscnt,
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor,
    mbe.betcnt betcnt_arr, mbe.hitcnt hitcnt_arr, mbe.betamt betamt_arr, mbe.hitamt hitamt_arr, 
    mbe.bor_min bor_min_arr, mbe.bor_max bor_max_arr
  from ml_evaluation me, ml_bork_evaluation mbe
  where me.resultno = mbe.resultno and me.result_type = mbe.result_type and me.bettype = mbe.bettype and me.kumiban = mbe.kumiban and me.modelno = mbe.modelno and me.patternid  = mbe.patternid and me.pattern = mbe.pattern
    and me.resultno::int between 3085 and 3140
    and me.bettype = '{bettype}' and me.kumiban = '{kumiban}'
    and me.result_type = '{result_type}'
    and me.modelno = '{model}'
    and me.incomerate between {incrmin} and {incrmax}
    and me.patternid like '{custom}'
END

-- bork 1-30
BEGIN FBI-2
  select
    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
    me.hitrate::double precision, me.bal_pluscnt,
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor,
    mbe.betcnt betcnt_arr, mbe.hitcnt hitcnt_arr, mbe.betamt betamt_arr, mbe.hitamt hitamt_arr, 
    mbe.bor_min bor_min_arr, mbe.bor_max bor_max_arr
  from ml_evaluation me, ml_bork_evaluation mbe
  where me.resultno = mbe.resultno and me.result_type = mbe.result_type and me.bettype = mbe.bettype and me.kumiban = mbe.kumiban and me.modelno = mbe.modelno and me.patternid  = mbe.patternid and me.pattern = mbe.pattern
    and me.resultno::int between 3141 and 3172
    and me.bettype = '{bettype}' and me.kumiban = '{kumiban}'
    and me.result_type = '{result_type}'
    and me.modelno = '{model}'
    and me.incomerate between {incrmin} and {incrmax}
    and me.patternid like '{custom}'
END


BEGIN FPI
  select
    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
    me.hitrate::double precision, me.bal_pluscnt,
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor,
    me.pr_bestmin, me.pr_bestmax, mre.lpr_bestmin, mre.lpr_bestmax, mre.rpr_bestmin, mre.rpr_bestmax
  from ml_evaluation me, ml_range_evaluation mre
  where me.resultno = mre.resultno and me.result_type = mre.result_type and me.bettype = mre.bettype and me.kumiban = mre.kumiban and me.modelno = mre.modelno and me.patternid  = mre.patternid and me.pattern = mre.pattern
    and me.bettype = '{bettype}' and me.kumiban = '{kumiban}'
    and me.result_type = '{result_type}'
    and me.incomerate between {incrmin} and {incrmax}
    and (me.modelno || '-' || me.patternid) in ({custom})
END

-- patternid별로 흑자통계를 얻은후 파라미터별로 랭킹을 매겨서 그중 n개를 취한 evaluation
BEGIN FPI-2
	select
	    '~' sel, (case when me2.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me2.bettype, me2.kumiban, me2.resultno, me2.modelno, me2.patternid, me2.pattern, 
	    (me2.hitamt-me2.betamt) incamt, me2.betcnt, me2.incomerate::double precision incrate,
	    me2.hitrate::double precision, me2.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me2, 
	(
		select
		  pl.result_type, pl.bettype, pl.kumiban, pl.patternid, pl.modelno,
		  tot.betcnt t_betcnt, pl.betcnt p_betcnt, (pl.betcnt::float / tot.betcnt::float)::numeric(5,2) p_betrate,
		  (tot.hitamt - tot.betamt) t_incamt,
		  (pl.hitamt - pl.betamt) p_incamt,
		  (tot.hitcnt::float / tot.betcnt::float)::numeric(5,2) t_hitrate,
		  (pl.hitcnt::float / pl.betcnt::float)::numeric(5,2) p_hitrate,
		  (tot.hitamt::float / tot.betamt::float)::numeric(5,2) t_incrate,
		  (pl.hitamt::float / pl.betamt::float)::numeric(5,2) p_incrate
		from
		  (
			select
			  result_type, bettype, kumiban, patternid, modelno,
			  sum(betcnt) betcnt, 
			  sum(hitcnt) hitcnt,
			  sum(betamt) betamt,
			  sum(hitamt) hitamt
			from ml_evaluation me
			where result_type = '{result_type}'
			  and bettype = '{bettype}' and kumiban = '{kumiban}'
			  and incomerate between {incrmin} and {incrmax}
			group by result_type, bettype, kumiban, patternid, modelno
		  ) pl,
		  (
			select
			  result_type, bettype, kumiban, patternid, modelno,
			  sum(betcnt) betcnt, 
			  sum(hitcnt) hitcnt,
			  sum(betamt) betamt,
			  sum(hitamt) hitamt
			from ml_evaluation me
			where result_type = '{result_type}'
			  and bettype = '{bettype}' and kumiban = '{kumiban}'
			group by result_type, bettype, kumiban, patternid, modelno
			) tot
		where pl.result_type = tot.result_type and pl.bettype = tot.bettype 
		  and pl.kumiban = tot.kumiban and pl.patternid = tot.patternid 
		  and pl.modelno = tot.modelno
		  and {custom}
		order by {factor} desc limit {limit}
	) me3
	where me2.result_type = me3.result_type and me2.bettype = me3.bettype and me2.kumiban = me3.kumiban
	  and me2.patternid = me3.patternid and me2.modelno = me3.modelno 
	  and me2.incomerate between {incrmin} and {incrmax}
END

#FPI-2를 전kumiban한번에 취득. 
BEGIN FPI-3
	select
	    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
	    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
	    me.hitrate::double precision, me.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me, 
	(
	   select 
	     *
	   from (
		   select
		     row_number() over (partition by result_type, bettype, kumiban order by {factor} desc) as ranking,
		     *
		   from st_patternid sp
		   where result_type = '{result_type}' 
		     and bettype = '{bettype}'
		     and {custom}
	   ) tmp
	   where ranking <= {limit}
	) sp2
	where me.result_type = sp2.result_type and me.bettype = sp2.bettype and me.kumiban = sp2.kumiban
	  and me.patternid = sp2.patternid and me.modelno = sp2.modelno 
	  and me.incomerate between {incrmin} and {incrmax}
END

#FPI-2를 bettype,kumiban별로 취득. 
BEGIN FPI-4
	select
	    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
	    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
	    me.hitrate::double precision, me.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me, 
	(
	   select 
	     *
	   from (
		   select
		     row_number() over (partition by result_type, bettype, kumiban order by {factor} desc) as ranking,
		     *
		   from st_patternid sp
		   where result_type = '{result_type}' 
		     and bettype = '{bettype}'
		     and kumiban = '{kumiban}'
		     and {custom}
	   ) tmp
	   where ranking <= {limit}
	) sp2
	where me.result_type = sp2.result_type and me.bettype = sp2.bettype and me.kumiban = sp2.kumiban
	  and me.patternid = sp2.patternid and me.modelno = sp2.modelno 
	  and me.incomerate between {incrmin} and {incrmax}
END
#		     and modelno in ('99080', '99100')
#bettype별로 전패턴id에 대해 학습기간 수익률이 1이상인 evaluation을 추출해서 simulation한 후 그래프출력
BEGIN FPI-5
	select
	    '~' sel, (case when me.result_type = '1' or me.result_type = '1C' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
	    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
	    me.hitrate::double precision, me.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me
    where resultno::int between 4961 and 5744
     and modelno = '{modelno}'
     and patternid = '{patternid}'
     and bettype = '{bettype}'
     and incomerate between {incrmin} and {incrmax}
END
#FPI-5로 생성한 simul결과로부터 선정된 evaluation들을 한개씩 로드한다.
BEGIN FPI-6
	select
	    '~' sel, (case when me.result_type = '1' or me.result_type = '1C' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
	    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
	    me.hitrate::double precision, me.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me
    where resultno::int between 4961 and 5744
     and modelno = '{modelno}'
     and patternid = '{patternid}'
     and bettype = '{bettype}'
     and kumiban = '{kumiban}'
     and incomerate between {incrmin} and {incrmax}
END
#FPI-2를 bettype,model별로 취득. 
BEGIN FPI-7
	select
	    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
	    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
	    me.hitrate::double precision, me.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me, 
	(
	   select 
	     *
	   from (
		   select
		     row_number() over (partition by result_type, bettype, kumiban order by {factor} desc) as ranking,
		     *
		   from st_patternid sp
		   where result_type = '{result_type}' 
		     and bettype = '{bettype}'
		     and modelno in ({models})
		     and {custom}
	   ) tmp
	   where ranking <= {limit}
	) sp2
	where me.result_type = sp2.result_type and me.bettype = sp2.bettype and me.kumiban = sp2.kumiban
	  and me.patternid = sp2.patternid and me.modelno = sp2.modelno 
	  and me.incomerate between {incrmin} and {incrmax}
END
#FPI-7을 99100모델 고정. ranking삭제
BEGIN FPI-8
	select
	    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
	    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
	    me.hitrate::double precision, me.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me, 
	(
	   select 
	     *
	   from st_patternid sp
	   where result_type = '{result_type}' 
	     and bettype = '{bettype}'
	     and modelno in ({models})
	     and patternid in ({patternids})
	) sp2
	where me.result_type = sp2.result_type and me.bettype = sp2.bettype and me.kumiban = sp2.kumiban
	  and me.patternid = sp2.patternid and me.modelno = sp2.modelno 
	  and me.incomerate between {incrmin} and {incrmax}
END
#FPI-8을 model, pattern, kumiban별로 테스트
BEGIN FPI-8-1
	select
	    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
	    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
	    me.hitrate::double precision, me.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me, 
	(
	   select 
	     *
	   from st_patternid sp
	   where result_type = '{result_type}' 
	     and bettype = '{bettype}'
	     and kumiban = '{kumiban}'
	     and modelno = '{modelno}'
	     and patternid = '{patternid}'
	) sp2
	where me.result_type = sp2.result_type and me.bettype = sp2.bettype and me.kumiban = sp2.kumiban
	  and me.patternid = sp2.patternid and me.modelno = sp2.modelno 
	  and me.incomerate between {incrmin} and {incrmax}
END
# wk~패턴에 대해 모델별로 3가지 term의 result를 작성하였다. 6662_0,1,2 (result 5787~5882)
# 위 결과의 term3가지를 눈으로 비교하면서 패턴별로 bork를 조정해가며 6662_3을 확인한다.
BEGIN FPI-9
	select
	    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
	    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
	    me.hitrate::double precision, me.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me
   where resultno::int between 5787 and 5882
     and result_type = '{result_type}' 
     and bettype = '{bettype}'
     and kumiban = '{kumiban}'
     and modelno = '{modelno}'
     and patternid = '{patternid}'
     and pattern = '{pattern}'
	
END

#BEGIN FPH-4
#select *
#from (
#	select
#	  '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, ({factor})::double precision factor, 
#	  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt, 
#	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
#	from stat_bork5
#	where 
#	  bettype = '{bettype}' 
#	  and result_type = '{result_type}' 
#	  and modelno = '{modelno}'
#	  and incrate between {incrmin} and {incrmax}
#	  and ({factor}) is not null
#	  and evaluations_id = '{result_eval_id}'
#	  and ( {custom} )
#	order by ({factor}) desc, hitrate desc limit {limit}
#) tblForReorder
#END

-- 위의 FPH-4 sql이 오류였으므로 다시작성하고 관련 simul도 재생성한다.
-- 임의의 모델에 대해 term구분방식을 바꿔봐면서 stat_bork5를 simul해본다.
BEGIN FPH-4
select 
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from  
(
  select 
    row_number() over (partition by kumiban order by {factor} desc, hitrate desc) as ranking,
    *
  from stat_bork5
  where
    bettype = '{bettype}' 
    and result_type = '{result_type}' 
    and modelno = '{modelno}'
    and incrate between {incrmin} and {incrmax}
    and ({factor}) is not null
    and ({factor}) > 0
    and evaluations_id = '{result_eval_id}'
    and ( {custom} )
) tmp
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- FPH4를 구미방별로 테스트해본다.
BEGIN FPH-4_test
select 
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from  
(
  select 
    row_number() over (partition by kumiban order by {factor} desc, hitrate desc) as ranking,
    *
  from stat_bork5
  where
    bettype = '{bettype}' 
    and kumiban = '{kumiban}' 
    and result_type = '{result_type}' 
    and modelno = '{modelno}'
    and incrate between {incrmin} and {incrmax}
    and ({factor}) is not null
    and ({factor}) > 0
    and evaluations_id = '{result_eval_id}'
    and ( {custom} )
) tmp
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- ml_evaluation + stat_bork5 99100
BEGIN FSB-1
select
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
  select
    row_number() over (partition by sb.kumiban order by {factor} desc, sb.hitrate desc) as ranking,
    sb.*
  from ml_evaluation me, stat_bork5 sb
  where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
    and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
    and me.evaluations_id = '666_0'
    and me.incomerate between {incrmin} and {incrmax}
    and sb.bettype = '{bettype}' 
    <optional> and sb.kumiban = '{kumiban}' </optional>
    and sb.result_type = '{result_type}' 
    and sb.modelno = '{modelno}'
    and ({factor}) is not null
    and ({factor}) > 0
    and sb.evaluations_id = '{result_eval_id}'
    and ( {custom} )
  ) tblReorder
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- stat_bork5 99100, 79100
BEGIN FSB-2
select 
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from  
(
  select 
    row_number() over (partition by kumiban order by {factor} desc, hitrate desc) as ranking,
    *
  from stat_bork5 sb
  where
    bettype = '{bettype}' 
    <optional> and kumiban = '{kumiban}' </optional>
    and result_type = '{result_type}' 
    and modelno = '{modelno}'
    and incrate between {incrmin} and {incrmax}
    and ({factor}) is not null
    and ({factor}) > 0
    and evaluations_id = '{result_eval_id}'
    and ( {custom} )
) tmp
where ranking <= {limit}
order by bettype, kumiban, ranking
END


-- ml_evaluation + stat_bork5 99100 + stat_bork5를 수익률 1이상으로 설정
BEGIN FSB-3
select
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
  select
    row_number() over (partition by sb.kumiban order by {factor} desc, sb.hitrate desc) as ranking,
    sb.*
  from ml_evaluation me, stat_bork5 sb
  where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
    and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
    and me.evaluations_id = '666_0'
    and me.incomerate between {incrmin} and {incrmax}
    and sb.bettype = '{bettype}' 
    <optional> and sb.kumiban = '{kumiban}' </optional>
    and sb.result_type = '{result_type}' 
    and sb.modelno = '{modelno}'
    and ({factor}) is not null
    and sb.evaluations_id = '{result_eval_id}'
    and ( {custom} )
    and sb.incrate > 1
  ) tblReorder
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- ml_evaluation + stat_bork5 99100 + ml_evaluation을 수익률 1이상으로 설정
BEGIN FSB-4
select
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
  select
    row_number() over (partition by sb.kumiban order by {factor} desc, sb.hitrate desc) as ranking,
    sb.*
  from ml_evaluation me, stat_bork5 sb
  where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
    and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
    and me.evaluations_id = '666_0'
    and me.incomerate > 1
    and sb.bettype = '{bettype}' 
    <optional> and sb.kumiban = '{kumiban}' </optional>
    and sb.result_type = '{result_type}' 
    and sb.modelno = '{modelno}'
    and ({factor}) is not null
    and sb.evaluations_id = '{result_eval_id}'
    and ( {custom} )
    and sb.incrate between {incrmin} and {incrmax}
  ) tblReorder
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- FSB-1에 대해 factor > 0을 뺴고, stat_bork5 수익율 1이상으로 제한
BEGIN FSB-5
select
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
  select
    row_number() over (partition by sb.kumiban order by {factor} desc, sb.hitrate desc) as ranking,
    sb.*
  from ml_evaluation me, stat_bork5 sb
  where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
    and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
    and me.evaluations_id = '666_0'
    and me.incomerate between {incrmin} and {incrmax}
    and sb.bettype = '{bettype}' 
    <optional> and sb.kumiban = '{kumiban}' </optional>
    and sb.result_type = '{result_type}' 
    and sb.modelno = '{modelno}'
    and ({factor}) is not null
    and sb.evaluations_id = '{result_eval_id}'
    and ( {custom} )
    and sb.incrate > 1
  ) tblReorder
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- FSB-2에 대해 factor > 0을 뺴고, stat_bork5 수익율 1이상으로 제한
BEGIN FSB-6
select 
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from  
(
  select 
    row_number() over (partition by kumiban order by {factor} desc, hitrate desc) as ranking,
    *
  from stat_bork5 sb
  where
    bettype = '{bettype}' 
    <optional> and kumiban = '{kumiban}' </optional>
    and result_type = '{result_type}' 
    and modelno = '{modelno}'
    and incrate between {incrmin} and {incrmax}
    and ({factor}) is not null
    and evaluations_id = '{result_eval_id}'
    and ( {custom} )
    and sb.incrate > 1
) tmp
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- FSB-1에 대해 stat_bork5 수익율 1이상으로 제한
BEGIN FSB-7
select
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
  select
    row_number() over (partition by sb.kumiban order by {factor} desc, sb.hitrate desc) as ranking,
    sb.*
  from ml_evaluation me, stat_bork5 sb
  where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
    and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
    and me.evaluations_id = '666_0'
    and me.incomerate between {incrmin} and {incrmax}
    and sb.bettype = '{bettype}' 
    <optional> and sb.kumiban = '{kumiban}' </optional>
    and sb.result_type = '{result_type}' 
    and sb.modelno = '{modelno}'
    and ({factor}) is not null
    and ({factor}) > 0
    and sb.evaluations_id = '{result_eval_id}'
    and ( {custom} )
    and sb.incrate > 1
  ) tblReorder
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- FSB-2에 대해 stat_bork5 수익율 1이상으로 제한
BEGIN FSB-8
select 
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from  
(
  select 
    row_number() over (partition by kumiban order by {factor} desc, hitrate desc) as ranking,
    *
  from stat_bork5 sb
  where
    bettype = '{bettype}' 
    <optional> and kumiban = '{kumiban}' </optional>
    and result_type = '{result_type}' 
    and modelno = '{modelno}'
    and incrate between {incrmin} and {incrmax}
    and ({factor}) is not null
    and ({factor}) > 0
    and evaluations_id = '{result_eval_id}'
    and ( {custom} )
    and sb.incrate > 1
) tmp
where ranking <= {limit}
order by bettype, kumiban, ranking
END



BEGIN GPT-1
select 
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor}) factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from  
(
  select 
    row_number() over (partition by kumiban order by {factor} desc, hitrate desc) as ranking,
    *
  from stat_gpt sb
  where
    bettype = '{bettype}' 
    <optional> and kumiban = '{kumiban}' </optional>
    and result_type = '{result_type}' 
    and modelno = '{modelno}'
    and incrate between {incrmin} and {incrmax}
    and ({factor}) is not null
    and evaluations_id = '{result_eval_id}'
    and ( {custom} )
) tmp
where ranking <= {limit}
order by bettype, kumiban, ranking
END



-- ml_evaluation incrate range + stat_bork5 99100
BEGIN GPT-2
select
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
  select
    row_number() over (partition by sb.kumiban order by {factor} desc, sb.hitrate desc) as ranking,
    sb.*
  from ml_evaluation me, stat_bork5 sb
  where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
    and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
    and me.evaluations_id = '666_0'
    and me.incomerate between {incrmin} and {incrmax}
    and sb.bettype = '{bettype}' 
    <optional> and sb.kumiban = '{kumiban}' </optional>
    and sb.result_type = '{result_type}' 
    and sb.modelno = '{modelno}'
    and ({factor}) is not null
    and sb.evaluations_id = '{result_eval_id}'
    and ( {custom} )
  ) tblReorder
where ranking <= {limit}
order by bettype, kumiban, ranking
END


-- ml_evaluation incrate range + stat_bork5 incrate > 1 99100
BEGIN GPT-3
select
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
  select
    row_number() over (partition by sb.kumiban order by {factor} desc, sb.hitrate desc) as ranking,
    sb.*
  from ml_evaluation me, stat_bork5 sb
  where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
    and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
    and me.evaluations_id = '666_0'
    and me.incomerate between {incrmin} and {incrmax}
    and sb.bettype = '{bettype}' 
    <optional> and sb.kumiban = '{kumiban}' </optional>
    and sb.result_type = '{result_type}' 
    and sb.modelno = '{modelno}'
    and ({factor}) is not null
    and sb.evaluations_id = '{result_eval_id}'
    and ( {custom} )
    and sb.incrate > 1
  ) tblReorder
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- ml_evaluation incrate > 1 + stat_bork5 incrate range 
BEGIN GPT-4
select
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
  select
    row_number() over (partition by sb.kumiban order by {factor} desc, sb.hitrate desc) as ranking,
    sb.*
  from ml_evaluation me, stat_bork5 sb
  where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
    and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
    and me.evaluations_id = '666_0'
    and me.incomerate > 1
    and sb.bettype = '{bettype}' 
    <optional> and sb.kumiban = '{kumiban}' </optional>
    and sb.result_type = '{result_type}' 
    and sb.modelno = '{modelno}'
    and ({factor}) is not null
    and sb.evaluations_id = '{result_eval_id}'
    and ( {custom} )
    and sb.incrate between {incrmin} and {incrmax}
  ) tblReorder
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- stat_bork5 incrate range 99100
BEGIN GPT-5
select 
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from  
(
  select 
    row_number() over (partition by kumiban order by {factor} desc, hitrate desc) as ranking,
    *
  from stat_bork5 sb
  where
    bettype = '{bettype}' 
    <optional> and kumiban = '{kumiban}' </optional>
    and result_type = '{result_type}' 
    and modelno = '{modelno}'
    and incrate between {incrmin} and {incrmax}
    and ({factor}) is not null
    and evaluations_id = '{result_eval_id}'
    and ( {custom} )
) tmp
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- stat_bork5 incrate range 79100
BEGIN GPT-6
select 
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, ({factor})::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from  
(
  select 
    row_number() over (partition by kumiban order by {factor} desc, hitrate desc) as ranking,
    *
  from stat_bork5 sb
  where
    bettype = '{bettype}' 
    <optional> and kumiban = '{kumiban}' </optional>
    and result_type = '{result_type}' 
    and modelno = '{modelno}'
    and incrate between {incrmin} and {incrmax}
    and ({factor}) is not null
    and evaluations_id = '{result_eval_id}'
    and ( {custom} )
) tmp
where ranking <= {limit}
order by bettype, kumiban, ranking
END

-- FSB,GPT에 대한 simul1_test.xlsx#test_FSB(4)를 실행하기 위한 SQL
BEGIN FSB-SIM2-1
select 
  mes.* 
from ml_evaluation_sim mes,
(
	select 
	  id_grade, id_bettype, id_kumiban, id_factor, id_custom, id_limit, id_modelno, id_sql
	from
	(
	select row_number() over (partition by id_bettype, id_kumiban order by incamt desc) as ranking, * from
	(
	select 
	  id_grade,
	  id_bettype, 
	  id_kumiban,
	  id_factor,
	  id_custom,
	  id_limit,
	  id_modelno,
	  id_sql,
	  count(1) incrnum,
	  (sum(betcnt)::float / (31*6)::float)::numeric(7,2) dailybet,
	  (sum(hitamt) - sum(betamt)) incamt,
	  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(5,2) hitrate,
	  (sum(hitamt)::float / sum(betamt)::float)::numeric(5,2) incrate
	from
	(
	    select
	      me2.*
	    from
		(
	      select * from ml_evaluation_sim mes where id_term = '6661' and incomerate > 1 and bal_slope[0] > 0
	    ) me1,
		(
	      select * from ml_evaluation_sim mes where id_term = '6662' and incomerate > 1 and bal_slope[0] > 0
	    ) me2
	    where 
	      me1.id_grade = me2.id_grade and me1.id_bettype = me2.id_bettype and me1.id_kumiban = me2.id_kumiban and me1.id_factor = me2.id_factor and me1.id_custom = me2.id_custom 
	      and me1.id_incr = me2.id_incr and me1.id_limit = me2.id_limit and me1.id_modelno = me2.id_modelno and me1.id_sql = me2.id_sql 
	) ev
	where id_grade = '{grade_type}' and id_limit = '{simul2_limit}' 
	    and id_bettype = '{bettype}' and id_kumiban = '{kumiban}'
	    and ( {simul2_custom} )
	group by
	  id_grade,
	  id_bettype, 
	  id_kumiban,
	  id_factor,
	  id_custom,
	  id_limit,
	  id_modelno,
	  id_sql
	order by id_bettype, id_kumiban, incamt desc
	) tmp
	) tmp2
	where ranking = {ranking}
	order by id_bettype, id_kumiban, ranking
) tmp3
where mes.id_grade = tmp3.id_grade and mes.id_bettype = tmp3.id_bettype and mes.id_kumiban = tmp3.id_kumiban and mes.id_sql = tmp3.id_sql 
  and mes.id_factor = tmp3.id_factor and mes.id_custom = tmp3.id_custom and mes.id_modelno = tmp3.id_modelno and mes.id_limit = tmp3.id_limit
order by 
  id_grade, id_bettype, id_kumiban, id_factor, id_custom, id_limit, id_modelno, id_sql, id_incr, id_term
END
;

BEGIN JSJ-T2
	select 
	  '~' sel, 
	  (case when t2.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	  t2.bettype, t2.kumiban, t2.modelno, t2.patternid, t2.pattern, 
	  t2.betcnt, (t2.hitamt - t2.betamt) incamt, t2.hitrate, t2.incomerate,
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from 
	  ( select * from ml_evaluation where evaluations_id = '{term_1}' ) t1
	  , ( select * from ml_evaluation where evaluations_id = '{term_2}' ) t2
	where 
	    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban 
	    and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
	  and t2.result_type = '{result_type}' and t2.bettype = '{bettype}' 
	  and t2.modelno = '{modelno}' and t2.patternid = '{simul_patternid}'
	  and t1.{factor} between {factor_min} and 999
	  and t2.{factor} between {factor_min} and 999
	order by grades, bettype, kumiban, modelno, patternid, pattern
END
BEGIN JSJ-T3
	select 
	  '~' sel, 
	  (case when t3.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	  t3.bettype, t3.kumiban, t3.modelno, t3.patternid, t3.pattern, 
	  t3.betcnt, (t3.hitamt - t3.betamt) incamt, t3.hitrate, t3.incomerate,
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from 
	  ( select * from ml_evaluation where evaluations_id = '{term_1}' ) t1
	  , ( select * from ml_evaluation where evaluations_id = '{term_2}' ) t2
	  , ( select * from ml_evaluation where evaluations_id = '{term_3}' ) t3
	where 
	    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban 
	    and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
	    and t1.result_type = t3.result_type and t1.bettype = t3.bettype and t1.kumiban = t3.kumiban 
	    and t1.modelno = t3.modelno and t1.patternid = t3.patternid and t1.pattern = t3.pattern 
	  and t3.result_type = '{result_type}' and t3.bettype = '{bettype}' 
	  and t3.modelno = '{modelno}' and t3.patternid = '{simul_patternid}'
	  and t1.{factor} between {factor_min} and 999
	  and t2.{factor} between {factor_min} and 999
	  and t3.{factor} between {factor_min} and 999
	order by grades, bettype, kumiban, modelno, patternid, pattern
END
BEGIN JSJ-S2
	select 
	  '~' sel, 
	  (case when t2.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	  t2.bettype, t2.kumiban, t2.modelno, t2.patternid, t2.pattern, 
	  t2.betcnt, t2.incamt, t2.hitrate, t2.incrate,
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from 
	  ( select * from stat_bork where evaluations_id = '{term_1}' ) t1
	  , ( select * from stat_bork where evaluations_id = '{term_2}' ) t2
	where 
	    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban 
	    and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
	  and t2.result_type = '{result_type}' and t2.bettype = '{bettype}' 
	  and t2.modelno = '{modelno}' and t2.patternid = '{simul_patternid}'
	  and (t1.{factor}) between {factor_min} and 99999
	  and (t2.{factor}) between {factor_min} and 99999
	order by grades, bettype, kumiban, modelno, patternid, pattern
END
BEGIN JSJ-S3
	select 
	  '~' sel, 
	  (case when t3.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	  t3.bettype, t3.kumiban, t3.modelno, t3.patternid, t3.pattern, 
	  t3.betcnt, t3.incamt, t3.hitrate, t3.incrate,
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from 
	  ( select * from stat_bork where evaluations_id = '{term_1}' ) t1
	  , ( select * from stat_bork where evaluations_id = '{term_2}' ) t2
	  , ( select * from stat_bork where evaluations_id = '{term_3}' ) t3
	where 
	    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban 
	    and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
	    and t1.result_type = t3.result_type and t1.bettype = t3.bettype and t1.kumiban = t3.kumiban 
	    and t1.modelno = t3.modelno and t1.patternid = t3.patternid and t1.pattern = t3.pattern 
	  and t3.result_type = '{result_type}' and t3.bettype = '{bettype}' 
	  and t3.modelno = '{modelno}' and t3.patternid = '{simul_patternid}'
	  and (t1.{factor}) between {factor_min} and 99999
	  and (t2.{factor}) between {factor_min} and 99999
	  and (t3.{factor}) between {factor_min} and 99999
	order by grades, bettype, kumiban, modelno, patternid, pattern
END
BEGIN JSJ-S2-test
	select 
	  '~' sel, 
	  (case when t2.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	  t2.bettype, t2.kumiban, t2.modelno, t2.patternid, t2.pattern, 
	  t2.betcnt, t2.incamt, t2.hitrate, t2.incrate,
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from 
	  ( select * from stat_bork where evaluations_id = '{term_1}' ) t1
	  , ( select * from stat_bork where evaluations_id = '{term_2}' ) t2
	where 
	    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban 
	    and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
	  and t2.result_type = '{result_type}' and t2.bettype = '{bettype}' and t2.kumiban = '{kumiban}' 
	  and t2.modelno = '{modelno}' and t2.patternid = '{simul_patternid}'
	  and (t1.{factor}) between {factor_min} and 99999
	  and (t2.{factor}) between {factor_min} and 99999
	order by grades, bettype, kumiban, modelno, patternid, pattern
END
BEGIN JSJ-S3-test
	select 
	  '~' sel, 
	  (case when t3.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	  t3.bettype, t3.kumiban, t3.modelno, t3.patternid, t3.pattern, 
	  t3.betcnt, t3.incamt, t3.hitrate, t3.incrate,
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from 
	  ( select * from stat_bork where evaluations_id = '{term_1}' ) t1
	  , ( select * from stat_bork where evaluations_id = '{term_2}' ) t2
	  , ( select * from stat_bork where evaluations_id = '{term_3}' ) t3
	where 
	    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban 
	    and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
	    and t1.result_type = t3.result_type and t1.bettype = t3.bettype and t1.kumiban = t3.kumiban 
	    and t1.modelno = t3.modelno and t1.patternid = t3.patternid and t1.pattern = t3.pattern 
	  and t3.result_type = '{result_type}' and t3.bettype = '{bettype}' and t3.kumiban = '{kumiban}' 
	  and t3.modelno = '{modelno}' and t3.patternid = '{simul_patternid}'
	  and (t1.{factor}) between {factor_min} and 99999
	  and (t2.{factor}) between {factor_min} and 99999
	  and (t3.{factor}) between {factor_min} and 99999
	order by grades, bettype, kumiban, modelno, patternid, pattern
END
BEGIN ZEN-S2
	select 
	  '~' sel, 
	  (case when t2.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	  t2.bettype, t2.kumiban, t2.modelno, t2.patternid, t2.pattern, 
	  t2.betcnt, t2.incamt, t2.hitrate, t2.incomerate,
	  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from 
	    ( select * from v_rank where evaluations_id = '{term_1}' ) t1
	  , ( select * from v_rank where evaluations_id = '{term_2}' ) t2
	where 
	    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban 
	    and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
	  and t2.result_type = '{result_type}' and t2.bettype = '{bettype}' 
	  <optional_1> and t2.kumiban = '{kumiban}' </optional_1>
	  and t2.modelno = '{modelno}' 
	  <optional_2> and t2.patternid = '{simul_patternid}' </optional_2> 
	  and (t1.{factor}) between {factor_min} and {factor_max}
	  and (t2.{factor}) between {factor_min} and {factor_max}
	order by grades, bettype, kumiban, modelno, patternid, pattern
END

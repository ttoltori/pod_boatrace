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

# arff_1 with odds_monitor
BEGIN arff_2
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

#clf_1 with before odds
BEGIN clf_2
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
    prediction1, prediction2, prediction3, probability1, probability2, probability3
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
    alevelcount, com_confidence, com_predict, grade, turn, racetype, wakulevellist, arr.nationwiningrate::double precision[]
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

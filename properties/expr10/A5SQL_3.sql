select count(1) from rec_race where ymd::int between 20180601 and 20210601;

select  (hitamt - betamt) incamt, *
from ml_evaluation me 
where 
 modelno = '99100' and pattern = 'B1'
and resultno::int >= 4961
order by bettype, incamt desc;


select 
 substring(wakulevellist from 1 for 2) wk1, count(1)
from rec_race rr 
where grade not in ('ip', 'G3', 'G2')
group by wk1;



select * from ml_evaluation_bk1 meb 
where patternid = 'nopattern'
  and bettype = '2T' and kumiban = '12'
order by modelno  
;


select max(ymd::int) from rec_race rr;

select * from ml_evaluation me where resultno = '272483';


update ml_evaluation set result_type = '1' where result_type = '1C';
update ml_bork_evaluation set result_type = '1' where result_type = '1C';
update ml_pr_evaluation set result_type = '1' where result_type = '1C';
update ml_range_evaluation set result_type = '1' where result_type = '1C';
update ml_term_evaluation set result_type = '1' where result_type = '1C';

delete from ml_evaluation where resultno::int between 272477 and 273260;
delete from ml_bork_evaluation where resultno::int between 272477 and 273260;
delete from ml_pr_evaluation where resultno::int between 272477 and 273260;
delete from ml_range_evaluation where resultno::int between 272477 and 273260;
	

select
	    '~' sel, (case when me.result_type = '1' or me.result_type = '1C' then 'ip,G3' else 'SG,G1,G2' end) grades, 
	    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
	    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
	    me.hitrate::double precision, me.bal_pluscnt,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	from ml_evaluation me
    where resultno::int between 4961 and 5744
     and modelno || patternid = '97080turn+race'
     and bettype = '1T'
     and kumiban = '1'
     and incomerate between 1.01 and 99
 ;



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
and me.bettype = '1T' and me.kumiban = '1'
and me.result_type = '1'
and me.modelno = '99100'
and me.incomerate between 0.9 and 0.99
and me.patternid like 'wk1'


insert into ml_evaluation_bk1 select * from ml_evaluation where resultno::int between 3761 and 4960;
insert into ml_bork_evaluation_bk1 select * from ml_bork_evaluation where resultno::int between 3761 and 4960;
insert into ml_pr_evaluation_bk1 select * from ml_pr_evaluation where resultno::int between 3761 and 4960;
insert into ml_range_evaluation_bk1 select * from ml_range_evaluation where resultno::int between 3761 and 4960;

delete from ml_evaluation where resultno::int between 3761 and 4960;
delete from ml_bork_evaluation where resultno::int between 3761 and 4960;
delete from ml_pr_evaluation where resultno::int between 3761 and 4960;
delete from ml_range_evaluation where resultno::int between 3761 and 4960;

select distinct resultno from ml_evaluation_bk1 me where resultno::int between 3761 and 4960;

select count(1) from ml_evaluation_bk1;

select min(resultno::int), max(resultno::int)
from ml_evaluation me;

select distinct resultno  
from ml_evaluation meb
where resultno::int between 1 and 5000;


select 
*
from ml_bork_evaluation me where resultno::int between 3141 and 3172; 

insert into ml_evaluation select * from ml_evaluation_bk1 where resultno::int between 3141 and 3172;
insert into ml_bork_evaluation select * from ml_bork_evaluation_bk1 where resultno::int between 3141 and 3172;



select count(1) from rec_race;
where grade in ('ip', 'G3');


select min(ymd) from rec_race;

select * from ml_evaluation me where resultno::int between 4761 and 4960;


select 
  modelno, bettype, sum(betcnt) betcnt, sum(hitamt) - sum(betamt) income, (sum(hitamt) - sum(betamt)) / sum(betcnt)
from ml_evaluation me 
where result_type = '1' and patternid = 'race'
group by modelno, bettype
order by bettype, modelno
;


select 
  patternid, sum(betcnt)
from ml_evaluation me 
where modelno = '99080' and bettype = '1T' and kumiban = '1'
group by patternid 
order by patternid 
;


select min(resultno::int), max(resultno::int)
from ml_evaluation_bk1 me 
where result_type = '1'
;

select distinct resultno::int
from ml_evaluation_bk1 me 
order by resultno::int
;


select 
  modelno, ymd, count(1) 
from ml_classification mc 
where ymd::int between 20220923 and 20221104
  and modelno like '99%'
group by modelno, ymd
order by modelno, ymd
;

delete from ml_classification 
where modelno = '99080' and ymd::int between 20221101 and 20221105;

select 
  modelno, min(ymd), max(ymd)
from ml_classification mc 
group by modelno 
order by modelno
;

select 
  (sanrenhukuprize  / 100) odds, count(1) 
from rec_race rr 
where ymd::int between 20090101 and 20220531
  and sanrenhukupopular  between 1 and 10
--  and tansyono = '1'
group by odds 
order by odds 
;



select
  (probability1*100)::int pr, count(1)
from ml_classification mc 
where ymd::int between 20090101 and 20220531
  and modelno not like '%102' and modelno not like '%103'
group by pr 
order by pr 
;

select min(ymd), max(ymd) from rec_race;

-- evaluation 
select
  -- 'none' grp,
  ev.bettype, ev.kumiban, 
  ev.resultno, ev.modelno, ev.patternid, ev.pattern, 
  ev.betcnt, ev.betamt, ev.hitcnt, ev.hitamt, ev.betrate, (ev.hitamt - ev.betamt) incamt, ev.hitrate, ev.incomerate, 
  -- probability range
  range_betcnt best_betcnt, range_betrate best_betrate, range_worstmin worst_min, range_worstmax worst_max, range_bestmin best_min, range_bestmax best_max,
  -- hitodds range
  rangeodds_betcnt bestodds_betcnt, rangeodds_betrate bestodds_betrate, rangeodds_worstmin worstodds_min, rangeodds_worstmax worstodds_max, rangeodds_bestmin bestodds_min, rangeodds_bestmax bestodds_max,
  -- hitodds stat  
  hitodds_median, hitodds_mean, hitodds_stddev, hitodds_min, hitodds_max, 
  ev.balance[0], ev.balance[1],ev.balance[2],ev.bal_slope[0],ev.bal_slope[1],ev.bal_slope[2]
from ml_evaluation ev 
where 
  ev.bettype = '1T' and ev.kumiban = '1'  and ev.resultno::int between 70001 and 79999  and ev.patternid = 'sim+each' --1,2,단계모두
order by 
--  ev.bettype, 
--  ev.kumiban, 
--  betcnt desc,
  --ev.range_betrate desc,
  ev.rangeodds_betrate desc,
  ev.modelno, 
  ev.patternid,
  ev.pattern,
  (ev.probability_median-ev.hitrate),
  ev.betcnt desc,
  ev.resultno::int 
;

--   --mc.features_rank1 || '::' || mc.features_rank2 || '::' || mc.features_rank3 features123,


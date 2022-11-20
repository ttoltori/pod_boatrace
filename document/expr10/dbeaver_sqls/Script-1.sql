select
  '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
  me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
  me.betcnt, me.hitrate::double precision, me.bal_pluscnt,
  mbe.betcnt, mbe.hitcnt, mbe.betamt, mbe.hitamt, mbe.incamt, mbe.betrate, mbe.hitrate, mbe.incomerate
from ml_evaluation me, ml_bork_evaluation mbe
where 
  me.resultno::int between 3085 and 3120
  and me.bettype = '3X' and me.kumiban = '12'
  and me.result_type = '1'
  and me.modelno = '99100'
  and me.patternid = 'pd123+wk123'
  and me.incomerate between 1 and 100
  and me.resultno = mbe.resultno and me.result_type = mbe.result_type and me.bettype = mbe.bettype and me.kumiban = mbe.kumiban and me.modelno = mbe.modelno and me.patternid  = mbe.patternid and me.pattern = mbe.pattern
order by bettype, kumiban
;


select 
  ymd, 
  sime,
  jyocd,
  raceno,
  grade,
  isvenus, 
  timezone, 
  turn, sanrentanno, sanrentanprize, sanrentanpopular, sanrenhukuno, sanrenhukuprize, sanrenhukupopular, nirentanno, nirentanprize, nirentanpopular, nirenhukuno, nirenhukuprize, nirenhukupopular, tansyono, tansyoprize, tansyopopular, kimarite, wakurank, levelrank, resultlevelrank, nationwiningrank, nation2winingrank, nation3winingrank, localwiningrank, local2winingrank, local3winingrank, motor2rank, motor3rank, startexhibitrank, exhibitrank, averagestartrank, fixedentrance, racetype, wakulevellist, alevelcount, femalecount, avgstcondrank, setuwinrank, flrank, com_predict, com_confidence, weekday
  
from ml_result mr, rec_race rr 
where mr.ymd = rr.ymd and mr.jyocd = rr.jyocd and mr.raceno = rr.raceno 




select count(1) from ml_result;

select 
  modelno, stat_bettype, bettype, bet_kumiban, count(1) betcnt,
  sum(hitamt) - sum(betamt) incamt, 
  (sum(hitamt)::float / sum(betamt)::float)::numeric(7,2) incomerate,
  (sum(hity)::float / count(1)::float)::numeric(5,2) hitrate
from ml_result
group by modelno, stat_bettype, bettype, bet_kumiban
order by modelno, stat_bettype, bettype, bet_kumiban
--order by incamt desc;
;

select modelno, max(ymd) from ml_classification mc group by modelno;

select min(ymd), max(ymd) from ml_result mr where modelno = '99080';

select resultno, patternid, sum(i0) sum_i0
from stat_bork5 sb 
where result_type = '21' and bettype = '3T' and kumiban = '123'
group by resultno, patternid
order by sum_i0 desc;

select patternid, sum(i04) sum_i04, sum(betcnt)
from stat_bork5 sb 
where result_type = '21' and bettype = '3T' and kumiban = '123'
group by patternid
order by sum_i04 desc;




select 
  betcnt, (betcnt::float / 122::float)::numeric(5,1) d_betcnt, (hitamt - betamt) incamt, incomerate, hitrate
from ml_evaluation me 
where evaluations_id = '94835_2N_21_i04_50_1.2~1.29_FPH3_554_4_5_100_ip_default_x_x_9~28=1'
;



delete from ml_range_evaluation where result_type = '22' and patternid = 'x';

select result_type, bettype, count(1) from ml_range_evaluation mbe 
where result_type in ('21', '22')  group by result_type, bettype order by result_type, bettype;

select bettype, kumiban, count(1) from ml_evaluation me where resultno = '2678'
group by bettype, kumiban order by bettype, kumiban;

select * from ml_bork_evaluation mbe where resultno = '2678';

select max(resultno::int) 
from ml_evaluation me 
where result_type = '5'
  and resultno::int between 93574 and 93653
;

select max(resultno::int) from ml_evaluation me;

select result_type, count(1)
from stat_bork5
where patternid = 'x'
group by result_type
order by result_type
;

select count(distinct resultno)
from ml_bork_evaluation 
where resultno::int between 2876 and 3073
;


select 
  count(1)  
from ml_evaluation me, stat_pr_tmp sp
where me.resultno = sp.resultno and me.result_type = sp.result_type and me.bettype = sp.bettype and me.kumiban = sp.kumiban and me.modelno = sp.modelno and me.patternid  = sp.patternid and me.pattern = sp.pattern
;


select *
FROM ml_pr_evaluation where result_type = '21';



select bettype, count(1) from ml_evaluation me 
where result_type = '1' and bal_pluscnt > 2 and betcnt > 1000 
group by bettype
;

select count(1) from ml_pr_evaluation me
;

select 
  t1.patternid, 
  t2.cnt t_cnt,
  t1.betcnt betcnt, 
  (t1.cnt::float / t2.cnt::float)::numeric(5,2) pl_cnt,
  (t1.betcnt::float / t2.betcnt::float)::numeric(5,2) pl_betcnt,
  (t1.incamt::float / t2.incamt::float)::numeric(5,2) pl_incamt
from
(
	select patternid, 
	  count(1) cnt, 
	  sum(betcnt) betcnt,
	  sum(hitcnt) hitcnt,
	  sum(betamt) betamt,
	  sum(hitamt) hitamt,
	  sum(hitamt) - sum(betamt) incamt, 
	  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(7,2) hitrate,
	  (sum(hitamt)::float / sum(betamt)::float)::numeric(7,2) incomerate,
	  sum(bal_pluscnt) bal_pluscnt
	from ml_evaluation
	where result_type = '1' and modelno = '99100'
	  and bettype = '3U' and kumiban = '123'
	  and hitamt - betamt > 0
	group by patternid
	order by incamt desc
) t1, 
(
	select patternid, 
	  count(1) cnt, 
	  sum(betcnt) betcnt,
	  sum(hitcnt) hitcnt,
	  sum(betamt) betamt,
	  sum(hitamt) hitamt,
	  sum(hitamt) - sum(betamt) incamt, 
	  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(7,2) hitrate,
	  (sum(hitamt)::float / sum(betamt)::float)::numeric(7,2) incomerate,
	  sum(bal_pluscnt) bal_pluscnt
	from ml_evaluation
	where result_type = '1' and modelno = '99100'
	  and bettype = '3U' and kumiban = '123'
	group by patternid
	order by incamt desc
) t2
where t1.patternid = t2.patternid
order by pl_betcnt desc
;


select bettype, patternid, bettype, count(1) cnt, sum(i09) ss from stat_bork4 sb 
group by bettype, patternid, bettype 
order by bettype, ss desc
;

select
  (ss::float / cnt::float)::numeric(5,2) val, *
from (
	select patternid, bettype, count(1) cnt, sum(i09) ss from stat_bork4 sb 
	group by patternid, bettype 
	order by ss desc
)tblReorder
order by (ss::float / cnt::float) desc
;

select wakulevellist, count(1) cnt from rec_race group by wakulevellist order by cnt desc;

select  substring(wakulevellist from 1 for 11) wk1234, count(1) hitcnt
from rec_race race, ml_classification clf 
where race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno and sanrentanno <> 'Üôà÷Ø¡'
  and modelno = '99100'
  and grade in ('ip', 'G3')
  and (prediction1 || prediction2 || prediction3) = sanrentanno
  and sanrentanno = '123'
group by wk1234
order by hitcnt desc
;

select count(1) from stat_bork4 sb ;



select count(1) from stat_bork4 where patternid = 'wk1234' and true;

select * from ml_evaluation me where evaluations_id like '%FIH%';

select min(resultno), max(resultno) from ml_evaluation where resultno::int between 268019 and 268028;


select bettype, modelno, count(1) from stat_bork4 sb group by bettype , modelno order by bettype , modelno;


select count(1) from ml_evaluation me where resultno::int = 1575;

select distinct nirentanno from rec_race order by nirentanno;

select distinct resultno from ml_evaluation mbe where result_type = '11' order by resultno ;

select count(1) from ml_bork_evaluation mre  where resultno::int between 1293 and 1489;

select bettype, modelno, count(1) from ml_bork_evaluation mbe group by bettype, modelno order by bettype; 

select distinct kumiban from stat_bork4 sb where bettype = '2N' order by kumiban;

select distinct grade from rec_race;

select max(ymd) from ml_classification mc;

update rec_race set fixedentrance = 
	( case 
	  when fixedentrance = 'N' then 'N'
	  when fixedentrance = 'äÌïÒ÷ùÞÅéÄ' then 'Y'
	  when fixedentrance = 'òäìýÍ³ïÒ' then 'F'
	  end )
where ymd >= '20220805' and ymd <= '20220827';


select max(ymd) from rec_race;

select * from ml_evaluation me where result_type = '20';


select count(1) from ml_evaluation me where resultno::int between 20001 and 29999;

select count(*) from ml_evaluation me where resultno::int between 52334 and 59999;

select bbor from stat_range sr 
order by bbor asc limit 100;


insert into stat_range
select 
  result_type, grades, bettype, kumiban, resultno, modelno, patternid, pattern, 
  bpr, bror, brork, bbor, bbork, lpr, rpr, lror, rror, lrork, rrork, lbor, rbor, lbork, rbork, 
  incamt, betcnt, incrate, hitrate, bal_pluscnt
from (
  select 
    (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, 
	(me.pr_hitamt - me.pr_betamt)/500 bpr, 
	(me.ror_hitamt - me.ror_betamt)/500 bror,
	(me.rork_hitamt - me.rork_betamt)/500 brork,
	(me.bor_hitamt - me.bor_betamt)/500 bbor,
	(me.bork_hitamt - me.bork_betamt)/500 bbork,
	(mre.lpr_hitamt - mre.lpr_betamt)/500 lpr,
	(mre.rpr_hitamt - mre.rpr_betamt)/500 rpr,
	(mre.lror_hitamt - mre.lror_betamt)/500 lror,
	(mre.rror_hitamt - mre.rror_betamt)/500 rror,
	(mre.lrork_hitamt - mre.lrork_betamt)/500 lrork,
	(mre.rrork_hitamt - mre.rrork_betamt)/500 rrork,
	(mre.lbor_hitamt - mre.lbor_betamt)/500 lbor,
	(mre.rbor_hitamt - mre.rbor_betamt)/500 rbor,
	(mre.lbork_hitamt - mre.lbork_betamt)/500 lbork,
	(mre.rbork_hitamt - mre.rbork_betamt)/500 rbork,
    (me.hitamt - me.betamt) incamt, me.betcnt, 
    (me.hitamt::float / me.betamt::float)::numeric(5,2) incrate, (me.hitcnt::float / me.betcnt::float)::numeric(5,2) hitrate, me.bal_pluscnt
  from ml_evaluation me, ml_range_evaluation mre 
  where me.resultno = mre.resultno and me.result_type = mre.result_type and me.bettype = mre.bettype and me.kumiban = mre.kumiban and me.modelno = mre.modelno and me.patternid  = mre.patternid and me.pattern = mre.pattern
    and me.result_type in  ('1', '11')
) tblset
;




select result_type, count(1) from ml_range_evaluation mre group by result_type;

select
  bettype, kumiban, modelno, pattern, betcnt, (hitamt-betamt) incamt, hitrate, incomerate 
from ml_evaluation me 
where pattern = 'nopattern' and result_type = '1'
order by bettype, kumiban, modelno;


select 
  me.betcnt, (me.hitamt - me.betamt)/500 inc,  mre.*
from ml_range_evaluation mre, ml_evaluation me 
where me.resultno = mre.resultno and me.result_type = mre.result_type and me.bettype = mre.bettype and me.kumiban = mre.kumiban and me.modelno = mre.modelno and me.patternid  = mre.patternid and me.pattern = mre.pattern
  and mre.result_type in ('1', '11') and me.bettype = '3T' and me.kumiban = '126'
order by (rpr_hitamt-rpr_betamt)/500 desc, me.betcnt desc limit 100;


select 
  resultno, bettype, kumiban, betcnt, (hitamt-betamt) incamt, hitrate, incomerate incrate, bal_slope[0],
  pr_bestmin, pr_bestmax, pr_betcnt , (pr_hitamt - pr_betamt) pr_incamt, pr_hitrate, pr_incomerate 
from ml_evaluation me
where resultno::int between 24405 and 38223 -- ip
 and bettype = '2F' and kumiban = '16'
order by bettype, kumiban, resultno;


select 
  distinct ymd
from ml_classification mc where modelno = '99083'
order by ymd desc;

select distinct result_type from ml_evaluation me;

select max(ymd) from rec_race;

truncate stat_bork5;
insert into stat_bork5
select 
  result_type, grades, bettype, kumiban, resultno, modelno, patternid, pattern, 
  i0, i1, i2, i3, i4, i5, i6, i7, i8, i9,
  i04, i27, i59, i09, 
  incamt, betcnt, incrate, hitrate, bal_pluscnt
from (
  select 
    (case when me.result_type = '21' then 'ip,G3' else 'SG,G1,G2' end) grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, 
    bk.incamt[0]/500 i0, bk.incamt[1]/500 i1, bk.incamt[2]/500 i2, bk.incamt[3]/500 i3, bk.incamt[4]/500 i4, 
    bk.incamt[5]/500 i5, bk.incamt[6]/500 i6, bk.incamt[7]/500 i7, bk.incamt[8]/500 i8, bk.incamt[9]/500 i9, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500) i04, 
	(bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500) i27,    
    (bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i59, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i09, 
    (me.hitamt - me.betamt) incamt, me.betcnt, 
    (me.hitamt::float / me.betamt::float)::numeric(5,2) incrate, (me.hitcnt::float / me.betcnt::float)::numeric(5,2) hitrate, me.bal_pluscnt,     
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bk, 'x' range_selector, 'x' bonus_bkbor
  from ml_evaluation me, ml_bork_evaluation bk
  where me.resultno = bk.resultno and me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern
    and me.result_type in  ('21', '22')
    and me.patternid <> 'x'
    and me.resultno::int between 2876 and 3073
) tblset
;


insert into stat_bork4
select 
  result_type, grades, bettype, kumiban, resultno, modelno, patternid, pattern, 
  i0, i1, i2, i3, i4, i5, i6, i7, i8, i9,
  i01, i02, i03, i04, i05, i06, i07, i08, i09,
  i89, i79, i69, i59, i49, i39, i29, i19, 
  i13, i24, i35, i46, i57, i68, 
  i14, i25, i36, i47, i58, 
  i15, i26, i37, i48, i45,
  incamt, betcnt, incrate, hitrate, bal_pluscnt
from (
  select 
    (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, 
    bk.incamt[0]/500 i0, bk.incamt[1]/500 i1, bk.incamt[2]/500 i2, bk.incamt[3]/500 i3, bk.incamt[4]/500 i4, 
    bk.incamt[5]/500 i5, bk.incamt[6]/500 i6, bk.incamt[7]/500 i7, bk.incamt[8]/500 i8, bk.incamt[9]/500 i9, 
    (bk.incamt[0]/500 + bk.incamt[1]/500) i01, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500) i02, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500) i03, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500) i04, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500) i05,
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500) i06,
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500) i07,
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500) i08,
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i09,
    (bk.incamt[8]/500 + bk.incamt[9]/500) i89, 
    (bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i79, 
    (bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i69, 
    (bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i59, 
    (bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i49,
    (bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i39,
    (bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i29,
    (bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i19,
    (bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500) i13,
    (bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500) i24,
    (bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500) i35,
    (bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500) i46,
    (bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500) i57,
    (bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500) i68,
    -- (bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i79,
    (bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500) i14, 
    (bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500) i25, 
    (bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500) i36, 
    (bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500) i47, 
    (bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500) i58, 
    -- (bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i69,
	(bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500) i15,    
	(bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500) i26,    
	(bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500) i37,    
	(bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500) i48,    
	(bk.incamt[4]/500 + bk.incamt[5]/500) i45,    
	-- (bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i59,    
    (me.hitamt - me.betamt) incamt, me.betcnt, 
    (me.hitamt::float / me.betamt::float)::numeric(5,2) incrate, (me.hitcnt::float / me.betcnt::float)::numeric(5,2) hitrate, me.bal_pluscnt,     
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bk, 'x' range_selector, 'x' bonus_bkbor
  from ml_evaluation me, ml_bork_evaluation bk
  where me.resultno = bk.resultno and me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern
--    and me.result_type in  ('1', '11')
    -- and me.resultno::int between 2278 and 2671
  and me.patternid = 'wk1234'
) tblset
;


select
  me.modelno, me.bettype, me.kumiban, sum(me.hitamt-me.betamt) inc,
  sum(bk.incamt[0]) in0, sum(bk.incamt[1]) in1, sum(bk.incamt[2]) in2, sum(bk.incamt[3]) in3, sum(bk.incamt[4]) in4, 
  sum(bk.incamt[5]) in5, sum(bk.incamt[6]) in6, sum(bk.incamt[7]) in7, sum(bk.incamt[8]) in8, sum(bk.incamt[9]) in9
from ml_evaluation me, ml_bork_evaluation bk
where 
  me.resultno = bk.resultno and me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern
  and me.patternid = 'nopattern'
  and me.result_type in  ('1') 
  and me.modelno = '99100'
--  and me.ymd::int between 20210602 and 20220131
group by me.modelno, me.bettype, me.kumiban
order by me.modelno, me.bettype, me.kumiban
;

select
  me.modelno, me.bettype, me.kumiban, sum(me.betcnt) bc,
  sum(bk.betcnt[0]) bc0, sum(bk.betcnt[1]) bc1, sum(bk.betcnt[2]) bc2, sum(bk.betcnt[3]) bc3, sum(bk.betcnt[4]) bc4, 
  sum(bk.betcnt[5]) bc5, sum(bk.betcnt[6]) bc6, sum(bk.betcnt[7]) bc7, sum(bk.betcnt[8]) bc8, sum(bk.betcnt[9]) bc9
from ml_evaluation me, ml_bork_evaluation bk
where 
  me.resultno = bk.resultno and me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern
  and me.patternid = 'nopattern'
  and me.result_type in  ('1') 
  and me.modelno = '99100'
--  and me.ymd::int between 20210602 and 20220131
group by me.modelno, me.bettype, me.kumiban
order by me.modelno, me.bettype, me.kumiban
;
 
select * 
from ml_evaluation me 
where me.patternid = 'nopattern' and me.result_type in  ('1') and me.modelno = '99100'
order by bettype, kumiban 
;

select * 
from ml_bork_evaluation me
where me.patternid = 'nopattern' and me.result_type in  ('1') and me.modelno = '99100'
order by bettype, kumiban 
;


create table stat_bork2 as 
select 
  result_type, grades, bettype, kumiban, resultno, modelno, patternid, pattern, 
  row_number() over (partition by result_type, bettype, kumiban order by ( betcnt ) desc ) as ranking,
  i0, i01, i02, i03, i04, i05, incamt, betcnt, bal_pluscnt
from (
  select 
    (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, 
    bk.incamt[0]/500 i0,  
    (bk.incamt[0]/500 + bk.incamt[1]/500) i01, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500) i02, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500) i03,
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500) i04,
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500) i05,
    (me.hitamt - me.betamt) incamt, me.betcnt, me.bal_pluscnt,     
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bk, 'x' range_selector, 'x' bonus_bkbor
  from ml_evaluation me, ml_bork_evaluation bk
  where me.resultno = bk.resultno and me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern
    and me.result_type in  ('1', '11')
) tblset
where modelno not in ('99102','99103');

insert into stat_bork 
select 
  result_type, grades, bettype, kumiban, resultno, modelno, patternid, pattern, 
  row_number() over (partition by result_type, bettype, kumiban order by ( betcnt ) desc ) as ranking,
  i0, i1, i2, i3, i4, i5, i6, i7, i8, i9,
  i02, i13, i24, i35, i46, i57, i68, i79, i05, i09, incamt, betcnt, bal_pluscnt
from (
  select 
    (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, 
    bk.incamt[0]/500 i0, bk.incamt[1]/500 i1, bk.incamt[2]/500 i2, bk.incamt[3]/500 i3, bk.incamt[4]/500 i4, 
    bk.incamt[5]/500 i5, bk.incamt[6]/500 i6, bk.incamt[7]/500 i7, bk.incamt[8]/500 i8, bk.incamt[9]/500 i9, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500) i02, 
    (bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500) i13, 
    (bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500) i24, 
    (bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500) i35, 
    (bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500) i46, 
    (bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500) i57, 
    (bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500) i68, 
    (bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i79, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500) i05,
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i09,
    (me.hitamt - me.betamt) incamt, me.betcnt, me.bal_pluscnt,     
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bk, 'x' range_selector, 'x' bonus_bkbor
  from ml_evaluation me, ml_bork_evaluation bk
  where me.resultno = bk.resultno and me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern
    and me.result_type in  ('1', '11')
) tblset
where i09 > 0 and modelno <> '99103';

create index indexes_stat_bork on stat_bork (result_type, resultno, modelno, patternid, pattern, bettype, kumiban);

copy (
select 
  result_type, bettype, kumiban, count(1) cnt, i0, i1, i2, i3, i4, i5, i6, i7, i8, i9,
  (i0+i1+i2) i02, (i1+i2+i3) i13, (i2+i3+i4) i24, (i3+i4+i5) i35, 
  (i4+i5+i6) i46, (i5+i6+i7) i57, (i6+i7+i8) i68, (i7+i8+i9) i79,
  (i0+i1+i2+i3+i4+i5) i05,
  (i0+i1+i2+i3+i4+i5+i6+i7+i8+i9) i09,
  sum(i) inc, sum(bet) bet
from (
  select 
    me.result_type, me.bettype, me.kumiban, 
    me.betcnt bet, (me.hitamt - me.betamt) i, 
    incamt[0]/500 i0,
    incamt[1]/500 i1,
    incamt[2]/500 i2,
    incamt[3]/500 i3,
    incamt[4]/500 i4,
    incamt[5]/500 i5,
    incamt[6]/500 i6,
    incamt[7]/500 i7,
    incamt[8]/500 i8,
    incamt[9]/500 i9
  from ml_bork_evaluation mbe, ml_evaluation me
  where me.result_type = '11' and me.resultno = mbe.resultno and me.result_type = mbe.result_type and me.bettype = mbe.bettype and me.kumiban = mbe.kumiban and me.modelno = mbe.modelno and me.patternid  = mbe.patternid and me.pattern = mbe.pattern
    and me.modelno <> '99103'
    -- and bettype = '3T' and kumiban = '132' 
) tblset
where (i0+i1+i2+i3+i4+i5+i6+i7+i8+i9) > 0
--where (i0+i1+i2+i3+i4+i5) > 0
group by result_type, bettype, kumiban, i0, i1, i2, i3, i4, i5, i6, i7, i8, i9
order by result_type, bettype, kumiban, i09 
) to 'D:\Dev\experiment\expr10\work\bork_0to9_SG.tsv' csv delimiter E'\t' header;



copy (
select 
  result_type, bettype, kumiban, count(1) cnt, inc0, inc1, inc2, inc3, inc4, inc5, inc6, inc7, inc8, inc9,
  (inc0+inc1+inc2) inc0to2,
  (inc0+inc1+inc2+inc3+inc4+inc5) inc0to5,
  (inc0+inc1+inc2+inc3+inc4+inc5+inc6+inc7+inc8+inc9) inc0to9,
  sum(inc) inc, sum(bet) bet, 
  (sum(bet0)+sum(bet1)+sum(bet2)) bet0to2, 
  (sum(bet0)+sum(bet1)+sum(bet2)+sum(bet3)+sum(bet4)+sum(bet5)) bet0to5, 
  (sum(bet0)+sum(bet1)+sum(bet2)+sum(bet3)+sum(bet4)+sum(bet5)+sum(bet6)+sum(bet7)+sum(bet8)+sum(bet9)) bet0to9
--  sum(bet0) bet0, sum(bet1) bet1, sum(bet2) bet2, sum(bet3) bet3, sum(bet4) bet4, sum(bet5) bet5
from (
  select 
    me.result_type, me.bettype, me.kumiban, 
    me.betcnt bet, (me.hitamt - me.betamt) inc, 
    incamt[0]/500 inc0,
    incamt[1]/500 inc1,
    incamt[2]/500 inc2,
    incamt[3]/500 inc3,
    incamt[4]/500 inc4,
    incamt[5]/500 inc5,
    incamt[6]/500 inc6,
    incamt[7]/500 inc7,
    incamt[8]/500 inc8,
    incamt[9]/500 inc9,
    mbe.betcnt[0] bet0,
    mbe.betcnt[1] bet1,
    mbe.betcnt[2] bet2,
    mbe.betcnt[3] bet3,
    mbe.betcnt[4] bet4,
    mbe.betcnt[5] bet5,
    mbe.betcnt[6] bet6,
    mbe.betcnt[7] bet7,
    mbe.betcnt[8] bet8,
    mbe.betcnt[9] bet9
  from ml_bork_evaluation mbe, ml_evaluation me
  where me.result_type = '11' and me.resultno = mbe.resultno and me.result_type = mbe.result_type and me.bettype = mbe.bettype and me.kumiban = mbe.kumiban and me.modelno = mbe.modelno and me.patternid  = mbe.patternid and me.pattern = mbe.pattern
    and me.modelno <> '99103'
    -- and bettype = '3T' and kumiban = '132' 
) tblset
-- where (inc0+inc1+inc2+inc3+inc4+inc5+inc6+inc7+inc8+inc9) > 0
where (inc0+inc1+inc2+inc3+inc4+inc5) > 0
group by result_type, bettype, kumiban, inc0, inc1, inc2, inc3, inc4, inc5, inc6, inc7, inc8, inc9
-- order by result_type, bettype, kumiban, incamt0, incamt1, incamt2 
) to 'D:\Dev\experiment\expr10\work\bork0to9_no_99103_SG.tsv' csv delimiter E'\t' header;



select * from ml_classification mc where modelno = '99103' limit 100;


select 
  substring(wakulevellist from 1 for 5), count(1), sum(nirentanprize)
from rec_race rr 
where substring(sanrentanno from 1 for 2) = '16'
group by substring(wakulevellist from 1 for 5)
order by substring(wakulevellist from 1 for 5)
;

select bettype, kumiban, sum(betcnt) betcnt, (sum(hitamt) - sum(betamt)) incamt, (sum(hitcnt)::float / sum(betcnt)::float)::numeric(5,2) hitrate, 
  (sum(hitamt)::float / sum(betamt)::float)::numeric(5,2) incomerate
from ml_evaluation me
where result_type = '1'
group by bettype, kumiban 
order by bettype, kumiban;




copy (
select *
from (
	select 
	  result_type, bettype, kumiban, count(1) cnt, incamt0, incamt1, incamt2, (incamt0+incamt1+incamt2) incamt012, 
	  sum(betcnt) betcnt, (sum(betcnt0)+sum(betcnt1)+sum(betcnt2)) betcnt012, ((sum(betcnt0)+sum(betcnt1)+sum(betcnt2))::float / sum(betcnt)::float)::numeric(5,2) betrate,
	  sum(incamt) incamt, sum(betcnt0) betcnt0, sum(betcnt1) betcnt1, sum(betcnt2) betcnt2
	from (
	  select 
	    me.result_type, me.bettype, me.kumiban, 
	    me.betcnt betcnt,
	    (me.hitamt - me.betamt) incamt,
	    mbe.incamt[0]/500 incamt0,
	    mbe.incamt[1]/500 incamt1,
	    mbe.incamt[2]/500 incamt2,
	    mbe.betcnt[0] betcnt0,
	    mbe.betcnt[1] betcnt1,
	    mbe.betcnt[2] betcnt2
	  from ml_bork_evaluation mbe, ml_evaluation me  
	  where me.resultno = mbe.resultno and me.result_type = mbe.result_type and me.bettype = mbe.bettype and me.kumiban = mbe.kumiban and me.modelno = mbe.modelno and me.patternid  = mbe.patternid and me.pattern = mbe.pattern
	    and me.result_type = '1'
	) tblset
	group by result_type, bettype, kumiban, incamt0, incamt1, incamt2
	order by result_type, bettype, kumiban, incamt0, incamt1, incamt2 
) tblset2
where incamt012 > 0
) to 'D:\Dev\experiment\expr10\work\stat_bork012.tsv' csv delimiter E'\t' header;
;

copy (
select 
  result_type, bettype, kumiban, count(1) cnt, incamt0, incamt1, incamt2, incamt3, incamt4, incamt5,
  (incamt0+incamt1+incamt2+incamt3+incamt4+incamt5) incamt0to5,
  sum(betcnt) betcnt, 
  (sum(betcnt0)+sum(betcnt1)+sum(betcnt2)+sum(betcnt3)+sum(betcnt4)+sum(betcnt5)) betcnt0to5, sum(incamt) incamt,
  sum(betcnt0) betcnt0, sum(betcnt1) betcnt1, sum(betcnt2) betcnt2, sum(betcnt3) betcnt3, sum(betcnt4) betcnt4, sum(betcnt5) betcnt5
from (
  select 
    me.result_type, me.bettype, me.kumiban, 
    me.betcnt, (me.hitamt - me.betamt) incamt, 
    incamt[0]/500 incamt0,
    incamt[1]/500 incamt1,
    incamt[2]/500 incamt2,
    incamt[3]/500 incamt3,
    incamt[4]/500 incamt4,
    incamt[5]/500 incamt5,
    mbe.betcnt[0] betcnt0,
    mbe.betcnt[1] betcnt1,
    mbe.betcnt[2] betcnt2,
    mbe.betcnt[3] betcnt3,
    mbe.betcnt[4] betcnt4,
    mbe.betcnt[5] betcnt5
  from ml_bork_evaluation mbe, ml_evaluation me
  where me.result_type = '11' and me.resultno = mbe.resultno and me.result_type = mbe.result_type and me.bettype = mbe.bettype and me.kumiban = mbe.kumiban and me.modelno = mbe.modelno and me.patternid  = mbe.patternid and me.pattern = mbe.pattern
    -- and bettype = '3T' and kumiban = '132' 
) tblset
where (incamt0+incamt1+incamt2+incamt3+incamt4+incamt5) > 0
group by result_type, bettype, kumiban, incamt0, incamt1, incamt2, incamt3, incamt4, incamt5
order by result_type, bettype, kumiban, incamt0, incamt1, incamt2 
) to 'D:\Dev\experiment\expr10\work\stat_11_bork012345.tsv' csv delimiter E'\t' header;
;

select
  result_type, bettype, kumiban, sum(betcnt[0]) betcnt0, 
  sum(incamt[0]) incamt0, 
  (sum(hitcnt[0])::float / sum(betcnt[0])::float)::numeric(5,2) hitrate0,
  (sum(hitamt[0])::float / sum(betamt[0])::float)::numeric(5,2) incomerate0
from ml_bork_evaluation mbe 
where incamt[0] > 2000
  and bettype = '3T' and kumiban = '126'
group by result_type, bettype, kumiban
order by bettype, kumiban, result_type
;



select result_type, patternid, count(1)  from ml_range_evaluation mbe group by result_type, patternid order by result_type, patternid ;

select me.result_type, me.patternid,  sum(rpr_betcnt) sum_rprbetcnt, sum(rpr_hitamt  - rpr_betamt) sum_rprincamt, (sum(rpr_hitcnt)::float / sum(rpr_betcnt)::float )::numeric(5,2) rpr_hitrate,
  sum(pr_betcnt) sum_prbetcnt, sum(pr_hitamt  - pr_betamt) sum_princamt, (sum(pr_hitcnt)::float / sum(pr_betcnt)::float )::numeric(5,2) pr_hitrate
from ml_range_evaluation mre, ml_evaluation me 
where me.resultno = mre.resultno and me.result_type = mre.result_type and me.bettype = mre.bettype and me.kumiban = mre.kumiban and me.modelno = mre.modelno and me.patternid  = mre.patternid and me.pattern = mre.pattern
  and me.result_type = '12' and me.bettype = '3T' and me.kumiban = '123' 
  and me.modelno not in ('99103', 'x')
group by me.result_type, me.patternid
order by sum_princamt desc , sum_rprbetcnt desc;


select me.result_type, sum(rpr_betcnt) sum_rprbetcnt, sum(rpr_hitamt  - rpr_betamt) sum_rprincamt, (sum(rpr_hitcnt)::float / sum(rpr_betcnt)::float )::numeric(5,2) rpr_hitrate,
  sum(pr_betcnt) sum_prbetcnt, sum(pr_hitamt  - pr_betamt) sum_princamt, (sum(pr_hitcnt)::float / sum(pr_betcnt)::float )::numeric(5,2) pr_hitrate
from ml_range_evaluation mre, ml_evaluation me 
where me.resultno = mre.resultno and me.result_type = mre.result_type and me.bettype = mre.bettype and me.kumiban = mre.kumiban and me.modelno = mre.modelno and me.patternid  = mre.patternid and me.pattern = mre.pattern
  and me.bettype = '3T' and me.kumiban = '123' 
  and me.modelno not in ('99103', 'x')
  and me.modelno = '99080'
group by me.result_type 
order by sum_rprincamt desc , sum_rprbetcnt desc;

select * from ml_range_evaluation mre2 where result_type = '2';

select *
from (
  select 
    '~' sel, row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( rpr_bestmax - pr_bestmax ) asc ) as ranking, ( rpr_bestmax - pr_bestmax ) distance,
    'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, me.betcnt, (me.hitamt - me.betamt) incamt,
    me.pr_betrate, me.pr_betcnt, mre.rpr_betcnt, (pr_bestmin || '~' || pr_bestmax || '=1') bonus_pr,
    'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
    -- (rpr_bestmin || '~' || rpr_bestmax || '=1') bonus_pr
  from ml_evaluation me, ml_range_evaluation mre
  where me.resultno = mre.resultno and me.result_type = mre.result_type and me.bettype = mre.bettype and me.kumiban = mre.kumiban and me.modelno = mre.modelno and me.patternid  = mre.patternid and me.pattern = mre.pattern
    and me.result_type = '12' and me.bettype = '3T' and me.kumiban = '124'
    and (me.hitamt - me.betamt) > 0 
    and me.pr_bestmin = mre.rpr_bestmin 
    -- and me.modelno <> '99103'
    and me.betcnt > 260
) tblset
where ranking between 1 and 30
order by ranking;





select 
  pr_bestmin, count(1) cnt 
from ml_evaluation 
where result_type = '14' and bettype = '3T' and kumiban = '124'
group by pr_bestmin order by pr_bestmin 
;


-- ±¸¹Ì¹æº° ¼öÀÍ¼º Á¶»ç nopattern
select 'all' kubun,  patternid, bettype, kumiban,
  sum(bal_pluscnt) bal_pluscnt,
  sum(betcnt) betcnt,
  sum(hitcnt) hitcnt,
  sum(betamt) betamt,
  sum(hitamt) hitamt,
  sum(hitamt) - sum(betamt) incomeamt, 
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(7,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(7,2) incomerate
from ml_evaluation
where result_type = '1' 
--	and patternid = 'nopattern'
--	and (hitamt - betamt) > 0
--	and patternid like '%prob%'
group by patternid, bettype, kumiban  
order by bettype, kumiban
--order by incomeamt desc
;




-- betrate, ro_betrate ºÐÆ÷Á¶»ç
select betrate, rangeodds_betrate ro_betrate, sum(betcnt) / 6 total_betcnt
from ml_evaluation ev
where ev.bettype = '1T' and ev.kumiban = '1'  and ev.result_type = '1' 
  and betrate between 0.03 and 1
  and rangeodds_betrate between 0.2 and 0.29
group by betrate, ro_betrate
order by betrate desc, ro_betrate desc;


-- incomerate ºÐÆ÷Á¶»ç
select substring(incomerate::text from 1 for 3) total_incomerate, 
   count(1) eval_count,
   sum(betcnt) / 6 total_betcnt
from ml_evaluation ev
where ev.bettype = '1T' and ev.kumiban = '1'  and ev.result_type = '1' 
  and betrate >= 0.01
group by total_incomerate
order by total_incomerate desc;


select * from ml_evaluation
where betrate = 0.05 and rangeodds_betrate > 0.4;

--ÆÐÅÏ ºÐÆ÷ Á¶»ç
select modelno, ( substring(nationwiningrate[1]::text from 1 for 1) || '-' || race.racetype::text || '-' || race.alevelcount  ) ptn, count(*) cnt 
from rec_race race, ml_classification claf, rec_racer_arr arr
where race.ymd = claf.ymd and race.jyocd = claf.jyocd and race.raceno = claf.raceno
  and race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
  and claf.modelno::int between 99063 and 99063
  and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601
group by modelno, ptn 
order by cnt desc, ptn, modelno;

-- ml_evaluation µ¥ÀÌÅÍ Á¸Àç Á¶»ç
select resultno, count(1) cnt from ml_evaluation group by resultno order by resultno;

-- ml_result µ¥ÀÌÅÍ Á¸Àç Á¶»ç
select resultno, min(ymd), max(ymd), count(1) cnt from ml_result group by resultno order by resultno;

-- ml_classification µ¥ÀÌÅÍ Á¸Àç Á¶»ç
select modelno, min(ymd), max(ymd), count(1) cnt from ml_classification group by modelno order by modelno;


select 
  res.ymd ymd, res.jyocd jyocd, res.raceno raceno, 
  res.bettype, res.bet_kumiban, res.bet_odds,
  mc.prediction1, mc.prediction2 , mc.prediction3, mc.probability1, mc.probability2, mc.probability3,
  (res.hitamt::float / res.betamt::float)::text incomerate
from ml_result res, rec_race race, ml_classification mc 
where race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno
  and race.ymd = mc.ymd and race.jyocd = mc.jyocd and race.raceno = mc.raceno and res.modelno = mc.modelno 
  and race.ymd::int between 20210602 and 20211130
  and res.resultno::int = 946
order by ymd, jyocd, raceno;


select 
  count(1) cnt
from ml_classification mc 
where  
  ymd::int between 20210602 and 20211130
  and modelno::int = 99086
;


select min(betrate), max(betrate), min(rangeodds_betrate), max(rangeodds_betrate)
from ml_evaluation ev
where 
 ev.bettype = '1T' and ev.kumiban = '1'  and ev.result_type = '1' and patternid not like '%com%' 


select * from ml_evaluation where result_type = '22';

select bal_slope from ml_evaluation where resultno = '683';

-- ó·â÷1,2,3ªÎ÷áøù??îÜñéáãðàÞÛ
select 
  'digit123' digit123,
  modelno mno,
  substring(prediction from 1 for 3) predict,
  count(1) betcnt, 
  sum(case when substring(sanrentanno from 1 for 3) = ( substring (prediction from 1 for 3) ) then 1 else 0 end) hitcnt,
  (sum(case when substring(sanrentanno from 1 for 3) = ( substring (prediction from 1 for 3) ) then 1 else 0 end)::float / count(1)::float)::numeric(5,2) hitrate
from (
	select
	  (prediction1 || (case when prediction2 is null then '' else prediction2 end) || (case when prediction3 is null then '' else prediction3 end)) prediction, 
	  sanrentanno, modelno
	from rec_race race, 
		 ml_classification clf
	where race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno
	  and clf.ymd::int <= 20210601
) tmp1
group by 
   mno, 
   predict
order by 
   predict, 
   mno,
   hitrate desc,
   hitcnt desc, 
   betcnt desc
;

	  
select * from ml_classification where modelno::int = 99052;

select '3T-rank12' kubun,
  substring(sanrentanno from 1 for 2) keta1,
  count(1) cnt,
  avg(sanrentanprize::float / 100::float)::numeric(5,2) avg_hitodds,
  (count(1)::float / 162590::float)::numeric(5,2) hitrate,
  (sum(sanrentanprize)::float / (162590*100)::float)::numeric(5,2) incrate, 
  (sum(sanrentanprize) - (162590*100))::int incamt, 
  (stddev(sanrentanprize)::float / 100::float)::numeric(5,2) stddev_hitodds, 
  ((percentile_disc(0.5) within group (order by sanrentanprize))::float / 100::float)::numeric(5,2) center_hitodds, -- ü¾?þ·
  ((mode() within group (order by sanrentanprize))::float / 100::float)::numeric(5,2) mostlot_hitodds --ÉÌæîþ·
from rec_race race where sanrentanno <> 'Üôà÷Ø¡' 
  and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601
group by keta1 order by keta1;


select '3F' bettype, sanrenhukuno kumiban, 
  count(1) hitcnt, 
  (count(1)::float / 162590::float)::numeric(5,3) hitrate, 
  (sum(sanrenhukuprize)::float / (162590*100)::float)::numeric(5,2) incrate,
  avg(sanrenhukuprize::float / 100::float)::numeric(5,2) avg_hitodds, 
  (stddev(sanrenhukuprize)::float / 100::float)::numeric(5,2) stddev_hitodds,
  ((percentile_disc(0.5) within group (order by sanrenhukuprize))::float / 100::float)::numeric(5,2) center_hitodds, -- ü¾?þ·
  ((mode() within group (order by sanrenhukuprize))::float / 100::float)::numeric(5,2) mostlot_hitodds, --ÉÌæîþ·
  (sum(sanrenhukuprize) - (162590*100))::int incamt
from rec_race race
where sanrentanno <> 'Üôà÷Ø¡' and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601
group by bettype, kumiban
order by kumiban;

select ptn, count(*) cnt 
from rec_race race
where sanrentanno <> 'Üôà÷Ø¡' 
  and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601 group by ptn order by ptn;


select modelno, count(*) cnt from ml_classification group by modelno order by modelno; 

select * from ml_classification where modelno = '99049';

select (sort_desc(probabilities1))[6] from ml_classification;

select 
  odds.bettype, odds.kumiban, odds.odds, count(1)
from rec_race race, ml_classification clf, odds_result odds
where race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno and sanrentanno <> 'ÔòÐÈ?' 
    and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno 
    and race.tansyono = clf.prediction1 -- ?ü¾????
    and odds.bettype = '1T' and odds.kumiban = clf.prediction1 -- odds_result?ml_classification?JOIN
    and race.ymd::int between 20180601 and 20210601
    and clf.modelno = '99037'
group by odds.bettype, odds.kumiban, odds.odds
order by odds.bettype, odds.kumiban, odds.odds;

--ÆÐÅÏ ºÐÆ÷ Á¶»ç  old10
select modelno, (  kumiban1 || kumiban2  || kumiban3   ) ptn, count(*) cnt 
from rec_race race, ml_classification_old claf, rec_racer_arr arr
where race.ymd = claf.ymd and race.jyocd = claf.jyocd and race.raceno = claf.raceno
  and race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
  and claf.modelno::int between 8 and 8
  -- and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601 
group by modelno, ptn 
order by cnt desc, ptn, modelno;

--ÆÐÅÏ ºÐÆ÷Á¶»ç ž°ÁîÆ÷ÇÔ
select (  (odds::int)::text  ) ptn, count(*) cnt 
from rec_race race, ml_classification claf, odds_result odds
where race.ymd = claf.ymd and race.jyocd = claf.jyocd and race.raceno = claf.raceno
  and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno
  and odds.bettype = '3T' and odds.kumiban = '123' 
  and substring((prediction1 || prediction2 || prediction3) from 1 for 3) = '123'
--  and odds.kumiban = substring(claf.prediction1 from 1 for 3) 
  -- and odds.kumiban =  claf.prediction1 || claf.prediction2 || claf.prediction3
  and claf.modelno = '99065'
  and race.ymd ::int > 20180601 and race.ymd ::int < 20210601 group by (odds::int) order by (odds::int) desc ;

select * from ml_classification where modelno = '99059' order by ymd, jyocd, raceno, modelno;

select distinct prediction1 from ml_classification where modelno = '99008';

select max(ymd) from rec_race;

select prediction1 pre, count(*) cnt from ml_classification where modelno = '99040' group by pre order by cnt desc;

select cast(nationwiningrate[1] as double precision) from rec_racer_arr;

select com_predict, com_confidence from rec_race where ymd = '20180601';

select (sort_desc(probabilities1))[1] - (sort_desc(probabilities1))[2] from ml_classification
where modelno = '99041' and ymd = '20190101' and jyocd = '14' and raceno = 1;

select max(ymd) from rec_race where com_confidence = '';

select sanrentanno, count(*) cnt from rec_race
where ymd::int between 20180601 and 20211224
group by sanrentanno order by cnt desc;

select nirentanno, count(*) cnt from rec_race
group by nirentanno order by cnt desc;

select * from ml_classification where modelno = '99308';

select max(ymd) from rec_race;


select kumiban, min(ymd), max(ymd) from odds_monitor group by kumiban;

select bettype, kumiban, min(ymd), max(ymd) from odds_result group by bettype, kumiban;

select * from odds_monitor where kumiban = '1';

select 'nopattern' pattern, race.ymd, race.jyocd, race.raceno, race.sime, 
oddslist[0] om00,oddslist[1] om01,oddslist[2] om02,oddslist[3] om03,oddslist[4] om04,oddslist[5] om05,oddslist[6] om06,oddslist[7] om07,oddslist[8] om08,oddslist[9] om09,oddslist[10] om10,oddslist[11] om11,oddslist[12] om12,oddslist[13] 
om13,oddslist[14] om14,oddslist[15] om15,oddslist[16] om16,oddslist[17] om17,oddslist[18] om18,oddslist[19] om19,oddslist[20] om20,oddslist[21] om21,oddslist[22] om22,oddslist[23] om23,oddslist[24] om24,oddslist[25] om25,oddslist[26] 
om26,oddslist[27] om27,oddslist[28] om28,oddslist[29] om29,oddslist[30] om30,oddslist[31] om31,oddslist[32] om32,oddslist[33] om33,oddslist[34] om34,oddslist[35] om35,oddslist[36] om36,oddslist[37] om37,oddslist[38] om38,oddslist[39] om39 
from rec_race race, rec_racer_arr arr, odds_monitor odds where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno 
and sanrentanno <> 'ÔòÐÈ?' and grade in ('ip', 'G3', 'G2', 'G1', 'SG') and race.ymd >= '20210907' and race.ymd <= '20211224'  and (odds.bettype = '1T' and odds.kumiban = '1') order by pattern, race.ymd, race.sime; 


select * from odds_monitor where ymd = '20210907' and jyocd = '23' and raceno = 1 and bettype = '1T' and kumiban = '1';

select count(*) from ml_classification where resultno::int = 336 and kumiban = '1';

select distinct resultno::int from ml_evaluation order by resultno;

select distinct modelno, min(ymd) || '~' || max(ymd) from ml_classification group by modelno order by modelno ;

select modelno, min(ymd), max(ymd) from ml_classification where modelno::int  in (99063, 99080, 99083, 99086, 99089, 99094, 99097, 99098) group by modelno order by modelno;

select min(ymd), max(ymd), count(1) cnt from odds_monitor where bettype = '1T' and kumiban = '1'; 

select * from rec_racer_arr where jyocd = '14' and raceno = 1 and ymd = '20180601'; 

select count(*) from ml_classification;

select count(*) from ml_evaluation;

select * from ml_evaluation where result_type = '1' and pattern = 'nopattern';

select * from ml_classification where modelno::int = 99100 and ymd::int = 20220309 order by ymd;

select prediction3, count(*) cnt from ml_classification where modelno::int = 99070 group by prediction3;

select * from ml_evaluation;

select * from ml_classification where modelno::int = 99063 and ymd::int between 20211225 and 20220122;


select count(distinct patternid) from ml_evaluation where result_type = '1';

select * from ml_evaluation where result_type = '1' order by modelno, bettype, kumiban, patternid, pattern limit 2000; 

select * from ml_classification where modelno::int = 99103 limit 1000;

select * from ml_evaluation where result_type = '1' order by modelno, bettype, kumiban, patternid, pattern limit 2000; 

select * from ml_evaluation where result_type = '2' and bal_slope[0] > 8 and betcnt > 250 and pattern = '!all';

select * from ml_evaluation where result_type = '2' order by modelno, bettype, kumiban, patternid, pattern limit 100; 

select * from ml_evaluation where resultno::int = 50001; 

select 
*
from ml_evaluation ev where ev.bettype = '3F' and ev.kumiban = '462'  and ev.result_type = '1' 
and ( betcnt between 0 and 999999 )
and ( (hitamt - betamt) between 1 and 9999999 )
and ( hitrate between 0 and 1 )
and ( modelno::int in (99100) )
and (patternid in ('nw12'))
and (true)
order by ev.modelno, ev.patternid, ev.pattern, ev.resultno
;

select modelno, result_type, count(1) from ml_rork_evaluation mbe where patternid = 'nopattern' group by modelno, result_type ;


select
  bettype, kumiban, count(1)
from ml_evaluation me 
where bal_pluscnt >= 3 and result_type = '1'
group by bettype, kumiban
order by bettype, kumiban
;

select 
  bettype, kumiban, modelno,
  sum(betcnt) betcnt,
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(7,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(7,2) incomerate
from ml_evaluation me 
where 
  patternid = 'nopattern'
  and result_type = '1'
  and bettype = '2T'
--  and modelno::int in (99100, 99102, 99103)
group by bettype, kumiban, modelno
order by bettype, kumiban, modelno
;

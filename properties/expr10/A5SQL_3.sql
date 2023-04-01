delete from ml_evaluation where resultno = '95351';

select distinct evaluations_id  from stat_bork5;


select 
  ranking, mes.* 
from ml_evaluation_sim mes,
(
	select 
	  ranking, id_grade, id_bettype, id_kumiban, id_factor, id_custom, id_limit, id_modelno, id_sql
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
	where id_limit = '30' 
	   and id_bettype = '2T' and id_kumiban = '12'
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
	where ranking = 1
	order by id_bettype, id_kumiban, ranking
) tmp3
where mes.id_grade = tmp3.id_grade and mes.id_bettype = tmp3.id_bettype and mes.id_kumiban = tmp3.id_kumiban and mes.id_sql = tmp3.id_sql 
  and mes.id_factor = tmp3.id_factor and mes.id_custom = tmp3.id_custom and mes.id_modelno = tmp3.id_modelno and mes.id_limit = tmp3.id_limit
order by 
  id_grade, id_bettype, id_kumiban, id_factor, id_custom, id_limit, id_modelno, id_sql, id_incr, id_term
;


--{ip,1T,*-*-*,i09,wk,1.01~1.04,10,99100,6m,FSB-7,666,2,273895}
copy (
select
  id_bettype || '_' || id_kumiban category,  id_factor || '_' ||  id_custom || '_' ||  id_modelno  || '_' || id_sql sim_type_1,  id_incr sim_type_2,
  betcnt, hitcnt, betamt, hitamt, hitrate, incomerate, balance[0] balance, bal_slope[0] bal_slope, hitr_slope, incr_slope, 
  hodds_min, hodds_max, hodds_mean, hodds_stddev, hodds_median, 
  rodds_min, rodds_max, rodds_mean, rodds_stddev, rodds_median,
  bodds_min, bodds_max, bodds_mean, bodds_stddev, bodds_median
from
(
  select ids[1] id_grade, ids[2] id_bettype, kumiban id_kumiban, ids[4] id_factor, ids[5] id_custom, ids[6] id_incr, ids[7] id_limit, ids[8] id_modelno, ids[10] id_sql, (ids[11] || ids[12]) id_term, *
  from ml_evaluation_sim mes 
    where (ids[11] || ids[12]) = '6662'
      and incomerate > 1 and (bal_slope[0]) > 0 
) ev
where id_limit = '30' 
  and id_bettype = '1T' and id_kumiban = '1'
order by
  (hitamt - betamt) desc
limit 30
) to 'D:\Dev\experiment\expr10\simulation_results_1T_1.csv' csv header;

  select ids[1] id_grade, ids[2] id_bettype, kumiban id_kumiban, ids[4] id_factor, ids[5] id_custom, ids[6] id_incr, ids[7] id_limit, ids[8] id_modelno, ids[10] id_sql, (ids[11] || ids[12]) id_term, *
  from ml_evaluation_sim mes where (ids[11] || ids[12]) = '6662' and incomerate > 1 and (bal_slope[0]) > 0 and ids[7] = '30'
;

select 
  balance[0]
from ml_evaluation_sim mes 
;


select result_type, count(1) from stat_gpt sg group by result_type ;

create table stat_gpt_tmp as
select
  result_type, bettype, kumiban, resultno, modelno, patternid, pattern,
  ((betcnt::float - 1.0) / (655200 - 1.0) * 0.33) +  ((hitcnt::float - 1.0) / (31835 - 1.0) * 0.33) +  ((hitamt::float - betamt::float - -18304875.0) / (134193.0 - -18304875.0) * 0.33) gp333, 
  ((betcnt::float - 1.0) / (655200 - 1.0) * 0.1) +  ((hitcnt::float - 1.0) / (31835 - 1.0) * 0.1) +  ((hitamt::float - betamt::float - -18304875.0) / (134193.0 - -18304875.0) * 0.8) gp118,
  ((betcnt::float - 1.0) / (655200 - 1.0) * 0.1) +  ((hitcnt::float - 1.0) / (31835 - 1.0) * 0.8) +  ((hitamt::float - betamt::float - -18304875.0) / (134193.0 - -18304875.0) * 0.1) gp181, 
  ((betcnt::float - 1.0) / (655200 - 1.0) * 0.8) +  ((hitcnt::float - 1.0) / (31835 - 1.0) * 0.1) +  ((hitamt::float - betamt::float - -18304875.0) / (134193.0 - -18304875.0) * 0.1) gp811, 
  ((bal_slope[0] - -100) / (151945 - -100) * 0.33) + ((bal_slope[1] - -100) / (151945 - -100) * 0.33) + ((bal_slope[2] - -100) / (151945 - -100) * 0.33) slp333
from ml_evaluation
where evaluations_id = '666_0'
--  and bettype = '3T' and kumiban = '123'
--  and incomerate > 1
--  order by gp333 desc
;


select 
  result_type, bettype, kumiban, resultno, modelno, patternid, pattern, gp333, gp118, gp181, gp811, slp333,
  ((gp333 - (select min(gp333) from stat_gpt_tmp)) /  ((select max(gp333) from stat_gpt_tmp) - (select min(gp333) from stat_gpt_tmp)) * 0.5) + 
  
from stat_gpt_tmp  
;


select
  min(gp333) min333, max(gp333) max333, 
  min(gp118) min118, max(gp118) max118,
  min(gp181) min181, max(gp181) max181,
  min(gp811) min811, max(gp811) max811,
  min(slp333) minslp333, max(slp333) maxslp333
from stat_gtp_tmp
;

select * from ml_evaluation me where evaluations_id = '666_1' and (bork_hitamt -bork_betamt) = 1;

select 
  min(betcnt) bcmin, max(betcnt) bcmax, 
  min(hitcnt) hcmin, max(hitcnt) hcmax, 
  min(hitamt-betamt) icmin, max(hitamt-betamt) icmax, 
  min(bork_betcnt) bork_bcmin, max(bork_betcnt) bork_bcmax, 
  min(bork_hitcnt) bork_hcmin, max(bork_hitcnt) bork_hcmax, 
  min(bork_hitamt-bork_betamt) bork_icmin, max(bork_hitamt-bork_betamt) bork_icmax,
  min(bor_betcnt) bor_bcmin, max(bor_betcnt) bor_bcmax, 
  min(bor_hitcnt) bor_hcmin, max(bor_hitcnt) bor_hcmax, 
  min(bor_hitamt-bor_betamt) bor_icmin, max(bor_hitamt-bor_betamt) bor_icmax 
from ml_evaluation
where evaluations_id = '666_1'
;

select bal_slope[0] from ml_evaluation me where 
bal_slope[0] > -100 and 
result_type = '1'
order by  bal_slope[0]
;

select * from ml_evaluation me where bal_slope[0] > 10000;

alter table ml_evaluation add column gpt333 double precision;


select count(1) from ml_evaluation me;

ALTER TABLE stat_bork5 DROP COLUMN hm;
ALTER TABLE stat_bork5 DROP COLUMN hmr;
ALTER TABLE stat_bork5 DROP COLUMN borkhm;
ALTER TABLE stat_bork5 DROP COLUMN borkhmr;
ALTER TABLE stat_bork5 DROP COLUMN borhm;
ALTER TABLE stat_bork5 DROP COLUMN borhmr;


ALTER TABLE stat_bork5 ADD COLUMN gp double precision;
ALTER TABLE stat_bork5 ADD COLUMN borkgp double precision;
ALTER TABLE stat_bork5 ADD COLUMN borgp double precision;

select min(resultno::int), max(resultno::int) from ml_evaluation me where evaluations_id = '666_0';


select count(1) from ml_evaluation me where resultno::int between 7529 and 7609;

select min(resultno::int), max(resultno::int) from ml_evaluation me where result_type = '1';

select distinct resultno from ml_evaluation me 
where resultno::int >= 273485
order by resultno;


select
  row_number() over (partition by sb.kumiban order by i04 desc, sb.hitrate desc) as ranking,
  sb.*
from ml_evaluation me, stat_bork5 sb
where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
  and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
  and me.incomerate between 1.01 and 99
  and sb.bettype = '3T' 
  and sb.kumiban = '123' 
  and sb.result_type = '1' 
  and sb.modelno = '99100'
  and (i04) is not null
  and (i04) > 0
  and sb.evaluations_id = '99100_6m'
  and ( true )
order by ranking
;


select
'~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, (i04)::double precision factor,
incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
	select
	row_number() over (partition by sb.kumiban order by i04 desc, sb.hitrate desc) as ranking,
	sb.*
	from ml_evaluation me, stat_bork5 sb
	where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
	and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
	and me.evaluations_id = '666_0'
	and me.incomerate between 1.01 and 1.04
	and sb.bettype = '1T'
	and sb.result_type = '1'
	and sb.modelno = '99100'
	and (i04) is not null
	and (i04) > 0
	and sb.evaluations_id = '99100_6m'
	and ( true )
) tblReorder
where ranking <= 10
order by bettype, kumiban, ranking
;

	select
	row_number() over (partition by sb.kumiban order by i04 desc, sb.hitrate desc) as ranking,
	sb.*
	from ml_evaluation me, stat_bork5 sb
	where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
	and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern
	--and me.evaluations_id = '666_0'
	and me.resultno::int between 7501 and 7508
	and me.incomerate between 1.01 and 1.04
	and sb.bettype = '1T'
	and sb.result_type = '1'
	and sb.modelno = '99100'
	and (i04) is not null
	and (i04) > 0
	and sb.evaluations_id = '99100_6m'
	and ( true )
;


select
  '~' sel, grades, bettype, kumiban, ranking, resultno, modelno, patternid, pattern, (i04)::double precision factor,
  incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
select
  row_number() over (partition by sb.kumiban order by i04 desc, sb.hitrate desc) as ranking,
  sb.*
from ml_evaluation me, stat_bork5 sb
where me.result_type = sb.result_type and me.bettype = sb.bettype and me.kumiban = sb.kumiban
  and me.modelno = sb.modelno and me.patternid = sb.patternid and me.pattern = sb.pattern 
  and me.resultno::int between 7501 and 7508
  and me.incomerate between 0.9 and 99
  and sb.bettype = '3T' 
  and sb.kumiban = '126' 
  and sb.result_type = '1' 
  and sb.modelno = '99100'
  and (i04) is not null
  and (i04) > 0
  and sb.evaluations_id = '99100_6m'
  and ( true )
) tblReorder
where ranking <= 50
order by bettype, kumiban, ranking
;

select 
*
from ml_evaluation me where result_type = '1' and bettype = '3T' and kumiban = '123' and modelno = '99100'
and 

select distinct modelno from ml_classification mc;

select *
from (
  select
    '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, (i04)::double precision factor,
    incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
  from stat_bork5
  where
    bettype = '3T'
    and result_type = '1'
    and modelno = '79100'
    and incrate between 1.2 and 999
    and (i04) is not null
    and evaluations_id = '79100_14m'
    and ( patternid like '%wk%' )
    order by (i04) desc, hitrate desc limit 50
  ) tblForReorder
;
  
  select 
    ranking, '~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, (i04)::double precision factor,
    incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt,
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
  from  
  (
    select 
      row_number() over (partition by kumiban order by i04 desc, hitrate desc) as ranking,
      *
    from stat_bork5
    where
      bettype = '2N'
      and result_type = '1'
      and modelno = '79100'
      and incrate between 1.2 and 999
      and (i04) is not null
      and (i04) > 0
      and evaluations_id = '79100_14m'
      and ( patternid like '%wk%' )
  ) tmp
  where ranking <= 
;  
  



select max(ymd) from ml_classification mc;

select *
from (
select
'~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, (i04)::double precision factor,
incamt, betcnt, incrate, hitrate::double precision, bal_pluscnt, evaluations_id,
'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from stat_bork5
where
bettype = '1T'
and result_type = '1'
and modelno = '79100'
and incrate between 0.0 and 999
and (i04) is not null
--and evaluations_id = '79100_14m'
and ( patternid in ('wk1','wk12','wk123','wk1234') )
order by (i04) desc, hitrate desc limit 10
) tblForReorder
;

select bettype, count(1) from stat_bork5 sb where evaluations_id = '79100_14m'
group by bettype
order by bettype;

select
  bettype, 
  -- kumiban, 
  modelno,
  sum(betcnt) betcnt,
  sum((hitamt-betamt)) incamt, 
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(5,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(5,2) incomerate,
  sum(bal_pluscnt) balsum
from ml_evaluation me
where resultno::int between 6311 and 6706
  and result_type = '11' and incomerate > 1.01
--  group by modelno
group by bettype, modelno
;


select count(1) from ml_evaluation me;

select max(ymd) from ml_classification mc;


select * from ml_classification mc where modelno = '99100'; 

select * from ml_evaluation me where resultno = '6212';

-- ip_3F_*-*-*_i04_wkall_1.1~x_30_2y_775_2_273446

select
  --bettype, 
  --kumiban, 
  evalids[8] trainterm,
  sum(betcnt) betcnt,
  sum((hitamt-betamt)) incamt, 
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(5,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(5,2) incomerate
from
(
  select 
    string_to_array(evaluations_id, '_') evalids, *
  from ml_evaluation me 
  where 
    resultno::int between 273454 and 273458
) tmp
where 
  evalids[4] = 'i04'
  and evalids[5] = 'wkall'
  and evalids[10] = '2'
group by trainterm
order by trainterm
--group by bettype, trainterm
--order by bettype, trainterm
;
 



select 
    me.result_type, 'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
    bk.incamt[0]/500 i0, bk.incamt[1]/500 i1, bk.incamt[2]/500 i2, bk.incamt[3]/500 i3, bk.incamt[4]/500 i4, 
    bk.incamt[5]/500 i5, bk.incamt[6]/500 i6, bk.incamt[7]/500 i7, bk.incamt[8]/500 i8, bk.incamt[9]/500 i9, 
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500) i04, 
    (bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500) i27,
    (bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i59,
    (bk.incamt[0]/500 + bk.incamt[1]/500 + bk.incamt[2]/500 + bk.incamt[3]/500 + bk.incamt[4]/500 + bk.incamt[5]/500 + bk.incamt[6]/500 + bk.incamt[7]/500 + bk.incamt[8]/500 + bk.incamt[9]/500) i09,
    (me.hitamt - me.betamt) incamt, me.betcnt, me.incomerate, me.hitrate, me.bal_pluscnt
  from ml_evaluation me, ml_bork_evaluation bk
  where me.resultno = bk.resultno and me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern
    and me.resultno::int between 5883 and 6179
    and me.result_type = '1'
    and me.evaluations_id = '1y'
  order by me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern
; 

create index indexes_evaluationsid on ml_evaluation (evaluations_id);


select count(1) from ml_evaluation_bk1 me;

select min(resultno::int) from ml_evaluation;
select max(resultno::int) from ml_evaluation_bk1 where resultno::int < 10000;


select * from ml_evaluation me where resultno = '6180';

delete from ml_evaluation where resultno::int in (6180);

select
  s1.result_type, s1.bettype, s1.kumiban, s1.modelno, s1.patternid, (sum(s1.betcnt)::float / (365*3)::float)::numeric(5,1) dailybet 
from 
  (select * from ml_evaluation_tmp where serialno = 1) s1,
  (select * from ml_evaluation_tmp where serialno = 2) s2,
  (select * from ml_evaluation_tmp where serialno = 3) s3
where (s1.result_type = s2.result_type and s1.bettype = s2.bettype and s1.kumiban = s2.kumiban and s1.modelno = s2.modelno and s1.patternid = s2.patternid and s1.pattern = s2.pattern) 
 and (s1.result_type = s3.result_type and s1.bettype = s3.bettype and s1.kumiban = s3.kumiban and s1.modelno = s3.modelno and s1.patternid = s3.patternid and s1.pattern = s3.pattern)
 and (s1.incomerate > 1 and s2.incomerate > 1 and s3.incomerate > 1)
group by s1.result_type, s1.bettype, s1.kumiban, s1.modelno, s1.patternid
order by s1.result_type, s1.bettype, s1.kumiban, dailybet desc
;

select count(1) from rec_race;

select
  s1.*
from 
  (select * from ml_evaluation_tmp where serialno = 1) s1,
  (select * from ml_evaluation_tmp where serialno = 2) s2,
  (select * from ml_evaluation_tmp where serialno = 3) s3
where (s1.result_type = s2.result_type and s1.bettype = s2.bettype and s1.kumiban = s2.kumiban and s1.modelno = s2.modelno and s1.patternid = s2.patternid and s1.pattern = s2.pattern) 
 and (s1.result_type = s3.result_type and s1.bettype = s3.bettype and s1.kumiban = s3.kumiban and s1.modelno = s3.modelno and s1.patternid = s3.patternid and s1.pattern = s3.pattern)
 and (s1.incomerate > 1 and s2.incomerate > 1 and s3.incomerate > 1)
 and s1.result_type = '1' 
 and s1.bettype = '2T' 
 and s1.kumiban = '12' 
order by s1.result_type, s1.bettype, s1.kumiban, s1.modelno, s1.patternid, s1.pattern
;


create table ml_evaluation_tmp as 
select *,  row_number() over (partition by result_type, bettype, kumiban, modelno, patternid, pattern order by resultno::int) as serialno
from ml_evaluation me 
where resultno::int between 5787 and 5882

;

delete from ml_evaluation where resultno = '5805';

select * from st_patternid sp where patternid = 'nopattern';

select
*
from (
select
row_number() over (partition by result_type, bettype, kumiban order by p_betcnt desc) as ranking,
*
from st_patternid sp
where result_type = '1'
and bettype = '3T'
and modelno in ('97103')
and true
) tmp
where ranking <= 1
;


delete from st_patternid where modelno = '97103' and patternid like '%prob12%';

select distinct patternid from ml_evaluation me where modelno = '97102' and patternid like '%prob12%' order by patternid ;  

select *
from ml_evaluation me where modelno = '97103' and bettype = '3T' and kumiban = '123' and resultno = '5684';

delete from ml_evaluation where resultno::int between 273321 and 273325;

select max(resultno::int) from ml_evaluation_bk1 meb  where resultno::int < 10000 ;

select max(resultno::int) from ml_evaluation_bk1 meb  where resultno::int > 273260 ;

select * from ml_evaluation me where resultno::int = 5753 and bettype = '3T' order by betcnt desc;

select *
from ml_evaluation me where resultno = '4961' and bettype = '1T' and kumiban = '1';


select distinct resultno from ml_term_evaluation_bk1 meb 
where resultno::int between 4961 and 5756;


select count(1) from ml_classification mc ;


select 
--substring(wakulevellist from 1 for 14) wk, 
wakulevellist wk, 
count(1) cnt
from rec_race rr
where grade = 'ip'
group by wk 
order by cnt desc
;

select count(1)
from rec_race rr;


select count(1) from ml_evaluation me 
where result_type = '11'
and resultno::int between 4961 and 5752
;



select distinct resultno from ml_evaluation_bk1 meb 
where patternid = 'wk1234';

delete from ml_evaluation where resultno::int between 273261 and 273270;

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


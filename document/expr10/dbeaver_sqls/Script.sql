drop table if exists odds_monitor;
create table odds_monitor (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
bettype varchar(2),
kumiban varchar(4),
--oddslist double precision[],
--lastindex smallint,
bork int,
bor double precision,
rork int,
ror double precision,
);
drop index if exists indexes_odds_monitor;
create index indexes_odds_monitor on odds_monitor (ymd, jyocd, raceno);


copy (
select 
  stat_bettype, bettype, bet_kumiban, modelno,
  count(1) betcnt, 
  sum(hity) hitcnt,
  sum(betamt) betamt,
  sum(hitamt) hitamt,
  sum(hitamt-betamt) incamt
from ml_result mr, rec_race rr 
  where mr.ymd = rr.ymd and mr.jyocd = rr.jyocd and mr.raceno = rr.raceno
    and rr.sanrentanno <> 'Üôà÷Ø¡'
    and mr.ymd < '20210602'
group by stat_bettype, bettype, bet_kumiban, modelno
order by stat_bettype, bettype, bet_kumiban, modelno
) to 'D:\Dev\TableauRepository\boatml\betting°³¿ä.tsv' csv delimiter E'\t' header;
;






select count(1) from ml_bork_evaluation me where patternid = 'wk1234';

#ip,G3   SG,G1,G2  , 'x' bonus_borkbor

select
  sre.evaluations_id, 
  --lbor_bestmin, lbor_bestmax, lbor_betcnt, lbor_betamt, lbor_hitcnt, lbor_hitamt, lbor_betrate, lbor_hitrate, lbor_incomerate, 
  --rbor_bestmin, rbor_bestmax, rbor_betcnt, rbor_betamt, rbor_hitcnt, rbor_hitamt, rbor_betrate, rbor_hitrate, rbor_incomerate,
  betcnt, bork_bestmin, bork_bestmax, bork_betcnt, (hitamt-betamt) incamt, 
  lbork_bestmax, lbork_betcnt, (sre.lbork_hitamt-sre.lbork_betamt) lbork_incamt, lbork_betamt, lbork_hitcnt, lbork_hitamt, lbork_betrate, lbork_hitrate, lbork_incomerate 
  --rbork_bestmin, rbork_bestmax, rbork_betcnt, rbork_betamt, rbork_hitcnt, rbork_hitamt, rbork_betrate, rbork_hitrate, rbork_incomerate 
from st_evaluation se, st_range_evaluation sre 
where
  se.evaluations_id = sre.evaluations_id 
  and se.sim_term = '3'
  and se.sim_grade = 'ip'
  and substring(se.kumiban from 1 for 3) = '123'
  and incomerate > 1
--  and se.sim_factor = 'i09'
--order by se.sim_bettype, se.sim_model
order by boddsrk_stddev  
;

select
  bork_betcnt, *
from
(
	select 
	  abs(s1.bork_bestmin-s3.bork_bestmin) + abs(s1.bork_bestmax-s3.bork_bestmax) sa,
	  s3.evaluations_id evid, s3.bork_bestmin borkmin, s3.bork_bestmax borkmax,
	  s3.*
	from 
	(
	  select * 
	  from st_evaluation se
	  where sim_term = '1'
	) s1,
	(
	  select * 
	  from st_evaluation se
	  where sim_term = '3'
	) s3
	where 
	  s1.sim_bettype = s3.sim_bettype
	  and s1.sim_factor = s3.sim_factor
	  and s1.sim_limit = s3.sim_limit
	  and s1.sim_incr = s3.sim_incr
	  and s1.sim_group = s3.sim_group
	  and s1.kumiban = s3.kumiban
	  and s1.sim_model = s3.sim_model
	  and s1.sim_grade = s3.sim_grade
	  and s1.sim_prob = s3.sim_prob
	--  and s1.bork_bestmin = s3.bork_bestmin
	--  and s1.bork_bestmax = s3.bork_bestmax
	  and substring(s1.kumiban from 1 for 3) = '123'
	order by 
	-- s1.sim_bettype, s1.kumiban
	  abs(s1.bork_bestmin-s3.bork_bestmin) + abs(s1.bork_bestmax-s3.bork_bestmax)
) tmp
where sa < 10
order by (borkmax - borkmin) desc
;


select 
  se.sim_model, se.sim_bettype, sre.lbork_hitrate,
  sum(betcnt) betcnt, 
  sum(hitamt-betamt) incamt, 
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(5,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(5,2) incrate,
  sum(bork_betcnt) bork_betcnt,
  sum(bor_betcnt) bor_betcnt,
  sum(lbork_betcnt) lbork_betcnt,
  sum(lbor_betcnt) lbor_betcnt
from st_evaluation se, st_range_evaluation sre 
where
  se.evaluations_id = sre.evaluations_id 
  and se.sim_term = '3'
  and se.sim_grade = 'ip'
  and substring(se.kumiban from 1 for 3) = '123'
--  and se.sim_factor = 'i09'
group by se.sim_bettype, se.sim_model, sre.lbork_hitrate
--order by se.sim_bettype, se.sim_model
order by sre.lbork_hitrate desc
;



select
  me.evaluations_id, me.bork_bestmin, me.bork_bestmax, me.bork_betcnt, (me.bork_hitamt-me.bork_betamt) incomeamt, me.bork_betrate, me.bork_hitrate, me.bork_incomerate, 
  (me.hitamt-me.betamt) t_incomeamt, me.betcnt t_betcnt, me.hitrate t_hitrate
from st_evaluation me, st_range_evaluation mre 
where
  me.evaluations_id = mre.evaluations_id 
--  and me.bettype in ('3X', '3Y', '2N')
--  and me.bettype in ('3P', '3R')
  and me.kumiban = '21'
  and (
        me.evaluations_id like '%10_1.1%'
    and me.evaluations_id like '%842_3%'
    and me.evaluations_id like '%ip%'
  )
--  and me.incomerate > 1
--order by mre.lbork_betcnt desc
--order by me.bork_betcnt desc
order by (me.hitamt-me.betamt) desc, me.bork_betcnt desc, me.bork_bestmin 
;
  

select
  bettype, kumiban, evaluations_id, bettype, kumiban, betcnt, (hitamt - betamt) incamt, hitrate, incomerate, resultno
from ml_evaluation_sim me 
where 
  bettype = '3F' and kumiban = '123'
  and evaluations_id like '%_ip_%'
  and ( evaluations_id like '%_6_%' or evaluations_id like '%_8_%')
order by incamt desc
;



select *
from (
select
'~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, rpr factor, incamt, betcnt, bal_pluscnt,
'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from stat_range
where bettype = '1T' and kumiban = '1'
and result_type = '1'
and modelno in ('99080','99083','99100','99102')
and rpr is not null
order by rpr desc, betcnt desc limit 30
) tblset
order by betcnt desc limit 10;

truncate odds_monitor;

select * from ml_evaluation me where resultno = '99998';

select
'~' sel, grades, bettype, kumiban, resultno, modelno, patternid, pattern, 
i05, 
incamt, betcnt, incrate, hitrate, bal_pluscnt,
'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from stat_bork4
where bettype = '2T' and kumiban = '12'
and result_type = '1'
and modelno = '99080'
and incrate >= 0.95
order by betcnt desc limit 30
--order by i09 desc, betcnt desc limit 30
;

select substring(wakulevellist from 1 for 2) || '-' || substring(wakulevellist from 7 for 2) from rec_race rr ;

select *
from (
	select '~' sel, row_number() over (partition by result_type, bettype, kumiban order by ( betcnt ) desc ) as ranking, *
	from (
	  select 
	    'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, me.betcnt, (me.hitamt - me.betamt) incamt, me.bal_pluscnt, 
	    (bork.incamt[0]/500 + bork.incamt[1]/500 + bork.incamt[2]/500) binc012,
	    (bork.incamt[0]/500 + bork.incamt[1]/500 + bork.incamt[2]/500 + bork.incamt[3]/500 + bork.incamt[4]/500 + bork.incamt[5]/500) binc012345,
	    (bork.incamt[0]/500 + bork.incamt[1]/500 + bork.incamt[2]/500 + bork.incamt[3]/500 + bork.incamt[4]/500 + bork.incamt[5]/500 + bork.incamt[6]/500 + bork.incamt[7]/500 + bork.incamt[8]/500 + bork.incamt[9]/500) binc0to9,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	  from ml_evaluation me, ml_bork_evaluation bork, ml_rork_evaluation rork
	  where me.resultno = bork.resultno and me.result_type = bork.result_type and me.bettype = bork.bettype and me.kumiban = bork.kumiban and me.modelno = bork.modelno and me.patternid  = bork.patternid and me.pattern = bork.pattern
	    and me.resultno = rork.resultno and me.result_type = rork.result_type and me.bettype = rork.bettype and me.kumiban = rork.kumiban and me.modelno = rork.modelno and me.patternid  = rork.patternid and me.pattern = rork.pattern
	    and me.result_type = '1' and me.bettype = '1T' and me.kumiban = '2'
	) tblset
	where 
	  binc012345 between 25 and 39
	  --and incamt >= 0
	  --and bincamt0 >= 0 and bincamt1 >= 0 and bincamt2 >= 0 and bincamt3 >= 0 and bincamt4 >= 0 and bincamt5 >= 0
	  --bincamt0 >= 3
	  and modelno <> '99103'
) tblset2
where ranking between 1 and 200
order by betcnt desc;


select *
from (
	select '~' sel, row_number() over (partition by result_type, bettype, kumiban order by ( betcnt ) desc ) as ranking, *
	from (
	  select 
	    'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, me.betcnt, (me.hitamt - me.betamt) incamt, me.bal_pluscnt, 
	    rork.incamt[0]/500 rincamt0, rork.incamt[1]/500 rincamt1, rork.incamt[2]/500 rincamt2,
	    (rork.incamt[0]/500 + rork.incamt[1]/500 + rork.incamt[2]/500) rincamt012,
	    bork.incamt[0]/500 bincamt0, bork.incamt[1]/500 bincamt1, bork.incamt[2]/500 bincamt2, 
	    (bork.incamt[0]/500 + bork.incamt[1]/500 + bork.incamt[2]/500) bincamt012,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	  from ml_evaluation me, ml_bork_evaluation bork, ml_rork_evaluation rork
	  where me.resultno = bork.resultno and me.result_type = bork.result_type and me.bettype = bork.bettype and me.kumiban = bork.kumiban and me.modelno = bork.modelno and me.patternid  = bork.patternid and me.pattern = bork.pattern
	    and me.resultno = rork.resultno and me.result_type = rork.result_type and me.bettype = rork.bettype and me.kumiban = rork.kumiban and me.modelno = rork.modelno and me.patternid  = rork.patternid and me.pattern = rork.pattern
	    and me.result_type = '1' and me.bettype = '2T' and me.kumiban = '12'
	) tblset
	where 
	  bincamt0 >= 11
) tblset2
where ranking between 1 and 100
order by ranking;

#ip,G3   SG,G1,G2  , 'x' bonus_borkbor

select *
from (
	select '~' sel, row_number() over (partition by result_type, bettype, kumiban order by ( betcnt ) desc ) as ranking, *
	from (
	  select 
	    'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, me.betcnt, (me.hitamt - me.betamt) incamt, me.bal_pluscnt, 
	    (bork.incamt[0]/500 + bork.incamt[1]/500 + bork.incamt[2]/500 + bork.incamt[3]/500 + bork.incamt[4]/500 + bork.incamt[5]/500) bincamt012345,
	    bork.incamt[0]/500 bincamt0, bork.incamt[1]/500 bincamt1, bork.incamt[2]/500 bincamt2, bork.incamt[3]/500 bincamt3, bork.incamt[4]/500 bincamt4, bork.incamt[5]/500 bincamt5,
	    (bork.incamt[0]/500 + bork.incamt[1]/500 + bork.incamt[2]/500) bincamt012,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	  from ml_evaluation me, ml_bork_evaluation bork, ml_rork_evaluation rork
	  where me.resultno = bork.resultno and me.result_type = bork.result_type and me.bettype = bork.bettype and me.kumiban = bork.kumiban and me.modelno = bork.modelno and me.patternid  = bork.patternid and me.pattern = bork.pattern
	    and me.resultno = rork.resultno and me.result_type = rork.result_type and me.bettype = rork.bettype and me.kumiban = rork.kumiban and me.modelno = rork.modelno and me.patternid  = rork.patternid and me.pattern = rork.pattern
	    and me.result_type = '1' and me.bettype = '3T' and me.kumiban = '213'
	) tblset
	where 
	  bincamt012345 between 3 and 7
	  -- and modelno not in ('99103', '99102')
	  --and bincamt0 >= 0 and bincamt1 >= 0 and bincamt2 >= 0 and bincamt3 >= 0 and bincamt4 >= 0 and bincamt5 >= 0
	  -- and incamt >= 1000
	  --bincamt0 >= 3
) tblset2
where ranking between 1 and 100
order by betcnt desc;




select *
from (
  select 
    '~' sel,  row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( me.betcnt ) desc ) as ranking,
    'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, me.betcnt, (me.hitamt - me.betamt) incamt,
    me.bal_pluscnt, bork.betcnt[0] borkbetcnt0,
    bork.incamt[0]/500 bincamt0, bork.incamt[1]/500 bincamt1, bork.incamt[2]/500 bincamt2, (bork.incamt[0]/500+bork.incamt[1]/500+bork.incamt[2]/500) bincamt012,
    rork.incamt[0]/500 rincamt0, rork.incamt[1]/500 rincamt1, rork.incamt[2]/500 rincamt2, (rork.incamt[0]/500+rork.incamt[1]/500+rork.incamt[2]/500) rincamt012, 
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
  from ml_evaluation me, ml_bork_evaluation bork, ml_rork_evaluation rork
  where me.resultno = bork.resultno and me.result_type = bork.result_type and me.bettype = bork.bettype and me.kumiban = bork.kumiban and me.modelno = bork.modelno and me.patternid  = bork.patternid and me.pattern = bork.pattern
    and me.resultno = rork.resultno and me.result_type = rork.result_type and me.bettype = rork.bettype and me.kumiban = rork.kumiban and me.modelno = rork.modelno and me.patternid  = rork.patternid and me.pattern = rork.pattern
    and me.result_type = '1' and me.bettype = '2T' and me.kumiban = '12'
) tblset
where
  bal_pluscnt >= 2
  -- and ranking between 1 and 30
order by patternid, modelno desc limit 30;



select *
from (
	select '~' sel, row_number() over (partition by result_type, bettype, kumiban order by ( betcnt ) desc ) as ranking, *
	from (
	  select 
	    'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, me.betcnt, (me.hitamt - me.betamt) incamt, me.bal_pluscnt, 
	    incamt[0]/500 incamt0, incamt[1]/500 incamt1, incamt[2]/500 incamt2, incamt[3]/500 incamt3, incamt[4]/500 incamt4, incamt[5]/500 incamt5,
	    (incamt[0]/500 + incamt[1]/500 + incamt[2]/500) incamt012,
	    (incamt[0]/500 + incamt[1]/500 + incamt[2]/500 + incamt[3]/500 + incamt[4]/500 + incamt[5]/500) incamt012345,
	    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
	  from ml_evaluation me, ml_bork_evaluation bork
	  where me.resultno = bork.resultno and me.result_type = bork.result_type and me.bettype = bork.bettype and me.kumiban = bork.kumiban and me.modelno = bork.modelno and me.patternid  = bork.patternid and me.pattern = bork.pattern
	    and me.result_type = '1' and me.bettype = '2T' and me.kumiban = '13'
	) tblset
	where
	  -- incamt012345 between 7 and 10
	  -- and incamt0 >= 0 and incamt1 >= 0 and incamt2 >= 0 and incamt3 >= 0 and incamt4 >= 0 and incamt5 >= 0
	  -- and incamt012 > 6
	  -- and ranking between 1 and 30
) tblset2
where ranking between 1 and 30
order by ranking;




select *
from (
  select 
    '~' sel,  row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( me.betcbt ) desc ) as ranking,
    'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, me.betcnt, (me.hitamt - me.betamt) incamt,
    incamt[0]/1000 incamt0, incamt[1]/1000 incamt1, incamt[2]/1000 incamt2, (incamt[0]/1000+incamt[1]/1000+incamt[2]/1000) incamt012, 
    (incamt[0]/1000+incamt[1]/1000) incamt01, (incamt[1]/1000+incamt[2]/1000) incamt12, (incamt[0]/1000+incamt[2]/1000) incamt02,
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
  from ml_evaluation me, ml_bork_evaluation bork
  where me.resultno = bork.resultno and me.result_type = bork.result_type and me.bettype = bork.bettype and me.kumiban = bork.kumiban and me.modelno = bork.modelno and me.patternid  = bork.patternid and me.pattern = bork.pattern
    and me.result_type = '1' and me.bettype = '2T' and me.kumiban = '12'
) tblset
where
  incamt0 >= 1 and incamt012 >= 2
  -- incamt0 >= 1 and incamt1 >= 1 and incamt2 >= 1
  -- and incmat4 >= 0 and incamt2 >= 0
  -- incamt0 >= 1 and incamt1 >= 0 and incamt2 >= 0
  and ranking between 1 and 30
order by modelno, patternid desc;




select *
from (
  select 
    '~' sel, row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( bork.betcnt[0] ) desc ) as ranking, 
    -- '~' sel, row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( bork.betcnt[0]) desc ) as ranking, 
    'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, me.betcnt, (me.hitamt - me.betamt) incamt,
    ( bork.betcnt[0] + bork.betcnt[1] + bork.betcnt[2] ) bork012_betcnt,
    (( bork.betcnt[0] + bork.betcnt[1] + bork.betcnt[2] )::float / me.betcnt::float)::numeric(5,2) bork012_betrate,
    ( bork.incamt[0] + bork.incamt[1] + bork.incamt[2] ) bork012_incamt,
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
  from ml_evaluation me, ml_bork_evaluation bork
  where me.resultno = bork.resultno and me.result_type = bork.result_type and me.bettype = bork.bettype and me.kumiban = bork.kumiban and me.modelno = bork.modelno and me.patternid  = bork.patternid and me.pattern = bork.pattern
    and me.result_type = '1' and me.bettype = '3T' and me.kumiban = '134'
    -- and me.patternid like '%prob%' 
    -- and me.patternid not like '%nw%'
    and ( incamt[0] > 2000 )
    --and (incamt[0] + incamt[1] + incamt[2]) >= 6000
    -- and incamt[1] > 0 and incamt[2] > 0
    -- and incamt[0]/1000 >= 1 and incamt[1]/1000 >= 1 and incamt[2]/1000 >= 1
) tblset
where ranking between 1 and 200
order by ranking;



select *
from (
  select 
    -- '~' sel, row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( rpr_bestmax - pr_bestmax ) asc ) as ranking, ( rpr_bestmax - pr_bestmax )::numeric(5,2) distance,
    -- '~' sel, row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( pr_hitamt - pr_betamt ) desc ) as ranking, ( rpr_bestmax - pr_bestmax )::numeric(5,2) distance,
    '~' sel, row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( mre.lbork_betcnt ) desc ) as ranking, ( rpr_bestmax - pr_bestmax )::numeric(5,2) distance,
    'ip,G3' grades, me.bettype, me.kumiban, me.resultno, me.result_type, me.modelno, me.patternid, me.pattern, me.betcnt, (me.hitamt - me.betamt) incamt,
    me.pr_betrate, me.pr_hitrate, me.pr_betcnt, me.pr_incomerate,  mre.rpr_betcnt, 
    (pr_bestmin || '~' || pr_bestmax || '=1') bonus_pr,
    'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
    -- (rpr_bestmin || '~' || rpr_bestmax || '=1') bonus_pr
  from ml_evaluation me, ml_range_evaluation mre, ml_bork_evaluation mbe 
  where me.resultno = mre.resultno and me.result_type = mre.result_type and me.bettype = mre.bettype and me.kumiban = mre.kumiban and me.modelno = mre.modelno and me.patternid  = mre.patternid and me.pattern = mre.pattern
    and me.resultno = mbe.resultno and me.result_type = mbe.result_type and me.bettype = mbe.bettype and me.kumiban = mbe.kumiban and me.modelno = mbe.modelno and me.patternid  = mbe.patternid and me.pattern = mbe.pattern
    and me.result_type = '12' and me.bettype = '3T' and me.kumiban = '124'
    and me.patternid like 'prob%' and me.patternid not like '%nw%'
    and mre.lbork_bestmin = 1 and mbe.incamt[0] > 0
    -- and (me.hitamt - me.betamt) > 0 
    -- and me.pr_bestmin = mre.rpr_bestmin 
    -- and me.modelno not in ('99103')
    -- and me.betcnt > 260
) tblset
where ranking between 1 and 100
order by ranking;


select 
'~' sel, ranking, 'ip,G3' grades, me_bettype bettype, me_kumiban kumiban, me_resultno resultno, me_result_type result_type, me_modelno modelno, me_patternid patternid, me_pattern pattern, me_betcnt betcnt, me_incamt incamt, 
 rpr_betrate, rpr_betcnt, (rpr_hitamt - rpr_betamt) rpr_incamt, rpr_hitrate, rpr_incomerate, 
 -- lbor_betrate, lbor_betcnt, (lbor_hitamt - lbor_betamt) lbor_incamt, lbor_hitrate, lbor_incomerate, 
 'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_pr, 'x' bonus_borkbor
 -- bonusªòconcatª·ªÆí»ÔÑÓÛìý
from (
select
  row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( rng.rpr_betcnt ) desc ) as ranking,
  me.bettype me_bettype, me.kumiban me_kumiban, me.resultno me_resultno, me.result_type me_result_type, me.modelno me_modelno, me.patternid me_patternid, me.pattern me_pattern, me.betcnt me_betcnt, (me.hitamt-me.betamt) me_incamt, 
  rng.*
from 
	(
	select
       *
	from ml_evaluation me
	-- where  bal_pluscnt >= 2
	--  where bal_pluscnt >= 2  and (hitamt - betamt) > 0
	 -- where bal_pluscnt >= 2  and (hitamt - betamt) > 3000
	-- where balance[0] > 100000 and balance[1] > 100000 and balance[2] > 100000
	-- where bal_slope[0] > 8 and bal_slope[1] > 8 and bal_slope[2] > 8   
	) me,
	(	 
	 select
	   *
	 from ml_range_evaluation
	 where
	  result_type = '1'
	  and bettype = '3T' and kumiban = '123'
	  -- and rpr_betrate = 1
	  -- and rpr_hitrate > 0.15
	  -- and (rpr_hitamt - rpr_betamt) > 0
	  -- and patternid = 'nopattern'
	  -- and modelno::int in (99103)
	  --and modelno::int = 99100 and patternid = 'wk1' and pattern = 'B1'
	  -- and (incamt[0] + incamt[1] + incamt[2] + incamt[3] + incamt[4] + incamt[5]) > 0
	  -- and incamt[0] > 0
	  -- and ( (patternid like '%wk%') or  (patternid like '%prob%') ) 
	  -- and ( (patternid not like '%prob%') )
	  -- and (patternid not like '%nw%')
	) rng
where me.resultno = rng.resultno and me.result_type = rng.result_type and me.bettype = rng.bettype and me.kumiban = rng.kumiban and me.modelno = rng.modelno and me.patternid  = rng.patternid and me.pattern = rng.pattern
   and me.pr_bestmin = rng.rpr_bestmin
) tblset
where ranking between 1 and 30
;





select 
'~' sel, ranking, 'ip,G3' grades, bettype, kumiban, resultno, result_type, modelno, patternid, pattern, betcnt, incamt, bork_betcnt, bork_incamt, bork_betrate,
pr_bestmin, pr_bestmax,
 'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_pr, 'x' bonus_borkbor
from (
select
  --row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( bork.betcnt[0] + bork.betcnt[1] + bork.betcnt[2] + bork.betcnt[3] + bork.betcnt[4] ) desc ) as ranking,
  -- row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( bork.incamt[0] + bork.incamt[1] + bork.incamt[2] + bork.incamt[3] + bork.incamt[4]  + bork.incamt[5]) desc ) as ranking,
  row_number() over (partition by me.result_type, me.bettype, me.kumiban order by ( me.betcnt ) desc ) as ranking,
  ( bork.incamt[0] + bork.incamt[1] + bork.incamt[2] + bork.incamt[3] + bork.incamt[4]  + bork.incamt[5]) bork_incamt,
  ( bork.betcnt[0] + bork.betcnt[1] + bork.betcnt[2] + bork.betcnt[3] + bork.betcnt[4]  + bork.betcnt[5]) bork_betcnt,
  (( bork.betrate[0] + bork.betrate[1] + bork.betrate[2] + bork.betrate[3] + bork.betrate[4]  + bork.betrate[5]) / 6.0)::numeric(5,2) bork_betrate,
  me.*
from 
	(
	select
	  resultno, result_type, bettype, kumiban, modelno, patternid, pattern,
	  -- row_number() over (partition by result_type, bettype, kumiban order by (hitamt - betamt) desc ) as ranking,
	  betcnt,  (hitamt - betamt) incamt,  hitrate,  incomerate,  (hodds_median * hitrate)::numeric(10,2) exprate, pr_betcnt, bor_betrate, pr_bestmin, pr_bestmax, pr_betrate,
	  prob_mean
	from ml_evaluation
	-- where pr_incomerate > 1 
	--where (prob_min - prob_median) > 0
	-- where bor_betrate > 0.5
	-- where  (hitamt - betamt) > 4000
	 -- where bal_pluscnt >= 2  and (hitamt - betamt) > 3000
	-- where balance[0] > 100000 and balance[1] > 100000 and balance[2] > 100000
	-- where bal_slope[0] > 8 and bal_slope[1] > 8 and bal_slope[2] > 8   
	) me,
	(	 
	 select
	   resultno, result_type, bettype, kumiban, modelno, patternid, pattern, incamt, betcnt, hitcnt, betrate
	 from ml_bork_evaluation bork
	 where
	  result_type = '1'
	  and bettype = '1T' and kumiban = '4'
	  -- and patternid = 'nopattern'
	  and modelno::int in (99100, 99080) and patternid = 'nopattern'
	  --and modelno::int = 99100 and patternid = 'wk1' and pattern = 'B1'
	  -- and (incamt[0] + incamt[1] + incamt[2] + incamt[3] + incamt[4] + incamt[5]) > 0
	  -- and incamt[0] > 0
	  -- and ( (patternid like '%wk%') or  (patternid like '%prob%') ) 
	  -- and ( (patternid like '%prob%') )
	  -- and (patternid not like '%nw%')
	) bork
where me.resultno = bork.resultno and me.result_type = bork.result_type and me.bettype = bork.bettype and me.kumiban = bork.kumiban and me.modelno = bork.modelno and me.patternid  = bork.patternid and me.pattern = bork.pattern
order by betcnt desc
) tblset
where ranking between 1 and 30
;


----------------------------
#SG,G1,G2

select 
'~' sel, ranking, 'SG,G1,G2' grades, bettype, kumiban, resultno, result_type, modelno, patternid, pattern, betcnt, incamt, bork_betcnt, bork_incamt, bork_betrate,
 'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_pr
from (
select 
  row_number() over (partition by me.result_type, me.bettype, me.kumiban order by (bork.betcnt[0]) desc ) as ranking,
  bork.betcnt[0] bork_betcnt, bork.incamt[0] bork_incamt, bork.betrate[0] bork_betrate,
  me.*
from 
	(
	select
	  resultno, result_type, bettype, kumiban, modelno, patternid, pattern,
	  -- row_number() over (partition by result_type, bettype, kumiban order by (hitamt - betamt) desc ) as ranking,
	  betcnt,  (hitamt - betamt) incamt,  hitrate,  incomerate,  (hodds_median * hitrate)::numeric(10,2) exprate,
	  hodds_mean,  hodds_max,  hodds_stddev,  hitcnt,  betamt,  hitamt,
	  pr_betcnt,  pr_betamt,  pr_hitcnt,  pr_hitamt,  (pr_hitamt - pr_betamt) princamt,
	  (pr_hitcnt::float / pr_betcnt::float)::numeric(10,2) prhitrate,
	  (pr_hitamt::float / pr_betamt::float)::numeric(10,2) princrate,
	  bor_betcnt,  bor_betamt,  bor_hitcnt,  bor_hitamt,  (bor_hitamt - bor_betamt) borincamt,
	  (bor_hitcnt::float / bor_betcnt::float)::numeric(10,2) borhitrate,
	  (bor_hitamt::float / bor_betamt::float)::numeric(10,2) borincrate,
	  ( (bal_slope[0]+bal_slope[1]+bal_slope[2]) / 3 )::numeric(7,2)  balslope_avg
	from ml_evaluation
	where  incomerate >= 1.01
	) me,
	(	 
	 select
	   resultno, result_type, bettype, kumiban, modelno, patternid, pattern, incamt, betcnt, hitcnt, betrate
	 from ml_bork_evaluation bork
	 where
	  result_type = '11'
	  and bettype = '1T' and kumiban = '1'
	  and incamt[0] > 0 
	  and (patternid like '%prob%')
	) bork
where me.resultno = bork.resultno and me.result_type = bork.result_type and me.bettype = bork.bettype and me.kumiban = bork.kumiban and me.modelno = bork.modelno and me.patternid  = bork.patternid and me.pattern = bork.pattern
order by ranking
) tblset
where ranking between 1 and 50
;

	where  (hitamt - betamt) > 0
--	  and modelno::int = 99100
--	  and betcnt[0] > 40
--	  and (patternid like '%prob%' and pattern like '%0.6%')
--	  and patternid not like '%nw%'
--	  and patternid = 'nopattern'
--  row_number() over (partition by me.result_type, me.bettype, me.kumiban order by me.incamt desc ) as ranking,
--  row_number() over (partition by me.result_type, me.bettype, me.kumiban order by bork.incamt[0] desc ) as ranking,
--  row_number() over (partition by me.result_type, me.bettype, me.kumiban order by (bork.betcnt[0] + bork.betcnt[1]+bork.betcnt[2]) desc ) as ranking,
--  row_number() over (partition by me.result_type, me.bettype, me.kumiban order by me.betcnt desc ) as ranking,
--	where bal_slope[0] > 8 and bal_slope[1] > 8 and bal_slope[2] > 8

--	    rork.incamt[0]/500 rincamt0, rork.incamt[1]/500 rincamt1, rork.incamt[2]/500 rincamt2, rork.incamt[3]/500 rincamt3, rork.incamt[4]/500 rincamt4, rork.incamt[5]/500 rincamt5,
--	    (rork.incamt[0]/500 + rork.incamt[1]/500 + rork.incamt[2]/500) rincamt012,
--	    (rork.incamt[0]/500 + rork.incamt[1]/500 + rork.incamt[2]/500 + rork.incamt[3]/500 + rork.incamt[4]/500 + rork.incamt[5]/500) rincamt012345,

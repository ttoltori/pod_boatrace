select *
from ml_range_evaluation mre 
where rpr_incomerate < 1;


copy (
select 
--patternid
  me.result_type, me.bettype, me.kumiban, me.patternid, me.modelno,
  (case when me.resultno::int between 3176 and 3435 then 'term1' else 'term2' end) term,
-- total
  sum(betcnt) betcnt, sum(hitamt - betamt) incamt, 
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(5,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(5,2) incrate,  
  avg(me.bal_slope[0]) bal_slope,
-- best_pr
  sum( (case when pr_incomerate <= 1 then 0 else pr_betcnt end) ) pr_betcnt, 
  sum( (case when pr_incomerate <= 1 then 0 else pr_hitamt - pr_betamt end) ) pr_incamt,
  (sum( (case when pr_incomerate <= 1 then 0 else pr_betcnt end) )::float / sum(betcnt)::float)::numeric(5,2) pr_betrate,
  (sum(pr_hitcnt)::float / sum(pr_betcnt)::float)::numeric(5,2) pr_hitrate,
  (sum(pr_hitamt)::float / sum(pr_betamt)::float)::numeric(5,2) pr_incrate,
-- left pr
  sum( (case when lpr_incomerate <= 1 then 0 else lpr_betcnt end) ) lpr_betcnt, 
  sum( (case when lpr_incomerate <= 1 then 0 else lpr_hitamt - lpr_betamt end) ) lpr_incamt,
  (sum( (case when lpr_incomerate <= 1 then 0 else lpr_betcnt end) )::float / sum(betcnt)::float)::numeric(5,2) lpr_betrate,
  (sum(lpr_hitcnt)::float / sum(lpr_betcnt)::float)::numeric(5,2) lpr_hitrate,
  (sum(lpr_hitamt)::float / sum(lpr_betamt)::float)::numeric(5,2) lpr_incrate,
-- right pr
  sum( (case when rpr_incomerate <= 1 then 0 else rpr_betcnt end) ) rpr_betcnt, 
  sum( (case when rpr_incomerate <= 1 then 0 else rpr_hitamt - rpr_betamt end) ) rpr_incamt,
  (sum( (case when rpr_incomerate <= 1 then 0 else rpr_betcnt end) )::float / sum(betcnt)::float)::numeric(5,2) rpr_betrate,
  (sum(rpr_hitcnt)::float / sum(rpr_betcnt)::float)::numeric(5,2) rpr_hitrate,
  (sum(rpr_hitamt)::float / sum(rpr_betamt)::float)::numeric(5,2) rpr_incrate,
-- left bork
  sum( (case when lbork_incomerate <= 1 then 0 else lbork_betcnt end) ) lbork_betcnt, 
  sum( (case when lbork_incomerate <= 1 then 0 else lbork_hitamt - lbork_betamt end) ) lbork_incamt,
  (sum( (case when lbork_incomerate <= 1 then 0 else lbork_betcnt end) )::float / sum(betcnt)::float)::numeric(5,2) lbork_betrate,
  (sum(lbork_hitcnt)::float / sum(lbork_betcnt)::float)::numeric(5,2) lbork_hitrate,
  (sum(lbork_hitamt)::float / sum(lbork_betamt)::float)::numeric(5,2) lbork_incrate
from ml_evaluation me, ml_range_evaluation mre 
where me.resultno = mre.resultno and me.modelno = mre.modelno and me.patternid = mre.patternid and me.pattern = mre.pattern and me.bettype = mre.bettype and me.kumiban = mre.kumiban 
group by me.result_type, me.bettype, me.kumiban, me.patternid, me.modelno, term
order by me.result_type, me.bettype, me.kumiban, me.patternid, me.modelno
) to 'D:\Dev\TableauRepository\boatml\eval_patternid_term1232-12.tsv' csv delimiter E'\t' header;
;

copy (
select 
  me.result_type, me.bettype, me.kumiban, me.patternid, me.pattern, me.modelno, 
  me.betcnt, (me.hitamt - me.betamt) incamt, hitrate, incomerate, me.bal_slope[0] bal_slope,
  bork_bestmin, bork_bestmax, bork_betcnt, bork
  pr_bestmin, pr_bestmax, pr_betcnt, (pr_hitamt - pr_betamt) pr_incamt, pr_betrate, pr_hitrate, pr_incomerate,
  lpr_bestmin, lpr_bestmax, lpr_betcnt, (lpr_hitamt - lpr_betamt) lpr_incamt, lpr_betrate, lpr_hitrate, lpr_incomerate,
  rpr_bestmin, rpr_bestmax, rpr_betcnt, (rpr_hitamt - rpr_betamt) rpr_incamt, rpr_betrate, rpr_hitrate, rpr_incomerate,
  lbork_bestmin, lbork_bestmax, lbork_betcnt, (lbork_hitamt - lbork_betamt) lbork_incamt, lbork_betrate, lbork_hitrate, lbork_incomerate,
  lrork_bestmin, lrork_bestmax, lrork_betcnt, (lrork_hitamt - lrork_betamt) lrork_incamt, lrork_betrate, lrork_hitrate, lrork_incomerate
from ml_evaluation me, ml_range_evaluation mre 
where me.resultno = mre.resultno and me.modelno = mre.modelno and me.patternid = mre.patternid and me.pattern = mre.pattern and me.bettype = mre.bettype and me.kumiban = mre.kumiban 
  and me.bettype = '2T'
) to 'D:\Dev\TableauRepository\boatml\eval_2T.tsv' csv delimiter E'\t' header;
;


copy (
select 
  me.result_type, me.bettype, me.kumiban, me.patternid, me.modelno, 
  sum(betcnt) betcnt, sum(hitamt - betamt) incamt, 
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(5,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(5,2) incrate,
  sum(mre.lpr_betcnt) lpr_betcnt, sum(mre.lpr_hitamt - mre.lpr_betamt) lpr_incamt,
  sum(mre.rpr_betcnt) rpr_betcnt, sum(mre.rpr_hitamt - mre.rpr_betamt) rpr_incamt,
  sum(mre.lbork_betcnt) lbork_betcnt, sum(mre.lbork_hitamt - mre.lbork_betamt) lbork_incamt,
  sum(me.pr_betcnt) bestpr_betcnt, sum(me.pr_hitamt-me.pr_betamt) bestpr_incamt,
  sum(me.bork_betcnt) bestbork_betcnt, sum(me.bork_hitamt-me.bork_betamt) bestbork_incamt
from ml_evaluation me, ml_range_evaluation mre 
where me.resultno = mre.resultno and me.modelno = mre.modelno and me.patternid = mre.patternid and me.pattern = mre.pattern and me.bettype = mre.bettype and me.kumiban = mre.kumiban 
group by me.result_type, me.bettype, me.kumiban, me.patternid, me.modelno
order by me.result_type, me.bettype, me.kumiban, me.patternid, me.modelno
) to 'C:\Dev\temp\eval礖귟쫨.tsv' csv delimiter E'\t' header;
;


select 
max(resultno::int)
from ml_evaluation me 
where resultno::int between 3371 and 3435
;

select modelno, sum(betcnt)/1000,  count(1), sum(hitamt-betamt) from ml_evaluation me 
where incomerate >= 1 and bettype = '3T'
group by modelno 
order by modelno;



select modelno, min(ymd), max(ymd) 
from ml_classification mc 
group by modelno order by modelno
;

select * from ml_classification mc where modelno = '99301';

select
  substring(sanrentanno from 1 for 1 ) clazz, count(1) cnt
from rec_race
where ymd::int between 20180601 and 20210531
group by substring(sanrentanno from 1 for 1 )
order by clazz
;

select count(1) from ml_classification mc ;

select count(1) from ml_evaluation me ;

select
  * 
from odds_monitor om 
where bettype = '2T' and kumiban = '12'
order by ymd, jyocd, raceno;

select max(ymd) from rec_racer_arr;
update rec_race set fixedentrance = 
	( case 
	  when fixedentrance = 'N' then 'N'
	  when fixedentrance = '若됧츣�씮鵝욜뵪' then 'Y'
	  when fixedentrance = '�꿨뀯�쎓若�' then 'F'
	  end )
where ymd >= '20220923' and ymd <= '20221105';

select 
  kumiban, boddsrk_min, count(1)  
from ml_evaluation me 
where resultno::int between 1000 and 2000
  and bettype = '3T'
group by kumiban, boddsrk_min
order by kumiban, boddsrk_min
;

select * from ml_bork_evaluation mbe where resultno::int = 3145;

select 
 distinct resultno::int
from ml_evaluation me 
where resultno::int < 10000
order by resultno::int 
;

select 
  resultno,
  modelno,
  bettype,
  kumiban,
  split_part(evaluations_id, '_', 4) incr,
  split_part(evaluations_id, '_', 5) triming,
  split_part(evaluations_id, '_', 6) borkrange,
  split_part(evaluations_id, '_', 7) borkborconv,
  split_part(evaluations_id, '_', 8) groupsqlid,
  split_part(evaluations_id, '_', 9) term1,
  split_part(evaluations_id, '_', 10) term2,
  -- split_part(evaluations_id, '_', 11) result_type,
  -- split_part(evaluations_id, '_', 12) modelno,
    split_part(evaluations_id, '_', 13) grade,
    split_part(evaluations_id, '_', 14) custom
from ml_evaluation me 
where me.resultno::int between 270245 and 271306
;


select bettype , kumiban 
from odds_monitor om 
group by bettype , kumiban ;


copy (
select 
  * 
from ml_evaluation me 
where me.resultno::int between 270245 and 271306
) to 'D:\Dev\export\20221107\ml_evaluation.tsv' csv delimiter E'\t' header;
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
and me.resultno::int between 3085 and 3124
and me.bettype = '1T' and me.kumiban = '1'
and me.result_type = '1'
and me.modelno = '99100'
and me.incomerate between 0 and 99
and me.patternid = 'wk1,wk12,wk123,wk1234'

select min(resultno::int), max(resultno::int) 
from ml_evaluation me 
where resultno::int >= 270245;


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
and me.resultno::int between 3085 and 3120
and me.bettype = '3T' and me.kumiban = '123'
and me.result_type = '1'
and me.modelno = '99100'
and me.incomerate between 1 and 999
and me.patternid like '%'
;

select * from ml_bork_evaluation mbe where resultno = '3097';

select count(1) from ml_evaluation me where bettype = '2N';

select count(1) from ml_result;

select distinct resultno from ml_result mr;

select bettype, bet_kumiban, prediction1, count(1) 
from ml_pork_classification mpc 
group by bettype, bet_kumiban, prediction1
order by bettype, bet_kumiban, prediction1
;

select * 
from ml_evaluation me 
where 

select * from ml_evaluation me where result_type = '6';


select * from ml_classification mc 
where modelno = '99201';

select 
  *
from 
(
select 
  me.result_type, 
  me.bettype, 
  me.kumiban,
  me.modelno,
  me.patternid, 
  row_number() over (partition by me.result_type, me.bettype, me.kumiban, me.modelno order by (betcnt::float / t_betcnt::float)::numeric(5,2) desc ) as ranking,
  ptnnum, t_ptnnum,
 (betcnt::float / nullif(ptnnum::float,0) )::numeric(10,3) ptnbetcnt,
 (t_betcnt::float / nullif(t_ptnnum::float,0) )::numeric(10,3) t_ptnbetcnt,
  betcnt,  t_betcnt,  (betcnt::float / t_betcnt::float)::numeric(5,2) p_betrate,
  incamt, t_incamt,
  hitrate, t_hitrate,
  incrate, t_incrate,
  exprate, t_exprate,
  prbetcnt, t_prbetcnt,
  princamt, t_princamt,
  prhitrate, t_prhitrate,
  princrate, t_princrate,
  borbetcnt, t_borbetcnt,
  borincamt, t_borincamt,
  borhitrate, t_borhitrate,
  borincrate, t_borincrate
from 
(select 
  result_type, bettype, kumiban, modelno, patternid, 
  count(distinct pattern) ptnnum,  
  (sum(betcnt) / nullif(count(distinct pattern),0)) ptnbetcnt,
  sum(betcnt) betcnt,
  (sum(hitamt) - sum(betamt)) incamt,
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(10,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(10,2) incrate,
  avg(hodds_median * hitrate)::numeric(10,2) exprate,
  avg(hodds_mean)::numeric(10,2) avg_hoddsmean,
  avg(hodds_max)::numeric(10,2) avg_hoddsmax,
  avg(hodds_stddev)::numeric(10,2) avg_hoddsstddev,
  sum(hitcnt) hitcnt,
  sum(betamt) betamt,
  sum(hitamt) hitamt,
  sum(pr_betcnt) prbetcnt,
  sum(pr_betamt) prbetamt,
  sum(pr_hitcnt) prhitcnt,
  sum(pr_hitamt) prhitamt,
  (sum(pr_hitamt) - sum(pr_betamt)) princamt,
  (sum(pr_hitcnt)::float / sum(pr_betcnt)::float)::numeric(10,2) prhitrate,
  (sum(pr_hitamt)::float / sum(pr_betamt)::float)::numeric(10,2) princrate,
  sum(bor_betcnt) borbetcnt,
  sum(bor_betamt) borbetamt,
  sum(bor_hitcnt) borhitcnt,
  sum(bor_hitamt) borhitamt,
  (sum(bor_hitamt) - sum(bor_betamt)) borincamt,
  (sum(bor_hitcnt)::float / sum(bor_betcnt)::float)::numeric(10,2) borhitrate,
  (sum(bor_hitamt)::float / sum(bor_betamt)::float)::numeric(10,2) borincrate,
  avg( (bal_slope[0]+bal_slope[1]+bal_slope[2]) / 3 )::numeric(7,2)  balslope_avg
from ml_evaluation ev
where cardinality(balance) = 3
      --and (hitamt - betamt) > 0
	and (bork_bestmin) = 1
group by result_type, bettype, kumiban, modelno, patternid
) me,
    (select 
  result_type, bettype, kumiban, modelno, patternid, 
  count(distinct pattern) t_ptnnum,  
  (sum(betcnt) / nullif(count(distinct pattern),0)) t_ptnbetcnt,
  sum(betcnt) t_betcnt,
  (sum(hitamt) - sum(betamt)) t_incamt,
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(10,2) t_hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(10,2) t_incrate,
  avg(hodds_median * hitrate)::numeric(10,2) t_exprate,
  avg(hodds_mean)::numeric(10,2) t_avg_hoddsmean,
  avg(hodds_max)::numeric(10,2) t_avg_hoddsmax,
  avg(hodds_stddev)::numeric(10,2) t_avg_hoddsstddev,
  sum(hitcnt) t_hitcnt,
  sum(betamt) t_betamt,
  sum(hitamt) t_hitamt,
  sum(pr_betcnt) t_prbetcnt,
  sum(pr_betamt) t_prbetamt,
  sum(pr_hitcnt) t_prhitcnt,
  sum(pr_hitamt) t_prhitamt,
  (sum(pr_hitamt) - sum(pr_betamt)) t_princamt,
  (sum(pr_hitcnt)::float / sum(pr_betcnt)::float)::numeric(10,2) t_prhitrate,
  (sum(pr_hitamt)::float / sum(pr_betamt)::float)::numeric(10,2) t_princrate,
  sum(bor_betcnt) t_borbetcnt,
  sum(bor_betamt) t_borbetamt,
  sum(bor_hitcnt) t_borhitcnt,
  sum(bor_hitamt) t_borhitamt,
  (sum(bor_hitamt) - sum(bor_betamt)) t_borincamt,
  (sum(bor_hitcnt)::float / sum(bor_betcnt)::float)::numeric(10,2) t_borhitrate,
  (sum(bor_hitamt)::float / sum(bor_betamt)::float)::numeric(10,2) t_borincrate,
  avg( (bal_slope[0]+bal_slope[1]+bal_slope[2]) / 3 )::numeric(7,2)  t_balslope_avg
from ml_evaluation ev
where cardinality(balance) = 3
group by result_type, bettype, kumiban, modelno, patternid
) tme
where
  me.result_type  = tme.result_type and me.patternid  = tme.patternid and me.bettype = tme.bettype and me.kumiban = tme.kumiban and me.modelno = tme.modelno 
  and me.result_type = '1'
--  and me.modelno::int = 99100
--  and hodds_max < 100
  and me.bettype = '3T' 
--  and me.kumiban = '132'
order by 
  result_type, 
  bettype, 
  kumiban,
--  borbetcnt desc -- �쓳�옄�닾�몴�닔
  incamt desc
--  betcnt desc
--p_ptnnumrate desc
-- p_ptnbetcnt desc
--p_incamtrate desc
--incamt_stb desc
-- hitrate desc
) ranked
where 
  ranked.ranking between 1 and 10 -- �긽�쐞 10媛쒖뵫
;

------------------------------------------------------------------------------

-- simulation step 2
select 
  evaluations_id, *
from (
	select 
	   row_number() over (partition by bettype, kumiban order by bor_bestmin ) as ranking,
	   *
	from ml_evaluation 
	where result_type = '2' 
--	  and bal_slope[0] > 0
	  and pattern = '!all' 
	  and patternid is not null 
	  and incomerate > 0.95
--	  and bork_bestmin between 1 and 2
) ranked
where ranked.ranking between 1 and 3
order by bettype, kumiban, ranking
;


select 
  *
from 
(
select 
  me.resultno,
  me.result_type, 
  me.bettype, 
  me.kumiban,
  me.modelno,
  me.patternid, 
  row_number() over (partition by me.result_type, me.bettype, me.kumiban, me.modelno order by (betcnt::float / t_betcnt::float)::numeric(5,2) desc ) as ranking,
  ptnnum, t_ptnnum,
 (betcnt::float / nullif(ptnnum::float,0) )::numeric(10,3) ptnbetcnt,
 (t_betcnt::float / nullif(t_ptnnum::float,0) )::numeric(10,3) t_ptnbetcnt,
  betcnt,  t_betcnt,  (betcnt::float / t_betcnt::float)::numeric(5,2) p_betrate,
  incamt, t_incamt,
  hitrate, t_hitrate,
  incrate, t_incrate,
  exprate, t_exprate,
  prbetcnt, t_prbetcnt,
  princamt, t_princamt,
  prhitrate, t_prhitrate,
  princrate, t_princrate,
  borbetcnt, t_borbetcnt,
  borincamt, t_borincamt,
  borhitrate, t_borhitrate,
  borincrate, t_borincrate
from 
(select 
  resultno, result_type, bettype, kumiban, modelno, patternid, 
  count(distinct pattern) ptnnum,  
  (sum(betcnt) / nullif(count(distinct pattern),0)) ptnbetcnt,
  sum(betcnt) betcnt,
  (sum(hitamt) - sum(betamt)) incamt,
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(10,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(10,2) incrate,
  avg(hodds_median * hitrate)::numeric(10,2) exprate,
  avg(hodds_mean)::numeric(10,2) avg_hoddsmean,
  avg(hodds_max)::numeric(10,2) avg_hoddsmax,
  avg(hodds_stddev)::numeric(10,2) avg_hoddsstddev,
  sum(hitcnt) hitcnt,
  sum(betamt) betamt,
  sum(hitamt) hitamt,
  sum(pr_betcnt) prbetcnt,
  sum(pr_betamt) prbetamt,
  sum(pr_hitcnt) prhitcnt,
  sum(pr_hitamt) prhitamt,
  (sum(pr_hitamt) - sum(pr_betamt)) princamt,
  (sum(pr_hitcnt)::float / sum(pr_betcnt)::float)::numeric(10,2) prhitrate,
  (sum(pr_hitamt)::float / sum(pr_betamt)::float)::numeric(10,2) princrate,
  sum(bor_betcnt) borbetcnt,
  sum(bor_betamt) borbetamt,
  sum(bor_hitcnt) borhitcnt,
  sum(bor_hitamt) borhitamt,
  (sum(bor_hitamt) - sum(bor_betamt)) borincamt,
  (sum(bor_hitcnt)::float / sum(bor_betcnt)::float)::numeric(10,2) borhitrate,
  (sum(bor_hitamt)::float / sum(bor_betamt)::float)::numeric(10,2) borincrate,
  avg( (bal_slope[0]+bal_slope[1]+bal_slope[2]) / 3 )::numeric(7,2)  balslope_avg
from ml_evaluation ev
where (hitamt - betamt) > 0 
  and result_type = '2' and pattern = '!all'
group by resultno, result_type, bettype, kumiban, modelno, patternid
) me,
    (select 
  resultno, result_type, bettype, kumiban, modelno, patternid, 
  count(distinct pattern) t_ptnnum,  
  (sum(betcnt) / nullif(count(distinct pattern),0)) t_ptnbetcnt,
  sum(betcnt) t_betcnt,
  (sum(hitamt) - sum(betamt)) t_incamt,
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(10,2) t_hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(10,2) t_incrate,
  avg(hodds_median * hitrate)::numeric(10,2) t_exprate,
  avg(hodds_mean)::numeric(10,2) t_avg_hoddsmean,
  avg(hodds_max)::numeric(10,2) t_avg_hoddsmax,
  avg(hodds_stddev)::numeric(10,2) t_avg_hoddsstddev,
  sum(hitcnt) t_hitcnt,
  sum(betamt) t_betamt,
  sum(hitamt) t_hitamt,
  sum(pr_betcnt) t_prbetcnt,
  sum(pr_betamt) t_prbetamt,
  sum(pr_hitcnt) t_prhitcnt,
  sum(pr_hitamt) t_prhitamt,
  (sum(pr_hitamt) - sum(pr_betamt)) t_princamt,
  (sum(pr_hitcnt)::float / sum(pr_betcnt)::float)::numeric(10,2) t_prhitrate,
  (sum(pr_hitamt)::float / sum(pr_betamt)::float)::numeric(10,2) t_princrate,
  sum(bor_betcnt) t_borbetcnt,
  sum(bor_betamt) t_borbetamt,
  sum(bor_hitcnt) t_borhitcnt,
  sum(bor_hitamt) t_borhitamt,
  (sum(bor_hitamt) - sum(bor_betamt)) t_borincamt,
  (sum(bor_hitcnt)::float / sum(bor_betcnt)::float)::numeric(10,2) t_borhitrate,
  (sum(bor_hitamt)::float / sum(bor_betamt)::float)::numeric(10,2) t_borincrate,
  avg( (bal_slope[0]+bal_slope[1]+bal_slope[2]) / 3 )::numeric(7,2)  t_balslope_avg
from ml_evaluation ev
where result_type = '2' and pattern = '!all'
group by resultno, result_type, bettype, kumiban, modelno, patternid
) tme
where
  me.result_type  = tme.result_type and me.patternid  = tme.patternid and me.bettype = tme.bettype and me.kumiban = tme.kumiban and me.modelno = tme.modelno 
--    and pattern = '!all'
--  and me.result_type = '2'
--  and me.modelno::int = 99102
--  and hodds_max < 100
--  and me.bettype = '3T' and me.kumiban = '132'
order by 
  result_type, 
  bettype, 
  kumiban,
  betcnt desc -- �쓳�옄�닾�몴�닔
--  betcnt desc
--p_ptnnumrate desc
-- p_ptnbetcnt desc
--p_incamtrate desc
--incamt_stb desc
-- hitrate desc
) ranked
where 
  ranked.ranking between 1 and 3 -- �긽�쐞 10媛쒖뵫
;


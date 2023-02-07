

select *
from ml_evaluation_bk1 meb
where result_type = '1' and bettype = '2T' and kumiban = '12'
  and patternid = 'nopattern'
order by hitrate desc
;

select
  bettype, kumiban, modelno, (betcnt::float / (365*3)::float)::numeric(5,1) dailybet,
  (hitamt - betamt) incamt, hitrate, incomerate
from ml_evaluation me 
where patternid = 'nopattern'
  and result_type = '1' and bettype = '3T'
order by bettype, kumiban, incomerate desc
;  




select 
    '~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
    (me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
    me.hitrate::double precision, me.bal_pluscnt,
    'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from (
	select 
	  row_number() over (partition by me.bettype, me.kumiban order by mte.pluscnt desc) as ranking, 
 	  mte.pluscnt, mte.plusrate,  me.*
	from ml_evaluation me, ml_term_evaluation mte
	where me.resultno = mte.resultno and me.result_type = mte.result_type and me.bettype = mte.bettype and me.kumiban = mte.kumiban and me.modelno = mte.modelno and me.patternid  = mte.patternid and me.pattern = mte.pattern
	  and me.result_type = '1' 
	  and me.bettype = '3T' 
	  and me.kumiban = '126'
	  and me.incomerate between 1 and 99
	  and me.modelno like '%'
	  and me.patternid like '%'
) me
where ranking <= 30
order by bettype, kumiban, ranking
;


select 
  *
from (
   select
     row_number() over (partition by result_type, bettype, kumiban order by p_hmeanrate desc) as ranking,
     *
   from st_patternid sp
   where result_type = '1' 
) tmp
where ranking <= 1
order by bettype, kumiban
;

select min(resultno::int), max(resultno::int)
from ml_evaluation me 
where result_type = '1';

insert into st_patternid
select
  *,  
  (4 / (1/t_betcnt::float + 1/t_hitcnt::float + 1/t_incamt::float) + 1/termcnt::float)::numeric(11,2) t_hmean,
  (4 / (1/p_betcnt::float + 1/p_hitcnt::float + 1/p_incamt::float) + 1/pluscnt::float)::numeric(11,2) p_hmean,
  (2 / (1/t_hitrate + 1/t_incrate))::numeric(5,2) t_hmeanrate, 
  (4 / (1/p_betrate + 1/p_hitrate + 1/p_incrate + 1/plusrate))::numeric(5,2) p_hmeanrate
from
(
	select
	  pl.result_type, pl.bettype, pl.kumiban, pl.patternid, pl.modelno,
	  term.termcnt,
	  term.pluscnt,
	  (term.pluscnt::float / term.termcnt::float)::numeric(5,2) plusrate,
	  tot.betcnt t_betcnt, 
	  pl.betcnt p_betcnt, 
	  tot.hitcnt t_hitcnt, 
	  pl.hitcnt p_hitcnt, 
	  (tot.hitamt - tot.betamt)/100 t_incamt, 
	  (pl.hitamt - pl.betamt)/100 p_incamt, 
	  (pl.betcnt::float / tot.betcnt::float)::numeric(5,2) p_betrate,
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
		where result_type = '1'
		  and resultno::int between 4961 and 5744
		  and incomerate between 1.01 and 99
		  -- and bettype = '1T' and kumiban = '1'
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
		where result_type = '1'
		  and resultno::int between 4961 and 5744
		group by result_type, bettype, kumiban, patternid, modelno
		) tot,
  	    (
		  select
		    result_type, bettype, kumiban, patternid, modelno,
		    sum(termcnt) termcnt, 
		    sum(pluscnt) pluscnt
		  from ml_term_evaluation mte
		  where result_type = '1'
		    and resultno::int between 4961 and 5744
		  group by result_type, bettype, kumiban, patternid, modelno
		) term
	where pl.result_type = tot.result_type and pl.bettype = tot.bettype and pl.kumiban = tot.kumiban and pl.patternid = tot.patternid and pl.modelno = tot.modelno
	and pl.result_type = term.result_type and pl.bettype = term.bettype and pl.kumiban = term.kumiban and pl.patternid = term.patternid and pl.modelno = term.modelno
) tmp
where p_hitrate <> 0 and p_betrate <> 0 and p_incrate <> 0  
  and t_hitrate <> 0 and t_incrate <> 0
  and p_betcnt <> 0 and p_hitcnt <> 0 and p_incamt <> 0
  and t_betcnt <> 0 and t_hitcnt <> 0 and t_incamt <> 0
  and ((1/t_betcnt::float + 1/t_hitcnt::float + 1/t_incamt::float)) <> 0
  and ((1/p_betcnt::float + 1/p_hitcnt::float + 1/p_incamt::float)) <> 0
  and ((1/t_hitrate + 1/t_incrate)) <> 0
  and ((1/p_betrate + 1/p_hitrate + 1/p_incrate)) <> 0
 ;

truncate st_patternid;

select * from st_patternid sp where t_incamt > 0;

select count(1) from ml_evaluation;
select min(resultno::int), max(resultno::int)
from ml_evaluation me;

select count(1) from ml_term_evaluation mte; 

select count(1)
from ml_classification mcb; 

delete from ml_classification where ymd::int >= 20221106;


delete from ml_evaluation where resultno::int between 272258 and 272267;

select distinct result_type from ml_evaluation me;

select distinct resultno::int from ml_evaluation_bk1 meb order by resultno::int;

select min(resultno), max(resultno) from ml_evaluation me where result_type = '1'; 

insert into ml_evaluation_bk1 select * from ml_evaluation where resultno::int between 3176 and 3695;
delete from ml_evaluation where  resultno::int between 3176 and 3695;

insert into ml_evaluation select * from ml_evaluation_bk1 where resultno::int between 1 and 648; 
truncate st_patternid;

select * from st_patternid sp 
order by result_type, bettype, kumiban, p_hmean desc; 

truncate st_patternid;

insert into st_patternid
select
  *,  
  (3 / (1/t_betcnt::float + 1/t_hitcnt::float + 1/t_incamt::float))::numeric(11,2) t_hmean,
  (3 / (1/p_betcnt::float + 1/p_hitcnt::float + 1/p_incamt::float))::numeric(11,2) p_hmean,
  (2 / (1/t_hitrate + 1/t_incrate))::numeric(5,2) t_hmeanrate, 
  (3 / (1/p_betrate + 1/p_hitrate + 1/p_incrate))::numeric(5,2) p_hmeanrate
from
(
	select
	  pl.result_type, pl.bettype, pl.kumiban, pl.patternid, pl.modelno,
	  tot.betcnt t_betcnt, 
	  pl.betcnt p_betcnt, 
	  tot.hitcnt t_hitcnt, 
	  pl.hitcnt p_hitcnt, 
	  (tot.hitamt - tot.betamt)/100 t_incamt, 
	  (pl.hitamt - pl.betamt)/100 p_incamt, 
	  (pl.betcnt::float / tot.betcnt::float)::numeric(5,2) p_betrate,
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
		where result_type = '11'
		  and incomerate between 1.01 and 99
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
		where result_type = '11'
		group by result_type, bettype, kumiban, patternid, modelno
		) tot
	where pl.result_type = tot.result_type and pl.bettype = tot.bettype 
	  and pl.kumiban = tot.kumiban and pl.patternid = tot.patternid 
	  and pl.modelno = tot.modelno
) tmp
where p_hitrate <> 0 and p_betrate <> 0 and p_incrate <> 0  
  and t_hitrate <> 0 and t_incrate <> 0
  and p_betcnt <> 0 and p_hitcnt <> 0 and p_incamt <> 0
  and t_betcnt <> 0 and t_hitcnt <> 0 and t_incamt <> 0
  and ((1/t_betcnt::float + 1/t_hitcnt::float + 1/t_incamt::float)) <> 0
  and ((1/p_betcnt::float + 1/p_hitcnt::float + 1/p_incamt::float)) <> 0
  and ((1/t_hitrate + 1/t_incrate)) <> 0
  and ((1/p_betrate + 1/p_hitrate + 1/p_incrate)) <> 0
 ;

select * from st_patternid sp 
where patternid like 'wk%'
order by result_type, bettype, kumiban, harmean desc;


create table tmp_st_patternid as
select * from st_patternid sp;



insert into st_patternid 
select *, (3 / (1/p_betrate + 1/p_hitrate + 1/p_incrate))::numeric(5,2) from tmp_st_patternid where p_betrate > 0 and p_hitrate > 0;

select * from tmp_st_patternid tsp where p_incrate = 0;

select 1/1;

ALTER TABLE st_patternid 
ADD COLUMN harmean double precision;



delete from ml_evaluation where resultno = '272248';


select
	'~' sel,
	(case
		when me2.result_type = '1' then 'ip,G3'
		else 'SG,G1,G2'
	end) grades,
	me2.bettype,
	me2.kumiban,
	me2.resultno,
	me2.modelno,
	me2.patternid,
	me2.pattern,
	(me2.hitamt-me2.betamt) incamt,
	me2.betcnt,
	me2.incomerate::double precision incrate,
	me2.hitrate::double precision,
	me2.bal_pluscnt,
	'x' bonus_pr,
	'x' bonus_bor,
	'x' bonus_bork,
	'x' range_selector,
	'x' bonus_borkbor
from
	ml_evaluation me2,
	(
	select
		pl.result_type,
		pl.bettype,
		pl.kumiban,
		pl.patternid,
		pl.modelno,
		tot.betcnt t_betcnt,
		pl.betcnt p_betcnt,
		(pl.betcnt::float / tot.betcnt::float)::numeric(5,
		2) p_betrate,
		(tot.hitamt - tot.betamt) t_incamt,
		(pl.hitamt - pl.betamt) p_incamt,
		(tot.hitcnt::float / tot.betcnt::float)::numeric(5,
		2) t_hitrate,
		(pl.hitcnt::float / pl.betcnt::float)::numeric(5,
		2) p_hitrate,
		(tot.hitamt::float / tot.betamt::float)::numeric(5,
		2) t_incrate,
		(pl.hitamt::float / pl.betamt::float)::numeric(5,
		2) p_incrate
	from
		(
		select
			result_type,
			bettype,
			kumiban,
			patternid,
			modelno,
			sum(betcnt) betcnt,
			sum(hitcnt) hitcnt,
			sum(betamt) betamt,
			sum(hitamt) hitamt
		from
			ml_evaluation me
		where
			result_type = '1'
			and bettype = '1T'
			and kumiban = '1'
			and incomerate between 1 and 99
		group by
			result_type,
			bettype,
			kumiban,
			patternid,
			modelno
) pl,
		(
		select
			result_type,
			bettype,
			kumiban,
			patternid,
			modelno,
			sum(betcnt) betcnt,
			sum(hitcnt) hitcnt,
			sum(betamt) betamt,
			sum(hitamt) hitamt
		from
			ml_evaluation me
		where
			result_type = '1'
			and bettype = '1T'
			and kumiban = '1'
		group by
			result_type,
			bettype,
			kumiban,
			patternid,
			modelno
) tot
	where
		pl.result_type = tot.result_type
		and pl.bettype = tot.bettype
		and pl.kumiban = tot.kumiban
		and pl.patternid = tot.patternid
		and pl.modelno = tot.modelno
	order by
		x desc
	limit 1
) me3
where
	me2.result_type = me3.result_type
	and me2.bettype = me3.bettype
	and me2.kumiban = me3.kumiban
	and me2.patternid = me3.patternid
	and me2.modelno = me3.modelno
	and me2.incomerate between 1 and 99
;


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
	     row_number() over (partition by result_type, bettype, kumiban order by p_betcnt desc) as ranking,
	     *
	   from st_patternid sp
   ) tmp
   where ranking <= 1
) sp2
where me.result_type = sp2.result_type and me.bettype = sp2.bettype and me.kumiban = sp2.kumiban
  and me.patternid = sp2.patternid and me.modelno = sp2.modelno 
  and me.incomerate between 1 and 99
;


	  
select distinct result_type from st_patternid sp;




select 
from ml_evaluation
where 



copy (
select * from ml_evaluation me 
) to 'D:\Dev\export\20221122\ml_evaluation.tsv' csv delimiter E'\t' header;


select
'~' sel, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades,
me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern,
(me.hitamt-me.betamt) incamt, me.betcnt, me.incomerate::double precision incrate,
me.hitrate::double precision, me.bal_pluscnt,
'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor,
me.pr_bestmin, me.pr_bestmax, mre.lpr_bestmin, mre.lpr_bestmax, mre.rpr_bestmin, mre.rpr_bestmax
from ml_evaluation me, ml_range_evaluation mre
where me.resultno = mre.resultno and me.result_type = mre.result_type and me.bettype = mre.bettype and me.kumiban = mre.kumiban and me.modelno = mre.modelno and me.patternid  = mre.patternid and me.pattern = mre.pattern
and me.bettype = '1T' and me.kumiban = '1'
and me.result_type = '1'
and me.incomerate between 0 and 99
and (me.modelno || '-' || me.patternid) in ('98080-turn+race')
;


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
      and (hitamt - betamt) > 0
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
  and me.modelno::int = 99100
--  and hodds_max < 100
--  and me.bettype = '3T' and me.kumiban = '132'
order by 
  result_type, 
  bettype, 
  kumiban,
  betcnt desc -- 흑자투표수
--  betcnt desc
--p_ptnnumrate desc
-- p_ptnbetcnt desc
--p_incamtrate desc
--incamt_stb desc
-- hitrate desc
) ranked
where 
  ranked.ranking between 1 and 10 -- 상위 10개씩
;




select bal_slope from ml_evaluation where resultno = '683';

-- 着順1,2,3の投票数・的中率調査
select
  'digit3' digit,
  clf.modelno mno,
   prediction3 prediction,
  -- -- mc.class_rank1 classr, 
  mc.algorithm_rank1 algorithm, mc.features_rank1 feature,
  count(1) betcnt, 
  sum(case when substring(sanrentanno from 3 for 1) = prediction3 then 1 else 0 end) hitcnt,
  (sum(case when substring(sanrentanno from 3 for 1) = prediction3 then 1 else 0 end)::float / count(1)::float)::numeric(5,2) hitrate
from rec_race race, ml_classification clf, ml_model_config mc
where race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno
  and clf.modelno = mc.modelno 
  and clf.ymd::int <= 20210601
group by mno, 
   prediction,
  -- -- classr,
  algorithm, feature
order by 
   prediction, 
  hitcnt desc, betcnt desc;


select * from ml_classification where modelno::int = 99052;

select '3T-rank12' kubun,
  substring(sanrentanno from 1 for 2) keta1,
  count(1) cnt,
  avg(sanrentanprize::float / 100::float)::numeric(5,2) avg_hitodds,
  (count(1)::float / 162590::float)::numeric(5,2) hitrate,
  (sum(sanrentanprize)::float / (162590*100)::float)::numeric(5,2) incrate, 
  (sum(sanrentanprize) - (162590*100))::int incamt, 
  (stddev(sanrentanprize)::float / 100::float)::numeric(5,2) stddev_hitodds, 
  ((percentile_disc(0.5) within group (order by sanrentanprize))::float / 100::float)::numeric(5,2) center_hitodds, -- 驩粢
  ((mode() within group (order by sanrentanprize))::float / 100::float)::numeric(5,2) mostlot_hitodds --涅
from rec_race race where sanrentanno <> '不成立' 
  and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601
group by keta1 order by keta1;


select '3F' bettype, sanrenhukuno kumiban, 
  count(1) hitcnt, 
  (count(1)::float / 162590::float)::numeric(5,3) hitrate, 
  (sum(sanrenhukuprize)::float / (162590*100)::float)::numeric(5,2) incrate,
  avg(sanrenhukuprize::float / 100::float)::numeric(5,2) avg_hitodds, 
  (stddev(sanrenhukuprize)::float / 100::float)::numeric(5,2) stddev_hitodds,
  ((percentile_disc(0.5) within group (order by sanrenhukuprize))::float / 100::float)::numeric(5,2) center_hitodds, -- 驩粢
  ((mode() within group (order by sanrenhukuprize))::float / 100::float)::numeric(5,2) mostlot_hitodds, --涅
  (sum(sanrenhukuprize) - (162590*100))::int incamt
from rec_race race
where sanrentanno <> '不成立' and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601
group by bettype, kumiban
order by kumiban;

select ptn, count(*) cnt 
from rec_race race
where sanrentanno <> '不成立' 
  and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601 group by ptn order by ptn;


select modelno, count(*) cnt from ml_classification group by modelno order by modelno; 

select * from ml_classification where modelno = '99049';

select (sort_desc(probabilities1))[6] from ml_classification;

select 
  odds.bettype, odds.kumiban, odds.odds, count(1)
from rec_race race, ml_classification clf, odds_result odds
where race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno and sanrentanno <> '橙瑾悄' 
    and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno 
    and race.tansyono = clf.prediction1 -- 釟驩・・档靼
    and odds.bettype = '1T' and odds.kumiban = clf.prediction1 -- odds_result・ml_classification・JOIN
    and race.ymd::int between 20180601 and 20210601
    and clf.modelno = '99037'
group by odds.bettype, odds.kumiban, odds.odds
order by odds.bettype, odds.kumiban, odds.odds;

--패턴 분포 조사
select modelno, (  prediction1 || prediction2 || prediction3  ) ptn, count(*) cnt 
from rec_race race, ml_classification claf, rec_racer_arr arr
where race.ymd = claf.ymd and race.jyocd = claf.jyocd and race.raceno = claf.raceno
  and race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
  and claf.modelno::int between 99080 and 99080
  and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601
group by modelno, ptn 
order by cnt desc, ptn, modelno;

--패턴 분포 조사  old10
select modelno, (  kumiban1 || kumiban2  || kumiban3   ) ptn, count(*) cnt 
from rec_race race, ml_classification_old claf, rec_racer_arr arr
where race.ymd = claf.ymd and race.jyocd = claf.jyocd and race.raceno = claf.raceno
  and race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
  and claf.modelno::int between 8 and 8
  -- and race.ymd ::int >= 20180601 and race.ymd ::int <= 20210601 
group by modelno, ptn 
order by cnt desc, ptn, modelno;

--패턴 분포조사 옺즈포함
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

select kumiban, min(ymd), max(ymd) from odds_monitor group by kumiban;

select bettype, kumiban, min(ymd), max(ymd) from odds_result group by bettype, kumiban;

select * from odds_monitor where kumiban = '1';

select 'nopattern' pattern, race.ymd, race.jyocd, race.raceno, race.sime, 
oddslist[0] om00,oddslist[1] om01,oddslist[2] om02,oddslist[3] om03,oddslist[4] om04,oddslist[5] om05,oddslist[6] om06,oddslist[7] om07,oddslist[8] om08,oddslist[9] om09,oddslist[10] om10,oddslist[11] om11,oddslist[12] om12,oddslist[13] 
om13,oddslist[14] om14,oddslist[15] om15,oddslist[16] om16,oddslist[17] om17,oddslist[18] om18,oddslist[19] om19,oddslist[20] om20,oddslist[21] om21,oddslist[22] om22,oddslist[23] om23,oddslist[24] om24,oddslist[25] om25,oddslist[26] 
om26,oddslist[27] om27,oddslist[28] om28,oddslist[29] om29,oddslist[30] om30,oddslist[31] om31,oddslist[32] om32,oddslist[33] om33,oddslist[34] om34,oddslist[35] om35,oddslist[36] om36,oddslist[37] om37,oddslist[38] om38,oddslist[39] om39 
from rec_race race, rec_racer_arr arr, odds_monitor odds where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno 
and sanrentanno <> '橙瑾悄' and grade in ('ip', 'G3', 'G2', 'G1', 'SG') and race.ymd >= '20210907' and race.ymd <= '20211224'  and (odds.bettype = '1T' and odds.kumiban = '1') order by pattern, race.ymd, race.sime; 


select * from odds_monitor where ymd = '20210907' and jyocd = '23' and raceno = 1 and bettype = '1T' and kumiban = '1';

select count(*) from ml_classification where resultno::int = 336 and kumiban = '1';

select distinct resultno::int from ml_evaluation order by resultno;

select distinct modelno::int from ml_classification order by modelno ;

select modelno, min(ymd), max(ymd) from ml_classification where modelno::int  in (99063, 99080, 99083, 99086, 99089, 99094, 99097, 99098) group by modelno order by modelno;

select min(ymd), max(ymd), count(1) cnt from odds_monitor where bettype = '1T' and kumiban = '1'; 

select level from rec_racer_arr where jyocd = '14' and raceno = 1 and ymd = '20180601'; 

select count(*) from ml_classification;

select count(*) from ml_evaluation;


select prediction3, count(*) cnt from ml_classification where modelno::int = 99070 group by prediction3;

select * from ml_evaluation where resultno::int >= 70064;

select * from ml_classification where modelno::int = 99063 and ymd::int between 20211225 and 20220122;

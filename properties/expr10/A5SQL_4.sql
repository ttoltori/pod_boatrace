


select create_ss_tables('ss5555_1','stat5555_1');
select create_ss_tables('ss5555_2','stat5555_2');
select create_ss_tables('ss5555_3','stat5555_3');
select create_ss_tables('ss5555_4','stat5555_4');
select create_ss_tables('ss777_1','stat777_1');
select create_ss_tables('ss777_2','stat777_2');
select create_ss_tables('ss777_3','stat777_3');


select 
	 ranksb1, ranksb2,  sbr1.id_modelno, sbr1.id_factor, sbr1.id_patternid, sbr1.bettype, sbr1.id_kumiban, sbr1.i0, sbr1.i02, sbr1.i04, sbr1.i09, sbr1.income, sbr1.bc02, sbr1.bc04
	from 
    (
	  	select
		  ranksb1, ranksb2, kumiban, i041, i042, keycol
		from (
		  select 
		    --( row_number() over (partition by sb1.id_grade, sb1.id_kumiban order by (sb1.i0+sb1.i02+sb1.i04+sb1.i09) desc) ) as ranksb1,
		    --( row_number() over (partition by sb2.id_grade, sb2.id_kumiban order by (sb2.i0+sb2.i02+sb2.i04+sb2.i09) desc) ) as ranksb2,
		    ( row_number() over (partition by sb1.id_grade, sb1.id_kumiban order by (sb1.i02) desc) ) as ranksb1,
		    ( row_number() over (partition by sb2.id_grade, sb2.id_kumiban order by (sb2.i02) desc) ) as ranksb2,
		    sb1.i04 i041, sb2.i04 i042,
		    sb2.*
		  from 
		    (select sb.*, (se.hitamt-se.betamt) income from sim_bork sb, sim_eval se where sb.keycol = se.keycol and sb.id_term = '1' and se.id_term = '1' 
		      -- and sb.id_grade = 'ip' and sb.id_bettype in ('3T', '3R', '3P') 
		      and sb.id_grade = 'ip' and sb.id_bettype in ('3T') 
		      --and se.incomerate > 1.8) sb1,
		      and i0 > 0 and i02 > 0 and i04 > 0 and i09 > 0
		      -- and ci02 > 0 and ci04 > 0 and ci09 > 0 and se.incomerate > 1
		      ) sb1,
		    (select sb.*, (se.hitamt-se.betamt) income from sim_bork sb, sim_eval se where sb.keycol = se.keycol and sb.id_term = '2' and se.id_term = '2' 
		      -- and sb.id_grade = 'ip' and sb.id_bettype in ('3T', '3R', '3P')
		      and sb.id_grade = 'ip' and sb.id_bettype in ('3T') 
		      --and se.incomerate > 1.8) sb2
		      and i0 > 0 and i02 > 0 and i04 > 0 and i09 > 0
		      -- and ci02 > 0 and ci04 > 0 and ci09 > 0 and se.incomerate > 1
		      ) sb2
		  where sb1.keycol = sb2.keycol
		) tr1
		where ranksb1 <= 100 and ranksb2 <= 100
	) sb12,
  	( select (se.hitamt-se.betamt) income, sb.*  from sim_bork sb, sim_eval se where sb.keycol = se.keycol and sb.id_term = 'R1' and se.id_term = 'R1' 
	      and sb.id_grade = 'ip' and sb.id_bettype in ('3T', '3R', '3P')
	) sbr1
	where sb12.keycol = sbr1.keycol
	  -- and sbr1.i04 > 0 and sbr1.i02 > 0 and sbr1.i09 > 0
	order by sbr1.id_kumiban, sbr1.i0 desc
  ;




  
select
    ( row_number() over (partition by sb2.id_grade, sb2.id_kumiban order by (sb1.ci04 + sb2.ci04) desc) ) as rank1, sb2.*
  from 
  (	select
	  *
	from (
	  select 
	    ( row_number() over (partition by sb2.id_grade, sb2.id_kumiban order by (sb1.ci04 + sb2.ci04) desc) ) as rank1,
	    ( row_number() over (partition by sb2.id_grade, sb2.id_kumiban order by abs(sb1.ci04 - sb2.ci04) ) ) as rank2,
	    sb2.*
	  from 
	    (select sb.* from sim_bork sb, sim_eval se where sb.keycol = se.keycol and sb.id_term = '1' and se.id_term = '1' 
	      and sb.id_grade = 'ip' and sb.id_bettype in ('3T', '3R', '3P') 
	      --and se.incomerate > 1.8) sb1,
	      and i0 > 0 and i02 > 0 and i04 > 0 and i09 > 0) sb1,
	    (select sb.* from sim_bork sb, sim_eval se where sb.keycol = se.keycol and sb.id_term = '2' and se.id_term = '2' 
	      and sb.id_grade = 'ip' and sb.id_bettype in ('3T', '3R', '3P') 
	      --and se.incomerate > 1.8) sb2
	      and i0 > 0 and i02 > 0 and i04 > 0 and i09 > 0) sb2,
	  where sb1.keycol = sb2.keycol
	) tr1
	where rank1 <= 10
  ) sb12,
  (select (se.hitamt-se.betamt) income, sb.*  from sim_bork sb, sim_eval se where sb.keycol = se.keycol and sb.id_term = 'R1' and se.id_term = 'R1' 
      and sb.id_grade = 'ip' and sb.id_bettype in ('3T', '3R', '3P')
  ) sbr1
  where sb12.keycol = sbr1.keycol
--  group by rank1
  order by rank1
;

  select
    sb12.rank1, sb12.rank2, sbr1.*
  from 
  (
    select
     *
    from (
	  select
	    ( row_number() over (partition by id_grade, id_kumiban order by rank2factor ) ) as rank2,
	    *
	  from 
	  (	select
		  *
		from (
		  select 
		    ( row_number() over (partition by sb2.id_grade, sb2.id_kumiban order by (sb1.i09 + sb2.i09) desc) ) as rank1,
		    abs(sb1.i09 - sb2.i09) rank2factor,
		    sb2.*
		  from 
		    (select sb.* from sim_bork sb, sim_eval se where sb.keycol = se.keycol and sb.id_term = '1' and se.id_term = '1' 
		      and sb.id_grade = 'ip' and sb.id_bettype in ('3T', '3R', '3P') 
		      --and se.incomerate > 1.8) sb1,
		      and sb.i0 > 0) sb1,
		    (select sb.* from sim_bork sb, sim_eval se where sb.keycol = se.keycol and sb.id_term = '2' and se.id_term = '2' 
		      and sb.id_grade = 'ip' and sb.id_bettype in ('3T', '3R', '3P') 
		      --and se.incomerate > 1.8) sb2
		      and sb.i0 > 0) sb2
		  where sb1.keycol = sb2.keycol
		) tr1
		where rank1 <= 10
	  ) tr2
    ) tr3
    where rank2 <= 3
  ) sb12,
  (select (se.hitamt-se.betamt) income, sb.*  from sim_bork sb, sim_eval se where sb.keycol = se.keycol and sb.id_term = 'R1' and se.id_term = 'R1' 
      and sb.id_grade = 'ip' and sb.id_bettype in ('3T', '3R', '3P')
  ) sbr1
  where sb12.keycol = sbr1.keycol
  order by sb12.kumiban, sb12.rank2
;
  
  
    select
      rank2, sb3.id_kumiban, sb3.i0, sb3.i1, sb3.i2, sb3.i3, sb3.i4, sb3.i02, sb3.i04
      -- sum(sb3.i0) si0, sum(sb3.i1) si1, sum(sb3.i2) si2, sum(sb3.i3) si3, sum(sb3.i4) si4, sum(sb3.i02) si02, sum(sb3.i04) si04
    from (
		select
		  *
	    from 
	    (
		    select
		      (row_number() over (partition by sb2.id_grade, sb2.id_kumiban order by sb2.i02 desc)) as rank2, sb2.*
		    from
			( select * 
			  from 
			    ( select (row_number() over (partition by id_grade, id_kumiban order by i02 desc)) as rank1, *
			      from sim_bork where id_term = '1' and id_bettype in ('3T', '3R', '3P') and id_grade = 'ip'
			    ) tr1
			  where rank1 <= 20
			) sb1,
			( select * from sim_bork where id_term = '2' and id_bettype in ('3T', '3R', '3P') and id_grade = 'ip') sb2
			where sb1.keycol = sb2.keycol
	    ) tr2
	    where rank2 <= 1
	) sb12,
	( select * from sim_bork where id_term = 'R1' and id_bettype in ('3T', '3R', '3P') and id_grade = 'ip') sb3
	where sb12.keycol = sb3.keycol
--	group by sb12.rank2, sb3.id_kumiban
--	order by sb3.id_kumiban
	;
	
select
  rank1, sb2.id_kumiban, sb2.i0, sb2.i1, sb2.i2, sb2.i3, sb2.i4, sb2.i02, sb2.i04, sb1.i04
from
( select * 
  from 
    ( select (row_number() over (partition by id_grade, id_kumiban order by i04 desc)) as rank1, *
      from sim_bork where id_term = '2' and id_bettype in ('3T', '3R', '3P') and id_grade = 'ip'
    ) tr1
  where rank1 <= 10
) sb1,
( select * from sim_bork where id_term = 'R1' and id_bettype in ('3T', '3R', '3P') and id_grade = 'ip') sb2
where sb1.keycol = sb2.keycol
order by rank1, sb2.id_kumiban
;


select 
  id_modelno, id_patternid, id_bettype, id_kumiban, i0, i1, i2, i3, i4, i02, i04, bc02, bc04, tc02, tc04, tpc02, tpc04, (i1+i2+i3+i4) i14
--  sum(i0), sum(i1), sum(i2), sum(i3), sum(i4)
--  id_modelno, sum(i04) sum_i04, sum(bc04) sum_bc0
from (
	select
	  (row_number() over (partition by sb1.id_grade, sb1.id_kumiban order by sbr1.i04 desc)) as ranking,
	  sbr2.*
	from
--	  (select * from sim_bork where id_term = '1' and (tpc04::float/(case when tc04=0 then 1 else tc04 end)::float) >= 0.6 ) sb1,
--	  (select * from sim_bork where id_term = '2' and (tpc04::float/(case when tc04=0 then 1 else tc04 end)::float) >= 0.6 ) sb2,
	  (select * from sim_bork where id_term = '1' and i04 > 100) sb1,
	  (select * from sim_bork where id_term = '2' and i04 > 100) sb2,
	  (select * from sim_bork where id_term = 'R1') sbr1,
	  (select * from sim_bork where id_term = 'R2') sbr2
	where sb1.id_grade = sb2.id_grade and sb1.id_bettype = sb2.id_bettype and sb1.id_kumiban = sb2.id_kumiban and sb1.id_patternid = sb2.id_patternid 
	  and sb1.id_factor = sb2.id_factor and sb1.id_factormin = sb2.id_factormin and sb1.id_modelno = sb2.id_modelno and sb1.id_sqlid = sb2.id_sqlid 
	  and sb1.id_termtype = sb2.id_termtype 
	  and sb1.id_grade = sbr1.id_grade and sb1.id_bettype = sbr1.id_bettype and sb1.id_kumiban = sbr1.id_kumiban and sb1.id_patternid = sbr1.id_patternid 
	  and sb1.id_factor = sbr1.id_factor and sb1.id_factormin = sbr1.id_factormin and sb1.id_modelno = sbr1.id_modelno and sb1.id_sqlid = sbr1.id_sqlid 
	  and sb1.id_termtype = sbr1.id_termtype
	  and sb1.id_grade = sbr2.id_grade and sb1.id_bettype = sbr2.id_bettype and sb1.id_kumiban = sbr2.id_kumiban and sb1.id_patternid = sbr2.id_patternid 
	  and sb1.id_factor = sbr2.id_factor and sb1.id_factormin = sbr2.id_factormin and sb1.id_modelno = sbr2.id_modelno and sb1.id_sqlid = sbr2.id_sqlid 
	  and sb1.id_termtype = sbr2.id_termtype
) ranked
where id_bettype in ('3T', '3R', '3P') and id_grade = 'ip'
  and ranking = 1
--group by id_modelno
--order by sum_i04 desc
;

select 
  id_modelno, id_patternid, id_bettype, id_kumiban, i0, i1, i2, i3, i4, i02, i04, bc02, bc04, tc02, tc04, tpc02, tpc04, (i1+i2+i3+i4) i14
--  sum(i0), sum(i1), sum(i2), sum(i3), sum(i4)
--  id_modelno, sum(i04) sum_i04, sum(bc04) sum_bc0
from (
	select
	  (row_number() over (partition by sb1.id_grade, sb1.id_kumiban order by sbr1.i04 desc)) as ranking,
	  sbr2.*
	from
--	  (select * from sim_bork where id_term = '1' and (tpc04::float/(case when tc04=0 then 1 else tc04 end)::float) >= 0.6 ) sb1,
--	  (select * from sim_bork where id_term = '2' and (tpc04::float/(case when tc04=0 then 1 else tc04 end)::float) >= 0.6 ) sb2,
	  (select * from sim_bork where id_term = '1' and i04 > 100) sb1,
	  (select * from sim_bork where id_term = '2' and i04 > 100) sb2,
	  (select * from sim_bork where id_term = 'R1') sbr1,
	  (select * from sim_bork where id_term = 'R2') sbr2
	where sb1.id_grade = sb2.id_grade and sb1.id_bettype = sb2.id_bettype and sb1.id_kumiban = sb2.id_kumiban and sb1.id_patternid = sb2.id_patternid 
	  and sb1.id_factor = sb2.id_factor and sb1.id_factormin = sb2.id_factormin and sb1.id_modelno = sb2.id_modelno and sb1.id_sqlid = sb2.id_sqlid 
	  and sb1.id_termtype = sb2.id_termtype 
	  and sb1.id_grade = sbr1.id_grade and sb1.id_bettype = sbr1.id_bettype and sb1.id_kumiban = sbr1.id_kumiban and sb1.id_patternid = sbr1.id_patternid 
	  and sb1.id_factor = sbr1.id_factor and sb1.id_factormin = sbr1.id_factormin and sb1.id_modelno = sbr1.id_modelno and sb1.id_sqlid = sbr1.id_sqlid 
	  and sb1.id_termtype = sbr1.id_termtype
	  and sb1.id_grade = sbr2.id_grade and sb1.id_bettype = sbr2.id_bettype and sb1.id_kumiban = sbr2.id_kumiban and sb1.id_patternid = sbr2.id_patternid 
	  and sb1.id_factor = sbr2.id_factor and sb1.id_factormin = sbr2.id_factormin and sb1.id_modelno = sbr2.id_modelno and sb1.id_sqlid = sbr2.id_sqlid 
	  and sb1.id_termtype = sbr2.id_termtype
) ranked
where id_bettype in ('3T', '3R', '3P') and id_grade = 'ip'
  and ranking = 1
--group by id_modelno
--order by sum_i04 desc
;

-- 0.308678351	0.159390049	0.11272012	0.089760271	0.074455347	0.064765747	0.055554959	0.049739385	0.044406348	0.040529423
drop table sim_bork;
-- ip_1T_1_prob1+wk123_i02-100_~1_11609_JSJ-S2_77_12_10163_104_7.37_11.2
create table sim_bork as
--insert into sim_bork
--select * from
--(
select 
  (ids[1] || '_' || ids[2] || '_' || kumiban || '_' || ids[4] || '_' || ids[5] || '_' || ids[6] || '_' || ids[7] || '_' || ids[8] || '_' || ids[9]) keycol,
  cast(ids[1] as varchar(2)) id_grade , cast(ids[2] as varchar(2)) id_bettype, 
  cast(kumiban as varchar(3)) id_kumiban, cast(ids[4] as varchar(20)) id_patternid, 
  cast(ids[5] as varchar(20)) id_factor, cast(ids[6] as varchar(20)) id_factormin, 
  cast(ids[7] as varchar(5)) id_modelno, cast(ids[8] as varchar(20)) id_sqlid, 
  cast(ids[9] as varchar(5)) id_termtype, cast(ids[10] as varchar(5)) id_term, 
  cast(ids[11] as varchar(6)) id_resultno,
  betcnt[0] bc0, betcnt[1] bc1, betcnt[2] bc2, betcnt[3] bc3, betcnt[4] bc4, (betcnt[0] + betcnt[1] + betcnt[2]) bc02, (betcnt[0] + betcnt[1] + betcnt[2] + betcnt[3] + betcnt[4]) bc04,
  term_cnt[0] tc0, term_cnt[1] tc1, term_cnt[2] tc2, term_cnt[3] tc3, term_cnt[4] tc4, (term_cnt[0] + term_cnt[1] + term_cnt[2]) tc02, (term_cnt[0] + term_cnt[1] + term_cnt[2] + term_cnt[3] + term_cnt[4]) tc04,
  term_pluscnt[0] tpc0, term_pluscnt[1] tpc1, term_pluscnt[2] tpc2, term_pluscnt[3] tpc3, term_pluscnt[4] tpc4, (term_pluscnt[0] + term_pluscnt[1] + term_pluscnt[2]) tpc02, (term_pluscnt[0] + term_pluscnt[1] + term_pluscnt[2] + term_pluscnt[3] + term_pluscnt[4]) tpc04,
  incamt[0] i0, incamt[1] i1, incamt[2] i2, incamt[3] i3, incamt[4] i4, 
  (incamt[0] + incamt[1] + incamt[2]) i02, 
  (incamt[0] + incamt[1] + incamt[2] + incamt[3] + incamt[4]) i04, 
  (incamt[0] + incamt[1] + incamt[2] + incamt[3] + incamt[4] + incamt[5] + incamt[6] + incamt[7] + incamt[8] + incamt[9]) i09,
  ( (incamt[0]*0.3) + (incamt[1]*0.15) + (incamt[2]*0.11) )::int ci02,
  ( (incamt[0]* 0.3) + (incamt[1]*0.15) + (incamt[2]*0.11) + (incamt[3]*0.08) + (incamt[4]*0.07) )::int ci04, 
  ( (incamt[0]* 0.3) + (incamt[1]*0.15) + (incamt[2]*0.11) + (incamt[3]*0.08) + (incamt[4]*0.07) +
    (incamt[5]* 0.06) + (incamt[6]*0.05) + (incamt[7]*0.04) + (incamt[8]*0.04) + (incamt[9]*0.04)
  )::int ci09, 
  *  
from 
(
  select  string_to_array(evaluations_id, '_') ids, 
  * from ml_bork_evaluation me where result_type = '4'
) tmp
--) tmp2
--where id_term = 'R2' 
;

drop index if exists indexes_grade_bork;
drop index if exists indexes_factor_bork;
drop index if exists indexes_term_bork;
drop index if exists indexes_bettype_bork;
drop index if exists indexes_keycol_bork;

create index indexes_grade_bork on sim_bork (id_grade);
create index indexes_factor_bork on sim_bork (id_factor);
create index indexes_term_bork on sim_bork (id_term);
create index indexes_bettype_bork on sim_bork (id_bettype);
create index indexes_keycol_bork on sim_bork (keycol);

drop table sim_eval;
create table sim_eval as 
select
  (ids[1] || '_' || ids[2] || '_' || kumiban || '_' || ids[4] || '_' || ids[5] || '_' || ids[6] || '_' || ids[7] || '_' || ids[8] || '_' || ids[9]) keycol,
  cast(ids[1] as varchar(2)) id_grade , cast(ids[2] as varchar(2)) id_bettype, 
  cast(kumiban as varchar(3)) id_kumiban, cast(ids[4] as varchar(20)) id_patternid, 
  cast(ids[5] as varchar(20)) id_factor, cast(ids[6] as varchar(20)) id_factormin, 
  cast(ids[7] as varchar(5)) id_modelno, cast(ids[8] as varchar(20)) id_sqlid, 
  cast(ids[9] as varchar(5)) id_termtype, cast(ids[10] as varchar(5)) id_term, 
  cast(ids[11] as varchar(6)) id_resultno,
  betcnt, hitcnt, betamt, hitamt, betrate, hitrate, incomerate
from 
(
  select  string_to_array(evaluations_id, '_') ids, 
  * from ml_evaluation me where result_type = '4'
)tmp;

drop index if exists indexes_grade_eval;
drop index if exists indexes_factor_eval;
drop index if exists indexes_term_eval;
drop index if exists indexes_bettype_eval;
drop index if exists indexes_keycol_eval;

create index indexes_grade_eval on sim_bork (id_grade);
create index indexes_factor_eval on sim_bork (id_factor);
create index indexes_term_eval on sim_bork (id_term);
create index indexes_bettype_eval on sim_bork (id_bettype);
create index indexes_keycol_eval on sim_bork (keycol);

select count(1) from sim_eval;

select 
  sum(betcnt[0]), sum(betcnt[1]),sum(betcnt[2]),sum(betcnt[3]),sum(betcnt[4]),sum(betcnt[5]),sum(betcnt[6]),sum(betcnt[7]),sum(betcnt[8]),sum(betcnt[9])
from ml_bork_evaluation me where result_type = '4';

select distinct evaluations_id from stat_bork sb ;
delete from stat_bork where evaluations_id in ('66_3', '66_4');

truncate stat_bork;
insert into stat_bork
  select 
    me.result_type, me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
    bk.incamt[0] i0, bk.incamt[1] i1, bk.incamt[2] i2, bk.incamt[3] i3, bk.incamt[4] i4, 
    bk.incamt[5] i5, bk.incamt[6] i6, bk.incamt[7] i7, bk.incamt[8] i8, bk.incamt[9] i9, 
    bk.betcnt[0] bc0, bk.betcnt[1] bc1, bk.betcnt[2] bc2, bk.betcnt[3] bc3, bk.betcnt[4] bc4, 
    bk.betcnt[5] bc5, bk.betcnt[6] bc6, bk.betcnt[7] bc7, bk.betcnt[8] bc8, bk.betcnt[9] bc9, 
    bk.hitcnt[0] hc0, bk.hitcnt[1] hc1, bk.hitcnt[2] hc2, bk.hitcnt[3] hc3, bk.hitcnt[4] hc4, 
    bk.hitcnt[5] hc5, bk.hitcnt[6] hc6, bk.hitcnt[7] hc7, bk.hitcnt[8] hc8, bk.hitcnt[9] hc9, 
--    bk.term_cnt[0] tc0, bk.term_cnt[1] tc1, bk.term_cnt[2] tc2, bk.term_cnt[3] tc3, bk.term_cnt[4] tc4, 
--    bk.term_cnt[5] tc5, bk.term_cnt[6] tc6, bk.term_cnt[7] tc7, bk.term_cnt[8] tc8, bk.term_cnt[9] tc9, 
--    bk.term_pluscnt[0] tpc0, bk.term_pluscnt[1] tpc1, bk.term_pluscnt[2] tpc2, bk.term_pluscnt[3] tpc3, bk.term_pluscnt[4] tpc4, 
--    bk.term_pluscnt[5] tpc5, bk.term_pluscnt[6] tpc6, bk.term_pluscnt[7] tpc7, bk.term_pluscnt[8] tpc8, bk.term_pluscnt[9] tpc9, 
    me.betcnt, me.hitcnt, me.betamt, me.hitamt, (me.hitamt - me.betamt) incamt, me.betrate, me.hitrate, me.incomerate, me.bal_pluscnt, me.evaluations_id
  from ml_evaluation me, ml_bork_evaluation bk
  where  
    me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern and me.evaluations_id = bk.evaluations_id
    and me.evaluations_id in ('66_3', '66_4')
    -- and me.result_type in ('1', '11')
    -- and me.bettype in ('3T', '3R', '3P', '2N', '3X')
    --and me.resultno::int between 425 and 568
;

select distinct evaluations_id from ml_bork_evaluation_result ;

---------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------
copy (
(select
  '5', id_grade, id_bettype, id_kumiban, id_patternid, '66_1', '66_2', id_factor, id_factormin, id_modelno, 'STA-S1-test', incamt
from
  (
    select 
      (row_number() over (partition by sb.id_grade, sb.id_kumiban order by i04 desc)) as ranking,
      *
	from
	  ( select 
		  sbr.*
		from  
	 	  (select * from sim_bork where id_term = '1' and (i04 > 0)) sb1,
	 	  (select * from sim_bork where id_term = '2' and (i04 > 0)) sb2,
		  (select * from sim_bork where id_term = 'R1') sbr
		where sb1.id_grade = sb2.id_grade and sb1.id_bettype = sb2.id_bettype and sb1.id_kumiban = sb2.id_kumiban and sb1.id_patternid = sb2.id_patternid 
		  and sb1.id_factor = sb2.id_factor and sb1.id_factormin = sb2.id_factormin and sb1.id_modelno = sb2.id_modelno and sb1.id_sqlid = sb2.id_sqlid 
		  and sb1.id_termtype = sb2.id_termtype and sb1.id_resultno = sb2.id_resultno 
		  and sb1.id_grade = sbr.id_grade and sb1.id_bettype = sbr.id_bettype and sb1.id_kumiban = sbr.id_kumiban and sb1.id_patternid = sbr.id_patternid 
		  and sb1.id_factor = sbr.id_factor and sb1.id_factormin = sbr.id_factormin and sb1.id_modelno = sbr.id_modelno and sb1.id_sqlid = sbr.id_sqlid 
		  and sb1.id_termtype = sbr.id_termtype and sb1.id_resultno = sbr.id_resultno
	  ) sb
	where 
	  --sb.id_bettype = '1T'
	  -- sb.id_bettype in ('2N', '3X')
	  sb.id_bettype in ('3T', '3R', '3P')
	  and tc0 > 0 and (tpc04::float / tc04::float) >= 0.4
  ) tmp
where ranking <= 1
order by id_grade desc, id_kumiban, ranking
)
) to 'D:\Dev\experiment\expr10\simulation_step2\top1_i04_2T3R3P.tsv' csv delimiter E'\t';
;


select
  '5', id_grade, id_bettype, id_kumiban, id_patternid, '66_1', '66_2', id_factor, id_factormin, id_modelno, 'STA-S1-test', incamt
from
  (
    select 
      (row_number() over (partition by sb.id_grade, sb.id_kumiban order by i04 desc)) as ranking,
      *
	from
	  ( select 
		  sbr.*
		from  
	 	  (select * from sim_bork where id_term = '1' and (i04 > 0)) sb1,
	 	  (select * from sim_bork where id_term = '2' and (i04 > 0)) sb2,
		  (select * from sim_bork where id_term = 'R1') sbr
		where sb1.id_grade = sb2.id_grade and sb1.id_bettype = sb2.id_bettype and sb1.id_kumiban = sb2.id_kumiban and sb1.id_patternid = sb2.id_patternid 
		  and sb1.id_factor = sb2.id_factor and sb1.id_factormin = sb2.id_factormin and sb1.id_modelno = sb2.id_modelno and sb1.id_sqlid = sb2.id_sqlid 
		  and sb1.id_termtype = sb2.id_termtype and sb1.id_resultno = sb2.id_resultno 
		  and sb1.id_grade = sbr.id_grade and sb1.id_bettype = sbr.id_bettype and sb1.id_kumiban = sbr.id_kumiban and sb1.id_patternid = sbr.id_patternid 
		  and sb1.id_factor = sbr.id_factor and sb1.id_factormin = sbr.id_factormin and sb1.id_modelno = sbr.id_modelno and sb1.id_sqlid = sbr.id_sqlid 
		  and sb1.id_termtype = sbr.id_termtype and sb1.id_resultno = sbr.id_resultno
	  ) sb
	where 
	  --sb.id_bettype = '1T'
	  -- sb.id_bettype in ('2N', '3X')
	  sb.id_bettype in ('3T', '3R', '3P')
	  and tc0 > 0 and (tpc04::float / tc04::float) >= 0.4
  ) tmp
where ranking <= 1
order by id_grade desc, id_kumiban, ranking
;


select
  '4', id_grade, id_bettype, id_kumiban, id_patternid, '66_1', '66_2', id_factor, id_factormin, id_modelno, 'STA-S1-test', i0
from
  (
    select 
      row_number() over (partition by sb.id_grade, sb.id_kumiban order by ( i0 ) desc) as ranking,
      *
	from
	  ( select 
		  sbr.*
		from  
	 	  (select * from sim_bork where id_factor = 'i0' and id_term = '1' and (i0 > 0)) sb1,
	 	  (select * from sim_bork where id_factor = 'i0' and id_term = '2' and (i0 > 0)) sb2,
		  (select * from sim_bork where id_factor = 'i0' and  id_term = 'R1') sbr1,
		  (select * from sim_bork where id_factor = 'i0' and  id_term = 'R2') sbr2,
		where sb1.id_grade = sb2.id_grade and sb1.id_bettype = sb2.id_bettype and sb1.id_kumiban = sb2.id_kumiban and sb1.id_patternid = sb2.id_patternid 
		  and sb1.id_factor = sb2.id_factor and sb1.id_factormin = sb2.id_factormin and sb1.id_modelno = sb2.id_modelno and sb1.id_sqlid = sb2.id_sqlid 
		  and sb1.id_termtype = sb2.id_termtype and sb1.id_resultno = sb2.id_resultno 
		  and sb1.id_grade = sbr.id_grade and sb1.id_bettype = sbr.id_bettype and sb1.id_kumiban = sbr.id_kumiban and sb1.id_patternid = sbr.id_patternid 
		  and sb1.id_factor = sbr.id_factor and sb1.id_factormin = sbr.id_factormin and sb1.id_modelno = sbr.id_modelno and sb1.id_sqlid = sbr.id_sqlid 
		  and sb1.id_termtype = sbr.id_termtype and sb1.id_resultno = sbr.id_resultno
	  ) sb
	where 
	  --sb.id_bettype = '1T'
	  -- sb.id_bettype in ('2N', '3X')
	  sb.id_bettype in ('3T', '3R', '3P')
  ) tmp
where ranking <= 1
order by id_grade desc, id_kumiban, ranking
;



select 
  count(1)
from ml_bork_evaluation mbe
--where resultno::int between 10001 and 10448
--where resultno::int between 10449 and 10896
where resultno::int between 10897 and 11344
;


copy (
select 
 *
from
  (	select 
	  -- row_number() over (partition by sb.id_grade, sb.id_bettype, sb.id_kumiban order by (sb.incamt[0] + sb.incamt[1] + sb.incamt[2]) desc) as ranking,
	  sb.id_bettype, sb.id_kumiban, sb.id_factor, sb.id_factormin, sb.id_grade, sb.id_patternid, sb.id_modelno, 
      row_number() over (partition by sb.id_grade, sb.id_bettype, sb.id_kumiban order by ( sb.incamt02+sb.incamt04 ) desc) as rank_sum,
	  row_number() over (partition by sb.id_grade, sb.id_bettype, sb.id_kumiban order by (sb.incamt02) desc) as rank_incamt02,
	  row_number() over (partition by sb.id_grade, sb.id_bettype, sb.id_kumiban order by (sb.incamt04) desc) as rank_incamt04,
      ( sb.incamt02+sb.incamt04 ) incamt0204,
      (sb.incamt02) incamt02,
      (sb.incamt04) incamt04,
      (se.hitamt-se.betamt) incamtse,
      (sb.hitcnt02::float / sb.betcnt02::float)::numeric(5,2) hitrate02,
      (sb.hitcnt04::float / sb.betcnt04::float)::numeric(5,2) hitrate04,
      (se.hitrate) hitratese,
      (sb.hitamt02::float / (sb.betcnt02*100)::float)::numeric(5,2) incrate02,
      (sb.hitamt04::float / (sb.betcnt04*100)::float)::numeric(5,2) incrate04,
      (se.incomerate) incratese,
      ((sb.betcnt02)::float/210)::numeric(5,2) daily_betcnt02,
      ((sb.betcnt04)::float/210)::numeric(5,2) daily_betcnt04,
	  ( se.id_grade || '_' || se.id_bettype || '_' || se.id_kumiban || '_' || se.id_patternid || '_' || se.id_factor || '_' || se.id_factormin || '_' || 
      se.id_modelno || '_' || se.id_sqlid || '_' || se.id_termtype || '_' || se.id_term || '_' || se.id_resultno  || '_' || se.daily_incamt  || '_' || se.daily_hitrate || '_' || se.daily_betcnt) eval_id
	from sim_eval se, 
	  ( select 
		  sb2.*
		from  
		  --(select * from sim_bork where id_term = '12' and (incamt02 > 0 or incamt04 > 0)) sb1,
  		 (select * from sim_bork where id_term = '12' ) sb1,
		  (select * from sim_bork where id_term = 'R'  and (incamt02 > 0 or incamt04 > 0)) sb2
		where sb1.id_grade = sb2.id_grade and sb1.id_bettype = sb2.id_bettype and sb1.id_kumiban = sb2.id_kumiban and sb1.id_patternid = sb2.id_patternid 
		  and sb1.id_factor = sb2.id_factor and sb1.id_factormin = sb2.id_factormin and sb1.id_modelno = sb2.id_modelno and sb1.id_sqlid = sb2.id_sqlid 
		  and sb1.id_termtype = sb2.id_termtype and sb1.id_resultno = sb2.id_resultno 
	  ) sb
	where se.id_grade = sb.id_grade and se.id_bettype = sb.id_bettype and se.id_kumiban = sb.id_kumiban and se.id_patternid = sb.id_patternid 
	  and se.id_factor = sb.id_factor and se.id_factormin = sb.id_factormin and se.id_modelno = sb.id_modelno and se.id_sqlid = sb.id_sqlid 
	  and se.id_termtype = sb.id_termtype and se.id_term = sb.id_term and se.id_resultno = sb.id_resultno 
	  and sb.betcnt02 > 0 and sb.betcnt04 > 0
  ) tmo
where rank_sum <= 10
  -- and id_grade = 'ip' and id_kumiban = '123'
-- order by id_grade, id_bettype, id_kumiban, rank_incamt02, rank_incamt04
order by id_grade, id_bettype, id_kumiban, rank_sum
) to 'D:\Dev\experiment\expr10\simulation_step1\simul_ZIU\top_10.tsv' csv delimiter E'\t' header;
;


-- 77_12, 77_R占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁쉩�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛�뜝�럥�럵�뜝�럥���뜝�럩�뤈�뜝�럩援뀐옙�쐻占쎈윞占쎈젇占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢�뵯占쎈탿�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥궘占쏙옙�걠占쎌졎占쎈빝沃섓옙占쎌굲�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀯옙�빋占쎈뼔夷뚳옙�쇊占쎈늉筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀧솾�꺂�뒩�뜮戮녹삕�얠렱�쐻占쎈윪�뤃�먯삕占쎌맶�뜝�럥�쑅�뜝�럥萸쏙옙�쐻占쎈윪占쎌맚占쎈쐻占쎈윪占쎈츧占쎈쐻占쎈짗占쎌굲0,1,2占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁쉩�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛嶺뚯옓�렰占쎌맶�뜝�럥堉℡뜝�럡��嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌띿뜴�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗�뜝�럩�쟼占쎈쇊占쎈걦�뵳�겭�쐻占쎈윪甕곗쉩�쐻占쎈윞�굜�옚�뵾占쎌뒗占쎄콪鸚룹꼪�쐻占쎈윪筌뤴뼹�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎈ぇ占쎈쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐앾옙�쐻占쎈윥鸚룐몾履쏉옙援쏉옙節륁삕占쎈뿰�삕占쎌맶�뜝�럥�쐾�뜝�럥鍮잌뜝�럥�맶�뜝�럥�쑅�뜏類ㅻ즸占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗�뜝�럥�맶�뜝�럥�쑅�뜝�럥堉잞옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅鶯ㅼ렮踰귨옙�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥吏쀥뜝�럩援꿨뜝�럥�맶�뜝�럥�쑋�뜝�럥�렊�뜝�럥�럸�뜝�럥利드뜝�럩援��뜝�럥�솗�뜝�럥�졒�뜝�럩留띰옙�쐻占쎈윥占쎌맽占쎈쐻占쎈윥占쎌쟽占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛�뜝�럥�럯占쎈쐻占쎈윥占쎄틯�뜝�럥�맶�뜝�럥�쑋占쎈쨨占쎈Ŋ�굲�뜝�럩留띰옙�쐻占쎈윥占쎌몝癲ル슢�뵯占쎈탿�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쐾占쎄턀占쎄뎀占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝占쎈쐻占쎈윞占쎄섐占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빆�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗輿삳뿫遊얍뜝�룞�삕�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀧솾�꺂�뒩占쎈뤅占쎈쐻占쎈뼢占쎄땀筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐앾옙�쐻占쎈윥鸚룐몾履쏉옙寃�泳�占썲뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀯옙�빋占쎈뼔筌앹뼇�븶占쎄덩占쎌굲�뜝�럡�렊�뜝�럥�맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎄콪占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢�뵯占쎈탿�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇鍮�占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗�땱�떜利얕린洹⑥삕占쎈㎍占쎈쐻占쎈윥占쎌몗�뜝�럩逾쀥뜝�럥�맶�뜝�럥�쐾�뜝�럥�젃�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앭뜝�럥�맶�뜝�럥�쑋�뜝�럥援곤옙�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�㏃녇占쎄틓占쎈뮝占쎈쑏筌믩끃�굲占쎌젂影�瑜곸굲占쎈쑏占쎈꺽占쎌맶�뜝�럥吏쀥뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅嶺뚯쉸占싸살맶�뜝�럥�쑋占쎈쨨占쎈Ŋ�굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐪筌먲퐦�삕�솒占쏙옙援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐앾옙�쐻占쎈윥鸚룐벂�쐻占쎈윥�씙�뜝�럡�렊�뜝�럥�맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌띿뜴�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪占쎌쓦�뜝�럥�맶�뜝�럥�쐾�뜝�럥占쏙옙占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥筌욎�λ쐻占쎈윪�뤃�먯삕占쎌맶�뜝�럥�쐾占쎄턀占쎈쳟占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗癲ル슢�뒦占쎄땀�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐앾옙�쐻占쎈윥鸚룐몾履쏉옙寃�泳�占썲뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇鍮놅옙�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀧솾�꺂�돲占쎌돵占쎈뮡�뜝�럡�렊�뜝�럥�맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빆�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝占쎈쐻占쎈윪獄��슃�쐻占쎈윪占쎈�뗰옙�쐻占쎈윞占쎈빟占쎈쐻占쎈윥獒뺤룊�삕占쎌맶�뜝�럥�쑅占쎈쐻占쎈윥占쎈룈�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐪筌먦끇臾꾢뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩留썲뜝�럥�맶�뜝�럥�쑅�뜝�럩�읇�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇鍮됵옙�뀋�뜝�럡�땽�뜝�럥裕쏉옙�쐻占쎈윥占쎈윲�뜝�럥�맶�뜝�럥�쑅�뜝�럡�떙占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝�뜝�럥夷ⓨ뜝�럥흮占쎄뎡占쎈쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐앯솾�꺂�뒧占쎈뎐�뜝�럥�꺙占쎈쐻占쎈윪�뤃�먯삕占쎌맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛�뜝�럥�럵占쎈쐻占쎈윪筌뤵룇�삕占쎌맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛嶺뚯옓�뢾占쎌몡�뜝�럥爰뤷뜝�럩援뀐옙�쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁쉩�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛嶺뚯옓�렰占쎌맶�뜝�럥堉℡뜝�럡��嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪獄�釉앹삕占쎌맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀧솾�꺂�뒩占쎈뤅占쎈쐻占쎈뼢占쎄땀筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐앾옙�쐻占쎈윥鸚룐벂�쐻占쎈윥�씙�뜝�럡�렊�뜝�럥�맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빆�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐪筌먲퐦�삕�솒占쏙옙援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐앾옙�쐻占쎈윥鸚룐벂�쐻占쎈윥�씙�뜝�럡�렊�뜝�럥�맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁쉩�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛嶺뚯옓�렰占쎌맶�뜝�럥堉℡뜝�럡��嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�룞�삕�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐앭뜝�럥�맶�뜝�럥�쑋�뜝�럩諭울옙�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪占쎈쐻占쎈윥占쎈㎍�뜝�럥�맶占쎈쐻�뜝占� 2023/6/22
select 
 *
from
  (	select 
	  -- row_number() over (partition by sb.id_grade, sb.id_bettype, sb.id_kumiban order by (sb.incamt[0] + sb.incamt[1] + sb.incamt[2]) desc) as ranking,
	  row_number() over (partition by sb.id_grade, sb.id_bettype, sb.id_kumiban order by (sb.betcnt[0] + sb.betcnt[1] + sb.betcnt[2]) desc) as ranking,
	  sb.id_grade, sb.id_bettype, sb.id_kumiban,
	  ( se.id_grade || '_' || se.id_bettype || '_' || se.id_kumiban || '_' || se.id_patternid || '_' || se.id_factor || '_' || se.id_factormin || '_' || 
      se.id_modelno || '_' || se.id_sqlid || '_' || se.id_termtype || '_' || se.id_term || '_' || se.id_resultno  || '_' || se.daily_incamt  || '_' || se.daily_hitrate || '_' || se.daily_betcnt) eval_id,
      (sb.incamt[0] + sb.incamt[1] + sb.incamt[2]) inc_sb,
      (se.hitamt-se.betamt) inc_se,
      ((sb.betcnt[0]+sb.betcnt[1]+sb.betcnt[2])::float/210)::numeric(5,2) daily_bet
	from sim_eval se, 
	  ( select 
		  sb2.*
		from  
		  (select * from sim_bork where id_term = '12' and (incamt[0] + incamt[1] + incamt[2]) > 0) sb1,
		  (select * from sim_bork where id_term = 'R'  and (incamt[0] + incamt[1] + incamt[2]) > 0) sb2
		where sb1.id_grade = sb2.id_grade and sb1.id_bettype = sb2.id_bettype and sb1.id_kumiban = sb2.id_kumiban and sb1.id_patternid = sb2.id_patternid 
		  and sb1.id_factor = sb2.id_factor and sb1.id_factormin = sb2.id_factormin and sb1.id_modelno = sb2.id_modelno and sb1.id_sqlid = sb2.id_sqlid 
		  and sb1.id_termtype = sb2.id_termtype and sb1.id_resultno = sb2.id_resultno 
	  ) sb
	where se.id_grade = sb.id_grade and se.id_bettype = sb.id_bettype and se.id_kumiban = sb.id_kumiban and se.id_patternid = sb.id_patternid 
	  and se.id_factor = sb.id_factor and se.id_factormin = sb.id_factormin and se.id_modelno = sb.id_modelno and se.id_sqlid = sb.id_sqlid 
	  and se.id_termtype = sb.id_termtype and se.id_term = sb.id_term and se.id_resultno = sb.id_resultno 
  ) tmo
where ranking <= 3 
  and id_grade = 'ip'
order by id_grade, id_bettype, id_kumiban, ranking
;



--truncate ml_evaluation;
--truncate ml_bork_evaluation;
--truncate ml_range_evaluation;
--truncate ml_term_evaluation;
--delete from ml_evaluation where resultno::int > 84;
--delete from ml_bork_evaluation where resultno::int > 84;
--delete from ml_range_evaluation where resultno::int > 84;
--delete from ml_term_evaluation where resultno::int > 84;

select
  result_type, modelno, bettype,
  sum(betcnt) betcnt,
  sum((hitamt-betamt)) incamt, 
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(5,3) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(5,3) incomerate
from ml_evaluation me 
where evaluations_id = 'test'
  and patternid = 'nopattern'
  and substring(kumiban from 1 for 1) = '1'
  and modelno in ('79101','70101')
group by result_type, modelno, bettype
order by result_type, bettype, modelno
;


select *
from ml_classification mc 
where modelno = '90100'
and probabilities1[5] is null
;

select max(resultno::int) from ml_evaluation me;

delete from ml_classification where modelno = '70101';
select * from ml_classification mc where modelno = '90103';



select modelno, max(ymd) from ml_classification mc group by modelno order by modelno;

select count(1) from rec_race where ymd::int between   

select 'nopattern' pattern, 
  race.ymd, sanrentanprize::double precision 
  from rec_race race, rec_racer_arr arr, ml_classification clf, rec_bodds rb
  where race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
    and race.ymd = clf.ymd and race.jyocd = clf.jyocd and race.raceno = clf.raceno 
    and race.ymd = rb.ymd and race.jyocd = rb.jyocd and race.raceno = rb.raceno 
    and clf.modelno = '79101'
    and sanrentanno <> '' 
    and race.ymd >= '20220602' and race.ymd <= '20220602' 
  order by pattern, race.ymd, race.sime
;


select count

select
  count(1) 
from rec_bodds rb 
;

truncate rec_bodds;

select 
  '2T_' || nirentanno, count(1) cnt
from rec_race rr
where ymd::int < 20210602
--where (sanrentanno like '1%' or sanrentanno like '2%' or sanrentanno like '3%' or sanrentanno like '4%')
--  and (sanrentanno not like '%5%' and sanrentanno not like '%6%')
group by nirentanno 
order by cnt desc
;


delete from ml_evaluation where resultno::int between 10001 and 10002;
delete from ml_bork_evaluation where resultno::int between 10001 and 10002;
delete from ml_range_evaluation where resultno::int between 10001 and 10002;
delete from ml_term_evaluation where resultno::int between 10001 and 10002;



select 
  (hitamt-betamt)/100 incamt, count(1) cnt, sum(betcnt) s_betcnt, (sum(hitamt)::float/sum(betamt)::float)::numeric(5,2) incrate
from ml_evaluation me
where bettype = '3T'
group by incamt
order by incamt desc
;

select 
  (incomerate)::numeric(5,1) incomerate10, count(1) cnt, sum(betcnt) s_betcnt, (sum(hitamt)::float/sum(betamt)::float)::numeric(5,2) incrate
from ml_evaluation me
where bettype = '3T'
group by incomerate10
order by incomerate10 desc
;


select max(ymd) from ml_classification mc ;
select max(ymd) from rec_race mc ;

select count(1) from ml_bork_evaluation; -- where result_type = '4';
select count(1) from sim_eval where result_type = '4';



-- 
copy (
select
  id_grade || '_' || id_bettype || '_' || id_kumiban || '_' || id_patternid || '_' || id_factor || '_' || id_factormin || '_' || 
  id_modelno || '_' || id_sqlid || '_' || id_termtype || '_' || id_term || '_' || id_resultno  || '_' || daily_incamt  || '_' || daily_hitrate 
--  , daily_hitrate, (hitamt-betamt) incamt
from (
  select row_number() over (partition by id_grade, id_bettype, id_kumiban order by cnt desc) as ranking, * 
--  from sim_bork_cross
  from sim_bork
) tmp
where ranking between 1 and 20
order by id_grade, id_bettype, id_kumiban, ranking
) to 'D:\Dev\experiment\expr10\simulation_step1\simul_JSJ_ranked\top_plus_20_cross_cnt.tsv' csv delimiter E'\t';
;

--
copy (
select
  id_grade || '_' || id_bettype || '_' || id_kumiban || '_' || id_patternid || '_' || id_factor || '_' || id_factormin || '_' || 
  id_modelno || '_' || id_sqlid || '_' || id_termtype || '_' || id_term || '_' || id_resultno  || '_' || daily_incamt  || '_' || daily_hitrate 
--  , daily_hitrate, (hitamt-betamt) incamt
from (
  select row_number() over (partition by id_grade, id_bettype, id_kumiban order by daily_hitrate desc) as ranking, * 
  from sim_eval
--  from sim_eval_plus
  where id_term = 'R'
) tmp
where ranking between 1 and 20
order by id_grade, id_bettype, id_kumiban, ranking
) to 'D:\Dev\experiment\expr10\simulation_step1\simul_JSJ_ranked\top_20_daily_hitrate.tsv' csv delimiter E'\t';
;


-- simulation占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁쉩�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛嶺뚯옓�렰占쎌맶�뜝�럥堉℡뜝�럡��嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뗰옙�굲
drop table sim_bork_cross_plus;
create table sim_bork_cross_plus as  
select 
  *,
  (hitcnt::float/betcnt::float)::numeric(5,2) hitrate,
  (hitamt::float/(betcnt::float*100))::numeric(5,2) incomerate,
  (case when id_termtype = '77' then (hitamt-betamt)/180 else (hitamt-betamt)/150 end) daily_incamt,
  (case when id_termtype = '77' then (betcnt::float/180)::numeric(5,1) else (betcnt::float/150)::numeric(5,1) end) daily_betcnt,
  ((case when id_termtype = '77' then (betcnt::float/180) else (betcnt::float/150) end) * (hitcnt::float/betcnt::float))::numeric(5,2) daily_hitrate
from (
	select 
	  tr.id_grade, tr.id_bettype, tr.id_kumiban, tr.id_patternid, tr.id_factor, tr.id_factormin, 
	  tr.id_modelno, tr.id_sqlid, tr.id_termtype, tr.id_term, tr.id_resultno, tr.evaluations_id,
	  (
	    (case when tt.incamt[0] > 0 and tr.incamt[0] > 0 then 1 else 0 end) + (case when tt.incamt[1] > 0 and tr.incamt[1] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[2] > 0 and tr.incamt[2] > 0 then 1 else 0 end) + (case when tt.incamt[3] > 0 and tr.incamt[3] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[4] > 0 and tr.incamt[4] > 0 then 1 else 0 end) + (case when tt.incamt[5] > 0 and tr.incamt[5] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[6] > 0 and tr.incamt[6] > 0 then 1 else 0 end) + (case when tt.incamt[7] > 0 and tr.incamt[7] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[8] > 0 and tr.incamt[8] > 0 then 1 else 0 end) + (case when tt.incamt[9] > 0 and tr.incamt[9] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[10] > 0 and tr.incamt[10] > 0 then 1 else 0 end) + (case when tt.incamt[11] > 0 and tr.incamt[11] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[12] > 0 and tr.incamt[12] > 0 then 1 else 0 end) + (case when tt.incamt[13] > 0 and tr.incamt[13] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[14] > 0 and tr.incamt[14] > 0 then 1 else 0 end) + (case when tt.incamt[15] > 0 and tr.incamt[15] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[16] > 0 and tr.incamt[16] > 0 then 1 else 0 end) + (case when tt.incamt[17] > 0 and tr.incamt[17] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[18] > 0 and tr.incamt[18] > 0 then 1 else 0 end) + (case when tt.incamt[19] > 0 and tr.incamt[19] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[20] > 0 and tr.incamt[20] > 0 then 1 else 0 end) + (case when tt.incamt[21] > 0 and tr.incamt[21] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[22] > 0 and tr.incamt[22] > 0 then 1 else 0 end) + (case when tt.incamt[23] > 0 and tr.incamt[23] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[24] > 0 and tr.incamt[24] > 0 then 1 else 0 end) + (case when tt.incamt[25] > 0 and tr.incamt[25] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[26] > 0 and tr.incamt[26] > 0 then 1 else 0 end) + (case when tt.incamt[27] > 0 and tr.incamt[27] > 0 then 1 else 0 end) + 
	    (case when tt.incamt[28] > 0 and tr.incamt[28] > 0 then 1 else 0 end) + (case when tt.incamt[29] > 0 and tr.incamt[29] > 0 then 1 else 0 end) 
	  ) cnt,
	  (
	    (case when tt.incamt[0] > 0 and tr.incamt[0] > 0 then tr.betcnt[0] else 0 end) + (case when tt.incamt[1] > 0 and tr.incamt[1] > 0 then tr.betcnt[1] else 0 end) + 
	    (case when tt.incamt[2] > 0 and tr.incamt[2] > 0 then tr.betcnt[2] else 0 end) + (case when tt.incamt[3] > 0 and tr.incamt[3] > 0 then tr.betcnt[3] else 0 end) + 
	    (case when tt.incamt[4] > 0 and tr.incamt[4] > 0 then tr.betcnt[4] else 0 end) + (case when tt.incamt[5] > 0 and tr.incamt[5] > 0 then tr.betcnt[5] else 0 end) + 
	    (case when tt.incamt[6] > 0 and tr.incamt[6] > 0 then tr.betcnt[6] else 0 end) + (case when tt.incamt[7] > 0 and tr.incamt[7] > 0 then tr.betcnt[7] else 0 end) + 
	    (case when tt.incamt[8] > 0 and tr.incamt[8] > 0 then tr.betcnt[8] else 0 end) + (case when tt.incamt[9] > 0 and tr.incamt[9] > 0 then tr.betcnt[9] else 0 end) + 
	    (case when tt.incamt[10] > 0 and tr.incamt[10] > 0 then tr.betcnt[10] else 0 end) + (case when tt.incamt[11] > 0 and tr.incamt[11] > 0 then tr.betcnt[11] else 0 end) + 
	    (case when tt.incamt[12] > 0 and tr.incamt[12] > 0 then tr.betcnt[12] else 0 end) + (case when tt.incamt[13] > 0 and tr.incamt[13] > 0 then tr.betcnt[13] else 0 end) + 
	    (case when tt.incamt[14] > 0 and tr.incamt[14] > 0 then tr.betcnt[14] else 0 end) + (case when tt.incamt[15] > 0 and tr.incamt[15] > 0 then tr.betcnt[15] else 0 end) + 
	    (case when tt.incamt[16] > 0 and tr.incamt[16] > 0 then tr.betcnt[16] else 0 end) + (case when tt.incamt[17] > 0 and tr.incamt[17] > 0 then tr.betcnt[17] else 0 end) + 
	    (case when tt.incamt[18] > 0 and tr.incamt[18] > 0 then tr.betcnt[18] else 0 end) + (case when tt.incamt[19] > 0 and tr.incamt[19] > 0 then tr.betcnt[19] else 0 end) + 
	    (case when tt.incamt[20] > 0 and tr.incamt[20] > 0 then tr.betcnt[20] else 0 end) + (case when tt.incamt[21] > 0 and tr.incamt[21] > 0 then tr.betcnt[21] else 0 end) + 
	    (case when tt.incamt[22] > 0 and tr.incamt[22] > 0 then tr.betcnt[22] else 0 end) + (case when tt.incamt[23] > 0 and tr.incamt[23] > 0 then tr.betcnt[23] else 0 end) + 
	    (case when tt.incamt[24] > 0 and tr.incamt[24] > 0 then tr.betcnt[24] else 0 end) + (case when tt.incamt[25] > 0 and tr.incamt[25] > 0 then tr.betcnt[25] else 0 end) + 
	    (case when tt.incamt[26] > 0 and tr.incamt[26] > 0 then tr.betcnt[26] else 0 end) + (case when tt.incamt[27] > 0 and tr.incamt[27] > 0 then tr.betcnt[27] else 0 end) + 
	    (case when tt.incamt[28] > 0 and tr.incamt[28] > 0 then tr.betcnt[28] else 0 end) + (case when tt.incamt[29] > 0 and tr.incamt[29] > 0 then tr.betcnt[29] else 0 end) 
	  ) betcnt,
	  (
	    (case when tt.incamt[0] > 0 and tr.incamt[0] > 0 then tr.betamt[0] else 0 end) + (case when tt.incamt[1] > 0 and tr.incamt[1] > 0 then tr.betamt[1] else 0 end) + 
	    (case when tt.incamt[2] > 0 and tr.incamt[2] > 0 then tr.betamt[2] else 0 end) + (case when tt.incamt[3] > 0 and tr.incamt[3] > 0 then tr.betamt[3] else 0 end) + 
	    (case when tt.incamt[4] > 0 and tr.incamt[4] > 0 then tr.betamt[4] else 0 end) + (case when tt.incamt[5] > 0 and tr.incamt[5] > 0 then tr.betamt[5] else 0 end) + 
	    (case when tt.incamt[6] > 0 and tr.incamt[6] > 0 then tr.betamt[6] else 0 end) + (case when tt.incamt[7] > 0 and tr.incamt[7] > 0 then tr.betamt[7] else 0 end) + 
	    (case when tt.incamt[8] > 0 and tr.incamt[8] > 0 then tr.betamt[8] else 0 end) + (case when tt.incamt[9] > 0 and tr.incamt[9] > 0 then tr.betamt[9] else 0 end) + 
	    (case when tt.incamt[10] > 0 and tr.incamt[10] > 0 then tr.betamt[10] else 0 end) + (case when tt.incamt[11] > 0 and tr.incamt[11] > 0 then tr.betamt[11] else 0 end) + 
	    (case when tt.incamt[12] > 0 and tr.incamt[12] > 0 then tr.betamt[12] else 0 end) + (case when tt.incamt[13] > 0 and tr.incamt[13] > 0 then tr.betamt[13] else 0 end) + 
	    (case when tt.incamt[14] > 0 and tr.incamt[14] > 0 then tr.betamt[14] else 0 end) + (case when tt.incamt[15] > 0 and tr.incamt[15] > 0 then tr.betamt[15] else 0 end) + 
	    (case when tt.incamt[16] > 0 and tr.incamt[16] > 0 then tr.betamt[16] else 0 end) + (case when tt.incamt[17] > 0 and tr.incamt[17] > 0 then tr.betamt[17] else 0 end) + 
	    (case when tt.incamt[18] > 0 and tr.incamt[18] > 0 then tr.betamt[18] else 0 end) + (case when tt.incamt[19] > 0 and tr.incamt[19] > 0 then tr.betamt[19] else 0 end) + 
	    (case when tt.incamt[20] > 0 and tr.incamt[20] > 0 then tr.betamt[20] else 0 end) + (case when tt.incamt[21] > 0 and tr.incamt[21] > 0 then tr.betamt[21] else 0 end) + 
	    (case when tt.incamt[22] > 0 and tr.incamt[22] > 0 then tr.betamt[22] else 0 end) + (case when tt.incamt[23] > 0 and tr.incamt[23] > 0 then tr.betamt[23] else 0 end) + 
	    (case when tt.incamt[24] > 0 and tr.incamt[24] > 0 then tr.betamt[24] else 0 end) + (case when tt.incamt[25] > 0 and tr.incamt[25] > 0 then tr.betamt[25] else 0 end) + 
	    (case when tt.incamt[26] > 0 and tr.incamt[26] > 0 then tr.betamt[26] else 0 end) + (case when tt.incamt[27] > 0 and tr.incamt[27] > 0 then tr.betamt[27] else 0 end) + 
	    (case when tt.incamt[28] > 0 and tr.incamt[28] > 0 then tr.betamt[28] else 0 end) + (case when tt.incamt[29] > 0 and tr.incamt[29] > 0 then tr.betamt[29] else 0 end) 
	  ) betamt,
	  (
	    (case when tt.incamt[0] > 0 and tr.incamt[0] > 0 then tr.hitcnt[0] else 0 end) + (case when tt.incamt[1] > 0 and tr.incamt[1] > 0 then tr.hitcnt[1] else 0 end) + 
	    (case when tt.incamt[2] > 0 and tr.incamt[2] > 0 then tr.hitcnt[2] else 0 end) + (case when tt.incamt[3] > 0 and tr.incamt[3] > 0 then tr.hitcnt[3] else 0 end) + 
	    (case when tt.incamt[4] > 0 and tr.incamt[4] > 0 then tr.hitcnt[4] else 0 end) + (case when tt.incamt[5] > 0 and tr.incamt[5] > 0 then tr.hitcnt[5] else 0 end) + 
	    (case when tt.incamt[6] > 0 and tr.incamt[6] > 0 then tr.hitcnt[6] else 0 end) + (case when tt.incamt[7] > 0 and tr.incamt[7] > 0 then tr.hitcnt[7] else 0 end) + 
	    (case when tt.incamt[8] > 0 and tr.incamt[8] > 0 then tr.hitcnt[8] else 0 end) + (case when tt.incamt[9] > 0 and tr.incamt[9] > 0 then tr.hitcnt[9] else 0 end) + 
	    (case when tt.incamt[10] > 0 and tr.incamt[10] > 0 then tr.hitcnt[10] else 0 end) + (case when tt.incamt[11] > 0 and tr.incamt[11] > 0 then tr.hitcnt[11] else 0 end) + 
	    (case when tt.incamt[12] > 0 and tr.incamt[12] > 0 then tr.hitcnt[12] else 0 end) + (case when tt.incamt[13] > 0 and tr.incamt[13] > 0 then tr.hitcnt[13] else 0 end) + 
	    (case when tt.incamt[14] > 0 and tr.incamt[14] > 0 then tr.hitcnt[14] else 0 end) + (case when tt.incamt[15] > 0 and tr.incamt[15] > 0 then tr.hitcnt[15] else 0 end) + 
	    (case when tt.incamt[16] > 0 and tr.incamt[16] > 0 then tr.hitcnt[16] else 0 end) + (case when tt.incamt[17] > 0 and tr.incamt[17] > 0 then tr.hitcnt[17] else 0 end) + 
	    (case when tt.incamt[18] > 0 and tr.incamt[18] > 0 then tr.hitcnt[18] else 0 end) + (case when tt.incamt[19] > 0 and tr.incamt[19] > 0 then tr.hitcnt[19] else 0 end) + 
	    (case when tt.incamt[20] > 0 and tr.incamt[20] > 0 then tr.hitcnt[20] else 0 end) + (case when tt.incamt[21] > 0 and tr.incamt[21] > 0 then tr.hitcnt[21] else 0 end) + 
	    (case when tt.incamt[22] > 0 and tr.incamt[22] > 0 then tr.hitcnt[22] else 0 end) + (case when tt.incamt[23] > 0 and tr.incamt[23] > 0 then tr.hitcnt[23] else 0 end) + 
	    (case when tt.incamt[24] > 0 and tr.incamt[24] > 0 then tr.hitcnt[24] else 0 end) + (case when tt.incamt[25] > 0 and tr.incamt[25] > 0 then tr.hitcnt[25] else 0 end) + 
	    (case when tt.incamt[26] > 0 and tr.incamt[26] > 0 then tr.hitcnt[26] else 0 end) + (case when tt.incamt[27] > 0 and tr.incamt[27] > 0 then tr.hitcnt[27] else 0 end) + 
	    (case when tt.incamt[28] > 0 and tr.incamt[28] > 0 then tr.hitcnt[28] else 0 end) + (case when tt.incamt[29] > 0 and tr.incamt[29] > 0 then tr.hitcnt[29] else 0 end) 
	  ) hitcnt,
	  (
	    (case when tt.incamt[0] > 0 and tr.incamt[0] > 0 then tr.hitamt[0] else 0 end) + (case when tt.incamt[1] > 0 and tr.incamt[1] > 0 then tr.hitamt[1] else 0 end) + 
	    (case when tt.incamt[2] > 0 and tr.incamt[2] > 0 then tr.hitamt[2] else 0 end) + (case when tt.incamt[3] > 0 and tr.incamt[3] > 0 then tr.hitamt[3] else 0 end) + 
	    (case when tt.incamt[4] > 0 and tr.incamt[4] > 0 then tr.hitamt[4] else 0 end) + (case when tt.incamt[5] > 0 and tr.incamt[5] > 0 then tr.hitamt[5] else 0 end) + 
	    (case when tt.incamt[6] > 0 and tr.incamt[6] > 0 then tr.hitamt[6] else 0 end) + (case when tt.incamt[7] > 0 and tr.incamt[7] > 0 then tr.hitamt[7] else 0 end) + 
	    (case when tt.incamt[8] > 0 and tr.incamt[8] > 0 then tr.hitamt[8] else 0 end) + (case when tt.incamt[9] > 0 and tr.incamt[9] > 0 then tr.hitamt[9] else 0 end) + 
	    (case when tt.incamt[10] > 0 and tr.incamt[10] > 0 then tr.hitamt[10] else 0 end) + (case when tt.incamt[11] > 0 and tr.incamt[11] > 0 then tr.hitamt[11] else 0 end) + 
	    (case when tt.incamt[12] > 0 and tr.incamt[12] > 0 then tr.hitamt[12] else 0 end) + (case when tt.incamt[13] > 0 and tr.incamt[13] > 0 then tr.hitamt[13] else 0 end) + 
	    (case when tt.incamt[14] > 0 and tr.incamt[14] > 0 then tr.hitamt[14] else 0 end) + (case when tt.incamt[15] > 0 and tr.incamt[15] > 0 then tr.hitamt[15] else 0 end) + 
	    (case when tt.incamt[16] > 0 and tr.incamt[16] > 0 then tr.hitamt[16] else 0 end) + (case when tt.incamt[17] > 0 and tr.incamt[17] > 0 then tr.hitamt[17] else 0 end) + 
	    (case when tt.incamt[18] > 0 and tr.incamt[18] > 0 then tr.hitamt[18] else 0 end) + (case when tt.incamt[19] > 0 and tr.incamt[19] > 0 then tr.hitamt[19] else 0 end) + 
	    (case when tt.incamt[20] > 0 and tr.incamt[20] > 0 then tr.hitamt[20] else 0 end) + (case when tt.incamt[21] > 0 and tr.incamt[21] > 0 then tr.hitamt[21] else 0 end) + 
	    (case when tt.incamt[22] > 0 and tr.incamt[22] > 0 then tr.hitamt[22] else 0 end) + (case when tt.incamt[23] > 0 and tr.incamt[23] > 0 then tr.hitamt[23] else 0 end) + 
	    (case when tt.incamt[24] > 0 and tr.incamt[24] > 0 then tr.hitamt[24] else 0 end) + (case when tt.incamt[25] > 0 and tr.incamt[25] > 0 then tr.hitamt[25] else 0 end) + 
	    (case when tt.incamt[26] > 0 and tr.incamt[26] > 0 then tr.hitamt[26] else 0 end) + (case when tt.incamt[27] > 0 and tr.incamt[27] > 0 then tr.hitamt[27] else 0 end) + 
	    (case when tt.incamt[28] > 0 and tr.incamt[28] > 0 then tr.hitamt[28] else 0 end) + (case when tt.incamt[29] > 0 and tr.incamt[29] > 0 then tr.hitamt[29] else 0 end) 
	  ) hitamt,
	  (
	    (case when tt.incamt[0] > 0 and tr.incamt[0] > 0 then tr.incamt[0] else 0 end) + (case when tt.incamt[1] > 0 and tr.incamt[1] > 0 then tr.incamt[1] else 0 end) + 
	    (case when tt.incamt[2] > 0 and tr.incamt[2] > 0 then tr.incamt[2] else 0 end) + (case when tt.incamt[3] > 0 and tr.incamt[3] > 0 then tr.incamt[3] else 0 end) + 
	    (case when tt.incamt[4] > 0 and tr.incamt[4] > 0 then tr.incamt[4] else 0 end) + (case when tt.incamt[5] > 0 and tr.incamt[5] > 0 then tr.incamt[5] else 0 end) + 
	    (case when tt.incamt[6] > 0 and tr.incamt[6] > 0 then tr.incamt[6] else 0 end) + (case when tt.incamt[7] > 0 and tr.incamt[7] > 0 then tr.incamt[7] else 0 end) + 
	    (case when tt.incamt[8] > 0 and tr.incamt[8] > 0 then tr.incamt[8] else 0 end) + (case when tt.incamt[9] > 0 and tr.incamt[9] > 0 then tr.incamt[9] else 0 end) + 
	    (case when tt.incamt[10] > 0 and tr.incamt[10] > 0 then tr.incamt[10] else 0 end) + (case when tt.incamt[11] > 0 and tr.incamt[11] > 0 then tr.incamt[11] else 0 end) + 
	    (case when tt.incamt[12] > 0 and tr.incamt[12] > 0 then tr.incamt[12] else 0 end) + (case when tt.incamt[13] > 0 and tr.incamt[13] > 0 then tr.incamt[13] else 0 end) + 
	    (case when tt.incamt[14] > 0 and tr.incamt[14] > 0 then tr.incamt[14] else 0 end) + (case when tt.incamt[15] > 0 and tr.incamt[15] > 0 then tr.incamt[15] else 0 end) + 
	    (case when tt.incamt[16] > 0 and tr.incamt[16] > 0 then tr.incamt[16] else 0 end) + (case when tt.incamt[17] > 0 and tr.incamt[17] > 0 then tr.incamt[17] else 0 end) + 
	    (case when tt.incamt[18] > 0 and tr.incamt[18] > 0 then tr.incamt[18] else 0 end) + (case when tt.incamt[19] > 0 and tr.incamt[19] > 0 then tr.incamt[19] else 0 end) + 
	    (case when tt.incamt[20] > 0 and tr.incamt[20] > 0 then tr.incamt[20] else 0 end) + (case when tt.incamt[21] > 0 and tr.incamt[21] > 0 then tr.incamt[21] else 0 end) + 
	    (case when tt.incamt[22] > 0 and tr.incamt[22] > 0 then tr.incamt[22] else 0 end) + (case when tt.incamt[23] > 0 and tr.incamt[23] > 0 then tr.incamt[23] else 0 end) + 
	    (case when tt.incamt[24] > 0 and tr.incamt[24] > 0 then tr.incamt[24] else 0 end) + (case when tt.incamt[25] > 0 and tr.incamt[25] > 0 then tr.incamt[25] else 0 end) + 
	    (case when tt.incamt[26] > 0 and tr.incamt[26] > 0 then tr.incamt[26] else 0 end) + (case when tt.incamt[27] > 0 and tr.incamt[27] > 0 then tr.incamt[27] else 0 end) + 
	    (case when tt.incamt[28] > 0 and tr.incamt[28] > 0 then tr.incamt[28] else 0 end) + (case when tt.incamt[29] > 0 and tr.incamt[29] > 0 then tr.incamt[29] else 0 end) 
	  ) incamt
	from 
	  (select * from sim_bork_plus where id_term <> 'R') tt,
	  (select * from sim_bork_plus where id_term = 'R') tr
	where
	    tt.id_grade = tr.id_grade and tt.id_bettype = tr.id_bettype and tt.kumiban = tr.kumiban and tt.id_patternid = tr.id_patternid
      and tt.id_factor = tr.id_factor and tt.id_factormin = tr.id_factormin and tt.id_modelno = tr.id_modelno and tt.id_sqlid = tr.id_sqlid and tt.id_termtype = tr.id_termtype
) tmp
where betcnt > 0
;



-- ml_bork_evaluation.
drop table sim_bork_plus;
create table sim_bork_plus as 
select sb.* 
from sim_bork sb, sim_eval_plus se 
where sb.evaluations_id = se.evaluations_id and sb.kumiban = se.kumiban
;



-- ml_evaluation�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럩紐욃뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앯솾�꺂�뒧筌잙뎽�쐻占쎈뼢占쎄땀筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇�돥占쎌맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럩紐욃뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앯솾�꺂�뒧筌잙뎽�쐻占쎈뼢占쎄땀筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇鍮됵옙�뀋�뜝�럡�땽�뜝�럥裕쏉옙�쐻占쎈윥占쎈윻占쎈쐻占쎈윥占쏙옙占쎈쐻占쎈윪占쎈쨮占쎈쐻占쎈윪�뤃�먯삕占쎌맶�뜝�럥�쐾�뜝�럥�젃�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앯솾�꺂�뒧占쎈뎐�뜝�럥�꺙占쎈쐻占쎈윪�뤃�먯삕占쎌맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�룞�삕�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐앭뜝�럥�맶�뜝�럥�쑋�뜝�럩諭울옙�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗癲ル슣�돵�뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥筌욌쪋�삕占쎌맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앭뜝�럥�맶�뜝�럥�쑅嶺뚋욌벉�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅嶺뚯쉸占싸살맶�뜝�럥�쑋占쎈쨨占쎈Ŋ�굲�뜝�럥�뗰옙�쐻占쎈윥占쎈뭸占쎈쐻占쎈윞占쎈뻾�뜝�럥�맶�뜝�럥�쑋占쎈쨨占쎈Ŋ�굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎄콪占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢�뵯占쎈탿�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇�돥占쎌맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럩紐욃뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앯솾�꺂�뒧筌잙뎽�쐻占쎈뼢占쎄땀筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑅�뜝�럥�쑌�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇鍮됵옙�뀋�뜝�럡�땽�뜝�럥裕쏉옙�쐻占쎈윥占쎈윻占쎈쐻占쎈윥占쏙옙占쎈쐻占쎈윪占쎈쨮占쎈쐻占쎈윪�뤃�먯삕占쎌맶�뜝�럥�쐾�뜝�럥�젃�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앯솾�꺂�뒧占쎈뎐�뜝�럥�꺙占쎈쐻占쎈윪�뤃�먯삕占쎌맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�룞�삕�뜝�럩援뀐옙�쐻占쎈윥占쎈룿�뜝�럥�맶�뜝�럥�쑅占쎌젂�겫釉먮븸�뜝�럥�뒄占쎌젂占쎌뵥�얜뀘移곤옙�윥占쎈／占쎈쐻占쎈윥�댚�렱�쐻占쎈윞占쎈젇占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥釉붾���뜝�럥�맶�뜝�럥吏쀥뜝�럩援뀐옙�쐻占쎈윞�굜�껊쐻占쎈윥占쏙옙占쎈쐻占쎈윥�뙴�뎽�쐻占쎈윥筌욊랬�삕占쎌맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앯솾�꺂�뒧占쎈뎐�뜝�럥�꺙占쎈쐻占쎈윪�뤃�먯삕占쎌맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉔뜮�맮�삕占쎈�뗰옙�쐻占쎈윞占쎈빟占쎈쐻占쎈윥獒뺤룊�삕占쎌맶�뜝�럥�쑅�뜝�럥�쑓占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윞占쎈뼑�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앾옙�쐻占쎈윥鸚룐벂�쐻占쎈윥�씙�뜝�럡�렊�뜝�럥�맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛�뜝�럥�럯占쎈쐻占쎈윥占쎄틯�뜝�럥�맶�뜝�럥�쑋占쎈쨨占쎈Ŋ�굲�뜝�럩留띰옙�쐻占쎈윥占쎌몝癲ル슢�뵯占쎈탿�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앯솾�꺂�뒧筌잙뎽�쐻占쎈쑌�뙴洹⑥삕占쎈뿰�삕占쎌맶�뜝�럥�쐾�뜝�럥鍮잌뜝�럥�맶�뜝�럥�쑅�뜏類ㅻ묄占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗癲ル슢�뒧野껓옙�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗�뜝�럩�쟼�뜝�럡�맗�뜝�럩留띰옙�쐻占쎈윥占쎌맽占쎈쐻占쎈짗占쎌굲占쎌녇占쎄틓占쎈뮛�뜝�럥�럯占쎈쐻占쎈윥占쎄틯�뜝�럥�맶�뜝�럥�쑋占쎈쨨占쎈Ŋ�굲�뜝�럩留띰옙�쐻占쎈윥占쎌몝癲ル슢�뵯占쎈탿�뜝�럩援뀐옙�쐻占쎈윪筌띾씛�삕占쎌맶�뜝�럥�쑅�뜝�럩紐쀥뜝�럥�맶�뜝�럥�쑋嶺뚮쪇占싸살맶�뜝�럥�쑅�뜝�럥�럪�뜝�럥�맶�뜝�럥�쑅�뜝�럩紐앯솾�꺂�뒧筌잙돁�쑏�뜝�뜴�쐻占쎈윪�뤃�먯삕占쎌맶�뜝�럥�쑋嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁���삕占쎈퉳�뜝�럥堉붺춯�빘堉뉛옙釉뜹뜝�럡�뜦�뜝�럩援뀐옙�쐻占쎈윞占쎈젇占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢�뵯占쎈け�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫�뜝�럥�맶�뜝�럥�쑅�뜝�럥�럪占쎈쐻占쎈윥占쎈㎍�뜝�럥�맶占쎈쐻�뜝占�
drop table sim_eval_plus;
-- SG_1T_*-*-*_wk12+jyo+race_i09-100_0_79100_JSJ-S2_77_12_10150
create table sim_eval_plus as 
select 
  cast(ids[1] as varchar(2)) id_grade , cast(ids[2] as varchar(2)) id_bettype, 
  cast(kumiban as varchar(3)) id_kumiban, cast(ids[4] as varchar(20)) id_patternid, 
  cast(ids[5] as varchar(20)) id_factor, cast(ids[6] as varchar(20)) id_factormin, 
  cast(ids[7] as varchar(5)) id_modelno, cast(ids[8] as varchar(20)) id_sqlid, 
  cast(ids[9] as varchar(5)) id_termtype, cast(ids[10] as varchar(5)) id_term, 
  cast(ids[11] as varchar(6)) id_resultno,
  (case when ids[9] = '77' then (hitamt-betamt)/180 else (hitamt-betamt)/150 end) daily_incamt,
  (case when ids[9] = '77' then (betcnt::float/180)::numeric(5,1) else (betcnt::float/150)::numeric(5,1) end) daily_betcnt,
  ((case when ids[9] = '77' then (betcnt::float/180) else (betcnt::float/150) end) * hitrate)::numeric(5,2) daily_hitrate,
  *  
from 
(
  select  string_to_array(evaluations_id, '_') ids, * 
  from ml_evaluation me where result_type = '4' and (hitamt-betamt) > 0
)tmp;



-- ml_evaluation占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁쉩�쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁빉�녇占쎄틓占쎈뮛嶺뚯옓�렰占쎌맶�뜝�럥堉℡뜝�럡��嶺뚮씭�뵛占쎌굲�뜝�럩留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎌몞占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뉐뜝�떥�궡留띰옙�쐻占쎈윥占쎌몗占쎈쐻占쎈윥占쎈윫占쎈쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몗占쎈쐻占쎈윪筌륁�λ쐻占쎈윥占쎈㎍占쎈쐻占쎈윥占쎌몝癲ル슢履뗰옙�굲
drop table sim_eval;
-- SG_1T_*-*-*_wk12+jyo+race_i09-100_0_79100_JSJ-S2_77_12_10150
create table sim_eval as 
select 
  cast(ids[1] as varchar(2)) id_grade , cast(ids[2] as varchar(2)) id_bettype, 
  cast(kumiban as varchar(3)) id_kumiban, cast(ids[4] as varchar(20)) id_patternid, 
  cast(ids[5] as varchar(20)) id_factor, cast(ids[6] as varchar(20)) id_factormin, 
  cast(ids[7] as varchar(5)) id_modelno, cast(ids[8] as varchar(20)) id_sqlid, 
  cast(ids[9] as varchar(5)) id_termtype, cast(ids[10] as varchar(5)) id_term, 
  cast(ids[11] as varchar(6)) id_resultno,
  (case when ids[10] = '12' then (hitamt-betamt)/420 else (hitamt-betamt)/210 end) daily_incamt,
  (case when ids[10] = '12' then (betcnt::float/420)::numeric(5,1) else (betcnt::float/210)::numeric(5,1) end) daily_betcnt,
  ((case when ids[9] = '12' then (betcnt::float/420) else (betcnt::float/210) end) * hitrate)::numeric(5,2) daily_hitrate,
  *  
from 
(
  select  string_to_array(evaluations_id, '_') ids, 
  * from ml_evaluation me where result_type = '4'
)tmp;



select 
  result_type, bettype, (i09/50) factor, count(1)
from stat_bork
where result_type = '1' and bettype = '3T'
group by result_type, bettype, factor
order by result_type, bettype, factor
;


select
  bal_slope[0] slope1, *
from (
  select row_number() over (partition by id_grade, id_bettype, id_kumiban order by daily_incamt desc) as ranking, * 
  from tab_sim ts
  where id_term = 'R'
) tmp
where ranking between 1 and 20
order by id_grade, id_bettype, id_kumiban, ranking
;


select * from ml_evaluation me where result_type = '4';

select max(resultno) from ml_evaluation me where resultno::int between 10166 and 10188;

select
  r1.betcnt, r1.*
from
    ( select * from stat_bork  where evaluations_id = '77_1') t1
  , ( select * from stat_bork where evaluations_id = '77_2') t2
  , ( select * from stat_bork where evaluations_id = '77_3' ) r1
where 
  t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
  and (t1.i02/100) >= 0 and (t2.i02/100) >= 0
  and t1.bettype = '3T' and t1.kumiban like '123' and t1.modelno = '21609' and t1.patternid = 'wk12+jyo'
;

select distinct modelno from ml_evaluation;

select  
  r1.result_type, r1.bettype, r1.kumiban, r1.modelno, r1.patternid, r1.evaluations_id, 
  count(1) cnt,
  sum(r1.i02) factor,
  sum(r1.hitamt-r1.betamt) incamtr,
  sum(t1.betcnt) betcnt1,
  sum(t2.betcnt) betcnt2,
  sum(r1.betcnt) betcntr, 
  sum(t1.hitamt-t1.betamt) incamt1,
  sum(t2.hitamt-t2.betamt) incamt2,
  (sum(r1.betcnt)::float/210)::numeric(5,1) dailybet,
  ( (sum(r1.betcnt)::float/210) * (sum(r1.hitcnt)::float / sum(r1.betcnt)::float) )::numeric(5,1) dailyhit,
  sum(r1.hitcnt) hitcnt,
  sum(r1.betamt) betamt,
  sum(r1.hitamt) hitamt,
  (sum(r1.hitamt-r1.betamt)::float / sum(r1.betcnt))::numeric(10,2) betinc,
  (sum(r1.hitcnt)::float / sum(r1.betcnt)::float)::numeric(10,2) hitrate,
  (sum(r1.hitamt)::float / sum(r1.betamt)::float)::numeric(10,2) incrate
from
  ( select * from stat_bork  where evaluations_id = '77_1') t1
  , ( select * from stat_bork where evaluations_id = '77_2') t2
  , ( select * from stat_bork where evaluations_id = '77_3' ) r1
where 
  t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
--  and (t1.i02/100) >= 0 and (t2.i02/100) >= 0
  and (t1.incrate >= 0.9 and t2.incrate >= 0.9)
  and r1.bettype = '3T' and r1.kumiban like '126'
--  and t1.bettype = '3F' and t1.patternid = 'wk123'
--  and substring(r1.kumiban from 1 for 1) not in ('1', '2')
group by r1.result_type, r1.bettype, r1.kumiban, r1.modelno, r1.patternid, r1.evaluations_id
order by bettype, kumiban, result_type, factor desc
;

select
'~' sel,
(case when t2.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades,
t2.bettype, t2.kumiban, t2.modelno, t2.patternid, t2.pattern,
t2.betcnt, t2.incamt, t2.hitrate, t2.incrate,
'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from
( select * from stat_bork where evaluations_id = '77_1' ) t1
, ( select * from stat_bork where evaluations_id = '77_2' ) t2
where
t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban
and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern
and t2.result_type = '1' and t2.bettype = '3T' and t2.kumiban = '123'
and t2.modelno = '21609' and t2.patternid = 'wk12+jyo'
and (t1.i02/100) between 0 and 9999999999
and (t2.i02/100) between 0 and 9999999999
order by grades, bettype, kumiban, modelno, patternid, pattern
;


select distinct modelno from stat_bork sb;

select 
  result_type, bettype, kumiban, modelno, patternid, count(1) cnt,
  sum(i0) inc
from stat_bork  
where evaluations_id = '77_1' and bettype = '3T'
  and (i0/100) >= 0
group by result_type, bettype, kumiban, modelno, patternid
order by result_type, bettype, kumiban, cnt desc
; 

select count(1) from tab_sim;
select count(1) from stat_bork;
select count(1) from ml_evaluation;

truncate stat_bork;
insert into stat_bork
  select 
    me.result_type, me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
    bk.incamt[0] i0, bk.incamt[1] i1, bk.incamt[2] i2, bk.incamt[3] i3, bk.incamt[4] i4, 
    bk.incamt[5] i5, bk.incamt[6] i6, bk.incamt[7] i7, bk.incamt[8] i8, bk.incamt[9] i9, 
    (bk.incamt[0] + bk.incamt[1]) i01,
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2]) i02,
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2] + bk.incamt[3]) i03,
    (bk.incamt[1] + bk.incamt[2] + bk.incamt[3]) i13,
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2] + bk.incamt[3] + bk.incamt[4]) i04,
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2] + bk.incamt[3] + bk.incamt[4] + bk.incamt[5]) i05,
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2] + bk.incamt[3] + bk.incamt[4] + bk.incamt[5] + bk.incamt[6]) i06,
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2] + bk.incamt[3] + bk.incamt[4] + bk.incamt[5] + bk.incamt[6] + bk.incamt[7]) i07,
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2] + bk.incamt[3] + bk.incamt[4] + bk.incamt[5] + bk.incamt[6] + bk.incamt[7] + bk.incamt[8]) i08,
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2] + bk.incamt[3] + bk.incamt[4] + bk.incamt[5] + bk.incamt[6] + bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i09,
    (bk.incamt[1] + bk.incamt[2] + bk.incamt[3] + bk.incamt[4] + bk.incamt[5] + bk.incamt[6] + bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i19,
    (bk.incamt[2] + bk.incamt[3] + bk.incamt[4] + bk.incamt[5] + bk.incamt[6] + bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i29,
    (bk.incamt[3] + bk.incamt[4] + bk.incamt[5] + bk.incamt[6] + bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i39,
    (bk.incamt[4] + bk.incamt[5] + bk.incamt[6] + bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i49,
    (bk.incamt[5] + bk.incamt[6] + bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i59,
    (bk.incamt[6] + bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i69,
    (bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i79,
    (bk.incamt[8] + bk.incamt[9]) i89,
    me.betcnt, me.hitcnt, me.betamt, me.hitamt, (me.hitamt - me.betamt) incamt, me.betrate, me.hitrate, me.incomerate, me.bal_pluscnt, me.evaluations_id
  from ml_evaluation me, ml_bork_evaluation bk
  where  
    me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern and me.evaluations_id = bk.evaluations_id
     and me.result_type in ('1', '11')
    --and me.resultno::int between 425 and 568
;

select  
  r1.result_type, r1.bettype, r1.modelno, r1.patternid, r1.evaluations_id, 
  count(1) cnt,
--  sum(r1.betcnt) betcnt, 
  (sum(r1.betcnt)::float/180)::numeric(5,1) dailybet,
  ( (sum(r1.betcnt)::float/450) * (sum(r1.hitcnt)::float / sum(r1.betcnt)::float) )::numeric(5,1) dailyhit,
  sum(r1.hitcnt) hitcnt,
  sum(r1.betamt) betamt,
  sum(r1.hitamt) hitamt,
  sum(r1.hitamt-r1.betamt) incamt,
  (sum(r1.hitamt-r1.betamt)::float / sum(r1.betcnt))::numeric(5,2) betinc,
  (sum(r1.hitcnt)::float / sum(r1.betcnt)::float)::numeric(5,2) hitrate,
  (sum(r1.hitamt)::float / sum(r1.betamt)::float)::numeric(5,2) incrate
from
  ( select * from ml_evaluation where evaluations_id = '77_1') t1
  , ( select * from ml_evaluation where evaluations_id = '77_2') t2
  , ( select * from ml_evaluation where evaluations_id = '77_3' ) r1
where 
  t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
  and t1.incomerate > 0.9 and t2.incomerate > 0.9
  and t1.bork_betrate > 0.2 and t2.bork_betrate > 0.2
  and t1.bettype = '3T'
--  and t1.bettype = '3F' and t1.patternid = 'wk123'
--  and substring(r1.kumiban from 1 for 1) not in ('1', '2')
group by r1.result_type, r1.bettype, r1.modelno, r1.patternid, r1.evaluations_id
order by bettype, result_type, incamt desc
;

select
  result_type, bettype, modelno, patternid, i04
from 
(
	select 
	  result_type, bettype, modelno, patternid, sum((incamt[0]+incamt[1]+incamt[2]+incamt[3]+incamt[4])) i04
	from ml_bork_evaluation where evaluations_id = '77_2'
	where (incamt[0]+incamt[1]+incamt[2]+incamt[3]+incamt[4]) > 0
	group by result_type, bettype, modelno, patternid
) t1
order by result_type, bettype, i04 desc 
; 


select 
  result_type, bettype, modelno, patternid, sum((incamt[0]+incamt[1]+incamt[2]+incamt[3]+incamt[4]))/100 i04
from ml_bork_evaluation 
where evaluations_id = '77_2'
  and (incamt[0]+incamt[1]+incamt[2]+incamt[3]+incamt[4]) > 0
group by result_type, bettype, modelno, patternid
order by result_type, bettype, i04 desc 
;



select count(1) from ml_evaluation mbe;

select bettype, count(1) from ml_evaluation me where resultno = '1' group by bettype;

select
'~' sel,
(case when t3.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades,
t3.bettype, t3.kumiban, t3.modelno, t3.patternid, t3.pattern,
t3.betcnt, (t3.hitamt - t3.betamt) incamt, t3.hitrate, t3.incomerate,
'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from
( select * from ml_evaluation where evaluations_id = '555_1' ) t1
, ( select * from ml_evaluation where evaluations_id = '555_2' ) t2
, ( select * from ml_evaluation where evaluations_id = '{term_3}' ) t3
where
t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban
and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern
and t1.result_type = t3.result_type and t1.bettype = t3.bettype and t1.kumiban = t3.kumiban
and t1.modelno = t3.modelno and t1.patternid = t3.patternid and t1.pattern = t3.pattern
and t3.result_type = '1' and t3.bettype = '1T'
and t3.modelno = '79100' and t3.patternid = 'wk1234'
and t1.incomerate between 0.1 and 999
and t2.incomerate between 0.1 and 999
and t3.incomerate between 0.1 and 999
order by grades, bettype, kumiban, modelno, patternid, pattern

select * from ml_evaluation me where resultno = '10011';

select 
  substring(probability1::text from 1 for 3) || '-' || substring(probability2::text from 1 for 3) || '-' || substring(probability3::text from 1 for 3) ptn, count(1)
  --substring(wakulevellist from 1 for 11) ptn, count(1)
from rec_race race, ml_classification cls, rec_racer_arr arr
where race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno 
  and race.ymd = arr.ymd and race.jyocd = arr.jyocd and race.raceno = arr.raceno 
group by ptn
order by ptn
;



select  
  r1.result_type, r1.bettype, r1.modelno, r1.patternid, r1.evaluations_id, 
  count(1) cnt,
--  sum(r1.betcnt) betcnt, 
  (sum(r1.betcnt)::float/450)::numeric(5,1) dailybet,
  ( (sum(r1.betcnt)::float/450) * (sum(r1.hitcnt)::float / sum(r1.betcnt)::float) )::numeric(5,1) dailyhit,
  sum(r1.hitcnt) hitcnt,
  sum(r1.betamt) betamt,
  sum(r1.hitamt) hitamt,
  sum(r1.hitamt-r1.betamt) incamt,
  (sum(r1.hitamt-r1.betamt)::float / sum(r1.betcnt))::numeric(5,2) betinc,
  (sum(r1.hitcnt)::float / sum(r1.betcnt)::float)::numeric(5,2) hitrate,
  (sum(r1.hitamt)::float / sum(r1.betamt)::float)::numeric(5,2) incrate
from
  ( select * from ml_evaluation where evaluations_id = '15_1') t1
  , ( select * from ml_evaluation where evaluations_id = '15_2' ) r1
where 
  t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
  and t1.incomerate > 1
--  and substring(r1.kumiban from 1 for 1) not in ('1', '2')
group by r1.bettype, r1.result_type, r1.modelno, r1.patternid, r1.evaluations_id
order by  bettype, result_type, incamt desc
;

select  
  r1.result_type, r1.bettype, r1.modelno, r1.patternid, r1.evaluations_id, 
  count(1) cnt,
--  sum(r1.betcnt) betcnt, 
  (sum(r1.betcnt)::float/180)::numeric(5,1) dailybet,
  ( (sum(r1.betcnt)::float/450) * (sum(r1.hitcnt)::float / sum(r1.betcnt)::float) )::numeric(5,1) dailyhit,
  sum(r1.hitcnt) hitcnt,
  sum(r1.betamt) betamt,
  sum(r1.hitamt) hitamt,
  sum(r1.hitamt-r1.betamt) incamt,
  (sum(r1.hitamt-r1.betamt)::float / sum(r1.betcnt))::numeric(5,2) betinc,
  (sum(r1.hitcnt)::float / sum(r1.betcnt)::float)::numeric(5,2) hitrate,
  (sum(r1.hitamt)::float / sum(r1.betamt)::float)::numeric(5,2) incrate
from
  ( select * from ml_evaluation where evaluations_id = '77_1') t1
  , ( select * from ml_evaluation where evaluations_id = '77_2') t2
  , ( select * from ml_evaluation where evaluations_id = '77_3' ) r1
where 
  t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
  and t1.incomerate > 0.9 and t2.incomerate > 0.9
  and t1.bork_betrate > 0.2 and t2.bork_betrate > 0.2
  and t1.bettype = '3T'
--  and t1.bettype = '3F' and t1.patternid = 'wk123'
--  and substring(r1.kumiban from 1 for 1) not in ('1', '2')
group by r1.result_type, r1.bettype, r1.modelno, r1.patternid, r1.evaluations_id
order by bettype, result_type, incamt desc
;

select  
  r1.result_type, r1.bettype, r1.modelno, r1.patternid, r1.evaluations_id, 
  count(1) cnt,
--  sum(r1.betcnt) betcnt, 
  (sum(r1.betcnt)::float/180)::numeric(5,1) dailybet,
  ( (sum(r1.betcnt)::float/450) * (sum(r1.hitcnt)::float / sum(r1.betcnt)::float) )::numeric(5,1) dailyhit,
  sum(r1.hitcnt) hitcnt,
  sum(r1.betamt) betamt,
  sum(r1.hitamt) hitamt,
  sum(r1.hitamt-r1.betamt) incamt,
  (sum(r1.hitamt-r1.betamt)::float / sum(r1.betcnt))::numeric(5,2) betinc,
  (sum(r1.hitcnt)::float / sum(r1.betcnt)::float)::numeric(5,2) hitrate,
  (sum(r1.hitamt)::float / sum(r1.betamt)::float)::numeric(5,2) incrate
from
  ( select * from ml_evaluation where evaluations_id = '555_1') t1
  , ( select * from ml_evaluation where evaluations_id = '555_2') t2
  , ( select * from ml_evaluation where evaluations_id = '555_3') t3
  , ( select * from ml_evaluation where evaluations_id = '555_4' ) r1
where 
  t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = t3.result_type and t1.bettype = t3.bettype and t1.kumiban = t3.kumiban and t1.modelno = t3.modelno and t1.patternid = t3.patternid and t1.pattern = t3.pattern
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
  and t1.incomerate > 0.6 and t2.incomerate > 0.6 and t3.incomerate > 0.6
  and t1.bal_pluscnt > 0 and t2.bal_pluscnt > 0 and t3.bal_pluscnt > 0
  and t1.bettype = '3T'
group by r1.bettype, r1.result_type, r1.modelno, r1.patternid, r1.evaluations_id
order by bettype, result_type, incrate desc
--order by r1.result_type, r1.bettype, r1.modelno, r1.patternid, r1.evaluations_id
;

select  
  r1.*
from
  ( select * from ml_evaluation where evaluations_id = '555_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '555_2'  ) t2
  , ( select * from ml_evaluation where evaluations_id = '555_3'  ) t3
  , ( select * from ml_evaluation where evaluations_id = '555_4' ) r1
where 
  t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = t3.result_type and t1.bettype = t3.bettype and t1.kumiban = t3.kumiban and t1.modelno = t3.modelno and t1.patternid = t3.patternid and t1.pattern = t3.pattern
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
  and t1.incomerate > 1 and t2.incomerate > 1 and t3.incomerate > 1
;


select 
  result_type, bettype, patternid, evaluations_id, 
  count(1) cnt,
  sum(betcnt) betcnt, 
  sum(hitcnt) hitcnt,
  sum(betamt) betamt,
  sum(hitamt) hitamt,
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(5,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(5,2) incrate
from ml_evaluation me 
where 
  bettype = '3T' 
  and modelno = '99100'
--  and substring(evaluations_id from 1 for 2) = '77'
group by result_type, bettype, patternid, evaluations_id 
order by result_type, bettype, patternid, evaluations_id
;


select count(1) from ml_evaluation;


alter table ml_bork_evaluation alter column evaluations_id type varchar(255);

select 
  modelno, evaluations_id, count(1) 
from ml_evaluation me
group by modelno, evaluations_id
order by evaluations_id, modelno
;

select modelno, min(ymd), max(ymd) from ml_classification mc 
group by modelno;


select 
  r.betcnt, r.kumiban, (r.hitamt - r.betamt) incamt
from ( select * from ml_evaluation where evaluations_id = '10_3' ) r,
(
select 
  '~' sel, '1' result_type, 
  (case when t2.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
  t2.bettype, t2.kumiban, t2.modelno, t2.patternid, t2.pattern, 
  t2.betcnt, (t2.hitamt - t2.betamt) incamt, t2.hitrate, t2.incomerate,
  'x' bonus_pr,  'x' bonus_bor,  'x' bonus_bork, 'x' range_selector, 'x' bonus_borkbor
from 
  ( select * from ml_evaluation where evaluations_id = '10_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '10_2' ) t2
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t2.result_type = '1' and t2.bettype = '3T' and t2.modelno = '79100' and t2.patternid = 'wk12+jyo'
--  and r1.result_type = '{result_result_type}' r1.bettype = '{bettype}' and r1.modelno = '{modelno} 'and r1.patternid = '{patternid}'
--  and t1.{factor} between {factor_min} and {factor_max}
--  and t2.{factor} between {factor_min} and {factor_max}
order by grades, bettype, kumiban, modelno, patternid, pattern
) t
where 
  t.result_type= r.result_type  and t.bettype = r.bettype and t.kumiban = r.kumiban and t.modelno = r.modelno and t.patternid = r.patternid and t.pattern = r.pattern
;


copy (
select  r1.*  from
  ( select * from ml_evaluation where evaluations_id = '5_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '5_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '5_3' ) r1
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
union 
select  r2.*  from
  ( select * from ml_evaluation where evaluations_id = '5_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '5_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '5_4' ) r2
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r2.result_type and t1.bettype = r2.bettype and t1.kumiban = r2.kumiban and t1.modelno = r2.modelno and t1.patternid = r2.patternid and t1.pattern = r2.pattern
union
select  r1.*  from
  ( select * from ml_evaluation where evaluations_id = '10_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '10_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '10_3' ) r1
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
union 
select  r2.*  from
  ( select * from ml_evaluation where evaluations_id = '10_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '10_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '10_4' ) r2
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r2.result_type and t1.bettype = r2.bettype and t1.kumiban = r2.kumiban and t1.modelno = r2.modelno and t1.patternid = r2.patternid and t1.pattern = r2.pattern
union
select  r1.*  from
  ( select * from ml_evaluation where evaluations_id = '15_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '15_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '15_3' ) r1
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
union 
select  r2.*  from
  ( select * from ml_evaluation where evaluations_id = '15_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '15_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '15_4' ) r2
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r2.result_type and t1.bettype = r2.bettype and t1.kumiban = r2.kumiban and t1.modelno = r2.modelno and t1.patternid = r2.patternid and t1.pattern = r2.pattern
union
select  r1.*  from
  ( select * from ml_evaluation where evaluations_id = '20_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '20_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '20_3' ) r1
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
union 
select  r2.*  from
  ( select * from ml_evaluation where evaluations_id = '20_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '20_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '20_4' ) r2
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r2.result_type and t1.bettype = r2.bettype and t1.kumiban = r2.kumiban and t1.modelno = r2.modelno and t1.patternid = r2.patternid and t1.pattern = r2.pattern
union
select  r1.*  from
  ( select * from ml_evaluation where evaluations_id = '12_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '12_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '12_3' ) r1
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r1.result_type and t1.bettype = r1.bettype and t1.kumiban = r1.kumiban and t1.modelno = r1.modelno and t1.patternid = r1.patternid and t1.pattern = r1.pattern
union 
select  r2.*  from
  ( select * from ml_evaluation where evaluations_id = '12_1' ) t1
  , ( select * from ml_evaluation where evaluations_id = '12_2' ) t2
  , ( select * from ml_evaluation where evaluations_id = '12_4' ) r2
where 
    t1.result_type = t2.result_type and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.result_type = r2.result_type and t1.bettype = r2.bettype and t1.kumiban = r2.kumiban and t1.modelno = r2.modelno and t1.patternid = r2.patternid and t1.pattern = r2.pattern
) to 'D:\Dev\experiment\expr10\analysis_st2\2term_1.tsv' csv delimiter E'\t' header encoding 'UTF-8'
;

select substring(probability1::text from 1 for 3) || '-' || substring(probability2::text from 1 for 3) || '-' || substring(probability3::text from 1 for 3), count(1)
from rec_race ;


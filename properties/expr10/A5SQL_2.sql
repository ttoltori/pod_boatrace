insert into rec_racer2 
  select ymd, jyocd, raceno, generate_series(1, array_length(runcnt, 1)) AS waku,
    unnest(runcnt), unnest(runcnt_slope), unnest(cond), unnest(cond_slope), 
    unnest(n1point), unnest(n1point_slope), unnest(n2point), unnest(n2point_slope), unnest(n3point), unnest(n3point_slope), 
    unnest(n1point_waku), unnest(n1point_waku_slope), unnest(n2point_waku), unnest(n2point_waku_slope), unnest(n3point_waku), unnest(n3point_waku_slope), 
    unnest(avgstart_waku), unnest(avgstart_waku_slope)
  from rec_racer_arr2
;

  
select *
  from rec_racer2
  where ymd = '20090227' and jyocd = '15' and raceno = 9;

select 
 ymd, jyocd, raceno,  unnest(runcnt)
from rec_racer_arr2
where ymd = '20090221' and jyocd = '02' and raceno = 1; 

select model, ymd from

select min(probability1) min1, max(probability1) max1,
	 min(probability2) min2,  max(probability2)  max2,
	 min(probability3) min3,  max(probability3)  max3,
	 min(probability4) min4,  max(probability4)  max4,
	 min(probability5) min5,  max(probability5)  max5,
	 min(probability6) min6,  max(probability6)  max6
from ml_classification mc;  where modelno = '89100';

select min(probability1)::numeric(5,2) min1, max(probability1)::numeric(5,2) max1,
   min(probability1+probability2)::numeric(5,2) min12,  max(probability1+probability2)::numeric(5,2) max12,
   min(probability1+probability2+probability3)::numeric(5,2) min123,  max(probability1+probability2+probability3)::numeric(5,2) max123
from ml_classification mc;  where modelno = '89100';

select  min(popu_variance), max (popu_variance) from ml_classification mc  where modelno = '89100';

select * from ml_classification order by ymd, jyocd, raceno, modelno;



DELETE FROM ml_classification;

copy ml_classification from 'C:\jsj\!japan_backup\Backup\DB\ml_classification.tsv' csv delimiter E'\t' header;
;

select concat_ws('_', race.ymd, race.jyocd, lpad(race.raceno::text, 2, '0')) raceid,
race.jyocd,race.raceno::text,turn::text,race.grade,race.racetype::text,fixedentrance,entry::text en,sex sex,age::text age,level lv,weight::text weight,branch branch,exhibit::text exhibit,
(rank::text) classes
from rec_race race, rec_racer racer
where race.ymd = racer.ymd and race.jyocd = racer.jyocd and race.raceno = racer.raceno
and sanrentanno <> '不成立'
and race.grade in ('ip', 'G3', 'G2', 'G1', 'SG')
and race.ymd >= '20160602' and race.ymd <= '20210601'
and (true)
order by race.ymd, race.sime

;



select max(ymd) from rec_race;

truncate rec_race;
truncate rec_race_waku;
truncate rec_race_waku2;
truncate rec_racer;
truncate rec_racer_arr;
truncate ml_classification;


select modelno, min(ymd) from ml_classification mc group by modelno order by modelno;

select sanrentanno, count(1) cnt
from rec_race where sanrentanno <> '不成立'
group by sanrentanno 
order by cnt desc;

select distinct modelno from ml_classification mc; 
select count(1) from ml_evaluation;

copy ml_model_config from 'C:\jsj\!japan_backup\Backup\DB\rec_race_waku.tsv' csv delimiter E'\t';


copy ( select * from rec_race ) to 'C:\Dev\export\20240117\rec_race.tsv' csv delimiter E'\t' header;
copy ( select * from rec_race_waku ) to 'C:\Dev\export\20240117\rec_race_waku.tsv' csv delimiter E'\t' header;
copy ( select * from rec_race_waku2 ) to 'C:\Dev\export\20240117\rec_race_waku2.tsv' csv delimiter E'\t' header;
copy ( select * from rec_racer ) to 'C:\Dev\export\20240117\rec_racer.tsv' csv delimiter E'\t' header;
copy ( select * from rec_racer_arr ) to 'C:\Dev\export\20240117\rec_racer_arr.tsv' csv delimiter E'\t' header;
copy ( select * from ml_classification ) to 'C:\Dev\export\20240117\ml_classification.tsv' csv delimiter E'\t' header;

select count(1) from ml_evaluation;

select max(ymd) from ml_classification mc where modelno = '99100';

create database boatml2 owner postgres encoding 'UTF8';


create table ml_eval_6664 (
resultno varchar(6),
modelno varchar(5),
patternid varchar(20),
pattern varchar(200),
bettype varchar(30),
kumiban varchar(30),
betcnt int,
hitcnt int,
betamt int,
hitamt int,
betrate double precision,
hitrate double precision,
incomerate double precision,
hmeanrate double precision,        -- betrate,hitrate,incomerateの調和平均（総合性能指標）2002/2/23 追加
balance int[],                     -- 区間毎の残高評価
bal_slope double precision[],
betr_slope double precision,       -- 投票率変化推移
hitr_slope double precision,       -- 的中率変化推移
incr_slope double precision,       -- 収益率変化推移
pt_precision double precision,     -- MLのconfusion matrix評価 (bettype,kumiban,patternの組み合わせ）
pt_recall double precision,
pt_fmeasure double precision,
-- 的中オッズの記述統計量
hodds_min double precision,
hodds_max double precision,
hodds_mean double precision,
hodds_stddev double precision,
hodds_median double precision, 
-- 的中オッズRANKINGの記述統計量
hoddsrk_min double precision,
hoddsrk_max double precision,
hoddsrk_mean double precision,
hoddsrk_stddev double precision,
hoddsrk_median double precision, 
-- 確定オッズの記述統計量
rodds_min double precision,
rodds_max double precision,
rodds_mean double precision,
rodds_stddev double precision,
rodds_median double precision,
-- 確定オッズRANKINGの記述統計量
roddsrk_min double precision,
roddsrk_max double precision,
roddsrk_mean double precision,
roddsrk_stddev double precision,
roddsrk_median double precision,
-- 直前オッズ記述統計量
bodds_min double precision,
bodds_max double precision,
bodds_mean double precision,
bodds_stddev double precision,
bodds_median double precision, 
-- 直前オッズRANKING記述統計量
boddsrk_min double precision,
boddsrk_max double precision,
boddsrk_mean double precision,
boddsrk_stddev double precision,
boddsrk_median double precision, 
-- 予想確率の記述統計量
prob_min double precision,
prob_max double precision,
prob_mean double precision,
prob_stddev double precision,
prob_median double precision,
-- 確定オッズの最適値計算情報
ror_bestmin double precision,       -- 黒字となった最適範囲min
ror_bestmax double precision,       -- 黒字となった最適範囲max
ror_betcnt int,                     -- 最適範囲内のデータ数
ror_betamt int,                     -- 最適範囲内のbet金額
ror_hitcnt int,                     -- 最適範囲内の的中したデータ数
ror_hitamt int,                     -- 最適範囲内の的中金額合計
ror_betrate double precision,       -- range_cnt / betcnt
ror_hitrate double precision,       -- range_hitcnt / range_betcnt
ror_incomerate double precision,    -- range_hitamt / range_betamt
-- 確定オッズRANKINGの最適値計算情報
rork_bestmin double precision,       -- 黒字となった最適範囲min
rork_bestmax double precision,       -- 黒字となった最適範囲max
rork_betcnt int,                     -- 最適範囲内のデータ数
rork_betamt int,                     -- 最適範囲内のbet金額
rork_hitcnt int,                     -- 最適範囲内の的中したデータ数
rork_hitamt int,                     -- 最適範囲内の的中金額合計
rork_betrate double precision,       -- range_cnt / betcnt
rork_hitrate double precision,       -- range_hitcnt / range_betcnt
rork_incomerate double precision,    -- range_hitamt / range_betamt
-- 直前オッズの最適値計算情報
bor_bestmin double precision,       -- 黒字となった最適範囲min
bor_bestmax double precision,       -- 黒字となった最適範囲max
bor_betcnt int,                     -- 最適範囲内のデータ数
bor_betamt int,                     -- 最適範囲内のbet金額
bor_hitcnt int,                     -- 最適範囲内の的中したデータ数
bor_hitamt int,                     -- 最適範囲内の的中金額合計
bor_betrate double precision,       -- range_cnt / betcnt
bor_hitrate double precision,       -- range_hitcnt / range_betcnt
bor_incomerate double precision,    -- range_hitamt / range_betamt
-- 直前オッズRANKINGの最適値計算情報
bork_bestmin double precision,       -- 黒字となった最適範囲min
bork_bestmax double precision,       -- 黒字となった最適範囲max
bork_betcnt int,                     -- 最適範囲内のデータ数
bork_betamt int,                     -- 最適範囲内のbet金額
bork_hitcnt int,                     -- 最適範囲内の的中したデータ数
bork_hitamt int,                     -- 最適範囲内の的中金額合計
bork_betrate double precision,       -- range_cnt / betcnt
bork_hitrate double precision,       -- range_hitcnt / range_betcnt
bork_incomerate double precision,    -- range_hitamt / range_betamt
-- 予想確率の最適値計算情報
pr_bestmin double precision,       -- 黒字となった最適範囲min
pr_bestmax double precision,       -- 黒字となった最適範囲max
pr_betcnt int,                     -- 最適範囲内のデータ数
pr_betamt int,                     -- 最適範囲内のbet金額
pr_hitcnt int,                     -- 最適範囲内の的中したデータ数
pr_hitamt int,                     -- 最適範囲内の的中金額合計
pr_betrate double precision,       -- range_cnt / betcnt
pr_hitrate double precision,       -- range_hitcnt / range_betcnt
pr_incomerate double precision,    -- range_hitamt / range_betamt
bal_pluscnt int,                   -- 黒字の基数
result_type varchar(10),           -- 実験のタイプ 
evaluations_id varchar(255),         -- simulationの場合のみ、関連group no
bonus_pr varchar(30),
bonus_bor varchar(30),
bonus_bork varchar(30),
-- TOP1-3直前オッズRANKINGの最適値計算情報
topbork_bestmin double precision,       -- 黒字となった最適範囲min
topbork_bestmax double precision,       -- 黒字となった最適範囲max
topbork_betcnt int,                     -- 最適範囲内のデータ数
topbork_betamt int,                     -- 最適範囲内のbet金額
topbork_hitcnt int,                     -- 最適範囲内の的中したデータ数
topbork_hitamt int,                     -- 最適範囲内の的中金額合計
topbork_betrate double precision,       -- range_cnt / betcnt
topbork_hitrate double precision,       -- range_hitcnt / range_betcnt
topbork_incomerate double precision    -- range_hitamt / range_betamt
);

drop index if exists indexes_ml_eval_6664;
create index indexes_ml_eval_6664 on ml_eval_6664 (resultno, modelno, patternid, pattern, bettype, kumiban, result_type);
drop index if exists indexes_ml_eval_6664_evaluationsid;
create index indexes_ml_eval_6664_evaluationsid on ml_eval_6664 (evaluations_id);


delete from ml_evaluation where resultno::int between 273965 and 274004;
delete from ml_bork_evaluation where resultno::int between 273965 and 274004;
delete from ml_pr_evaluation where resultno::int between 273965 and 274004 ;
delete from ml_range_evaluation where resultno::int between 273965 and 274004 ;
delete from ml_term_evaluation where resultno::int between 273965 and 274004 ;
  

select 
  substring(wakulevellist from 1 for 5) || '-' || race.turn || '-' || race.raceno  ptn, 
  count(1) cnt
from rec_race race group by ptn order by cnt desc;


select count(1) from ml_result;

select 
  resultno, sum(betrate), sum(betcnt), sum(hitamt-betamt)
from ml_evaluation me 
where resultno::int in (9001,9002)
group by resultno
;

select 
  sum(betrate), sum(betcnt), sum(hitamt-betamt)
from ml_evaluation me 
where evaluations_id = '666_3' and modelno = '99100'
  and bettype = '3T' and kumiban = '123'
  and patternid = 'wk12+jyo'
--group by patternid 
;

select count(1)
from ml_classification mc 
where prediction1 = '1' and prediction2 = '2' and prediction3 = '3'
 and modelno = '99100'
 and ymd::int between 20220601 and 20221130
;





select min(ymd), max(ymd) from ml_classification mc where modelno = '79100';

select * from ml_evaluation me where resultno::int = 274155;

select distinct incomerate from ml_evaluation me 
where bettype = '1T' and kumiban = '1' and incomerate > 0.89;


select count(1) from ml_evaluation me;

-- evaluation 
select
  -- 'none' grp,
  ev.bettype, ev.kumiban, 
  ev.resultno, ev.modelno, ev.patternid, ev.pattern, 
  mc.features_rank1 || '::' || mc.features_rank2 || '::' || mc.features_rank3 features123,
  ev.betcnt, ev.betamt, ev.hitcnt, ev.hitamt, ev.betrate, (ev.hitamt - ev.betamt) incamt, ev.hitrate, ev.incomerate, 
  -- probability range
  range_betcnt best_betcnt, range_betrate best_betrate, range_worstmin worst_min, range_worstmax worst_max, range_bestmin best_min, range_bestmax best_max,
  -- hitodds range
  rangeodds_betcnt bestodds_betcnt, rangeodds_betrate bestodds_betrate, rangeodds_worstmin worstodds_min, rangeodds_worstmax worstodds_max, rangeodds_bestmin bestodds_min, rangeodds_bestmax bestodds_max,
  -- hitodds stat  
  hitodds_median, hitodds_mean, hitodds_stddev, hitodds_min, hitodds_max, 
  ev.balance[0], ev.balance[1],ev.balance[2],ev.bal_slope[0],ev.bal_slope[1],ev.bal_slope[2]
from ml_evaluation ev, ml_model_config mc 
where ev.modelno = mc.modelno
  and ev.bettype = '1T' and ev.kumiban = '1'  and ev.resultno::int between 731 and 59999  --1,2,단계모두
    and ( balance[0] > 98000 and  balance[1] > 98000 and balance[2] > 98000 )
   and (ev.hitodds_mean - ev.hitodds_stddev) > 1
   and ev.range_betrate > 0.5 
   and ev.betcnt > 1000
   --and (bal_slope[0] > -2 and  bal_slope[1] > -2 and bal_slope[2] > -2)
   --and ev.hitodds_max < 10
  -- and (bal_slope[0] > 5 and  bal_slope[1] > 5 and bal_slope[2] > 5)
  -- and ev.betrate >= 0.005
   -- and (balance[0] > 105000 and  balance[1] > 105000 and balance[2] > 105000)
   --and ev.range_betrate > 0.5
  --and ev.patternid = 'probr1-1dig'
  --and probability_median between 0.8 and 1
  --and ev.betcnt > 5000
  -- and ev.hitrate > 0.5
   --and ev.betcnt >= 10000
   -- and ev.incomerate > 1
   --and (ev.range_worstmax < ev.range_bestmin)
    -- and (balance[0] + balance[1] + balance[2]) > 330000
  -- and (bal_slope[0] > 5 and  bal_slope[1] > 5 and bal_slope[2] > 5)
  -- and ev.hitodds_max < 50
  -- and pt_fmeasure >= 0.5
  -- and ev.hitrate between 0.4 and 0.79
  --and ev.hitrate >= 0.8
  -- and (ev.hitodds_mean - ev.hitodds_stddev) >= 0
  -- and ev.hitodds_stddev < 3
    -- and ev.hitodds_max < 50
  --and ev.incomerate >= 1.05 and ev.betcnt >= 1000
  -- and ev.hitrate >= 0.8 and ev.betcnt >= 1000
   --and ev.betrate >= 0.05
  -- and (balance[0] > 100000 and  balance[1] > 100000 and balance[2] > 100000)
  --and (ev.hitamt - ev.betamt) > 0
  --and ev.betrate >= 0.1 
  --and ev.hitrate > 0.8
  -- and (balance[0] > 105000 and  balance[1] > 105000 and balance[2] > 105000)
  -- and patternid not like 'waku%'  
   --and mc.algorithm_rank1 = 'cf_lgbm-1_py'
--  and ev.pattern not like '%-A1%' 
--  and ev.hitrate::float >= 0.8
  -- and (ev.hitamt - ev.betamt) > 0
  -- and (balance[0] > 100000 and  balance[1] > 100000 and balance[2] > 100000)
  -- and mc.algorithm_rank1 LIKE 'cf_bayesnet%'
  -- and mc.features_rank1 like 'nw_%'
  -- and mc.features_rank1 in ('nw_ext_8', 'nw_ext_9')
--  and mc.modelno::int in (99065,99066,99067)
--  and (rc.resultno::int between 543 and 999 )
--  and (ev.hitamt - ev.betamt) > 0 and ev.betcnt > 50
  -- and rc.pattern_id = 'nopattern'
  -- and ev.bettype = '3T'
order by 
--  ev.bettype, 
--  ev.kumiban, 
--  betcnt desc,
  --ev.range_betrate desc,
  ev.modelno, 
  ev.patternid,
  ev.pattern,
  (ev.probability_median-ev.hitrate),
  ev.betcnt desc,
  ev.resultno::int 
;

  -- mc.algorithm_rank1 || '::' || mc.algorithm_rank2 || '::' || mc.algorithm_rank3 algorithm123,
  -- probability_mean prob_mean, probability_median prob_medi,  
  -- ev.hmeanrate, range_hmeanrate range_hmeanrate,
  -- probability_median prob_med, probability_mean prob_mean, probability_stddev prob_stddev, probability_min prob_min, probability_max prob_max, 
  -- inc_mean, inc_stddev, inc_min, inc_max,  pt_precision, pt_recall, pt_fmeasure 


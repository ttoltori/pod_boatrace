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

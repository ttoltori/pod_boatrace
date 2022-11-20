select kumiban, count(*) cnt
from rec_race race, stat_ml_result ml
where 
  race.ymd >= '20160101' and race.ymd <= '20171231'
  and race.ymd = ml.ymd and race.jyocd = ml.jyocd and race.raceno = ml.raceno
  and race.tansyono = ml.kumiban
  and race.tansyoprize > 100
group by kumiban
order by kumiban
;

select kumiban, count(*) cnt
from rec_race race, stat_ml_result ml
where 
  race.ymd >= '20160101' and race.ymd <= '20171231'
  and race.ymd = ml.ymd and race.jyocd = ml.jyocd and race.raceno = ml.raceno
  and race.tansyoprize > 250 and race.tansyoprize <550
group by kumiban
order by kumiban
;



select substring(ymd from 1 for 4) y, count(*) from rec_racer 
group by y;

insert into stat_deviation
select ymd, jyocd, raceno, 
  stddev_pop(devi_famewin1) devi_famewin1,
  stddev_pop(devi_famewin2) devi_famewin2,
  stddev_pop(devi_famewin3) devi_famewin3,
  stddev_pop(devi_famerank1) devi_famerank1,
  stddev_pop(devi_famerank2) devi_famerank2,
  stddev_pop(devi_famerank3) devi_famerank3,
  stddev_pop(devi_winingrate_12) devi_winingrate_12,
  stddev_pop(devi_winingrate_23) devi_winingrate_23,
  stddev_pop(devi_winingrate_14) devi_winingrate_14,
  stddev_pop(devi_winingrate_123) devi_winingrate_123,
  stddev_pop(devi_winingrate_123456) devi_winingrate_123456
from 
  (
  select waku2.ymd, waku2.jyocd, waku2.raceno,
    unnest(array[win1_rank1_famerate, win1_rank2_famerate, win1_rank3_famerate]) as devi_famewin1, 
    unnest(array[win2_rank1_famerate, win2_rank2_famerate, win2_rank3_famerate]) as devi_famewin2, 
    unnest(array[win3_rank1_famerate, win3_rank2_famerate, win3_rank3_famerate]) as devi_famewin3, 
    unnest(array[win1_rank1_famerate, win2_rank1_famerate, win3_rank1_famerate]) as devi_famerank1, 
    unnest(array[win1_rank2_famerate, win2_rank2_famerate, win3_rank2_famerate]) as devi_famerank2, 
    unnest(array[win1_rank3_famerate, win2_rank3_famerate, win3_rank3_famerate]) as devi_famerank3, 
    unnest(array[nationwiningrate1, nationwiningrate2]) as devi_winingrate_12, 
    unnest(array[nationwiningrate2, nationwiningrate3]) as devi_winingrate_23, 
    unnest(array[nationwiningrate1, nationwiningrate4]) as devi_winingrate_14, 
    unnest(array[nationwiningrate1, nationwiningrate2, nationwiningrate3]) as devi_winingrate_123,
    unnest(array[nationwiningrate1, nationwiningrate2, nationwiningrate3, nationwiningrate4, nationwiningrate5, nationwiningrate6]) as devi_winingrate_123456
  from rec_race_waku2 waku2, rec_fame_rank3 rank3 
  where waku2.ymd = rank3.ymd and waku2.jyocd = rank3.jyocd and waku2.raceno = rank3.raceno
  ) tmp
group by ymd, jyocd, raceno
order by ymd, jyocd, raceno
;


delete from rec_waku_recent;
delete from stat_waku_recent;
delete from stat_waku_win;
delete from stat_race;


select count(*) cnt from rec_racer where jyocd = '1' and entry = 

select avg(alevelcount) from rec_race where racetype in ('優勝戦'); 5.2646003262642741
select avg(alevelcount) from rec_race where racetype in ('準優勝戦', '準優勝'); 4.454804535035004
select avg(alevelcount) from rec_race where racetype in ('予選'); 2.4153585318556556
select avg(alevelcount) from rec_race where racetype in ('一般戦', '一般'); 2.1493885036820781
select avg(alevelcount) from rec_race where racetype in ('予選特選', '予選特賞'); 4.0436848140665101
select avg(alevelcount) from rec_race where racetype like '選抜'; 3.5982658959537572
select avg(alevelcount) from rec_race where racetype in ('特選', '特賞'); 4.2361232885951305

select substring(ymd from 1 for 4) yyyy, count(*) from rec_race group by yyyy;
select avg(cnt) from 
(select entry, count(*) cnt from rec_racer where ymd >= '20160101' and ymd <= '20161231' group by entry
) tmp;


select nirenhukuno, count(nirenhukuno) from rec_race group by nirenhukuno;


update rec_race set isVenus = 'N' where isVenus = '';
update rec_race set timezone = 'Z' where timezone = '';

select 
	( case 
	  when racetype = '予選' then '予選'
	  when racetype in ('一般戦', '一般') then '一般'
	  when racetype in ('準優勝戦', '準優勝') then '準優勝戦'
	  when racetype = '優勝戦' then '優勝戦'
	  when racetype in ('予選特選', '予選特賞') then '予選特'
	  when racetype like '%選抜%' then '選抜'
	  when racetype in ('特選', '特賞') then '特'
	  else 'else' end ) type1, 
	  count(*) cnt
from rec_race
group by type1 order by cnt desc, type1;

select distinct racetype, count(*) cnt  from rec_race group by racetype order by cnt desc, racetype;

copy (
select *, ( case nirenhukuno when '12' then '12' else 'not12' end ) winner
from 
(
select 
  race.*, waku.*, waku2.*, odds.*, oddsrank.*, famewaku.*, famerank.*
from rec_race race, rec_race_waku waku, rec_race_waku2 waku2, rec_odds odds, rec_odds_rank10 oddsrank, rec_fame_waku famewaku, rec_fame_rank3 famerank
where race.ymd >= '20120101' and race.ymd <= '20161231'
  and race.ymd = waku.ymd and race.jyocd = waku.jyocd and race.raceno = waku.raceno
  and race.ymd = waku2.ymd and race.jyocd = waku2.jyocd and race.raceno = waku2.raceno
  and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno and odds.bettype = '2F' and odds.kumiban = '12'
  and race.ymd = oddsrank.ymd and race.jyocd = oddsrank.jyocd and race.raceno = oddsrank.raceno and oddsrank.bettype = '2F'
  and race.ymd = famewaku.ymd and race.jyocd = famewaku.jyocd and race.raceno = famewaku.raceno
  and race.ymd = famerank.ymd and race.jyocd = famerank.jyocd and race.raceno = famerank.raceno
  and (
    odds.odds >= 5 and odds.odds <= 6.5
  )
) tmp
) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\results\20181104_test\tmp.csv' with csv;

copy (

select 
  0, race.*, waku.*, waku2.*, odds.*, oddsrank.*, famewaku.*, famerank.*
from rec_race race, rec_race_waku waku, rec_race_waku2 waku2, rec_odds odds, rec_odds_rank10 oddsrank, rec_fame_waku famewaku, rec_fame_rank3 famerank
where race.ymd >= '20120101' and race.ymd <= '20161231'
  and race.ymd = waku.ymd and race.jyocd = waku.jyocd and race.raceno = waku.raceno
  and race.ymd = waku2.ymd and race.jyocd = waku2.jyocd and race.raceno = waku2.raceno
  and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno and odds.bettype = '2F' and odds.kumiban = '12'
  and race.ymd = oddsrank.ymd and race.jyocd = oddsrank.jyocd and race.raceno = oddsrank.raceno and oddsrank.bettype = '2F'
  and race.ymd = famewaku.ymd and race.jyocd = famewaku.jyocd and race.raceno = famewaku.raceno
  and race.ymd = famerank.ymd and race.jyocd = famerank.jyocd and race.raceno = famerank.raceno
  and (
    odds.odds >= 5 and odds.odds <= 6.5
  )
order by random() limit 10000
) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\results\20181104_test\rec_all_12_10000.csv' with csv;

;
group by nirenhukuno order by nirenhukuno;



select nirenhukuno, count(nirenhukuno), min(nirenhukuprize) min , max(nirenhukuprize) max, avg(nirenhukuprize) avg, 
percentile_disc(0.5) within group (order by nirenhukuprize) center, -- 中央値
mode() within group (order by nirenhukuprize) mod, --最頻値
percentile_disc(0.25) within group (order by nirenhukuprize) mod35, -- 2分位値
percentile_disc(0.75) within group (order by nirenhukuprize) mod75, -- 3分位値
stddev (nirenhukuprize) deviation, -- 標準偏差
variance (nirenhukuprize) vari -- 分散標本
from rec_race race
where race.ymd >= '20120101' and race.ymd <= '20161231'
group by nirenhukuno order by nirenhukuno;


select nirenhukuno, count(nirenhukuno), avg(nirenhukuprize)

select 
  betamount, income, balance, 
  cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(5,2)) hitrate,
  cast( (cast(income as float)/ cast(betamount as float) *100) as numeric(5,2)) incomerate,
  (betcnt / 365) dayilbet 
from (
select 
  sum ( case nirenhukuno when '12' then 100 else 100 end ) betamount,
  sum ( case nirenhukuno when '12' then nirenhukuprize else 0 end ) income,
  sum ( case nirenhukuno when '12' then 1 else 1 end ) betcnt,
  sum ( case nirenhukuno when '12' then 1 else 0 end ) hitcnt,
  sum ( case nirenhukuno when '12' then nirenhukuprize - 100 else -100 end ) balance
from rec_race race
  , rec_fame_rank3 famerank
where race.ymd >= '20170101' and race.ymd <= '20171231'
  and race.ymd = famerank.ymd and race.jyocd = famerank.jyocd and race.raceno = famerank.raceno
  and race.ymd = odds.ymd and race.jyocd = odds.jyocd and race.raceno = odds.raceno and odds.bettype = '2F' and odds.kumiban = race.nirenhukuno
--  and ( 
--      (win1_rank1_waku = '1' and win2_rank1_waku = '1') 
--     or (win1_rank1_waku = '2' and win2_rank1_waku = '2')
--  )
  and ( 
      (substring(nationwiningrank from 1 for 1) in ('1', '2')) 
    and (substring(nationwiningrank from 2 for 1) not in ('3','4','5','6'))
  )
) tmp;


copy (
select race.*
from rec_race race, rec_race_waku waku, rec_odds_rank10 oddsrank, rec_fame_waku famewaku, rec_fame_rank3 famerank
where race.ymd <= '20161231'
  and race.ymd = waku.ymd and race.jyocd = waku.jyocd and race.raceno = waku.raceno
  and race.ymd = oddsrank.ymd and race.jyocd = oddsrank.jyocd and race.raceno = oddsrank.raceno and oddsrank.bettype = '2F'
  and race.ymd = famewaku.ymd and race.jyocd = famewaku.jyocd and race.raceno = famewaku.raceno
  and race.ymd = famerank.ymd and race.jyocd = famerank.jyocd and race.raceno = famerank.raceno
  
  and ( 
      (substring(nationwiningrank from 1 for 1) in ('1', '2')) 
    and (substring(nationwiningrank from 2 for 1) not in ('3','4','5','6'))
  )
  
--group by nirenhukuno order by nirenhukuno;
order by race.ymd, race.sime
) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\results\20181104_test\rec_race_result.csv' with csv;

-- 5 or 6が含まれる2Fのレース
select * into rec_race_56 from rec_race where ymd <= '20161231' 
and (nirenhukuno like '%5' or nirenhukuno like '%6' or nirenhukuno like '56') ;

copy (select * from rec_race_12) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\results\20181104_test\rec_race_12.csv' with csv;
copy (select * from rec_race_13) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\results\20181104_test\rec_race_13.csv' with csv;
copy (select * from rec_race_56) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\results\20181104_test\rec_race_56.csv' with csv;



select * into rec_race_13 from rec_race where ymd <= '20161231' and nirenhukuno = '13';

select race.nirenhukuno, count(race.nirenhukuno), sum(nirenhukuprize) from rec_race 
where ymd <= '20161231' 
group by nirenhukuno order by nirenhukuno;

delete from rec_race;
delete from rec_racer;
delete from rec_race_waku;
delete from rec_race_waku2;

delete from rec_odds;
delete from rec_odds_rank10;
delete from rec_fame;
delete from rec_fame_waku;
delete from rec_fame_rank3;

drop table all_rec_race;
drop table all_rec_racer;
drop table all_rec_race_waku;
drop table all_rec_race_waku2;
drop table all_rec_odds;
drop table all_rec_odds_rank10;
drop table all_rec_fame;
drop table all_rec_fame_waku;
drop table all_rec_fame_rank3;


select distinct ymd from 
(
select ymd, (ymd || jyocd || raceno) keyfield, count((ymd || jyocd || raceno)) cnt 
from rec_fame_rank3
group by ymd, keyfield order by cnt desc, keyfield
) tmp
where tmp.cnt > 1
order by ymd
;



select race.nirenhukuno, count(race.nirenhukuno), sum(nirenhukuprize) 
from rec_fame_rank3 rank3, rec_race race, rec_race_waku 
where rank3.ymd >= '20120101' and rank3.ymd <= '20161231'
  and race.ymd = rank3.ymd and race.jyocd = rank3.jyocd and race.raceno = rank3.raceno
  and (
        (win1_rank1_waku = '1' and win2_rank1_waku = '2') or
        (win1_rank1_waku = '2' and win2_rank1_waku = '1') 
      )
group by race.nirenhukuno
;

select race.nirenhukuno, count(race.nirenhukuno), sum(nirenhukuprize)from rec_fame_waku famewaku, rec_race race 
where famewaku.ymd >= '20100101' and famewaku.ymd <= '20161231'
  and race.ymd = famewaku.ymd and race.jyocd = famewaku.jyocd and race.raceno = famewaku.raceno
  and (waku1_famerate - waku2_famerate) > 10 and (waku1_famerate - waku2_famerate) < 20 
group by race.nirenhukuno
;
-- 
select race.tansyono, sum(tansyoprize), count(race.tansyono) from rec_fame_waku famewaku, rec_race race 
where famewaku.ymd >= '20100101' and famewaku.ymd <= '20161231'
  and race.ymd = famewaku.ymd and race.jyocd = famewaku.jyocd and race.raceno = famewaku.raceno
  and waku1_famerate >= 99
group by race.tansyono


select count(*) from rec_fame_waku famewaku, rec_race race 
where famewaku.ymd >= '20100101' and famewaku.ymd <= '20161231'
  and race.ymd = famewaku.ymd and race.jyocd = famewaku.jyocd and race.raceno = famewaku.raceno
  and waku1_famerate >= 95 
  -- and race.tansyono = '1'



4. 着順別支持率90%以上のレース数
select count(*) from rec_fame_waku famewaku 
where famewaku.ymd >= '20100101' and famewaku.ymd <= '20161231'
  and waku1_famerate >= 91.51;

4. 着順別支持率90%
select win, 
percentile_disc(0.90) within group (order by famerate) median90 -- 3分位値
from rec_fame fame
where fame.ymd >= '20100101' and fame.ymd <= '20161231'
group by win order by win;

3. 着順別支持率分布
select win, min(famerate) min , max(famerate) max, avg(famerate) avg, 
percentile_disc(0.5) within group (order by famerate) median50, -- 中央値
mode() within group (order by famerate) modeval, --最頻値
percentile_disc(0.25) within group (order by famerate) median25, -- 2分位値
percentile_disc(0.75) within group (order by famerate) median75 -- 3分位値
from rec_fame fame
where fame.ymd >= '20100101' and fame.ymd <= '20161231'
group by win order by win;

2. 支持率分布
select min(famerate) min , max(famerate) max, avg(famerate) avg, 
percentile_disc(0.5) within group (order by famerate) median50, -- 中央値
mode() within group (order by famerate) modeval, --最頻値
percentile_disc(0.25) within group (order by famerate) median25, -- 2分位値
percentile_disc(0.75) within group (order by famerate) median75 -- 3分位値
from rec_fame fame
where fame.ymd >= '20100101' and fame.ymd <= '20161231';


from rec_fame fame;

select 
from rec_race race, rec_fame fame
where race.ymd >= '20100101' and race.ymd <= '20161231'
 and race.ymd = fame.ymd and race.jyocd = fame.jyocd and race.raceno = fame.raceno

 

1.ALLはバックアップだけして削除する。実戦では一般だけアップロードする
-- 全件を～allテーブルにコピー
select * into all_rec_race from rec_race;
select tbl2.* into all_rec_racer from rec_racer tbl2;
select tbl2.* into all_rec_race_waku from rec_race_waku tbl2;
select tbl2.* into all_rec_race_waku2 from rec_race_waku2 tbl2;
select tbl2.* into all_rec_odds from rec_odds tbl2;
select tbl2.* into all_rec_odds_rank10 from rec_odds_rank10 tbl2;
select tbl2.* into all_rec_fame from rec_fame tbl2;
select tbl2.* into all_rec_fame_waku from rec_fame_waku tbl2;
select tbl2.* into all_rec_fame_rank3 from rec_fame_rank3 tbl2;

-- SG級のデータを削除
delete from rec_fame_rank3 where (ymd || jyocd || raceno) in (select (ymd || jyocd || raceno) from rec_race race where race.grade in ('SG', 'G1', 'G2'));
delete from rec_fame_waku where (ymd || jyocd || raceno) in (select (ymd || jyocd || raceno) from rec_race race where race.grade in ('SG', 'G1', 'G2'));
delete from rec_fame where (ymd || jyocd || raceno) in (select (ymd || jyocd || raceno) from rec_race race where race.grade in ('SG', 'G1', 'G2'));
delete from rec_odds_rank10 where (ymd || jyocd || raceno) in (select (ymd || jyocd || raceno) from rec_race race where race.grade in ('SG', 'G1', 'G2'));
delete from rec_odds where (ymd || jyocd || raceno) in (select (ymd || jyocd || raceno) from rec_race race where race.grade in ('SG', 'G1', 'G2'));
delete from rec_race_waku2 where (ymd || jyocd || raceno) in (select (ymd || jyocd || raceno) from rec_race race where race.grade in ('SG', 'G1', 'G2'));
delete from rec_race_waku where (ymd || jyocd || raceno) in (select (ymd || jyocd || raceno) from rec_race race where race.grade in ('SG', 'G1', 'G2'));
delete from rec_racer where (ymd || jyocd || raceno) in (select (ymd || jyocd || raceno) from rec_race race where race.grade in ('SG', 'G1', 'G2'));
delete from rec_race where grade in ('SG', 'G1', 'G2');


-- データCSVバックアップ
copy (select * from rec_race) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\rec_race.csv' with csv;
copy (select * from rec_racer) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\rec_racer.csv' with csv;
copy (select * from rec_race_waku) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\rec_race_waku.csv' with csv;
copy (select * from rec_race_waku2) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\rec_race_waku2.csv' with csv;
copy (select * from rec_odds) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\rec_odds.csv' with csv;
copy (select * from rec_odds_rank10) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\rec_odds_rank10.csv' with csv;
copy (select * from rec_fame) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\rec_fame.csv' with csv;
copy (select * from rec_fame_waku) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\rec_fame_waku.csv' with csv;
copy (select * from rec_fame_rank3) to 'C:\Dev\workspace\Oxygen\pod_boatrace\src\statistics\boatstat\rec_fame_rank3.csv' with csv;

insert into rec_race_sg select * from rec_race where grade in ('SG', 'G1', 'G2');

select * from rec_race

select * into rec_race_all from rec_race where grade in ('SG', 'G1', 'G2');
select tbl2.* into rec_racer_all from rec_racer tbl2, rec_race race
 where race.ymd = tbl2.ymd and race.jyocd = tbl2.jyocd and race.raceno = tbl2.raceno and race.grade in ('SG', 'G1', 'G2');
select tbl2.* into rec_race_waku_all from rec_race_waku tbl2, rec_race race
 where race.ymd = tbl2.ymd and race.jyocd = tbl2.jyocd and race.raceno = tbl2.raceno and race.grade in ('SG', 'G1', 'G2');
select tbl2.* into rec_odds_all from rec_odds tbl2, rec_race race
 where race.ymd = tbl2.ymd and race.jyocd = tbl2.jyocd and race.raceno = tbl2.raceno and race.grade in ('SG', 'G1', 'G2');
select tbl2.* into rec_odds_rank10_all from rec_odds_rank10 tbl2, rec_race race
 where race.ymd = tbl2.ymd and race.jyocd = tbl2.jyocd and race.raceno = tbl2.raceno and race.grade in ('SG', 'G1', 'G2');
select tbl2.* into rec_fame_all from rec_fame tbl2, rec_race race
 where race.ymd = tbl2.ymd and race.jyocd = tbl2.jyocd and race.raceno = tbl2.raceno and race.grade in ('SG', 'G1', 'G2');
select tbl2.* into rec_fame_waku_all from rec_fame_waku tbl2, rec_race race
 where race.ymd = tbl2.ymd and race.jyocd = tbl2.jyocd and race.raceno = tbl2.raceno and race.grade in ('SG', 'G1', 'G2');
select tbl2.* into rec_fame_rank3_all from rec_fame_rank3 tbl2, rec_race race
 where race.ymd = tbl2.ymd and race.jyocd = tbl2.jyocd and race.raceno = tbl2.raceno and race.grade in ('SG', 'G1', 'G2');



from rec_race race, rec_fame_rank3 rank3, stat_deviation


select ntile(10) over (order by devi_winingrate_123456) devi_123456 
from 

copy (
select race.* from rec_race race, zen_filter zen
where race.ymd >= '20170309' and race.ymd <= '20180125'
  and (nirenhukuprize >= 250 and nirenhukuprize <= 500)
  and (zen.pattern = race.nationwiningrank)
) to 'Y:/test/tmp/rec_race_2F_12.csv' with csv;

select count(*) from rec_race race
where race.ymd >= '20090101' and race.ymd <= '20180124'
  and nirentanno = '12';
;


select 
  betamount, income, balance, 
  cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(5,2)) hitrate,
  cast( (cast(income as float)/ cast(betamount as float) *100) as numeric(5,2)) incomerate,
  (betcnt / 180) dayilbet
from (
	select 
--	jyocd,
	 sum ( case nirenhukuno when zen.kumiban then 100 else 100 end ) betamount,
	 sum ( case nirenhukuno when zen.kumiban then nirenhukuprize else 0 end ) income,
	 sum ( case nirenhukuno when zen.kumiban then 1 else 1 end ) betcnt,
	 sum ( case nirenhukuno when zen.kumiban then 1 else 0 end ) hitcnt,
	 sum ( case nirenhukuno when zen.kumiban then nirenhukuprize - 100 else -100 end ) balance
	from rec_race_ip race, zen_filter zen
	where race.ymd >= '20170725' and race.ymd <= '20180124'
	  and ( nationwiningrank ) = zen.pattern
--	group by jyocd
--	order by jyocd
) tmp;


select generate_result('20090101', '20170724', 'nirentan', 'rec_race_ip', 'jyocd || nationwiningrank', 'na', 'na');

select * into rec_race_ip from rec_race where grade in ('ip', 'G3');



select bettype, count(*) 
from stat_result_tmp
group by bettype;


---------------------------------
select * into stat_result_tmp 
from stat_result;

select count(*) from rec_race;
delete from rec_race where y < 2013;

select count(*) from rec_race_waku;
delete from rec_race_waku where y < 2013;

select count(*) from rec_racer;
delete from rec_racer where y < 2013;

copy rec_race from 'C:/Dev/workspace/Oxygen/pod_boatrace/tmp/rec_race_2013-2018.csv' with csv;
copy rec_racer from 'C:/Dev/workspace/Oxygen/pod_boatrace/tmp/rec_racer_2013-2018.csv' with csv;
copy rec_race_waku from 'C:/Dev/workspace/Oxygen/pod_boatrace/tmp/rec_race_waku_2013-2018.csv' with csv;


copy 
(
  select * from rec_race where y < 2013
) to 'C:/Dev/workspace/Oxygen/pod_boatrace/tmp/rec_race_2009-2012.csv' with csv;

copy 
(
  select * from rec_race_waku where y < 2013
) to 'C:/Dev/workspace/Oxygen/pod_boatrace/tmp/rec_racer_2009-2012.csv' with csv;

copy 
(
  select * from rec_racer where y < 2013
) to 'C:/Dev/workspace/Oxygen/pod_boatrace/tmp/rec_race_waku_2009-2012.csv' with csv;

-----------------------

select 
  tmp1.bettype,
  tmp1.totalhitcount,
  tmp1.totalincome,
  tmp1.avghitrate,
  tmp1.avgincomerate,
  tmp2.avghitcount,
  tmp2.totalincome,
  tmp2.avghitrate,
  tmp2.avgincomerate
from
  (
    select
      bettype, 
      sum(hitcount) totalhitcount,
      (sum(prize) - (sum(hitcount) * 100)) totalincome,
      cast(avg(hitrate) as numeric(5,2)) avghitrate, 
      cast(avg(incomerate) as numeric(5,2)) avgincomerate
    from stat_result_tmp
    group by bettype
  ) tmp1,
  (
    select
      bettype, 
      sum(hitcount) avghitcount,
      (sum(prize) - (sum(hitcount) * 100)) totalincome,
      cast(avg(hitrate) as numeric(5,2)) avghitrate , 
      cast(avg(incomerate) as numeric(5,2)) avgincomerate
    from stat_result_tmp
    where patterncnt >= 12
      and hitrate >= 0.15
    group by bettype
  ) tmp2
where
  tmp1.bettype = tmp2.bettype
order by bettype
;
-----------------------
select 
  tmp1.bettype,
  tmp1.totalhitcount,
  tmp1.totalincome,
  tmp1.avghitrate,
  tmp1.avgincomerate,
  tmp2.avghitcount,
  tmp2.totalincome,
  tmp2.avghitrate,
  tmp2.avgincomerate
from
  (
    select
      bettype, 
      sum(hitcount) totalhitcount,
      (sum(prize) - (sum(hitcount) * 100)) totalincome,
      cast(avg(hitrate) as numeric(5,2)) avghitrate, 
      cast(avg(incomerate) as numeric(5,2)) avgincomerate
    from stat_result
    group by bettype
  ) tmp1,
  (
    select
      bettype, 
      sum(hitcount) avghitcount,
      (sum(prize) - (sum(hitcount) * 100)) totalincome,
      cast(avg(hitrate) as numeric(5,2)) avghitrate , 
      cast(avg(incomerate) as numeric(5,2)) avgincomerate
    from stat_result
    where patterncnt > 10
      and (hitrate * incomerate) > 1
    group by bettype
  ) tmp2
where
  tmp1.bettype = tmp2.bettype
;

-----------------------------------

select y, nirentanno, count(nirentanno) cnt
from rec_race
group by y, nirentanno
order by y, cnt desc;

-- 01-03-01-05-06-05
select * from stat_result
where incomerate > 10 and patterncnt > 10
and hitrate > 0.1;

where substring(pattern from 13 for 2) = '01' or
substring(pattern from 16 for 2) = '01'; 

select * from stat_result 
where hitrate >= 1
and patterncnt >= 5;

select bettype, kumiban, pattern, hitrate  from stat_result
--group by bettype, kumiban, pattern, hitrate
order by bettype, kumiban, pattern, hitrate;

select bettype, count(*) from stat_result
group by bettype;


select count(*) from stat_race;

select count(*) from stat_pattern;

select count(*) from rec_race_waku;

select bettype, kumiban, pattern, count(*)
from stat_result
group by bettype, kumiban, pattern
order by bettype, kumiban, pattern;
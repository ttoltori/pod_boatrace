


select min(ymd), max(ymd), count(1) 
from ml_classification mc 
where modelno = '20017'
;

select max(ymd) from rec_race;


select
  grade, count(1)
from rec_race race
where ymd::int between 20210603 and 20230602
    and sanrentanno <> '不成立'
group by grade
;

select
	'nopattern' pattern,
	race.ymd,
	sanrentanprize::double precision,
	substring(race.ymd
from
	5 for 2) mm,
	race.jyocd,
	race.raceno::text,
	turn::text,
	race.grade,
	race.racetype::text,
	femalecount::text,
	alevelcount::text,
	timezone::text,
	fixedentrance,
	entry[1]::text entry1,
	entry[2]::text entry2,
	entry[3]::text entry3,
	entry[4]::text entry4,
	entry[5]::text entry5,
	entry[6]::text entry6,
	nationwiningrate[1]::text nationwiningrate1,
	nationwiningrate[2]::text nationwiningrate2,
	nationwiningrate[3]::text nationwiningrate3,
	nationwiningrate[4]::text nationwiningrate4,
	nationwiningrate[5]::text nationwiningrate5,
	nationwiningrate[6]::text nationwiningrate6,
	nation2winingrate[1]::text nation2winingrate1,
	nation2winingrate[2]::text nation2winingrate2,
	nation2winingrate[3]::text nation2winingrate3,
	nation2winingrate[4]::text nation2winingrate4,
	nation2winingrate[5]::text nation2winingrate5,
	nation2winingrate[6]::text nation2winingrate6,
	nation3winingrate[1]::text nation3winingrate1,
	nation3winingrate[2]::text nation3winingrate2,
	nation3winingrate[3]::text nation3winingrate3,
	nation3winingrate[4]::text nation3winingrate4,
	nation3winingrate[5]::text nation3winingrate5,
	nation3winingrate[6]::text nation3winingrate6,
	localwiningrate[1]::text localwiningrate1,
	localwiningrate[2]::text localwiningrate2,
	localwiningrate[3]::text localwiningrate3,
	localwiningrate[4]::text localwiningrate4,
	localwiningrate[5]::text localwiningrate5,
	localwiningrate[6]::text localwiningrate6,
	local2winingrate[1]::text local2winingrate1,
	local2winingrate[2]::text local2winingrate2,
	local2winingrate[3]::text local2winingrate3,
	local2winingrate[4]::text local2winingrate4,
	local2winingrate[5]::text local2winingrate5,
	local2winingrate[6]::text local2winingrate6,
	local3winingrate[1]::text local3winingrate1,
	local3winingrate[2]::text local3winingrate2,
	local3winingrate[3]::text local3winingrate3,
	local3winingrate[4]::text local3winingrate4,
	local3winingrate[5]::text local3winingrate5,
	local3winingrate[6]::text local3winingrate6,
	motor2winingrate[1]::text motor2winingrate1,
	motor2winingrate[2]::text motor2winingrate2,
	motor2winingrate[3]::text motor2winingrate3,
	motor2winingrate[4]::text motor2winingrate4,
	motor2winingrate[5]::text motor2winingrate5,
	motor2winingrate[6]::text motor2winingrate6,
	sex[1] sex1,
	sex[2] sex2,
	sex[3] sex3,
	sex[4] sex4,
	sex[5] sex5,
	sex[6] sex6,
	level[1] level1,
	level[2] level2,
	level[3] level3,
	level[4] level4,
	level[5] level5,
	level[6] level6,
	age[1]::text age1,
	age[2]::text age2,
	age[3]::text age3,
	age[4]::text age4,
	age[5]::text age5,
	age[6]::text age6,
	weight[1]::text weit1,
	weight[2]::text weit2,
	weight[3]::text weit3,
	weight[4]::text weit4,
	weight[5]::text weit5,
	weight[6]::text weit6,
	flying[1]::text fly1,
	flying[2]::text fly2,
	flying[3]::text fly3,
	flying[4]::text fly4,
	flying[5]::text fly5,
	flying[6]::text fly6,
	late[1]::text late1,
	late[2]::text late2,
	late[3]::text late3,
	late[4]::text late4,
	late[5]::text late5,
	late[6]::text late6,
	averagestart[1]::text avgst1,
	averagestart[2]::text avgst2,
	averagestart[3]::text avgst3,
	averagestart[4]::text avgst4,
	averagestart[5]::text avgst5,
	averagestart[6]::text avgst6,
	levelrank lvlrank,
	nationwiningrank nwrank,
	nation2winingrank n2wrank,
	nation3winingrank n3wrank,
	localwiningrank lwrank,
	local2winingrank l2wrank,
	local3winingrank l3wrank,
	motor2rank m2rank,
	motor3rank m3rank,
	startexhibitrank stexhirank,
	exhibitrank exhirank,
	averagestartrank avgstrank,
	avgstcondrank avgcnrank,
	setuwinrank seturank,
	( (case
		when arr.rank = 9 then 6
		else arr.rank
	end)::text ) classes
from
	rec_race race,
	rec_racer_arr arr,
	rec_racer_arr2 arr2
where
	race.ymd = arr.ymd
	and race.jyocd = arr.jyocd
	and race.raceno = arr.raceno
	and race.ymd = arr2.ymd
	and race.jyocd = arr2.jyocd
	and race.raceno = arr2.raceno
	and sanrentanno <> '不成立'
	and grade in ('ip', 'G3', 'G2', 'G1', 'SG')
	and race.ymd >= '20160603'
	and race.ymd <= '20231030'
	and (true)
order by
	pattern,
	race.ymd,
	race.sime
;





select * from ml_classification mc where modelno = '98102';

select 'nopattern' pattern, race.ymd,
substring(race.ymd from 5 for 2) mm,race.jyocd,race.raceno::text,turn::text,race.grade,race.racetype::text,femalecount::text,alevelcount::text,timezone::text,fixedentrance,concat_ws('', race.ymd, race.jyocd, lpad(race.raceno::text, 2, '0')) raceid,entry::text en,sex sex,age::text age,level lv,weight::text weight,branch branch,exhibit::text exhibit,startexhibit::text stexhi,racer.waku::text waku,flying::text fly,late::text late,averagestart::text avgst,avgtime::text avgtime,nationwiningrate::text nw,nation2winingrate::text n2w,nation3winingrate::text n3w,localwiningrate::text lw,nvldbl(local2winingrate,0.0)::text l2w,nvldbl(local3winingrate,0.0)::text l3w,motorno::text mtno,motor2winingrate::text m2w,boatno::text btno,boat2winingrate::text b2w,bor[0]::text bor3T123,bor[1]::text bor3T124,bor[2]::text bor3T125,bor[3]::text bor3T126,bor[4]::text bor3T132,bor[5]::text bor3T134,bor[6]::text bor3T135,bor[7]::text bor3T136,bor[8]::text bor3T142,bor[9]::text bor3T143,bor[10]::text bor3T145,bor[11]::text bor3T146,bor[12]::text bor3T152,bor[13]::text bor3T153,bor[14]::text bor3T154,bor[15]::text bor3T156,bor[16]::text bor3T162,bor[17]::text bor3T163,bor[18]::text bor3T164,bor[19]::text bor3T165,bor[20]::text bor3T213,bor[21]::text bor3T214,bor[22]::text bor3T215,bor[23]::text bor3T216,bor[24]::text bor3T231,bor[25]::text bor3T234,bor[26]::text bor3T235,bor[27]::text bor3T236,bor[28]::text bor3T241,bor[29]::text bor3T243,bor[30]::text bor3T245,bor[31]::text bor3T246,bor[32]::text bor3T251,bor[33]::text bor3T253,bor[34]::text bor3T254,bor[35]::text bor3T256,bor[36]::text bor3T261,bor[37]::text bor3T263,bor[38]::text bor3T264,bor[39]::text bor3T265,bor[40]::text bor3T312,bor[41]::text bor3T314,bor[42]::text bor3T315,bor[43]::text bor3T316,bor[44]::text bor3T321,bor[45]::text bor3T324,bor[46]::text bor3T325,bor[47]::text bor3T326,bor[48]::text bor3T341,bor[49]::text bor3T342,bor[50]::text bor3T345,bor[51]::text bor3T346,bor[52]::text bor3T351,bor[53]::text bor3T352,bor[54]::text bor3T354,bor[55]::text bor3T356,bor[56]::text bor3T361,bor[57]::text bor3T362,bor[58]::text bor3T364,bor[59]::text bor3T365,bor[60]::text bor3T412,bor[61]::text bor3T413,bor[62]::text bor3T415,bor[63]::text bor3T416,bor[64]::text bor3T421,bor[65]::text bor3T423,bor[66]::text bor3T425,bor[67]::text bor3T426,bor[68]::text bor3T431,bor[69]::text bor3T432,bor[70]::text bor3T435,bor[71]::text bor3T436,bor[72]::text bor3T451,bor[73]::text bor3T452,bor[74]::text bor3T453,bor[75]::text bor3T456,bor[76]::text bor3T461,bor[77]::text bor3T462,bor[78]::text bor3T463,bor[79]::text bor3T465,bor[80]::text bor3T512,bor[81]::text bor3T513,bor[82]::text bor3T514,bor[83]::text bor3T516,bor[84]::text bor3T521,bor[85]::text bor3T523,bor[86]::text bor3T524,bor[87]::text bor3T526,bor[88]::text bor3T531,bor[89]::text bor3T532,bor[90]::text bor3T534,bor[91]::text bor3T536,bor[92]::text bor3T541,bor[93]::text bor3T542,bor[94]::text bor3T543,bor[95]::text bor3T546,bor[96]::text bor3T561,bor[97]::text bor3T562,bor[98]::text bor3T563,bor[99]::text bor3T564,bor[100]::text bor3T612,bor[101]::text bor3T613,bor[102]::text bor3T614,bor[103]::text bor3T615,bor[104]::text bor3T621,bor[105]::text bor3T623,bor[106]::text bor3T624,bor[107]::text bor3T625,bor[108]::text bor3T631,bor[109]::text bor3T632,bor[110]::text bor3T634,bor[111]::text bor3T635,bor[112]::text bor3T641,bor[113]::text bor3T642,bor[114]::text bor3T643,bor[115]::text bor3T645,bor[116]::text bor3T651,bor[117]::text bor3T652,bor[118]::text bor3T653,bor[119]::text bor3T654,
( (case when rank = 9 then 6 else rank end)::text ) classes
from rec_race race, rec_racer racer, rec_racer2 racer2, rec_bodds bo
where race.ymd = racer.ymd and race.jyocd = racer.jyocd and race.raceno = racer.raceno
and race.ymd = bo.ymd and race.jyocd = bo.jyocd and race.raceno = bo.raceno
and racer.ymd = racer2.ymd and racer.jyocd = racer2.jyocd and racer.raceno = racer2.raceno and racer.waku = racer2.waku
and sanrentanno <> '不成立'
and race.grade in ('ip', 'G3', 'G2', 'G1', 'SG')
and race.ymd >= '20210601' and race.ymd <= '20230601'
and (race.grade = 'ip')
order by race.ymd, race.jyocd, race.raceno, racer.waku
;

select * from odds_monitor where odds_monitor.bettype = '3T';


copy (
select * from rec_racer_arr2 mc  
) to 'F:\Dev\export\rec_racer_arr2.tsv' csv delimiter E'\t' header;


SELECT 
  modelno, max(ymd)
FROM ml_classification
WHERE modelno IN ('20010', '20011')
GROUP BY modelno
ORDER BY modelno
;

select count(1) from ml_classification mc where modelno = '20008';

select grade, count(1)
from rec_race 
group by grade;

-- ml_result에 대한 평가
select 
  -- race.grade,
  substring(wakulevellist from 1 for 2) waku1,
  modelno,  
  count(1) betcnt,
  (sum(hitamt) - sum(betamt) ) incamt,
  (sum(hity)::float / count(1)::float )::numeric(5,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(7,2) incrate
from ml_result res, rec_race race
where res.ymd = race.ymd and res.jyocd = race.jyocd and res.raceno = race.raceno 
  and race.grade in ('ip')
  and res.stat_bettype = '3T'
  and res.bet_kumiban = '123'
group by substring(wakulevellist from 1 for 2), modelno 
-- group by race.grade, modelno
order by waku1, modelno
;


select 
  t1.bet_kumiban, 
  count(1) betcnt,
  (sum(t1.hitamt) - sum(t1.betamt) ) incamt,
  (sum(t1.hity)::float / count(1)::float )::numeric(5,2) hitrate,
  (sum(t1.hitamt)::float / sum(t1.betamt)::float)::numeric(7,2) incrate
from
	(select 
	  *
	from ml_result where resultno = '45'
	) t1,
	(select 
	  *
	from ml_result where resultno = '49'
	) t2
where t1.ymd = t2.ymd and t1.jyocd = t2.jyocd and t1.raceno = t2.raceno
  and t1.stat_bettype = t2.stat_bettype
  and t1.bet_kumiban = t2.bet_kumiban
  and t1.stat_bettype = '3T'
group by t1.bet_kumiban
;



select * from ml_classification mc where modelno = '20002';

create table ml_result_1_44 as select * from ml_result;
truncate ml_result;

select distinct grade from rec_racer;

select count(distinct (ymd || jyocd || raceno)) from rec_racer where rank = 9;

select * from rec_racer where rank = 9 order by ymd, jyocd, raceno;

select * from rec_racer where ymd='20090101' and jyocd='02' and raceno=6;

select * from rec_racer_arr where ymd='20090101' and jyocd='02' and raceno=6;

select 
  distinct runcnt
from rec_racer2 
order by runcnt
;


select 
  nullif(n3point_waku_slope, 0)
from rec_racer2
where n3point_waku_slope ='NaN'
-- order by COALESCE(n3point_waku_slope, 9999) desc
;

select
  substring(sanrentanno from 3 for 1) rank1, count(1) hitcnt, 
  (count(1)::float / (select count(1) totalcnt from rec_race where ymd::int between 20210603 and  20220602)::float)::numeric(5,3) hitrate
from rec_race race
where ymd::int between 20210603 and  20220602
group by   substring(sanrentanno from 3 for 1)
;

select 
  (prediction1) tansyono,
  count(1) hitcnt,
  (count(1)::float / (select count(1) totalcnt from ml_classification where modelno = '89100' and ymd::int between 20210603 and 20220602)::float)::numeric(5,3) hitrate
from ml_classification mc 
where modelno = '89100' and ymd::int between 20210603 and  20220602
group by modelno, prediction1
;


select distinct modelno 
from ml_classification mc ;


select distinct avgstart_waku_slope[5] from rec_racer_arr2 rra;

select 
  resultno, bettype, 
  (sum(betcnt)::float)::numeric(15,2) betcnt,
  (sum(hitamt) - sum(betamt)) incamt,
  (sum(hitcnt)::float / sum(betcnt)::float)::numeric(7,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(7,2) incrate
from ml_evaluation me 
where resultno::int in (1,3,5,7)
group by resultno, bettype 
order by bettype, resultno::int
;


select modelno, min(skewness), max(skewness) from ml_classification mc group by modelno;
select min(probability1) min1, min(probability2) min2, min(probability3) min3, min(probability4) min4, select max(probability1) max1, max(probability2) max2, max(probability3) max3, max(probability4) max4 from ml_classification where modelno='89100';


select distinct resultno from ml_evaluation me;

SELECT 
    value,
    (value - min_value) * 20.0 / (max_value - min_value) AS percentage
FROM 
    (SELECT 
        probability1 value,
        MIN(probability1) over() AS min_value,
        MAX(probability1) over() AS max_value
    FROM 
        ml_classification where modelno = '89100') AS subquery;

select min(probability1) over () from ml_classification mc;
       
select min(probability1), max(probability1) from ml_classification where  

update ml_classification set memo = '89100 6개월단위 model', modelno='xxxxx' where modelno = '89100';

truncate ml_result;
truncate ml_evaluation;

select modelno, min(ymd), max(ymd) from ml_classification mc group by modelno; 

select count(1) from ml_result;

select
  race.grade, t8.bettype, t8.predict_rank123,
  count(t8.ymd) betcnt,
  (sum(t8.hity)::float / count(t8.ymd)::float)::numeric(5,2) hitrate,
  (sum( (case  when t8.hity = 1 then t8.result_amt else 0 end ) ) - sum(t8.betamt)) incamt,
  (sum( (case  when t8.hity = 1 then t8.result_amt else 0 end ) )::float / sum(t8.betamt)::float)::numeric(5,2) incrate
from
(
	select
	  *
	from  ml_result mr 
	where pattern = 'nopattern' and modelno = '89100'
) t8,
(
	select
	  *
	from  ml_result mr 
	where pattern = 'nopattern' and modelno = '99100'
)t9,
rec_race race
where t8.ymd = t9.ymd and t8.jyocd = t9.jyocd and t8.raceno = t9.raceno
  and t8.bettype = t9.bettype and t8.predict_rank123 = t9.predict_rank123
  and t8.ymd = race.ymd and t8.jyocd = race.jyocd and t8.raceno = race.raceno
  and race.grade = 'ip'
group by race.grade, t8.bettype, t8.predict_rank123
  
select 
  bettype, bet_kumiban, modelno,   count(1) betcnt,  
  (sum(hity)::float / count(1)::float)::numeric(5,2) hitrate,
  (sum( (case  when hity = 1 then result_amt else 0 end ) ) - sum(betamt)) incamt,
  (sum( (case  when hity = 1 then result_amt else 0 end ) )::float / sum(betamt)::float)::numeric(5,2) incrate
from ml_result
where pattern = 'nopattern'
group by bettype,bet_kumiban, modelno
;

drop table if exists rec_racer;
create table rec_racer (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
entry smallint,
sex varchar(2),
age smallint,
level varchar(2),
weight numeric(3,1),
branch varchar(4),
exhibit numeric(5,2),
startexhibit numeric(5,2),
flying smallint,
late smallint,
averagestart numeric(5,2),
avgtime numeric(5,2),
nationwiningrate numeric(5,2),
nation2winingrate numeric(5,2),
nation3winingrate numeric(5,2),
localwiningrate numeric(5,2),
local2winingrate numeric(5,2),
local3winingrate numeric(5,2),
motorno smallint,
motor2winingrate numeric(5,2),
motor3winingrate numeric(5,2),
boatno smallint,
boat2winingrate numeric(5,2),
boat3winingrate numeric(5,2),
waku smallint,
rank smallint,
startresult numeric(5,2),
grade varchar(2),
racetype varchar(4)
);
drop index if exists indexes_rec_racer;
create index indexes_rec_racer on rec_racer (ymd, jyocd, raceno, entry);


drop table if exists ol_racer2;
create table ol_racer2 (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
runcnt int[],                          -- 出走回数
runcnt_slope double precision[],                    -- 出走回数傾向
cond double precision[],               -- コンディション指数
cond_slope double precision[],         -- コンディション指数傾向
n1point double precision[],            -- 能力指数
n1point_slope double precision[],      -- 能力指数傾向
n2point double precision[],            -- 2連帯能力指数
n2point_slope double precision[],      -- 2連帯能力指数傾向
n3point double precision[],            -- 3連帯能力指数
n3point_slope double precision[],      -- 3連帯能力指数傾向
n1point_waku double precision[],       -- 当該枠選手の枠能力指数
n1point_waku_slope double precision[], -- 当該枠選手の枠能力指数傾向
n2point_waku double precision[],       -- 当該枠選手の2連帯枠能力指数
n2point_waku_slope double precision[], -- 当該枠選手の2連帯枠能力指数傾向
n3point_waku double precision[],       -- 当該枠選手の3連帯枠能力指数
n3point_waku_slope double precision[], -- 当該枠選手の3連帯枠能力指数傾向
avgstart_waku double precision[],      -- 当該枠選手の枠平均スタート
avgstart_waku_slope double precision[] -- 当該枠選手の枠平均スタート傾向
);
create index indexes_ol_racer2 on ol_racer2 (ymd, jyocd, raceno);

select max(ymd) from ol_race;
select count(1) from ol_race where ymd = '20231107';



select distinct evaluations_id from ml_evaluation_bk2 meb ;

select * from ml_evaluation where resultno = '11344';

select 
  modelno, min(ymd), max(ymd)
from ml_classification_bk2 
group by modelno;

select modelno, max(ymd) from ml_classification_bk1 mc group by modelno order by modelno;


insert into stat_gpt
select
  result_type, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades,
  bettype, kumiban, resultno, modelno, patternid, pattern,
  (me.hitamt - me.betamt) incamt, me.betcnt, me.incomerate, me.hitrate, me.bal_pluscnt,
  ((betcnt::float - 1.0) / (655200 - 1.0) * 0.33) +  ((hitcnt::float - 1.0) / (31835 - 1.0) * 0.33) +  ((hitamt::float - betamt::float - -18304875.0) / (134193.0 - -18304875.0) * 0.33) gp333, 
  ((betcnt::float - 1.0) / (655200 - 1.0) * 0.1) +  ((hitcnt::float - 1.0) / (31835 - 1.0) * 0.1) +  ((hitamt::float - betamt::float - -18304875.0) / (134193.0 - -18304875.0) * 0.8) gp118,
  ((betcnt::float - 1.0) / (655200 - 1.0) * 0.1) +  ((hitcnt::float - 1.0) / (31835 - 1.0) * 0.8) +  ((hitamt::float - betamt::float - -18304875.0) / (134193.0 - -18304875.0) * 0.1) gp181, 
  ((betcnt::float - 1.0) / (655200 - 1.0) * 0.8) +  ((hitcnt::float - 1.0) / (31835 - 1.0) * 0.1) +  ((hitamt::float - betamt::float - -18304875.0) / (134193.0 - -18304875.0) * 0.1) gp811, 
  ((bal_slope[0] - -100) / (151945 - -100) * 0.33) + ((bal_slope[1] - -100) / (151945 - -100) * 0.33) + ((bal_slope[2] - -100) / (151945 - -100) * 0.33) slp333,
  '99100_6m' 
from ml_evaluation me
where evaluations_id = '666_0'
--  and bettype = '3T' and kumiban = '123'
--  and incomerate > 1
--  order by gp333 desc
;


-- FSB결과 분석
select * from
(
select row_number() over (partition by id_bettype, id_kumiban order by incamt desc) as ranking, * from
(
select 
  id_bettype, 
  id_kumiban,
  id_sql,
  id_factor,
  id_custom,
  id_modelno,
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
      select *
      from ml_evaluation_sim mes where id_term = '6661' and incomerate > 1 and (bal_slope[0]) > 0
    ) me1,
	(
      select *
      from ml_evaluation_sim mes where id_term = '6662' and incomerate > 1 and (bal_slope[0]) > 0
    ) me2
    where 
      me1.id_grade = me2.id_grade and me1.id_bettype = me2.id_bettype and me1.id_kumiban = me2.id_kumiban and me1.id_factor = me2.id_factor and me1.id_custom = me2.id_custom 
      and me1.id_incr = me2.id_incr and me1.id_limit = me2.id_limit and me1.id_modelno = me2.id_modelno and me1.id_sql = me2.id_sql 
) ev
where id_limit = '30' 
   and id_bettype = '3T' and id_kumiban = '123' 
   and id_custom = 'wk'
group by 
  id_bettype, 
  id_kumiban,
  id_factor,
  id_custom,
  id_modelno,
  id_sql
order by id_bettype, id_kumiban, incamt desc
) tmp
) tmp2
where ranking <= 100
order by id_bettype, id_kumiban, ranking
;



drop table ml_evaluation_sim;

insert into ml_evaluation_sim
(
	select 
	  ids[1] id_grade, ids[2] id_bettype, kumiban id_kumiban, ids[4] id_factor, ids[5] id_custom, ids[6] id_incr, ids[7] id_limit, ids[8] id_modelno, ids[10] id_sql, (ids[11] || ids[12]) id_term, 
	  resultno, modelno, patternid, pattern, bettype, kumiban, betcnt, hitcnt, betamt, hitamt, betrate, hitrate, incomerate, hmeanrate, balance, bal_slope, betr_slope, 
      hitr_slope, incr_slope, pt_precision, pt_recall, pt_fmeasure, hodds_min, hodds_max, hodds_mean, hodds_stddev, hodds_median, hoddsrk_min, hoddsrk_max, hoddsrk_mean, 
      hoddsrk_stddev, hoddsrk_median, rodds_min, rodds_max, rodds_mean, rodds_stddev, rodds_median, roddsrk_min, roddsrk_max, roddsrk_mean, roddsrk_stddev, roddsrk_median, 
      bodds_min, bodds_max, bodds_mean, bodds_stddev, bodds_median, boddsrk_min, boddsrk_max, boddsrk_mean, boddsrk_stddev, boddsrk_median, prob_min, prob_max, prob_mean, prob_stddev, 
      prob_median, ror_bestmin, ror_bestmax, ror_betcnt, ror_betamt, ror_hitcnt, ror_hitamt, ror_betrate, ror_hitrate, ror_incomerate, rork_bestmin, rork_bestmax, rork_betcnt, rork_betamt, 
      rork_hitcnt, rork_hitamt, rork_betrate, rork_hitrate, rork_incomerate, bor_bestmin, bor_bestmax, bor_betcnt, bor_betamt, bor_hitcnt, bor_hitamt, bor_betrate, bor_hitrate, bor_incomerate, 
      bork_bestmin, bork_bestmax, bork_betcnt, bork_betamt, bork_hitcnt, bork_hitamt, bork_betrate, bork_hitrate, bork_incomerate, pr_bestmin, pr_bestmax, pr_betcnt, pr_betamt, pr_hitcnt, pr_hitamt, 
      pr_betrate, pr_hitrate, pr_incomerate, bal_pluscnt, result_type, evaluations_id, bonus_pr, bonus_bor, bonus_bork, topbork_bestmin, topbork_bestmax, topbork_betcnt, topbork_betamt, topbork_hitcnt, 
      topbork_hitamt, topbork_betrate, topbork_hitrate, topbork_incomerate 
	from 
	(
		select 
		  *,
		  string_to_array(evaluations_id, '_') ids
		from ml_evaluation me 
		where resultno::int between 274155 and 274724
	) tmp
);

-- gp333 gp118 gp181 gp811除去
delete from ml_evaluation_sim where resultno::int between 273965 and 274004;



truncate stat_bork5;

select evaluations_id, count(1) from stat_bork5 sb group by evaluations_id ;



insert into stat_bork5
  select 
    me.result_type, (case when me.result_type = '1' then 'ip,G3' else 'SG,G1,G2' end) grades, 
    me.bettype, me.kumiban, me.resultno, me.modelno, me.patternid, me.pattern, 
    bk.incamt[0] i0, bk.incamt[1] i1, bk.incamt[2] i2, bk.incamt[3] i3, bk.incamt[4] i4, 
    bk.incamt[5] i5, bk.incamt[6] i6, bk.incamt[7] i7, bk.incamt[8] i8, bk.incamt[9] i9, 
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2] + bk.incamt[3] + bk.incamt[4]) i04, 
    (bk.incamt[2] + bk.incamt[3] + bk.incamt[4] + bk.incamt[5] + bk.incamt[6] + bk.incamt[7]) i27,
    (bk.incamt[5] + bk.incamt[6] + bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i59,
    (bk.incamt[0] + bk.incamt[1] + bk.incamt[2] + bk.incamt[3] + bk.incamt[4] + bk.incamt[5] + bk.incamt[6] + bk.incamt[7] + bk.incamt[8] + bk.incamt[9]) i09,
    (me.hitamt - me.betamt) incamt, me.betcnt, me.incomerate, me.hitrate, me.bal_pluscnt, 
    ( case 
	    when me.evaluations_id = '666_1' and me.modelno = '99100' then '99100_6m_1'
        when  me.evaluations_id = '666_1' and me.modelno = '79100' then '79100_6m_1'
        when  me.evaluations_id = '666_2' and me.modelno = '99100' then '99100_6m_2'
        when  me.evaluations_id = '666_2' and me.modelno = '79100' then '79100_6m_2'
        when  me.evaluations_id = '666_3' and me.modelno = '99100' then '99100_6m_3'
        when  me.evaluations_id = '666_3' and me.modelno = '79100' then '79100_6m_3'
    end),
    (3 / (1/me.betcnt::float + 1/me.hitcnt::float + 1/((me.hitamt/100)-(me.betamt/100))::float) )::double precision hm,
    (3 / (1/me.bork_betcnt::float + 1/me.bork_hitcnt::float + 1/((me.bork_hitamt/100)-(me.bork_betamt/100))::float) )::double precision borkhm,
    (3 / (1/me.bor_betcnt::float + 1/me.bor_hitcnt::float + 1/((me.bor_hitamt/100)-(me.bor_betamt/100))::float) )::double precision borhm,
    ((me.betcnt::float - 1.0) / (104255 - 1.0) * 0.33) +  ((me.hitcnt::float - 1.0) / (5145 - 1.0) * 0.33) +  ((me.hitamt::float - me.betamt::float - -2633271.0) / (32399.0 - -2633271.0) * 0.33) gp, 
    ((me.bork_betcnt::float - 1.0) / (4168 - 1.0) * 0.33) +  ((me.bork_hitcnt::float - 1.0) / (708 - 1.0) * 0.33) +  ((me.bork_hitamt::float - me.bork_betamt::float - 1.0) / (112799.0 - 1.0) * 0.33) borkgp, 
    ((me.bor_betcnt::float - 1.0) / (3217 - 1.0) * 0.33) +  ((me.bor_hitcnt::float - 1.0) / (656 - 1.0) * 0.33) +  ((me.bor_hitamt::float - me.bor_betamt::float - 1.0) / (59364.0 - 1.0) * 0.33) borgp
    --  hmr은 일단 제외한다. hm에 속성이 포함되어있고 ranking을 왜곡시킬 우려가 있으므로. 나중에 잘 안되면 한번 해보자.
--    (3 / (1/me.betrate + 1/me.hitrate + 1/me.incomerate) )::double precision hmr,
--    (3 / (1/me.bork_betrate + 1/me.bork_hitrate + 1/me.bork_incomerate) )::double precision borkhmr,
--    (3 / (1/me.bor_betrate + 1/me.bor_hitrate + 1/me.bor_incomerate) )::double precision borkhmr
  from ml_evaluation me, ml_bork_evaluation bk
  where me.resultno = bk.resultno and me.result_type = bk.result_type and me.bettype = bk.bettype and me.kumiban = bk.kumiban and me.modelno = bk.modelno and me.patternid  = bk.patternid and me.pattern = bk.pattern
     and me.evaluations_id in ('666_1','666_2','666_3')
--     and me.evaluations_id = '666_3' and me.modelno = '99100'
     and ( 
        me.betcnt <> 0 and me.hitcnt <> 0 and ((me.hitamt/100)-(me.betamt/100)) <> 0
        and me.bork_betcnt <> 0 and me.bork_hitcnt <> 0 and ((me.bork_hitamt/100)-(me.bork_betamt/100)) <> 0
        and me.bor_betcnt <> 0 and me.bor_hitcnt <> 0 and ( (me.bor_hitamt/100)-(me.bor_betamt/100)) <> 0
       and (1/me.betcnt::float + 1/me.hitcnt::float + 1/((me.hitamt/100)-(me.betamt/100))::float) <> 0
       and (1/me.bork_betcnt::float + 1/me.bork_hitcnt::float + 1/((me.bork_hitamt/100)-(me.bork_betamt/100))::float) <> 0
       and (1/me.bor_betcnt::float + 1/me.bor_hitcnt::float + 1/((me.bor_hitamt/100)-(me.bor_betamt/100))::float) <> 0
    )
 ;



select count(1) from stat_bork5 where resultno::int > 7555;

select
    *, string_to_array(evaluations_id, '_') evalids
  from ml_evaluation me 
  where evaluations_id like '%7m_775_2%'



-- FPH4에 대해 775_1. 775_2 모두 흑자인 패턴id들
select * from (
select 
  row_number() over (partition by btype, kban order by dailybet desc) as ranking, *
from
(
select
  t2.bettype btype, 
  t2.kumiban kban,
  concat_ws('_', t2.evalids[4],t2.evalids[5],t2.evalids[6],t2.evalids[7]) ptnid,
  (t2.betcnt) bet_cnt,
  ((t2.betcnt)::float / 210)::numeric(5,2) dailybet,
  ((t2.hitamt-t2.betamt)) incamt, 
  ((t2.hitcnt)::float / (t2.betcnt)::float)::numeric(5,2) hitrate,
  ((t2.hitamt)::float / (t2.betamt)::float)::numeric(5,2) incomerate,
  (t2.bal_pluscnt) balsum
from   
(
  select
    *, string_to_array(evaluations_id, '_') evalids
  from ml_evaluation me 
  where evaluations_id like '%7m_775_2%'
    and incomerate >= 1.01
) t2
where t2.evalids[1] = 'ip'
  -- and t2.evalids[5] <> 'all'
  and t2.bettype = '3T'
  --and t2.evalids[4] = 'i04'
order by btype, kban, dailybet desc
) tall
) tall2
where ranking = 1
;  

-- ip_3F_*-*-*_i27_wk_1.3~x_30_79100_7m_775_1_273471

-- {ip,1T,*-*-*,i09,wkall,1.05~x,10,79100,7m,775,2,273469}
;
-- result에 대해 775_1. 775_2 모두 흑자인 패턴id들
select * from (
select 
  row_number() over (partition by btype, kban order by dailybet desc) as ranking, *
from 
(
select
  t1.bettype btype, 
  t1.kumiban kban,
  t1.patternid ptnid,
  count(1) ptncnt,
  sum(t2.betcnt) bet_cnt,
  sum(t2.betrate) bet_rate,
  (sum(t2.betcnt)::float / 210)::numeric(5,2) dailybet,
  sum((t2.hitamt-t2.betamt)) incamt, 
  (sum(t2.hitcnt)::float / sum(t2.betcnt)::float)::numeric(5,2) hitrate,
  (sum(t2.hitamt)::float / sum(t2.betamt)::float)::numeric(5,2) incomerate,
  sum(t2.bal_pluscnt) balsum
from   
(
  select
    *
  from ml_evaluation me 
  where evaluations_id = '775_1'
    and incomerate >= 1.01
    and betcnt > (30*7 / 2)
) t1,
(
  select
    *
  from ml_evaluation me 
  where evaluations_id = '775_2'
    and incomerate >= 1.01
    and betcnt > (30*7 / 2)
) t2
where t1.result_type = '1'
  and t1.modelno = t2.modelno and t1.patternid = t2.patternid and t1.pattern = t2.pattern 
  and t1.bettype = t2.bettype and t1.kumiban = t2.kumiban and t1.result_type = t2.result_type
group by btype, kban, ptnid 
order by btype, kban, dailybet desc
) tall
) tall2 where ranking = 1
;  


select max(resultno::int) from ml_evaluation me;

truncate stat_bork5;

delete from stat_bork5 where evaluations_id = '79100_14m';

select distinct evaluations_id from stat_bork5 sb;


select distinct evaluations_id from stat_bork5 sb; 


delete from ml_evaluation where resultno::int >= 273469;
delete from ml_bork_evaluation where resultno::int >= 273469;
delete from ml_pr_evaluation where resultno::int >= 273469;
delete from ml_range_evaluation where resultno::int >= 273469;
delete from ml_term_evaluation where resultno::int >= 273469;
    
select
  *   
from st_patternid sp 
where result_type = '1'
  and modelno = '99100'
order by bettype, kumiban, patternid, p_hmean 
;


select
  bettype,kumiban, modelno, betcnt, (hitamt - betamt) incamt, incomerate, hitrate, bal_pluscnt 
from ml_evaluation me 
where result_type = '1' and patternid = 'nopattern'
order by bettype,kumiban, incomerate desc
;



select
  *
from st_patternid sp 
where patternid in ('wk1', 'wk12', 'wk123', 'wk1234')
  and modelno = '99100'
;


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
		where 
		  -- result_type = '11'
		  resultno::int between 4961 and 5752
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
		where 
		  -- result_type = '11'
		  resultno::int between 4961 and 5752
		group by result_type, bettype, kumiban, patternid, modelno
		) tot,
  	    (
		  select
		    result_type, bettype, kumiban, patternid, modelno,
		    sum(termcnt) termcnt, 
		    sum(pluscnt) pluscnt
		  from ml_term_evaluation mte
		  where 
		    -- result_type = '11'
		  resultno::int between 4961 and 5752
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

select count(1) from st_patternid sp ;

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

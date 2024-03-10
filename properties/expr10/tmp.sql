select ('nopattern') pattern, 
  count(*)
from rec_race race, ml_result res, ml_classification cls, odds_monitor omon 
where race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno 
  and race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno and res.modelno = cls.modelno 
  and res.ymd = omon.ymd and res.jyocd = omon.jyocd and res.raceno = omon.raceno and res.bettype = omon.bettype and res.bet_kumiban = omon.kumiban 
  and sanrentanno <> '不成立' 
  and race.ymd >= '20210607' and race.ymd <= '20210917' 
  and resultno = '2814' 
  and custom = '' and kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3' 
;
-> 10740

select ('nopattern') pattern, 
  count(*)
from rec_race race, ml_result res, ml_classification cls, odds_monitor omon 
where race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno 
  and res.ymd = cls.ymd and res.jyocd = cls.jyocd and res.raceno = cls.raceno and res.modelno = cls.modelno 
  and res.ymd = omon.ymd and res.jyocd = omon.jyocd and res.raceno = omon.raceno and res.bettype = omon.bettype and res.bet_kumiban = omon.kumiban 
  and sanrentanno <> '不成立' 
  and race.ymd >= '20210607' and race.ymd <= '20210917' 
  and res.resultno = '2814' and custom = '' 
  and kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3' 
;

select count(*) from ml_classification 
where ymd >= '20210607' and ymd <= '20210917' 
  and kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3'
  and modelno = '14';

select count(*) from ml_result res
where ymd >= '20210607' and ymd <= '20210917' 
  and resultno = '2814' and res.bettype = '3T' and bet_kumiban = '123'
  and modelno = '14';

select count(*) from ml_result res, ml_classification cls
where res.ymd >= '20210607' and res.ymd <= '20210917' 
  and resultno = '2814' and res.bettype = '3T' and bet_kumiban = '123'
  and res.ymd = cls.ymd and res.jyocd = cls.jyocd and res.raceno = cls.raceno and res.modelno = cls.modelno;

select count(*) from ml_result res, ml_classification cls
where res.ymd >= '20210607' and res.ymd <= '20210917' 
  and resultno = '2814' 
  and res.ymd = cls.ymd and res.jyocd = cls.jyocd and res.raceno = cls.raceno and res.modelno = cls.modelno
  and (kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3' )
;


select count(*) from ml_result res, ml_classification cls, rec_race race
where res.ymd >= '20210607' and res.ymd <= '20210917' 
  and resultno = '2814' and res.bettype = '3T' and bet_kumiban = '123'
  and res.ymd = cls.ymd and res.jyocd = cls.jyocd and res.raceno = cls.raceno and res.modelno = cls.modelno;
  and race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno 
;

select count(*) from ml_result res, rec_race race
where res.ymd >= '20210607' and res.ymd <= '20210917' 
  and resultno = '2814' and res.bettype = '3T' and bet_kumiban = '123'
  and race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno and sanrentanno <> '不成立' 
;

select count(*) from ml_result res, rec_race race, ml_classification cls
where res.ymd >= '20210607' and res.ymd <= '20210917' 
  and resultno = '2814' 
  and race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno and sanrentanno <> '不成立' 
  and race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno and res.modelno = cls.modelno
  and res.bettype = '3T'
;


------------------------------------------------------------------------------------
select 
  bettype, bet_kumiban,
  count(*) cnt 
from ml_result
where resultno = '2814' and custom = ''
  and ymd::int between 20210607 and 20210922
  and bettype = '3T' and bet_kumiban = '123'
group by bettype, bet_kumiban
order by bettype, bet_kumiban;

-> 2191



  select 'nopattern' pattern,
    count(*)
  from ml_result res, odds_monitor omon
  where res.ymd = omon.ymd and res.jyocd = omon.jyocd and res.raceno = omon.raceno 
    and res.bettype = omon.bettype and res.bet_kumiban = omon.kumiban
    and res.ymd::int between 20210607 and 20210922
    and resultno = '2814' and custom = ''
    and res.bettype = '3T' and bet_kumiban = '123'
  
-> 2191


  select 'nopattern' pattern,
    count(*)
  from ml_result res, ml_classification cls
  where 
    res.ymd = cls.ymd and res.jyocd = cls.jyocd and res.raceno = cls.raceno 
    and res.modelno = cls.modelno
    and res.ymd::int between 20210607 and 20210922
    and resultno = '2814' and custom = ''
    and res.bettype = '3T' and bet_kumiban = '123'
  
-> 2233


  select 'nopattern' pattern,
    count(*)
  from ml_result res, ml_classification cls, odds_monitor omon
  where 
    res.ymd = cls.ymd and res.jyocd = cls.jyocd and res.raceno = cls.raceno 
    and res.modelno = cls.modelno
    and res.ymd = omon.ymd and res.jyocd = omon.jyocd and res.raceno = omon.raceno 
    and res.bettype = omon.bettype and res.bet_kumiban = omon.kumiban
    and res.ymd::int between 20210607 and 20210922
    and resultno = '2814' and custom = ''
    and res.bettype = '3T' and bet_kumiban = '123';

-> 2233


  select 'nopattern' pattern,
    count(*)
  from rec_race race, ml_result res, ml_classification cls, odds_monitor omon
  where race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno 
    and race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno 
    and res.modelno = cls.modelno
    and res.ymd = omon.ymd and res.jyocd = omon.jyocd and res.raceno = omon.raceno 
    and res.bettype = omon.bettype and res.bet_kumiban = omon.kumiban
    and race.ymd::int between 20210607 and 20210922
    and resultno = '2814' and custom = ''
    and res.bettype = '3T' and bet_kumiban = '123';

-> 2233


select count(*) from ml_classification 
where ymd::int between 20210607 and 20210922
  and kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3'
  and modelno = '14';

  select 'nopattern' pattern,
    res.modelno
  from ml_result res, ml_classification cls
  where 
    res.ymd = cls.ymd and res.jyocd = cls.jyocd and res.raceno = cls.raceno 
    and res.modelno = cls.modelno
    and res.ymd::int between 20210607 and 20210922
    and resultno = '2814' and custom = ''
    and res.bettype = '3T' and bet_kumiban = '123';
  order by res.modelno:

  select 'nopattern' pattern,
    count(*)
  from rec_race race, ml_result res, ml_classification cls, odds_monitor omon
  where race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno 
    and race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno 
    and res.modelno = cls.modelno
    and res.ymd = omon.ymd and res.jyocd = omon.jyocd and res.raceno = omon.raceno 
    and res.bettype = omon.bettype and res.bet_kumiban = omon.kumiban
    and race.ymd::int between 20210607 and 20210922
    and resultno = '2814' and custom = ''
    and kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3' 
    and omon.beforeodds > 4 
;

    and res.bettype = '3T' and bet_kumiban = '123';
;

select ('nopattern') pattern, 
  count(*) cnt
from rec_race race, ml_result res, ml_classification cls, odds_monitor omon 
where race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno 
  and race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno 
  and res.modelno = cls.modelno 
  and res.ymd = omon.ymd and res.jyocd = omon.jyocd and res.raceno = omon.raceno 
  and res.bettype = omon.bettype and res.bet_kumiban = omon.kumiban 
  and race.ymd >= '20210607' and race.ymd <= '20210917' 
  and resultno = '2814' and custom = '' 
  -- and res.bettype = '3T' and res.bet_kumiban = '123'
  and kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3' 
;

  select
  -- race.raceno ptn,
  -- substring(wakulevellist from 1 for 2) ptn,
  resultno ptn,
  -- (bet_odds * probability_mul)::numeric(5,1) ptn, 
  sum(1) betcnt,
  sum(hity) hitcnt,
  sum(betamt) betamt,
  sum(hitamt) hitamt,
  sum(hitamt) - sum(betamt) incomeamt, 
  (sum(hity)::float / sum(1)::float)::numeric(7,2) hitrate,
  (sum(hitamt)::float / sum(betamt)::float)::numeric(7,2) incomerate
from 
  ml_result res, 
  ml_classification cls, 
  odds_monitor omon
where res.ymd = omon.ymd and res.jyocd = omon.jyocd and res.raceno = omon.raceno and res.bettype = omon.bettype and res.bet_kumiban = omon.kumiban
    and res.ymd = cls.ymd and res.jyocd = cls.jyocd and res.raceno = cls.raceno and res.modelno = cls.modelno
    and (res.ymd::int between 20210607 and 20210917)
    and resultno::int = 2814
    and res.bettype = '3T' and res.bet_kumiban = '123'
    -- and omon.beforeodds > 4
  group by ptn
  order by ptn
;

select count(*) from ml_result res
where resultno = '2814' and custom = '' 
  and res.ymd >= '20210607' and res.ymd <= '20210917' 
  and res.bettype = '3T' and res.bet_kumiban = '123';
  
  
delete from ml_result where resultno = '2814';
  
;
select count(*) from ml_classification cls
where cls.ymd >= '20210607' and cls.ymd <= '20210917' 
  and kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3' 
  and modelno = '14'
;

select count(*) from ml_result res
where res.ymd >= '20210607' and res.ymd <= '20210917' 
  and resultno = '2814'
  and res.bettype = '3T' and res.bet_kumiban = '123'
;

select count(*) from ml_classification cls, ml_result res
where res.ymd >= '20210607' and res.ymd <= '20210917' 
  and kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3' 
  and res.resultno = '2814'
  and res.ymd = cls.ymd and res.jyocd = cls.jyocd and res.raceno = cls.raceno and res.modelno = cls.modelno
;

select ('nopattern') pattern, 
  cls.modelno, race.ymd, race.jyocd, race.raceno, race.sime, tansyono, nirentanno, nirenhukuno, sanrentanno, sanrenhukuno, tansyoprize, nirentanprize, 
  nirenhukuprize, sanrentanprize, sanrenhukuprize, tansyopopular, nirentanpopular, nirenhukupopular, sanrentanpopular, sanrenhukupopular, kumiban1, kumiban2, 
  kumiban3, probability1, probability2, probability3, probability_sum, probability_mul 
from rec_race race, ml_result res, ml_classification cls, odds_monitor omon 
where race.ymd = res.ymd and race.jyocd = res.jyocd and race.raceno = res.raceno 
  and race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno and res.modelno = cls.modelno 
  and res.ymd = omon.ymd and res.jyocd = omon.jyocd and res.raceno = omon.raceno and res.bettype = omon.bettype 
  and res.bet_kumiban = omon.kumiban 
  and race.ymd >= '20210607' and race.ymd <= '20210922' 
  and resultno = '2814' and custom = '' 
  and kumiban1 = '1' and kumiban2 = '2' and kumiban3 = '3' 


  select 'nopattern' pattern, 
    count(*)
  from rec_race race, ml_classification cls 
  where race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno 
    and sanrentanno <> '不成立' 
    and race.ymd >= '20210607' and race.ymd <= '20210917' 
    and modelno = '14'
    and cls.kumiban1 = '1' and cls.kumiban2 = '2' 
;


select 'nopattern' pattern, 
  count(*)
from rec_race race, ml_classification cls 
where race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno 
  and sanrentanno <> '不成立' 
  and race.ymd >= '20210607' and race.ymd <= '20210917' 
  and modelno = '14'  
  and cls.kumiban1 = '1' and cls.kumiban2 = '2' 
;



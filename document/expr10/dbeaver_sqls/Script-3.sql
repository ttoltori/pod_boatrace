select * from ol_race where ymd::int = 20220424 and jyocd = '09' and raceno = 1 order by sime for update;

select * from ml_classification where ymd::int = 20220424 and jyocd = '09' and raceno = 1  order by sime;

select * from ol_race where ymd::int = 20220702 order by sime for update;

select distinct modelno from ml_classification where ymd::int = 20220707; --order by sime;

select * from ml_result where ymd::int = 20220504 order by sime for update;

select bettype, sum(1) betcnt, sum(hitamt - betamt) from ml_result where ymd::int > 20220926 group by bettype, bet_kumiban;


select bettype, bet_kumiban, sum(hitamt), sum(betamt), sum(hitamt) - sum(betamt) from ml_result group by bettype, bet_kumiban ;

select stat_bettype, sum(1) betcnt, sum(hitamt - betamt),
sum(hitamt)::float / sum(betamt)::float
from ml_result where ymd::int > 20220926 group by stat_bettype order by stat_bettype ;



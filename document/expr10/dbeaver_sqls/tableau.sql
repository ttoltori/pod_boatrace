copy (
select 
  (case when mr.ymd < '20210602' then 'train' 
        when mr.ymd >= '20210602' and mr.ymd <= '20211031' then 'term1'
        when mr.ymd >= '20211101' and mr.ymd <= '20220331' then 'term2'
        when mr.ymd >= '20220401' and mr.ymd <= '20220831' then 'term3'
        else 'noterm'
    end) term,
--  (case when mr.ymd < '20210602' then 'train' 
--        when mr.ymd >= '20210602' and mr.ymd <= '20211130' then 'term1'
--        when mr.ymd >= '20211201' and mr.ymd <= '20220531' then 'term2'
--        when mr.ymd >= '20220601' and mr.ymd <= '20220831' then 'term3'
--        else 'noterm'
--    end) term,
  rr.ymd, 
  substring(rr.sime from 1 for 2) hh,
  rr.weekday,
  rr.jyocd,
  rr.raceno,
  mr.modelno,
  mr.stat_bettype s_bettype,
  mr2.bet_kumiban s_kumiban,
  (case when mr2.bet_odds is null then -1 else mr2.bet_odds end) s_bodds,
  (case when mr2.bet_oddsrank is null then -1 else mr2.bet_oddsrank end) s_bork,
  mr2.result_odds s_rodds,
  mr2.result_oddsrank s_rork,
  mr.bettype,
  mr.bet_kumiban bkumiban,
--  mr.bet_odds bodds,
  (case when mr.bet_odds is null then -1 else mr.bet_odds end) bodds,
--  mr.bet_oddsrank bork,
  (case when mr.bet_oddsrank is null then -1 else mr.bet_oddsrank end) bork,
  mr.result_kumiban rkumiban,
  mr.result_odds rodds,
  mr.result_oddsrank rork,
  1 betcnt,
  mr.hity hitcnt,
  mr.betamt,
  mr.hitamt,
  (mr.hitamt - mr.betamt) incamt,
  rr.grade,
  rr.isvenus, 
  rr.timezone, 
  rr.turn, 
  rr.kimarite, 
  rr.fixedentrance, 
  rr.racetype, 
  rr.alevelcount, 
  rr.femalecount, 
  rr.com_predict, 
  rr.com_confidence,
  substring(probability1::text from 1 for 3) prob1,
  substring(probability2::text from 1 for 3) prob2,
  substring(probability3::text from 1 for 3) prob3,
  substring(rr.wakulevellist from 1 for 2) wk1,
  substring(rr.wakulevellist from 4 for 2) wk2,
  substring(rr.wakulevellist from 7 for 2) wk3,
  substring(rr.wakulevellist from 10 for 2) wk4,
  substring(rr.wakulevellist from 13 for 2) wk5,
  substring(rr.wakulevellist from 16 for 2) wk6,
  substring(rra.nationwiningrate[1]::text from 1 for 1) nw1,
  substring(rra.nationwiningrate[2]::text from 1 for 1) nw2,
  substring(rra.nationwiningrate[3]::text from 1 for 1) nw3,
  substring(mr.predict_rank123 from 1 for 1) pd_rk1,
  substring(mr.predict_rank123 from 2 for 1) pd_rk2,
  substring(mr.predict_rank123 from 3 for 1) pd_rk3,
  substring(mr.result_rank123 from 1 for 1) re_rk1,
  substring(mr.result_rank123 from 2 for 1) re_rk2,
  substring(mr.result_rank123 from 3 for 1) re_rk3
from ml_result mr, rec_race rr, ml_classification mc, rec_racer_arr rra, 
  (select * from ml_result where bettype = '2T') mr2
where mr.ymd = rr.ymd and mr.jyocd = rr.jyocd and mr.raceno = rr.raceno 
  and mr.ymd = mr2.ymd and mr.jyocd = mr2.jyocd and mr.raceno = mr2.raceno and mr.modelno = mr2.modelno
  and rra.ymd = rr.ymd and rra.jyocd = rr.jyocd and rra.raceno = rr.raceno
  and mc.ymd = rr.ymd and mc.jyocd = rr.jyocd and mc.raceno = rr.raceno and mr.modelno = mc.modelno 
  and rr.sanrentanno <> 'Üôà÷Ø¡'
  and mr.stat_bettype = '3F' and mr.bettype = '3F' 
  and (
  	(mr.ymd between '20180601' and '20210601' and (mr.result_oddsrank is not null and mr.result_odds is not null)
  	) or 
  	(mr.ymd between '20210602' and '20220831'
    	and (
  			mr.bet_odds between 0 and 50
  			and mr.bet_oddsrank between 0 and 20
    	)
    )  		
  )
order by mr.ymd, mr.jyocd, mr.raceno, mr.sime, mr.modelno, mr.stat_bettype, mr.bettype, mr2.bet_kumiban, mr.bet_kumiban
) to 'D:\Dev\TableauRepository\boatml\2T_3F_all_555.tsv' csv delimiter E'\t' header;
;

select
  mr.*
from ml_result mr, (select * from ml_result where bettype = '2T') mr2
where mr.ymd = mr2.ymd and mr.jyocd = mr2.jyocd and mr.raceno = mr2.raceno and mr.modelno = mr2.modelno
  and mr.stat_bettype = '2N' and mr.bettype = '3T' 
  and mr.ymd = '20180601' and mr.jyocd = '18' and mr.raceno = 4
;

copy (
select 
  (case when mr.ymd < '20210602' then 'train' 
        when mr.ymd >= '20210602' and mr.ymd <= '20211031' then 'term1'
        when mr.ymd >= '20211101' and mr.ymd <= '20220331' then 'term2'
        when mr.ymd >= '20220401' and mr.ymd <= '20220831' then 'term3'
        else 'noterm'
    end) term,
--  (case when mr.ymd < '20210602' then 'train' 
--        when mr.ymd >= '20210602' and mr.ymd <= '20211130' then 'term1'
--        when mr.ymd >= '20211201' and mr.ymd <= '20220531' then 'term2'
--        when mr.ymd >= '20220601' and mr.ymd <= '20220831' then 'term3'
--        else 'noterm'
--    end) term,
  rr.ymd, 
  substring(rr.sime from 1 for 2) hh,
  rr.weekday,
  rr.jyocd,
  rr.raceno,
  mr.modelno,
  mr.stat_bettype,
  mr.bettype,
  mr.bet_kumiban bkumiban,
--  mr.bet_odds bodds,
  (case when mr.bet_odds is null then -1 else mr.bet_odds end) bodds,
--  mr.bet_oddsrank bork,
  (case when mr.bet_oddsrank is null then -1 else mr.bet_oddsrank end) bork,
  mr.result_kumiban rkumiban,
  mr.result_odds rodds,
  mr.result_oddsrank rork,
  1 betcnt,
  mr.hity hitcnt,
  mr.betamt,
  mr.hitamt,
  (mr.hitamt - mr.betamt) incamt,
  rr.grade,
  rr.isvenus, 
  rr.timezone, 
  rr.turn, 
  rr.kimarite, 
  rr.fixedentrance, 
  rr.racetype, 
  rr.alevelcount, 
  rr.femalecount, 
  rr.com_predict, 
  rr.com_confidence,
  substring(probability1::text from 1 for 3) prob1,
  substring(probability2::text from 1 for 3) prob2,
  substring(probability3::text from 1 for 3) prob3,
  substring(rr.wakulevellist from 1 for 2) wk1,
  substring(rr.wakulevellist from 4 for 2) wk2,
  substring(rr.wakulevellist from 7 for 2) wk3,
  substring(rr.wakulevellist from 10 for 2) wk4,
  substring(rr.wakulevellist from 13 for 2) wk5,
  substring(rr.wakulevellist from 16 for 2) wk6,
  substring(rra.nationwiningrate[1]::text from 1 for 1) nw1,
  substring(rra.nationwiningrate[2]::text from 1 for 1) nw2,
  substring(rra.nationwiningrate[3]::text from 1 for 1) nw3,
  substring(mr.predict_rank123 from 1 for 1) pd_rk1,
  substring(mr.predict_rank123 from 2 for 1) pd_rk2,
  substring(mr.predict_rank123 from 3 for 1) pd_rk3,
  substring(mr.result_rank123 from 1 for 1) re_rk1,
  substring(mr.result_rank123 from 2 for 1) re_rk2,
  substring(mr.result_rank123 from 3 for 1) re_rk3
from ml_result mr, rec_race rr, ml_classification mc, rec_racer_arr rra 
where mr.ymd = rr.ymd and mr.jyocd = rr.jyocd and mr.raceno = rr.raceno 
  and rra.ymd = rr.ymd and rra.jyocd = rr.jyocd and rra.raceno = rr.raceno
  and mc.ymd = rr.ymd and mc.jyocd = rr.jyocd and mc.raceno = rr.raceno and mr.modelno = mc.modelno 
  and rr.sanrentanno <> 'Üôà÷Ø¡'
  and mr.bettype = '3F' 
  and (
  	(mr.ymd between '20180601' and '20210601' and (mr.result_oddsrank is not null and mr.result_odds is not null)
  	) or 
  	(mr.ymd between '20210602' and '20220831'
    	and (
  			mr.bet_odds between 0 and 50
  			and mr.bet_oddsrank between 0 and 20
    	)
    )  		
  )
--  and mr.stat_bettype in ('3T')
--  and substring(mr.predict_rank123 from 1 for 3) = '123' 
--  and mr.modelno in ('99080', '99100', '99102')
order by mr.ymd, mr.jyocd, mr.raceno, mr.sime, mr.modelno, mr.stat_bettype, mr.bettype, mr.bet_kumiban
) to 'D:\Dev\TableauRepository\boatml\all_3F_all_555.tsv' csv delimiter E'\t' header;
;

select count(1), min(bet_odds), max(bet_odds)
from ml_result mr 
where 
   mr.bettype = '2T' and mr.bet_kumiban = '23'
  and mr.ymd between '20210602' and '20220831'
;


select distinct stat_bettype from ml_result mr where bettype = '2T';

select min(ymd), max(ymd) from rec_race rr where ymd between '20180601' and '20220531';
select min(ymd), max(ymd) from rec_racer_arr rr;
select min(ymd), max(ymd) from ml_classification mc;

select min(ymd), max(ymd) from ml_result rr;

select count(1) from ml_result;

copy (
select 
  rr.ymd, 
  substring(rr.sime from 1 for 2) hh,
  rr.jyocd,
  rr.raceno,
  rr.grade,
  rr.isvenus, 
  rr.timezone, 
  rr.turn, 
  rr.kimarite, 
  rr.fixedentrance, 
  rr.racetype, 
  rr.alevelcount, 
  rr.femalecount, 
  rr.com_predict, 
  rr.com_confidence,
  substring(rr.wakulevellist from 1 for 2) wk1,
  substring(rr.wakulevellist from 4 for 2) wk2,
  substring(rr.wakulevellist from 7 for 2) wk3,
  substring(rr.wakulevellist from 10 for 2) wk4,
  substring(rr.wakulevellist from 13 for 2) wk5,
  substring(rr.wakulevellist from 16 for 2) wk6,
  substring(rra.nationwiningrate[1]::text from 1 for 1) nw1,
  substring(rra.nationwiningrate[2]::text from 1 for 1) nw2,
  substring(rra.nationwiningrate[3]::text from 1 for 1) nw3,
  tansyono, tansyopopular, tansyoprize, 
  rr.nirentanno, rr.nirentanpopular, rr.nirentanprize,
  rr.nirenhukuno, rr.nirenhukupopular, rr.nirenhukuprize,
  sanrentanno, sanrentanprize, sanrentanpopular,
  sanrenhukuno, sanrenhukupopular, sanrenhukuprize 
from rec_race rr , rec_racer_arr rra 
where rr.ymd between '20180601' and '20220531'
  and rra.ymd = rr.ymd and rra.jyocd = rr.jyocd and rra.raceno = rr.raceno
order by rr.ymd, rr.jyocd, rr.raceno, rr.sime
) to 'D:\Dev\TableauRepository\boatml\rec_race_term-36-12.tsv' csv delimiter E'\t' header;
;




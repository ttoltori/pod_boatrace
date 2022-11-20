DROP FUNCTION IF EXISTS create_stat_ml_ptn_linear_analyze (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400),
  int[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[], numeric(7,2)[]);
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_linear_analyze (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramArrOddsRank int[],
paramArrOdds numeric(7,2)[],
paramArrTotalRate numeric(7,2)[],
paramArrHitRate numeric(7,2)[],
paramArrIncomeRate numeric(7,2)[]
) RETURNS VOID AS $$
DECLARE
    params text;
    custat refcursor; -- 패턴결과테이블 커서
    recstat record; -- 패턴결과테이블 레코드
    counter int; -- 행카운터
    lr stat_ml_ptn_linear_analyze%ROWTYPE; -- 축적결과테이블 행
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    raise info 'delete stat_ml_ptn_linear_analyze %', params;
    delete from stat_ml_ptn_linear_analyze
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;
      
    counter := 1;
    open custat for 
      select 
        stat.*
      from stat_ml_result_ptn_analyze stat, stat_ml_ptn_final final
      where stat.description = paramDescription and stat.bettype = paramBettype and stat.bet_kumiban = paramKumiban and stat.pattern_name = paramPatternName
        and stat.description = final.description and stat.bettype = final.bettype and stat.bet_kumiban = final.kumiban
        and stat.pattern_name = final.pattern_name and stat.pattern_value = final.pattern_value
        and stat.bet_oddsrank >= paramArrOddsRank[1] and stat.bet_oddsrank <= paramArrOddsRank[2]
        and stat.bet_odds >= paramArrOdds[1] and stat.bet_odds <= paramArrOdds[2]
        and totalrate >= paramArrTotalRate[1] and totalrate <= paramArrTotalRate[2]
        and hitrate >= paramArrHitRate[1] and hitrate <= paramArrHitRate[2]
        and incomerate >= paramArrIncomeRate[1] and incomerate <= paramArrIncomeRate[2]
      order by stat.ymd, stat.sime;
      
	loop
	    fetch custat into recstat;
	    if not found then 
	      exit;
	    end if;
	    
        lr.description := recstat.description;
        lr.ymd := recstat.ymd;
        lr.sime := recstat.sime;
        lr.jyocd := recstat.jyocd;
        lr.raceno := recstat.raceno;
        lr.bettype := recstat.bettype;
        lr.kumiban := recstat.bet_kumiban;
        lr.pattern_name := recstat.pattern_name;
        lr.pattern_value := recstat.pattern_value;
        lr.betcnt := counter;
	    if counter = 1 then
	      lr.hitcnt := recstat.hity;
	      lr.betamt := recstat.betamt;
	      lr.hitamt := recstat.hitamt;
	      lr.incomeamt := recstat.hitamt - recstat.betamt;
	    else
	      lr.hitcnt := lr.hitcnt + recstat.hity;
	      lr.betamt := lr.betamt + recstat.betamt;
	      lr.hitamt := lr.hitamt + recstat.hitamt;
	      lr.incomeamt := lr.incomeamt + (recstat.hitamt - recstat.betamt);
	    end if;
        lr.hitrate := cast( (cast(lr.hitcnt as float)/ cast(lr.betcnt as float) *100) as numeric(7,2));
        lr.incomerate := cast( (cast(lr.hitamt as float)/ cast(lr.betamt as float) *100) as numeric(7,2));
        lr.hitamt_nolinear = recstat.hitamt;
	    
	    insert into stat_ml_ptn_linear_analyze values 
	      (lr.description, lr.ymd, lr.sime, lr.jyocd, lr.raceno, lr.bettype, lr.kumiban, lr.pattern_name, lr.pattern_value,
	       lr.betcnt, lr.hitcnt, lr.betamt, lr.hitamt, lr.incomeamt, lr.hitrate, lr.incomerate, lr.hitamt_nolinear);
	    counter := counter + 1;
	end loop;
	close custat;

END
$$ LANGUAGE plpgsql;

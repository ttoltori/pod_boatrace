-- linear에 패턴갯수의 제한이 걸린 것에 주의할 것!!!
-- 이후의 lienear와 join하는 경우 stat가 아니라 linear의 필드와 파라미터를 join해야한다. 
DROP FUNCTION IF EXISTS create_stat_ml_ptn_linear (VARCHAR(200), VARCHAR(2), VARCHAR(4), VARCHAR(400), int);
CREATE OR REPLACE FUNCTION create_stat_ml_ptn_linear (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4),
paramPatternName VARCHAR(400),
paramMinPtnCnt int
) RETURNS VOID AS $$
DECLARE
	params text;
    cuptn refcursor; -- 패턴 커서
    recptn record; -- 패턴 레코드
    custat refcursor; -- 패턴결과테이블 커서
    recstat record; -- 패턴결과테이블 레코드
    ptncounter int; -- 패턴카운터
    lr stat_ml_ptn_linear%ROWTYPE; -- 축적결과테이블 행
    counter int; -- 행카운터
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban || '_' ||  paramPatternName;
    
    raise info 'delete stat_ml_ptn_linear';
	delete from stat_ml_ptn_linear
	where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName;
	
	raise info 'insert stat_ml_ptn_linear. %', params;
    -- 패턴에 대한 커서를 오픈해서 루프한다.
    ptncounter := 1;
    open cuptn for 
      select * from stat_ml_ptn_cnt
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban and pattern_name = paramPatternName
        and pattern_cnt >= paramMinPtnCnt;
	loop
	  fetch cuptn into recptn;
	  if not found then 
	    exit;
	  end if;
	  
	  -- 각 패턴값에 대한 투표결과를 시간순으로 루프한다.
	  counter := 1;
      open custat for 
	    select * from stat_ml_result_ptn statptn
	    where statptn.description = recptn.description 
	      and statptn.bettype = recptn.bettype 
	      and statptn.bet_kumiban = recptn.kumiban
	      and statptn.pattern_name = recptn.pattern_name
	      and statptn.pattern_value = recptn.pattern_value
	    order by statptn.ymd, statptn.sime;
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
	    
	    insert into stat_ml_ptn_linear values 
	      (lr.description, lr.ymd, lr.sime, lr.jyocd, lr.raceno, lr.bettype, lr.kumiban, lr.pattern_name, lr.pattern_value,
	       lr.betcnt, lr.hitcnt, lr.betamt, lr.hitamt, lr.incomeamt, lr.hitrate, lr.incomerate, lr.hitamt_nolinear);
	    counter := counter + 1;
	  end loop;
	  close custat;
	  
      ptncounter := ptncounter + 1;
	end loop;
	close cuptn;
END
$$ LANGUAGE plpgsql;

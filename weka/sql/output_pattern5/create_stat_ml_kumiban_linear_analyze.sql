DROP FUNCTION IF EXISTS create_stat_ml_kumiban_linear_analyze (VARCHAR(200), VARCHAR(2), VARCHAR(4));
CREATE OR REPLACE FUNCTION create_stat_ml_kumiban_linear_analyze (
paramDescription VARCHAR(200),
paramBettype VARCHAR(2),
paramKumiban VARCHAR(4)
) RETURNS VOID AS $$
DECLARE
    params text;
    csrlinear refcursor; -- 패턴linear테이블 커서
    reclinear record; -- 패턴linear테이블 레코드
    counter int; -- 행카운터
    lr stat_ml_kumiban_linear_analyze%ROWTYPE; -- 축적결과테이블 행
    last_income_amt int; -- 직전 수입금액
    hity int;
    minus_amt int; -- 누적적자금액
BEGIN
    params := paramDescription || '_' || paramBettype || '_' ||  paramKumiban;
    
    raise info 'delete stat_ml_kumiban_linear_analyze %', params;
    delete from stat_ml_kumiban_linear_analyze
      where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban;

    
    raise info 'insert stat_ml_kumiban_linear_analyze %', params;
    counter := 1;
	minus_amt := 0; -- 적자금액 초기화
    open csrlinear for 
		select 
		  description, bettype, kumiban, ymd, sime, jyocd, raceno, 
		  count(pattern_name) pattern_count,
		  (sum(hitamt_nolinear) / count(pattern_name)) hitamt
		from stat_ml_ptn_linear_analyze linear
		where description = paramDescription and bettype = paramBettype and kumiban = paramKumiban
		group by description, bettype, kumiban, ymd, sime, jyocd, raceno
		order by description, bettype, kumiban, ymd, sime, jyocd, raceno
		;
      
	loop
	    fetch csrlinear into reclinear;
	    if not found then 
	      exit;
	    end if;
	    
        lr.description := reclinear.description;
        lr.ymd := reclinear.ymd;
        lr.sime := reclinear.sime;
        lr.jyocd := reclinear.jyocd;
        lr.raceno := reclinear.raceno;
        lr.bettype := reclinear.bettype;
        lr.kumiban := reclinear.kumiban;
        lr.pattern_cnt := reclinear.pattern_count;
        lr.betcnt := counter;
        if reclinear.hitamt = 0 then
          hity := 0;
        else
          hity := 1;
        end if;
	    if counter = 1 then
	      lr.hitcnt := hity;
	      lr.betamt := 100;
	      lr.hitamt := reclinear.hitamt;
	      lr.incomeamt := lr.hitamt - lr.betamt;
		  if reclinear.hitamt = 0 then
			lr.recover := 0; -- 비적중
			minus_amt := minus_amt + lr.betamt; -- 적자누적 
 		  else
 		    lr.recover := 6; -- 적중흑자증가
          end if;
	      last_income_amt := lr.incomeamt;
	    else
	      lr.hitcnt := lr.hitcnt + hity;
	      lr.betamt := lr.betamt + 100;
	      lr.hitamt := lr.hitamt + reclinear.hitamt;
	      lr.incomeamt := lr.incomeamt + (reclinear.hitamt - 100);
	      lr.balance_changed := 0;
	      if lr.incomeamt < 0 then -- 적자
	    	if last_income_amt >= 0 then -- 흑자->적자전환
	    	  lr.balance_changed := -1;
	    	end if;
	      else
	    	if last_income_amt < 0 then -- 적자->흑자전환
	    	  lr.balance_changed := 1;
	    	end if;
	      end if;
	      
		  if reclinear.hitamt = 0 then
			lr.recover := 0; -- 비적중
 		    minus_amt := minus_amt + lr.betamt; -- 적자누적 
 		  else
 		    if minus_amt = 0 then
 		      lr.recover := 1; -- 연속적중
 		    else
		      if last_income_amt < 0 then --이전적자상태
		        if lr.balance_changed = 1 then -- 적자->흑자전환
		          lr.recover := 4; -- 적중흑자전환성공
		        else
		          lr.recover := 3; -- 적중흑자전환실패
		        end if;
		      else -- 이전 흑자상태
		        if lr.balance_changed = -1 then -- 흑자->적자전환
		          lr.recover := 2; -- 적중적자전환
		        else
		          if lr.hitamt > minus_amt then
		            lr.recover := 6; -- 적중흑자증가
		          else
		            lr.recover := 5; -- 적중흑자감소
		          end if;
		        end if;
		      end if;
 		    end if;
 		    minus_amt := 0; -- 적자누적초기화
          end if;
	      last_income_amt := lr.incomeamt;
	    end if;
        lr.hitrate := cast( (cast(lr.hitcnt as float)/ cast(lr.betcnt as float) *100) as numeric(7,2));
        lr.incomerate := cast( (cast(lr.hitamt as float)/ cast(lr.betamt as float) *100) as numeric(7,2));
        lr.hity_nolinear := hity;
        lr.betamt_nolinear := 100;
        lr.hitamt_nolinear = reclinear.hitamt;
	    
	    insert into stat_ml_kumiban_linear_analyze values 
	      (lr.description, lr.ymd, lr.sime, lr.jyocd, lr.raceno, lr.bettype, lr.kumiban, lr.pattern_cnt,
	       lr.betcnt, lr.hitcnt, lr.betamt, lr.hitamt, lr.incomeamt, lr.hitrate, lr.incomerate, 
	       lr.hity_nolinear, lr.betamt_nolinear, lr.hitamt_nolinear, lr.balance_changed, lr.recover);
	    counter := counter + 1;
	end loop;
	close csrlinear;

END
$$ LANGUAGE plpgsql;

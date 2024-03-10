-- kumibanl単位生成
DROP FUNCTION IF EXISTS ranking_5555_wk(int, int, VARCHAR(40), VARCHAR(250), VARCHAR(200), VARCHAR(1), VARCHAR(2)[], VARCHAR(3)); 
CREATE OR REPLACE FUNCTION ranking_5555_wk(
pFactorNo int,
pRanking int,
pFactorName  VARCHAR(40),
pFactorValue VARCHAR(250), 
pCond VARCHAR(200),
pResultType VARCHAR(1),
pBettypeList VARCHAR(2)[],
pKumiban VARCHAR(3)
)
RETURNS VOID AS $$
  DECLARE
    factor VARCHAR(40); 
  BEGIN
	factor := concat_ws('-', pFactorNo, pFactorName);
	
    raise notice 'start %', factor;
    
    EXECUTE format ('delete from ptn_ranking where factor = ''%s''', factor );
    
    if pKumiban != null then
	    EXECUTE format ('
			insert into ptn_ranking
			select
			  ranking, ''%s'', sr.*
			from
			(
			    select
			      *
			    from 
			    (
					select
					  row_number() over (partition by kumiban order by (srfactor) desc) as ranking, keycol2, kumiban, srfactor
					from
					(
						select
						  *
						from (
							select
							  keycol2, kumiban, ( %s ) srfactor 
						 	from 
							(
								select
								  s1.keycol
								from
								    (select keycol from stat5555_1 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' and patternid like ''%%wk%%'' ) s1
								  , (select keycol from stat5555_2 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' and patternid like ''%%wk%%'' ) s2
								  , (select keycol from stat5555_3 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' and patternid like ''%%wk%%'' ) s3
								where s1.keycol = s2.keycol and s1.keycol = s3.keycol
							) t123,
							stat5555_4 sr
							where t123.keycol = sr.keycol
							group by keycol2, kumiban
						) tmp2
					) tmp
				) tr
				where ranking <= %s
			) pidr,
			(
				select
				  s.* 
				from 
				(
					select
					  s1.keycol
					from
					    (select keycol from stat5555_1 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' and patternid like ''%%wk%%'' ) s1
					  , (select keycol from stat5555_2 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' and patternid like ''%%wk%%'' ) s2
					  , (select keycol from stat5555_3 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' and patternid like ''%%wk%%'' ) s3
					where s1.keycol = s2.keycol and s1.keycol = s3.keycol
				) t123,	stat5555_4 s
				where t123.keycol = s.keycol
			) sr
			where sr.keycol2 = pidr.keycol2
			order by ranking
		', factor, pFactorValue, pCond, pResultType, pKumiban, pCond, pResultType, pKumiban
		 , pCond, pResultType, pKumiban, pRanking
		 , pCond, pResultType, pKumiban, pCond, pResultType, pKumiban, pCond, pResultType, pKumiban
		) USING pBettypeList;
	else
	    EXECUTE format ('
			insert into ptn_ranking
			select
			  ranking, ''%s'', sr.*
			from
			(
			    select
			      *
			    from 
			    (
					select
					  row_number() over (partition by kumiban order by (srfactor) desc) as ranking, keycol2, kumiban, srfactor
					from
					(
						select
						  *
						from (
							select
							  keycol2, kumiban, ( %s ) srfactor 
						 	from 
							(
								select
								  s1.keycol
								from
								    (select keycol from stat5555_1 where (%s) and result_type = ''%s'' and bettype = any($1) and patternid like ''%%wk%%'' ) s1
								  , (select keycol from stat5555_2 where (%s) and result_type = ''%s'' and bettype = any($1) and patternid like ''%%wk%%'' ) s2
								  , (select keycol from stat5555_3 where (%s) and result_type = ''%s'' and bettype = any($1) and patternid like ''%%wk%%'' ) s3
								where s1.keycol = s2.keycol and s1.keycol = s3.keycol
							) t123,
							stat5555_4 sr
							where t123.keycol = sr.keycol
							group by keycol2, kumiban
						) tmp2
					) tmp
				) tr
				where ranking <= %s
			) pidr,
			(
				select
				  s.* 
				from 
				(
					select
					  s1.keycol
					from
					    (select keycol from stat5555_1 where (%s) and result_type = ''%s'' and bettype = any($1) and patternid like ''%%wk%%'' ) s1
					  , (select keycol from stat5555_2 where (%s) and result_type = ''%s'' and bettype = any($1) and patternid like ''%%wk%%'' ) s2
					  , (select keycol from stat5555_3 where (%s) and result_type = ''%s'' and bettype = any($1) and patternid like ''%%wk%%'' ) s3
					where s1.keycol = s2.keycol and s1.keycol = s3.keycol
				) t123,	stat5555_4 s
				where t123.keycol = s.keycol
			) sr
			where sr.keycol2 = pidr.keycol2
			order by ranking
		', factor, pFactorValue, pCond, pResultType, pCond, pResultType, pCond, pResultType, pRanking
		 , pCond, pResultType, pCond, pResultType, pCond, pResultType
		) USING pBettypeList;
	end if;
	
	EXECUTE format ('
		copy (
		select
		  ''~'' sel, ranking, factor, (case when result_type = ''1'' then ''ip,G3'' else ''SG,G1,G2'' end) grades, bettype, kumiban, modelno, patternid, pattern,
		  betcnt, incamt, hitrate, incrate,
		  ''x'' bonus_pr,  ''x'' bonus_bor,  ''x'' bonus_bork, ''x'' range_selector, ''x'' bonus_borkbor
		from ptn_ranking pr 
		where factor = ''%s''
		order by kumiban, ranking
		) to ''D:\simul_TSV\groups_test\%s.tsv'' with csv header delimiter E''\t''
    ', factor, factor);
    
  END;
$$ LANGUAGE plpgsql;

-- kumibanl単位生成
DROP FUNCTION IF EXISTS s5_generate_ranking(VARCHAR(20), VARCHAR(250), VARCHAR(200), int, VARCHAR(1), VARCHAR(2)[], VARCHAR(3)); 
CREATE OR REPLACE FUNCTION s5_generate_ranking(
pFactorName  VARCHAR(20),
pFactorValue VARCHAR(250), 
pCond VARCHAR(200),
pRanking int,
pResultType VARCHAR(1),
pBettypeList VARCHAR(2)[],
pKumiban VARCHAR(3)
)
RETURNS VOID AS $$
  begin
	  
	raise notice 'delete ptn_ranking %', pFactorName;

    EXECUTE format ('delete from ptn_ranking where factor = ''%s''', pFactorName);
   
	raise notice 'insert ptn_ranking %...', pFactorName;
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
							    (select keycol from stat6_1 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' ) s1
							  , (select keycol from stat6_2 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' ) s2
							  , (select keycol from stat6_3 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' ) s3
							where s1.keycol = s2.keycol and s1.keycol = s3.keycol
						) t123,
						stat6_4 sr
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
				    (select keycol from stat6_1 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' ) s1
				  , (select keycol from stat6_2 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' ) s2
				  , (select keycol from stat6_3 where (%s) and result_type = ''%s'' and bettype = any($1) and kumiban = ''%s'' ) s3
				where s1.keycol = s2.keycol and s1.keycol = s3.keycol 
			) t123,	stat6_4 s
			where t123.keycol = s.keycol
		) sr
		where sr.keycol2 = pidr.keycol2
		order by ranking
	', pFactorName, pFactorValue, pCond, pResultType, pKumiban, pCond, pResultType, pKumiban, pCond, pResultType, pKumiban, pRanking
	 , pCond, pResultType, pKumiban, pCond, pResultType, pKumiban, pCond, pResultType, pKumiban
	) USING pBettypeList;

	raise notice 'copy ptn_ranking %...', pFactorName;

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
    ', pFactorName, pFactorName);
  END;
$$ LANGUAGE plpgsql;

-- kumibanl単位生成
DROP FUNCTION IF EXISTS s1a_generate_ranking(VARCHAR(20), int, VARCHAR(250), VARCHAR(200), int, VARCHAR(1), VARCHAR(2)[]); 
CREATE OR REPLACE FUNCTION s1a_generate_ranking (
pFactorName  VARCHAR(20),
pFactorNo int,
pFactorValue VARCHAR(250), 
pCond VARCHAR(200),
pRanking int,
pResultType VARCHAR(1),
pBettypeList VARCHAR(2)[]
)
RETURNS VOID AS $$
  begin
	  
    EXECUTE format ('delete from ptn_ranking where factor = ''%s-%s''', pFactorName, pFactorNo);
    EXECUTE format ('
		insert into ptn_ranking
		select
		  ranking, ''%s-%s'', sr.*
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
							    (select keycol from stat_bork1 where (%s) and result_type = ''%s'' and bettype = any($1) ) s1
							  , (select keycol from stat_bork2 where (%s) and result_type = ''%s'' and bettype = any($1) ) s2
							where s1.keycol = s2.keycol 
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
				    (select keycol from stat_bork1 where (%s) and result_type = ''%s'' and bettype = any($1) ) s1
				  , (select keycol from stat_bork2 where (%s) and result_type = ''%s'' and bettype = any($1) ) s2
				where s1.keycol = s2.keycol 
			) t123,	stat6_4 s
			where t123.keycol = s.keycol
		) sr
		where sr.keycol2 = pidr.keycol2
		order by ranking
	', pFactorName, pFactorNo, pFactorValue, pCond, pResultType, pCond, pResultType, pRanking
	 , pCond, pResultType, pCond, pResultType
	) USING pBettypeList;

	raise notice 'copy ptn_ranking %...', pFactorName;

	EXECUTE format ('
		copy (
		select
		  ''~'' sel, ranking, factor, (case when result_type = ''1'' then ''ip,G3'' else ''SG,G1,G2'' end) grades, bettype, kumiban, modelno, patternid, pattern,
		  betcnt, incamt, hitrate, incrate,
		  ''x'' bonus_pr,  ''x'' bonus_bor,  ''x'' bonus_bork, ''x'' range_selector, ''x'' bonus_borkbor
		from ptn_ranking pr 
		where factor = ''%s-%s''
		order by kumiban, ranking
		) to ''D:\simul_TSV\groups_test\%s-%s.tsv'' with csv header delimiter E''\t''
    ', pFactorName, pFactorNo, pFactorName, pFactorNo);
   

  END;
$$ LANGUAGE plpgsql;
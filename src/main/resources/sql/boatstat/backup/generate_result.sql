
DROP FUNCTION IF EXISTS generate_result(
VARCHAR(8),
VARCHAR(8), 
VARCHAR(20),
VARCHAR(20),
VARCHAR(120),
VARCHAR(120), 
VARCHAR(120) 
);

CREATE OR REPLACE FUNCTION generate_result(
paramFromYmd  VARCHAR(8),
paramToYmd VARCHAR(8), 
paramBetTypeName VARCHAR(20),
paramTable VARCHAR(20),
paramPattern VARCHAR(120),
paramGroupCol VARCHAR(120), 
paramOrderBy VARCHAR(120))
RETURNS TEXT AS $$
  
    EXECUTE format ('
		select 
		  grouping, betamount, income, balance, 
		  cast( (cast(hitcnt as float)/ cast(betcnt as float) *100) as numeric(5,2)) hitrate,
		  cast( (cast(income as float)/ cast(betamount as float) *100) as numeric(5,2)) incomerate
		from (
			select 
			 (''%s'') as grouping,
			 sum ( case %sno when zen.kumiban then 100 else 100 end ) betamount,
			 sum ( case %sno when zen.kumiban then %sprize else 0 end ) income,
			 sum ( case %sno when zen.kumiban then 1 else 1 end ) betcnt,
			 sum ( case %sno when zen.kumiban then 1 else 0 end ) hitcnt,
			 sum ( case %sno when zen.kumiban then %sprize - 100 else -100 end ) balance
			from %s race, zen_filter zen
			where race.ymd >= ''%s'' and race.ymd <= ''%s''
			  and ''%s'' = zen.pattern
			group by grouping
			order by grouping
		) tmp'
		, paramGroupCol
		, paramBetTypeName, paramBetTypeName, paramBetTypeName, paramBetTypeName, paramBetTypeName, paramBetTypeName, paramBetTypeName
		, paramTable
		, paramFromYmd, paramToYmd
		, paramPattern
	);
       
$$ LANGUAGE plpgsql;

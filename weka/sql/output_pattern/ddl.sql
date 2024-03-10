drop table if exists stat_ml_result_ptn;
create table stat_ml_result_ptn (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
bet_odds numeric(7,2),
bet_oddsrank int,
result_kumiban varchar(4),
result_odds numeric(7,2),
result_oddsrank int,
hity int, 
hitn int, 
betamt int,
hitamt int,
pattern_name varchar(400),
pattern_value varchar(400) 
);
drop index if exists indexes_stat_ml_result_ptn;
create index indexes_stat_ml_result_ptn on stat_ml_result_ptn (description, ymd, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_daily;
create table stat_ml_ptn_daily (
description varchar(200),
ymd varchar(8),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2), 
totalrate numeric(7,2) -- totalrate_slopeを求めるために必要
);
create index indexes_stat_ml_ptn_daily on stat_ml_ptn_daily (description, bettype, kumiban, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_monthly;
create table stat_ml_ptn_monthly (
description varchar(200),
yyyy varchar(4),
mm varchar(2),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2)
);
create index indexes_stat_ml_ptn_monthly on stat_ml_ptn_monthly (description, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_yearly;
create table stat_ml_ptn_yearly (
description varchar(200),
yyyy varchar(4),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2)
);
create index indexes_stat_ml_ptn_yearly on stat_ml_ptn_yearly (description, bettype, kumiban, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_totally;
create table stat_ml_ptn_totally (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2)
);
create index indexes_stat_ml_ptn_totally on stat_ml_ptn_totally (description, bettype, kumiban, pattern_name, pattern_value);


delete from stat_ml_ptn_final where description = '' and bettype = '' and kumiban = '' and patern_name=''
drop table if exists stat_ml_ptn_final;
create table stat_ml_ptn_final (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
betrate numeric(7,3),
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2),
betrate_slope numeric(7,3),
hitrate_slope numeric(7,3),
incomerate_slope numeric(7,3),
totalrate_slope numeric(7,3),
daily_bet_count int,
daily_plus_count int,
daily_plus_rate numeric(7,2),
monthly_bet_count int,
monthly_plus_count int,
monthly_plus_rate numeric(7,2),
stable_years_count int 
);
drop index if exists indexes_stat_ml_ptn_final;
create index indexes_stat_ml_ptn_final on stat_ml_ptn_final (description, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_analyze cascade;
create table stat_ml_ptn_analyze2 (
bettype varchar(2),
kumiban varchar(4),
modelno varchar(4),
modelattr varchar(40),
pattern_name varchar(40),
pattern_value varchar(400),
yyyy varchar(4),
mm varchar(2),
betcount_daily int,
betamt int,
incomeamt int,
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2),
range_oddsrank int[],
range_odds numeric(7,2)[],
range_totalrate numeric(7,2)[],
range_hitrate numeric(7,2)[],
range_incomerate numeric(7,2)[],
description varchar(400),
isfinal varchar(1),
ismonthly varchar(1),
updatetime timestamp default CURRENT_TIMESTAMP
);
create index indexes_stat_ml_ptn_analyze on stat_ml_ptn_analyze (bettype, kumiban, modelno, modelattr, pattern_name);

drop table if exists stat_ml_ptn_filtered cascade;
create table stat_ml_ptn_filtered (
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(40),
betcount_daily int,
betamt int,
incomeamt int,
hitrate double precision,
incomerate double precision,
totalrate double precision,
min_oddsrank int,
max_oddsrank int,
min_odds  double precision,
max_odds  double precision,
pattern_value varchar(400),
ptn_betamt int,
ptn_incomeamt int,
ptn_hitrate double precision,
ptn_incomerate double precision,
ptn_totalrate double precision,
ptn_daily_bet_count int,
ptn_daily_plus_rate double precision,
hitrate_slope double precision,
incomerate_slope double precision,
totalrate_slope double precision
);
create index indexes_stat_ml_ptn_filtered on stat_ml_ptn_filtered (bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_result_weka;
create table stat_ml_result_weka (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
bet_odds numeric(7,2),
bet_oddsrank int,
result_kumiban varchar(4),
result_odds numeric(7,2),
result_oddsrank int,
hity int, 
hitn int, 
betamt int,
hitamt int,
pattern_name_count int,
pattern_value_count int 
);
create index indexes_stat_ml_result_weka on stat_ml_result_weka (ymd, jyocd, raceno, description);

drop table if exists stat_ml_result_wekaptn;
create table stat_ml_result_wekaptn (
ymd varchar(8),
jyocd varchar(2),
raceno smallint,
description varchar(400),
bettype varchar(2),
bet_kumiban varchar(4),
pattern_name varchar(40),
pattern_value varchar(400)
);
create index indexes_stat_ml_result_wekaptn on stat_ml_result_wekaptn (ymd, jyocd, raceno, description,bettype,bet_kumiban );

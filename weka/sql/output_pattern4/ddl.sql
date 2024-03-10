drop table if exists stat_ml_result_ptn;
create table stat_ml_result_ptn (
ymd varchar(8),
sime varchar(4),
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
create index indexes_stat_ml_result_ptn on stat_ml_result_ptn (description, ymd, jyocd, raceno, pattern_name, pattern_value);

drop table if exists stat_ml_result_ptn_analyze;
create table stat_ml_result_ptn_analyze (
ymd varchar(8),
sime varchar(4),
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
create index indexes_stat_ml_result_ptn_analyze on stat_ml_result_ptn_analyze (description, ymd, jyocd, raceno, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_cnt;
create table stat_ml_ptn_cnt (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
pattern_cnt int
);
create index indexes_stat_ml_ptn_cnt on stat_ml_ptn_cnt (description, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_linear;
create table stat_ml_ptn_linear (
description varchar(200),
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int,
betamt int, 
hitamt int, 
incomeamt int, 
hitrate numeric(7,2),
incomerate numeric(7,2),
hitamt_nolinear int,
balance_changed int -- 흑자전환=1, 적자전환=-1, 그외=0
);
create index indexes_stat_ml_ptn_linear on stat_ml_ptn_linear (description, ymd, sime, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_linear_bak;
create table stat_ml_ptn_linear_bak (
description varchar(200),
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int,
betamt int, 
hitamt int, 
incomeamt int, 
hitrate numeric(7,2),
incomerate numeric(7,2),
hitamt_nolinear int,
balance_changed int -- 흑자전환=1, 적자전환=-1, 그외=0
);
create index indexes_stat_ml_ptn_linear_bak on stat_ml_ptn_linear_bak (description, ymd, sime, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_linear_analyze;
create table stat_ml_ptn_linear_analyze (
description varchar(200),
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
hitrate numeric(7,2),
incomerate numeric(7,2),
hitamt_nolinear int,
balance_changed int -- 흑자전환=1, 적자전환=-1, 그외=0
);
create index indexes_stat_ml_ptn_linear_analyze on stat_ml_ptn_linear_analyze (description, ymd, sime, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_kumiban_linear_analyze;
create table stat_ml_kumiban_linear_analyze (
description varchar(200),
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
bettype varchar(2),
kumiban varchar(4),
pattern_cnt int,
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int, 
incomeamt int, 
hitrate numeric(7,2),
incomerate numeric(7,2),
hity_nolinear int,
betamt_nolinear int,
hitamt_nolinear int,
balance_changed int -- 흑자전환=1, 적자전환=-1, 그외=0
);
create index indexes_stat_ml_kumiban_linear_analyze on stat_ml_kumiban_linear_analyze (description, ymd, sime, bettype, kumiban);

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
totalrate numeric(10,2) -- totalrate_slopeを求めるために必要
);
create index indexes_stat_ml_ptn_daily on stat_ml_ptn_daily (description, bettype, kumiban, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_totally_stmp;
create table stat_ml_ptn_totally_stmp (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
betcnt int, 
hitcnt int, 
betamt int, 
hitamt int 
);
create index indexes_stat_ml_ptn_totally_stmp on stat_ml_ptn_totally_stmp (description, bettype, kumiban, pattern_name, pattern_value);

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

drop table if exists stat_ml_ptn_metric cascade;
create table stat_ml_ptn_metric (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
pattern_value varchar(400),
minus_days int, -- 적자 일수
plus_days int, -- 흑자 일수
minus_changed_count int, -- 적자전환 횟수
plus_changed_count int, -- 흑자전환 횟수
minus_days_rate  numeric(7,2), -- 전체일수에 대한 적자일수 비율
linear_betamt_slope numeric(10,5),
linear_hitamt_slope numeric(10,2),
linear_incomeamt_slope numeric(10,5),
linear_hitrate_slope numeric(10,5),
linear_incomerate_slope numeric(10,5),
hitamt_mod int,
hitamt_percent int,
hitamt_sum_over int,
hitamt_sum_under int,
hitamt_bias_rate numeric(7,2),
hit_bet_slope_rate numeric(7,2)
);
create index indexes_stat_ml_ptn_metric on stat_ml_ptn_metric (description, bettype, kumiban, pattern_name, pattern_value);


drop table if exists stat_ml_ptn_metric_analyze cascade;
create table stat_ml_ptn_metric_analyze (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(400),
minus_days int, -- 적자 일수
plus_days int, -- 흑자 일수
minus_changed_count int, -- 적자전환 횟수
plus_changed_count int, -- 흑자전환 횟수
minus_days_rate  numeric(7,2), -- 전체일수에 대한 적자일수 비율
linear_betamt_slope numeric(10,5),
linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
linear_hitrate_slope numeric(10,5),
linear_incomerate_slope numeric(10,5),
hitamt_mod int,
hitamt_percent int,
hitamt_sum_over int,
hitamt_sum_under int,
hitamt_bias_rate numeric(7,2),
hit_bet_slope_rate numeric(7,2)
);
create index indexes_stat_ml_ptn_metric_analyze on stat_ml_ptn_metric_analyze (description, bettype, kumiban, pattern_name);


drop table if exists stat_ml_kumiban_metric_analyze cascade;
create table stat_ml_kumiban_metric_analyze (
description varchar(200),
bettype varchar(2),
kumiban varchar(4),
pattern_cnt_avg numeric(7,2), -- 평균패턴중복수
minus_days int, -- 적자 일수
plus_days int, -- 흑자 일수
minus_changed_count int, -- 적자전환 횟수
plus_changed_count int, -- 흑자전환 횟수
minus_days_rate  numeric(7,2), -- 전체일수에 대한 적자일수 비율
linear_betamt_slope numeric(10,5),
linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
linear_hitrate_slope numeric(10,5),
linear_incomerate_slope numeric(10,5),
hitamt_mod int,
hitamt_percent int,
hitamt_sum_over int,
hitamt_sum_under int,
hitamt_bias_rate numeric(7,2),
hit_bet_slope_rate numeric(7,2)
);
create index indexes_stat_ml_kumiban_metric_analyze on stat_ml_kumiban_metric_analyze (description, bettype, kumiban);

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
hitamt_bias_rate numeric(7,2),
hit_bet_slope_rate numeric(7,2),
minus_days int, -- 적자 일수
plus_days int, -- 흑자 일수
minus_changed_count int, -- 적자전환 횟수
plus_changed_count int, -- 흑자전환 횟수
minus_days_rate  numeric(7,2), -- 전체일수에 대한 적자일수 비율
linear_betamt_slope numeric(10,5),
linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
linear_hitrate_slope numeric(10,5),
linear_incomerate_slope numeric(10,5),
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
days_bet int,
days_plus int,
days_plus_rate numeric(7,2),
betcnt_limit int
);
create index indexes_stat_ml_ptn_final on stat_ml_ptn_final (description, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stmp_stat_ml_ptn_final;
create table stmp_stat_ml_ptn_final (
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
hitamt_bias_rate numeric(7,2),
hit_bet_slope_rate numeric(7,2),
minus_days int, -- 적자 일수
plus_days int, -- 흑자 일수
minus_changed_count int, -- 적자전환 횟수
plus_changed_count int, -- 흑자전환 횟수
minus_days_rate  numeric(7,2), -- 전체일수에 대한 적자일수 비율
linear_betamt_slope numeric(10,5),
linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
linear_hitrate_slope numeric(10,5),
linear_incomerate_slope numeric(10,5),
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
days_bet int,
days_plus int,
days_plus_rate numeric(7,2),
betcnt_limit int
);
create index indexes_stmp_stat_ml_ptn_final on stmp_stat_ml_ptn_final (description, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_analyze cascade;
create table stat_ml_ptn_analyze (
description varchar(400),
bettype varchar(2),
kumiban varchar(4),
pattern_name varchar(40),
betcount_daily numeric(7,2),
betcnt int,
hitcnt int,
betamt int,
hitamt int,
incomeamt int,
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2),
hitamt_bias_rate numeric(7,2),
hit_bet_slope_rate numeric(7,2),
minus_days int, -- 적자 일수
plus_days int, -- 흑자 일수
minus_changed_count int, -- 적자전환 횟수
plus_changed_count int, -- 흑자전환 횟수
minus_days_rate  numeric(7,2), -- 전체일수에 대한 적자일수 비율
linear_betamt_slope numeric(10,5),
linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
linear_hitrate_slope numeric(10,5),
linear_incomerate_slope numeric(10,5),
condition varchar(400),
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
range_oddsrank int[],
range_odds numeric(7,2)[],
range_totalrate numeric(7,2)[],
range_hitrate numeric(7,2)[],
range_incomerate numeric(7,2)[],
range_biasrate numeric(7,2)[],
range_minusdaysrate numeric(7,2)[],
max_minuschangedcount int,
range_betcntlimit int[],
isfinal varchar(1),
isCustom varchar(1),
updatetime timestamp default CURRENT_TIMESTAMP
);
create index indexes_stat_ml_ptn_analyze on stat_ml_ptn_analyze (bettype, kumiban, pattern_name);


drop table if exists stat_ml_kumiban_analyze cascade;
create table stat_ml_kumiban_analyze (
description varchar(400),
bettype varchar(2),
kumiban varchar(4),
betcount_daily numeric(7,2),
betcnt int,
hitcnt int,
betamt int,
hitamt int,
incomeamt int,
hitrate numeric(7,2),
incomerate numeric(7,2),
totalrate numeric(7,2),
pattern_cnt_avg numeric(7,2), -- 평균 패턴중복횟수
hitamt_bias_rate numeric(7,2),
hit_bet_slope_rate numeric(7,2),
minus_days int, -- 적자 일수
plus_days int, -- 흑자 일수
minus_changed_count int, -- 적자전환 횟수
plus_changed_count int, -- 흑자전환 횟수
minus_days_rate  numeric(7,2), -- 전체일수에 대한 적자일수 비율
linear_betamt_slope numeric(10,5),
linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
linear_hitrate_slope numeric(10,5),
linear_incomerate_slope numeric(10,5),
condition varchar(400),
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
range_oddsrank int[],
range_odds numeric(7,2)[],
range_totalrate numeric(7,2)[],
range_hitrate numeric(7,2)[],
range_incomerate numeric(7,2)[],
range_biasrate numeric(7,2)[],
range_minusdaysrate numeric(7,2)[],
max_minuschangedcount int,
range_betcntlimit int[],
isfinal varchar(1),
isCustom varchar(1),
updatetime timestamp default CURRENT_TIMESTAMP
);
create index indexes_stat_ml_kumiban_analyze on stat_ml_kumiban_analyze (bettype, kumiban);

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
hitrate_slope double precision,
incomerate_slope double precision,
incomeamt_slope double precision,
min_oddsrank int,
max_oddsrank int,
min_odds double precision,
max_odds double precision,
pattern_value varchar(400),
ptn_betamt int,
ptn_incomeamt int,
ptn_hitrate double precision,
ptn_incomerate double precision,
ptn_totalrate double precision,
ptn_days_bet int,
ptn_days_plus_rate double precision,
ptn_hitrate_slope double precision,
ptn_incomerate_slope double precision,
ptn_incomeamt_slope double precision
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

-- 2019/9/8 패턴 추출 절차를 간소화하기 위해 추가
drop table if exists st_ptn_final;
create table st_ptn_final (
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
betrate double precision,
hitrate double precision,
incomerate double precision,
minus_balance_count int, -- 적자투표수
plus_balance_count int,  -- 흑자 투표수
minus_balance_changed_count int, -- 적자전환 횟수
plus_balance_changed_count int, -- 적자전환 횟수
minus_balance_cnt_rate double precision, -- 전체투표수에 대한 적자투표수 비율
slope_incomeamt double precision,  -- slope 수익
intercept_incomeamt double precision  -- intercept 수익
);
create index indexes_st_ptn_final on st_ptn_final (description, bettype, kumiban, pattern_name, pattern_value);


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
result_amt int, -- 20190818 추가
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
result_amt int, -- 20190818 추가
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
balance_changed int, -- 흑자전환=1, 적자전환=-1, 그외=0
recover int -- 적중한 시점에서 적자를 만회했는지 여부 0=비적중 1=적중적자만회 2=연속적중 -1=적중적자만회실패
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
balance_changed int, -- 흑자전환=1, 적자전환=-1, 그외=0
recover int -- 적중한 시점에서 적자를 만회했는지 여부 0=비적중 1=적중적자만회 2=연속적중 -1=적중적자만회실패
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
balance_changed int, -- 흑자전환=1, 적자전환=-1, 그외=0
recover int -- 적중한 시점에서 적자를 만회했는지 여부 0=비적중 1=연속적중 2=적중적자전환 3=적중흑자전환실패 4=적중흑자전환성공 5=적중흑자감소 6=적중흑자증가
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
balance_changed int, -- 흑자전환=1, 적자전환=-1, 그외=0
recover int -- 적중한 시점에서 적자를 만회했는지 여부 0=비적중 1=연속적중 2=적중적자전환 3=적중흑자전환실패 4=적중흑자전환성공 5=적중흑자감소 6=적중흑자증가
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
minus_days int, -- 적자 투표수
plus_days int, -- 흑자 투표수
minus_changed_count int, -- 적자전환 횟수
plus_changed_count int, -- 흑자전환 횟수
minus_days_rate numeric(7,2), -- 전체투표에 대한 적자투표 비율
-- linear_betamt_slope numeric(10,5),
-- linear_hitamt_slope numeric(10,2),
linear_incomeamt_slope numeric(10,5),
recover_success_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환투표수 비율
recover_fail_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환실패투표수 비율
recover_plus_rate numeric(7,2), -- 적중투표수에 대한 흑자증가투표수 비율
recover_minus_rate numeric(7,2), -- 적중투표수에 대한 흑자감소투표수 비율
-- linear_hitrate_slope numeric(10,5),
-- linear_incomerate_slope numeric(10,5),
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
-- linear_betamt_slope numeric(10,5),
-- linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
recover_success_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환투표수 비율
recover_fail_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환실패투표수 비율
recover_plus_rate numeric(7,2), -- 적중투표수에 대한 흑자증가투표수 비율
recover_minus_rate numeric(7,2), -- 적중투표수에 대한 흑자감소투표수 비율
-- linear_hitrate_slope numeric(10,5),
-- linear_incomerate_slope numeric(10,5),
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
-- linear_betamt_slope numeric(10,5),
-- linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
recover_success_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환투표수 비율
recover_fail_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환실패투표수 비율
recover_plus_rate numeric(7,2), -- 적중투표수에 대한 흑자증가투표수 비율
recover_minus_rate numeric(7,2), -- 적중투표수에 대한 흑자감소투표수 비율
-- linear_hitrate_slope numeric(10,5),
-- linear_incomerate_slope numeric(10,5),
hitamt_mod int,
hitamt_percent int,
hitamt_sum_over int,
hitamt_sum_under int,
hitamt_bias_rate numeric(7,2),
hit_bet_slope_rate numeric(7,2)
);
create index indexes_stat_ml_kumiban_metric_analyze on stat_ml_kumiban_metric_analyze (description, bettype, kumiban);

drop table if exists stat_ml_ptn_final_alltype;
create table stat_ml_ptn_final_alltype (
mltype varchar(20),
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
-- linear_betamt_slope numeric(10,5),
-- linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
-- linear_hitrate_slope numeric(10,5),
-- linear_incomerate_slope numeric(10,5),
recover_success_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환투표수 비율
recover_fail_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환실패투표수 비율
recover_plus_rate numeric(7,2), -- 적중투표수에 대한 흑자증가투표수 비율
recover_minus_rate numeric(7,2), -- 적중투표수에 대한 흑자감소투표수 비율
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
days_bet int,
days_plus int,
days_plus_rate numeric(7,2),
betcnt_limit int
);
create index indexes_stat_ml_ptn_final_alltype on stat_ml_ptn_final_alltype (mltype, description, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stat_ml_ptn_final;
create table stat_ml_ptn_final (
yyyy varchar(4), -- 20190818 entry1방식에 대한 manual finalize를 위해 년도를 추가
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
linear_incomeamt_slope numeric(10,5),
recover_success_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환투표수 비율
recover_fail_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환실패투표수 비율
recover_plus_rate numeric(7,2), -- 적중투표수에 대한 흑자증가투표수 비율
recover_minus_rate numeric(7,2), -- 적중투표수에 대한 흑자감소투표수 비율
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
days_bet int, -- 20190814 해당 레코드의 수익성 등급으로 대용
days_plus int,
days_plus_rate numeric(7,2),
betcnt_limit int,
level int -- 해당 레코드의 수익성 전망 등급 20190814추가
);
create index indexes_stat_ml_ptn_final on stat_ml_ptn_final (description, bettype, kumiban, pattern_name, pattern_value);

drop table if exists stmp_stat_ml_ptn_final;
create table stmp_stat_ml_ptn_final (
yyyy varchar(4), -- 20190818 entry1방식에 대한 manual finalize를 위해 년도를 추가
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
linear_incomeamt_slope numeric(10,5),
recover_success_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환투표수 비율
recover_fail_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환실패투표수 비율
recover_plus_rate numeric(7,2), -- 적중투표수에 대한 흑자증가투표수 비율
recover_minus_rate numeric(7,2), -- 적중투표수에 대한 흑자감소투표수 비율
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
days_bet int, -- 20190814 해당 레코드의 수익성 등급으로 대용
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
incomeamtslpoe_percent numeric(7,2), -- final조건 수익금액상승기울기 상위비율
recoverplus_percent numeric(7,2), -- final조건 적중흑자증가투표비율 상위비율
betcnt_percent numeric(7,2), -- final조건 투표회수 상위 비율
hitrate_percent numeric(7,2), -- final조건 적중률 상위 비율
incomerate_percent numeric(7,2), -- final조건 수익률 상위 비율
minus_days_percent numeric(7,2), -- final조건 적자일수 상위 비율
linear_incomeamt_slope numeric(10,5), -- -1
recover_success_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환투표수 비율
recover_fail_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환실패투표수 비율
recover_plus_rate numeric(7,2), -- 적중투표수에 대한 흑자증가투표수 비율
recover_minus_rate numeric(7,2), -- 적중투표수에 대한 흑자감소투표수 비율
condition varchar(400), -- final조건 + analyze조건
hitamt_sum_under int,
hitamt_sum_over int,
hitamt_mod int,
hitamt_percent int,
range_oddsrank int[],
range_odds numeric(7,2)[],
range_totalrate numeric(7,2)[], -- default
range_hitrate numeric(7,2)[],  -- default
range_incomerate numeric(7,2)[],  -- default
range_biasrate numeric(7,2)[],  -- default
range_minusdaysrate numeric(7,2)[],  -- default
max_minuschangedcount int,  -- default
range_betcntlimit int[],  -- default
isfinal varchar(1),
isCustom varchar(1),
updatetime timestamp default CURRENT_TIMESTAMP
);
create index indexes_stat_ml_ptn_analyze on stat_ml_ptn_analyze (bettype, kumiban, pattern_name, condition);


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
incomeamtslpoe_percent numeric(7,2), -- final조건 수익금액상승기울기 상위비율
recoverplus_percent numeric(7,2), -- final조건 적중흑자증가투표비율 상위비율
betcnt_percent numeric(7,2), -- final조건 투표회수 상위 비율
hitrate_percent numeric(7,2), -- final조건 적중률 상위 비율
incomerate_percent numeric(7,2), -- final조건 수익률 상위 비율
minus_days_percent numeric(7,2), -- final조건 적자일수 상위 비율
-- linear_betamt_slope numeric(10,5),
-- linear_hitamt_slope numeric(10,5),
linear_incomeamt_slope numeric(10,5),
recover_success_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환투표수 비율
recover_fail_rate numeric(7,2), -- 적중투표수에 대한 적자=>흑자전환실패투표수 비율
recover_plus_rate numeric(7,2), -- 적중투표수에 대한 흑자증가투표수 비율
recover_minus_rate numeric(7,2), -- 적중투표수에 대한 흑자감소투표수 비율
-- linear_hitrate_slope numeric(10,5),
-- linear_incomerate_slope numeric(10,5),
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
create index indexes_stat_ml_kumiban_analyze on stat_ml_kumiban_analyze (bettype, kumiban, condition);



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
result_amt int,
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


drop table if exists stat_ml_result;
create table stat_ml_result (
ymd varchar(8),
sime varchar(4),
jyocd varchar(2),
raceno smallint,
description varchar(200),
bettype varchar(2),
bet_kumiban varchar(4),
bet_odds numeric(7,2),
bet_oddsrank int,
result_kumiban varchar(4),
result_odds numeric(7,2),
result_oddsrank int,
result_amt int, -- 20190818 추가
hity int, 
hitn int, 
betamt int,
hitamt int
);
create index indexes_stat_ml_result on stat_ml_result (ymd, jyocd, raceno, description, bettype);

create index indexes_stat_ml_result on stat_ml_result (ymd, jyocd, raceno, description, bettype);


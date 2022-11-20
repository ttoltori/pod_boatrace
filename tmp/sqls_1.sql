-- rank1のtraining data sql
BEGIN arff_sql_rank
  select 'nopattern' pattern, \
  race.ymd, sanrentanprize::double precision, \
  {features}, \
  {class_features} \
  from rec_race race, rec_race_waku waku, rec_race_waku2 waku2 \
  where race.ymd = waku.ymd and race.jyocd = waku.jyocd and race.raceno = waku.raceno \
    and race.ymd = waku2.ymd and race.jyocd = waku2.jyocd and race.raceno = waku2.raceno \
    and sanrentanno <> '不成立' \
    and grade in ({grade_condition}) \
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' \
    and ({class_condition}) \
  order by pattern, race.ymd, race.sime
END

arff_sql_rank1 = \
  select 'nopattern' pattern, \
  race.ymd, sanrentanprize::double precision, \
  {features}, \
  {class_features} \
  from rec_race race, rec_race_waku waku, rec_race_waku2 waku2 \
  where race.ymd = waku.ymd and race.jyocd = waku.jyocd and race.raceno = waku.raceno \
    and race.ymd = waku2.ymd and race.jyocd = waku2.jyocd and race.raceno = waku2.raceno \
    and sanrentanno <> '不成立' \
    and grade in ({grade_condition}) \
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' \
    and ({class_condition}) \
  order by pattern, race.ymd, race.sime

#rank2のtraining data sql
arff_sql_rank2 = \
  select 'nopattern' pattern, \
  race.ymd, sanrentanprize::double precision, \
  {features}, \
  {class_features} \
  from rec_race race, rec_race_waku waku, rec_race_waku2 waku2 \
  where race.ymd = waku.ymd and race.jyocd = waku.jyocd and race.raceno = waku.raceno \
    and race.ymd = waku2.ymd and race.jyocd = waku2.jyocd and race.raceno = waku2.raceno \
    and sanrentanno <> '不成立' \
    and grade in ({grade_condition}) \
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' \
    and ({class_condition}) \
  order by pattern, race.ymd, race.sime

#rank3のtraining data sql
arff_sql_rank3 = \
  select 'nopattern' pattern, \
  race.ymd, sanrentanprize::double precision, \
  {features}, \
  {class_features} \
  from rec_race race, rec_race_waku waku, rec_race_waku2 waku2 \
  where race.ymd = waku.ymd and race.jyocd = waku.jyocd and race.raceno = waku.raceno \
    and race.ymd = waku2.ymd and race.jyocd = waku2.jyocd and race.raceno = waku2.raceno \
    and sanrentanno <> '不成立' \
    and grade in ({grade_condition}) \
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' \
    and ({class_condition}) \
  order by pattern, race.ymd, race.sime

#classification data sql
classification_sql = \
  select 'nopattern' pattern, \
  race.ymd, race.jyocd, race.raceno, race.sime, \ 
  {features} \
  from rec_race race, rec_race_waku waku, rec_race_waku2 waku2 \
  where race.ymd = waku.ymd and race.jyocd = waku.jyocd and race.raceno = waku.raceno \
    and race.ymd = waku2.ymd and race.jyocd = waku2.jyocd and race.raceno = waku2.raceno \
    and sanrentanno <> '不成立' \
    and grade in ({grade_condition}) \
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' \
  order by pattern, race.ymd, race.sime

#result data sql ml_classification分析後確定する
result_sql_1 = \
  select 'nopattern' pattern, \
  cls.modelno, race.ymd, race.jyocd, race.raceno, race.sime, \ 
    tansyono, nirentanno, nirenhukuno, sanrentanno, sanrenhukuno, \
    tansyoprize, nirentanprize, nirenhukuprize, sanrentanprize, sanrenhukuprize, \
    (kumiban1 || kumiban2 || kumiban3) rank123 \
  from rec_race race, ml_classification cls \
  where race.ymd = cls.ymd and race.jyocd = cls.jyocd and race.raceno = cls.raceno \
    and sanrentanno <> '不成立' \
    and race.ymd >= '{fromYmd}' and race.ymd <= '{toYmd}' \
    and modelno = {used_nodel_no}
    and (probability1 between {range_p1_min} and {range_p1_max}) \
    and (probability2 between {range_p2_min} and {range_p2_max}) \
    and (probability3 between {range_p3_min} and {range_p3_max}) \
  order by pattern, race.ymd, race.sime

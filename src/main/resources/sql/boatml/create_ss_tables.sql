-- kumibanl単位生成
DROP FUNCTION IF EXISTS create_ss_tables(VARCHAR(40), VARCHAR(40)); 
CREATE OR REPLACE FUNCTION create_ss_tables(
pTarget VARCHAR(40),
pSource VARCHAR(40)
)
RETURNS VOID AS $$
  BEGIN
    EXECUTE format (' drop table if exists %s', pTarget);
	
    EXECUTE format ('
		CREATE TABLE %s (
		    keycol varchar(200) NULL,
		    keycol2 varchar(200) NULL,
			result_type varchar(2) NULL,
			bettype varchar(3) NULL,
			kumiban varchar(3) NULL,
			resultno varchar(5) NULL,
			modelno varchar(5) NULL,
			patternid varchar(20) NULL,
			pattern varchar(200) NULL,
			i09 int NULL,
			i02 int NULL,
			i04 int NULL,
			i19 int NULL,
			i39 int NULL,
			i27 int NULL,
			si09 int NULL,
			si02 int NULL,
			si04 int NULL,
			si19 int NULL,
			si27 int NULL,
			bc09 int NULL,
			bc02 int NULL,
			bc04 int NULL,
			bc19 int NULL,
			bc39 int NULL,
			bc27 int NULL,
			hc09 int NULL,
			hc02 int NULL,
			hc04 int NULL,
			hc19 int NULL,
			hc39 int NULL,
			hc27 int NULL,
			betcnt int NULL,
			hitcnt int NULL,
			betamt int NULL,
			hitamt int NULL,
			incamt int NULL,
			betrate double precision NULL,
			hitrate double precision NULL,
			incrate double precision NULL,
			bal_pluscnt int NULL,
			evaluations_id varchar(255)
		)
	', pTarget);
    
    EXECUTE format ('drop index if exists indexes_keycol_%s', pTarget);
    EXECUTE format ('create index indexes_keycol_%s on %s (keycol)', pTarget, pTarget);
    EXECUTE format ('drop index if exists indexes_keycol2_%s', pTarget);
    EXECUTE format ('create index indexes_keycol2_%s on %s (keycol2)', pTarget, pTarget);
    EXECUTE format ('drop index if exists indexes_resulttype_%s', pTarget);
    EXECUTE format ('create index indexes_resulttype_%s on %s (result_type)', pTarget, pTarget);
    EXECUTE format ('drop index if exists indexes_bettype_%s', pTarget);
    EXECUTE format ('create index indexes_bettype_%s on %s (bettype)', pTarget, pTarget);
    EXECUTE format ('drop index if exists indexes_kumiban_%s', pTarget);
    EXECUTE format ('create index indexes_kumiban_%s on %s (kumiban)', pTarget, pTarget);
    EXECUTE format ('drop index if exists indexes_modelno_%s', pTarget);
    EXECUTE format ('create index indexes_modelno_%s on %s (modelno)', pTarget, pTarget);
    EXECUTE format ('drop index if exists indexes_patternid_%s', pTarget);
    EXECUTE format ('create index indexes_patternid_%s on %s (patternid)', pTarget, pTarget);
    EXECUTE format ('drop index if exists indexes_evaluationsid_%s', pTarget);
    EXECUTE format ('create index indexes_evaluationsid_%s on %s (evaluations_id)', pTarget, pTarget);
	
    EXECUTE format ('
		insert into %s
		select 
		  keycol, keycol2, result_type, bettype, kumiban, resultno, modelno, patternid, pattern,
		  (i0+i1+i2+i3+i4+i5+i6+i7+i8+i9) i09,
		  (i0+i1+i2) i02,
		  (i0+i1+i2+i3+i4) i04,
		  (i1+i2+i3+i4+i5+i6+i7+i8+i9) i19,
		  (i3+i4+i5+i6+i7+i8+i9) i39,
		  (i2+i3+i4+i5+i6+i7) i27,
		  (i0*4+i1*2+i2*1.5+i3+i4+i5+i6+i7+i8+i9) si09,
		  (i0*4+i1*2+i2*1.5) si02,
		  (i0*4+i1*2+i2*1.5+i3+i4) si04,
		  (i1*2+i2*1.5+i3+i4+i5+i6+i7+i8+i9) si19,
		  (i2*1.5+i3+i4+i5+i6+i7) i27,
		  (bc0+bc1+bc2+bc3+bc4+bc5+bc6+bc7+bc8+bc9) bc09,
		  (bc0+bc1+bc2) bc02,
		  (bc0+bc1+bc2+bc3+bc4) bc04,
		  (bc1+bc2+bc3+bc4+bc5+bc6+bc7+bc8+bc9) bc19,
		  (bc3+bc4+bc5+bc6+bc7+bc8+bc9) bc39,
		  (bc2+bc3+bc4+bc5+bc6+bc7) bc27,
		  (hc0+hc1+hc2+hc3+hc4+hc5+hc6+hc7+hc8+hc9) hc09,
		  (hc0+hc1+hc2) hc02,
		  (hc0+hc1+hc2+hc3+hc4) hc04,
		  (hc1+hc2+hc3+hc4+hc5+hc6+hc7+hc8+hc9) hc19,
		  (hc3+hc4+hc5+hc6+hc7+hc8+hc9) hc39,
		  (hc2+hc3+hc4+hc5+hc6+hc7) hc27,
		  betcnt, hitcnt, betamt, hitamt, incamt, betrate, hitrate, incrate, bal_pluscnt, evaluations_id
		from %s
	', pTarget, pSource
	);

END;
$$ LANGUAGE plpgsql;

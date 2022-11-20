package com.pengkong.boatrace.mybatis.entity;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * generate_statのパラメータ
 * @author qwerty
 *
 */
@Getter
@Setter
public class ParamGenerateBoatStat {
	private String fromYmd;
	private String toYmd;
	private String betType;
	private String betTypeName;
	private String kumiban;
	private String pattern;
	private String table;
	private int minPatternCnt;
	private BigDecimal minIncomeRate;
	private BigDecimal maxIncomeRate;
	private String path;
	
	public ParamGenerateBoatStat(String fromYmd, String toYmd, String betType, String betTypeName, String kumiban,
			String pattern, String table, Integer minPatternCnt, BigDecimal minIncomeRate, BigDecimal maxIncomeRate,
			String path) {
		super();
		this.fromYmd = fromYmd;
		this.toYmd = toYmd;
		this.betType = betType;
		this.betTypeName = betTypeName;
		this.kumiban = kumiban;
		this.pattern = pattern;
		this.table = table;
		this.minPatternCnt = minPatternCnt;
		this.minIncomeRate = minIncomeRate;
		this.maxIncomeRate = maxIncomeRate;
		this.path = path;
	}
	
}

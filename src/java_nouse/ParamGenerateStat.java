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
public class ParamGenerateStat {
	private String ymd;
	private String paramBettype;
	private Integer paramMinPattenrCnt;
	private Integer paramMaxPattenrCnt;
	private BigDecimal paramMinHitRate;
	private BigDecimal paramMaxHitRate;
	private BigDecimal paramMinIncomeRate;
	private BigDecimal paramMaxIncomeRate;
	private String paramPath;
	
	public ParamGenerateStat(String ymd, String paramBettype, 
			Integer paramMinPattenrCnt, 
			Integer paramMaxPattenrCnt, 
			BigDecimal paramMinHitRate,
			BigDecimal paramMaxHitRate,
			BigDecimal paramMinIncomeRate, 
			BigDecimal paramMaxIncomeRate, 
			String paramPath) {
		super();
		this.ymd = ymd;
		this.paramBettype = paramBettype;
		this.paramMinPattenrCnt = paramMinPattenrCnt;
		this.paramMaxPattenrCnt = paramMaxPattenrCnt;
		this.paramMinHitRate = paramMinHitRate;
		this.paramMaxHitRate = paramMaxHitRate;
		this.paramMinIncomeRate = paramMinIncomeRate;
		this.paramMaxIncomeRate = paramMaxIncomeRate;
		this.paramPath = paramPath;
	}
}
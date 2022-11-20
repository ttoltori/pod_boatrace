package com.pengkong.boatrace.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * generate_powerのパラメータ
 * @author qwerty
 *
 */
@Getter
@Setter
public class ParamGeneratePower {
	private String ymd;
	private Short paramSectionRacer;
	private Short paramSectionAvgtime;
	private Short paramSectionJyoMotor;
	private String paramPath;
	
	public ParamGeneratePower(String ymd, Short paramSectionRacer, Short paramSectionAvgtime,
			Short paramSectionJyoMotor, String paramPath) {
		super();
		this.ymd = ymd;
		this.paramSectionRacer = paramSectionRacer;
		this.paramSectionAvgtime = paramSectionAvgtime;
		this.paramSectionJyoMotor = paramSectionJyoMotor;
		this.paramPath = paramPath;
	}
}

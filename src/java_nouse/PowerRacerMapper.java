package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.PowerRacer;
import com.pengkong.boatrace.mybatis.entity.PowerRacerExample;
import java.util.List;

public interface PowerRacerMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_racer
	 * @mbg.generated
	 */
	List<PowerRacer> selectByExample(PowerRacerExample example);
}
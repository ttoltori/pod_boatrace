package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.PowerJyoMotor;
import com.pengkong.boatrace.mybatis.entity.PowerJyoMotorExample;
import java.util.List;

public interface PowerJyoMotorMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor
	 * @mbg.generated
	 */
	List<PowerJyoMotor> selectByExample(PowerJyoMotorExample example);
}
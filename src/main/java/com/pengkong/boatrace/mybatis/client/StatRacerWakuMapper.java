package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.StatRacerWaku;
import com.pengkong.boatrace.mybatis.entity.StatRacerWakuExample;
import java.util.List;

public interface StatRacerWakuMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_racer_waku
	 * @mbg.generated
	 */
	int deleteByExample(StatRacerWakuExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_racer_waku
	 * @mbg.generated
	 */
	int insert(StatRacerWaku record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_racer_waku
	 * @mbg.generated
	 */
	int insertSelective(StatRacerWaku record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_racer_waku
	 * @mbg.generated
	 */
	List<StatRacerWaku> selectByExample(StatRacerWakuExample example);
}
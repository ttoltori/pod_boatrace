package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.RecRace;
import com.pengkong.boatrace.mybatis.entity.RecRaceExample;
import java.util.List;

public interface RecRaceMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race
	 * @mbg.generated
	 */
	int deleteByExample(RecRaceExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race
	 * @mbg.generated
	 */
	int insert(RecRace record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race
	 * @mbg.generated
	 */
	int insertSelective(RecRace record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race
	 * @mbg.generated
	 */
	List<RecRace> selectByExample(RecRaceExample example);
}
package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.RecRaceSg;
import com.pengkong.boatrace.mybatis.entity.RecRaceSgExample;
import java.util.List;

public interface RecRaceSgMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_sg
	 * @mbg.generated
	 */
	int deleteByExample(RecRaceSgExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_sg
	 * @mbg.generated
	 */
	int insert(RecRaceSg record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_sg
	 * @mbg.generated
	 */
	int insertSelective(RecRaceSg record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_sg
	 * @mbg.generated
	 */
	List<RecRaceSg> selectByExample(RecRaceSgExample example);
}
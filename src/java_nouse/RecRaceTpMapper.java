package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.RecRaceTp;
import com.pengkong.boatrace.mybatis.entity.RecRaceTpExample;
import java.util.List;

public interface RecRaceTpMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_tp
	 * @mbg.generated
	 */
	int deleteByExample(RecRaceTpExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_tp
	 * @mbg.generated
	 */
	int insert(RecRaceTp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_tp
	 * @mbg.generated
	 */
	int insertSelective(RecRaceTp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_tp
	 * @mbg.generated
	 */
	List<RecRaceTp> selectByExample(RecRaceTpExample example);
}
package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.RecRacerTrend;
import com.pengkong.boatrace.mybatis.entity.RecRacerTrendExample;
import java.util.List;

public interface RecRacerTrendMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_racer_trend
	 * @mbg.generated
	 */
	int deleteByExample(RecRacerTrendExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_racer_trend
	 * @mbg.generated
	 */
	int insert(RecRacerTrend record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_racer_trend
	 * @mbg.generated
	 */
	int insertSelective(RecRacerTrend record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_racer_trend
	 * @mbg.generated
	 */
	List<RecRacerTrend> selectByExample(RecRacerTrendExample example);
}
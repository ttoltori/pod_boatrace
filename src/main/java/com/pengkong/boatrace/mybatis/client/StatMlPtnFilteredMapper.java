package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.StatMlPtnFiltered;
import com.pengkong.boatrace.mybatis.entity.StatMlPtnFilteredExample;
import java.util.List;

public interface StatMlPtnFilteredMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_ml_ptn_filtered
	 * @mbg.generated
	 */
	int deleteByExample(StatMlPtnFilteredExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_ml_ptn_filtered
	 * @mbg.generated
	 */
	int insert(StatMlPtnFiltered record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_ml_ptn_filtered
	 * @mbg.generated
	 */
	int insertSelective(StatMlPtnFiltered record);
}
package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.RunRankResult;
import com.pengkong.boatrace.mybatis.entity.RunRankResultExample;
import java.util.List;

public interface RunRankResultMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.run_rank_result
	 * @mbg.generated
	 */
	int deleteByExample(RunRankResultExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.run_rank_result
	 * @mbg.generated
	 */
	int insert(RunRankResult record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.run_rank_result
	 * @mbg.generated
	 */
	int insertSelective(RunRankResult record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.run_rank_result
	 * @mbg.generated
	 */
	List<RunRankResult> selectByExample(RunRankResultExample example);
}
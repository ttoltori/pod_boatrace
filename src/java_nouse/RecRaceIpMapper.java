package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.RecRaceIp;
import com.pengkong.boatrace.mybatis.entity.RecRaceIpExample;
import java.util.List;

public interface RecRaceIpMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_ip
	 * @mbg.generated
	 */
	int deleteByExample(RecRaceIpExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_ip
	 * @mbg.generated
	 */
	int insert(RecRaceIp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_ip
	 * @mbg.generated
	 */
	int insertSelective(RecRaceIp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.rec_race_ip
	 * @mbg.generated
	 */
	List<RecRaceIp> selectByExample(RecRaceIpExample example);
}
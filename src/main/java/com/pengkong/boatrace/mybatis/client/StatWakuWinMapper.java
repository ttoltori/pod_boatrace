package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.StatWakuWin;
import com.pengkong.boatrace.mybatis.entity.StatWakuWinExample;
import java.util.List;

public interface StatWakuWinMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_waku_win
	 * @mbg.generated
	 */
	int deleteByExample(StatWakuWinExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_waku_win
	 * @mbg.generated
	 */
	int insert(StatWakuWin record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_waku_win
	 * @mbg.generated
	 */
	int insertSelective(StatWakuWin record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.stat_waku_win
	 * @mbg.generated
	 */
	List<StatWakuWin> selectByExample(StatWakuWinExample example);
}
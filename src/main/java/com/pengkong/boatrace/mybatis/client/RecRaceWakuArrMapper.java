package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.RecRaceWakuArr;
import com.pengkong.boatrace.mybatis.entity.RecRaceWakuArrExample;
import java.util.List;

public interface RecRaceWakuArrMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.rec_race_waku_arr
     *
     * @mbg.generated
     */
    int deleteByExample(RecRaceWakuArrExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.rec_race_waku_arr
     *
     * @mbg.generated
     */
    int insert(RecRaceWakuArr record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.rec_race_waku_arr
     *
     * @mbg.generated
     */
    int insertSelective(RecRaceWakuArr record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.rec_race_waku_arr
     *
     * @mbg.generated
     */
    List<RecRaceWakuArr> selectByExample(RecRaceWakuArrExample example);
}
package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.StatMlResultPtn;
import com.pengkong.boatrace.mybatis.entity.StatMlResultPtnExample;

public interface StatMlResultPtnMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.stat_ml_result_ptn
     *
     * @mbg.generated
     */
    int deleteByExample(StatMlResultPtnExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.stat_ml_result_ptn
     *
     * @mbg.generated
     */
    int insert(StatMlResultPtn record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.stat_ml_result_ptn
     *
     * @mbg.generated
     */
    int insertSelective(StatMlResultPtn record);
}
package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.StatMlResultWeka;
import com.pengkong.boatrace.mybatis.entity.StatMlResultWekaExample;

public interface StatMlResultWekaMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.stat_ml_result_weka
     *
     * @mbg.generated
     */
    int deleteByExample(StatMlResultWekaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.stat_ml_result_weka
     *
     * @mbg.generated
     */
    int insert(StatMlResultWeka record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.stat_ml_result_weka
     *
     * @mbg.generated
     */
    int insertSelective(StatMlResultWeka record);
}
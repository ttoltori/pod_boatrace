package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.OlRacer;
import com.pengkong.boatrace.mybatis.entity.OlRacerExample;
import java.util.List;

public interface OlRacerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.ol_racer
     *
     * @mbg.generated
     */
    int deleteByExample(OlRacerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.ol_racer
     *
     * @mbg.generated
     */
    int insert(OlRacer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.ol_racer
     *
     * @mbg.generated
     */
    int insertSelective(OlRacer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.ol_racer
     *
     * @mbg.generated
     */
    List<OlRacer> selectByExample(OlRacerExample example);
}
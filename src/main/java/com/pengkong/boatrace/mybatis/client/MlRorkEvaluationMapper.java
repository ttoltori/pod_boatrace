package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.MlRorkEvaluation;
import com.pengkong.boatrace.mybatis.entity.MlRorkEvaluationExample;

public interface MlRorkEvaluationMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.ml_rork_evaluation
     *
     * @mbg.generated
     */
    int deleteByExample(MlRorkEvaluationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.ml_rork_evaluation
     *
     * @mbg.generated
     */
    int insert(MlRorkEvaluation record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.ml_rork_evaluation
     *
     * @mbg.generated
     */
    int insertSelective(MlRorkEvaluation record);
}
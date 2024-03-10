package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.OddsPredict;
import com.pengkong.boatrace.mybatis.entity.OddsPredictExample;
import java.util.List;

public interface OddsPredictMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_predict
     *
     * @mbg.generated
     */
    int deleteByExample(OddsPredictExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_predict
     *
     * @mbg.generated
     */
    int insert(OddsPredict record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_predict
     *
     * @mbg.generated
     */
    int insertSelective(OddsPredict record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_predict
     *
     * @mbg.generated
     */
    List<OddsPredict> selectByExample(OddsPredictExample example);
}
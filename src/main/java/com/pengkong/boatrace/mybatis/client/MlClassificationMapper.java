package com.pengkong.boatrace.mybatis.client;

import com.pengkong.boatrace.mybatis.entity.MlClassification;
import com.pengkong.boatrace.mybatis.entity.MlClassificationExample;
import java.util.List;

public interface MlClassificationMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ml_classification
	 * @mbg.generated
	 */
	int deleteByExample(MlClassificationExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ml_classification
	 * @mbg.generated
	 */
	int insert(MlClassification record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ml_classification
	 * @mbg.generated
	 */
	int insertSelective(MlClassification record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ml_classification
	 * @mbg.generated
	 */
	List<MlClassification> selectByExample(MlClassificationExample example);
}
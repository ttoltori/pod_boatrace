package com.pengkong.boatrace.mybatis.entity;

import java.util.ArrayList;
import java.util.List;

public class OlClassificationExample {
    /**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	protected String orderByClause;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	protected boolean distinct;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	protected List<Criteria> oredCriteria;

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public OlClassificationExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	protected abstract static class GeneratedCriteria {
		protected List<Criterion> criteria;

		protected GeneratedCriteria() {
			super();
			criteria = new ArrayList<Criterion>();
		}

		public boolean isValid() {
			return criteria.size() > 0;
		}

		public List<Criterion> getAllCriteria() {
			return criteria;
		}

		public List<Criterion> getCriteria() {
			return criteria;
		}

		protected void addCriterion(String condition) {
			if (condition == null) {
				throw new RuntimeException("Value for condition cannot be null");
			}
			criteria.add(new Criterion(condition));
		}

		protected void addCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new RuntimeException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value));
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2));
		}

		public Criteria andModelnoIsNull() {
			addCriterion("modelno is null");
			return (Criteria) this;
		}

		public Criteria andModelnoIsNotNull() {
			addCriterion("modelno is not null");
			return (Criteria) this;
		}

		public Criteria andModelnoEqualTo(String value) {
			addCriterion("modelno =", value, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoNotEqualTo(String value) {
			addCriterion("modelno <>", value, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoGreaterThan(String value) {
			addCriterion("modelno >", value, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoGreaterThanOrEqualTo(String value) {
			addCriterion("modelno >=", value, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoLessThan(String value) {
			addCriterion("modelno <", value, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoLessThanOrEqualTo(String value) {
			addCriterion("modelno <=", value, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoLike(String value) {
			addCriterion("modelno like", value, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoNotLike(String value) {
			addCriterion("modelno not like", value, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoIn(List<String> values) {
			addCriterion("modelno in", values, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoNotIn(List<String> values) {
			addCriterion("modelno not in", values, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoBetween(String value1, String value2) {
			addCriterion("modelno between", value1, value2, "modelno");
			return (Criteria) this;
		}

		public Criteria andModelnoNotBetween(String value1, String value2) {
			addCriterion("modelno not between", value1, value2, "modelno");
			return (Criteria) this;
		}

		public Criteria andYmdIsNull() {
			addCriterion("ymd is null");
			return (Criteria) this;
		}

		public Criteria andYmdIsNotNull() {
			addCriterion("ymd is not null");
			return (Criteria) this;
		}

		public Criteria andYmdEqualTo(String value) {
			addCriterion("ymd =", value, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdNotEqualTo(String value) {
			addCriterion("ymd <>", value, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdGreaterThan(String value) {
			addCriterion("ymd >", value, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdGreaterThanOrEqualTo(String value) {
			addCriterion("ymd >=", value, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdLessThan(String value) {
			addCriterion("ymd <", value, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdLessThanOrEqualTo(String value) {
			addCriterion("ymd <=", value, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdLike(String value) {
			addCriterion("ymd like", value, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdNotLike(String value) {
			addCriterion("ymd not like", value, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdIn(List<String> values) {
			addCriterion("ymd in", values, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdNotIn(List<String> values) {
			addCriterion("ymd not in", values, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdBetween(String value1, String value2) {
			addCriterion("ymd between", value1, value2, "ymd");
			return (Criteria) this;
		}

		public Criteria andYmdNotBetween(String value1, String value2) {
			addCriterion("ymd not between", value1, value2, "ymd");
			return (Criteria) this;
		}

		public Criteria andJyocdIsNull() {
			addCriterion("jyocd is null");
			return (Criteria) this;
		}

		public Criteria andJyocdIsNotNull() {
			addCriterion("jyocd is not null");
			return (Criteria) this;
		}

		public Criteria andJyocdEqualTo(String value) {
			addCriterion("jyocd =", value, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdNotEqualTo(String value) {
			addCriterion("jyocd <>", value, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdGreaterThan(String value) {
			addCriterion("jyocd >", value, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdGreaterThanOrEqualTo(String value) {
			addCriterion("jyocd >=", value, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdLessThan(String value) {
			addCriterion("jyocd <", value, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdLessThanOrEqualTo(String value) {
			addCriterion("jyocd <=", value, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdLike(String value) {
			addCriterion("jyocd like", value, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdNotLike(String value) {
			addCriterion("jyocd not like", value, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdIn(List<String> values) {
			addCriterion("jyocd in", values, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdNotIn(List<String> values) {
			addCriterion("jyocd not in", values, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdBetween(String value1, String value2) {
			addCriterion("jyocd between", value1, value2, "jyocd");
			return (Criteria) this;
		}

		public Criteria andJyocdNotBetween(String value1, String value2) {
			addCriterion("jyocd not between", value1, value2, "jyocd");
			return (Criteria) this;
		}

		public Criteria andRacenoIsNull() {
			addCriterion("raceno is null");
			return (Criteria) this;
		}

		public Criteria andRacenoIsNotNull() {
			addCriterion("raceno is not null");
			return (Criteria) this;
		}

		public Criteria andRacenoEqualTo(Integer value) {
			addCriterion("raceno =", value, "raceno");
			return (Criteria) this;
		}

		public Criteria andRacenoNotEqualTo(Integer value) {
			addCriterion("raceno <>", value, "raceno");
			return (Criteria) this;
		}

		public Criteria andRacenoGreaterThan(Integer value) {
			addCriterion("raceno >", value, "raceno");
			return (Criteria) this;
		}

		public Criteria andRacenoGreaterThanOrEqualTo(Integer value) {
			addCriterion("raceno >=", value, "raceno");
			return (Criteria) this;
		}

		public Criteria andRacenoLessThan(Integer value) {
			addCriterion("raceno <", value, "raceno");
			return (Criteria) this;
		}

		public Criteria andRacenoLessThanOrEqualTo(Integer value) {
			addCriterion("raceno <=", value, "raceno");
			return (Criteria) this;
		}

		public Criteria andRacenoIn(List<Integer> values) {
			addCriterion("raceno in", values, "raceno");
			return (Criteria) this;
		}

		public Criteria andRacenoNotIn(List<Integer> values) {
			addCriterion("raceno not in", values, "raceno");
			return (Criteria) this;
		}

		public Criteria andRacenoBetween(Integer value1, Integer value2) {
			addCriterion("raceno between", value1, value2, "raceno");
			return (Criteria) this;
		}

		public Criteria andRacenoNotBetween(Integer value1, Integer value2) {
			addCriterion("raceno not between", value1, value2, "raceno");
			return (Criteria) this;
		}

		public Criteria andSimeIsNull() {
			addCriterion("sime is null");
			return (Criteria) this;
		}

		public Criteria andSimeIsNotNull() {
			addCriterion("sime is not null");
			return (Criteria) this;
		}

		public Criteria andSimeEqualTo(String value) {
			addCriterion("sime =", value, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeNotEqualTo(String value) {
			addCriterion("sime <>", value, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeGreaterThan(String value) {
			addCriterion("sime >", value, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeGreaterThanOrEqualTo(String value) {
			addCriterion("sime >=", value, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeLessThan(String value) {
			addCriterion("sime <", value, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeLessThanOrEqualTo(String value) {
			addCriterion("sime <=", value, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeLike(String value) {
			addCriterion("sime like", value, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeNotLike(String value) {
			addCriterion("sime not like", value, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeIn(List<String> values) {
			addCriterion("sime in", values, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeNotIn(List<String> values) {
			addCriterion("sime not in", values, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeBetween(String value1, String value2) {
			addCriterion("sime between", value1, value2, "sime");
			return (Criteria) this;
		}

		public Criteria andSimeNotBetween(String value1, String value2) {
			addCriterion("sime not between", value1, value2, "sime");
			return (Criteria) this;
		}

		public Criteria andPrediction1IsNull() {
			addCriterion("prediction1 is null");
			return (Criteria) this;
		}

		public Criteria andPrediction1IsNotNull() {
			addCriterion("prediction1 is not null");
			return (Criteria) this;
		}

		public Criteria andPrediction1EqualTo(String value) {
			addCriterion("prediction1 =", value, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1NotEqualTo(String value) {
			addCriterion("prediction1 <>", value, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1GreaterThan(String value) {
			addCriterion("prediction1 >", value, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1GreaterThanOrEqualTo(String value) {
			addCriterion("prediction1 >=", value, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1LessThan(String value) {
			addCriterion("prediction1 <", value, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1LessThanOrEqualTo(String value) {
			addCriterion("prediction1 <=", value, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1Like(String value) {
			addCriterion("prediction1 like", value, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1NotLike(String value) {
			addCriterion("prediction1 not like", value, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1In(List<String> values) {
			addCriterion("prediction1 in", values, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1NotIn(List<String> values) {
			addCriterion("prediction1 not in", values, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1Between(String value1, String value2) {
			addCriterion("prediction1 between", value1, value2, "prediction1");
			return (Criteria) this;
		}

		public Criteria andPrediction1NotBetween(String value1, String value2) {
			addCriterion("prediction1 not between", value1, value2, "prediction1");
			return (Criteria) this;
		}

		public Criteria andProbability1IsNull() {
			addCriterion("probability1 is null");
			return (Criteria) this;
		}

		public Criteria andProbability1IsNotNull() {
			addCriterion("probability1 is not null");
			return (Criteria) this;
		}

		public Criteria andProbability1EqualTo(Double value) {
			addCriterion("probability1 =", value, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbability1NotEqualTo(Double value) {
			addCriterion("probability1 <>", value, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbability1GreaterThan(Double value) {
			addCriterion("probability1 >", value, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbability1GreaterThanOrEqualTo(Double value) {
			addCriterion("probability1 >=", value, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbability1LessThan(Double value) {
			addCriterion("probability1 <", value, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbability1LessThanOrEqualTo(Double value) {
			addCriterion("probability1 <=", value, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbability1In(List<Double> values) {
			addCriterion("probability1 in", values, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbability1NotIn(List<Double> values) {
			addCriterion("probability1 not in", values, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbability1Between(Double value1, Double value2) {
			addCriterion("probability1 between", value1, value2, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbability1NotBetween(Double value1, Double value2) {
			addCriterion("probability1 not between", value1, value2, "probability1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1IsNull() {
			addCriterion("probabilities1 is null");
			return (Criteria) this;
		}

		public Criteria andProbabilities1IsNotNull() {
			addCriterion("probabilities1 is not null");
			return (Criteria) this;
		}

		public Criteria andProbabilities1EqualTo(Object value) {
			addCriterion("probabilities1 =", value, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1NotEqualTo(Object value) {
			addCriterion("probabilities1 <>", value, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1GreaterThan(Object value) {
			addCriterion("probabilities1 >", value, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1GreaterThanOrEqualTo(Object value) {
			addCriterion("probabilities1 >=", value, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1LessThan(Object value) {
			addCriterion("probabilities1 <", value, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1LessThanOrEqualTo(Object value) {
			addCriterion("probabilities1 <=", value, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1In(List<Object> values) {
			addCriterion("probabilities1 in", values, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1NotIn(List<Object> values) {
			addCriterion("probabilities1 not in", values, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1Between(Object value1, Object value2) {
			addCriterion("probabilities1 between", value1, value2, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andProbabilities1NotBetween(Object value1, Object value2) {
			addCriterion("probabilities1 not between", value1, value2, "probabilities1");
			return (Criteria) this;
		}

		public Criteria andPrediction2IsNull() {
			addCriterion("prediction2 is null");
			return (Criteria) this;
		}

		public Criteria andPrediction2IsNotNull() {
			addCriterion("prediction2 is not null");
			return (Criteria) this;
		}

		public Criteria andPrediction2EqualTo(String value) {
			addCriterion("prediction2 =", value, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2NotEqualTo(String value) {
			addCriterion("prediction2 <>", value, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2GreaterThan(String value) {
			addCriterion("prediction2 >", value, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2GreaterThanOrEqualTo(String value) {
			addCriterion("prediction2 >=", value, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2LessThan(String value) {
			addCriterion("prediction2 <", value, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2LessThanOrEqualTo(String value) {
			addCriterion("prediction2 <=", value, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2Like(String value) {
			addCriterion("prediction2 like", value, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2NotLike(String value) {
			addCriterion("prediction2 not like", value, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2In(List<String> values) {
			addCriterion("prediction2 in", values, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2NotIn(List<String> values) {
			addCriterion("prediction2 not in", values, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2Between(String value1, String value2) {
			addCriterion("prediction2 between", value1, value2, "prediction2");
			return (Criteria) this;
		}

		public Criteria andPrediction2NotBetween(String value1, String value2) {
			addCriterion("prediction2 not between", value1, value2, "prediction2");
			return (Criteria) this;
		}

		public Criteria andProbability2IsNull() {
			addCriterion("probability2 is null");
			return (Criteria) this;
		}

		public Criteria andProbability2IsNotNull() {
			addCriterion("probability2 is not null");
			return (Criteria) this;
		}

		public Criteria andProbability2EqualTo(Double value) {
			addCriterion("probability2 =", value, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbability2NotEqualTo(Double value) {
			addCriterion("probability2 <>", value, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbability2GreaterThan(Double value) {
			addCriterion("probability2 >", value, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbability2GreaterThanOrEqualTo(Double value) {
			addCriterion("probability2 >=", value, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbability2LessThan(Double value) {
			addCriterion("probability2 <", value, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbability2LessThanOrEqualTo(Double value) {
			addCriterion("probability2 <=", value, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbability2In(List<Double> values) {
			addCriterion("probability2 in", values, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbability2NotIn(List<Double> values) {
			addCriterion("probability2 not in", values, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbability2Between(Double value1, Double value2) {
			addCriterion("probability2 between", value1, value2, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbability2NotBetween(Double value1, Double value2) {
			addCriterion("probability2 not between", value1, value2, "probability2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2IsNull() {
			addCriterion("probabilities2 is null");
			return (Criteria) this;
		}

		public Criteria andProbabilities2IsNotNull() {
			addCriterion("probabilities2 is not null");
			return (Criteria) this;
		}

		public Criteria andProbabilities2EqualTo(Object value) {
			addCriterion("probabilities2 =", value, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2NotEqualTo(Object value) {
			addCriterion("probabilities2 <>", value, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2GreaterThan(Object value) {
			addCriterion("probabilities2 >", value, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2GreaterThanOrEqualTo(Object value) {
			addCriterion("probabilities2 >=", value, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2LessThan(Object value) {
			addCriterion("probabilities2 <", value, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2LessThanOrEqualTo(Object value) {
			addCriterion("probabilities2 <=", value, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2In(List<Object> values) {
			addCriterion("probabilities2 in", values, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2NotIn(List<Object> values) {
			addCriterion("probabilities2 not in", values, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2Between(Object value1, Object value2) {
			addCriterion("probabilities2 between", value1, value2, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andProbabilities2NotBetween(Object value1, Object value2) {
			addCriterion("probabilities2 not between", value1, value2, "probabilities2");
			return (Criteria) this;
		}

		public Criteria andPrediction3IsNull() {
			addCriterion("prediction3 is null");
			return (Criteria) this;
		}

		public Criteria andPrediction3IsNotNull() {
			addCriterion("prediction3 is not null");
			return (Criteria) this;
		}

		public Criteria andPrediction3EqualTo(String value) {
			addCriterion("prediction3 =", value, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3NotEqualTo(String value) {
			addCriterion("prediction3 <>", value, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3GreaterThan(String value) {
			addCriterion("prediction3 >", value, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3GreaterThanOrEqualTo(String value) {
			addCriterion("prediction3 >=", value, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3LessThan(String value) {
			addCriterion("prediction3 <", value, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3LessThanOrEqualTo(String value) {
			addCriterion("prediction3 <=", value, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3Like(String value) {
			addCriterion("prediction3 like", value, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3NotLike(String value) {
			addCriterion("prediction3 not like", value, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3In(List<String> values) {
			addCriterion("prediction3 in", values, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3NotIn(List<String> values) {
			addCriterion("prediction3 not in", values, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3Between(String value1, String value2) {
			addCriterion("prediction3 between", value1, value2, "prediction3");
			return (Criteria) this;
		}

		public Criteria andPrediction3NotBetween(String value1, String value2) {
			addCriterion("prediction3 not between", value1, value2, "prediction3");
			return (Criteria) this;
		}

		public Criteria andProbability3IsNull() {
			addCriterion("probability3 is null");
			return (Criteria) this;
		}

		public Criteria andProbability3IsNotNull() {
			addCriterion("probability3 is not null");
			return (Criteria) this;
		}

		public Criteria andProbability3EqualTo(Double value) {
			addCriterion("probability3 =", value, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbability3NotEqualTo(Double value) {
			addCriterion("probability3 <>", value, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbability3GreaterThan(Double value) {
			addCriterion("probability3 >", value, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbability3GreaterThanOrEqualTo(Double value) {
			addCriterion("probability3 >=", value, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbability3LessThan(Double value) {
			addCriterion("probability3 <", value, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbability3LessThanOrEqualTo(Double value) {
			addCriterion("probability3 <=", value, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbability3In(List<Double> values) {
			addCriterion("probability3 in", values, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbability3NotIn(List<Double> values) {
			addCriterion("probability3 not in", values, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbability3Between(Double value1, Double value2) {
			addCriterion("probability3 between", value1, value2, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbability3NotBetween(Double value1, Double value2) {
			addCriterion("probability3 not between", value1, value2, "probability3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3IsNull() {
			addCriterion("probabilities3 is null");
			return (Criteria) this;
		}

		public Criteria andProbabilities3IsNotNull() {
			addCriterion("probabilities3 is not null");
			return (Criteria) this;
		}

		public Criteria andProbabilities3EqualTo(Object value) {
			addCriterion("probabilities3 =", value, "probabilities3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3NotEqualTo(Object value) {
			addCriterion("probabilities3 <>", value, "probabilities3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3GreaterThan(Object value) {
			addCriterion("probabilities3 >", value, "probabilities3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3GreaterThanOrEqualTo(Object value) {
			addCriterion("probabilities3 >=", value, "probabilities3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3LessThan(Object value) {
			addCriterion("probabilities3 <", value, "probabilities3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3LessThanOrEqualTo(Object value) {
			addCriterion("probabilities3 <=", value, "probabilities3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3In(List<Object> values) {
			addCriterion("probabilities3 in", values, "probabilities3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3NotIn(List<Object> values) {
			addCriterion("probabilities3 not in", values, "probabilities3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3Between(Object value1, Object value2) {
			addCriterion("probabilities3 between", value1, value2, "probabilities3");
			return (Criteria) this;
		}

		public Criteria andProbabilities3NotBetween(Object value1, Object value2) {
			addCriterion("probabilities3 not between", value1, value2, "probabilities3");
			return (Criteria) this;
		}
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table public.ol_classification
	 * @mbg.generated
	 */
	public static class Criterion {
		private String condition;
		private Object value;
		private Object secondValue;
		private boolean noValue;
		private boolean singleValue;
		private boolean betweenValue;
		private boolean listValue;
		private String typeHandler;

		public String getCondition() {
			return condition;
		}

		public Object getValue() {
			return value;
		}

		public Object getSecondValue() {
			return secondValue;
		}

		public boolean isNoValue() {
			return noValue;
		}

		public boolean isSingleValue() {
			return singleValue;
		}

		public boolean isBetweenValue() {
			return betweenValue;
		}

		public boolean isListValue() {
			return listValue;
		}

		public String getTypeHandler() {
			return typeHandler;
		}

		protected Criterion(String condition) {
			super();
			this.condition = condition;
			this.typeHandler = null;
			this.noValue = true;
		}

		protected Criterion(String condition, Object value, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.typeHandler = typeHandler;
			if (value instanceof List<?>) {
				this.listValue = true;
			} else {
				this.singleValue = true;
			}
		}

		protected Criterion(String condition, Object value) {
			this(condition, value, null);
		}

		protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.secondValue = secondValue;
			this.typeHandler = typeHandler;
			this.betweenValue = true;
		}

		protected Criterion(String condition, Object value, Object secondValue) {
			this(condition, value, secondValue, null);
		}
	}

	/**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table public.ol_classification
     *
     * @mbg.generated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }
}
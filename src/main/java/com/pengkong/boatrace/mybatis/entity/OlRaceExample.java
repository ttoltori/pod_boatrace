package com.pengkong.boatrace.mybatis.entity;

import java.util.ArrayList;
import java.util.List;

public class OlRaceExample {
    /**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	protected String orderByClause;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	protected boolean distinct;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	protected List<Criteria> oredCriteria;

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	public OlRaceExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
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
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.ol_race
	 * @mbg.generated
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table public.ol_race
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

		public Criteria andGradeIsNull() {
			addCriterion("grade is null");
			return (Criteria) this;
		}

		public Criteria andGradeIsNotNull() {
			addCriterion("grade is not null");
			return (Criteria) this;
		}

		public Criteria andGradeEqualTo(String value) {
			addCriterion("grade =", value, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeNotEqualTo(String value) {
			addCriterion("grade <>", value, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeGreaterThan(String value) {
			addCriterion("grade >", value, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeGreaterThanOrEqualTo(String value) {
			addCriterion("grade >=", value, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeLessThan(String value) {
			addCriterion("grade <", value, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeLessThanOrEqualTo(String value) {
			addCriterion("grade <=", value, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeLike(String value) {
			addCriterion("grade like", value, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeNotLike(String value) {
			addCriterion("grade not like", value, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeIn(List<String> values) {
			addCriterion("grade in", values, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeNotIn(List<String> values) {
			addCriterion("grade not in", values, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeBetween(String value1, String value2) {
			addCriterion("grade between", value1, value2, "grade");
			return (Criteria) this;
		}

		public Criteria andGradeNotBetween(String value1, String value2) {
			addCriterion("grade not between", value1, value2, "grade");
			return (Criteria) this;
		}

		public Criteria andTimezoneIsNull() {
			addCriterion("timezone is null");
			return (Criteria) this;
		}

		public Criteria andTimezoneIsNotNull() {
			addCriterion("timezone is not null");
			return (Criteria) this;
		}

		public Criteria andTimezoneEqualTo(String value) {
			addCriterion("timezone =", value, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneNotEqualTo(String value) {
			addCriterion("timezone <>", value, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneGreaterThan(String value) {
			addCriterion("timezone >", value, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneGreaterThanOrEqualTo(String value) {
			addCriterion("timezone >=", value, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneLessThan(String value) {
			addCriterion("timezone <", value, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneLessThanOrEqualTo(String value) {
			addCriterion("timezone <=", value, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneLike(String value) {
			addCriterion("timezone like", value, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneNotLike(String value) {
			addCriterion("timezone not like", value, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneIn(List<String> values) {
			addCriterion("timezone in", values, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneNotIn(List<String> values) {
			addCriterion("timezone not in", values, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneBetween(String value1, String value2) {
			addCriterion("timezone between", value1, value2, "timezone");
			return (Criteria) this;
		}

		public Criteria andTimezoneNotBetween(String value1, String value2) {
			addCriterion("timezone not between", value1, value2, "timezone");
			return (Criteria) this;
		}

		public Criteria andTurnIsNull() {
			addCriterion("turn is null");
			return (Criteria) this;
		}

		public Criteria andTurnIsNotNull() {
			addCriterion("turn is not null");
			return (Criteria) this;
		}

		public Criteria andTurnEqualTo(String value) {
			addCriterion("turn =", value, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnNotEqualTo(String value) {
			addCriterion("turn <>", value, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnGreaterThan(String value) {
			addCriterion("turn >", value, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnGreaterThanOrEqualTo(String value) {
			addCriterion("turn >=", value, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnLessThan(String value) {
			addCriterion("turn <", value, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnLessThanOrEqualTo(String value) {
			addCriterion("turn <=", value, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnLike(String value) {
			addCriterion("turn like", value, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnNotLike(String value) {
			addCriterion("turn not like", value, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnIn(List<String> values) {
			addCriterion("turn in", values, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnNotIn(List<String> values) {
			addCriterion("turn not in", values, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnBetween(String value1, String value2) {
			addCriterion("turn between", value1, value2, "turn");
			return (Criteria) this;
		}

		public Criteria andTurnNotBetween(String value1, String value2) {
			addCriterion("turn not between", value1, value2, "turn");
			return (Criteria) this;
		}

		public Criteria andFixedentranceIsNull() {
			addCriterion("fixedentrance is null");
			return (Criteria) this;
		}

		public Criteria andFixedentranceIsNotNull() {
			addCriterion("fixedentrance is not null");
			return (Criteria) this;
		}

		public Criteria andFixedentranceEqualTo(String value) {
			addCriterion("fixedentrance =", value, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceNotEqualTo(String value) {
			addCriterion("fixedentrance <>", value, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceGreaterThan(String value) {
			addCriterion("fixedentrance >", value, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceGreaterThanOrEqualTo(String value) {
			addCriterion("fixedentrance >=", value, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceLessThan(String value) {
			addCriterion("fixedentrance <", value, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceLessThanOrEqualTo(String value) {
			addCriterion("fixedentrance <=", value, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceLike(String value) {
			addCriterion("fixedentrance like", value, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceNotLike(String value) {
			addCriterion("fixedentrance not like", value, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceIn(List<String> values) {
			addCriterion("fixedentrance in", values, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceNotIn(List<String> values) {
			addCriterion("fixedentrance not in", values, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceBetween(String value1, String value2) {
			addCriterion("fixedentrance between", value1, value2, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andFixedentranceNotBetween(String value1, String value2) {
			addCriterion("fixedentrance not between", value1, value2, "fixedentrance");
			return (Criteria) this;
		}

		public Criteria andRacetypeIsNull() {
			addCriterion("racetype is null");
			return (Criteria) this;
		}

		public Criteria andRacetypeIsNotNull() {
			addCriterion("racetype is not null");
			return (Criteria) this;
		}

		public Criteria andRacetypeEqualTo(String value) {
			addCriterion("racetype =", value, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeNotEqualTo(String value) {
			addCriterion("racetype <>", value, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeGreaterThan(String value) {
			addCriterion("racetype >", value, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeGreaterThanOrEqualTo(String value) {
			addCriterion("racetype >=", value, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeLessThan(String value) {
			addCriterion("racetype <", value, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeLessThanOrEqualTo(String value) {
			addCriterion("racetype <=", value, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeLike(String value) {
			addCriterion("racetype like", value, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeNotLike(String value) {
			addCriterion("racetype not like", value, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeIn(List<String> values) {
			addCriterion("racetype in", values, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeNotIn(List<String> values) {
			addCriterion("racetype not in", values, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeBetween(String value1, String value2) {
			addCriterion("racetype between", value1, value2, "racetype");
			return (Criteria) this;
		}

		public Criteria andRacetypeNotBetween(String value1, String value2) {
			addCriterion("racetype not between", value1, value2, "racetype");
			return (Criteria) this;
		}

		public Criteria andWakulevellistIsNull() {
			addCriterion("wakulevellist is null");
			return (Criteria) this;
		}

		public Criteria andWakulevellistIsNotNull() {
			addCriterion("wakulevellist is not null");
			return (Criteria) this;
		}

		public Criteria andWakulevellistEqualTo(String value) {
			addCriterion("wakulevellist =", value, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistNotEqualTo(String value) {
			addCriterion("wakulevellist <>", value, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistGreaterThan(String value) {
			addCriterion("wakulevellist >", value, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistGreaterThanOrEqualTo(String value) {
			addCriterion("wakulevellist >=", value, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistLessThan(String value) {
			addCriterion("wakulevellist <", value, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistLessThanOrEqualTo(String value) {
			addCriterion("wakulevellist <=", value, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistLike(String value) {
			addCriterion("wakulevellist like", value, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistNotLike(String value) {
			addCriterion("wakulevellist not like", value, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistIn(List<String> values) {
			addCriterion("wakulevellist in", values, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistNotIn(List<String> values) {
			addCriterion("wakulevellist not in", values, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistBetween(String value1, String value2) {
			addCriterion("wakulevellist between", value1, value2, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andWakulevellistNotBetween(String value1, String value2) {
			addCriterion("wakulevellist not between", value1, value2, "wakulevellist");
			return (Criteria) this;
		}

		public Criteria andAlevelcountIsNull() {
			addCriterion("alevelcount is null");
			return (Criteria) this;
		}

		public Criteria andAlevelcountIsNotNull() {
			addCriterion("alevelcount is not null");
			return (Criteria) this;
		}

		public Criteria andAlevelcountEqualTo(Integer value) {
			addCriterion("alevelcount =", value, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andAlevelcountNotEqualTo(Integer value) {
			addCriterion("alevelcount <>", value, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andAlevelcountGreaterThan(Integer value) {
			addCriterion("alevelcount >", value, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andAlevelcountGreaterThanOrEqualTo(Integer value) {
			addCriterion("alevelcount >=", value, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andAlevelcountLessThan(Integer value) {
			addCriterion("alevelcount <", value, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andAlevelcountLessThanOrEqualTo(Integer value) {
			addCriterion("alevelcount <=", value, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andAlevelcountIn(List<Integer> values) {
			addCriterion("alevelcount in", values, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andAlevelcountNotIn(List<Integer> values) {
			addCriterion("alevelcount not in", values, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andAlevelcountBetween(Integer value1, Integer value2) {
			addCriterion("alevelcount between", value1, value2, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andAlevelcountNotBetween(Integer value1, Integer value2) {
			addCriterion("alevelcount not between", value1, value2, "alevelcount");
			return (Criteria) this;
		}

		public Criteria andFemalecountIsNull() {
			addCriterion("femalecount is null");
			return (Criteria) this;
		}

		public Criteria andFemalecountIsNotNull() {
			addCriterion("femalecount is not null");
			return (Criteria) this;
		}

		public Criteria andFemalecountEqualTo(Integer value) {
			addCriterion("femalecount =", value, "femalecount");
			return (Criteria) this;
		}

		public Criteria andFemalecountNotEqualTo(Integer value) {
			addCriterion("femalecount <>", value, "femalecount");
			return (Criteria) this;
		}

		public Criteria andFemalecountGreaterThan(Integer value) {
			addCriterion("femalecount >", value, "femalecount");
			return (Criteria) this;
		}

		public Criteria andFemalecountGreaterThanOrEqualTo(Integer value) {
			addCriterion("femalecount >=", value, "femalecount");
			return (Criteria) this;
		}

		public Criteria andFemalecountLessThan(Integer value) {
			addCriterion("femalecount <", value, "femalecount");
			return (Criteria) this;
		}

		public Criteria andFemalecountLessThanOrEqualTo(Integer value) {
			addCriterion("femalecount <=", value, "femalecount");
			return (Criteria) this;
		}

		public Criteria andFemalecountIn(List<Integer> values) {
			addCriterion("femalecount in", values, "femalecount");
			return (Criteria) this;
		}

		public Criteria andFemalecountNotIn(List<Integer> values) {
			addCriterion("femalecount not in", values, "femalecount");
			return (Criteria) this;
		}

		public Criteria andFemalecountBetween(Integer value1, Integer value2) {
			addCriterion("femalecount between", value1, value2, "femalecount");
			return (Criteria) this;
		}

		public Criteria andFemalecountNotBetween(Integer value1, Integer value2) {
			addCriterion("femalecount not between", value1, value2, "femalecount");
			return (Criteria) this;
		}

		public Criteria andComPredictIsNull() {
			addCriterion("com_predict is null");
			return (Criteria) this;
		}

		public Criteria andComPredictIsNotNull() {
			addCriterion("com_predict is not null");
			return (Criteria) this;
		}

		public Criteria andComPredictEqualTo(String value) {
			addCriterion("com_predict =", value, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictNotEqualTo(String value) {
			addCriterion("com_predict <>", value, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictGreaterThan(String value) {
			addCriterion("com_predict >", value, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictGreaterThanOrEqualTo(String value) {
			addCriterion("com_predict >=", value, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictLessThan(String value) {
			addCriterion("com_predict <", value, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictLessThanOrEqualTo(String value) {
			addCriterion("com_predict <=", value, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictLike(String value) {
			addCriterion("com_predict like", value, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictNotLike(String value) {
			addCriterion("com_predict not like", value, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictIn(List<String> values) {
			addCriterion("com_predict in", values, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictNotIn(List<String> values) {
			addCriterion("com_predict not in", values, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictBetween(String value1, String value2) {
			addCriterion("com_predict between", value1, value2, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComPredictNotBetween(String value1, String value2) {
			addCriterion("com_predict not between", value1, value2, "comPredict");
			return (Criteria) this;
		}

		public Criteria andComConfidenceIsNull() {
			addCriterion("com_confidence is null");
			return (Criteria) this;
		}

		public Criteria andComConfidenceIsNotNull() {
			addCriterion("com_confidence is not null");
			return (Criteria) this;
		}

		public Criteria andComConfidenceEqualTo(Integer value) {
			addCriterion("com_confidence =", value, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andComConfidenceNotEqualTo(Integer value) {
			addCriterion("com_confidence <>", value, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andComConfidenceGreaterThan(Integer value) {
			addCriterion("com_confidence >", value, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andComConfidenceGreaterThanOrEqualTo(Integer value) {
			addCriterion("com_confidence >=", value, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andComConfidenceLessThan(Integer value) {
			addCriterion("com_confidence <", value, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andComConfidenceLessThanOrEqualTo(Integer value) {
			addCriterion("com_confidence <=", value, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andComConfidenceIn(List<Integer> values) {
			addCriterion("com_confidence in", values, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andComConfidenceNotIn(List<Integer> values) {
			addCriterion("com_confidence not in", values, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andComConfidenceBetween(Integer value1, Integer value2) {
			addCriterion("com_confidence between", value1, value2, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andComConfidenceNotBetween(Integer value1, Integer value2) {
			addCriterion("com_confidence not between", value1, value2, "comConfidence");
			return (Criteria) this;
		}

		public Criteria andStatusIsNull() {
			addCriterion("status is null");
			return (Criteria) this;
		}

		public Criteria andStatusIsNotNull() {
			addCriterion("status is not null");
			return (Criteria) this;
		}

		public Criteria andStatusEqualTo(Integer value) {
			addCriterion("status =", value, "status");
			return (Criteria) this;
		}

		public Criteria andStatusNotEqualTo(Integer value) {
			addCriterion("status <>", value, "status");
			return (Criteria) this;
		}

		public Criteria andStatusGreaterThan(Integer value) {
			addCriterion("status >", value, "status");
			return (Criteria) this;
		}

		public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
			addCriterion("status >=", value, "status");
			return (Criteria) this;
		}

		public Criteria andStatusLessThan(Integer value) {
			addCriterion("status <", value, "status");
			return (Criteria) this;
		}

		public Criteria andStatusLessThanOrEqualTo(Integer value) {
			addCriterion("status <=", value, "status");
			return (Criteria) this;
		}

		public Criteria andStatusIn(List<Integer> values) {
			addCriterion("status in", values, "status");
			return (Criteria) this;
		}

		public Criteria andStatusNotIn(List<Integer> values) {
			addCriterion("status not in", values, "status");
			return (Criteria) this;
		}

		public Criteria andStatusBetween(Integer value1, Integer value2) {
			addCriterion("status between", value1, value2, "status");
			return (Criteria) this;
		}

		public Criteria andStatusNotBetween(Integer value1, Integer value2) {
			addCriterion("status not between", value1, value2, "status");
			return (Criteria) this;
		}
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table public.ol_race
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
     * This class corresponds to the database table public.ol_race
     *
     * @mbg.generated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }
}
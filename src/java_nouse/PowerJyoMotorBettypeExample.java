package com.pengkong.boatrace.mybatis.entity;

import java.util.ArrayList;
import java.util.List;

public class PowerJyoMotorBettypeExample {
    /**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	protected String orderByClause;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	protected boolean distinct;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	protected List<Criteria> oredCriteria;

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	public PowerJyoMotorBettypeExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
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
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table public.power_jyo_motor_bettype
	 * @mbg.generated
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table public.power_jyo_motor_bettype
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

		public Criteria andBettypeIsNull() {
			addCriterion("bettype is null");
			return (Criteria) this;
		}

		public Criteria andBettypeIsNotNull() {
			addCriterion("bettype is not null");
			return (Criteria) this;
		}

		public Criteria andBettypeEqualTo(String value) {
			addCriterion("bettype =", value, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeNotEqualTo(String value) {
			addCriterion("bettype <>", value, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeGreaterThan(String value) {
			addCriterion("bettype >", value, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeGreaterThanOrEqualTo(String value) {
			addCriterion("bettype >=", value, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeLessThan(String value) {
			addCriterion("bettype <", value, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeLessThanOrEqualTo(String value) {
			addCriterion("bettype <=", value, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeLike(String value) {
			addCriterion("bettype like", value, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeNotLike(String value) {
			addCriterion("bettype not like", value, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeIn(List<String> values) {
			addCriterion("bettype in", values, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeNotIn(List<String> values) {
			addCriterion("bettype not in", values, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeBetween(String value1, String value2) {
			addCriterion("bettype between", value1, value2, "bettype");
			return (Criteria) this;
		}

		public Criteria andBettypeNotBetween(String value1, String value2) {
			addCriterion("bettype not between", value1, value2, "bettype");
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

		public Criteria andMotornoIsNull() {
			addCriterion("motorno is null");
			return (Criteria) this;
		}

		public Criteria andMotornoIsNotNull() {
			addCriterion("motorno is not null");
			return (Criteria) this;
		}

		public Criteria andMotornoEqualTo(Short value) {
			addCriterion("motorno =", value, "motorno");
			return (Criteria) this;
		}

		public Criteria andMotornoNotEqualTo(Short value) {
			addCriterion("motorno <>", value, "motorno");
			return (Criteria) this;
		}

		public Criteria andMotornoGreaterThan(Short value) {
			addCriterion("motorno >", value, "motorno");
			return (Criteria) this;
		}

		public Criteria andMotornoGreaterThanOrEqualTo(Short value) {
			addCriterion("motorno >=", value, "motorno");
			return (Criteria) this;
		}

		public Criteria andMotornoLessThan(Short value) {
			addCriterion("motorno <", value, "motorno");
			return (Criteria) this;
		}

		public Criteria andMotornoLessThanOrEqualTo(Short value) {
			addCriterion("motorno <=", value, "motorno");
			return (Criteria) this;
		}

		public Criteria andMotornoIn(List<Short> values) {
			addCriterion("motorno in", values, "motorno");
			return (Criteria) this;
		}

		public Criteria andMotornoNotIn(List<Short> values) {
			addCriterion("motorno not in", values, "motorno");
			return (Criteria) this;
		}

		public Criteria andMotornoBetween(Short value1, Short value2) {
			addCriterion("motorno between", value1, value2, "motorno");
			return (Criteria) this;
		}

		public Criteria andMotornoNotBetween(Short value1, Short value2) {
			addCriterion("motorno not between", value1, value2, "motorno");
			return (Criteria) this;
		}

		public Criteria andWakuIsNull() {
			addCriterion("waku is null");
			return (Criteria) this;
		}

		public Criteria andWakuIsNotNull() {
			addCriterion("waku is not null");
			return (Criteria) this;
		}

		public Criteria andWakuEqualTo(String value) {
			addCriterion("waku =", value, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuNotEqualTo(String value) {
			addCriterion("waku <>", value, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuGreaterThan(String value) {
			addCriterion("waku >", value, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuGreaterThanOrEqualTo(String value) {
			addCriterion("waku >=", value, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuLessThan(String value) {
			addCriterion("waku <", value, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuLessThanOrEqualTo(String value) {
			addCriterion("waku <=", value, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuLike(String value) {
			addCriterion("waku like", value, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuNotLike(String value) {
			addCriterion("waku not like", value, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuIn(List<String> values) {
			addCriterion("waku in", values, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuNotIn(List<String> values) {
			addCriterion("waku not in", values, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuBetween(String value1, String value2) {
			addCriterion("waku between", value1, value2, "waku");
			return (Criteria) this;
		}

		public Criteria andWakuNotBetween(String value1, String value2) {
			addCriterion("waku not between", value1, value2, "waku");
			return (Criteria) this;
		}

		public Criteria andPowerrankIsNull() {
			addCriterion("powerrank is null");
			return (Criteria) this;
		}

		public Criteria andPowerrankIsNotNull() {
			addCriterion("powerrank is not null");
			return (Criteria) this;
		}

		public Criteria andPowerrankEqualTo(Short value) {
			addCriterion("powerrank =", value, "powerrank");
			return (Criteria) this;
		}

		public Criteria andPowerrankNotEqualTo(Short value) {
			addCriterion("powerrank <>", value, "powerrank");
			return (Criteria) this;
		}

		public Criteria andPowerrankGreaterThan(Short value) {
			addCriterion("powerrank >", value, "powerrank");
			return (Criteria) this;
		}

		public Criteria andPowerrankGreaterThanOrEqualTo(Short value) {
			addCriterion("powerrank >=", value, "powerrank");
			return (Criteria) this;
		}

		public Criteria andPowerrankLessThan(Short value) {
			addCriterion("powerrank <", value, "powerrank");
			return (Criteria) this;
		}

		public Criteria andPowerrankLessThanOrEqualTo(Short value) {
			addCriterion("powerrank <=", value, "powerrank");
			return (Criteria) this;
		}

		public Criteria andPowerrankIn(List<Short> values) {
			addCriterion("powerrank in", values, "powerrank");
			return (Criteria) this;
		}

		public Criteria andPowerrankNotIn(List<Short> values) {
			addCriterion("powerrank not in", values, "powerrank");
			return (Criteria) this;
		}

		public Criteria andPowerrankBetween(Short value1, Short value2) {
			addCriterion("powerrank between", value1, value2, "powerrank");
			return (Criteria) this;
		}

		public Criteria andPowerrankNotBetween(Short value1, Short value2) {
			addCriterion("powerrank not between", value1, value2, "powerrank");
			return (Criteria) this;
		}
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table public.power_jyo_motor_bettype
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
     * This class corresponds to the database table public.power_jyo_motor_bettype
     *
     * @mbg.generated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }
}
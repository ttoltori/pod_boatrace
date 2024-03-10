package com.pengkong.boatrace.mybatis.entity;

import java.util.ArrayList;
import java.util.List;

public class OddsResultExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    public OddsResultExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
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
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table public.odds_result
     *
     * @mbg.generated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table public.odds_result
     *
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

        public Criteria andRacenoEqualTo(Short value) {
            addCriterion("raceno =", value, "raceno");
            return (Criteria) this;
        }

        public Criteria andRacenoNotEqualTo(Short value) {
            addCriterion("raceno <>", value, "raceno");
            return (Criteria) this;
        }

        public Criteria andRacenoGreaterThan(Short value) {
            addCriterion("raceno >", value, "raceno");
            return (Criteria) this;
        }

        public Criteria andRacenoGreaterThanOrEqualTo(Short value) {
            addCriterion("raceno >=", value, "raceno");
            return (Criteria) this;
        }

        public Criteria andRacenoLessThan(Short value) {
            addCriterion("raceno <", value, "raceno");
            return (Criteria) this;
        }

        public Criteria andRacenoLessThanOrEqualTo(Short value) {
            addCriterion("raceno <=", value, "raceno");
            return (Criteria) this;
        }

        public Criteria andRacenoIn(List<Short> values) {
            addCriterion("raceno in", values, "raceno");
            return (Criteria) this;
        }

        public Criteria andRacenoNotIn(List<Short> values) {
            addCriterion("raceno not in", values, "raceno");
            return (Criteria) this;
        }

        public Criteria andRacenoBetween(Short value1, Short value2) {
            addCriterion("raceno between", value1, value2, "raceno");
            return (Criteria) this;
        }

        public Criteria andRacenoNotBetween(Short value1, Short value2) {
            addCriterion("raceno not between", value1, value2, "raceno");
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

        public Criteria andKumibanIsNull() {
            addCriterion("kumiban is null");
            return (Criteria) this;
        }

        public Criteria andKumibanIsNotNull() {
            addCriterion("kumiban is not null");
            return (Criteria) this;
        }

        public Criteria andKumibanEqualTo(String value) {
            addCriterion("kumiban =", value, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanNotEqualTo(String value) {
            addCriterion("kumiban <>", value, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanGreaterThan(String value) {
            addCriterion("kumiban >", value, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanGreaterThanOrEqualTo(String value) {
            addCriterion("kumiban >=", value, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanLessThan(String value) {
            addCriterion("kumiban <", value, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanLessThanOrEqualTo(String value) {
            addCriterion("kumiban <=", value, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanLike(String value) {
            addCriterion("kumiban like", value, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanNotLike(String value) {
            addCriterion("kumiban not like", value, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanIn(List<String> values) {
            addCriterion("kumiban in", values, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanNotIn(List<String> values) {
            addCriterion("kumiban not in", values, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanBetween(String value1, String value2) {
            addCriterion("kumiban between", value1, value2, "kumiban");
            return (Criteria) this;
        }

        public Criteria andKumibanNotBetween(String value1, String value2) {
            addCriterion("kumiban not between", value1, value2, "kumiban");
            return (Criteria) this;
        }

        public Criteria andOddsIsNull() {
            addCriterion("odds is null");
            return (Criteria) this;
        }

        public Criteria andOddsIsNotNull() {
            addCriterion("odds is not null");
            return (Criteria) this;
        }

        public Criteria andOddsEqualTo(Double value) {
            addCriterion("odds =", value, "odds");
            return (Criteria) this;
        }

        public Criteria andOddsNotEqualTo(Double value) {
            addCriterion("odds <>", value, "odds");
            return (Criteria) this;
        }

        public Criteria andOddsGreaterThan(Double value) {
            addCriterion("odds >", value, "odds");
            return (Criteria) this;
        }

        public Criteria andOddsGreaterThanOrEqualTo(Double value) {
            addCriterion("odds >=", value, "odds");
            return (Criteria) this;
        }

        public Criteria andOddsLessThan(Double value) {
            addCriterion("odds <", value, "odds");
            return (Criteria) this;
        }

        public Criteria andOddsLessThanOrEqualTo(Double value) {
            addCriterion("odds <=", value, "odds");
            return (Criteria) this;
        }

        public Criteria andOddsIn(List<Double> values) {
            addCriterion("odds in", values, "odds");
            return (Criteria) this;
        }

        public Criteria andOddsNotIn(List<Double> values) {
            addCriterion("odds not in", values, "odds");
            return (Criteria) this;
        }

        public Criteria andOddsBetween(Double value1, Double value2) {
            addCriterion("odds between", value1, value2, "odds");
            return (Criteria) this;
        }

        public Criteria andOddsNotBetween(Double value1, Double value2) {
            addCriterion("odds not between", value1, value2, "odds");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table public.odds_result
     *
     * @mbg.generated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table public.odds_result
     *
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
}
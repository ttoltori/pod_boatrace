package com.pengkong.boatrace.exp10.simulation.calculator.expectation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * 기대치(확률*옺즈)를 계산하기 위해 ml_classification으로부터 확률을 구하는 클래스.
 * @author ttolt
 *
 */
public abstract class AbstractProbabilityExpCalculator {
	Logger logger = LoggerFactory.getLogger(AbstractProbabilityExpCalculator.class);
	
	Map<String, Function<DBRecord, Double>> mapMethod;

	public AbstractProbabilityExpCalculator() {
		mapMethod = new HashMap<>();
		mapMethod.put("1T", this::getProbability1T);
		mapMethod.put("2T", this::getProbability2T);
		mapMethod.put("3T", this::getProbability3T);
		mapMethod.put("2F", this::getProbability2F);
		mapMethod.put("3F", this::getProbability3F);
		
//		mapMethod.put("2M", this::getProbability2T);
//		mapMethod.put("3N", this::getProbability2T);
//		mapMethod.put("2N", this::getProbability3T);
//		mapMethod.put("3P", this::getProbability3T);
//		mapMethod.put("3R", this::getProbability3T);
//		mapMethod.put("3X", this::getProbability3T);
//		mapMethod.put("2G", this::getProbability2F);
//		mapMethod.put("3G", this::getProbability3F);
//		mapMethod.put("3B", this::getProbability3T);
//		mapMethod.put("3C", this::getProbability3T);
//		mapMethod.put("3D", this::getProbability3T);
//		mapMethod.put("3E", this::getProbability3T);
	}
	
	/** DB取得値から予想的中確率をbettype別の組み合わを取得する */
	public Double calculate(String betType, DBRecord rec) {
		return mapMethod.get(betType).apply(rec);
	}
	/** 単勝 */
	abstract Double getProbability1T(DBRecord rec);
	/** 2連単 */
	abstract Double getProbability2T(DBRecord rec);
	/** 3連単 */
	abstract Double getProbability3T(DBRecord rec);
	/** 2連複 */
	abstract Double getProbability2F(DBRecord rec);
	/** 3連複 */
	abstract Double getProbability3F(DBRecord rec);
	
}

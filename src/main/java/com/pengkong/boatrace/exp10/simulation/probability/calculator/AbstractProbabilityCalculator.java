package com.pengkong.boatrace.exp10.simulation.probability.calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.MLClassificationGenerator;
import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * DB取得値から予想的中確率をbettype別に組み合わせるクラス 
 * @author ttolt
 *
 */
public abstract class AbstractProbabilityCalculator {
	Logger logger = LoggerFactory.getLogger(AbstractProbabilityCalculator.class);
	
	Map<String, Function<DBRecord, Double>> mapMethod;

	public AbstractProbabilityCalculator() {
		mapMethod = new HashMap<>();
		mapMethod.put("1T", this::getProbability1T);
		mapMethod.put("2T", this::getProbability2T);
		mapMethod.put("3T", this::getProbability3T);
		mapMethod.put("2F", this::getProbability2F);
		mapMethod.put("3F", this::getProbability3F);
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

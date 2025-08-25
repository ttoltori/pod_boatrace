package com.pengkong.boatrace.exp10.simulation.calculator.probability;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.MLClassificationGenerator;
import com.pengkong.boatrace.server.db.dto.DBRecord;
import com.pengkong.common.MathUtil;

/**
 * DB取得値から予想的中確率をbettype別に組み合わせるクラス 
 * @author ttolt
 *
 */
public abstract class AbstractProbabilityCalculator {
	Logger logger = LoggerFactory.getLogger(AbstractProbabilityCalculator.class);
	
	Map<String, Function<DBRecord, Double>> mapMethod;

	Double rp1;
	Double rp2;
	Double rp3;
	Double rp4;
	
	public AbstractProbabilityCalculator() {
		mapMethod = new HashMap<>();
		mapMethod.put("1T", this::getProbability1T);
		mapMethod.put("2T", this::getProbability2T);
		mapMethod.put("3T", this::getProbability3T);
		mapMethod.put("2F", this::getProbability2F);
		mapMethod.put("3F", this::getProbability3F);
		
		mapMethod.put("2M", this::getProbability2M);
		mapMethod.put("3N", this::getProbability3N);
		mapMethod.put("2N", this::getProbability2N);
		mapMethod.put("3P", this::getProbability3P);
		mapMethod.put("3R", this::getProbability3R);
		mapMethod.put("3X", this::getProbability3X);
		mapMethod.put("2G", this::getProbability2G);
		mapMethod.put("3G", this::getProbability3G);
		mapMethod.put("3B", this::getProbability3B);
		mapMethod.put("3C", this::getProbability3C);
		mapMethod.put("3D", this::getProbability3D);
		mapMethod.put("3E", this::getProbability3E);
	}
	
	/** DB取得値から予想的中確率をbettype別の組み合わを取得する */
	public Double calculate(String statBettype, DBRecord rec) {
		if (rec.getDouble("probability6") != null )
				calculateRelativeProbability(rec);
		return mapMethod.get(statBettype).apply(rec);
	}
	
	private void calculateRelativeProbability(DBRecord rec) {
		Double sum  = rec.getDouble("probability1") + rec.getDouble("probability2") + rec.getDouble("probability3")
		  + rec.getDouble("probability4") + rec.getDouble("probability5") + rec.getDouble("probability6");
		
		rp1 = rec.getDouble("probability1") / sum;
		rp2 = rec.getDouble("probability2") / sum;
		rp3 = rec.getDouble("probability3") / sum;
		rp4 = rec.getDouble("probability4") / sum;
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
	
	abstract Double getProbability2M(DBRecord rec);
	abstract Double getProbability3N(DBRecord rec);
	abstract Double getProbability2N(DBRecord rec);
	abstract Double getProbability3P(DBRecord rec);
	abstract Double getProbability3R(DBRecord rec);
	abstract Double getProbability3X(DBRecord rec);
	abstract Double getProbability2G(DBRecord rec);
	abstract Double getProbability3G(DBRecord rec);
	abstract Double getProbability3B(DBRecord rec);
	abstract Double getProbability3C(DBRecord rec);
	abstract Double getProbability3D(DBRecord rec);
	abstract Double getProbability3E(DBRecord rec);
	
}

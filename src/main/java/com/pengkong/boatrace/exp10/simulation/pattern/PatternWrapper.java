package com.pengkong.boatrace.exp10.simulation.pattern;

import com.pengkong.boatrace.server.db.dto.DBRecord;

/**
 * DB취득 데이터로부터 패턴을 추출하기 위한 추상클래스. 
 * @author ttolt
 *
 */
public class PatternWrapper extends AbstractPatternWrapper {
	private DBRecord rec;

	public PatternWrapper(DBRecord rec) {
		this.rec = rec;
	}

	@Override
	String getJyo() {
		return rec.getString("jyocd");
	}

	@Override
	String getRaceNo() {
		return String.valueOf(rec.get("raceno"));
	}

	@Override
	String getTurn() {
		return rec.getString("turn");
	}

	@Override
	String getAlcnt() {
		return String.valueOf(rec.get("alevelcount"));
	}

	@Override
	String getGrade() {
		return rec.getString("grade");
	}

	@Override
	String getRtype() {
		return rec.getString("racetype");
	}

	@Override
	String getWakulevellist() {
		return rec.getString("wakulevellist");
	}

	@Override
	String getComConfidence() {
		return String.valueOf(rec.get("com_confidence"));
	}

	@Override
	String getComPredict() {
		return rec.getString("com_predict");
	}

	@Override
	String getPrediction1() {
		return rec.getString("prediction1");
	}

	@Override
	String getPrediction2() {
		return rec.getString("prediction2");
	}

	@Override
	String getPrediction3() {
		return rec.getString("prediction3");
	}

	@Override
	double[] getNationwiningrate() {
		return rec.getDoubleArray("nationwiningrate");
	}

	@Override
	double[] getProbabilities1() {
		return rec.getDoubleArray("probabilities1");
	}

	@Override
	double[] getProbabilities2() {
		return rec.getDoubleArray("probabilities2");
	}

	@Override
	double getProbability1() {
		return rec.getDouble("probability1");
	}

	@Override
	double getProbability2() {
		return rec.getDouble("probability2");
	}

	@Override
	double getProbability3() {
		return rec.getDouble("probability3");
	}

	@Override
	double getOdds() {
		return rec.getDouble("odds");
	}
	
	@Override
	String getFixedEntrance() {
		return rec.getString("fixedentrance");
	}

	@Override
	String getTimezone() {
		return rec.getString("timezone");
	}

	@Override
	String getCond1() {
		return rec.getString("cond1");
	}

	@Override
	String getN1point1() {
		return rec.getString("n1point1");
	}

	@Override
	String getN1pointWaku1() {
		return rec.getString("n1pointwaku1");	
	}

	@Override
	String getCond2() {
		return rec.getString("cond2");
	}

	@Override
	String getN1point2() {
		return rec.getString("n1point2");
	}

	@Override
	String getN1pointWaku2() {
		return rec.getString("n1pointwaku2");	
	}
}

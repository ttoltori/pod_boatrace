package com.pengkong.boatrace.server.util;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.pengkong.boatrace.exception.NullValueException;
import com.pengkong.boatrace.server.db.dto.BoatDbRecord;
import com.pengkong.boatrace.server.stat.BoatRegressionResult;
import com.pengkong.boatrace.server.stat.BoatStatObject;

public class BoatMath {

	/** 평균 */
	static final Mean mean = new Mean();
	/** 평균편차 */
	static final StandardDeviation sdPopulation = new StandardDeviation(true);
	static final SimpleRegression regr = new SimpleRegression();
	public BoatMath() {
	}

	/** 부모의 투표회수에 대한 점유율 */
	public static double getBetCntRatePerParent(BoatStatObject obj) {
		return (double)obj.betcnt / (double) ((BoatStatObject)obj.parent).betcnt;
	}
	
	/** 부모의 적중금액에 대한 점유율 */
	public static double getHitAmtRatePerParent(BoatStatObject obj) {
		return (double)obj.hitamt / (double)((BoatStatObject)obj.parent).hitamt;
	}

	/** 적중률 */
	public static double getHitRate(BoatStatObject obj) {
		return (double)obj.hitcnt / (double)obj.betcnt;
	}
	
	/** 수익율 */
	public static double getIncomeRate(BoatStatObject obj) {
		return (double)obj.hitamt / (double)obj.betamt;
	}
	
	/** 투표회수에 대한 적자상태횟수의 비율 */
	public static double getMinusBalanceCntRate(BoatStatObject obj) {
		return (double)obj.minusBalanceCount / (double)obj.betcnt;
	}

	/** 특정 컬럼에 대한 평균값 */
	public static double getAverage(BoatStatObject obj, String column) {
		return BoatMath.getAverage(obj.listDbRecord, column);
	}

	/** 특정 컬럼에 대한 평균편차 */
	public static double getDeviation(BoatStatObject obj, String column) {
		return BoatMath.getDeviation(obj.listDbRecord, column);
	}
	
	/** 디비 레코드들에 대한 평균값 */
	public static double getAverage(List<BoatDbRecord> listRec, String column) {
		return mean.evaluate(getArrayData(listRec, column));
	}
	
	/** 디비 레코드들에 대한 평균편차 */
	public static double getDeviation(List<BoatDbRecord> listRec, String column) {
		return sdPopulation.evaluate(getArrayData(listRec, column));
	}
	
	/** 디비 레코드들에 대한 regression취득 */
	public static BoatRegressionResult getRegression(BoatStatObject bso) throws Exception {
		regr.clear();
		BoatRegressionResult result = new BoatRegressionResult();
		
		double[] arrData = bso.getLinearIncomeAmt();
		for (int i = 0; i < arrData.length; i++) {
			regr.addData(i, arrData[i]);
		}
		regr.regress();
		
		result.slope = regr.getSlope();
		result.intercept = regr.getIntercept();
		return result;
	}
	
	private static double[] getArrayData(List<BoatDbRecord> listRec, String column) {
		double[] arrData = new double[listRec.size()];
		try {
			for (int i = 0; i < listRec.size(); i++) {
				arrData[i] = listRec.get(i).getDoubleValue(column);
			}
		} catch (NullValueException e) {
			// null값은 skip한다.
		}
		
		return arrData;
	}

}

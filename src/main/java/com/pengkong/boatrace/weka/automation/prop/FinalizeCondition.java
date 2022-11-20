package com.pengkong.boatrace.weka.automation.prop;

// 2019/9/16 final을 만드는 방식이 바뀌어서 사용하지 않는다. 
@Deprecated
public class FinalizeCondition {
	public String paramPercentIncomeAmtStr;
	public String paramPercentHitRateStr;
	public String paramPercentIncomeRateStr;
	public String paramPercentMinusDaysRateStr;
	public String paramPercentIncomeAmtSlopeStr;
	public String paramPercentRecoverPlusRateStr;
	

	public FinalizeCondition(String paramPercentIncomeAmt, String paramPercentHitRate, String paramPercentIncomeRate,
			String paramPercentMinusDaysRate, String paramPercentIncomeAmtSlope, String paramPercentRecoverPlusRate) {
		super();
		this.paramPercentIncomeAmtStr = paramPercentIncomeAmt;
		this.paramPercentHitRateStr = paramPercentHitRate;
		this.paramPercentIncomeRateStr = paramPercentIncomeRate;
		this.paramPercentMinusDaysRateStr = paramPercentMinusDaysRate;
		this.paramPercentIncomeAmtSlopeStr = paramPercentIncomeAmtSlope;
		this.paramPercentRecoverPlusRateStr = paramPercentRecoverPlusRate;
	}

	public boolean isEmpty() {
		return (paramPercentIncomeAmtStr == null && paramPercentHitRateStr == null && paramPercentIncomeRateStr == null &&
				paramPercentMinusDaysRateStr == null && paramPercentIncomeAmtSlopeStr == null && paramPercentRecoverPlusRateStr == null);
	}
	
	public String toString() {
		return "{PercentIncomeAmtSlope:" + paramPercentIncomeAmtSlopeStr + ", PercentRecoverPlusRate:" + paramPercentRecoverPlusRateStr + 
			   ", PercentBetCnt:" + paramPercentIncomeAmtStr + ", PercentHitRate:" + paramPercentHitRateStr + ", PercentIncomeRate:"
				+ paramPercentIncomeRateStr + ", PercentMinusDaysRate:" + paramPercentMinusDaysRateStr + "}";
	}
}

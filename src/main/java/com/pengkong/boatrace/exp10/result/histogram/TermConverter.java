package com.pengkong.boatrace.exp10.result.histogram;

import com.pengkong.boatrace.util.BoatUtil;

/**
 * 日単位の期間範囲を区切る.
 * @author ttolt
 *
 */
public class TermConverter {

	/** 時系列グラフの期間単位カウンタ */
	private int termCount = 0;
	
	/** 時系列グラフの期間単位 */
	private int termDays = 0;
	
	private String lastTermYmd;
	
	public TermConverter(int termDays) {
		this.termDays = termDays;
	}
	
	/** 
	 * 指定期間単位のfactorを取得する。
	 * @return 
	 */
	public Double getTermFactor(String ymd) {
		if (lastTermYmd == null) {
			lastTermYmd = ymd;
		}
		
		int days = BoatUtil.daysBetween(lastTermYmd, ymd);
		if (days >= termDays) {
			termCount++;
			lastTermYmd = ymd;
		}
		
		return Double.valueOf(termCount);
	}
}

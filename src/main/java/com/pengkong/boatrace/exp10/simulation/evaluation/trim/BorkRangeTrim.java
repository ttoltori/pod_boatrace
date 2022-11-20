package com.pengkong.boatrace.exp10.simulation.evaluation.trim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.exp10.simulation.range.AbstractRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.LRtrimRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.LtrimRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.RangeStatUnit;
import com.pengkong.boatrace.exp10.simulation.range.SelectiveLRtrimRangeFinder;
import com.pengkong.boatrace.exp10.simulation.range.SelectiveRangeFinder;
import com.pengkong.boatrace.server.db.dto.DBRecord;

public class BorkRangeTrim {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	AbstractRangeFinder rangeFinder;
	
	String trimType;
	
	void initialize() throws Exception {
		
		this.trimType = prop.getString("bork_trim");
		if (trimType.equals("L")) { // LTrim
			rangeFinder = new LtrimRangeFinder();
		} else if (trimType.equals("LR")) { // LRTrim
			rangeFinder = new LRtrimRangeFinder();
		} else if (trimType.equals("SLR")) { // SelectiveLRTrim
			rangeFinder = new SelectiveLRtrimRangeFinder();
		} else if (trimType.equals("S")) { // SelectiveLRTrim
			rangeFinder = new SelectiveRangeFinder();
		} else {
			throw new IllegalStateException("Undefined bork_tirm property");
		}
	}
	
	/**
	 * Evaluationのborkに設定する範囲文字列を結合する。
	 * @return bork文字列 例) 0~1=1,3=5=1
	 * @throws Exception
	 */
	public String createBorkString(DBRecord rec) throws Exception {
		if (trimType == null) {
			initialize();
		}

		// ml_bork_evaluationから取得したarrayをRangeStatUnitに変換する
		rangeFinder.setListUnit(createRangeStatUnits(rec));

		List<RangeStatUnit> units = rangeFinder.findBestRangeStatUnits();
		if (units.size() <= 0) {
			return null;
		}
		
		
		// 範囲
		if (!trimType.equals("S")) { // if not selective
			int startBork = units.get(0).factorValue().intValue(); 
			int endBork = units.get(units.size()-1).factorValue().intValue();
			
			// 例) 0~5=1
			return String.valueOf(startBork) + Delimeter.WAVE.getValue() + String.valueOf(endBork) + "=1"; 
		} else {
			StringBuilder sb = new StringBuilder();
			// 個別 select
			for (RangeStatUnit unit : units) {
				int bork = unit.factorValue().intValue();
				sb.append(String.valueOf(bork) + Delimeter.WAVE.getValue() + String.valueOf(bork) + "=1,");
			}
			return sb.substring(0, sb.length()-1);
		}
	}

	
	/**
	 * Evaluationのborに設定する範囲文字列を結合する。
	 * @return bork文字列 例) 1.2~2.5=1,3.3=5.6=1
	 * @throws Exception
	 */
	public String createBorString(DBRecord rec) throws Exception {
		if (trimType == null) {
			initialize();
		}
		
		rangeFinder.setListUnit(createRangeStatUnits(rec));

		List<RangeStatUnit> units = rangeFinder.findBestRangeStatUnits();
		if (units.size() <= 0) {
			return null;
		}
		
		// 範囲
		if (!trimType.equals("S")) { // if not selective
			double[] minMax = getBoddsMinMax(units);
			// 例) 1.4~5.6=1
			return String.valueOf(minMax[0]) + Delimeter.WAVE.getValue() + String.valueOf(minMax[1]) + "=1"; 
		} else {
			StringBuilder sb = new StringBuilder();
			// 個別 select
			for (RangeStatUnit unit : units) {
				double[] minMax = getBoddsMinMax(Arrays.asList(unit));
				
				sb.append(String.valueOf(minMax[0]) + Delimeter.WAVE.getValue() + String.valueOf(minMax[1]) + "=1,");
			}
			return sb.substring(0, sb.length()-1);
		}
	}

	double[] getBoddsMinMax(List<RangeStatUnit> units) {
		double[] minMax = new double[] {999, 0};
		
		for (RangeStatUnit unit : units) {
			if (unit.bOddsMin < minMax[0]) {
				minMax[0] = unit.bOddsMin; 
			}
			if (unit.bOddsMax > minMax[1]) {
				minMax[1] = unit.bOddsMax;
			}
		}
		
		return minMax;
	}
	
	List<RangeStatUnit> createRangeStatUnits(DBRecord rec) throws Exception {
		
		List<RangeStatUnit> results = new ArrayList<>();
		// 例) 1~10  -> [1, 10]
		String[] rangeToken = prop.getString("bork_range").split(Delimeter.WAVE.getValue());
		int borkMin = Integer.valueOf(rangeToken[0]);
		int borkMax = Integer.valueOf(rangeToken[1]);
		
		int[] arrBetcnt = rec.getIntArray("betcnt_arr");  
		int[] arrHitcnt = rec.getIntArray("hitcnt_arr");  
		int[] arrBetamt = rec.getIntArray("betamt_arr");  
		int[] arrHitamt = rec.getIntArray("hitamt_arr");
		double[] arrBorMin = rec.getDoubleArray("bor_min_arr");
		double[] arrBorMax = rec.getDoubleArray("bor_max_arr");
		
		for (int i = borkMin-1; i <= borkMax-1; i++) {
			RangeStatUnit unit = new RangeStatUnit(Double.valueOf(i+1));
			unit.betCnt = arrBetcnt[i];
			unit.hitCnt = arrHitcnt[i];
			unit.betAmt = arrBetamt[i];
			unit.hitAmt = arrHitamt[i];
			unit.bOddsMin = arrBorMin[i];
			unit.bOddsMax = arrBorMax[i];
			
			results.add(unit);
		}
		
		return results;
	}
}

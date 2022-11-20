package com.pengkong.boatrace.converter;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.mybatis.entity.StatWakuRecent;
import com.pengkong.boatrace.service.stat.RaceStatDataSet;

/**
 * １～６枠の統計計算結果データをStatWakuRecentレコードへ変換する
 * @author qwerty
 *
 */
public class RaceStatDataSet2StatWakuRecent {

	public static StatWakuRecent convert(List<RaceStatDataSet> listRaceStatDataSet) throws WarningException, Exception {
		StatWakuRecent result = new StatWakuRecent();
		if (listRaceStatDataSet.size() != 6) {
			throw new WarningException("invalid listRaceStatDataSet#size():" + listRaceStatDataSet.size()); 
		}
		
		RaceStatDataSet raceStatDataSet = listRaceStatDataSet.get(0);
		result.setYmd(raceStatDataSet.ymd);
		result.setJyocd(raceStatDataSet.jyoCd);
		result.setRaceno(raceStatDataSet.raceNo);
		
		Method method = null;
		for (int i=0; i < 6; i++) {
			// stat_waku_recent.statnationwiningrate1~6
			method = StatWakuRecent.class.getMethod("setStatnationwiningrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.nationWiningRate.rate1);
			
			// stat_waku_recent.statnation2winingrate1~6
			method = StatWakuRecent.class.getMethod("setStatnation2winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.nationWiningRate.rate2);
			
			// stat_waku_recent.statnation3winingrate1~6
			method = StatWakuRecent.class.getMethod("setStatnation3winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.nationWiningRate.rate3);
			
			// stat_waku_recent.statlocalwiningrate1~6
			method = StatWakuRecent.class.getMethod("setStatlocalwiningrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.localWiningRate.rate1);
			
			// stat_waku_recent.statlocal2winingrate1~6
			method = StatWakuRecent.class.getMethod("setStatlocal2winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.localWiningRate.rate2);
			
			// stat_waku_recent.statlocal3winingrate1~6
			method = StatWakuRecent.class.getMethod("setStatlocal3winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.localWiningRate.rate3);
			
			// stat_waku_recent.statstart1~6
			method = StatWakuRecent.class.getMethod("setStatstart" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.time.start);
			
			// stat_waku_recent.statmotorwiningrate1~6
			method = StatWakuRecent.class.getMethod("setStatmotorwiningrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.motorWiningRate.rate1);
			
			// stat_waku_recent.statmotor2winingrate1~6
			method = StatWakuRecent.class.getMethod("setStatmotor2winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.motorWiningRate.rate2);
			
			// stat_waku_recent.statmotor3winingrate1~6
			method = StatWakuRecent.class.getMethod("setStatmotor3winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statDataSet.motorWiningRate.rate3);
		}
		
		return result;
	}

}

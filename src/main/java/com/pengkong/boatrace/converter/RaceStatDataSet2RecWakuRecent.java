package com.pengkong.boatrace.converter;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.mybatis.entity.RecWakuRecent;
import com.pengkong.boatrace.service.stat.RaceStatDataSet;

/**
 * １～６枠の統計計算結果データをRecWakuRecentレコードへ変換する
 * @author qwerty
 *
 */
public class RaceStatDataSet2RecWakuRecent {

	public static RecWakuRecent convert(List<RaceStatDataSet> listRaceStatDataSet) throws WarningException, Exception {
		RecWakuRecent result = new RecWakuRecent();
		if (listRaceStatDataSet.size() != 6) {
			throw new WarningException("invalid listRaceStatDataSet#size():" + listRaceStatDataSet.size()); 
		}
		
		RaceStatDataSet raceStatDataSet = listRaceStatDataSet.get(0);
		result.setYmd(raceStatDataSet.ymd);
		result.setJyocd(raceStatDataSet.jyoCd);
		result.setRaceno(raceStatDataSet.raceNo);
		
		Method method = null;
		for (int i=0; i < 6; i++) {
			// rec_waku_recent.recentnationwiningrate1~6
			method = RecWakuRecent.class.getMethod("setRecentnationwiningrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.nationWiningRate.rate1);
			
			// rec_waku_recent.recentnation2winingrate1~6
			method = RecWakuRecent.class.getMethod("setRecentnation2winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.nationWiningRate.rate2);
			
			// rec_waku_recent.recentnation3winingrate1~6
			method = RecWakuRecent.class.getMethod("setRecentnation3winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.nationWiningRate.rate3);
			
			// rec_waku_recent.recentlocalwiningrate1~6
			method = RecWakuRecent.class.getMethod("setRecentlocalwiningrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.localWiningRate.rate1);
			
			// rec_waku_recent.recentlocal2winingrate1~6
			method = RecWakuRecent.class.getMethod("setRecentlocal2winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.localWiningRate.rate2);
			
			// rec_waku_recent.recentlocal3winingrate1~6
			method = RecWakuRecent.class.getMethod("setRecentlocal3winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.localWiningRate.rate3);
			
			// rec_waku_recent.recentavgst1~6
			method = RecWakuRecent.class.getMethod("setRecentavgst" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.time.start);
			
			// rec_waku_recent.recentavgtime1~6
			method = RecWakuRecent.class.getMethod("setRecentavgtime" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.time.time);
			
			// rec_waku_recent.recentmotor2winingrate1~6
			method = RecWakuRecent.class.getMethod("setRecentmotor2winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.motorWiningRate.rate2);
			
			// rec_waku_recent.recentmotor3winingrate1~6
			method = RecWakuRecent.class.getMethod("setRecentmotor3winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).recDataSet.motorWiningRate.rate3);
		}
		
		return result;
	}

}

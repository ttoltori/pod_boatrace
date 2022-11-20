package com.pengkong.boatrace.converter;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.mybatis.entity.StatWakuWin;
import com.pengkong.boatrace.service.stat.RaceStatDataSet;

/**
 * １～６枠の統計計算結果データをStatWakuWinレコードへ変換する
 * @author qwerty
 *
 */
public class RaceStatDataSet2StatWakuWin {

	public static StatWakuWin convert(List<RaceStatDataSet> listRaceStatDataSet) throws WarningException, Exception {
		StatWakuWin result = new StatWakuWin();
		if (listRaceStatDataSet.size() != 6) {
			throw new WarningException("invalid listRaceStatDataSet#size():" + listRaceStatDataSet.size()); 
		}
		
		RaceStatDataSet raceStatDataSet = listRaceStatDataSet.get(0);
		result.setYmd(raceStatDataSet.ymd);
		result.setJyocd(raceStatDataSet.jyoCd);
		result.setRaceno(raceStatDataSet.raceNo);
		
		Method method = null;
		for (int i=0; i < 6; i++) {
			// stat_waku_win.racerruncount1~6
			method = StatWakuWin.class.getMethod("setRacerruncount" + (i+1), Integer.class);
			method.invoke(result, listRaceStatDataSet.get(i).statWakuDataSet.racerWiningRate.runCount);
			
			// stat_waku_win.racerwiningrate1~6
			method = StatWakuWin.class.getMethod("setRacerwiningrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statWakuDataSet.racerWiningRate.rate1);
			
			// stat_waku_win.racer2winingrate1~6
			method = StatWakuWin.class.getMethod("setRacer2winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statWakuDataSet.racerWiningRate.rate2);
			
			// stat_waku_win.racer3winingrate1~6
			method = StatWakuWin.class.getMethod("setRacer3winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statWakuDataSet.racerWiningRate.rate3);
			
			// stat_waku_win.motorruncount1~6
			method = StatWakuWin.class.getMethod("setMotorruncount" + (i+1), Integer.class);
			method.invoke(result, listRaceStatDataSet.get(i).statWakuDataSet.motorWiningRate.runCount);
			
			// stat_waku_win.motorwiningrate1~6
			method = StatWakuWin.class.getMethod("setMotorwiningrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statWakuDataSet.motorWiningRate.rate1);
			
			// stat_waku_win.motor2winingrate1~6
			method = StatWakuWin.class.getMethod("setMotor2winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statWakuDataSet.motorWiningRate.rate2);
			
			// stat_waku_win.motor3winingrate1~6
			method = StatWakuWin.class.getMethod("setMotor3winingrate" + (i+1), BigDecimal.class);
			method.invoke(result, listRaceStatDataSet.get(i).statWakuDataSet.motorWiningRate.rate3);
		}
		
		return result;
	}

}

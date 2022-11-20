package com.pengkong.boatrace.converter;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.service.stat.MotorStat;
import com.pengkong.boatrace.service.stat.RaceStatDataSet;
import com.pengkong.boatrace.service.stat.RacerStat;
import com.pengkong.boatrace.service.stat.RecentDataSet;
import com.pengkong.boatrace.service.stat.StatWakuDataSet;
import com.pengkong.common.PropertyUtil;

/**
 * RacerStat, MotorStatから統計データを計算した結果を
 * rec_waku_recent, stat_waku_recent, stat_waku_winに格納するための１人分の全データを取得する
 */
public class RaceMotorStat2RaceStatDataSet {

	public RaceMotorStat2RaceStatDataSet() {
	}
	
	/**
	 * RacerStat, MotorStatから統計データを計算した結果を
	 * rec_waku_recent, stat_waku_recent, stat_waku_winに格納するための１人分の全データを取得する
	 * @param racerStat 選手マネージャ
	 * @param motorStat モータマネージャ
	 * @param baseYmd 集計基準日
	 * @param jyoCd 場コード
	 * @param waku 統計データを取得する対象選手の枠
	 * @return
	 */
	public RaceStatDataSet convert(RacerStat racerStat, MotorStat motorStat, String baseYmd, String jyoCd, Short raceNo, Short waku) {
		RaceStatDataSet raceStatDataSet = new RaceStatDataSet();
		raceStatDataSet.ymd = baseYmd;
		raceStatDataSet.jyoCd = jyoCd;
		raceStatDataSet.raceNo = raceNo;
		raceStatDataSet.waku = waku;
		
		PropertyUtil prop = PropertyUtil.getInstance();
		
		// rec_waku_recent
		RecentDataSet recDataSet = new RecentDataSet();
		recDataSet.nationWiningRate = 
				racerStat.getRecWakuRecentWiningRate(baseYmd, prop.getInteger("daysRecNationWiningRate"), BoatConst.RATE_TYPE_NATION);
		recDataSet.localWiningRate =  
				racerStat.getRecWakuRecentWiningRate(baseYmd, prop.getInteger("daysRecLocalWiningRate"), BoatConst.RATE_TYPE_LOCAL);
		recDataSet.time =  
				racerStat.getRecWakuRecentTime(baseYmd, prop.getInteger("daysRecNationWiningRate"));
		recDataSet.motorWiningRate =  
				motorStat.getRecWakuRecentMotorWiningRate(baseYmd, prop.getInteger("daysMotorWiningRate"), jyoCd);

		// stat_waku_recent
		RecentDataSet statDataSet = new RecentDataSet();
		statDataSet.nationWiningRate = 
				racerStat.getStatWakuRecentNationWiningRate(baseYmd, prop.getInteger("daysStatNationWiningRate"));
		statDataSet.localWiningRate = 
				racerStat.getStatWakuRecentLocalWiningRate(baseYmd, prop.getInteger("daysStatLocalWiningRate"), jyoCd);
		statDataSet.time = 
				racerStat.getStatWakuRecentTime(baseYmd, prop.getInteger("daysStatNationWiningRate"));
		statDataSet.motorWiningRate = 
				motorStat.getStatWakuRecentMotorWiningRate(baseYmd, prop.getInteger("daysMotorWiningRate"), jyoCd);
		
		// stat_waku_win
		StatWakuDataSet statWakuDataSet = new StatWakuDataSet();
		statWakuDataSet.racerWiningRate = 
				racerStat.getStatRacerWakuWiningRate(baseYmd, prop.getInteger("daysWakuRacerWiningRate"), waku);
		statWakuDataSet.motorWiningRate = 
				motorStat.getStatMotorWakuWiningRate(baseYmd, prop.getInteger("daysWakuMotorWiningRate"), jyoCd, waku);
		
		raceStatDataSet.recDataSet = recDataSet;
		raceStatDataSet.statDataSet = statDataSet;
		raceStatDataSet.statWakuDataSet = statWakuDataSet;
		
		return raceStatDataSet;
	}

}

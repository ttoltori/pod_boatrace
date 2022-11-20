package com.pengkong.boatrace.service.stat;

/**
 * RacerStat, MotorStatから統計データを計算した結果を
 * rec_waku_recent, stat_waku_recent, stat_waku_winに格納するための１人分の全データを保持する
 */
public class RaceStatDataSet {
	public String ymd;
	
	public String jyoCd;
	
	public Short raceNo;
	
	public Short waku;
	
	/** rec_waku_recent data set */
	public RecentDataSet recDataSet;
	
	/** stat_waku_recent data set*/
	public RecentDataSet statDataSet;
	
	/** stat_waku_win data set */
	public StatWakuDataSet statWakuDataSet;
	
	public RaceStatDataSet() {
		// TODO Auto-generated constructor stub
	}

}

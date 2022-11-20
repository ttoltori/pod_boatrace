package com.pengkong.boatrace.service.stat;

/**
 * rec_waku_recent, stat_waku_recentの１人分のデータを保持する.
 * @author qwerty
 *
 */
public class RecentDataSet {
	/** 全国勝率1,2,3 */
	public AverageWiningRate nationWiningRate;
	
	/** 当地勝率1,2,3 */
	public AverageWiningRate localWiningRate;
	
	/** 平均 ST, 平均タイム */
	public AverageTime time;
	
	/** モータ２，３連帯率 */
	public AverageWiningRate motorWiningRate;
	
	public RecentDataSet() {
	}

}

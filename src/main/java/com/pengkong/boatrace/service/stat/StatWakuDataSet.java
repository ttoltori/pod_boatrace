package com.pengkong.boatrace.service.stat;

/**
 * stat_waku_winの１人分のデータを保持する.
 * @author qwerty
 *
 */
public class StatWakuDataSet {
	/** 枠選手勝率1,2,3 */
	public AverageWiningRate racerWiningRate;
	
	/** 枠モータ勝率1,2,3 */
	public AverageWiningRate motorWiningRate;
	
	public StatWakuDataSet() {
	}

}

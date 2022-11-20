package com.pengkong.boatrace.service.manager;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.OddsMonitor;

@Deprecated
public abstract class AbstractOddsMonitorManager {
	Logger logger = LoggerFactory.getLogger(AbstractOddsMonitorManager.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	/** key = ymd_jyocd_raceno_bettype_kumiban  value=OddsMonitor*/
	Map<String, OddsMonitor> mapOddsMonitor;
	
	String currentYmd;

	/** オッズ収集を開始した日付 */
	String oddsMonitoringStartYmd;
	
	/** 直前オッズを取得する */
	public Double getBeforeOdds(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws Exception {
		if (!isInitializeOK(ymd)) {
			return null;
		}
		
		String key = String.join("_", ymd, jyoCd, raceNo, betType, kumiban);
		OddsMonitor om = mapOddsMonitor.get(key);
		if (om == null) {
			return null;
		}
		
		return om.getBeforeodds();
	}

	/**
	 * 直前オッズの存在範囲をチェックして範囲内の場合オッズをロードする
	 * @param ymd
	 * @return
	 * @throws Exception
	 */
	boolean isInitializeOK(String ymd) throws Exception {
		if (oddsMonitoringStartYmd == null) {
			oddsMonitoringStartYmd = prop.getString("odds_monitoring_start_ymd");
		}
		
		if (Integer.valueOf(ymd) < Integer.valueOf(oddsMonitoringStartYmd)) {
			return false;
		}
		
		if (!ymd.equals(currentYmd)) {
			// 直前オッズ取得先は可変なので、継承クラスに処理を委任する.
			mapOddsMonitor = load(ymd);
			currentYmd = ymd;
		}
		
		return true;
	}
	
	abstract Map<String, OddsMonitor> load(String ymd) throws Exception;
	
}

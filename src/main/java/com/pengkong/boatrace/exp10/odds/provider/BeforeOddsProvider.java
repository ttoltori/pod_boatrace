package com.pengkong.boatrace.exp10.odds.provider;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.odds.loader.AbstractOddsFileLoader;
import com.pengkong.boatrace.exp10.odds.loader.OddsMonitorFileLoader;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

/**
 * 日付単位の直前オッズを保持しながらオッズを提供する.
 * 
 * @author ttolt
 *
 */
public class BeforeOddsProvider extends AbstractOddsProvider {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	String oddsMonitorStartYmd;
	
	@Override
	protected void initialize() {
		super.initialize();
		this.oddsMonitorStartYmd = MLPropertyUtil.getInstance().getString("odds_monitoring_start_ymd");
	}
	
	@Override
	public Odds get(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws Exception {
		if (Integer.valueOf(ymd) < Integer.valueOf(oddsMonitorStartYmd)) {
			return null;
		}
		
		return super.get(ymd, jyoCd, raceNo, betType, kumiban);
	}
	
	@Override
	protected AbstractOddsFileLoader getFileLoader() {
		if (fileLoader == null) {
			fileLoader = new OddsMonitorFileLoader();
		}
		
		return fileLoader;
	}

	@Override
	protected String createOddsFilepath(String ymd) {
		return prop.getString("DIRECTORY_ODDSMONITOR") + "odds_" + ymd + ".csv";
	}

	@Override
	protected String[] createBettypes() {
		String betTypes = prop.getString("bettype");
		// 例）1T,3T,2T
		betTypes = betTypes.replace("3M", "3T");
		betTypes = betTypes.replace("2M", "2T");
		betTypes = betTypes.replace("3N", "2T");
		betTypes = betTypes.replace("2N", "3T");

		return betTypes.split(Delimeter.COMMA.getValue());
	}
}

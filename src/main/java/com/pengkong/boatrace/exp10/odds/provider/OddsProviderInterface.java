package com.pengkong.boatrace.exp10.odds.provider;

import com.pengkong.boatrace.exp10.odds.Odds;

/** オッズ提供interface */
public interface OddsProviderInterface {
	public Odds get(String ymd, String jyoCd, String raceNo, String betType, String kumiban) throws Exception;
}

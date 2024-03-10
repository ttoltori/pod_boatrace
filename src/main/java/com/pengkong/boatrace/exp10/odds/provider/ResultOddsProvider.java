package com.pengkong.boatrace.exp10.odds.provider;

import com.pengkong.boatrace.exp10.odds.loader.AbstractOddsFileLoader;
import com.pengkong.boatrace.exp10.odds.loader.OddsResultFileLoader;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
/**
 * 日付単位の確定オッズを保持しながらオッズを提供する.
 * 
 * @author ttolt
 *
 */
public class ResultOddsProvider extends AbstractOddsProvider {
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	@Override
	protected AbstractOddsFileLoader getFileLoader() {
		if (fileLoader == null) {
			fileLoader = new OddsResultFileLoader();
		}
		
		return fileLoader;
	}

	@Override
	protected String createOddsFilepath(String ymd) {
		return prop.getString("DIRECTORY_ODDS") + "odds_" + ymd + ".csv";
	}
}

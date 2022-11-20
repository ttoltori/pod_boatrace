package com.pengkong.boatrace.exp10.odds.provider;

import com.pengkong.boatrace.common.enums.Delimeter;
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

	@Override
	protected String[] createBettypes() {
		String betTypes = prop.getString("bettype");
		// 例）1T,3T,2T
		betTypes = betTypes.replace("3M", "3T");
		betTypes = betTypes.replace("2M", "2T");
		betTypes = betTypes.replace("3N", "2T");
		betTypes = betTypes.replace("2N", "3T");
		betTypes = betTypes.replace("3P", "3T");
		betTypes = betTypes.replace("3R", "3T");
		betTypes = betTypes.replace("3U", "3T");
		betTypes = betTypes.replace("3X", "3T");
		betTypes = betTypes.replace("3Y", "3T");
        betTypes = betTypes.replace("3A", "3T");
        betTypes = betTypes.replace("2A", "2T");

		return betTypes.split(Delimeter.COMMA.getValue());
	}
}

package com.pengkong.boatrace.scraping;

import com.pengkong.common.FileUtil;

public class OddsBeforeDownloader extends OddsDownloader {

	@Override
	public void save(String yyyyMMdd, String csv) throws Exception {
		String filepath = prop.getString("DIRECTORY_ODDSBEFORE") + "odds_" + yyyyMMdd + ".before.csv";
		FileUtil.appendFileByLine(filepath, csv);
	}
}

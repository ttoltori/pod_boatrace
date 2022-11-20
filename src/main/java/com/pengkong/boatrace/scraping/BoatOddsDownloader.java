package com.pengkong.boatrace.scraping;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.Setu;
import com.pengkong.boatrace.scraping.parser.RaceListPageParser;
import com.pengkong.boatrace.scraping.parser.SetuListPageParser;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class BoatOddsDownloader {

	Logger logger = LoggerFactory.getLogger(BoatOddsDownloader.class);

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	PropertyUtil prop = PropertyUtil.getInstance();
	
	public void execute(String yyyyMMdd) throws Exception {
		logger.info("ダウンロード開始  年月日：" + yyyyMMdd);
		int year = Integer.parseInt(yyyyMMdd.substring(0, 4));
		int month = Integer.parseInt(yyyyMMdd.substring(4, 6));
		int day = Integer.parseInt(yyyyMMdd.substring(6, 8));
		
		// 当該日のオッズファイルが存在すれば削除する。
		String filepathOdds = prop.getString("DIRECTORY_ODDS") + "odds_" + yyyyMMdd + ".csv";
		
		FileUtil.deleteFile(filepathOdds);

		String urlDailySetuList = BoatTemplate.URL_DAILY_SETU_LIST.replace("{yyyyMMdd}", yyyyMMdd);
		// 節一覧取得
		List<Setu> setuList = new SetuListPageParser().parse(urlDailySetuList, yyyyMMdd, false);
		int setuCount = setuList.size();
		logger.info(setuCount + "個の節一覧を取得しました。");
		if (setuList.size() <= 0) {
			logger.warn("節一覧の件数が0件です。");
			return;
		}

		OddsDownloader downloader = new OddsDownloader();
		
		// setu loop
		for (int i = 0; i < setuCount; i++) {
			Setu setu = setuList.get(i);
			String urlSetuRaceList = "";
			try {
				String jyoCd = JyoManager.getJyoCd(setu.jyo);
				urlSetuRaceList = BoatTemplate.URL_SETU_RACELIST.replace("{jyoCd}", jyoCd).replace("{yyyyMMdd}",
						yyyyMMdd);
				// レース一覧取得
				List<Race> raceList = new RaceListPageParser().parse(urlSetuRaceList);
				logger.info("節解析 (" + (i + 1) + "/" + setuCount + ") " + ":" + yyyyMMdd + ", " + setu.jyo + ", "
						+ setu.name);
				// race loop
				for (Race race : raceList) {
					race.raceInfo.year = year;
					race.raceInfo.month = month;
					race.raceInfo.day = day;
					race.raceInfo.ymd = yyyyMMdd;
					String raceNo = String.valueOf(race.raceInfo.no);
					// 節情報
					race.setu = setu;
					String url = "";
					try {
						// 20190526 結果オッズはもう不要なのでダウンロードしない.
						// オッズ情報 
						downloader.download(yyyyMMdd, jyoCd, raceNo);
						
						logger.info("オッズダウンロード:" + yyyyMMdd + ", " + setu.jyo + ", " + race.raceInfo.no + "R");

					} catch (Exception e) {
						logger.error("オッズダウンロード中予期せぬエラー。(" + (i + 1) + "/" + setuCount + ") " + ":" + yyyyMMdd + ", " + setu.jyo
								+ ", " + setu.name + ", url=" + url, e);
						continue;
					}
				}
			} catch (Exception e) {
				logger.error("節解析中予期せぬエラー。(" + (i + 1) + "/" + setuCount + ") " + ":" + yyyyMMdd + ", " + setu.jyo
						+ ", " + setu.name + ", url=" + urlSetuRaceList, e);
				continue;
			}
		}
		logger.info("ダウンロード完了 年月日：" + yyyyMMdd);
	}

	/** 範囲処理 */
	public void execute(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);
			execute(yyyyMMdd);
			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
	}

	/** 当日処理 */
	public void execute() throws Exception {
		String yyyyMMdd = sdf.format(new Date());
		execute(yyyyMMdd);
	}

	public static void main(String[] args) {
//		args = new String[] {
//		"20190831", 
//		"20190922"
//		};
		
		try {
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties");
			BoatOddsDownloader boatWebDownloader = new BoatOddsDownloader();
			if (args.length == 0) {
				// 当日
				boatWebDownloader.execute();
			} else if (args.length == 1) {
				// 日付指定
				boatWebDownloader.execute(args[0]);
			} else if (args.length == 2) {
				// 日付範囲指定
				boatWebDownloader.execute(args[0], args[1]);
			} else {
				System.out.println("Usage: BoatOddsDownloader [fromYmd] [toYmd]");
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

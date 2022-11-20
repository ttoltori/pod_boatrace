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
import com.pengkong.boatrace.scraping.parser.RaceBeforePageParser;
import com.pengkong.boatrace.scraping.parser.RaceListPageParser;
import com.pengkong.boatrace.scraping.parser.RaceProgramPageParser;
import com.pengkong.boatrace.scraping.parser.RaceResultPageParser;
import com.pengkong.boatrace.scraping.parser.RankCalculator;
import com.pengkong.boatrace.scraping.parser.SetuListPageParser;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.boatrace.service.manager.ResultOddsManager;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

/**
 * コンピュータ予想がある前のWEBをダウンロードする。
 *　①コンピュータページ→出走表ダウンロード、
 *　②オッズダウンロードしない
 * @author qwerty
 *
 */
public class BoatWebDownloaderBeforeComputerBetting {

	Logger logger = LoggerFactory.getLogger(BoatWebDownloaderBeforeComputerBetting.class);

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	PropertyUtil prop = PropertyUtil.getInstance();

	public void download(String yyyyMMdd) throws Exception {
		logger.info("ダウンロード開始  年月日：" + yyyyMMdd);
		int year = Integer.parseInt(yyyyMMdd.substring(0, 4));
		int month = Integer.parseInt(yyyyMMdd.substring(4, 6));
		int day = Integer.parseInt(yyyyMMdd.substring(6, 8));
		
		// 当該日のレースファイルが存在すれば削除する
		String filepathRace = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
		FileUtil.deleteFile(filepathRace);

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

		ResultOddsManager.getInstance().initialize();
		
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
						// オッズ情報
						//OddsManager.getInstance().loadLines(new OddsDownloader().download(yyyyMMdd, jyoCd, raceNo));
						
						// 枠情報取得
						url = BoatTemplate.URL_RACE_PROGRAM.replace("{raceNo}", raceNo).replace("{jyoCd}", jyoCd)
								.replace("{yyyyMMdd}", yyyyMMdd);
						race = new RaceProgramPageParser().parseAndSetInfo(race, url);

						// 直前情報取得
						url = BoatTemplate.URL_RACE_BEFOREINFO.replace("{raceNo}", raceNo).replace("{jyoCd}", jyoCd)
								.replace("{yyyyMMdd}", yyyyMMdd);
						race = new RaceBeforePageParser().parseAndSet(race, url);

						// 結果情報取得
						url = BoatTemplate.URL_RACE_RESULT.replace("{raceNo}", raceNo).replace("{jyoCd}", jyoCd)
								.replace("{yyyyMMdd}", yyyyMMdd);
						new RaceResultPageParser().parseAndSet(race.raceResult, race.wakuList, url);
						
						// 計算情報取得
						new RankCalculator().calculateAndSet(race);

						// ファイルに保存
						FileUtil.appendFileByLine(filepathRace, race.toCsv());

						logger.info("レース解析:" + yyyyMMdd + ", " + setu.jyo + ", " + race.raceInfo.no + "R");

					} catch (Exception e) {
						logger.error("レース解析中予期せぬエラー。(" + (i + 1) + "/" + setuCount + ") " + ":" + yyyyMMdd + ", " + setu.jyo
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
	public void download(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);
			download(yyyyMMdd);
			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
	}

	/** 当日処理 */
	public void download() throws Exception {
		String yyyyMMdd = sdf.format(new Date());
		download(yyyyMMdd);
	}

	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/rule3T.properties");
			BoatWebDownloaderBeforeComputerBetting boatWebDownloader = new BoatWebDownloaderBeforeComputerBetting();
			if (args.length == 0) {
				// 当日
				boatWebDownloader.download();
			} else if (args.length == 1) {
				// 日付指定
				boatWebDownloader.download(args[0]);
			} else if (args.length == 2) {
				// 日付範囲指定
				boatWebDownloader.download(args[0], args[1]);
			} else {
				System.out.println("Usage: BoatWebDownloader [fromYmd] [toYmd]");
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

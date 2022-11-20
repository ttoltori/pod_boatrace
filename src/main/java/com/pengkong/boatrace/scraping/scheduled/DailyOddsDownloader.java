package com.pengkong.boatrace.scraping.scheduled;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.Setu;
import com.pengkong.boatrace.scraping.parser.RaceListPageParser;
import com.pengkong.boatrace.scraping.parser.SetuListPageParser;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.common.PropertyUtil;

/**
 * TODO 途中中止、順延、レース欠場の扱い方を追加する.
 * 中止、順延:別途スレッドで監視する
 * レース欠場：参照時に除外する
 * 
 * @author ttolt
 *
 */
public class DailyOddsDownloader {
	Logger logger = LoggerFactory.getLogger(DailyOddsDownloader.class);

	/** ダウンローダmap key=yyyyMMdd + "_" + jyoCd + "_" + raceNo, value=JsonOddsDownloadScheduledRunner */
	Map<String, OddsDownloadSchedulerRunner> mapRunner = new HashMap<>();
	
	public DailyOddsDownloader() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void execute() {
		DateTime now = DateTime.now();
		String yyyyMMdd = now.toString("yyyyMMdd");
		int year = now.getYear();
		int month = now.getMonthOfYear();
		int day = now.getDayOfMonth();
		
		logger.info("started：ymd=" + yyyyMMdd);
		
		// 当該日のオッズファイルが存在すれば削除する。
		// 20210530 すでにダウンロードした内容も削除されないようにする
		// String filepathOdds = prop.getString("DIRECTORY_ODDS") + "odds_" + yyyyMMdd + ".csv";
		// FileUtil.deleteFile(filepathOdds);

		String urlDailySetuList = BoatTemplate.URL_DAILY_SETU_LIST.replace("{yyyyMMdd}", yyyyMMdd);
		// 節一覧取得
		List<Setu> setuList = null;
		try {
			setuList = new SetuListPageParser().parse(urlDailySetuList, yyyyMMdd, true);
		} catch (Exception e1) {
			logger.error("節一覧取得 failed.", e1);
			return;
		}

		int setuCount = setuList.size();
		logger.info(setuCount + "個の節一覧を取得しました。");
		if (setuList.size() <= 0) {
			logger.warn("節一覧の件数が0件です。");
			return;
		}
		ExecutorService pool = Executors.newCachedThreadPool();
		// setu loop
		for (int i = 0; i < setuCount; i++) {
			Setu setu = setuList.get(i);
			String urlSetuRaceList = "";
			String jyoCd = JyoManager.getJyoCd(setu.jyo);
			urlSetuRaceList = BoatTemplate.URL_SETU_RACELIST.replace("{jyoCd}", jyoCd).replace("{yyyyMMdd}",
					yyyyMMdd);
			// レース一覧取得
			List<Race> raceList = null;
			try {
				raceList = new RaceListPageParser().parse(urlSetuRaceList);
			} catch (Exception e) {
				logger.error("レース一覧取得 failed. jyocd=" + jyoCd, e);
				continue;
			}
			
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
				
				// 20190526 結果オッズはもう不要なのでダウンロードしない.
				// オッズ情報 
				String keyMap = yyyyMMdd + "_" + jyoCd + "_" + raceNo;
				OddsDownloadSchedulerRunner runner = new OddsDownloadSchedulerRunner(race);
				runner.setSchedule(race.raceInfo.hh, race.raceInfo.mm, 60, 90);
				mapRunner.put(keyMap, runner);
				pool.submit(runner);
				logger.info("オッズダウンロードスレッドstarted:" + yyyyMMdd + ", " + setu.jyo + ", " + race.raceInfo.no + "R");
			}
		}

		try {
			pool.shutdown();
			pool.awaitTermination(23, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			logger.error("awaitTermination failed!", e);
			pool.shutdownNow();
		}
		
		logger.info("end.");
	}
	
	public static void main(String[] args) {
		try {
//			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/odds_monitor.properties");
			PropertyUtil.getInstance().addFile(args[0]);
			new DailyOddsDownloader().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

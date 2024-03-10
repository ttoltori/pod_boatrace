package com.pengkong.boatrace.scraping.scheduled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.model.Odds;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.scraping.scheduled.callable.OddsParseRunner1TF;
import com.pengkong.boatrace.scraping.scheduled.callable.OddsParseRunner2TF;
import com.pengkong.boatrace.scraping.scheduled.callable.OddsParseRunner3F;
import com.pengkong.boatrace.scraping.scheduled.callable.OddsParseRunner3T;
import com.pengkong.boatrace.scraping.scheduled.callable.OddsParseRunnerKF;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

import lombok.Getter;
/**
 * レース毎のオッズを取得してファイルに保存する。 
 * 
 * プロパティ：DIRECTORY_ODDS
 * @author ttolt
 */
public class OddsDownloadSchedulerRunner extends AbstractScheduledRunner {

	@Getter
	private Race race;

	/** ダウンロード回数 */
	private int cntDownload = 1;

	private final Logger logger = LoggerFactory.getLogger(OddsDownloadSchedulerRunner.class);

	/** オッズが配列の勝式 */
	private static final List<BetType> listMultiOddsBettype = new ArrayList<>(Arrays.asList(BetType._1F, BetType._KF));
	
	public OddsDownloadSchedulerRunner(Race race) {
		this.race = race;
		initialize();
	}

	private void initialize() {
	}

	@Override
	public void execute() {
		String yyyyMMdd = race.raceInfo.ymd;
		String jyoCd = race.setu.jyoCd;
		String raceNo = String.valueOf(race.raceInfo.no);
		String description = yyyyMMdd + "," + jyoCd + "," + raceNo + "," + cntDownload;
		logger.debug("start. " + description);
		String urlTemplate = BoatTemplate.URL_ODDS_BASE.replace("{raceNo}", raceNo)
				.replace("{jyoCd}", jyoCd).replace("{yyyyMMdd}", yyyyMMdd);
		
		ExecutorService pool = Executors.newFixedThreadPool(5);

		List<Callable<OddsParseResult>> listTask = new ArrayList<>();

		String url;

		// 1TF
		url = urlTemplate.replace("{oddsType}", "tf");
		listTask.add(new OddsParseRunner1TF(url));

		// 2TF
		url = urlTemplate.replace("{oddsType}", "2tf");
		listTask.add(new OddsParseRunner2TF(url));

		// 3T
		url = urlTemplate.replace("{oddsType}", "3t");
		listTask.add(new OddsParseRunner3T(url));

		// 3F
		url = urlTemplate.replace("{oddsType}", "3f");
		listTask.add(new OddsParseRunner3F(url));

		// KF
		url = urlTemplate.replace("{oddsType}", "k");
		listTask.add(new OddsParseRunnerKF(url));

		List<Future<OddsParseResult>> listResult;
		try {
			listResult = pool.invokeAll(listTask, 1, TimeUnit.MINUTES);
			String csvOdds = createCsv(listResult);
			saveCsv(yyyyMMdd, csvOdds);
			logger.info("odds saved. " + description);
			// ダウンロードカウンタを増加
			cntDownload++;
		} catch (Exception e) {
			logger.error("(" + description + ") failed.", e);
		} finally {
			pool.shutdownNow();
		}
		
		logger.debug("end. " + description);
	}
	
	/**
	 * オッズをcsv形式で集約する
	 * 
	 * @param listResult 勝式別オッズ取得結果のリスト
	 * 
	 * @return csv文字列（複数行）
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private String createCsv(List<Future<OddsParseResult>> listResult) throws InterruptedException, ExecutionException {
		StringBuilder sb = new StringBuilder();
		String line = "";

		for (Future<OddsParseResult> future : listResult) {
			OddsParseResult result = future.get();
			for (Map.Entry<BetType, Object> entry : result.getData().entrySet()) {
				BetType betType = entry.getKey();
				// レース情報設定
				setPrefix(sb, betType);
				if (listMultiOddsBettype.contains(betType)) {
					line = Odds.toCsvFloats(result.getOddsArrayMap(betType));
				} else {
					line = Odds.toCsv(result.getOddsMap(betType));
				}
				// オッズリスト設定
				sb.append(line);
				sb.append(System.lineSeparator());
			}
		}
		return sb.substring(0, sb.length()-2);
	}

	/**
	 * オッズをファイルにappendする
	 * @param yyyyMMdd 日付
	 * @param csv オッズのcsv
	 * @throws Exception
	 */
	private synchronized void saveCsv(String yyyyMMdd, String csv) throws Exception {
		String filepath =PropertyUtil.getInstance().getString("DIRECTORY_ODDS") + "odds_" + yyyyMMdd + ".csv";
		FileUtil.appendFileByLine(filepath, csv);
	}

	/**
	 * バッファにレース情報を設定する
	 * @param sb バッファ
	 * @param betType 勝式
	 * 
	 * @result sb
	 */
	private void setPrefix(StringBuilder sb, BetType betType) {
		
		String yyyyMMdd = race.raceInfo.ymd;
		String jyoCd = race.setu.jyoCd;
		String raceNo = String.valueOf(race.raceInfo.no);
		
		sb.append(BoatUtil.currentHms());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(yyyyMMdd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(jyoCd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(raceNo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(betType.getValue());
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(cntDownload);
		sb.append(BoatTemplate.CSV_DELIMITER);
	}

	@Override
	protected void notifyScheduleComplete() {
		// nothing to do
	}
}

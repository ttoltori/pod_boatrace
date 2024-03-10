package com.pengkong.boatrace.online.odds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.exception.FatalException;
import com.pengkong.boatrace.factory.OddsParserFactory;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.scraping.scheduled.AbstractScheduledRunner;
import com.pengkong.boatrace.scraping.scheduled.OddsParseResult;

public class OddsParseScheduledRunner extends AbstractScheduledRunner {
	private final Logger logger = LoggerFactory.getLogger(OddsParseScheduledRunner.class);

	/** レース情報 */
	OlRace race;
	
	/** 監視結果リスナー */
	OddsParseRunnerListener listener;
	
	/** オッズparsing回数 */
	int parsedCount = 0;
	
	public OddsParseScheduledRunner(OlRace race, OddsParseRunnerListener listener) {
		super();
		this.race = race;
		this.listener = listener;
		this.parsedCount = 0;
	}

	@Override
	public void execute() throws FatalException {
		
		String yyyyMMdd = race.getYmd();
		String jyoCd = race.getJyocd();
		String raceNo = String.valueOf(race.getRaceno());
		
		String urlTemplate = BoatTemplate.URL_ODDS_BASE.replace("{raceNo}", raceNo)
				.replace("{jyoCd}", jyoCd).replace("{yyyyMMdd}", yyyyMMdd);
		
		ExecutorService pool = Executors.newFixedThreadPool(5);

		List<Callable<OddsParseResult>> listTask = new ArrayList<>();

		String url;

		// 1TF
		url = urlTemplate.replace("{oddsType}", "tf");
		listTask.add(OddsParserFactory.createParser1TF(url));

		// 2TF
		url = urlTemplate.replace("{oddsType}", "2tf");
		listTask.add(OddsParserFactory.createParser2TF(url));

		// 3T
		url = urlTemplate.replace("{oddsType}", "3t");
		listTask.add(OddsParserFactory.createParser3T(url));

		// 3F
		url = urlTemplate.replace("{oddsType}", "3f");
		listTask.add(OddsParserFactory.createParser3F(url));

		// KF 収集しない 20220418
		//url = urlTemplate.replace("{oddsType}", "k");
		//listTask.add(new OddsParseRunnerKF(url));

		List<Future<OddsParseResult>> listResult;
		try {
			listResult = pool.invokeAll(listTask, 1, TimeUnit.MINUTES);
			
			notifyResult(listResult);
		} catch (Exception e) {
			logger.error("Odds parsing failed. " + race, e);
			listener.notifyOddsParseRunnerFailure(race);
			isStop = true;
		} finally {
			pool.shutdownNow();
		}
	}
	
	void notifyResult(List<Future<OddsParseResult>> listResult) throws InterruptedException, ExecutionException {
		parsedCount++;
		List<OddsParseResult> result = new ArrayList<>();
		for (Future<OddsParseResult> future : listResult) {
			result.add(future.get());
		}
		
		logger.debug("odds parsed " + parsedCount + " times. " + race);
		listener.notifyOddsParseResult(race, result);
	}

	@Override
	protected void notifyScheduleComplete() {
		listener.notifyOddsParseRunnerComplete(race);
	}
}

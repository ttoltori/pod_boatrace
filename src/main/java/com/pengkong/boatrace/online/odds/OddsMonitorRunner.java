package com.pengkong.boatrace.online.odds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.BetType;
import com.pengkong.boatrace.exp10.odds.Odds;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.online.enums.RaceStatus;
import com.pengkong.boatrace.online.race.AbstractDailyRaceQueue;
import com.pengkong.boatrace.online.race.DailyTohyoRaceQueue;
import com.pengkong.boatrace.scraping.scheduled.OddsParseResult;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.MathUtil;

public class OddsMonitorRunner implements Runnable, OddsParseRunnerListener {
	Logger logger = LoggerFactory.getLogger(OddsMonitorRunner.class);
	
	MLPropertyUtil prop =  MLPropertyUtil.getInstance();
	
	/** 取得したリアルタイムオッズを返却するためのlistener */
	OddsMonitorListener listener;
	
	public OddsMonitorRunner(OddsMonitorListener listener) {
		super();
		this.listener = listener;
	}

	@Override
	public void run() {
		ExecutorService pool = Executors.newCachedThreadPool();
		
		int raceCheckIntervalSec = Integer.parseInt(prop.getString("odds_server_racecheck_interval"));
		int parseTermMinute = Integer.parseInt(prop.getString("odds_server_parse_term"));
		int parseIntervalSec = Integer.parseInt(prop.getString("odds_server_parse_interval"));

		// エラー発生フラグ
		boolean isError = false;
		
		try {
			// あらかじめレースキューをロードする
			AbstractDailyRaceQueue raceQueue = new DailyTohyoRaceQueue(BoatUtil.currentYmd());
			raceQueue.ensureInitialized();
			
			// レースキューが空きになるまでループ
			while(raceQueue.size() > 0) {
				
				// 複数レース同時締切を対応するためにqueueサイズ回数試す。(while(true)は不安）
				while (raceQueue.size() > 0) {
					OlRace race = raceQueue.peek();
					
					int simeHour = Integer.parseInt(race.getSime().substring(0, 2));
					int simeMin = Integer.parseInt(race.getSime().substring(2, 4));
					
					// 締切時間超過
					if (isOutOfTime(simeHour, simeMin, race.getStatus())) {
						race = raceQueue.poll();
						listener.notifyOutOfTime(race);
						continue;
					}
					
					// オッズ監視スレッド開始時刻内
					if (isIntimeStart(simeHour, simeMin, parseTermMinute)) {
						race = raceQueue.poll();
						
						OddsParseScheduledRunner scheduledRunner = new OddsParseScheduledRunner(race, this);
						scheduledRunner.setSchedule(simeHour, simeMin, parseTermMinute, parseIntervalSec);
						
						// parseメインスレッド開始
						pool.execute(scheduledRunner);
					} else {
						break;
					}
				}
				
				// キューチェック間隔を待機
				Thread.sleep(1000 * raceCheckIntervalSec);
			}
		} catch (Exception e) {
			logger.error("Probably race queue access failed.", e);
			isError = true;
		} finally {
			// thread pool 終了
			try {
				pool.shutdown();
				pool.awaitTermination(1, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				logger.error("awaitTermination failed!", e);
				pool.shutdownNow();
			}
		}
		
		if (isError) {
			listener.notifyMonitorFailure();
		} else {
			listener.notifyMonitorComplete();
		}
	}

	/** 投票締切時間を超過したか判定する */
	boolean isOutOfTime(int simeHour, int simeMin, int status) {
		// 現在基準時刻取得
		DateTime now = DateTime.now();
		DateTime raceSime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), simeHour, simeMin);
		
		return (now.isAfter(raceSime) && status == RaceStatus.BEFORE_SIME.getValue());
	}
	
	/**
	 * 現在時刻がレースに対するオッズ監視開始に妥当であるか判定する。
	 * @param simeHour レース締切hour
	 * @param simeMin レース締切min
	 * @param termMin レース締切より？分前からオッズ監視を開始する
	 * @return true=オッズ監視を開始する false=まだオッズ監視開始すべき時間になっていない
	 */
	boolean isIntimeStart(int simeHour, int simeMin, int termMin) {
		// 現在基準時刻取得
		DateTime now = DateTime.now();

		// 例）termMin=60 オッズ監視はレース締切60分前から開始する
		DateTime raceSime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), simeHour, simeMin);
		DateTime oddsMonitorStart = raceSime.minusMinutes(termMin);

		// レース投票時間確報のためにさらに5秒前から開始する 
		oddsMonitorStart = oddsMonitorStart.minusSeconds(5);

		// 現在時刻がオッズ監視開始時刻以降の場合true
		return now.isAfter(oddsMonitorStart);		
	}

	
	@Override
	public void notifyOddsParseResult(OlRace race, List<OddsParseResult> listResult) {
		List<OddsMonitorItem> items = new ArrayList<>();
		
		// OddsParseResultループ
		for (OddsParseResult result : listResult) {
			// bettype ループ(result当たり最大２勝式分のデータが格納されるため）
			for (Entry<BetType, Object> entryBettype : result.getData().entrySet()) {
				BetType betType = entryBettype.getKey();
				// 範囲オッズは除外（1F,KF）
				if (betType.equals(BetType._1F) || betType.equals(BetType._KF)) {
					continue;
				}

				Map<String, Float> mapOdds = result.getOddsMap(betType);
				for (Entry<String, Float> entryKumiban : mapOdds.entrySet()) {
					String kumiban = entryKumiban.getKey();
					Float oddsValue = entryKumiban.getValue();
					
					items.add(new OddsMonitorItem(race.getYmd(), race.getJyocd(), race.getRaceno().toString(),
							betType.getValue(), kumiban, MathUtil.scale2(oddsValue.doubleValue())));
				}
			}
		}
		
		listener.notifyItems(items);
	}

	@Override
	public void notifyOddsParseRunnerComplete(OlRace race) {
		// レース当たりオッズ監視終了を通知する
		listener.notifyRaceComplete(race);
	}

	@Override
	public void notifyOddsParseRunnerFailure(OlRace race) {
		// レース当たりのオッズ監視スレッド失敗。スレッド内で監視を終了するので特に何もしない。
	}
}

class OddsMonitorItem {
	public String ymd;
	public String jyoCd;
	public String raceNo;
	public String betType;
	public String kumiban;
	public Odds odds;
	
	public OddsMonitorItem(String ymd, String jyoCd, String raceNo, String betType, String kumiban, Double oddsValue) {
		super();
		this.ymd = ymd;
		this.jyoCd = jyoCd;
		this.raceNo = raceNo;
		this.betType = betType;
		this.kumiban = kumiban;
		odds = new Odds(ymd, jyoCd, raceNo, betType, kumiban, oddsValue);
	}
	
	public String createKumibanKey() {
		return String.join("_", ymd, jyoCd, raceNo, betType, kumiban);
	}

	public String createBettypeKey() {
		return String.join("_", ymd, jyoCd, raceNo, betType);
	}
}
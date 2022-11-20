package com.pengkong.boatrace.online.tohyo;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.online.api.AbstractApiProvider;
import com.pengkong.boatrace.online.enums.RaceStatus;
import com.pengkong.boatrace.online.race.AbstractDailyRaceQueue;
import com.pengkong.boatrace.online.race.DailyTohyoRaceQueue;
import com.pengkong.boatrace.util.BoatUtil;

public abstract class AbstractTohyoRunner implements Runnable {
	Logger logger = LoggerFactory.getLogger(AbstractTohyoRunner.class);

	MLPropertyUtil prop =  MLPropertyUtil.getInstance();
	
	/** API */
	protected AbstractApiProvider apiProvider;

	/** 投票クラス */
	protected TohyoExecutor tohyoExecutor;
	
	/** 投票結果通知リスナー */
	TohyoRunnerListener listener;
	
	/** 実行フラグ */
	boolean isRunning = true;
	
	public AbstractTohyoRunner(AbstractApiProvider apiProvider, TohyoRunnerListener listener) {
		this.apiProvider = apiProvider;
		this.listener = listener;

		// 内部変数設定
		setup();
	}

	/** スレッド終了 */
	public void stop() {
		isRunning = false;
	}
	
	public void run() {
		try {
			// あらかじめレースキューをロードする
			AbstractDailyRaceQueue raceQueue = new DailyTohyoRaceQueue(BoatUtil.currentYmd());
			raceQueue.ensureInitialized();
			
			int tohyoTermSec = Integer.parseInt(prop.getString("tohyo_term"));
			int tohyoInterval = Integer.parseInt(prop.getString("tohyo_interval"));
			
			OlRace nextRace = null;
			while(isRunning && raceQueue.size() > 0) {
				// 複数レース同時締切を対応するためにqueueサイズ回数試す。(while(true)は不安）
				while (raceQueue.size() > 0) {
					OlRace race = raceQueue.peek();
					
					int simeHour = Integer.parseInt(race.getSime().substring(0, 2));
					int simeMin = Integer.parseInt(race.getSime().substring(2, 4));
					
					// 締切時間超過
					if (isOutOfTime(simeHour, simeMin, race.getStatus())) {
						race = raceQueue.poll();
						listener.notifyTohyoOutOfTime(race);
						logger.warn("tohyo is out of time." + race);
						
						continue;
					}
					
					// 投票開始時刻以降
					if (isIntimeTohyo(simeHour, simeMin, tohyoTermSec)) {
						race = raceQueue.poll();
						try {
							// 投票実施
							int count = tohyoExecutor.execute(race);
							
							if (count > 0) {
								// 投票成功
								logger.info("tohyo is successful. " + race);
								listener.notifyTohyoSuccess(race);
							} else {
								// 投票スキップ
								logger.info("tohyo is skipped. " + race);
								listener.notifyTohyoSkipped(race);
							}
						} catch (Exception e) {
							// 投票エラー
							logger.error("tohyo failed. but TohyoRunner is still running. failed race: "
									+ race, e);
							listener.notifyTohyoFailure(race);
						}
					} else {
						if (nextRace != race) {
							nextRace = race;
							logger.info("next race is => " + nextRace);
						}
						break;
					}
				}

				// 投票キュー確認の間隔
				Thread.sleep(tohyoInterval * 1000);
			}
		} catch (InterruptedException e) {
			logger.warn("TohyoRunner interrupted end.");
		} catch (Exception e) {
			logger.error("TohyoRunner failed", e);
			listener.notifyTohyoRunnerFailure();
		}
		
		logger.info("Tohyo Runner completed. ");
	}
	
	/** 投票締切時間を超過したか判定する */
	boolean isOutOfTime(int simeHour, int simeMin, int status) {
		// 現在基準時刻取得
		DateTime now = DateTime.now();
		DateTime raceSime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), simeHour, simeMin);
		
		return (now.isAfter(raceSime) && status == RaceStatus.BEFORE_SIME.getValue());
	}
	
	/**
	 * レース締切時刻からtermSec前の範囲内かを判定する
	 * @param simeHour 締切時刻
	 * @param simeMin 締切時刻
	 * @param termSec レース締切から？秒前
	 * @return true=投票を実施する. false=まだ投票を実施しない
	 */
	boolean isIntimeTohyo(int simeHour, int simeMin, int termSec) {
		// 現在基準時刻取得
		DateTime now = DateTime.now();

		// 例）termMin=60 オッズ監視はレース締切60分前から開始する
		DateTime raceSime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), simeHour, simeMin);
		DateTime tohyoStart = raceSime.minusSeconds(termSec);

		// 現在時刻がオッズ監視開始時刻以降の場合true
		return now.isAfter(tohyoStart);				
		
	}

	/** 内部変数設定 */
	abstract protected void setup();
}

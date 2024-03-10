package com.pengkong.boatrace.online.tohyo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.mybatis.entity.OlResult;
import com.pengkong.boatrace.online.dao.OlResultDAO;
import com.pengkong.boatrace.online.race.AbstractDailyRaceQueue;
import com.pengkong.boatrace.online.race.DailyResultRaceQueue;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;

public class TohyoResultRunner implements Runnable {
	Logger logger = LoggerFactory.getLogger(TohyoResultRunner.class);

	MLPropertyUtil prop =  MLPropertyUtil.getInstance();
	
	/** 実行フラグ */
	boolean isRunning = true;
	
	/** 結果html parsing及びDB update */
	TohyoResultExecutor executor = new TohyoResultExecutor();
	
	/** 処理結果リスナ */
	TohyoResultRunnerListener listener;
	
	
	public TohyoResultRunner(TohyoResultRunnerListener listener) {
		super();
		this.listener = listener;
	}

	/** スレッド終了 */
	public void stop() {
		isRunning = false;
	}

	@Override
	public void run() {
		try {
			// あらかじめレースキューをロードする
			AbstractDailyRaceQueue raceQueue = new DailyResultRaceQueue(BoatUtil.currentYmd());
			raceQueue.ensureInitialized();
			
			int tohyoResultTermMin = Integer.parseInt(prop.getString("tohyo_result_term"));
			int tohyoResultInterval = Integer.parseInt(prop.getString("tohyo_result_interval"));
			
			while(isRunning && raceQueue.size() > 0) {
				OlRace race = raceQueue.peek();

				int simeHour = Integer.parseInt(race.getSime().substring(0, 2));
				int simeMin = Integer.parseInt(race.getSime().substring(2, 4));

				// 投票結果取得開始時刻内
				if (isIntimeTohyoResult(simeHour, simeMin, tohyoResultTermMin)) {
					race = raceQueue.poll();
					try {
						// 投票結果DB取得
						List<OlResult> results = loadOlResults(race);
						
						if (results.size() <= 0) {
							logger.info("race result for update does not exist. " + race);

							listener.notifyTohyoResultSkipped(race);
							continue;
						}
						
						// 投票結果htmlから結果反映処理
						int count = executor.execute(results);
						
						logger.info("tohyo results updated. " + count + "records. " + race);
						listener.notifyTohyoResultSuccess(race);
						
					} catch (Exception e) {
						// 投票エラー
						logger.error("tohyo result failed. but TohyoResultRunner is still running. " + race, e);
						listener.notifyTohyoResultFailure(race);
					}
				} 

				// 投票キュー確認の間隔
				Thread.sleep(tohyoResultInterval * 1000);
			}
		} catch (InterruptedException e) {
			logger.warn("TohyoResultRunner interrupted end.");
		} catch (Exception e) {
			logger.error("TohyoResultRunner failed", e);
			listener.notifyTohyoResultRunnerFailure();
		}
		
		logger.info("Tohyo Result Runner completed.");
	}

	/**
	 * レース締切時刻からtermMinが経ているかを判定する
	 * @param simeHour 締切時刻
	 * @param simeMin 締切時刻
	 * @param termSec レース締切から？分後
	 * @return true=投票結果を取得する. false=まだ投票結果を取得しない
	 */
	boolean isIntimeTohyoResult(int simeHour, int simeMin, int termMin) {
		// 現在基準時刻取得
		DateTime now = DateTime.now();

		// 例）termMin=60 オッズ監視はレース締切60分前から開始する
		DateTime raceSime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), simeHour, simeMin);
		DateTime tohyoResultStart = raceSime.plusMinutes(termMin);

		return now.isAfter(tohyoResultStart);
	}

	/** 当該レースのOlResultのリストを取得する 
	 * @throws SQLException */
	List<OlResult> loadOlResults(OlRace race) throws IOException, SQLException {
		List<OlResult> results;
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			OlResultDAO dao = new OlResultDAO(session);
			results = dao.select(race.getYmd(), race.getJyocd(), race.getRaceno());
			
			return results;
		} finally {
			DatabaseUtil.close(session);
		}
	}
}

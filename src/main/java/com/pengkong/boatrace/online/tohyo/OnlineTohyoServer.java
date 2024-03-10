package com.pengkong.boatrace.online.tohyo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.online.api.AbstractApiProvider;
import com.pengkong.boatrace.online.dao.OlRaceDAO;
import com.pengkong.boatrace.online.enums.RaceStatus;
import com.pengkong.boatrace.online.exception.ApiException;
import com.pengkong.boatrace.online.factory.OnlineFactory;
import com.pengkong.boatrace.online.tohyo.pc.PcTohyoRunner;
import com.pengkong.boatrace.online.tohyo.report.Reporter;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.PropertyUtil;

import lombok.Getter;

public class OnlineTohyoServer implements TohyoRunnerListener, TohyoResultRunnerListener {
	Logger logger = LoggerFactory.getLogger(OnlineTohyoServer.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** 投票実施スレッド */
	AbstractTohyoRunner tohyoRunner;

	/** 投票結果確認スレッド */
	TohyoResultRunner resultRunner;

	/** API */
	@Getter
	AbstractApiProvider apiProvider;

	/** スレッドpool */
	ExecutorService pool;

	/** 投票現況出力 */
	Reporter reporter;

	public OnlineTohyoServer() {
		apiProvider = OnlineFactory.createPcApiProvider();
		tohyoRunner = new PcTohyoRunner(apiProvider, this);
		resultRunner = new TohyoResultRunner(this);
		reporter = new Reporter();
	}

	public void start() {
		int ret = 0;
		try {
			// ログイン
			apiProvider.login();
			Thread.sleep(5000);

			// 必要時入金
			depositIfNeeded();

			pool = Executors.newFixedThreadPool(2);

			// 投票スレッド開始
			pool.execute(tohyoRunner);
			logger.info("tohyo runner started.");

			// TohyoRunnerでDBを更新する(締切超過レースの状態更新）可能性があるので3秒間結果確認スレッドの起動を遅延させる。
			Thread.sleep(3000);

			// 投票結果確認スレッド開始
			pool.execute(resultRunner);
			logger.info("tohyo result runner started.");

			// 現況出力
			reporter.execute();

			// 投票、結果確認スレッド共に終了待機
			pool.shutdown();
			pool.awaitTermination(20, TimeUnit.HOURS);

			// 精算
			apiProvider.payoff();
			Thread.sleep(3000);

			// ログアウト
			apiProvider.logout();
		} catch (ApiException e) {
			logger.error("online tohyo server api failure. ", e);
			stop(1); // api error
		} catch (InterruptedException e) {
			logger.error("online tohyo server interrupted failure", e);
			stop(2);
		} catch (Exception e) {
			logger.error("online tohyo server unknown failure. ", e);
			stop(3); // unknown error
		}

		logger.info("online tohyo server ended with return code:" + ret);
		// 終了
		stop(0);
	}

	/** 残高０なら入金する */
	public void depositIfNeeded() throws Exception {
		// 残高更新
		apiProvider.updateBalance();

		Thread.sleep(3000);

		int startBalance = prop.getInteger("start_balance");
		//if (apiProvider.getSession().getBalance().purchasableBetAmount < startBalance) {
		if (apiProvider.getSession().getBalance().purchasableBetAmount < 3000) {
			// 入金要求
			apiProvider.deposit(startBalance);

			// 入金処理待機
			Thread.sleep(prop.getInteger("wait_deposit") * 1000);
		} else {
			logger.info("deposit skip. balance:" + apiProvider.getSession().getBalance().purchasableBetAmount);
		}
	}

	/**
	 * 終了処理
	 * 
	 * @param ret 0=正常、1=Apiエラー、2=Interrupted、3=UnknownError、4=RMI要求、5=投票スレッド異常終了、6=結果スレッド異常終了
	 */
	public void stop(int ret) {
		if (!pool.isTerminated()) {
			pool.shutdownNow();
		}

		System.exit(ret);
	}

	/** ol_raceの状態更新 */
	private void updateRaceStatus(OlRace race, RaceStatus status) {
		race.setStatus(status.getValue());
		SqlSession session = null;
		try {
			session = DatabaseUtil.open(prop.getString("target_db_resource"), false);

			OlRaceDAO dao = new OlRaceDAO(session);
			dao.update(race);

			session.commit();
		} catch (Exception e) {
			logger.error("Race status update failed. ",
					String.join(",", race.getYmd(), race.getJyocd(), race.getRaceno().toString()));
		} finally {
			DatabaseUtil.close(session);
		}
	}

	/** 投票完了 */
	@Override
	public void notifyTohyoSuccess(OlRace race) {
		updateRaceStatus(race, RaceStatus.TOHYO_COMPLETED);
	}

	/** 投票スキップ */
	@Override
	public void notifyTohyoSkipped(OlRace race) {
		updateRaceStatus(race, RaceStatus.TOHYO_SKIPPED);
	}

	/** 投票時間超過 */
	@Override
	public void notifyTohyoOutOfTime(OlRace race) {
		updateRaceStatus(race, RaceStatus.TOHYO_TIMEOUT);
	}

	/** 投票失敗 */
	@Override
	public void notifyTohyoFailure(OlRace race) {
		updateRaceStatus(race, RaceStatus.TOHYO_ERROR);
	}

	/** 投票結果確認完了 */
	@Override
	public void notifyTohyoResultSuccess(OlRace race) {
		updateRaceStatus(race, RaceStatus.RESULT_COMPLETED);

		// 現況出力
		reporter.execute();
	}

	/** 投票結果確認失敗 */
	@Override
	public void notifyTohyoResultFailure(OlRace race) {
		updateRaceStatus(race, RaceStatus.RESULT_ERROR);
	}

	/** 投票結果確認スキップ */
	@Override
	public void notifyTohyoResultSkipped(OlRace race) {
		// updateRaceStatus(race, RaceStatus.RESULT_SKIPPED);
	}

	/** 投票スレッド異常終了 */
	@Override
	public void notifyTohyoRunnerFailure() {
		logger.error("Tohyo Runner failed.");
		stop(5);
	}

	/** 投票結果確認スレッド異常終了 */
	@Override
	public void notifyTohyoResultRunnerFailure() {
		logger.error("Tohyo Result Runner failed.");
		stop(6);
	}

	public static void main(String[] args) {
		String propertyFilepath = args[0];
		// PropertyUtilを使っている箇所もあるので互換性確保のために
		try {
			PropertyUtil.getInstance().addFile(propertyFilepath);
			MLPropertyUtil.getInstance().addFile(propertyFilepath);

			new OnlineTohyoServer().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

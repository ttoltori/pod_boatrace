package com.pengkong.boatrace.online.api.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.online.api.AbstractApiProvider;

public class HeartBeatRunner implements HeartBeatRunnerInterface {
	Logger logger = LoggerFactory.getLogger(HeartBeatRunner.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	/** セッション維持のために実行するAPI */
	AbstractApiProvider apiProvider;
	
	/** 間隔（分単位） */
	int intervalMinutes;
	
	/** aliveフラグ */
	boolean isRunning = true;
	
	public HeartBeatRunner(AbstractApiProvider apiProvider, int intervalMinutes) {
		super();
		this.apiProvider = apiProvider;
		this.intervalMinutes = intervalMinutes;
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				Thread.sleep(intervalMinutes * 60 * 1000);

				apiProvider.updateBalance();
			}
		} catch (Exception e) {
			logger.error("heartbeat failed!",e);
			try {
				// 再ログインを試す
				apiProvider.login();
			} catch (Exception e1) {
				logger.error("re-login failed!",e);
			}
		}
	}
	
	@Override
	public void stop() {
		isRunning = false;
	}
}

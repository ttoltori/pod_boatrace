package com.pengkong.boatrace.service.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.service.manager.TohyoManager;
import com.pengkong.boatrace.simulation.model.BettingRule;

import lombok.Setter;

public class HeartBeat implements Runnable{
	private Logger logger = LoggerFactory.getLogger(HeartBeat.class);

	@Setter
	private volatile boolean isRunning = true;
	
	private final Object lockObj = new Object();
	
	private BettingRule rule = null;

	public HeartBeat(BettingRule rule) {
		this.rule = rule;
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				heartbeat();
			}
		} catch (Exception e) {
			logger.error("heartbeat failed!",e);
		}
		logger.debug("heartbeat stopped.");
	}
	
	private void heartbeat() throws Exception {
		synchronized (lockObj) {
			try {
				int updateBalanceIntervalSec = rule.getInt("heartBeatInterval") * 60;
				lockObj.wait(updateBalanceIntervalSec * 1000);
			} catch (InterruptedException e) {
				logger.error("heartbeat thread interrupted!", e);
			}
			
			if (isRunning) {
				// 残高更新要求
				TohyoManager.getInstance().updateBalance();
			}
		}
	}
	
	public void stop() {
		synchronized (lockObj) {
			this.isRunning = false;
			lockObj.notify();
		}
	}
}

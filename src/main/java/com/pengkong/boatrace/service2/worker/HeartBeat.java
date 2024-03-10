package com.pengkong.boatrace.service2.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.service2.manager.TohyoManager;
import com.pengkong.common.PropertyUtil;

import lombok.Setter;

/**
 * property dependencies
 *   heartBeatInterval
 * 
 * @author ttolt
 *
 */
public class HeartBeat implements Runnable{
	private Logger logger = LoggerFactory.getLogger(HeartBeat.class);

	@Setter
	private volatile boolean isRunning = true;
	
	private final Object lockObj = new Object();
	
	PropertyUtil prop = PropertyUtil.getInstance();
	
	public HeartBeat() {
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
				int updateBalanceIntervalSec = prop.getInteger("heartBeatInterval") * 60;
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

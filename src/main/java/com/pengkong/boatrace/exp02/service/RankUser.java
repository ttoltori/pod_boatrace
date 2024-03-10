package com.pengkong.boatrace.exp02.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.service.manager.ResultOddsManager;
import com.pengkong.boatrace.service.manager.RacerManager;
import com.pengkong.boatrace.service.manager.TohyoManager;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.PropertyUtil;

import lombok.Getter;

public class RankUser {
	private Logger logger = LoggerFactory.getLogger(RankUser.class);

	@Getter
	private volatile int balance = 0;
	@Getter
	private volatile int dailyStartBalance = 0;
	
	@Getter
	private BettingRule rule = null;
	
	@Getter
	protected String todayYmd;
	
	private volatile boolean isRunning = true; // 실전용
	private List<RankWorker> workerList = new ArrayList<>();

	private TohyoManager tohyoManager = TohyoManager.getInstance();
	
	protected SqlSession session;
	protected boolean isSaveResultToDB = false;
	
	public synchronized void addBalance(int value) {
		this.balance += value;
	}
	
	public synchronized void subtractBalance(int value) {
		this.balance -= value;
	}
	
	public RankUser(String userPropertyFilepath) throws Exception {
		rule = new BettingRule(userPropertyFilepath);
	}
	
	public void start() {
		try {
			String[] workerPproperties = rule.getString("workerProperties").split(",");
			for (int i = 0; i < workerPproperties.length; i++) {
				String workerPropertyFilepath = PropertyUtil.getInstance().getString("DIRECTORY_PROPERTY") + workerPproperties[i].trim() + ".properties";
				BettingRule workerRule = new BettingRule(workerPropertyFilepath);
				RankWorker worker = new RankWorker(this, workerRule);
				workerList.add(worker);
			}

			while(isRunning) {
				todayYmd = BoatUtil.currentYmd();
				
				// サービス開始前(00:00～08:30)なら待つ
				String serviceStartHm = rule.getString("serviceStartTime");
				int servicestartHmInt = Integer.parseInt(serviceStartHm);
				String serviceEndHm = rule.getString("serviceEndTime");
				int serviceEndHmInt = Integer.parseInt(serviceEndHm);
				int currentHmInt = BoatUtil.currentHmInt();
				if (currentHmInt < servicestartHmInt) {
					long waitForTodayServiceStart = BoatUtil.getWaitForTodayServiceStart(serviceStartHm);
					logger.info("当日サービス開始待機中. " + todayYmd);
					Thread.sleep(waitForTodayServiceStart);
					todayYmd = BoatUtil.currentYmd();
				} else if (currentHmInt > serviceEndHmInt) {
					// 次の日のサービス開始時刻まで待つ
					long waitForNextDay = BoatUtil.getWaitForNextServiceStart(rule.getString("serviceStartTime"));
					logger.info("翌日サービス開始待機中. " + todayYmd);
					Thread.sleep(waitForNextDay);
					todayYmd = BoatUtil.currentYmd();
				}
				
				ResultOddsManager.getInstance().initialize();
				TohyoManager.getInstance().initialize(rule);
				
				tohyoManager.login();
				Thread.sleep(1000);
				
				balance = rule.getInt("totalStartBalance"); // tohyoManager.depositIfNeeded();  
				dailyStartBalance = balance;
				

				ExecutorService executorService = Executors.newFixedThreadPool(workerList.size());
				for (RankWorker worker : workerList) {
					executorService.execute(worker);
				}
				executorService.shutdown();
				try {
					executorService.awaitTermination(1, TimeUnit.DAYS);
				} catch (InterruptedException e) {
					logger.error("worker thread failed!", e);
					executorService.shutdownNow();
				}
				
				//当日全レース終了
				//精算
				//tohyoManager.payoff();
				//Thread.sleep(rule.getInt("waitAfterPayoff"));
				
				tohyoManager.logout();
				
				dailyStartBalance = 0;
				
				// 次の日のサービス開始時刻まで待つ
				long waitForNextDay = BoatUtil.getWaitForNextServiceStart(rule.getString("serviceStartTime"));
				logger.info("翌日サービス開始待機中. " + todayYmd);
				Thread.sleep(waitForNextDay);
				
			}
		} catch (Exception e) {
			logger.error("user failed!!! ", e);
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().addFile(args[0]);
			RankUser user = new RankUser(args[0]);
			user.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

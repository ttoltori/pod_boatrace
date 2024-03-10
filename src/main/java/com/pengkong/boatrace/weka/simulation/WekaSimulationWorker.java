package com.pengkong.boatrace.weka.simulation;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.model.BetRequest;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.service.worker.betting.Betting;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.boatrace.simulation.model.PlayStatus;
import com.pengkong.boatrace.util.BoatUtil;

public class WekaSimulationWorker extends WekaSimulationWorkerBase implements Runnable {
	private Logger logger = LoggerFactory.getLogger(WekaSimulationWorker.class);
	
	int bettingSortTime;
	Betting betting = null;
	
	public WekaSimulationWorker(WekaSimulationUser user, BettingRule rule) throws Exception{
		super(user, rule);
		initialize(rule);
	}
	
	private void initialize(BettingRule rule) throws Exception{
		this.rule = rule;
		Constructor<?> c = Class.forName(rule.getString("bettingClass")).getConstructor(BettingRule.class);
		betting = (Betting) c.newInstance(rule);
	}
	
	@Override
	public void run() {
		try {
			doDailyBet(user.getTodayYmd());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void doDailyBet(String ymd) throws Exception {
		todayYmd = ymd;
		bettingSortTime = 0;
		status = PlayStatus.STATUS_OPEN;
		
		BeforeOddsManager.getInstance().load(ymd); //simul
		
		// レース一覧作成
		loadRaces(ymd);

		if (raceList.size() <= 0) {
			return;
		}
		
		betCount = 0;
		hitCount = 0;
		
		// 締切時間でソート
		raceList.sort((RaceEx r1, RaceEx r2) -> r1.sortTime - r2.sortTime);

		if (jyoCount <= 0) {
			return;
		}
		
		int i = 0;
		// for (i = 0; i < raceList.size(); i++) {
		while (i < raceList.size()) {
			// AP中止
			if (!isRunning) {
				break;
			}
			
			if (status != PlayStatus.STATUS_OPEN) {
				break;
			}
			
			RaceEx race = raceList.get(i);
			i++;
			
			if (race.status == RaceEx.STATUS_CLOSED) {
				continue;
			} else if (race.status == RaceEx.STATUS_WAIT) {
				// 既存ベッティングあり
				if (race.sortTime <= bettingSortTime) {
					race.status = RaceEx.STATUS_CLOSED;
					continue;
				}
				
				try {
					// BETTING!!!
					race = doBet(race);
					
				} catch (Exception e) {
					logger.error("doBet failed!", e);
					race.skip = "エラー";
					race.status = RaceEx.STATUS_CLOSED;
					//writeRaceInformation(race);
					continue;
				}

				// ベッティングなし
				if (race.betlist.size() == 0) {
					//race.skip = "スキップ";
					race.status = RaceEx.STATUS_CLOSED;
					continue;
				}

				int betMoney = race.getBetMoney();
				// 残高確認
				if ((user.getBalance() - betMoney) <= 0) {
					logger.error("破産...");
					System.exit(-1);
				}

				// 残高更新
				user.subtractBalance(betMoney);
				totalBetAmount += betMoney;
				betCount += race.betlist.size();

				// 結果確認用再スケジューリング
				race = setSortTimeForResult(race, user.getRule().getInt("waitForResult"));
				
				race.status = RaceEx.STATUS_BETTING;
				raceList.sort((RaceEx r1, RaceEx r2) -> r1.sortTime - r2.sortTime);
				// 再スタート
				i = 0;

			} else if (race.status == RaceEx.STATUS_BETTING) {
				try {
					// 払戻取得
					race.prize = doResult(race);
				} catch (Exception e) {
					logger.error("doResult failed!", e);
					race.skip = "エラー";
					race.status = RaceEx.STATUS_CLOSED;
					logger.debug(race.getSimpleInfo() + "," + race.skip);
					//writeRaceInformation(race);
					continue;
				}
				// 残高更新
				if (race.prize > 0) {
					// 払戻あり
					hitCount++;
					totalPrizeAmount += race.prize;
					user.addBalance(race.prize);
					//logger.info(race.getSimpleInfo() + "," + "的中!" + race.prize + ", " +  "balance:" + user.getBalance());
				} else {
					//logger.info(race.getSimpleInfo() + "," + "はずれ!" + ", " +  "balance:" + user.getBalance());
				}

				race.status = RaceEx.STATUS_CLOSED;
				writeRaceInformationWeka(race);
				
				// 残高確認
				if (user.getBalance() < baseBetAmount) {
					logger.error("破産...");
					System.exit(-1);
				}
			}
		} 
		doDailyClose();
		
		writeUserStatus(ymd);
	}
	
	/** ベッティング */
	public RaceEx doBet(RaceEx race) throws Exception {
		race = betting.createAndSetBetList(race);
		if (race.betlist.size() <= 0) {
			return race;
		}
		
		// ベッティング要求
		BetRequest betReq = new BetRequest();
		// betReq.kachisiki = BoatUtil.typeToKachisiki(rule.getString("type"));
		betReq.jyoCd = race.setu.jyoCd;
		betReq.raceNo = String.format("%02d", race.raceInfo.no);
		betReq.betList = race.betlist;
		//tohyoManager.bet(betReq);
		//logger.info("投票要求:" + betReq.toString());
		
		return race;
	}

	/** レース結果払い戻し取得 */
	public int doResult(RaceEx race) throws Exception {
		int prize = 0;
		for (Bet bet : race.betlist) {
			if (getResultKumiban(race).equals(bet.kumiban)) { 
				prize += (int) ((float) bet.money * ((float) getResultPrize(race) / (float) 100));
				break;
			}
		}
		
		return prize;
	}
	
	public static void main(String[] args) {
		try {
			//BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/honmyo_5.properties");
			//BoatProperty.init(args[0]);
			//BettingRule rule = new BettingRule(args[0]);
			//Worker worker = new Worker(rule);
			//user.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

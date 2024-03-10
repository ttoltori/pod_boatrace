package com.pengkong.boatrace.service.worker;

import java.lang.reflect.Constructor;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.exception.NotYetResultException;
import com.pengkong.boatrace.model.BetRequest;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.model.json.OddsJson;
import com.pengkong.boatrace.scraping.parser.RaceBeforePageParser;
import com.pengkong.boatrace.scraping.parser.RaceResultPageParser;
import com.pengkong.boatrace.service.manager.ResultOddsManager;
import com.pengkong.boatrace.service.manager.StatManager;
import com.pengkong.boatrace.service.worker.betting.Betting;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.boatrace.simulation.model.PlayStatus;
import com.pengkong.boatrace.util.BoatUtil;

public class Worker extends WorkerBase implements Runnable {
	private Logger logger = LoggerFactory.getLogger(Worker.class);
	
	int bettingSortTime;
	Betting betting = null;
	
	public Worker(User user, BettingRule rule) throws Exception{
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
		
		String yesterdayYmd = BoatUtil.yesterdayYmd(ymd);
		StatManager.getInstance().loadDailyFile(yesterdayYmd);
		
		// レース一覧作成
		loadRaces(ymd);

		if (raceList.size() <= 0) {
			return;
		}
		
		betCount = 0;
		hitCount = 0;
		
		// 締切時間でソート
		raceList.sort((RaceEx r1, RaceEx r2) -> r1.sortTime - r2.sortTime);

		//全レース終了ならリターンする
		String lastRaceHms = String.format("%06d", raceList.get(raceList.size()-1).sortTime); 
		if (BoatUtil.isAfterHms(lastRaceHms)) {
			return;
		}
		
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
			
			// レース締切時間または結果確認時間を過ぎた
			if (BoatUtil.isPastHms(race) && race.status != RaceEx.STATUS_BETTING) {
				race.status = RaceEx.STATUS_CLOSED;
				continue;
			}
			
			if (race.status == RaceEx.STATUS_CLOSED) {
				continue;
			} else if (race.status == RaceEx.STATUS_WAIT) {
				try {
					if (isOnTimeBetting(race)) {
						// 当該レースのオッズをロードする
						List<OddsJson> oddsJsonList = tohyoManager.getOddsList(race, rule.getString("type")); 
						ResultOddsManager.getInstance().loadOddsJsonList(race, oddsJsonList, rule.getString("type"));
						
						// 直前情報取得
						String url = BoatTemplate.URL_RACE_BEFOREINFO.replace("{raceNo}", race.getRaceNo()).replace("{jyoCd}", race.getJyoCd())
								.replace("{yyyyMMdd}", race.raceInfo.ymd);
						race = new RaceBeforePageParser().parseAndSetEx(race, url);
						
						if (condition.isBeforeConditionOk(race)) {
							// BETTING!!!
							race = doBet(race);
						} else {
							race.status = RaceEx.STATUS_CLOSED;
							continue;
						}
					} else {
						i = 0;
						sleepForInterval();
						continue;
					}
					
				} catch (Exception e) {
					logger.error("doBet failed!", e);
					race.skip = "エラー";
					race.status = RaceEx.STATUS_CLOSED;
					writeRaceInformation(race);
					continue;
				}

				// ベッティングなし
				if (race.betlist.size() == 0) {
					//race.skip = "スキップ";
					race.status = RaceEx.STATUS_CLOSED;
					writeRaceInformation(race);
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
					if (isOnTimeResult(race)) {
						// 払戻取得
						race.prize = doResult(race);
					} else {
						i = 0;
						sleepForInterval();
						continue;
					}
				} catch (NotYetResultException e) {
					// また結果ページにデータがない
					setSortTimeForResult(race, user.getRule().getInt("nextWaitForResult"));
					race.status = RaceEx.STATUS_BETTING;
					raceList.sort((RaceEx r1, RaceEx r2) -> r1.sortTime - r2.sortTime);
					// 再スタート
					i = 0;
				} catch (Exception e) {
					logger.error("doResult failed!", e);
					race.skip = "エラー";
					race.status = RaceEx.STATUS_CLOSED;
					logger.debug(race.getSimpleInfo() + "," + race.skip);
					writeRaceInformation(race);
					continue;
				}
				// 残高更新
				if (race.prize > 0) {
					// 払戻あり
					hitCount++;
					totalPrizeAmount += race.prize;
					user.addBalance(race.prize);
					logger.debug(race.getSimpleInfo() + "," + "的中!" + race.prize + ", " +  "balance:" + user.getBalance());
				} else {
					logger.debug(race.getSimpleInfo() + "," + "はずれ!" + ", " +  "balance:" + user.getBalance());
				}

				race.status = RaceEx.STATUS_CLOSED;
				writeRaceInformation(race);
				
				// 残高確認
				if (user.getBalance() < baseBetAmount) {
					logger.error("破産...");
					System.exit(-1);
				}
			}
			
			sleepForInterval();
		} 
		Thread.sleep(5000);
		tohyoManager.updateBalance();
		
		Thread.sleep(5000);
		
		doDailyClose();
		
		writeUserStatus(ymd);
	}
	
	private void sleepForInterval() throws Exception {
		int interval = rule.getInt("raceIntervalSec");
		Thread.sleep(interval * 1000);		
	}
	
	/** ベッティング */
	public RaceEx doBet(RaceEx race) throws Exception {
		race = betting.createAndSetBetList(race);
		if (race.betlist.size() <= 0) {
			return race;
		}
		
		// ベッティング要求
		BetRequest betReq = new BetRequest();
		//JSJ 20200202 betReq.kachisiki = BoatUtil.typeToKachisiki(rule.getString("type"));
		betReq.jyoCd = race.setu.jyoCd;
		betReq.raceNo = String.format("%02d", race.raceInfo.no);
		betReq.betList = race.betlist;
		//tohyoManager.bet(betReq);
		logger.info("投票要求:" + betReq.toString());
		
		return race;
	}

	/** レース結果払い戻し取得 */
	public int doResult(RaceEx race) throws Exception {
		// 結果ページ取得
		race = new RaceResultPageParser().parseAndSetResult(race);
		
		int prize = 0;
		for (Bet bet : race.betlist) {
			if (getResultKumiban(race).equals(bet.kumiban)) { 
				prize += (int) ((float) bet.money * ((float) getResultPrize(race) / (float) 100));
				break;
			}
		}
		
		return prize;
	}
	
	private boolean isOnTimeBetting(RaceEx race) throws Exception {
		int fromSeconds = user.getRule().getInt("beforeFromSeconds"); 
		int toSeconds = user.getRule().getInt("beforeToSeconds");
		if (BoatUtil.isBeforeInRangeSeconds(String.format("%06d", race.sortTime), 60, toSeconds)) {
			logger.debug(rule.getString("type") + " 次レース:" + RaceEx.getJyoRaceTime(race) + " 現在時刻:" + BoatUtil.currentHms());
		}
		
		return BoatUtil.isBeforeInRangeSeconds(String.format("%06d", race.sortTime), fromSeconds, toSeconds);
	}
	
	private boolean isOnTimeResult(RaceEx race) throws Exception {
		if (BoatUtil.isAfterHms(String.format("%06d", race.sortTime - 100))) {
			logger.debug(rule.getString("type") + " 次結果:" + RaceEx.getJyoRaceTime(race) + " 現在時刻:" + BoatUtil.currentHms());
		}
		return BoatUtil.isAfterHms(String.format("%06d", race.sortTime));
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

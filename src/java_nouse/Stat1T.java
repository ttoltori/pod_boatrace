package com.pengkong.boatrace.simulation2;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatProperty;
import com.pengkong.boatrace.model.OddsTItem;
import com.pengkong.boatrace.model.PopularItem;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.service.manager.OddsManager;
import com.pengkong.boatrace.service.manager.PowerManagerAvgtime;
import com.pengkong.boatrace.service.manager.PowerManagerJyoMotor;
import com.pengkong.boatrace.service.manager.PowerManagerRacer;
import com.pengkong.boatrace.service.manager.StatManager;
import com.pengkong.boatrace.service.worker.betting.Betting;
import com.pengkong.boatrace.simulation.model.Bet;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.boatrace.simulation.model.PlayStatus;
import com.pengkong.boatrace.util.BoatUtil;

public class Stat1T extends SimulationWorkerBase {
	private Logger logger = LoggerFactory.getLogger(Stat1T.class);
	
	int bettingSortTime;
	MartinKumiban martinCalculator = null;
	DistributeAmount distamo = null; 
	Betting betting = null;

	public Stat1T(String fromYmd, String toYmd, BettingRule rule) throws Exception{
		super();
		this.fromYmd = fromYmd;
		this.toYmd = toYmd;
		this.rule = rule;
		martinCalculator = new MartinKumiban(rule);
		distamo = new DistributeAmount(rule);
		Constructor<?> c = Class.forName(rule.getString("bettingClass")).getConstructor(BettingRule.class);
		betting = (Betting) c.newInstance(rule);
	}
	
	@Override
	public int getMartinCount() {
		return martinCalculator.getMartinCount();
	}

	@Override
	public int getMartinBetAmoutAll() {
		return martinCalculator.getBetAmountTotal();
	}
	
	@Override
	public void doDailyBet(String ymd) throws Exception {
		bettingSortTime = 0;
		martinDailySuccessCount = 0;
		martinDailyFailCount = 0;
		status = PlayStatus.STATUS_OPEN;
		
		OddsManager.getInstance().load(ymd);
		String yesterdayYmd = BoatUtil.yesterdayYmd(ymd);
		
		PowerManagerRacer.getInstance().loadDailyFile(yesterdayYmd);
		PowerManagerJyoMotor.getInstance().loadDailyFile(yesterdayYmd);
		PowerManagerAvgtime.getInstance().loadDailyFile(yesterdayYmd);
		StatManager.getInstance().loadDailyFile(yesterdayYmd);
		
		loadRaces(ymd);

		if (jyoCount <= 0) {
			return;
		}
		betCount = 0;
		hitCount = 0;
		
		// 締切時間でソート
		raceList.sort((RaceEx r1, RaceEx r2) -> r1.sortTime - r2.sortTime);
		int i = 0;
		// for (i = 0; i < raceList.size(); i++) {
		while (i < raceList.size()) {
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
					writeRaceInformation(race);
					continue;
				}

				if (race.multiplyType == RaceEx.MULTIPLY_RECOVERY) {
					martinCalculator.clear();
					isMartin = false;
					status = PlayStatus.STATUS_MARTIN_RESET_RESTRICT;
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
				if ((balance - betMoney) <= 0) {
					logger.error("破産...");
					System.exit(-1);
				}

				// 残高更新
				balance -= betMoney;
				totalBetAmount += betMoney;
				betCount += race.betlist.size();

				// 結果確認用再スケジューリング
				race = setSortTime(race);
				//bettingSortTime = race.sortTime;
				
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
					writeRaceInformation(race);
					continue;
				}
				// 残高更新
				if (race.prize > 0) {
					// 払戻あり
					hitCount++;
					totalPrizeAmount += race.prize;
					balance += race.prize;

					// マーティン法カウンタ解除
					if (race.multiplyType == RaceEx.MULTIPLY_MARTIN) {
						martinDailySuccessCount++;
						martinCalculator.clear();
						isMartin = false;
						
						if (martinDailySuccessCount > rule.getInt("martinSuccessRepeatLimit")) {
							// マーティ法回数制限による直中止判定
							status = PlayStatus.STATUS_MARTIN_RESET_RESTRICT;
						} else {
							// nothing to do
						}
					}
				} else {
					if (race.multiplyType == RaceEx.MULTIPLY_MARTIN) {
						martinDailyFailCount++;
						if (martinCalculator.getMartinCount() >= rule.getInt("martinCountLimit")) {
							// マーティ法最大金額超過
							martinCalculator.clear();
							isMartin = false;
						}
					} else {
						martinCalculator.setBetAmountTotal(martinCalculator.getBetAmountTotal() + race.getBetMoney());
						isMartin = true;
					}
				}

				race.status = RaceEx.STATUS_CLOSED;
				writeRaceInformation(race);
				
				// 残高確認
				if (balance < baseBetAmount) {
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
		if (isMartin && rule.getInt("martinCountLimit") > 0) {
			race = createAndSetBetListMartin(race);

			if (race.multiplyType == RaceEx.MULTIPLY_UNDER_MINIMUM) {
				race = betting.createAndSetBetList(race);
			}
		} else {
			race = betting.createAndSetBetList(race);
		}
		return race;
	}

	protected RaceEx createAndSetBetListMartin(RaceEx race) throws Exception{
		String[] kumibanList = rule.getString("kumibanMartinList").split(",");
		float virtualOdds = getVirtualOdds(race, kumibanList);
		if (virtualOdds < rule.getFloat("distamoMinimumVirtualOddsMartin")) {
			race.skip = "マーティン仮想オッズ不足:" + virtualOdds;
			return race;
		}
		
		return martinCalculator.createAndSetBetList(race);
	}
	
	/** レース結果払い戻し取得 */
	public int doResult(RaceEx race) throws Exception {
		int prize = 0;
		for (Bet bet : race.betlist) {
			if (getResultKumiban(race).equals(bet.kumiban)) { 
				prize = (int) ((float) bet.money * ((float) getResultPrize(race) / (float) 100));
				break;
			}
		}
		
		return prize;
	}
	
	protected RaceEx setPopularLevel(RaceEx race) throws Exception {
		String[] raceExcludeList = rule.getString("raceExcludeList").split(",");
		String strRaceNo = String.valueOf(race.raceInfo.no);
		for (int i=0; i < raceExcludeList.length; i++) {
			if (strRaceNo.equals(raceExcludeList[i])) {
				return race;
			}
		}
		
		if (
				ConditionsHonmyo.isHonmyo(race, rule.getString("type"))
			) {
			race.popularItemList.add(new PopularItem(null, 0, 0));
		}
		
		return race;
	}
	
	protected float getVirtualOdds(RaceEx race, String[] kumibanList) throws Exception {
		List<OddsTItem> listOdds = new ArrayList<>();
		for (int i = 0; i < kumibanList.length; i++) {
			OddsTItem oddsItem = OddsManager.getInstance().getOddsItem(race.getJyoCd(), race.getRaceNo(), rule.getString("type"), kumibanList[i]);
			if (oddsItem != null) {
				listOdds.add(oddsItem);
			}
		}
		
		return distamo.getVirtualOdds(listOdds);
	}
	
	public static void main(String[] args) {
		try {
			// User user = new User(args[0], args[1]);
			BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/stat_1T.properties");
			BettingRule rule = new BettingRule(BoatProperty.DIRECTORY_PROPERTY + "stat_1T.properties");
			Stat1T user = new Stat1T("20170505", "20170512", rule);
//			Honmyo1T_2 user = new Honmyo1T_2("20180702", "20180702", rule);
			user.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

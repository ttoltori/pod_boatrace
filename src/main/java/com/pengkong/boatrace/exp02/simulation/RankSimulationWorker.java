package com.pengkong.boatrace.exp02.simulation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp02.betting.RankBetting;
import com.pengkong.boatrace.exp02.model.RankRace;
import com.pengkong.boatrace.model.BetRequest;
import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.model.betting.RankBet;
import com.pengkong.boatrace.model.betting.RankBetConverter;
import com.pengkong.boatrace.model.betting.TohyoBet;
import com.pengkong.boatrace.service.manager.BeforeOddsManager;
import com.pengkong.boatrace.service.manager.ResultOddsManager;
import com.pengkong.boatrace.simulation.model.PlayStatus;
import com.pengkong.common.PropertyUtil;

public class RankSimulationWorker extends RankSimulationWorkerBase implements Runnable {
	private Logger logger = LoggerFactory.getLogger(RankSimulationWorker.class);
	
	int bettingSortTime;
	RankBetting betting = null;
	
	PropertyUtil prop = PropertyUtil.getInstance();

	RankBetConverter converter = new RankBetConverter();
	public RankSimulationWorker(RankSimulationUser user) throws Exception{
		super(user);
		initialize();
	}
	
	private void initialize() throws Exception{
		Constructor<?> c = Class.forName(prop.getString("bettingClass")).getConstructor();
		betting = (RankBetting) c.newInstance();
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
		
		BeforeOddsManager.getInstance().load(ymd); //직전옺즈
		ResultOddsManager.getInstance().load(ymd); // 결과옺즈 또는 실전옺즈
		
		// レース一覧作成
		loadRaces(ymd);

		if (raceList.size() <= 0) {
			return;
		}
		
		betCount = 0;
		hitCount = 0;
		
		// 締切時間でソート
		raceList.sort((RankRace r1, RankRace r2) -> r1.sortTime - r2.sortTime);

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
			
			RankRace race = raceList.get(i);
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
				if (race.betList.size() == 0) {
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
				betCount += race.betList.size();

				// 結果確認用再スケジューリング
				race = setSortTimeForResult(race, prop.getInteger("waitForResult"));
				
				race.status = RaceEx.STATUS_BETTING;
				raceList.sort((RankRace r1, RankRace r2) -> r1.sortTime - r2.sortTime);
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
					//writeRaceInformation(race);
					continue;
				}
				// 残高更新
				if (race.prize > 0) {
					// 払戻あり
					totalPrizeAmount += race.prize;
					user.addBalance(race.prize);
				}
				
				//logger.info(race.getSimpleInfo() + "," + "bet:" + race.getBetMoney() + ", " + "income:" + race.prize + ", balance:" + user.getBalance());
				race.status = RaceEx.STATUS_CLOSED;
				
				// 레이스 결과 저장
				insertRankResult(race);
				
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
	
	/** 베팅처리 */
	public RankRace doBet(RankRace race) throws Exception {
		List<RankBet> listRankBet = betting.createRankBetList(race);
		if (listRankBet.size() <= 0) {
			return race;
		}
		race.betList = convertRankBetList2TohyoBetList(listRankBet, race);
		
		// ベッティング要求
		BetRequest betReq = new BetRequest();
		// betReq.kachisiki = BoatUtil.typeToKachisiki(race.getType());
		betReq.jyoCd = race.getJyocd();
		betReq.raceNo = String.format("%02d", race.getRaceNo());
		betReq.betList = race.betList;
		// tohyoManager.bet(betReq);
		// logger.info("投票要求:" + betReq.toString());
		
		return race;
	}

	/**
	 * RankBet리스트를 TohyoBet리스트로 변환하여 반환한다.
	 * (TohyoBet은 근거가 된 복수개의 RankBet을 포함한다.)
	 * 
	 * @param rankBetList
	 * @return
	 */
	private List<Bet> convertRankBetList2TohyoBetList(List<RankBet> rankBetList, RankRace race) {
		List<Bet> result = new ArrayList<Bet>();
		HashMap<String, TohyoBet> mapBet = new HashMap<>();
		for (RankBet rankBet : rankBetList) {
			List<TohyoBet> listBet = converter.convert(rankBet);
			for (TohyoBet bet : listBet) {
				TohyoBet tohyoBet = mapBet.get(bet.getKey());
				if (tohyoBet == null) {
					tohyoBet = new TohyoBet(bet.type, bet.kumiban, 0, rankBet);
					// 투표직전옺즈 
					OddsItemEx odds = BeforeOddsManager.getInstance().getOddsItemEx(race.getJyocd(), String.valueOf(race.getRaceNo()), bet.type, bet.kumiban);
					if (odds != null) {
						tohyoBet.odds = odds.value;
						tohyoBet.oddsRank = odds.rank;
					}
					mapBet.put(tohyoBet.getKey(), tohyoBet);
				} else {
					tohyoBet.rankbetList.add(rankBet);
				}
				// 베팅금액결정
				// 1. bettype별로 일괄금액 설정
				//tohyoBet.money = prop.getInteger("betMoney_" + tohyoBet.type);
				// 2. rankType별의 랭킹베팅중에 가장 큰 금액을 베팅
				//if (tohyoBet.money < bet.money) {
				//	tohyoBet.money = bet.money;
				//}
				// 3. 중복되는 패턴에 대해서 일괄
				// tohyoBet.money = 100;
				// 4. 투표용베팅에 패턴추출한랭킹베팅의 베팅금액을 합산한다.
				tohyoBet.money += prop.getInteger("betMoney_" + tohyoBet.type);
			}
		}
		result.addAll(mapBet.values());
		
		return result; 
	}
	
	/** レース結果払い戻し取得 */
	public int doResult(RankRace race) throws Exception {
		int prize = 0;
		for (Bet bet : race.betList) {
			if (RankRace.getResultKumiban(race, bet.type).equals(bet.kumiban)) {
				TohyoBet tohyoBet = (TohyoBet)bet;
				if (tohyoBet.rankbetList.size() > 1) {
					System.out.println("");
				}
				hitCount++;
				prize += (int) ((float) tohyoBet.money * ((float) RankRace.getPrize(race, tohyoBet.type) / (float) 100));
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

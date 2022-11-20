package com.pengkong.boatrace.exp02.simulation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.common.DBCol;
import com.pengkong.boatrace.exception.RaceSkipException;
import com.pengkong.boatrace.exp02.model.RankRace;
import com.pengkong.boatrace.model.OddsItemEx;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.model.betting.RankBet;
import com.pengkong.boatrace.model.betting.TohyoBet;
import com.pengkong.boatrace.mybatis.client.RunRankResultMapper;
import com.pengkong.boatrace.mybatis.client.RunRankResultPtnMapper;
import com.pengkong.boatrace.mybatis.entity.RunRankResult;
import com.pengkong.boatrace.mybatis.entity.RunRankResultPtn;
import com.pengkong.boatrace.service.manager.ResultOddsManager;
import com.pengkong.boatrace.service.manager.TohyoManager;
import com.pengkong.boatrace.simulation.model.PlayStatus;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.boatrace.util.DatabaseUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public abstract class RankSimulationWorkerBase {
	private Logger logger = LoggerFactory.getLogger(RankSimulationWorkerBase.class);

	//protected List<RaceEx> raceList = new ArrayList<>();
	protected List<RankRace> raceList = new ArrayList<>();

	protected TohyoManager tohyoManager = TohyoManager.getInstance();
	
	/** ベッティング単位金額 */
	protected int baseBetAmount = 1000;
	
	/** 総ベッティング金額 */
	protected int totalBetAmount = 0;
	
	/** 総払い戻し金額 */
	public int totalPrizeAmount = 0;

	/** 的中数 */
	protected int hitCount = 0;

	protected int betCount = 0;

	int status = PlayStatus.STATUS_OPEN;
	
	protected String fromYmd;
	protected String toYmd;
	
	protected volatile boolean isRunning = true;
	protected String todayYmd;
	
	protected RankSimulationUser user;

	PropertyUtil prop = PropertyUtil.getInstance();
	
	public RankSimulationWorkerBase(RankSimulationUser user) throws Exception {
		super();
		this.user = user;
	}

	public abstract void doDailyBet(String yyyyMMdd) throws Exception;

	public void stop() {
		isRunning = false;
	}
	
	protected void loadRaces(String ymd) throws Exception {
		if ((raceList = user.getRaces(ymd)) == null) {
			raceList = new ArrayList<>();
		}
	}
	
	/** レース結果チェックのためにレースのソート基準時間を１５分後に設定する */
	protected RankRace setSortTimeForResult(RankRace race, int waitMinutes) throws RaceSkipException {
		try {
			String ymdHmS = race.getYmd() + String.format("%04d00", race.getSime());
			String nextYmdHmS = BoatUtil.getYhdHmsAfterMinutes(ymdHmS, waitMinutes);
			race.sortTime = Integer.parseInt(nextYmdHmS.substring(8, 14));
		} catch (Exception e) {
			throw new RaceSkipException("レースソート時間設定失敗.");
		}

		return race;
	}
	
	protected void writeUserStatus(String ymd) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(BoatUtil.currentYyyyMMddHHmmSS());
		sb.append(",");
		sb.append(ymd);
		sb.append(",");
		sb.append(betCount);
		sb.append(",");
		sb.append(user.getBalance() - user.getDailyStartBalance());
		sb.append(",");
		sb.append(user.getBalance());
		String line = sb.toString();

		String filepath = PropertyUtil.getInstance().getString("DIRECTORY_RESULT")  + "user_" + todayYmd + "_" + prop.getString("rankType") + ".csv";
		FileUtil.appendFileByLine(filepath, line);

		logger.info("総括," + sb.toString());
	}

	
	
	protected void insertRankResult(RankRace race) throws Exception {
		if (race.betList.size() <= 0) {
			return;
		}
		
		List<RunRankResult> listResult = new ArrayList<>();
		List<RunRankResultPtn> listResultPtn = new ArrayList<>();
		for (Bet tohyobet : race.betList) {
			TohyoBet bet = (TohyoBet)tohyobet;
			RunRankResult rankResult = new RunRankResult();
			rankResult.setYmd(race.getYmd());
			rankResult.setJyocd(race.getJyocd());
			rankResult.setRaceno((short)race.getRaceNo());
			rankResult.setSime(race.recRace.getString(DBCol.SIME));
			rankResult.setCondition(user.condition);
			rankResult.setBettype(bet.type);
			rankResult.setPredictRank123(race.recRace.getString(DBCol.PREDICT_RANK123));
			rankResult.setResultRank123(race.recRace.getString(DBCol.RESULT_RANK123));
			rankResult.setBetKumiban(bet.kumiban);
			rankResult.setBetOdds(BigDecimal.valueOf(bet.odds));
			rankResult.setBetOddsrank(bet.oddsRank);
			rankResult.setResultKumiban(race.recRace.getString(DBCol.RESULT_RANK123));
			// 투표결과 옺즈 
			OddsItemEx odds = ResultOddsManager.getInstance().getOddsItemEx(race.getJyocd(), String.valueOf(race.getRaceNo()), bet.type, bet.kumiban);
			if (odds != null) {
				rankResult.setResultOdds(BigDecimal.valueOf(odds.value));
				rankResult.setResultOddsrank(odds.rank);
			}
			
			rankResult.setBetamt(bet.money);
			if (bet.kumiban.equals(RankRace.getResultKumiban(race, bet.type))) {
				rankResult.setHity(1);
				rankResult.setHitn(0);
				//rankResult.setHitamt(RankRace.getPrize(race, bet.type));
				rankResult.setHitamt(race.prize);
			} else {
				rankResult.setHity(0);
				rankResult.setHitn(1);
				rankResult.setHitamt(0);
			}
			rankResult.setResultAmt(rankResult.getHitamt() - rankResult.getBetamt());
			// 해당 베팅에 포함된 랭크베팅패턴의 수
			int[] ptnCnt = bet.getPatternCnt();
			rankResult.setNormPtnCnt(ptnCnt[0]);
			rankResult.setFormPtnCnt(ptnCnt[1]);
			
			listResult.add(rankResult);
			for (RankBet rankBet : bet.rankbetList) {
				RunRankResultPtn ptn = new RunRankResultPtn();
				ptn.setBetKumiban(rankBet.betKumiban);
				ptn.setBettype(rankBet.betType);
				ptn.setCondition(user.condition);
				ptn.setExtkey(rankBet.recFilter.getString(DBCol.EXTKEY));
				ptn.setJyocd(rankResult.getJyocd());
				ptn.setRaceno(rankResult.getRaceno());
				ptn.setSime(rankResult.getSime());
				ptn.setYmd(rankResult.getYmd());
				
				listResultPtn.add(ptn);
			}
		}
		
		saveRankResult2DB(listResult, listResultPtn);
	}

	private void saveRankResult2DB(List<RunRankResult> listResult, List<RunRankResultPtn> listResultPtn) {
		RunRankResultMapper runRankResultMapper = DatabaseUtil.getSession().getMapper(RunRankResultMapper.class);
		RunRankResultPtnMapper runRankResultPtnMapper = DatabaseUtil.getSession().getMapper(RunRankResultPtnMapper.class);
		for (RunRankResult record : listResult) {
			runRankResultMapper.insert(record);
		}
		for (RunRankResultPtn record : listResultPtn) {
			runRankResultPtnMapper.insert(record);
		}
	}

	protected void doDailyClose() {
	}
}

package com.pengkong.boatrace.weka.simulation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.converter.Csv2RaceEx;
import com.pengkong.boatrace.converter.StatMlResultWeka2Csv;
import com.pengkong.boatrace.converter.StatMlResultWekaPtn2Csv;
import com.pengkong.boatrace.exception.RaceSkipException;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.ResultInfoSimple;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.mybatis.client.StatMlResultWekaMapper;
import com.pengkong.boatrace.mybatis.client.StatMlResultWekaptnMapper;
import com.pengkong.boatrace.mybatis.entity.StatMlResultWeka;
import com.pengkong.boatrace.mybatis.entity.StatMlResultWekaptn;
import com.pengkong.boatrace.service.manager.TohyoManager;
import com.pengkong.boatrace.service.manager.WekaPattern;
import com.pengkong.boatrace.service.worker.condition.Condition;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.boatrace.simulation.model.PlayStatus;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public abstract class WekaSimulationWorkerBase {
	private Logger logger = LoggerFactory.getLogger(WekaSimulationWorkerBase.class);

	protected List<RaceEx> raceList = new ArrayList<>();

	protected BettingRule rule;

	protected TohyoManager tohyoManager = TohyoManager.getInstance();
	
	/** ベッティング単位金額 */
	protected int baseBetAmount = 1000;
	
	/** 総ベッティング金額 */
	protected int totalBetAmount = 0;
	
	/** 総払い戻し金額 */
	public int totalPrizeAmount = 0;

	/** 的中数 */
	protected int hitCount = 0;

	/** 場数 */
	protected int jyoCount = 0;
	
	protected int betCount = 0;

	Condition condition;

	int status = PlayStatus.STATUS_OPEN;
	
	protected String fromYmd;
	protected String toYmd;
	
	protected volatile boolean isRunning = true;
	protected String todayYmd;
	
	protected WekaSimulationUser user;
	PropertyUtil prop = PropertyUtil.getInstance();
	public WekaSimulationWorkerBase(WekaSimulationUser user, BettingRule rule) throws Exception {
		super();
		this.user = user;
		this.rule = rule;
		
		Class<?> c = Class.forName(rule.getString("conditionClass"));
		condition = (Condition)c.newInstance();
	}

	public abstract void doDailyBet(String yyyyMMdd) throws Exception;

	public void stop() {
		isRunning = false;
	}
	
	protected void loadRaces(String ymd) throws Exception {
		raceList = new ArrayList<>();
 		jyoCount = 0;
 		
		String filepath = prop.getString("DIRECTORY_CSV") + "race_" + ymd + ".csv";
		List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
		HashMap<String, String> jyoCdMap = new HashMap<>();
		for (String csvLine : lines) {
			RaceEx race = Csv2RaceEx.convert(csvLine);
			// 対象外場は外す
			if (!isJyoAllowed(race.setu.jyoCd, race.setu.grade)) {
				continue;
			}
			
			// 対象外レースは外す
			if (isRaceExcluded(race.getRaceNo())) {
				continue;
			}

			if (!condition.isPreConditionOk(race)) {
				continue;
			}
			
			raceList.add(race);
			if (!jyoCdMap.containsKey(race.setu.jyoCd)) {
				jyoCdMap.put(race.setu.jyoCd, race.setu.jyoCd);
				jyoCount++;
			}
		}
	}
	
	//abstract protected RaceEx setPopularLevel(RaceEx race) throws Exception;

	protected boolean isJyoAllowed(String jyoCd, String jyoLevel) throws Exception {
		return (rule.getString("jyoIncludeList").contains(jyoCd)
				&& rule.getString("jyoIncludeLevels").contains(jyoLevel));
	}
	
	protected boolean isRaceExcluded(String raceNo) throws Exception {
		boolean ret = false;
		String[] raceExcludeList = rule.getString("raceExcludeList").split(",");
		for (int i=0; i < raceExcludeList.length; i++) {
			if (raceNo.equals(raceExcludeList[i])) {
				ret = true;
				break;
			}
		}
		
		return ret;
	}

	/** レース結果チェックのためにレースのソート基準時間を１５分後に設定する */
	protected RaceEx setSortTimeForResult(RaceEx race, int waitMinutes) throws RaceSkipException {
		try {
			String ymdHmS = race.raceInfo.ymd + String.format("%04d00", race.raceInfo.sime);
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
		sb.append(rule.ruleName);
		sb.append(",");
		sb.append(jyoCount);
		sb.append(",");
		sb.append(betCount);
		sb.append(",");
		sb.append(user.getBalance() - user.getDailyStartBalance());
		sb.append(",");
		sb.append(user.getBalance());
		String line = sb.toString();

		String filepath = prop.getString("DIRECTORY_RESULT") + "user_" + todayYmd + "_"+ rule.getString("type") + ".csv";
		FileUtil.appendFileByLine(filepath, line);

		logger.info("総括," + sb.toString());
	}

	protected void writeRaceInformationWeka(RaceEx race) throws Exception {
		if (race.betlist.size() <= 0) {
			return;
		}
		
		List<StatMlResultWeka> listWeka = new ArrayList<>();
		List<StatMlResultWekaptn> listWekaptn = new ArrayList<>();
		for (Bet bet : race.betlist) {
			WekaPattern wekaPattern;// = bet.wekaPattern;
			ResultInfoSimple raceResult = race.getResultInfoSimple(bet.type);
			
			StatMlResultWeka resultWeka = new StatMlResultWeka();
			resultWeka.setYmd(race.raceInfo.ymd);
			resultWeka.setJyocd(race.getJyoCd());
			resultWeka.setRaceno((short)race.raceInfo.no);
			resultWeka.setDescription(user.description);
			resultWeka.setBettype(bet.type);
			resultWeka.setBetKumiban(bet.kumiban);
			resultWeka.setBetOdds(BigDecimal.valueOf(bet.odds));
			resultWeka.setBetOddsrank(bet.oddsRank);
			resultWeka.setResultKumiban(raceResult.kumiban);
			resultWeka.setResultOdds(raceResult.odds);
			resultWeka.setResultOddsrank(raceResult.oddsRank);
			
			resultWeka.setBetamt(bet.money);
			if (bet.kumiban.equals(raceResult.kumiban)) {
				resultWeka.setHity(1);
				resultWeka.setHitn(0);
				resultWeka.setHitamt(raceResult.prize);
			} else {
				resultWeka.setHity(0);
				resultWeka.setHitn(1);
				resultWeka.setHitamt(0);
			}
//			resultWeka.setPatternNameCount(wekaPattern.getPatternNameCount());
//			resultWeka.setPatternValueCount(wekaPattern.getPatternValueCountAll());
			resultWeka.setPatternNameCount(0);
			resultWeka.setPatternValueCount(0);
			listWeka.add(resultWeka);
			
//			for (Entry<String, ArrayList<StatMlPtnFiltered>> entry : wekaPattern.mapListPattern.entrySet()) {
//				StatMlResultWekaptn resultWekaptn = new StatMlResultWekaptn();
//				resultWekaptn.setDescription(resultWeka.getDescription());
//				resultWekaptn.setYmd(resultWeka.getYmd());
//				resultWekaptn.setJyocd(resultWeka.getJyocd());
//				resultWekaptn.setRaceno(resultWeka.getRaceno());
//				resultWekaptn.setBettype(resultWeka.getBettype());
//				resultWekaptn.setBetKumiban(resultWeka.getBetKumiban());
//				resultWekaptn.setPatternName(entry.getKey());
//				for (StatMlPtnFiltered ptn : entry.getValue()) {
//					resultWekaptn.setPatternValue(ptn.getPatternValue());
//					listWekaptn.add(resultWekaptn);
//				}
//			}
		}
		
		if (user.isSaveResultToDB) {
			saveWeka2Db(listWeka);
//			saveWekaPtn2Db(listWekaptn);
		} else {
			saveWeka2File(listWeka);
//			saveWekaPtn2File(listWekaptn);
		}
	}

	/**
	 *  StatMlResultWeka保存
	 * @param listWeka
	 * @throws Exception
	 */
	private void saveWeka2Db(List<StatMlResultWeka> listWeka) throws Exception {
		StatMlResultWekaMapper mapper = user.session.getMapper(StatMlResultWekaMapper.class);
		for (Iterator<StatMlResultWeka> iterator = listWeka.iterator(); iterator.hasNext();) {
			StatMlResultWeka rec = iterator.next();
			mapper.insert(rec);
		}
	}
	
	/**
	 * StatMlResultWekaptn保存
	 * @param listWekaptn
	 */
	private void saveWekaPtn2Db(List<StatMlResultWekaptn> listWekaptn) {
		StatMlResultWekaptnMapper mapper = user.session.getMapper(StatMlResultWekaptnMapper.class);
		for (Iterator<StatMlResultWekaptn> iterator = listWekaptn.iterator(); iterator.hasNext();) {
			StatMlResultWekaptn rec = iterator.next();
			mapper.insert(rec);
		}
	}

	private void saveWeka2File(List<StatMlResultWeka> list) throws Exception {
		String filepath = prop.getString("DIRECTORY_RESULT") + "statmlresultweka_" + user.description + "_" + rule.getString("type") + ".csv";
		for (Iterator<StatMlResultWeka> iterator = list.iterator(); iterator.hasNext();) {
			StatMlResultWeka rec = iterator.next();
			FileUtil.appendFileByLine(filepath, StatMlResultWeka2Csv.convert(rec));	
		}
	}

	private void saveWekaPtn2File(List<StatMlResultWekaptn> list) throws Exception {
		String filepath = prop.getString("DIRECTORY_RESULT") + "statmlresultwekaptn_" + user.description + "_" + rule.getString("type") + ".csv";
		for (Iterator<StatMlResultWekaptn> iterator = list.iterator(); iterator.hasNext();) {
			StatMlResultWekaptn rec = iterator.next();
			FileUtil.appendFileByLine(filepath, StatMlResultWekaPtn2Csv.convert(rec));	
		}
	}
	
	protected void writeRaceInformation(RaceEx race) throws Exception {		
		StringBuilder sbRaceResult = new StringBuilder();
		sbRaceResult.append(BoatUtil.currentYyyyMMddHHmmSS());
		sbRaceResult.append(",");
		sbRaceResult.append(race.raceInfo.ymd);
		sbRaceResult.append(",");
		sbRaceResult.append(race.setu.jyoCd);
		sbRaceResult.append(",");
		sbRaceResult.append(race.setu.jyo);
		sbRaceResult.append(",");
		sbRaceResult.append(race.setu.turn);
		sbRaceResult.append(",");
		sbRaceResult.append(race.setu.grade);
		sbRaceResult.append(",");
		sbRaceResult.append(user.getBalance());
		sbRaceResult.append(",");
		sbRaceResult.append(race.raceInfo.no);
		sbRaceResult.append(",");
		// ベッティング内容
		if (race.betlist.size() > 0) {
			for (Bet bet : race.betlist) {
				sbRaceResult.append(bet.toString());	
			}
		}
		sbRaceResult.append(",");
		
		if (rule.getString("type").equals("3T")) {
			sbRaceResult.append(race.raceResult.sanrentanNo + "," 
					+ (float)race.raceResult.sanrentanPrize / 100f + "," + race.raceResult.sanrentanPopular);    
		} else if (rule.getString("type").equals("3F")) {
			sbRaceResult.append(race.raceResult.sanrenhukuNo + "," 
					+ (float)race.raceResult.sanrenhukuPrize / 100f + "," + race.raceResult.sanrenhukuPopular);    
		} else if (rule.getString("type").equals("2T")) {
			sbRaceResult.append(race.raceResult.nirentanNo + "," 
					+ (float)race.raceResult.nirentanPrize / 100f + "," + race.raceResult.nirentanPopular);    
		} else if (rule.getString("type").equals("2F")) {
			sbRaceResult.append(race.raceResult.nirenhukuNo + "," 
					+ (float)race.raceResult.nirenhukuPrize / 100f + "," + race.raceResult.nirenhukuPopular);    
		} else if (rule.getString("type").equals("1T")) {
			sbRaceResult.append(race.raceResult.tansyoNo + "," 
					+ (float)race.raceResult.tansyoPrize / 100f + "," + race.raceResult.tansyoPopular);    
		}
		sbRaceResult.append(",");
		
		sbRaceResult.append(race.multiplyType);
		sbRaceResult.append(",");
		if (race.betlist.size() > 0) {
			sbRaceResult.append(race.getBetMoney());
		} else {
			sbRaceResult.append(0);
		}
		sbRaceResult.append(",");

		if (race.prize > 0) {
			sbRaceResult.append("Y");
		} else {
			if (race.betlist.size() > 0) {
				sbRaceResult.append("N");
			} else {
				sbRaceResult.append(race.skip);
			}
		}
		sbRaceResult.append(",");
		sbRaceResult.append(race.prize);

		if (sbRaceResult.length() > 0) {
			String filepath = prop.getString("DIRECTORY_RESULT") + "race_" + todayYmd + "_" + rule.getString("type") + ".csv";
			FileUtil.appendFileByLine(filepath, sbRaceResult.toString());
		}
		//logger.info("レース終了," + sbRaceResult.toString());
	}
	
	protected String getLevelPattern(RaceEx race) {
		StringBuilder sb = new StringBuilder();
		for (Waku waku : race.wakuList) {
			sb.append(waku.level.substring(0, 1));
		}
		return sb.toString();
	}

	protected String getResultKumiban(RaceEx race) throws Exception {
		String ret = null;
		String type = rule.getString("type");
		if (type.equals("3T")) {
			ret = race.raceResult.sanrentanNo;
		} else if (type.equals("3F")) {
			ret = race.raceResult.sanrenhukuNo;
		} else if (type.equals("2T")) {
			ret = race.raceResult.nirentanNo;
		} else if (type.equals("2F")) {
			ret = race.raceResult.nirenhukuNo;
		} else if (type.equals("1T")) {
			ret = race.raceResult.tansyoNo;
		} 
		return ret;
	}

	protected int getResultPrize(RaceEx race) throws Exception {
		int ret = -1;
		String type = rule.getString("type");
		if (type.equals("3T")) {
			ret = race.raceResult.sanrentanPrize;
		} else if (type.equals("3F")) {
			ret = race.raceResult.sanrenhukuPrize;
		} else if (type.equals("2T")) {
			ret = race.raceResult.nirentanPrize;
		} else if (type.equals("2F")) {
			ret = race.raceResult.nirenhukuPrize;
		} else if (type.equals("1T")) {
			ret = race.raceResult.tansyoPrize;
		} 
		return ret;
	}

	protected void doDailyClose() {
	}

	protected int getHitRate() {
		if (totalBetAmount == 0) {
			return 0;
		}
		return (int)(((float)hitCount / (float)betCount) * 100f);
	}

	protected int getBenefitRate() {
		if (totalPrizeAmount == 0) {
			return 0;
		}
		return (int) (((float) totalPrizeAmount) / (float) totalBetAmount * 100f);
	}
	
	/** DBに結果を保存する */
	protected void writeStatMlResultWeka(RaceEx race) {
		
	}
	
	
}

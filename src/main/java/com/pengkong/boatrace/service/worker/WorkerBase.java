package com.pengkong.boatrace.service.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.exception.RaceSkipException;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Setu;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.scraping.parser.ComputerPageParser;
import com.pengkong.boatrace.scraping.parser.RaceListPageParser;
import com.pengkong.boatrace.scraping.parser.RankCalculator;
import com.pengkong.boatrace.scraping.parser.SetuListPageParser;
import com.pengkong.boatrace.service.manager.TohyoManager;
import com.pengkong.boatrace.service.worker.condition.Condition;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.boatrace.simulation.model.PlayStatus;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public abstract class WorkerBase {
	private String description;
	
	private Logger logger = LoggerFactory.getLogger(WorkerBase.class);

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
	
	protected volatile boolean isRunning = true;
	protected String todayYmd;
	
	protected User user;
	PropertyUtil prop = PropertyUtil.getInstance();

	public WorkerBase(User user, BettingRule rule) throws Exception {
		super();
		this.user = user;
		this.rule = rule;
		
		this.description = rule.getString("description");
		
		Class<?> c = Class.forName(rule.getString("conditionClass"));
		condition = (Condition)c.newInstance();
	}

	public abstract void doDailyBet(String yyyyMMdd) throws Exception;

	public void stop() {
		isRunning = false;
	}
	
	protected void loadRaces(String yyyyMMdd) throws Exception {
		raceList = new ArrayList<>();
		int year = Integer.parseInt(yyyyMMdd.substring(0, 4));
		int month = Integer.parseInt(yyyyMMdd.substring(4, 6));
		int day = Integer.parseInt(yyyyMMdd.substring(6, 8));
		
		String urlDailySetuList = BoatTemplate.URL_DAILY_SETU_LIST.replace("{yyyyMMdd}", yyyyMMdd);
		// 節一覧取得
		List<Setu> setuList = new SetuListPageParser().parse(urlDailySetuList, yyyyMMdd, true);
		int setuCount = setuList.size();
		logger.info(setuCount + "個の節一覧を取得しました。");
		if (setuList.size() <= 0) {
			logger.warn("節一覧の件数が0件です。");
			return;
		}
		
 		jyoCount = 0;
		HashMap<String, String> jyoCdMap = new HashMap<>();
		// setu loop
		for (int i = 0; i < setuCount; i++) {
			Setu setu = setuList.get(i);
			String urlSetuRaceList = "";
			try {
				// レース一覧取得
				urlSetuRaceList = BoatTemplate.URL_SETU_RACELIST.replace("{jyoCd}", setu.jyoCd).replace("{yyyyMMdd}",
						yyyyMMdd);
				List<RaceEx> setuRaceList = new RaceListPageParser().parseEx(urlSetuRaceList);
				logger.info("節解析 (" + (i + 1) + "/" + setuCount + ") " + ":" + yyyyMMdd + ", " + setu.jyo + ", " + setu.name);
				
				for (RaceEx race : setuRaceList) {
					race.raceInfo.year = year;
					race.raceInfo.month = month;
					race.raceInfo.day = day;
					race.raceInfo.ymd = yyyyMMdd;
					
					// 節情報
					race.setu = setu;
					String url = "";
					try {
						String jyoCd = race.getJyoCd();
						String raceNo = race.getRaceNo();
						
						// 対象外場は外す
						if (!isJyoAllowed(jyoCd, race.setu.grade)) {
							continue;
						}
						
						if (isRaceExcluded(raceNo)) {
							continue;
						}
						
						// 枠情報取得
						url = BoatTemplate.URL_COMPUTER_PREDICT.replace("{raceNo}", raceNo).replace("{jyoCd}", jyoCd)
								.replace("{yyyyMMdd}", yyyyMMdd);
						race = new ComputerPageParser().parseAndSetInfoEx(race, url);
						// 20180708 
//						url = BoatProperty.URL_RACE_PROGRAM.replace("{raceNo}", raceNo).replace("{jyoCd}", jyoCd)
//								.replace("{yyyyMMdd}", yyyyMMdd);
//						race = new RaceProgramPageParser().parseAndSetInfoEx(race, url);
						
						// 計算情報取得
						new RankCalculator().calculateAndSetEx(race);

						if (!condition.isPreConditionOk(race)) {
							continue;
						}
						
						raceList.add(race);
						if (!jyoCdMap.containsKey(race.setu.jyoCd)) {
							jyoCdMap.put(race.setu.jyoCd, race.setu.jyoCd);
							jyoCount++;
						}
						
					} catch (Exception e) {
						logger.error("レース解析中予期せぬエラー。(" + (i + 1) + "/" + setuCount + ") " + ":" + yyyyMMdd + ", " + setu.jyo
								+ ", " + setu.name + ", url=" + url, e);
						continue;
					}
				}

			} catch (Exception e) {
				logger.error("節解析中予期せぬエラー。(" + (i + 1) + "/" + setuCount + ") " + ":" + yyyyMMdd + ", " + setu.jyo
						+ ", " + setu.name + ", url=" + urlSetuRaceList, e);
				continue;
			}
		}
		
		logger.info(description + " レース数：" + raceList.size());
		for (RaceEx race : raceList) {
			String strRace = race.getJyoCd() + "," + race.setu.jyo + "," + race.getRaceNo() + "," + String.format("%04d", race.raceInfo.sime);
			logger.info(strRace);
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
}

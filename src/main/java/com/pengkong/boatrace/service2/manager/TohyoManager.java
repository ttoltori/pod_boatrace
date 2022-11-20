package com.pengkong.boatrace.service2.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pengkong.boatrace.model.BetRequest;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.model.json.Balance;
import com.pengkong.boatrace.model.json.Deposit;
import com.pengkong.boatrace.model.json.OddsJson;
import com.pengkong.boatrace.model.json.PayOff;
import com.pengkong.boatrace.service2.worker.HeartBeat;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.PropertyUtil;
import com.pengkong.common.StringUtil;

/**
 * property dependencies: 
 *   memberNo, pin, authPassword, betPassword : 認証情報
 *   waitAfterDeposit : 入金要求後待機時間
 *   totalStartBalance : 開始残高
 * 
 * @author ttolt
 *
 */
public class TohyoManager {
	Logger logger = LoggerFactory.getLogger(TohyoManager.class);

	private final String BROWSER_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
	private final String URL_BASE = "https://ib.mbrace.or.jp/";
	//private final String URL_BASE = "https://debug10.brtb.jp/";
	private final String URL_PCTOHYO_BASE = URL_BASE + "tohyo-ap-pctohyo-web/";
	private final String URL_PCTOHYO_SERVICE_BASE = URL_PCTOHYO_BASE + "service/";
	private final String URL_LOGIN_REQUEST = URL_PCTOHYO_BASE + "auth/login";
	private final String URL_DEPOSIT_REQUEST = URL_PCTOHYO_SERVICE_BASE + "amo/chargecomp";
	private final String URL_BALANCE_REQUEST = URL_PCTOHYO_SERVICE_BASE + "ref/balref";
	private final String URL_BETLIST_CONFIRM = URL_PCTOHYO_SERVICE_BASE + "bet/betconf";
	private final String URL_BET_COMPLETE = URL_PCTOHYO_SERVICE_BASE + "bet/betcomp";
	private final String URL_LOGOUT_REQUEST = URL_PCTOHYO_BASE + "logout";
	private final String URL_PAYOFF_REQUEST = URL_PCTOHYO_SERVICE_BASE + "amo/accountcomp";
	private final String URL_ODDS_REQUEST = URL_PCTOHYO_SERVICE_BASE + "bet/betinfo/oddsupdate/today";

	private String memberNo;
	private String pin;
	private String authPassword;
	private String betPassword;

	// セッション変数
	private String jsessionId;
	private String csrf;
	private String centerNo;
	private String r;
	private String token;

	/** 入金用JSON */
	private Deposit deposit;
	/** 残高更新用JSON */
	private Balance balance;
	/** 精算用JSON */
	private PayOff payOff;

	Connection con;
	Document doc;
	Connection.Response res;

	private HeartBeat heartbeat = null; 
	private Gson gson = new Gson();
	
	private HashMap<String, String> mapType = new HashMap<>();
	
	private static TohyoManager instance = new TohyoManager();
	
	private volatile boolean loginComplete = false;
	private volatile boolean payoffComplete = false;
	private volatile boolean depositComplete = false;
	private volatile boolean logoutComplete = false;
	
	PropertyUtil prop = PropertyUtil.getInstance();
	
	public static TohyoManager getInstance() {
		return instance;
	}

	
	public TohyoManager() {
		initialize();
	}

	public void initialize() {
		this.memberNo = prop.getString("memberNo");
		this.pin = prop.getString("pin");
		this.authPassword = prop.getString("authPassword");
		this.betPassword = prop.getString("betPassword");

		loginComplete = false;
		logoutComplete = false;
		depositComplete = false;
		payoffComplete = false;
		
		mapType.put("1T", "1");
		mapType.put("2T", "3");
		mapType.put("2F", "4");
		mapType.put("3T", "6");
		mapType.put("3F", "7");
		
		heartbeat = new HeartBeat();
	}
	/**
	 * 現在レースのオッズを取得する。
	 * 
	 */
	public List<OddsJson> getOddsList(RaceEx race, String type) throws Exception {
		List<OddsJson> oddsList = new ArrayList<>();

		JsonObject jo = getJsonOdds(race);
		JsonArray arrJo = jo.getAsJsonObject("oddsDetailListByKachishiki").getAsJsonArray(mapType.get(type));
		for (JsonElement je : arrJo) {
			OddsJson odds = gson.fromJson(je, OddsJson.class);
			oddsList.add(odds);
		}
		
		return oddsList;
	}
	
	/**
	 * 全オッズのjsonを取得する.
	 * 
	 * @param race レース
	 * @return
	 * @throws IOException
	 */
	public JsonObject getJsonOdds(Race race) throws IOException {
		String url = getOddsRequestUrl();
		String resJson = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT)
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.header("X-CSRF-TOKEN", csrf).header("X-Requested-With", "XMLHttpRequest")
				.data("screenFrom", "apA43", 
						"raceSelect", "02", 
						"betWay", "1",
						"multiJyo", "false",
						"selectionJyoList[0]", race.setu.jyoCd,
						"multiRace", "false",
						"selectionRaceNoList[0]", String.format("%02d", race.raceInfo.no)
						)
				.cookie("JSESSIONID", jsessionId)
				.ignoreContentType(true)
				.method(Method.POST)
				.execute()
				.body();

		return gson.fromJson(resJson, JsonObject.class);
	}
	
	/**
	 * 残高をチェックして入金が必要なら入金する。
	 * @return
	 * @throws Exception
	 */
	public synchronized int depositIfNeeded() throws Exception {
		if (depositComplete) {
			return balance.purchasableBetAmount;
		}
		
		// 残高更新
		updateBalance();
		
		Thread.sleep(3000);
		
		// 入金必要
		if (balance.purchasableBetAmount <= 0) {
			int totalStartBalance = prop.getInteger("totalStartBalance");
			// 入金要求
			deposit(totalStartBalance);
			
			Thread.sleep(prop.getInteger("waitAfterDeposit"));
			
			// 残高更新
			updateBalance();
		}
		
		depositComplete = true;
		return balance.purchasableBetAmount;
	}
	
	/**
	 * 入金
	 */
	private void deposit(int money) throws Exception {
		int amount = money / 1000; // 100円単位
		String url = getDepositRequestUrl();
		//String resJson = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT).validateTLSCertificates(false)
		String resJson = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT)
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.header("X-CSRF-TOKEN", csrf).header("X-Requested-With", "XMLHttpRequest")
				.cookie("JSESSIONID", jsessionId)
				.data("chargeInstructAmt", String.valueOf(amount), "betPassword", betPassword, "screenFrom", "B610",
						"token", token)
				.cookie("JSESSIONID", jsessionId).ignoreContentType(true).method(Method.POST).execute().body();
		deposit = gson.fromJson(resJson, Deposit.class);
		token = deposit.token;
		
		logger.info("入金:" + resJson);
	}

	/**
	 * 残高更新
	 */
	public void updateBalance() throws Exception {
		String url = getBalanceRequestUrl();
		//String resJson = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT).validateTLSCertificates(false)
		String resJson = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT)
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.header("X-CSRF-TOKEN", csrf).header("X-Requested-With", "XMLHttpRequest")
				.cookie("JSESSIONID", jsessionId)
				.data("screenFrom", "B610", "token", token)
				.cookie("JSESSIONID", jsessionId)
				.ignoreContentType(true)
				.method(Method.POST).execute().body();
		balance = gson.fromJson(resJson, Balance.class);
		token = balance.token;
		logger.debug("残高更新:" + resJson);
	}

	/**
	 * ログイン
	 * 
	 * @throws Exception
	 */
	public synchronized void login() throws Exception {
		if (loginComplete) {
			return;
		}
		
		// ログインページ表示
		//res = Jsoup.connect(URL_BASE).userAgent(BROWSER_USER_AGENT).validateTLSCertificates(false)
		res = Jsoup.connect(URL_BASE).userAgent(BROWSER_USER_AGENT)
				.method(Method.GET).execute();
		jsessionId = res.cookies().get("JSESSIONID");

		doc = res.parse();
		centerNo = getCenterNoFromMeta();
		csrf = getCsrfFromMeta();
		r = getRFromLoginForm();
		doc = null;

		// ログインrequest
		String url = getLoginUrl();
		//res = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT).validateTLSCertificates(false)
		res = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT)
				.data("dummyA", "", "dummyB", "", "memberNo", memberNo, "pin", pin, "authPassword", authPassword,
						"operationKbn", "", "screenFrom", "A604", "token", "", "_csrf", csrf)
				.cookie("JSESSIONID", jsessionId).method(Method.POST).followRedirects(true).execute();
		jsessionId = res.cookies().get("JSESSIONID");
		doc = res.parse();
		// TOPページへリダイレクト
		csrf = getCsrfFromMeta();
		centerNo = getCenterNoFromMeta();
		token = getTokenFromInputTag();
		loginComplete = true;
		logger.info("ログイン:" + memberNo);
		
		startHeartbeat();
	}

	private void startHeartbeat() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(heartbeat);
		executorService.shutdown();
	}
	
	private void stopHeartbeat() {
		heartbeat.stop();
	}
	
	
	/**
	 * 精算要求
	 */
	public synchronized int payoff() throws Exception {
		if (payoffComplete) {
			return -1;
		}
		
		String url = getPayoffUrl();
		//String resJson = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT).validateTLSCertificates(false)
		String resJson = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT)
				.header("Accept", "application/json, text/javascript, */*; q=0.01").header("X-CSRF-TOKEN", csrf)
				.header("X-Requested-With", "XMLHttpRequest").cookie("JSESSIONID", jsessionId)
				.data("betPassword", betPassword, "screenFrom", "B636", "token", token).cookie("JSESSIONID", jsessionId)
				.ignoreContentType(true).method(Method.POST).execute().body();
		payOff = gson.fromJson(resJson, PayOff.class);
		token = deposit.token;
		
		logger.info("精算:" + resJson);
		return payOff.accountInstructAmt;
	}

	/**
	 * ログアウト要求
	 */
	public synchronized void logout() throws Exception {
		if (logoutComplete) {
			return;
		}
		
		stopHeartbeat();
		
		String url = getLogoutUrl();
		//Jsoup.connect(url).userAgent(BROWSER_USER_AGENT).validateTLSCertificates(false)
		Jsoup.connect(url).userAgent(BROWSER_USER_AGENT)
				.header("Accept", "application/json, text/javascript, */*; q=0.01").header("X-CSRF-TOKEN", csrf)
				.header("X-Requested-With", "XMLHttpRequest")
				.data("screenFrom", "B610", "token", token)
				.cookie("JSESSIONID", jsessionId)
				.ignoreContentType(true)
				.method(Method.POST)
				.execute();
		jsessionId = "";
		
		loginComplete = true;
		logger.info("ログアウト:" + memberNo);
	}

	public synchronized void bet(BetRequest betReq) throws Exception {
		// 投票確認
		String url = getBetlistConfirmUrl();
		//Connection conn = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT).validateTLSCertificates(false)
		Connection conn = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT)
				.data("token", token,
				"_csrf", csrf, 
				"betWay", "1", 
				"jyoCode", betReq.jyoCd, 
				"multiJyo", "false", 
				"multiRace", "false",
				"lastSelectionKachishiki", "6", 
				"lastSelectionBetWay", "", 
				"lastSelectionRacer", "1", 
				"operationKbn","", 
				"screenFrom", "C614");
				for (int i = 0; i < betReq.betList.size(); i++) {
					Bet bet = betReq.betList.get(i);
					conn = conn.data("betContentsList[" + i + "].betList[0].betJyoCode", betReq.jyoCd);
					conn = conn.data("betContentsList[" + i + "].betList[0].raceNo", betReq.raceNo);
					conn = conn.data("betContentsList[" + i + "].betList[0].kachishiki", BoatUtil.typeToKachisiki(bet.type));
					conn = conn.data("betContentsList[" + i + "].betList[0].betWay", "1");
					conn = conn.data("betContentsList[" + i + "].betList[0].kumiban", "");
					conn = conn.data("betContentsList[" + i + "].betList[0].numberOfSheets", "");
					conn = conn.data("betContentsList[" + i + "].betList[0].unfoldedFlg", "1");
					conn = conn.data("betContentsList[" + i + "].betList[0].unfoldedKumiban", bet.getTohyoBetKumiban());
					conn = conn.data("betContentsList[" + i + "].betList[0].unfoldedNum", String.valueOf((int) (bet.money / 100)));
				}
		res = conn.data(
				"selectionJyoList[0]", betReq.jyoCd,
				"selectionRaceNoList[0]", betReq.raceNo
				)
				.cookie("JSESSIONID", jsessionId)
				.method(Method.POST)
				.followRedirects(true)
				.execute();
		doc = res.parse();
		csrf = getCsrfFromMeta();
		centerNo = getCenterNoFromMeta();
		token = getTokenFromInputTag();

		Thread.sleep(500);

		url = getBetCompleteUrl();
		//res = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT).validateTLSCertificates(false)
		res = Jsoup.connect(url).userAgent(BROWSER_USER_AGENT)
				.data("dummyA", "", 
						"dummyB", "", 
						"betAmount", String.valueOf(betReq.getBetAmount()), 
						"betPassword", betPassword, 
						"operationKbn", "", 
						"token", token, 
						"_csrf", csrf, 
						"screenFrom", "D628", 
						"betWay", "1", 
						"jyoCode", betReq.jyoCd, 
						"multiJyo", "false", 
						"selectionJyoList", betReq.jyoCd,
						"multiRace", "false", 
						"selectionRaceNoList", betReq.raceNo, 
						"lastSelectionKachishiki", "6",
						"lastSelectionBetWay", "", 
						"lastSelectionRacer", ""
					)
				.cookie("JSESSIONID", jsessionId)
				.method(Method.POST)
				.followRedirects(true)
				.execute();
		doc = res.parse();
		csrf = getCsrfFromMeta();
		centerNo = getCenterNoFromMeta();
		token = getTokenFromInputTag();
		
		Thread.sleep(500);
		logger.info("投票要求:" + betReq.toString());
	}

	private String getPayoffUrl() {
		return URL_PAYOFF_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}

	private String getLogoutUrl() {
		return URL_LOGOUT_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}

	private String getBetCompleteUrl() {
		return URL_BET_COMPLETE + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}

	private String getBetlistConfirmUrl() {
		return URL_BETLIST_CONFIRM + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(19);
	}

	private String getTokenFromInputTag() {
		try {
			return doc.getElementsByAttributeValue("name", "token").get(0).attr("value");
		} catch (Exception e) {
			logger.debug(doc.html());
		}
		
		return null;
	}

	private String getLoginUrl() {
		return URL_LOGIN_REQUEST + "?cid=" + centerNo + "&r=" + r;
	}

	private String getDepositRequestUrl() {
		return URL_DEPOSIT_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}

	private String getBalanceRequestUrl() {
		return URL_BALANCE_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}
	
	private String getOddsRequestUrl() {
		return URL_ODDS_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}

	private String getCenterNoFromMeta() {
		Elements metaTags = doc.getElementsByTag("meta");
		for (Element metaTag : metaTags) {
			if (metaTag.attr("name").equals("centerNo")) {
				return metaTag.attr("content");
			}
		}
		return null;
	}

	private String getCsrfFromMeta() {
		Elements metaTags = doc.getElementsByTag("meta");
		for (Element metaTag : metaTags) {
			if (metaTag.attr("name").equals("_csrf_token")) {
				return metaTag.attr("content");
			}
		}
		return null;
	}

	private String getRFromLoginForm() {
		Element loginForm = doc.getElementById("loginForm");
		String attr = loginForm.attr("action");
		String[] token = attr.split("=");
		return token[token.length - 1];
	}

	public static void main(String[] args) {
		try {
			TohyoManager tohyoManager = TohyoManager.getInstance();
			
			tohyoManager.login();
			Thread.sleep(500);
			
			tohyoManager.deposit(10000);
			Thread.sleep(3000);
			
			tohyoManager.updateBalance();
			Thread.sleep(500);
			
			tohyoManager.updateBalance();
			Thread.sleep(500);
			
			// 1T * 1
			BetRequest betReq = createBetReq("1T", new String[] {"1"});
			tohyoManager.bet(betReq);
			
			betReq = createBetReq("2T", new String[] {"12"});
			tohyoManager.bet(betReq);

			betReq = createBetReq("2F", new String[] {"12", "13"});
			tohyoManager.bet(betReq);
			
			betReq = createBetReq("3T", new String[] {"123", "124"});
			tohyoManager.bet(betReq);
			
			betReq = createBetReq("3F", new String[] {"123", "134", "145", "156"});
			tohyoManager.bet(betReq);
			
			tohyoManager.payoff();
			Thread.sleep(500);
			
			tohyoManager.updateBalance();
			Thread.sleep(500);
			
			tohyoManager.updateBalance();
			Thread.sleep(500);
			
			tohyoManager.logout();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static BetRequest createBetReq(String type, String[] kumiban) {
		BetRequest betReq = new BetRequest();
		//betReq.kachisiki = BoatUtil.typeToKachisiki(type);
		betReq.jyoCd = "02";
		betReq.raceNo = "12";
		List<Bet> betList = new ArrayList<>();
		for (int i = 0; i < kumiban.length; i++) {
			betList.add(new Bet(type, kumiban[i], 1000, 1.5f, 0 ));
		}
		betReq.betList =betList;

		return betReq;
	}
}

package com.pengkong.boatrace.service.manager;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.model.BetRequest;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.betting.Bet;
import com.pengkong.boatrace.model.json.Balance;
import com.pengkong.boatrace.model.json.Deposit;
import com.pengkong.boatrace.model.json.OddsJson;
import com.pengkong.boatrace.model.json.PayOff;
import com.pengkong.boatrace.simulation.model.BettingRule;
import com.pengkong.common.StringUtil;

public class TohyoManagerMock {
	Logger logger = LoggerFactory.getLogger(TohyoManagerMock.class);

	private final String BROWSER_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
	// private final String URL_BASE = "https://ib.mbrace.or.jp/";
	private final String URL_BASE = "https://debug8.brtb.jp/";
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

	private BettingRule rule;

	public TohyoManagerMock(BettingRule rule) throws Exception {
		super();
		this.rule = rule;
		this.memberNo = rule.getString("memberNo");
		this.pin = rule.getString("pin");
		this.authPassword = rule.getString("authPassword");
		this.betPassword = rule.getString("betPassword");
	}
	
	public List<OddsJson> getOddsList3T(RaceEx race) throws Exception {
		String url = getOddsRequestUrl();
		List<OddsJson> oddsList = new ArrayList<>();
		return oddsList;
	}
	
	/**
	 * 残高確認及び入金
	 * @throws Exception
	 */
	public int depositIfNeeded() throws Exception {
		return rule.getInt("totalStartBalance");
	}

	/**
	 * 入金
	 */
	public void deposit(int money) throws Exception {
		logger.info("入金:" + money);
	}

	/**
	 * 残高更新
	 */
	public void updateBalance() throws Exception {
		String url = getBalanceRequestUrl();
		logger.info("残高更新:" + url);
	}

	/**
	 * ログイン
	 * 
	 * @throws Exception
	 */
	public void login() throws Exception {
		logger.info("ログイン:" + memberNo);
	}

	/**
	 * 精算要求
	 */
	public void payoff() throws Exception {
		String url = getPayoffUrl();
		logger.info("精算:" + url);
	}

	/**
	 * ログアウト要求
	 */
	public void logout() throws Exception {
		logger.info("ログアウト:" + memberNo);
	}

	public void bet(BetRequest betReq) throws Exception {
		// 投票確認
		String url = getBetlistConfirmUrl();
		
		logger.info("投票要求:" + url);
	}
	
	private String getOddsRequestUrl() {
		return URL_ODDS_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
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
		return doc.getElementsByAttributeValue("name", "token").get(0).attr("value");
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
			TohyoManagerMock tohyoManager = new TohyoManagerMock(
					new BettingRule("C:/Dev/workspace/Oxygen/pod_boatrace/release/properties/rule3.properties"));
			tohyoManager.login();
			Thread.sleep(500);
			tohyoManager.deposit(10000);
			Thread.sleep(3000);
			tohyoManager.updateBalance();
			Thread.sleep(500);
			tohyoManager.updateBalance();
			Thread.sleep(500);
			
			List<Bet> betList = new ArrayList<>();
			betList.add(new Bet("3T", "123", 100, 5.4f, 1));
			betList.add(new Bet("3T", "124", 200, 3.4f, 1));
			BetRequest betReq = new BetRequest();
			betReq.jyoCd = "01";
			betReq.raceNo = "02";
			betReq.betList = betList;
			
			tohyoManager.bet(betReq);
			Thread.sleep(500);
			
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

}

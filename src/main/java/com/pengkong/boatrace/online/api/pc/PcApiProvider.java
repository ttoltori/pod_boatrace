package com.pengkong.boatrace.online.api.pc;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.model.json.Balance;
import com.pengkong.boatrace.model.json.Deposit;
import com.pengkong.boatrace.model.json.PayOff;
import com.pengkong.boatrace.online.api.AbstractApiProvider;
import com.pengkong.boatrace.online.api.session.HeartBeatRunner;
import com.pengkong.boatrace.online.api.session.Session;
import com.pengkong.boatrace.online.api.session.User;
import com.pengkong.boatrace.online.exception.ApiException;
import com.pengkong.boatrace.online.tohyo.bet.Bet;
import com.pengkong.boatrace.online.tohyo.bet.BetRequest;
import com.pengkong.boatrace.util.BoatUtil;
	
public class PcApiProvider extends AbstractApiProvider {
	Logger logger = LoggerFactory.getLogger(PcApiProvider.class);
	
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	PcApiHelper helper = new PcApiHelper();
	/**
	 * ログイン
	 * 
	 * @throws Exception
	 */
	@Override
	public void login() throws ApiException {
		Connection.Response res = null;
		Document doc = null;
		
		try {
			// ログインページ表示
			res = Jsoup.connect(PcUrl.URL_BASE).userAgent(PcUrl.BROWSER_USER_AGENT).validateTLSCertificates(false)
			//res = Jsoup.connect(PcUrl.URL_BASE).userAgent(PcUrl.BROWSER_USER_AGENT)
					.method(Method.GET).execute();
			doc = res.parse();
		} catch (Exception e) {
			throw new ApiException(e);
		}
		
		String jsessionId = res.cookies().get("JSESSIONID");
		String centerNo = helper.getCenterNoFromMeta(doc);
		String csrf = helper.getCsrfFromMeta(doc);
		String r = helper.getRFromLoginForm(doc);
		
		// reCAPTCHA
		String rctoken = getRecaptchaToken();
		
		// ユーザー生成
		User user = createUser();
		
		// ログイン要求
		String loginUrl = PcUrl.getLoginUrl(centerNo, r);
		
		try {
			res = Jsoup.connect(loginUrl).userAgent(PcUrl.BROWSER_USER_AGENT).validateTLSCertificates(false)
			//res = Jsoup.connect(loginUrl).userAgent(PcUrl.BROWSER_USER_AGENT)
					.data("dummyA", "", "dummyB", "", "memberNo", user.getMemberNo(), "pin", user.getPin(), "authPassword",
							user.getAuthPassword(), "operationKbn", "", "screenFrom", "A604", "token", "", "_csrf", csrf, "rctoken", rctoken)
					.cookie("JSESSIONID", jsessionId).method(Method.POST).followRedirects(true).execute();
			doc = res.parse();
		} catch (Exception e) {
			throw new ApiException(e);
		}

		// セッション生成
		session = new Session(
				user,
				res.cookies().get("JSESSIONID"),
				helper.getCsrfFromMeta(doc),
				helper.getTokenFromInputTag(doc),
				helper.getCenterNoFromMeta(doc)
				);

		// HeartBeat開始
		startHeartbeat(new HeartBeatRunner(this, Integer.parseInt(prop.getString("heartbeat_interval"))));

		logger.info("login is successful:" + user.getMemberNo());
	}
	
	/**
	 * ログアウト要求
	 */
	@Override
	public void logout() throws ApiException {
		stopHeartbeat();
		
		String url = PcUrl.getLogoutUrl(session.getCenterNo());
		
		try {
			Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT).validateTLSCertificates(false)
			//Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT)
					.header("Accept", "application/json, text/javascript, */*; q=0.01").header("X-CSRF-TOKEN", session.getCsrf())
					.header("X-Requested-With", "XMLHttpRequest")
					.data("screenFrom", "B610", "token", session.getToken())
					.cookie("JSESSIONID", session.getJsessionId())
					.ignoreContentType(true)
					.method(Method.POST)
					.execute();
			
			session.update("", "", "");
		} catch (Exception e) {
			throw new ApiException(e);
		}
		
		logger.info("logout is successful:" + session.getUser().getMemberNo());
	}
	
	/**
	 * 残高更新
	 */
	@Override
	public void updateBalance() throws ApiException {
		Gson gson = new Gson();
		
		String url = PcUrl.getBalanceRequestUrl(session.getCenterNo());
		String resJson = null;
		
		try {
			resJson = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT).validateTLSCertificates(false)
			//resJson = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT)
					.header("Accept", "application/json, text/javascript, */*; q=0.01")
					.header("X-CSRF-TOKEN", session.getCsrf()).header("X-Requested-With", "XMLHttpRequest")
					.cookie("JSESSIONID", session.getJsessionId())
					.data("screenFrom", "B610", "token", session.getToken())
					.ignoreContentType(true)
					.method(Method.POST).execute().body();
			
			Balance balance = gson.fromJson(resJson, Balance.class);
			session.update(balance);
		} catch (Exception e) {
			throw new ApiException(e);
		}
		logger.info("balance : " + session.getBalance().purchasableBetAmount);
	}

	/**
	 * 入金
	 */
	@Override
	public void deposit(int money) throws ApiException {
		String url = PcUrl.getDepositRequestUrl(session.getCenterNo());
		User user = session.getUser();
		Gson gson = new Gson();
		String resJson = null;
		
		// reCAPTCHA
		String rctoken = getRecaptchaToken();
		
		try {
			int amount = money / 1000; // 100円単位
			resJson = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT).validateTLSCertificates(false)
			//resJson = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT)
					.header("Accept", "application/json, text/javascript, */*; q=0.01")
					.header("X-CSRF-TOKEN", session.getCsrf()).header("X-Requested-With", "XMLHttpRequest")
					.cookie("JSESSIONID", session.getJsessionId())
					.data("chargeInstructAmt", String.valueOf(amount), "betPassword", user.getBetPassword(), "screenFrom", "B610",
							"token", session.getToken(), "rctoken", rctoken)
					.cookie("JSESSIONID", session.getJsessionId()).ignoreContentType(true).method(Method.POST).execute().body();
			Deposit deposit = gson.fromJson(resJson, Deposit.class);
			session.update(deposit);
		} catch (Exception e) {
			throw new ApiException(e);
		}
		
		logger.info("deposit is successful:" + resJson);
	}
	
	@Override
	public void bet(BetRequest betReq) throws ApiException {
		Connection.Response res;
		Document doc;
		try {
			logger.info("betting start." + betReq.toString());
			
			String rctoken = getRecaptchaToken();
			
			// 投票確認
			String url = PcUrl.getBetlistConfirmUrl(session.getCenterNo());
			Connection conn = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT).validateTLSCertificates(false)
			//Connection conn = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT)
					.data("token", session.getToken(),
					"rctoken", rctoken,
					"_csrf", session.getCsrf(), 
					"betWay", "1", 
					"jyoCode", betReq.getJyoCd(), 
					"multiJyo", "false", 
					"multiRace", "false",
					"lastSelectionKachishiki", "6", 
					"lastSelectionBetWay", "", 
					"lastSelectionRacer", "1", 
					"operationKbn","", 
					"screenFrom", "C614");
					for (int i = 0; i < betReq.getBetList().size(); i++) {
						Bet bet = betReq.getBetList().get(i);
						conn = conn.data("betContentsList[" + i + "].betList[0].betJyoCode", betReq.getJyoCd());
						conn = conn.data("betContentsList[" + i + "].betList[0].raceNo", betReq.getRaceNo());
						conn = conn.data("betContentsList[" + i + "].betList[0].kachishiki", BoatUtil.typeToKachisiki(bet.getBetType()));
						conn = conn.data("betContentsList[" + i + "].betList[0].betWay", "1");
						conn = conn.data("betContentsList[" + i + "].betList[0].kumiban", "");
						conn = conn.data("betContentsList[" + i + "].betList[0].numberOfSheets", "");
						conn = conn.data("betContentsList[" + i + "].betList[0].unfoldedFlg", "1");
						conn = conn.data("betContentsList[" + i + "].betList[0].unfoldedKumiban", bet.getTohyoBetKumiban());
						conn = conn.data("betContentsList[" + i + "].betList[0].unfoldedNum", String.valueOf((int) (bet.getBetAmt() / 100)));
					}
			res = conn.data(
					"selectionJyoList[0]", betReq.getJyoCd(),
					"selectionRaceNoList[0]", betReq.getRaceNo()
					)
					.cookie("JSESSIONID", session.getJsessionId())
					.method(Method.POST)
					.followRedirects(true)
					.execute();
			
			doc = res.parse();
			session.update(
					helper.getCsrfFromMeta(doc), 
					helper.getCenterNoFromMeta(doc),
					helper.getTokenFromInputTag(doc));

			// 0.5秒待機
			Thread.sleep(1000);

			// reCAPTCHA
			rctoken = getRecaptchaToken();

			// 投票要求
			User user = session.getUser();
			url = PcUrl.getBetCompleteUrl(session.getCenterNo());
			res = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT).validateTLSCertificates(false)
			//res = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT)
					.data("dummyA", "", 
							"dummyB", "", 
							"betAmount", String.valueOf(betReq.getBetAmount()), 
							"betPassword", user.getBetPassword(), 
							"operationKbn", "", 
							"token", session.getToken(), 
							"rctoken", rctoken, 
							"_csrf", session.getCsrf(), 
							"screenFrom", "D628", 
							"betWay", "1", 
							"jyoCode", betReq.getJyoCd(), 
							"multiJyo", "false", 
							"selectionJyoList", betReq.getJyoCd(),
							"multiRace", "false", 
							"selectionRaceNoList", betReq.getRaceNo(), 
							"lastSelectionKachishiki", "6",
							"lastSelectionBetWay", "", 
							"lastSelectionRacer", ""
						)
					.cookie("JSESSIONID", session.getJsessionId())
					.method(Method.POST)
					.followRedirects(true)
					.execute();
			
			doc = res.parse();
			session.update(
					helper.getCsrfFromMeta(doc), 
					helper.getCenterNoFromMeta(doc),
					helper.getTokenFromInputTag(doc));
			
			// 0.5秒待機
			Thread.sleep(500);			
		} catch (Exception e) {
			throw new ApiException(e);
		}

		logger.info("betting is successful");
	}
	
	/**
	 * 精算要求
	 */
	public int payoff() throws ApiException {
		String url = PcUrl.getPayoffUrl(session.getCenterNo());
		User user = session.getUser();
		Gson gson = new Gson();
		String resJson = null;
		PayOff payOff = null;
		try {
			resJson = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT).validateTLSCertificates(false)
			//resJson = Jsoup.connect(url).userAgent(PcUrl.BROWSER_USER_AGENT)
					.header("Accept", "application/json, text/javascript, */*; q=0.01").header("X-CSRF-TOKEN", session.getCsrf())
					.header("X-Requested-With", "XMLHttpRequest").cookie("JSESSIONID", session.getJsessionId())
					.data("betPassword", user.getBetPassword(), "screenFrom", "B636", 
							"token", session.getToken()).cookie("JSESSIONID", session.getJsessionId())
					.ignoreContentType(true).method(Method.POST).execute().body();
			
			payOff = gson.fromJson(resJson, PayOff.class);
			session.update(payOff);
		} catch (Exception e) {
			throw new ApiException(e);
		}

		logger.info("deposit is successful:" + resJson);
		
		return payOff.accountInstructAmt;
	}
}

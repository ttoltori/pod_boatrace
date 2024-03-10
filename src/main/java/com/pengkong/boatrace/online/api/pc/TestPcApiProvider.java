package com.pengkong.boatrace.online.api.pc;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.model.json.Balance;
import com.pengkong.boatrace.model.json.Deposit;
import com.pengkong.boatrace.model.json.PayOff;
import com.pengkong.boatrace.online.api.AbstractApiProvider;
import com.pengkong.boatrace.online.api.pc.PcApiHelper;
import com.pengkong.boatrace.online.api.pc.PcUrl;
import com.pengkong.boatrace.online.api.session.HeartBeatRunner;
import com.pengkong.boatrace.online.api.session.Session;
import com.pengkong.boatrace.online.api.session.User;
import com.pengkong.boatrace.online.exception.ApiException;
import com.pengkong.boatrace.online.tohyo.bet.BetRequest;

public class TestPcApiProvider extends AbstractApiProvider {
	Logger logger = LoggerFactory.getLogger(TestPcApiProvider.class);
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	PcApiHelper helper = new PcApiHelper();
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
		logger.info("Mock Api deposit. " + money);
	}

	@Override
	public void bet(BetRequest betReq) throws ApiException {
		logger.info("BETTING" + betReq);
		
	}

	/**
	 * 精算要求
	 */
	public int payoff() throws ApiException {
		logger.info("Mock Api payoff. ");
		return 0;
	}
}

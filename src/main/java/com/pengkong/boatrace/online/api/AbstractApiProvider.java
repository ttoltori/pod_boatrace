package com.pengkong.boatrace.online.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.online.api.pc.PcUrl;
import com.pengkong.boatrace.online.api.session.HeartBeatRunnerInterface;
import com.pengkong.boatrace.online.api.session.Session;
import com.pengkong.boatrace.online.api.session.User;
import com.pengkong.boatrace.online.exception.ApiException;
import com.pengkong.boatrace.online.tohyo.bet.BetRequest;

import lombok.Getter;

public abstract class AbstractApiProvider {
	Logger logger = LoggerFactory.getLogger(AbstractApiProvider.class);

	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** セッション情報 */
	@Getter
	protected Session session;
	
	/** セッション維持用メソッドを定期的に呼び出す */
	HeartBeatRunnerInterface runner;
	
	protected User createUser() {
		return new User(
				prop.getString("user_memberno"),
				prop.getString("user_pin"), 
				prop.getString("user_auth_pwd"), 
				prop.getString("user_bet_pwd"));
	}

	protected void startHeartbeat(HeartBeatRunnerInterface runner) {
		this.runner = runner;
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(runner);
		executorService.shutdown();
	}

	protected String getRecaptchaToken() throws ApiException {
		try {
			// google recaptcha anchor
			Response res = Jsoup.connect(PcUrl.URL_RECAPTCHA_ANCHOR).userAgent(PcUrl.BROWSER_USER_AGENT).validateTLSCertificates(false)
					.method(Method.GET).execute();
			Document doc = res.parse();
			
			Element input = doc.getElementById("recaptcha-token");
			String attr = input.attr("value");
			
			return attr;
		} catch (Exception e) {
			logger.error("google recaptcha failed.", e);
			throw new ApiException(e);
		}
	}
	
	protected void stopHeartbeat() {
		runner.stop();
	}

	/** ログイン */
	public abstract void login() throws ApiException;
	
	/** ログアウト */
	public abstract void logout() throws ApiException;
	
	/** 残高更新 */
	public abstract void updateBalance() throws ApiException;
	
	/**
	 * 入金要求
	 * @param money 入金金額
	 * @throws Exception
	 */
	public abstract void deposit(int money) throws ApiException;
	
	/** 投票 */
	public abstract void bet(BetRequest betReq) throws ApiException;
	
	/** 清算要求 
	 * @return 清算金額
	 * @throws Exception
	 */
	public abstract int payoff() throws ApiException;
}

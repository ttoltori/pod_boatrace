package com.pengkong.boatrace.online.api.pc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.model.json.Balance;
import com.pengkong.boatrace.online.api.AbstractApiProvider;
import com.pengkong.boatrace.online.api.session.HeartBeatRunner;
import com.pengkong.boatrace.online.api.session.Session;
import com.pengkong.boatrace.online.api.session.User;
import com.pengkong.boatrace.online.exception.ApiException;
import com.pengkong.boatrace.online.tohyo.bet.BetRequest;

public class MockPcApiProvider extends AbstractApiProvider {
	Logger logger = LoggerFactory.getLogger(MockPcApiProvider.class);
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	@Override
	public void login() throws ApiException {
		User user = createUser();
		Session session = new Session(user, "jsession", "csrf", "token", "centerno");
		super.session = session;
		
		// HeartBeat開始
		startHeartbeat(new HeartBeatRunner(this, Integer.parseInt(prop.getString("heartbeat_interval"))));
		logger.info("Mock Api login.");
	}

	@Override
	public void logout() throws ApiException {
		stopHeartbeat();
		
		logger.info("Mock Api logout.");
	}

	@Override
	public void updateBalance() throws ApiException {
		Balance balance = new Balance();
		balance.purchasableBetAmount = 10000;
		balance.token = "balanceToken";
		
		session.update(balance);
		
		logger.info("Mock Api updateBalance.");
	}

	@Override
	public void deposit(int money) throws ApiException {
		logger.info("Mock Api deposit. " + money);
	}

	@Override
	public void bet(BetRequest betReq) throws ApiException {
		logger.info("Mock Api bet. " + betReq);
		
	}

	@Override
	public int payoff() throws ApiException {
		logger.info("Mock Api payoff. ");
		return 0;
	}
}

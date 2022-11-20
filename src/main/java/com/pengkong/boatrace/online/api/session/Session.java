package com.pengkong.boatrace.online.api.session;

import com.pengkong.boatrace.model.json.Balance;
import com.pengkong.boatrace.model.json.Deposit;
import com.pengkong.boatrace.model.json.PayOff;

import lombok.Getter;

@Getter
public class Session {
	/** 加入者情報 */
	User user;
	
	/** 残高情報 */
	Balance balance;
	
	/** 入金情報 */
	Deposit deposit;
	
	/** 清算情報 */
	PayOff payOff;
	
	/** セッション変数 */
	private String jsessionId;
	private String csrf;
	private String token;
	private String centerNo;
	
	public Session(User user, String jsessionId, String csrf, String token, String centerNo) {
		super();
		this.user = user;
		this.jsessionId = jsessionId;
		this.csrf = csrf;
		this.token = token;
		this.centerNo = centerNo;
	}

	/** 残高更新 */
	public void update(Balance balance) {
		this.balance = balance;
		this.token = balance.token;
	}

	/** 入金情報 */
	public void update(Deposit deposit) {
		this.deposit = deposit;
		this.token = deposit.token;
	}

	/** 清算情報 */
	public void update(PayOff payOff) {
		this.payOff = payOff;
		this.token = payOff.token;
	}
	
	public void update(String csrf, String centerNo, String token) {
		this.csrf = csrf;
		this.centerNo = centerNo;
		this.token = token;
	}

}

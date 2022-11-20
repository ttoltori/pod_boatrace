package com.pengkong.boatrace.online.api.pc;

import com.pengkong.common.StringUtil;

public class PcUrl {
	public static final String BROWSER_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
	public static final String URL_BASE = "https://ib.mbrace.or.jp/";
	//public static final String URL_BASE = "https://debug10.brtb.jp/";
	public static final String URL_PCTOHYO_BASE = URL_BASE + "tohyo-ap-pctohyo-web/";
	public static final String URL_PCTOHYO_SERVICE_BASE = URL_PCTOHYO_BASE + "service/";
	public static final String URL_LOGIN_REQUEST = URL_PCTOHYO_BASE + "auth/login";
	public static final String URL_DEPOSIT_REQUEST = URL_PCTOHYO_SERVICE_BASE + "amo/chargecomp";
	public static final String URL_BALANCE_REQUEST = URL_PCTOHYO_SERVICE_BASE + "ref/balref";
	public static final String URL_BETLIST_CONFIRM = URL_PCTOHYO_SERVICE_BASE + "bet/betconf";
	public static final String URL_BET_COMPLETE = URL_PCTOHYO_SERVICE_BASE + "bet/betcomp";
	public static final String URL_LOGOUT_REQUEST = URL_PCTOHYO_BASE + "logout";
	public static final String URL_PAYOFF_REQUEST = URL_PCTOHYO_SERVICE_BASE + "amo/accountcomp";
	public static final String URL_ODDS_REQUEST = URL_PCTOHYO_SERVICE_BASE + "bet/betinfo/oddsupdate/today";

	public static String getLoginUrl(String centerNo, String r) {
		return URL_LOGIN_REQUEST + "?cid=" + centerNo + "&r=" + r;
	}
	
	public static String getBalanceRequestUrl(String centerNo) {
		return URL_BALANCE_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}
	
	public static String getLogoutUrl(String centerNo) {
		return URL_LOGOUT_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}
	
	public static String getDepositRequestUrl(String centerNo) {
		return URL_DEPOSIT_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}

	public static String getPayoffUrl(String centerNo) {
		return URL_PAYOFF_REQUEST + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}

	public static String getBetCompleteUrl(String centerNo) {
		return URL_BET_COMPLETE + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(13);
	}

	public static String getBetlistConfirmUrl(String centerNo) {
		return URL_BETLIST_CONFIRM + "?cid=" + centerNo + "&r=" + StringUtil.randomDigits(19);
	}
}

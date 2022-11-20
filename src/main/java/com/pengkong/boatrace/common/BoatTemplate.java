package com.pengkong.boatrace.common;

import com.pengkong.common.StringUtil;

/**
 * プロパティ
 * 
 * @author qwerty
 *
 */
public class BoatTemplate {
	public static final String CSV_DELIMITER = ",";
	public static final String ODDS_DELIMITER = "=";
	public static final String SETUDATA_DELIMITER = "/";

	public static final String HP_URL_BASE = "https://www.boatrace.jp";
	
	public static final String SCHEDULE_URL = "https://www.boatrace.jp/owpc/pc/race/gradesch?year={year}&hcd=01";
	/** 1. 特定日の節リスト */
	public static final String URL_DAILY_SETU_LIST = "https://www.boatrace.jp/owpc/pc/race/index?hd={yyyyMMdd}";
	/** 2. 節毎のレースリスト */
	public static final String URL_SETU_RACELIST = "https://www.boatrace.jp/owpc/pc/race/raceindex?jcd={jyoCd}&hd={yyyyMMdd}";
	/** 3. 直前情報 */
	public static final String URL_RACE_BEFOREINFO = "https://www.boatrace.jp/owpc/pc/race/beforeinfo?rno={raceNo}&jcd={jyoCd}&hd={yyyyMMdd}";
	/** 4. コンピュータ予想ページ */
	public static final String URL_COMPUTER_PREDICT = "https://www.boatrace.jp/owpc/pc/race/pcexpect?rno={raceNo}&jcd={jyoCd}&hd={yyyyMMdd}";
	/** 4. 出走表ページ */
	public static final String URL_RACE_PROGRAM = "https://www.boatrace.jp/owpc/pc/race/racelist?rno={raceNo}&jcd={jyoCd}&hd={yyyyMMdd}";
	/** 5. レース結果ページ */
	public static final String URL_RACE_RESULT = "https://www.boatrace.jp/owpc/pc/race/raceresult?rno={raceNo}&jcd={jyoCd}&hd={yyyyMMdd}";
	/** 6. オッズ情報ページ */
	public static final String URL_ODDS_BASE = "https://www.boatrace.jp/owpc/pc/race/odds{oddsType}?rno={raceNo}&jcd={jyoCd}&hd={yyyyMMdd}";
	
	/** 7. オッズ保管庫ページ 2018/10/14 http://www.orangebuoy.net/odds/?day=12&month=10&year=2018&jyo=2&r=1&mode=1 */
	public static final String URL_ODDSBANK_BASE = "http://www.orangebuoy.net/odds/?day={day}&month={month}&year={year}&jyo={jyo}&r={r}&mode={mode}";

	public static final String CSV_FILE_WEkA_PATTERNS = "weka_patterns.csv";
	
	/** オッズJSON取得 */
	public static final String URL_ODDS_REQUEST_JSON = "https://ib.mbrace.or.jp/tohyo-ap-pctohyo-web/bet/betinfo/oddsupdate/today?r=" + StringUtil.randomDigits(13);
	
	/** PC API user agent */
	public static final String BROWSER_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
	
}

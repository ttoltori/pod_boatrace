package com.pengkong.boatrace.common;

import java.util.HashMap;
import java.util.Map;

public class BoatConst {
	public static final int LEFT_PAD6 = 6;
	public static final int LEFT_PAD = 5;
	
	/** 転覆などの事故発生した選手のランク */
	public static final int RANK_OUT = 9;
	/** スタート展示でflying,lateした選手 */
	public static final float START_EXHIBIT_OUT = 9.9f;
	
	/** 節情報 */
	public static final int IDX_SETUINFO = 13;
	
	/** 枠別情報 */
	public static final int IDX_WAKULIST = 21;

	/** 枠別情報  複勝オッズ*/
	public static final int IDX_ODDS1F = 237;

	/** レース結果 */
	public static final int IDX_RESULT = 249;
	
	/** 順位算出 */
	public static final int IDX_CALCULATED = 280;
	
	/** ベッティング */
	public static final int IDX_COMPUTER_BET = 296;
	
	/** レース情報2 20180605 */
	public static final int IDX_RACEINFO2 = 299;
	
	/** 場単位マーティン継続 */
	public static final int MARTIN_CONTINUE_TYPE_JYO = 0;
	
	/** ユーザー単位マーティン継続 */
	public static final int MARTIN_CONTINUE_TYPE_USER = 1;
	
	/** 勝率区分：全国勝率 */
	public static final int RATE_TYPE_NATION = 1;
	/** 勝率区分：当地勝率 */
	public static final int RATE_TYPE_LOCAL = 2;
	/** 勝率区分：モータ勝率 */
	public static final int RATE_TYPE_MOTOR = 3;
	
	/** タイム区分：平均スタート */
	public static final int TIME_TYPE_AVG_START = 1;
	/** タイム区分：平均タイム */
	public static final int TIME_TYPE_AVG_TIME = 1;
	/** タイム区分：スタート */
	public static final int TIME_TYPE_START = 1;
	
	/** feature name変換map wekaを python */
	@SuppressWarnings("serial")
	public static Map<String, String> featureTypeMap = new HashMap<String, String>() {
		{
			put("numeric", "float");
			put("nominal", "category");
		}
	};

}

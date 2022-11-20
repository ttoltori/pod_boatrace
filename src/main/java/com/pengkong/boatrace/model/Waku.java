package com.pengkong.boatrace.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.common.StringUtil;

/**
 * 枠事の情報
 * 
 * @author qwerty
 *
 */
public class Waku {
	// 20180620
	public float basePoint = 0;
	public int powerPoint = 0;
	
	public int no;
	public String entry;
	public String name;
	// 20180708;
	public String sex; // M=男 F=女
	public int age;
	public String level;
	public float weight;
	public String branch;
	public String born;
	public int mark;
	/** 展示タイム */
	public float exhibitTime;
	/** スタート展示 */
	public float startExhibit;
	/** チルト */
	public float tilt;
	/** プロペラ */
	public String propela;
	/** 部品交換 */
	public String partChange;
	public int flying;
	public int late;
	/** 平均スタート */
	public float averageStart;
	public float nationWiningRate;
	public float nation2WiningRate;
	public float nation3WiningRate;
	public float localWiningRate;
	public float local2WiningRate;
	public float local3WiningRate;
	public String motorNo;
	public float motor2WiningRate;
	public float motor3WiningRate;
	public String boatNo;
	public float boat2WiningRate;
	public float boat3WiningRate;
	/** 今節進入コース一覧 */
	public String setuEnter;
	/** 今節スタートタイミング一覧 */
	public String setuStart;
	/** 今節成績一覧 */
	public String setuRank;
	/** 結果スタート成績 */
	public float resultStart;
	/** 結果着順 */
	public int resulRank;
	public String soken;
	
	/** 単勝のオッズ */
	public float oddsValue1T;
	/** 複勝オッズ 20180415 */
	public Float[] oddsValue1F;
	
	public static String toCsv(List<Waku> wakulist) {
		String[] entryArr = new String[6];
		String[] nameArr = new String[6];
		String[] sexArr = new String[6];
		String[] ageArr = new String[6];
		String[] levelArr = new String[6];
		String[] weightArr = new String[6];
		String[] branchArr = new String[6];
		String[] bornArr = new String[6];
		String[] markArr = new String[6];
		String[] exhibitTimeArr = new String[6];
		String[] startExhibitArr = new String[6];
		String[] tiltArr = new String[6];
		String[] propelaArr = new String[6];
		String[] partChangeArr = new String[6];
		String[] flyingArr = new String[6];
		String[] lateArr = new String[6];
		String[] averaeStartArr = new String[6];
		String[] nationWiningRateArr = new String[6];
		String[] nation2WiningRateArr = new String[6];
		String[] nation3WiningRateArr = new String[6];
		String[] localWiningRateArr = new String[6];
		String[] local2WiningRateArr = new String[6];
		String[] local3WiningRateArr = new String[6];
		String[] motorNoArr = new String[6];
		String[] motor2WiningRateArr = new String[6];
		String[] motor3WiningRateArr = new String[6];
		String[] boatNoArr = new String[6];
		String[] boat2WiningRateArr = new String[6];
		String[] boat3WiningRateArr = new String[6];
		String[] setuEnterArr = new String[6];
		String[] setuStartArr = new String[6];
		String[] setuRankArr = new String[6];
		String[] resultStartArr = new String[6];
		String[] resulRankArr = new String[6];
		String[] sokenArr = new String[6];
		String[] odds1TArr = new String[6];
		
		String[][] odds1FArr = new String[6][2];
		
		for (int i = 0; i < 6; i++) {
			Waku waku = wakulist.get(i);
			entryArr[i] = waku.entry;
			nameArr[i] = waku.name;
			sexArr[i] = waku.sex;
			ageArr[i] = String.valueOf(waku.age);
			levelArr[i] = waku.level;
			weightArr[i] = String.valueOf(waku.weight);
			branchArr[i] = waku.branch;
			bornArr[i] = waku.born;
			markArr[i] = String.valueOf(waku.mark);
			exhibitTimeArr[i] = String.valueOf(waku.exhibitTime);
			startExhibitArr[i] = String.valueOf(waku.startExhibit);
			tiltArr[i] = String.valueOf(waku.tilt);
			propelaArr[i] = waku.propela;
			partChangeArr[i] = waku.partChange;
			flyingArr[i] = String.valueOf(waku.flying);
			lateArr[i] = String.valueOf(waku.late);
			averaeStartArr[i] = String.valueOf(waku.averageStart);
			nationWiningRateArr[i] = String.valueOf(waku.nationWiningRate);
			nation2WiningRateArr[i] = String.valueOf(waku.nation2WiningRate);
			nation3WiningRateArr[i] = String.valueOf(waku.nation3WiningRate);
			localWiningRateArr[i] = String.valueOf(waku.localWiningRate);
			local2WiningRateArr[i] = String.valueOf(waku.local2WiningRate);
			local3WiningRateArr[i] = String.valueOf(waku.local3WiningRate);
			motorNoArr[i] = waku.motorNo;
			motor2WiningRateArr[i] = String.valueOf(waku.motor2WiningRate);
			motor3WiningRateArr[i] = String.valueOf(waku.motor3WiningRate);
			boatNoArr[i] = waku.boatNo;
			boat2WiningRateArr[i] = String.valueOf(waku.boat2WiningRate);
			boat3WiningRateArr[i] = String.valueOf(waku.boat3WiningRate);
			setuEnterArr[i] = waku.setuEnter;
			setuStartArr[i] = waku.setuStart;
			setuRankArr[i] = waku.setuRank;
			resultStartArr[i] = String.valueOf(waku.resultStart);
			resulRankArr[i] = String.valueOf(waku.resulRank);
			sokenArr[i] = waku.soken;
			odds1TArr[i] = String.valueOf(waku.oddsValue1T);
			odds1FArr[i][0] = String.valueOf(waku.oddsValue1F[0]);
			odds1FArr[i][1] = String.valueOf(waku.oddsValue1F[1]);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, entryArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, nameArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, sexArr)); // 20180708追加
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, ageArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, levelArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, weightArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, branchArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, bornArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, markArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, exhibitTimeArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, startExhibitArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, tiltArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, propelaArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, partChangeArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, flyingArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, lateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, averaeStartArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, nationWiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, nation2WiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, nation3WiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, localWiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, local2WiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, local3WiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, motorNoArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, motor2WiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, motor3WiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, boatNoArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, boat2WiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, boat3WiningRateArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, setuEnterArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, setuStartArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, setuRankArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, resultStartArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, resulRankArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, sokenArr));
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(String.join(BoatTemplate.CSV_DELIMITER, odds1TArr));
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 2; j++) {
				sb.append(BoatTemplate.CSV_DELIMITER);
				sb.append(odds1FArr[i][j]);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 出走表の節内STデータから平均を取得する
	 * @param val STの平均
	 * @return 平均ST=データがない, 0以外=節内STの平均値
	 */
	public static float getSetuAverageStart(Waku waku) {
		String[] token = waku.setuStart.split(BoatTemplate.SETUDATA_DELIMITER);
		int cnt = 0;
		float ret = 0f;
		for (String st : token) {
			if (!StringUtil.isEmpty(st)) {
				ret += Float.parseFloat(st);
				cnt++;
			}
		}
		
		if (cnt > 0) {
			ret = ret / cnt;
		} else {
			ret = waku.averageStart;
		}
		
		return ret;	
	}
	
	/**
	 * 出走表の節内着順データから平均を取得する
	 * @param val
	 * @return４=データがない, 0以外=平均値
	 */
	public static short getSetuAvgWin(Waku waku) {
		short ret = 4;
		
		String[] token = waku.setuRank.split("");
		short cnt = 0;
		for (String win : token) {
			if (!StringUtil.isEmpty(convertSetuRankData(win))) {
				ret += Short.parseShort(win);
				cnt++;
			}
		}
		
		if (cnt > 0) {
			ret = (short)(ret / cnt);
		}
		
		return ret;
	}
		
	private static String convertSetuRankData(String val) {
		String result = val;
		if (val.equals(" ")) {
			result = "";
		} else if (val.equals("転")) {
			result = "";
		} else if (val.equals("Ｆ")) {
			result = "";
		} else if (val.equals("Ｌ")) {
			result = "";
		} else if (val.equals("エ")) {
			result = "";
		} else if (val.equals("転")) {
			result = "";
		} else if (val.equals("沈")) {
			result = "";
		} else if (val.equals("妨")) {
			result = "";
		} else if (val.equals("不")) {
			result = "";
		} else if (val.equals("落")) {
			result = "";
		} else if (val.equals("欠")) {
			result = "";
		} else if (val.equals("失")) {
			result = "";
		} else if (val.equals("＿")) {
			result = "";
		}
		
		return result;
	}	
}

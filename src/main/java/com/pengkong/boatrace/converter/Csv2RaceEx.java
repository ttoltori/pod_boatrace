package com.pengkong.boatrace.converter;

import java.util.Arrays;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.scraping.parser.StatRankCalculator;
import com.pengkong.boatrace.util.BoatUtil;
import com.pengkong.common.StringUtil;

public class Csv2RaceEx {
	public static RaceEx convertOnlyRaceInfo(String csvLine) {
		RaceEx race = new RaceEx();
		String[] token = csvLine.split(BoatTemplate.CSV_DELIMITER);

		race.raceInfo.year = BoatUtil.toInt(token[0]);
		race.raceInfo.month = BoatUtil.toInt(token[1]);
		race.raceInfo.day = BoatUtil.toInt(token[2]);
		race.raceInfo.ymd = token[3];
		race.setu.jyoCd = token[BoatConst.IDX_SETUINFO + 5];
		race.raceInfo.no = BoatUtil.toInt(token[5]);

		return race;
	}
	
	public static RaceEx convertAndModifyForStatistics(String csvLine) {
		RaceEx race = convert(csvLine);
		
		// 20181125 勝率が０の場合統計に影響を最小化するために末端値に書き換える　例）0 -> 4.0  (4.0,5.4, ...)
		// BoatUtil.modifyWiningRate(race.wakuList);
		
		// 20181125 平均STが０の場合統計に影響を最小化するために末端値に書き換える　例）0 -> 0.8  (0.8,0.3, ..., 0.1)
		BoatUtil.modifyAverageStart(race.wakuList);
		
		// 順位を再設定する
		race = new StatRankCalculator().calculateAndSetEx(race);
		
		return race;
	}
	
	public static RaceEx convert(String csvLine) {
		RaceEx race = new RaceEx();
		String[] token = csvLine.split(BoatTemplate.CSV_DELIMITER);
		int tokenLength = token.length;

		race.raceInfo.year = BoatUtil.toInt(token[0]);
		race.raceInfo.month = BoatUtil.toInt(token[1]);
		race.raceInfo.day = BoatUtil.toInt(token[2]);
		race.raceInfo.ymd = token[3];
		race.raceInfo.hh = BoatUtil.toInt(token[4]);
		race.raceInfo.no = BoatUtil.toInt(token[5]);
		race.raceInfo.sime = BoatUtil.toInt(token[6]);
		race.raceInfo.temparature = BoatUtil.toFloat(token[7]);
		race.raceInfo.weather = token[8];
		race.raceInfo.windDirection = token[9];
		race.raceInfo.wind = BoatUtil.toFloat(token[10]);
		race.raceInfo.waterTemparature = BoatUtil.toFloat(token[11]);
		race.raceInfo.wave = BoatUtil.toInt(token[12]);
		race.setu.name = token[BoatConst.IDX_SETUINFO];
		race.setu.grade = token[BoatConst.IDX_SETUINFO + 1];
		race.setu.isVenus = token[BoatConst.IDX_SETUINFO + 2];
		race.setu.timezone = token[BoatConst.IDX_SETUINFO + 3];
		race.setu.turn = token[BoatConst.IDX_SETUINFO + 4];
		race.setu.jyoCd = token[BoatConst.IDX_SETUINFO + 5];
		race.setu.jyo = token[BoatConst.IDX_SETUINFO + 6];
		race.setu.winner = token[BoatConst.IDX_SETUINFO + 7];

		race.wakuList = Arrays.asList(new Waku(), new Waku(), new Waku(), new Waku(), new Waku(), new Waku());
		for (int i = 0; i < 6; i++) {
			race.wakuList.get(i).no = i + 1;
			race.wakuList.get(i).entry = token[BoatConst.IDX_WAKULIST + i];
			race.wakuList.get(i).name = token[BoatConst.IDX_WAKULIST + (6 * 1) + i];
			race.wakuList.get(i).sex = token[BoatConst.IDX_WAKULIST + (6 * 2) + i]; // 20180708追加
			race.wakuList.get(i).age = BoatUtil.toInt(token[BoatConst.IDX_WAKULIST + (6 * 3) + i]);
			race.wakuList.get(i).level = token[BoatConst.IDX_WAKULIST + (6 * 4) + i];
			race.wakuList.get(i).weight = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 5) + i]);
			race.wakuList.get(i).branch = token[BoatConst.IDX_WAKULIST + (6 * 6) + i];
			race.wakuList.get(i).born = token[BoatConst.IDX_WAKULIST + (6 * 7) + i];
			race.wakuList.get(i).mark = BoatUtil.toInt(token[BoatConst.IDX_WAKULIST + (6 * 8) + i]);
			race.wakuList.get(i).exhibitTime = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 9) + i]);
			race.wakuList.get(i).startExhibit = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 10) + i]);
			race.wakuList.get(i).tilt = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 11) + i]);
			race.wakuList.get(i).propela = token[BoatConst.IDX_WAKULIST + (6 * 12) + i];
			race.wakuList.get(i).partChange = token[BoatConst.IDX_WAKULIST + (6 * 13) + i];
			race.wakuList.get(i).flying = BoatUtil.toInt(token[BoatConst.IDX_WAKULIST + (6 * 14) + i]);
			race.wakuList.get(i).late = BoatUtil.toInt(token[BoatConst.IDX_WAKULIST + (6 * 15) + i]);
			race.wakuList.get(i).averageStart = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 16) + i]);
			race.wakuList.get(i).nationWiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 17) + i]);
			race.wakuList.get(i).nation2WiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 18) + i]);
			race.wakuList.get(i).nation3WiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 19) + i]);
			race.wakuList.get(i).localWiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 20) + i]);
			race.wakuList.get(i).local2WiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 21) + i]);
			race.wakuList.get(i).local3WiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 22) + i]);
			race.wakuList.get(i).motorNo = token[BoatConst.IDX_WAKULIST + (6 * 23) + i];
			race.wakuList.get(i).motor2WiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 24) + i]);
			race.wakuList.get(i).motor3WiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 25) + i]);
			race.wakuList.get(i).boatNo = token[BoatConst.IDX_WAKULIST + (6 * 26) + i];
			race.wakuList.get(i).boat2WiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 27) + i]);
			race.wakuList.get(i).boat3WiningRate = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 28) + i]);
			race.wakuList.get(i).setuEnter = token[BoatConst.IDX_WAKULIST + (6 * 29) + i];
			race.wakuList.get(i).setuStart = token[BoatConst.IDX_WAKULIST + (6 * 30) + i];
			race.wakuList.get(i).setuRank = token[BoatConst.IDX_WAKULIST + (6 * 31) + i];
			race.wakuList.get(i).resultStart = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 32) + i]);
			race.wakuList.get(i).resulRank = BoatUtil.toInt(token[BoatConst.IDX_WAKULIST + (6 * 33) + i]);
			race.wakuList.get(i).soken = token[BoatConst.IDX_WAKULIST + (6 * 34) + i];
			race.wakuList.get(i).oddsValue1T = BoatUtil.toFloat(token[BoatConst.IDX_WAKULIST + (6 * 35) + i]);

			// 20180415
			Float[] values = new Float[2];
			values[0] = BoatUtil.toFloat(token[BoatConst.IDX_ODDS1F + (i * 2)]);
			values[1] = BoatUtil.toFloat(token[BoatConst.IDX_ODDS1F + (i * 2) + 1]);
			race.wakuList.get(i).oddsValue1F = values;
		}
		
		race.raceResult.sanrentanNo = token[BoatConst.IDX_RESULT];
		race.raceResult.sanrentanPrize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 1]);
		race.raceResult.sanrentanPopular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 2]);
		race.raceResult.sanrenhukuNo = token[BoatConst.IDX_RESULT + 3];
		race.raceResult.sanrenhukuPrize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 4]);
		race.raceResult.sanrenhukuPopular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 5]);
		race.raceResult.nirentanNo = token[BoatConst.IDX_RESULT + 6];
		race.raceResult.nirentanPrize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 7]);
		race.raceResult.nirentanPopular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 8]);
		race.raceResult.nirenhukuNo = token[BoatConst.IDX_RESULT + 9];
		race.raceResult.nirenhukuPrize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 10]);
		race.raceResult.nirenhukuPopular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 11]);
		race.raceResult.tansyoNo = token[BoatConst.IDX_RESULT + 12];
		race.raceResult.tansyoPrize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 13]);
		race.raceResult.tansyoPopular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 14]);
		race.raceResult.hukusyo1No = token[BoatConst.IDX_RESULT + 15];
		race.raceResult.hukusyo1Prize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 16]);
		race.raceResult.hukusyo1Popular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 17]);
		race.raceResult.hukusyo2No = token[BoatConst.IDX_RESULT + 18];
		race.raceResult.hukusyo2Prize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 19]);
		race.raceResult.hukusyo2Popular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 20]);
		race.raceResult.kakuren1No = token[BoatConst.IDX_RESULT + 21];
		race.raceResult.kakuren1Prize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 22]);
		race.raceResult.kakuren1Popular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 23]);
		race.raceResult.kakuren2No = token[BoatConst.IDX_RESULT + 24];
		race.raceResult.kakuren2Prize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 25]);
		race.raceResult.kakuren2Popular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 26]);
		race.raceResult.kakuren3No = token[BoatConst.IDX_RESULT + 27];
		race.raceResult.kakuren3Prize = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 28]);
		race.raceResult.kakuren3Popular = BoatUtil.toInt(token[BoatConst.IDX_RESULT + 29]);
		race.raceResult.kimariTe = token[BoatConst.IDX_RESULT + 30];

		race.calculatedInfo.wakuRank = token[BoatConst.IDX_CALCULATED + 0];
		race.calculatedInfo.levelRank = token[BoatConst.IDX_CALCULATED + 1];
		race.calculatedInfo.resultLevelRank = token[BoatConst.IDX_CALCULATED + 2];
		race.calculatedInfo.nationWiningRank = token[BoatConst.IDX_CALCULATED + 3];
		race.calculatedInfo.nation2WiningRank = token[BoatConst.IDX_CALCULATED + 4];
		race.calculatedInfo.nation3WiningRank = token[BoatConst.IDX_CALCULATED + 5];
		race.calculatedInfo.localWiningRank = token[BoatConst.IDX_CALCULATED + 6];
		race.calculatedInfo.local2WiningRank = token[BoatConst.IDX_CALCULATED + 7];
		race.calculatedInfo.local3WiningRank = token[BoatConst.IDX_CALCULATED + 8];
		race.calculatedInfo.motor2Rank = token[BoatConst.IDX_CALCULATED + 9];
		race.calculatedInfo.motor3Rank = token[BoatConst.IDX_CALCULATED + 10];
		race.calculatedInfo.boat2Rank = token[BoatConst.IDX_CALCULATED + 11];
		race.calculatedInfo.boat3Rank = token[BoatConst.IDX_CALCULATED + 12];
		race.calculatedInfo.startExhibitRank = token[BoatConst.IDX_CALCULATED + 13];
		race.calculatedInfo.exhibitRank = token[BoatConst.IDX_CALCULATED + 14];
		race.calculatedInfo.averageStartRank = token[BoatConst.IDX_CALCULATED + 15];

		race.computerBetting.predict = token[BoatConst.IDX_COMPUTER_BET];
		// 2021/7/21 delimiterは削除する 例）4-3-1-2  → 4312
		if (!StringUtil.isEmpty(race.computerBetting.predict)) {
			race.computerBetting.predict = race.computerBetting.predict.replaceAll("-", ""); 
		}

		race.computerBetting.confidence = BoatUtil.toInt(token[BoatConst.IDX_COMPUTER_BET + 1]);
		race.computerBetting.betYn = (tokenLength > BoatConst.IDX_COMPUTER_BET + 2)
				? token[BoatConst.IDX_COMPUTER_BET + 2]
				: "N";
		race.sortTime = race.raceInfo.sime * 100;
		
		// 20180605 追加 
		race.raceInfo2.fixedEntrance = token[BoatConst.IDX_RACEINFO2];
		race.raceInfo2.raceType = token[BoatConst.IDX_RACEINFO2 + 1];
		race.raceInfo2.wakuLevelList = token[BoatConst.IDX_RACEINFO2 + 2];
		race.raceInfo2.aLevelCount = BoatUtil.toInt(token[BoatConst.IDX_RACEINFO2 + 3]);
		// 20180605 追加
		race.raceInfo2.femaleCount = BoatUtil.toInt(token[BoatConst.IDX_RACEINFO2 + 4]); // 20180708 追加
		
		// 20181118追加
		// csvに節順位が保存されている場合
		if (token.length > (BoatConst.IDX_RACEINFO2 + 5)) {
			race.raceInfo2.avgstCondRank = token[BoatConst.IDX_RACEINFO2 + 5]; // 20181118 追加
			race.raceInfo2.setuwinRank = token[BoatConst.IDX_RACEINFO2 + 6]; // 20181118 追加
			race.raceInfo2.flRank = token[BoatConst.IDX_RACEINFO2 + 7]; // 20181118 追加
		} else {
			// 節順位を再設定する
			race = new StatRankCalculator().calculateAndSetEx2(race);
		}

		return race;
	}
}

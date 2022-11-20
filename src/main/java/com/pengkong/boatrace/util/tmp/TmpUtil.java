package com.pengkong.boatrace.util.tmp;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.converter.Csv2RaceEx;
import com.pengkong.boatrace.model.Odds;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.Schedule;
import com.pengkong.boatrace.scraping.parser.OddsKFPageParser;
import com.pengkong.boatrace.scraping.parser.RaceListPageParser;
import com.pengkong.boatrace.scraping.parser.RaceProgramPageParser;
import com.pengkong.boatrace.scraping.parser.RaceResultPageParser;
import com.pengkong.boatrace.scraping.parser.SetuListPageParser;
import com.pengkong.boatrace.service.manager.ResultOddsManager;
import com.pengkong.boatrace.service.manager.ScheduleManagger;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class TmpUtil {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	PropertyUtil prop = PropertyUtil.getInstance();
	/**
	 * レースファイルに時間、場コード、単勝オッズ一覧、複勝オッズ一覧、単勝オッズ人気、複勝オッズ人気を追加して 再保存する。
	 */
	public void racefileUpgrade() {

	}
	
	/**
	 * 勝率0を末端値へ、平均ST０を末端値へ書き換えたため関連順位を修正する
	 * 20181125
	 * @throws Exception
	 */
	public void addRanks2(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		TmpRankCalculator6 tmpRankCalculator = new TmpRankCalculator6();
		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			String filepath = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}
			
			String fname = file.getName();
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				RaceEx race = Csv2RaceEx.convert(line);
				race = tmpRankCalculator.calculateAndSetEx(race);
				
				filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/test/csvfixed/" + fname;
				FileUtil.appendFileByLine(filepath, race.toCsv());
			}
			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
		
	}
		
	/**
	 * 平均STコンディションランク、節着順ランク、罰点ランクをCSVに追加する
	 * 20181118
	 * @throws Exception
	 */
	public void addRanks(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		TmpRankCalculator5 tmpRankCalculator = new TmpRankCalculator5();
		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			String filepath = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}
			
			String fname = file.getName();
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				RaceEx race = Csv2RaceEx.convert(line);
				race = tmpRankCalculator.calculateAndSetEx(race);
				
				filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/test/csvfixed/" + fname;
				FileUtil.appendFileByLine(filepath, race.toCsv());
			}
			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
		
	}
	
	/**
	 * 平均ST、平均タイムの順位をCSVに追加する
	 * 20180903 
	 * @throws Exception
	 */
	public void addAvgStartAvgTimeRank(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		TmpRankCalculator4 tmpRankCalculator = new TmpRankCalculator4();
		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			String filepath = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}
			
			String fname = file.getName();
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				RaceEx race = Csv2RaceEx.convert(line);
				race = tmpRankCalculator.calculateAndSetEx(race);
				
				filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/test/csvfixed/" + fname;
				FileUtil.appendFileByLine(filepath, race.toCsv());
			}
			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
		
	}
	
	
	/**
	 * ランク一覧の内容がすべて間違っていたため再計算して設定する。(順位ソートする前にWakuを再追加する）
	 * 20180729 
	 * @throws Exception
	 */
	public void modifyAllRanks(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		TmpRankCalculator3 tmpRankCalculator = new TmpRankCalculator3();
		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			String filepath = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}
			
			String fname = file.getName();
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				RaceEx race = Csv2RaceEx.convert(line);
				race = tmpRankCalculator.calculateAndSetEx(race);
				
				filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/test/csvfixed/" + fname;
				FileUtil.appendFileByLine(filepath, race.toCsv());
			}
			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
		
	}

	/**
	 * 拡連複オッズを追加する
	 * 
	 * @throws Exception
	 */
	public void oddsBugFix() throws Exception {
		OddsKFPageParser parser = new OddsKFPageParser();

		File dir = new File(prop.getString("DIRECTORY_ODDS"));
		File[] files = dir.listFiles();
		for (File file : files) {
			if (!file.isFile()) {
				continue;
			}
			String fname = file.getName();
			String[] ftoken = fname.split("\\.");
			String ymd = ftoken[0].replace("odds_", "");
			String filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			String curJyoRace = "";
			for (String line : lines) {
				String[] token = line.split(",");
				String lineJyoRace = String.join("", token[0], token[1], token[2]);
				if (!curJyoRace.equals(lineJyoRace)) {
					String url = BoatTemplate.URL_ODDS_BASE.replace("{raceNo}", token[2]).replace("{jyoCd}", token[1])
							.replace("{yyyyMMdd}", ymd);
					url = url.replace("{oddsType}", "k");
					String prefix = ymd + "," + token[1] + "," + token[2] + "," + "KF" + ",";
					String csv = prefix + Odds.toCsvFloats(parser.parse(url));
					FileUtil.appendFileByLine(filepath, csv);
					System.out.println(csv);
					curJyoRace = lineJyoRace;
				}
			}
		}
	}

	/**
	 * 拡連複レース結果を追加する
	 * 
	 * @throws Exception
	 */
	public void addKakurenResult(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		RaceResultPageParser raceResultPageParser = new RaceResultPageParser();
		TmpRankCalculator tmpRankCalculator = new TmpRankCalculator();

		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			String filepath = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			String fname = file.getName();
			ResultOddsManager.getInstance().load(yyyyMMdd);
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				RaceEx race = Csv2RaceEx.convert(line);
				String url = BoatTemplate.URL_RACE_RESULT.replace("{raceNo}", String.valueOf(race.raceInfo.no))
						.replace("{jyoCd}", race.setu.jyoCd).replace("{yyyyMMdd}", race.raceInfo.ymd);
				race.raceResult = raceResultPageParser.parseAndSet(race.raceResult, race.wakuList, url);
				tmpRankCalculator.calculateAndSet(race);
				filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/test/csv2/" + fname;
				FileUtil.appendFileByLine(filepath, race.toCsv());
			}
			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
	}

	/**
	 * 全国勝率ランク一覧の内容を修正。 20180608 
	 * @throws Exception
	 */
	public void modifyNationWiningRank(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		TmpRankCalculator2 tmpRankCalculator = new TmpRankCalculator2();
		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			String filepath = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}
			
			String fname = file.getName();
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				RaceEx race = Csv2RaceEx.convert(line);
				race = tmpRankCalculator.calculateAndSet(race);
				
				filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/test/csv2/" + fname;
				FileUtil.appendFileByLine(filepath, race.toCsv());
			}
			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
		
	}
	
	/**
	 * 進入固定、レース番組タイプ、枠順級一覧、A級選手数 追加
	 * @throws Exception
	 */
	public void addFixedEntranceEtc(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		RaceProgramPageParser parser = new RaceProgramPageParser();

		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			String filepath = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}
			
			String fname = file.getName();
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				RaceEx race = Csv2RaceEx.convert(line);
				String url = BoatTemplate.URL_RACE_PROGRAM.replace("{raceNo}", String.valueOf(race.raceInfo.no))
						.replace("{jyoCd}", race.setu.jyoCd).replace("{yyyyMMdd}", race.raceInfo.ymd);

				race.raceInfo2 = parser.parseRaceInfo2(race, url);
				
				filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/test/csv2/" + fname;
				FileUtil.appendFileByLine(filepath, race.toCsv());
			}
			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
		
	}
	
	public void digestRaceResult() throws Exception {
		File dir = new File(prop.getString("DIRECTORY_CSV"));
		File[] files = dir.listFiles();
		String filepath;
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			if (!file.isFile()) {
				continue;
			}
			String fname = file.getName();
			if (!fname.contains("race")) {
				continue;
			}
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				String[] t = line.split(",");
				String setu = getSetuNumber(t[17]);
				if (setu == null)
					continue;
				
				String[] token = t[276].split("-");
				String strLevelList = "";
				for (int i=0; i < token.length; i++) {
					strLevelList += token[i].substring(0,1);
				}
				if (strLevelList.contains("X")) {
					continue;
				}
				sb.append(t[18] + "_" + setu + "_" + strLevelList);
				sb.append(",");
				sb.append(t[18]);
				sb.append(",");
				sb.append(t[19]);
				sb.append(",");
				sb.append(t[14]);
				sb.append(",");
				sb.append(t[17]);
				sb.append(",");
				sb.append(t[3]);
				sb.append(",");
				sb.append(t[4]);
				sb.append(",");
				sb.append(t[5]);
				sb.append(",");

				// 着順
				String[] tmp = { "", "", "", "", "", "" };
				String[] tw = t[274].split("");
				for (int i = 0; i < tw.length; i++) {
					tmp[i] = tw[i];
				}
				for (int i = 0; i < tmp.length; i++) {
					sb.append(tmp[i]);
					sb.append(",");
				}

				// A級のinout密集度
				float coherency = 0f;
				int aCount = 0;
				for (int i = 0; i < 6; i++) {
					if (t[39 + i].contains("A")) {
						coherency += (i + 1);
						aCount++;
					}
				}
				if (aCount == 0) {
					coherency = 0f;
				} else {
					coherency = coherency / (float) aCount;
				}
				sb.append(coherency);
				sb.append(",");

				// 成績
				for (int i = 243; i <= 254; i++) {
					sb.append(t[i]);
					sb.append(",");
				}
				sb.append(t[273]);
				sb.append(",");
				sb.append(strLevelList);
				sb.append(",");
				sb.append(t[276]);
				sb.append(System.lineSeparator());
			}

			System.out.println(fname);
		}
		filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/resources/race_result_digest.csv";
		FileUtil.appendFileByLine(filepath, sb.toString());
	}
	
	public void digestRaceResult2() throws Exception {
		File dir = new File(prop.getString("DIRECTORY_CSV"));
		File[] files = dir.listFiles();
		String filepath;
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			if (!file.isFile()) {
				continue;
			}
			String fname = file.getName();
			if (!fname.contains("race")) {
				continue;
			}
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				String[] t = line.split(",");
				
				t[13] = "";
				t[17] = getSetuNumber(t[17]);
				
				//プロペラ、部品交換
				for( int i = 87; i <= 98; i++) {
					t[i] = "";
				}
				
				String strLevelList = "";
				String[] token = t[276].split("-");
				for (int i=0; i < token.length; i++) {
					strLevelList += token[i].substring(0,1);
				}
				if (strLevelList.contains("X")) {
					continue;
				}
				
				for (int i=0; i <= 224; i++) {
					sb.append(t[i]);
					sb.append(",");
				}
				for (int i=243; i <= 288; i++) {
					sb.append(t[i]);
					sb.append(",");
				}
				sb.append(strLevelList);
				sb.append(System.lineSeparator());
			}

			System.out.println(fname);
		}
		filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/resources/race_all.csv";
		FileUtil.appendFileByLine(filepath, sb.toString());
	}

	public void oddsTest() throws Exception{
		Gson gson = new Gson();
		JsonObject jo = gson.fromJson(new FileReader("C:/Dev/workspace/Oxygen/pod_boatrace/resources/api/odds_data.txt"), JsonObject.class);
		JsonObject oddsDetailListByKachishiki = jo.getAsJsonObject("oddsDetailListByKachishiki");
		JsonArray arrJo = oddsDetailListByKachishiki.getAsJsonArray("6");
		for (JsonElement je : arrJo) {
			com.pengkong.boatrace.model.json.OddsJson odds = gson.fromJson(je, com.pengkong.boatrace.model.json.OddsJson.class);
			System.out.println(odds);	
		}
	}
	
	/**
	 * schedule.csvファイルを再生成する。女子戦判断が間違っていたため
	 * @param fromYmd
	 * @param toYmd
	 */
	public void rebuildScheduleCsv(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);
		SetuListPageParser parser = new SetuListPageParser();

		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);
			String urlDailySetuList = BoatTemplate.URL_DAILY_SETU_LIST.replace("{yyyyMMdd}", yyyyMMdd);
			// 節一覧取得
			parser.parse(urlDailySetuList, yyyyMMdd, false);

			System.out.println(yyyyMMdd);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
		
	}
	
	/**
	 * 女子選手判定を追加(Waku.sex)
	 * @throws Exception
	 */
	public void bugfixWakuSex(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);

		HashMap<String, Race> mapRaces = null;
		while (currDate.compareTo(toDate) <= 0) {
			mapRaces = new HashMap<>();
			String yyyyMMdd = sdf.format(currDate);

			String filepath = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}
			
			String fname = file.getName();
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				RaceEx race = Csv2RaceEx.convert(line);
				
				String key = yyyyMMdd + race.getJyoCd() + race.getRaceNo();
				if (!mapRaces.containsKey(key)) {
					String jyoCd = race.getJyoCd();
					String urlSetuRaceList = BoatTemplate.URL_SETU_RACELIST.replace("{jyoCd}", jyoCd).replace("{yyyyMMdd}",
							yyyyMMdd);
					// レース一覧取得
					List<Race> raceList = new RaceListPageParser().parse(urlSetuRaceList);
					for (Race ra : raceList) {
						String keyTmp = yyyyMMdd + jyoCd + String.valueOf(ra.raceInfo.no);
						mapRaces.put(keyTmp, ra);
					}
				}
				Race raceTmp = mapRaces.get(key);

				int femaleCount = 0;
				for (int i = 0; i < 6; i++) {
					race.wakuList.get(i).sex = raceTmp.raceInfo.sexList.get(i);
					if (raceTmp.raceInfo.sexList.get(i).equals("F")) {
						femaleCount++;
					}
				}
				race.raceInfo2.femaleCount = femaleCount;
				
				filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/test/csvfixed/" + fname;
				FileUtil.appendFileByLine(filepath, race.toCsv());
			}
			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
	}

	/**
	 * 女子戦判定(isVenus)の誤りを修正
	 * @throws Exception
	 */
	public void bugfixIsVenus(String fromYmd, String toYmd) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(fromYmd));
		Date currDate = calendar.getTime();
		Date toDate = sdf.parse(toYmd);

		while (currDate.compareTo(toDate) <= 0) {
			String yyyyMMdd = sdf.format(currDate);

			String filepath = prop.getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
			File file = new File(filepath);
			if (!file.exists()) {
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
				continue;
			}
			
			String fname = file.getName();
			filepath = file.getPath();
			List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
			for (String line : lines) {
				RaceEx race = Csv2RaceEx.convert(line);
				Schedule schedule = ScheduleManagger.getInstance().find(race.setu.jyo, yyyyMMdd);
				if (schedule != null) {
					race.setu.isVenus = schedule.isVenus;
				}
				filepath = "C:/Dev/workspace/Oxygen/pod_boatrace/test/csvfixed/" + fname;
				FileUtil.appendFileByLine(filepath, race.toCsv());
			}
			System.out.println(fname);

			// 1日増加
			calendar.add(Calendar.DATE, 1);
			currDate = calendar.getTime();
		}
	}
	
	private String getSetuNumber(String setu) {
		if (setu.equals("初日")) {
			return "1";
		} else if (setu.equals("２日目")) {
			return "2";
		} else if (setu.equals("３日目")) {
			return "3";
		} else if (setu.equals("４日目")) {
			return "4";
		} else if (setu.equals("５日目")) {
			return "5";
		} else if (setu.equals("最終日")) {
			return "6";
		} else {
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			//BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_digest.properties");
			
			//20181118
			new TmpUtil().addRanks(args[0], args[1]);
			
			//20180903
			//new TmpUtil().addAvgStartAvgTimeRank(args[0], args[1]);
			
//			if (args[0].equals("4")) {
//				new TmpUtil().modifyAllRanks(args[1], args[2]);
//			} else if (args[0].equals("3")) {
//				new TmpUtil().bugfixWakuSex(args[1], args[2]);
//			} else if (args[0].equals("2")) {
//				new TmpUtil().bugfixIsVenus(args[1], args[2]);
//			} else if (args[0].equals("1")) {
//				new TmpUtil().rebuildScheduleCsv(args[1], args[2]);
//			}
			
			//new TmpUtil().modifyNationWiningRank(args[0], args[1]);
			//new TmpUtil().addFixedEntranceEtc(args[0], args[1]);
			//System.out.println((int) (Math.ceil(176 / 10) * 10));
			//new TmpUtil().oddsTest();
			//new TmpUtil().digestRaceResult2();
			// new TmpUtil().addKakurenResult(args[0], args[1]);
			// new TmpUtil().addKakurenResult("20170702", "20170703");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

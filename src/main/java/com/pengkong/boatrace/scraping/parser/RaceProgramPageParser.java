package com.pengkong.boatrace.scraping.parser;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.model.ComputerBetting;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.RaceInfo2;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.util.BoatUtil;
/**
 * 出走表ページ解析
 * @author qwerty
 *
 */
public class RaceProgramPageParser {

	Logger logger = LoggerFactory.getLogger(RaceProgramPageParser.class);

	public RaceEx parseAndSetInfoEx(RaceEx race, String url) throws Exception {
		logger.debug("url=" + url);
		Document doc = Jsoup.connect(url).get();
		race.wakuList = parseWakuList(doc);
		// 20180708 性別追加
		for (int i=0; i < 6; i++) {
			race.wakuList.get(i).sex = race.raceInfo.sexList.get(i); 
		}
		
		race.computerBetting = parseComputerBetting(doc);
		// 20180605追加
		race.raceInfo2 = parseRaceInfo2(doc, race.wakuList);

		doc = null;
		return race;
	}
	
	public Race parseAndSetInfo(Race race, String url) throws Exception {
		logger.debug("url=" + url);
		Document doc = Jsoup.connect(url).get();
		race.wakuList = parseWakuList(doc);
		// 20180708 性別追加
		for (int i=0; i < 6; i++) {
			race.wakuList.get(i).sex = race.raceInfo.sexList.get(i); 
		}
		
		race.computerBetting = parseComputerBetting(doc);
		// 20180605追加
		race.raceInfo2 = parseRaceInfo2(doc, race.wakuList);

		doc = null;
		return race;
	}

	/**
	 * ウェブダウンロードでなく、レースファイル一括変換時使用 20180605
	 * @param race
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public RaceInfo2 parseRaceInfo2(Race race, String url) throws Exception {
		logger.debug("url=" + url);
		Document doc = Jsoup.connect(url).get();
		return parseRaceInfo2(doc, race.wakuList);
	}
	
	private RaceInfo2 parseRaceInfo2(Document doc, List<Waku> wakuList) throws Exception {
		RaceInfo2 raceInfo2 = new RaceInfo2();
		// レース種類
		Element eleRaceType = doc.selectFirst("span.heading2_titleDetail");
		String raceType = eleRaceType.text().replaceAll("　", "");
		raceInfo2.raceType = raceType.split(" ")[0];
		
		// 進入固定
		Element eleFixedEntrance = doc.selectFirst("div.heading2_titleLabels span.label2");
		if (eleFixedEntrance != null) {
			raceInfo2.fixedEntrance = eleFixedEntrance.text();
		} else {
			raceInfo2.fixedEntrance = "N";
		}
		
		StringBuilder sb = new StringBuilder();
		int aLevelCount = 0;
		int femaleCount = 0;
		for (int i = 0; i < wakuList.size(); i++) {
			Waku waku = wakuList.get(i);
			
			sb.append(waku.level);
			if (i < wakuList.size() - 1) {
				sb.append("-");

			}
			if (waku.level.startsWith("A")) {
				aLevelCount++;
			}
		
			// 20180708追加
			if (waku.sex.equals("F")) {
				femaleCount++;
			}
		}
		
		raceInfo2.wakuLevelList = sb.toString();
		raceInfo2.aLevelCount = aLevelCount;
		raceInfo2.femaleCount = femaleCount;
		
		return raceInfo2;
	}
	
	
	private ComputerBetting parseComputerBetting(Document doc) throws Exception {
		ComputerBetting computerBetting = new ComputerBetting();
		computerBetting.predict = "";
		computerBetting.confidence = 0;

		// 20180612 doc = null;
		return computerBetting;
	}

	private List<Waku> parseWakuList(Document doc) throws Exception {
		List<Waku> wakuList = new ArrayList<>();
		Elements wakuRows = doc.select("div.table1 table tbody.is-fs12");
		if (wakuRows.size() != 6) {
			throw new WarningException("failed to parse waku list. rows.size=" + wakuRows.size());
		}
		for (Element row : wakuRows) {
			Waku waku = new Waku();
			waku.no = BoatUtil.toInt(row.child(0).child(0).text());
			// 印
			waku.mark = 0;

			// 登録番号～体重
			String div0 = row.child(0).child(2).child(0).text();
			String[] token = div0.split("/");
			waku.entry = token[0].trim();
			waku.level = token[1].trim();

			String div1 = row.child(0).child(2).child(1).text();
			waku.name = div1.replaceAll("　", "");

			token = row.child(0).child(2).child(2).html().split("<br>");
			String div2 = token[0];
			String div3 = token[1];

			token = div2.split("/");
			waku.branch = token[0].trim();
			waku.born = token[1].trim().replace("\n", "");

			token = div3.split("/");
			waku.age = BoatUtil.toInt(token[0].trim().replace("歳", ""));
			String weight = token[1].trim().replace("kg", "");
			if (!weight.equals("-")) {
				waku.weight = BoatUtil.toFloat(token[1].trim().replace("kg", ""));
			}

			// F数,L数,平均ST
			String td = row.child(0).child(3).html();
			token = td.split("<br>");
			waku.flying = BoatUtil.toInt(token[0].trim().replace("F", ""));
			waku.late = BoatUtil.toInt(token[1].trim().replace("L", ""));
			String averageStart = token[2].trim();
			if (!averageStart.equals("-")) {
				waku.averageStart = BoatUtil.toFloat(averageStart);
			}

			// 全国
			td = row.child(0).child(4).html();
			token = td.split("<br>");
			waku.nationWiningRate = BoatUtil.toFloat(token[0].trim());
			waku.nation2WiningRate = BoatUtil.toFloat(token[1].trim());
			waku.nation3WiningRate = BoatUtil.toFloat(token[2].trim());

			// 当地
			td = row.child(0).child(5).html();
			token = td.split("<br>");
			waku.localWiningRate = BoatUtil.toFloat(token[0].trim());
			waku.local2WiningRate = BoatUtil.toFloat(token[1].trim());
			waku.local3WiningRate = BoatUtil.toFloat(token[2].trim());

			// モーター
			td = row.child(0).child(6).html();
			token = td.split("<br>");
			waku.motorNo = token[0].trim();
			waku.motor2WiningRate = BoatUtil.toFloat(token[1].trim());
			waku.motor3WiningRate = BoatUtil.toFloat(token[2].trim());

			// ボート
			td = row.child(0).child(7).html();
			token = td.split("<br>");
			waku.boatNo = token[0].trim();
			waku.boat2WiningRate = BoatUtil.toFloat(token[1].trim());
			waku.boat3WiningRate = BoatUtil.toFloat(token[2].trim());
			waku.soken = row.child(0).child(23).text();

			StringBuilder sbSetuEnter = new StringBuilder();
			StringBuilder sbSetuStart = new StringBuilder();
			StringBuilder sbSetuRank = new StringBuilder();
			for (int i = 0; i < 12; i++) {
				String str = row.child(1).child(i).text();
				if (str.length() == 0) {
					str = " ";
				}
				sbSetuEnter.append(str);

				sbSetuStart.append(row.child(2).child(i).text());
				if (i < 11) {
					sbSetuStart.append("/");
				}

				str = row.child(3).child(i).text();
				if (str.length() == 0) {
					str = " ";
				} else if ("１２３４５６".contains(str)) {
					str = Normalizer.normalize(str, Normalizer.Form.NFKC);
				}
				sbSetuRank.append(str);
			}
			waku.setuEnter = sbSetuEnter.toString().trim();
			waku.setuStart = sbSetuStart.toString();
			waku.setuRank = sbSetuRank.toString().trim();

			waku.oddsValue1T = 0f;
			waku.oddsValue1F = new Float[] {0f, 0f}; 
			wakuList.add(waku);
		}

		return wakuList;
	}

	public static void main(String[] args) {
		try {
			RaceProgramPageParser parser = new RaceProgramPageParser();
			String url = "https://www.boatrace.jp/owpc/pc/race/racelist?rno=2&jcd=05&hd=20170224";
			//parser.parseRaceInfo2(new Race(), url);
			parser.parseAndSetInfo(new Race(), url);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

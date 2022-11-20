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
 * コンピュータ予想ページ解析
 * @author qwerty
 *
 */
public class ComputerPageParser {

	Logger logger = LoggerFactory.getLogger(ComputerPageParser.class);

	/**
	 * WorkerBaseで呼ばれる。実戦の時のみ利用すること！
	 * @param race
	 * @param url
	 * @return
	 * @throws Exception
	 */
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
		//20181125 TODO 上段の処理はparseAndSetInfoと同一なので統合することを検討する 
		
		// 20181125 nation,local,motorの勝利が0の場合末端値(もっとも低い)に書き換える
		// parseAndSetInfoに当該処理がない理由：WEBからダウンロードしてファイル保存時は元の値をそのまま保存するためである。
		// DBにimportする時、シミュレーションする時、Csv2RaceExにてBoatUtil.modifyWiningRate(race.wakuList)がコールされる
		//BoatUtil.modifyWiningRate(race.wakuList);
		
		// 20181125 averageStartの値が0の場合末端値(もっとも遅い）に書き換える
		// parseAndSetInfoに当該処理がない理由：WEBからダウンロードしてファイル保存時は元の値をそのまま保存するためである。
		// DBにimportする時、シミュレーションする時、Csv2RaceExにてBoatUtil.modifyAverageStart(race.wakuList)がコールされる
		BoatUtil.modifyAverageStart(race.wakuList);
		
		return race;
	}
	
	/**
	 * BoatWebDownloadから呼ばれる。ダウンロードの時のみ利用すること！
	 * @param race
	 * @param url
	 * @return
	 * @throws Exception
	 */
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
	
	private RaceInfo2 parseRaceInfo2(Document doc, List<Waku> wakuList) throws Exception {
		RaceInfo2 raceInfo2 = new RaceInfo2();
		// レース種類
		//Element eleRaceType = doc.selectFirst("span.heading2_titleDetail");
		Element eleRaceType = doc.selectFirst("h3.title16_titleDetail__add2020");
		
		String raceType = eleRaceType.text().replaceAll("　", "");
		raceInfo2.raceType = raceType.split(" ")[0];
		
		// 進入固定
		Element eleFixedEntrance = doc.selectFirst("div.title16_titleLabels__add2020 span.label2");
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
		Elements rows = doc.select("div.numberSet2_row");
		StringBuilder sb = new StringBuilder();
		sb.append(rows.get(4).child(0).text());
		sb.append("-");
		sb.append(rows.get(4).child(1).text());
		sb.append("-");
		sb.append(rows.get(4).child(2).text());
		sb.append("-");
		sb.append(rows.get(2).child(1).text());
		computerBetting.predict = sb.toString();

		Elements confidentRow = doc.select("div.state2");
		computerBetting.confidence = BoatUtil.toInt(confidentRow.get(0).child(1).attr("class").substring(15));

		doc = null;
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
			waku.no = BoatUtil.toInt(row.child(0).child(1).text());
			// 印
			if (row.child(0).child(0).childNodeSize() > 1) {
				waku.mark = getMark(row.child(0).child(0).child(0).attr("src"));
			}

			// 登録番号～体重
			String div0 = row.child(0).child(3).child(0).text();
			String[] token = div0.split("/");
			waku.entry = token[0].trim();
			waku.level = token[1].trim();

			String div1 = row.child(0).child(3).child(1).text();
			waku.name = div1.replaceAll("　", "");

			token = row.child(0).child(3).child(2).html().split("<br>");
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
			String td = row.child(0).child(4).html();
			token = td.split("<br>");
			waku.flying = BoatUtil.toInt(token[0].trim().replace("F", ""));
			waku.late = BoatUtil.toInt(token[1].trim().replace("L", ""));
			String averageStart = token[2].trim();
			if (!averageStart.equals("-")) {
				waku.averageStart = BoatUtil.toFloat(averageStart);
			}

			// 全国
			td = row.child(0).child(5).html();
			token = td.split("<br>");
			waku.nationWiningRate = BoatUtil.toFloat(token[0].trim());
			waku.nation2WiningRate = BoatUtil.toFloat(token[1].trim());
			waku.nation3WiningRate = BoatUtil.toFloat(token[2].trim());

			// 当地
			td = row.child(0).child(6).html();
			token = td.split("<br>");
			waku.localWiningRate = BoatUtil.toFloat(token[0].trim());
			waku.local2WiningRate = BoatUtil.toFloat(token[1].trim());
			waku.local3WiningRate = BoatUtil.toFloat(token[2].trim());

			// モーター
			td = row.child(0).child(7).html();
			token = td.split("<br>");
			waku.motorNo = token[0].trim();
			waku.motor2WiningRate = BoatUtil.toFloat(token[1].trim());
			waku.motor3WiningRate = BoatUtil.toFloat(token[2].trim());

			// ボート
			td = row.child(0).child(8).html();
			token = td.split("<br>");
			waku.boatNo = token[0].trim();
			waku.boat2WiningRate = BoatUtil.toFloat(token[1].trim());
			waku.boat3WiningRate = BoatUtil.toFloat(token[2].trim());
			waku.soken = row.child(0).child(24).text();

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

//			Float val = OddsManager.getInstance().getOddsValue(JyoManager.getJyoCd(race.setu.jyo),
//					String.valueOf(race.raceInfo.no), "1T", String.valueOf(waku.no));
//			waku.oddsValue1T = (val != null) ? val : 0f;
//			Float[] vals = OddsManager.getInstance().getOddsValue1FKF(JyoManager.getJyoCd(race.setu.jyo),
//					String.valueOf(race.raceInfo.no), "1F", String.valueOf(waku.no));
//			waku.oddsValue1F = (vals != null) ? vals : new Float[] {0f, 0f};
			waku.oddsValue1T = 0f;
			waku.oddsValue1F = new Float[] {0f, 0f};
			wakuList.add(waku);
		}

		return wakuList;
	}

	private int getMark(String src) {
		if (src.contains("1_1")) {
			return 1;
		} else if (src.contains("1_2")) {
			return 2;
		} else if (src.contains("1_4")) {
			return 3;
		} else {
			return 4;
		}
	}

	public static void main(String[] args) {
		try {
			ComputerPageParser parser = new ComputerPageParser();
			String url = "https://www.boatrace.jp/owpc/pc/race/pcexpect?rno=12&jcd=03&hd=20220221";
			parser.parseAndSetInfo(new Race(), url);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

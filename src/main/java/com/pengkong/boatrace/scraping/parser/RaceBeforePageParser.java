package com.pengkong.boatrace.scraping.parser;

import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatConst;
import com.pengkong.boatrace.exception.RaceSkipException;
import com.pengkong.boatrace.exception.WarningException;
import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.RaceInfo;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.util.BoatUtil;

/**
 * 直前情報ページパーサー.
 * 
 * @author qwerty
 *
 */
public class RaceBeforePageParser {
	Logger logger = LoggerFactory.getLogger(RaceBeforePageParser.class);
	
	public RaceEx parseAndSetEx(RaceEx race, String url) throws Exception {
		logger.debug("url=" + url);
		
		Document doc = Jsoup.connect(url).get();
		// parse and set wakulist 
		race.wakuList = parseAndSetWakuList(race.wakuList, doc);
		
		// parse and set race before info
		race.raceInfo = parseAndSetRaceBeforeInfo(race.raceInfo, doc);
		
		doc = null;
		return race;
	}
	
	public Race parseAndSet(Race race, String url) throws Exception {
		logger.debug("url=" + url);
		
		Document doc = Jsoup.connect(url).get();
		// parse and set wakulist 
		race.wakuList = parseAndSetWakuList(race.wakuList, doc);
		
		// parse and set race before info
		race.raceInfo = parseAndSetRaceBeforeInfo(race.raceInfo, doc);
		
		doc = null;
		return race;
	}
	
	private RaceInfo parseAndSetRaceBeforeInfo(RaceInfo raceInfo, Document doc) {
		Element item = doc.selectFirst("div.is-direction");
		raceInfo.temparature = BoatUtil.toFloat(item.child(1).child(1).text().replace("℃", ""));
		
		item = doc.selectFirst("div.is-weather");
		raceInfo.weather = item.child(1).child(0).text();
//		// 直前情報がまだ設定されていなければ
//		if (raceInfo.weather.equals("")) {
//			raceInfo.isEnd = false;
//			return raceInfo;
//		}
		
		item = doc.selectFirst("div.is-wind");
		raceInfo.wind = BoatUtil.toFloat(item.child(0).child(1).text().replace("m", ""));
		
		item = doc.selectFirst("div.is-windDirection");
		String windDirection = item.child(0).attr("class");
		if (windDirection.contains("is-" )) {
			raceInfo.windDirection = windDirection.substring(30);	
		} else {
			raceInfo.windDirection = "";
		}
		
		item = doc.selectFirst("div.is-waterTemperature");
		raceInfo.waterTemparature = BoatUtil.toFloat(item.child(0).child(1).text().replace("℃", ""));
		
		item = doc.selectFirst("div.is-wave");
		raceInfo.wave = BoatUtil.toInt(item.child(0).child(1).text().replace("cm", ""));
				
		return raceInfo;
	}
	
	private List<Waku> parseAndSetWakuList(List<Waku> wakuList, Document doc) throws Exception {
		Elements wakuRows = doc.select("div.table1 tbody.is-fs12");
		Elements stRows = doc.select("table.is-w238 tbody td");
		if (wakuRows.size() != 6 || stRows.size() != 6) {
			throw new WarningException("failed to parse before information list. rows.size=" + wakuRows.size());
		}
		
		// 展示タイム,チルト,プロペラ,部品交換
		for (int i=0; i < 6; i++) {
			Element wakuRow = wakuRows.get(i).child(0);
			Waku waku = wakuList.get(i);
			String exhibitTime =wakuRow.child(4).text().trim();
			if (exhibitTime.length() == 0) {
				throw new RaceSkipException("展示情報なし");
			}
			waku.exhibitTime = BoatUtil.toFloat(exhibitTime);
			waku.tilt = BoatUtil.toFloat(wakuRow.child(5).text());
			waku.propela = wakuRow.child(6).text().trim();
			waku.partChange = wakuRow.child(7).text().trim();
			String startExhibit = stRows.get(i).child(0).child(2).text();
			if (startExhibit.contains("F") || startExhibit.contains("L")) {
				// スタート展示でflying,lateした選手
				waku.startExhibit = BoatConst.START_EXHIBIT_OUT;
			} else {
				waku.startExhibit = BoatUtil.toFloat(startExhibit);	
			}
		}
		
		
		return wakuList;
	}

	 
	
	public static void main(String[] args) {
		try {
			RaceBeforePageParser parser = new RaceBeforePageParser();
			Race race = new Race();
			race.wakuList = Arrays.asList(new Waku(),new Waku(),new Waku(),new Waku(),new Waku(),new Waku());
			parser.parseAndSet(race, "https://www.boatrace.jp/owpc/pc/race/beforeinfo?rno=1&jcd=02&hd=20170415");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

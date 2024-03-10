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

import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.model.RaceInfo;
import com.pengkong.boatrace.util.BoatUtil;

public class RaceListPageParser {
	Logger logger = LoggerFactory.getLogger(RaceListPageParser.class);

	public List<RaceEx> parseEx(String url) throws Exception {
		logger.debug("url=" + url);
		
		List<RaceEx> raceList = new ArrayList<>();
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("table tbody tr");
		for (Element row : rows) {
			RaceEx race = new RaceEx();
			race.raceInfo = parseRaceInfo(row);
			race.sortTime = race.raceInfo.sime * 100;
			raceList.add(race);
		}
		
		doc = null;
		return raceList;
	}
	
	public List<Race> parse(String url) throws Exception {
		logger.debug("url=" + url);
		
		List<Race> raceList = new ArrayList<>();
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("table tbody tr");
		for (Element row : rows) {
			Race race = new Race();
			race.raceInfo = parseRaceInfo(row);
			raceList.add(race);
		}
		
		doc = null;
		return raceList;
	}
	
	private RaceInfo parseRaceInfo(Element row) {
		RaceInfo raceInfo = new RaceInfo();
		raceInfo.no = BoatUtil.toInt(row.child(0).child(0).text().replace("R", ""));
		String strSime = Normalizer.normalize(row.child(1).text(), Normalizer.Form.NFKC);
		raceInfo.sime = BoatUtil.toInt(strSime.replace(":", ""));
		raceInfo.isPastRace = (row.child(2).text().equals("発売終了")) ? true : false;
		// 20180415
		String[] hhmm = strSime.split(":");
		raceInfo.hh = BoatUtil.toInt(hhmm[0]);
		// 20210530
		raceInfo.mm = BoatUtil.toInt(hhmm[1]);
		String sex = "";
		for (int i = 0; i < 6; i++) {
			sex = (row.child(3+i).child(1).attr("class").contains("is-lady")) ? "F" : "M";  
//20200511			sex = (row.child(4+i).child(1).attr("class").contains("is-lady")) ? "F" : "M";  
			raceInfo.sexList.add(sex);
		}
		
		return raceInfo;
	}
	
	
	public static void main(String[] args) {
		try {
			RaceListPageParser parser = new RaceListPageParser();
			parser.parse("https://www.boatrace.jp/owpc/pc/race/raceindex?jcd=06&hd=20180705");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.pengkong.boatrace.scraping.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.exception.FatalException;
import com.pengkong.boatrace.util.BoatUtil;

/**
 * 3連複オッズのwebページを解析してオッズmapを返却する
 * 
 * @author qwerty
 *
 */
public class Odds3FPageParser {
	Logger logger = LoggerFactory.getLogger(Odds3FPageParser.class);
	
	/** 3連複オッズテーブルの行数 */
	private final int TR_NUM = 10;

	public HashMap<String, Float> parse(String url) throws IOException, FatalException {
		logger.debug("url=" + url);
		
		HashMap<String, Float> oddsMap = new HashMap<>();
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("table tbody.is-p3-0 tr");
		if (rows.size() != TR_NUM) {
			throw new FatalException("invalid 3連複 odds table. url = " + url + "parsed rows=" + rows.size());
		}

		String[] digit123 = new String[3];
		digit123[0] = "1";
		digit123[1] = "2";
		Element row = rows.get(0);
		digit123[2] = row.child(1).text();
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(2).text()));
		for (int i = 1; i <= 3; i++) {
			row = rows.get(i);
			digit123[2] = row.child(0).text();
			oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(1).text()));
		}
		
		digit123[1] = "3";
		digit123[0] = "1";
		row = rows.get(4);
		digit123[2] = row.child(1).text();
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(2).text()));
		digit123[0] = "2";
		digit123[2] = row.child(4).text();
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(5).text()));
		for (int i = 5; i <= 6; i++) {
			row = rows.get(i);
			digit123[0] = "1";
			digit123[2] = row.child(0).text();
			oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(1).text()));
			digit123[0] = "2";
			digit123[2] = row.child(2).text();
			oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(3).text()));
		}

		digit123[0] = "1";
		digit123[1] = "4";
		row = rows.get(7);
		digit123[2] = row.child(1).text();
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(2).text()));
		digit123[0] = "2";
		digit123[2] = row.child(4).text();
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(5).text()));
		digit123[0] = "3";
		digit123[2] = row.child(7).text();
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(8).text()));
		row = rows.get(8);
		digit123[0] = "1";
		digit123[2] = row.child(0).text();
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(1).text()));
		digit123[0] = "2";
		digit123[2] = row.child(2).text();
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(3).text()));
		digit123[0] = "3";
		digit123[2] = row.child(4).text();
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(5).text()));
		
		digit123[0] = "1";
		digit123[1] = "5";
		row = rows.get(9);
		digit123[2] = "6";
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(2).text()));
		digit123[0] = "2";
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(5).text()));
		digit123[0] = "3";
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(8).text()));
		digit123[0] = "4";
		oddsMap.put(String.join("", digit123), BoatUtil.toFloat(row.child(11).text()));
		
		doc = null;
		return oddsMap;
	}

	public static void main(String[] args) {
		try {
			Odds3FPageParser parser = new Odds3FPageParser();
			String url = "https://www.boatrace.jp/owpc/pc/race/odds3f?rno=1&jcd=02&hd=20170415";
			HashMap<String, Float> oddsMap = parser.parse(url);

			Map<String, Float> sortedMap = new TreeMap<>(oddsMap);
			Iterator<Entry<String, Float>> it = sortedMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Float> entry = it.next();
				System.out.println(entry.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package com.pengkong.boatrace.scraping.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.util.BoatUtil;

/**
 * 2連単・２連複オッズのwebページを解析してオッズmapを返却する
 * 
 * @author qwerty
 *
 */
public class Odds2TFPageParser {
	Logger logger = LoggerFactory.getLogger(Odds2TFPageParser.class);
	
	/** 2連単オッズテーブルの行数 */
	private final int TR_NUM = 5;

	public List<HashMap<String, Float>> parse(String url) throws IOException {
		logger.debug("url=" + url);
		
		List<HashMap<String, Float>> oddsMapList = new ArrayList<>();
		Document doc = Jsoup.connect(url).get();
		Elements tables = doc.select("table tbody.is-p3-0");

		// 2連単オッズ
		oddsMapList.add(parse2T(tables.get(0)));
		// 2連複オッズ
		oddsMapList.add(parse2F(tables.get(1)));
		
		doc = null;
		return oddsMapList;
	}

	private HashMap<String, Float> parse2T(Element table) {
		HashMap<String, Float> oddsMap = new HashMap<>();
		Elements rows = table.select("tr");
		for (int rowIdx = 0; rowIdx < TR_NUM; rowIdx++) {
			Element row = rows.get(rowIdx);
			String[] digit12 = new String[2];
			for (int i = 0; i < 11; i+=2) {
				digit12[0] = String.valueOf( (i / 2) + 1);
				digit12[1] = row.child(i).text();
				oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(i+1).text()));
			}
		}
		
		return oddsMap;
	}
	
	private HashMap<String, Float> parse2F(Element table) {
		HashMap<String, Float> oddsMap = new HashMap<>();
		Elements rows = table.select("tr");
		String[]digit12 = new String[2];
		
		digit12[0] = "1";
		digit12[1] = "2";
		Element row = rows.get(0);
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(1).text()));
		
		digit12[0] = "1";
		digit12[1] = "3";
		row = rows.get(1);
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(1).text()));
		digit12[0] = "2";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(3).text()));

		digit12[0] = "1";
		digit12[1] = "4";
		row = rows.get(2);
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(1).text()));
		digit12[0] = "2";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(3).text()));
		digit12[0] = "3";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(5).text()));
		
		digit12[0] = "1";
		digit12[1] = "5";
		row = rows.get(3);
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(1).text()));
		digit12[0] = "2";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(3).text()));
		digit12[0] = "3";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(5).text()));
		digit12[0] = "4";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(7).text()));
		
		digit12[0] = "1";
		digit12[1] = "6";
		row = rows.get(4);
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(1).text()));
		digit12[0] = "2";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(3).text()));
		digit12[0] = "3";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(5).text()));
		digit12[0] = "4";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(7).text()));
		digit12[0] = "5";
		oddsMap.put(String.join("", digit12), BoatUtil.toFloat(row.child(9).text()));
		
		return oddsMap;
	}
	
	public static void main(String[] args) {
		try {
			Odds2TFPageParser parser = new Odds2TFPageParser();
			String url = "https://www.boatrace.jp/owpc/pc/race/odds2tf?rno=1&jcd=02&hd=20180403";
			List<HashMap<String, Float>> oddsMapList = parser.parse(url);
			for (HashMap<String, Float> oddsMap : oddsMapList) {
				Map<String, Float> sortedMap = new TreeMap<>(oddsMap);
				Iterator<Entry<String, Float>> it = sortedMap.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Float> entry = it.next();
					System.out.println(entry.toString());
				}
				System.out.println("------------");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

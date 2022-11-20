package com.pengkong.boatrace.scraping.parser;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.model.Odds;
import com.pengkong.boatrace.util.BoatUtil;

/**
 * 拡連複オッズのwebページを解析してオッズmapを返却する
 * 
 * @author qwerty
 *
 */
public class OddsKFPageParser {
	Logger logger = LoggerFactory.getLogger(OddsKFPageParser.class);
	
	public HashMap<String, Float[]> parse(String url) throws IOException {
		logger.debug("url=" + url);
		
		HashMap<String, Float[]> oddsMap = new HashMap<>();
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("table tbody.is-p3-0 tr");
		
		String[]digit12 = new String[2];
		
		digit12[0] = "1";
		digit12[1] = "2";
		Element row = rows.get(0);
		oddsMap.put(String.join("", digit12), splitFloat(row.child(1).text()));
		
		digit12[0] = "1";
		digit12[1] = "3";
		row = rows.get(1);
		oddsMap.put(String.join("", digit12), splitFloat(row.child(1).text()));
		digit12[0] = "2";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(3).text()));

		digit12[0] = "1";
		digit12[1] = "4";
		row = rows.get(2);
		oddsMap.put(String.join("", digit12), splitFloat(row.child(1).text()));
		digit12[0] = "2";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(3).text()));
		digit12[0] = "3";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(5).text()));
		
		digit12[0] = "1";
		digit12[1] = "5";
		row = rows.get(3);
		oddsMap.put(String.join("", digit12), splitFloat(row.child(1).text()));
		digit12[0] = "2";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(3).text()));
		digit12[0] = "3";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(5).text()));
		digit12[0] = "4";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(7).text()));
		
		digit12[0] = "1";
		digit12[1] = "6";
		row = rows.get(4);
		oddsMap.put(String.join("", digit12), splitFloat(row.child(1).text()));
		digit12[0] = "2";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(3).text()));
		digit12[0] = "3";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(5).text()));
		digit12[0] = "4";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(7).text()));
		digit12[0] = "5";
		oddsMap.put(String.join("", digit12), splitFloat(row.child(9).text()));

		return oddsMap;
	}

	private Float[] splitFloat(String value) {
		String[] token = value.split("-");
		Float[] values = new Float[2];
		values[0] = BoatUtil.toFloat(token[0]);
		values[1] = BoatUtil.toFloat(token[1]);
		
		return values;
	}
	
	public static void main(String[] args) {
		try {
			OddsKFPageParser parser = new OddsKFPageParser();
			String url = "https://www.boatrace.jp/owpc/pc/race/oddsk?rno=1&jcd=02&hd=20170415";
			HashMap<String, Float[]> oddsMap = parser.parse(url);
			System.out.println(Odds.toCsvFloats(oddsMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

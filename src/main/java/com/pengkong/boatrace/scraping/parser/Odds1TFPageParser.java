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
 * 単勝・複勝オッズのwebページを解析してオッズmapを返却する
 * 
 * @author qwerty
 *
 */
public class Odds1TFPageParser {
	Logger logger = LoggerFactory.getLogger(Odds1TFPageParser.class);

	/** 単勝オッズテーブルの行数 */
	private final int TR_NUM = 6;

	private HashMap<String, Float> odds1T;
	private HashMap<String, Float[]> odds1F;
	
	public void parse(String url) throws IOException{
		logger.debug("url=" + url);
		
		Document doc = Jsoup.connect(url).get();
		Elements tables = doc.select("table.is-w495");

		// 単勝オッズ
		
		odds1T = parse1T(tables.get(0));
		// 複勝オッズ
		odds1F = parse1F(tables.get(1));

		doc = null;
	}
	
	public HashMap<String, Float> getOdds1T() {
		return odds1T;
	}

	public HashMap<String, Float[]> getOdds1F() {
		return odds1F;
	}


	private HashMap<String, Float> parse1T(Element table) {
		HashMap<String, Float> oddsMap = new HashMap<>();
		Elements rows = table.select("tbody tr");
		for (int rowIdx = 0; rowIdx < TR_NUM; rowIdx++) {
			Element row = rows.get(rowIdx);
			oddsMap.put(row.child(0).text(), BoatUtil.toFloat(row.child(2).text()));
		}

		return oddsMap;
	}

	private HashMap<String, Float[]> parse1F(Element table) {
		HashMap<String, Float[]> oddsMap = new HashMap<>();
		Elements rows = table.select("tbody tr");
		for (int rowIdx = 0; rowIdx < TR_NUM; rowIdx++) {
			Element row = rows.get(rowIdx);
			// 複勝の場合Float[0]～Float[1]の範囲血
			String[] token = row.child(2).text().split("-");
			Float[] values = new Float[2];
			values[0] = BoatUtil.toFloat(token[0]);
			values[1] = BoatUtil.toFloat(token[1]);
			oddsMap.put(row.child(0).text(), values);
		}

		return oddsMap;
	}

	public static void main(String[] args) {
		try {
			Odds1TFPageParser parser = new Odds1TFPageParser();
			String url = "https://www.boatrace.jp/owpc/pc/race/oddstf?rno=1&jcd=02&hd=20180403";
			parser.parse(url);
			
			HashMap<String, Float> odds1T = parser.getOdds1T();
			System.out.println(Odds.toCsv(odds1T));
			System.out.println("------------");
			HashMap<String, Float[]> odds1F = parser.getOdds1F();
			System.out.println(Odds.toCsvFloats(odds1F));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

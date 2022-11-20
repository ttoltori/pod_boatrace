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

/**
 * 3連単オッズのwebページを解析してオッズmapを返却する
 * 
 * @author qwerty
 *
 */
public class Odds3TPageParser {
	Logger logger = LoggerFactory.getLogger(Odds3TPageParser.class);
	
	/** 3連単オッズテーブルの行数 */
	private final int TR_NUM = 20;

	/** 3連単オッズテーブルの列数 (1行目) */
	private final int TD_NUM_FIRSTROW = 18;

	/** 3連単オッズテーブルの列数 (2,3,4行目) */
	private final int TD_NUM_234ROW = 12;

	public HashMap<String, Float> parse(String url) throws IOException, FatalException {
		logger.debug("url=" + url);
		
		HashMap<String, Float> oddsMap = new HashMap<>();
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("table tbody.is-p3-0 tr");
		if (rows.size() != TR_NUM) {
			throw new FatalException("invalid 3連弾 odds table. url = " + url + "parsed rows=" + rows.size());
		}

		String[] firstDigits = { "1", "2", "3", "4", "5", "6" };
		String[] secondDigits = new String[6];
		for (int rowIdx = 0; rowIdx < TR_NUM; rowIdx++) {
			Element row = rows.get(rowIdx);
			Elements cols = row.children();
			if (cols.size() == TD_NUM_FIRSTROW) {
				oddsMap = parseFirstRow(oddsMap, cols, firstDigits, secondDigits, url);
			} else if (cols.size() == TD_NUM_234ROW) {
				oddsMap = parse234Row(oddsMap, cols, firstDigits, secondDigits, url);
			} else {
				throw new FatalException("invalid 3連弾 odds table. url = " + url + "parsed colulms=" + cols.size());
			}

		}

		doc = null;
		return oddsMap;
	}

	/**
	 * オッズテーブルの１行目を解析する
	 * 
	 * @param oddsMap
	 *            オッズマップ(例：key="123", value="56.8"）
	 * @param cols
	 *            <td>の一覧
	 * @param firstDigits
	 *            １着No
	 * @param secondsDigits
	 *            2着No
	 * @param url
	 *            odds ページ
	 * @return oddsMap
	 * @throws Exception
	 */
	private HashMap<String, Float> parseFirstRow(HashMap<String, Float> oddsMap, Elements cols, String firstDigits[],
			String secodeDigits[], String url) throws FatalException{
		String secondDigit = "";
		String thirdDigit = "";
		Float value = null;
		for (int colIdx = 0; colIdx < TD_NUM_FIRSTROW; colIdx++) {
			Element col = cols.get(colIdx);
			int divide = (colIdx) / 3;
			int remain = (colIdx + 1) % 3;
			if (remain == 1) {
				secondDigit = col.text();
				secodeDigits[divide] = secondDigit;
			} else if (remain == 2) {
				thirdDigit = col.text();
			} else if (remain == 0) {
				value = Float.valueOf(col.text());
				String key = firstDigits[divide] + secondDigit + thirdDigit;
				if (key.length() != 3 || value == null) {
					throw new FatalException("invalid parsed odds. url = " + url + ", key=" + key + ", value=" + value);
				}
				oddsMap.put(key, value);
				secondDigit = thirdDigit = "";
				value = null;
			}
		}

		return oddsMap;
	}

	/**
	 * オッズテーブルの2,3,4行目を解析する
	 * 
	 * @param oddsMap
	 *            オッズマップ(例：key="123", value="56.8"）
	 * @param cols
	 *            <td>の一覧
	 * @param firstDigits
	 *            １着No
	 * @param secondsDigits
	 *            2着No
	 * @param url
	 *            odds ページ
	 * @return oddsMap
	 * @throws Exception
	 */
	private HashMap<String, Float> parse234Row(HashMap<String, Float> oddsMap, Elements cols, String firstDigits[],
			String secondDigits[], String url) throws FatalException {
		String thirdDigit = "";
		Float value = null;
		for (int colIdx = 0; colIdx < TD_NUM_234ROW; colIdx++) {
			Element col = cols.get(colIdx);
			int divide = (colIdx) / 2;
			int remain = (colIdx + 1) % 2;
			if (remain == 1) {
				thirdDigit = col.text();
			} else if (remain == 0) {
				value = Float.valueOf(col.text());
				String key = firstDigits[divide] + secondDigits[divide] + thirdDigit;
				if (key.length() != 3 || value == null) {
					throw new FatalException("invalid parsed odds. url = " + url + ", key=" + key + ", value=" + value);
				}
				oddsMap.put(key, value);
				thirdDigit = "";
				value = null;
			}
		}

		return oddsMap;
	}

	public static void main(String[] args) {
		try {
			Odds3TPageParser parser = new Odds3TPageParser();
			String url = "https://www.boatrace.jp/owpc/pc/race/odds3t?rno=9&jcd=03&hd=20170318";
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

package com.pengkong.boatrace.scraping.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.exception.OddsBankException;
import com.pengkong.boatrace.model.Odds;
import com.pengkong.boatrace.util.BoatUtil;

/**
 * 3連単オッズのwebページを解析してオッズmapを返却する
 * 
 * @author qwerty
 *
 */
public class OddsBankPageParser {
	Logger logger = LoggerFactory.getLogger(OddsBankPageParser.class);
	
	public List<String> parse(String yyyyMMdd, String jyoCd, String raceNo, String mode) throws Exception {
		List<String> result = new ArrayList<>();
		String line = "";
		
		String year = yyyyMMdd.substring(0, 4);
		String month = new Integer(yyyyMMdd.substring(4, 6)).toString();
		String day = new Integer(yyyyMMdd.substring(6, 8)).toString();
		String jyo = new Integer(jyoCd).toString();
		String r = new Integer(raceNo).toString();
		
		String url = BoatTemplate.URL_ODDSBANK_BASE.replace("{year}", year)
				.replace("{month}", month)
				.replace("{day}", day)
				.replace("{jyo}", jyo)
				.replace("{r}", r)
				.replace("{mode}", mode)
				;
		logger.debug("url=" + url);
		
		HashMap<String, Float> oddsMap;
		//20190316 Document doc = Jsoup.connect(url).get();
//		URL urlObj = new URL(url);
//		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 9668)); // or whatever your proxy is
//		HttpURLConnection uc = (HttpURLConnection)urlObj.openConnection(proxy);
//		uc.connect();
//		line = null;
//		StringBuffer tmp = new StringBuffer();
//		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
//		while ((line = in.readLine()) != null) {
//		      tmp.append(line);
//		}
//		Document doc = Jsoup.parse(String.valueOf(tmp));
		Document doc = Jsoup.connect(url).get();
		//doc = Jsoup.connect(url).get();
		if (doc.html().contains("アクセスが多いため")) {
			throw new OddsBankException();
		}
		Elements tbodies = doc.select("div.oddstable table tbody");
		
		// 3連単
		oddsMap = parseTbody3T(tbodies.get(0));
		line = Odds.toCsv(oddsMap);
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "3T") +  line);
		
		// 2連単
		oddsMap = parseTbody2T(tbodies.get(1));
		line = Odds.toCsv(oddsMap);
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "2T") +  line);
		
		// 3連複
		oddsMap = parseTbody3F(tbodies.get(2));
		line = Odds.toCsv(oddsMap);
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "3F") +  line);

		// 2連複
		oddsMap = parseTbody2F(tbodies.get(3));
		line = Odds.toCsv(oddsMap);
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "2F") +  line);
		
		doc = null;
		return result;
	}
	
	/**
	 * 2連複
	 * @param tbody
	 * @return
	 */
	private HashMap<String, Float> parseTbody2F(Element tbody) {
		HashMap<String, Float> mapOdds = new HashMap<>();
		int[] arrNumCol = {3, 6, 9, 12, 15};
		
		for (int i=0; i < 5; i++) {
			mapOdds = parseRow2F(mapOdds, tbody.child(i), arrNumCol[i]);
		}
		
		return mapOdds;
	}
	
	private HashMap<String, Float> parseRow2F(HashMap<String, Float> mapOdds, Element tr, int numCol) {
		String digit1 = "";
		String digit2 = "";
		Float odds;
		for (int i = 0; i < numCol; i++) {
			if ((i%3) == 0) {
				digit1 = tr.child(i).text();
			} else if ((i%3) == 1) {
				digit2 = tr.child(i).text();
			} else if ((i%3) == 2) {
				odds = BoatUtil.toFloat(tr.child(i).text());
				String key = digit1 + digit2;
				mapOdds.put(key, odds);
				digit1 = "";
				digit2 = "";
			}
		}
		
		return mapOdds;
	}

	/**
	 * 3連複
	 * @param tbody
	 * @return
	 */
	private HashMap<String, Float> parseTbody3F(Element tbody) {
		HashMap<String, Float> mapOdds = new HashMap<>();
		for (int i=0; i < 4; i++) {
			mapOdds = parseRow3F(mapOdds, tbody.child(i));
		}
		
		return mapOdds;
	}

	private HashMap<String, Float> parseRow3F(HashMap<String, Float> mapOdds, Element tr) {
		String digit1 = "";
		String digit2 = "";
		String digit3 = "";
		Float odds;
		for (int i=0; i < 20; i++) {
			if ((i%4 == 0)) {
				digit1 = tr.child(i).text();
			} else if ((i%4 == 1)) {
				digit2 = tr.child(i).text();
			} else if ((i%4 == 2)) {
				digit3 = tr.child(i).text();
			} else if ((i%4 == 3)) {
				odds = BoatUtil.toFloat(tr.child(i).text());
				String key = digit1 + digit2 + digit3;
				mapOdds.put(key, odds);
				digit1 = "";
				digit2 = "";
				digit3 = "";
			}
		}
		
		return mapOdds;
	}
	
	/**
	 * 2連単
	 * @param tbody
	 * @return
	 */
	private HashMap<String, Float> parseTbody2T(Element tbody) {
		HashMap<String, Float> mapOdds = new HashMap<>();
		String[] arrDigit1 = { "1", "2", "3", "4", "5", "6" };
		for (int i=0; i < 5; i++) {
			// digit1の行
			if (i == 0) {
				mapOdds = parseDigit1Row2T(mapOdds, arrDigit1, tbody.child(i));
			} else {
				// digit1の行以外
				mapOdds = parseDigit2Row2T(mapOdds, arrDigit1, tbody.child(i));
			}
		}
		
		return mapOdds;
	}
	
	private HashMap<String, Float> parseDigit1Row2T(HashMap<String, Float> mapOdds, String[] arrDigit1,  Element tr) {
		int digit1Idx = 0;
		String digit1 = "";
		String digit2 = "";
		Float odds;

		for (int i = 0; i < 18; i++) {
			if ((i%3 == 0)) {
				digit1 = arrDigit1[digit1Idx];
				digit1Idx++;
			} else if ((i%3 == 1)) {
				digit2 = tr.child(i).text();
			} else if ((i%3) == 2) {
				odds = BoatUtil.toFloat(tr.child(i).text());
				String key = digit1 + digit2;
				mapOdds.put(key, odds);
				digit1 = "";
				digit2 = "";
			}
		}
	
		return mapOdds;
	}
	
	private HashMap<String, Float> parseDigit2Row2T(HashMap<String, Float> mapOdds, String[] arrDigit1,  Element tr) {
		int digit1Idx = 0;
		String digit1 = "";
		String digit2 = "";
		Float odds;

		for (int i = 0; i < 12; i++) {
			if ((i%2) == 0) {
				digit1 = arrDigit1[digit1Idx];
				digit2 = tr.child(i).text();
				digit1Idx++;
			} else if ((i%2) == 1) {
				odds = BoatUtil.toFloat(tr.child(i).text());
				String key = digit1 + digit2;
				mapOdds.put(key, odds);
				digit1 = "";
				digit2 = "";
			} 
		}
	
		return mapOdds;
	}
	
	/**
	 * 3連単
	 * @param tbody
	 * @return
	 */
	private HashMap<String, Float> parseTbody3T(Element tbody) {
		HashMap<String, Float> mapOdds = new HashMap<>();
		String[] arrDigit1 = { "1", "2", "3", "4", "5", "6" };
		String[] arrDigit2 = new String[6];
		for (int i=1; i < 21; i++) {
			int j = i - 1;
			// digit2の行
			if ( (j % 4) == 0) {
				mapOdds = parseDigit2Row3T(mapOdds, arrDigit1, arrDigit2, tbody.child(i));
			} else {
				// digit2の行以外
				mapOdds = parseDigit3Row3T(mapOdds, arrDigit1, arrDigit2, tbody.child(i));
			}
		}
		
		return mapOdds;
	}
	
	private HashMap<String, Float> parseDigit2Row3T(HashMap<String, Float> mapOdds, String[] arrDigit1, String[] arrDigit2, Element tr) {
		int digit12Idx = 0;
		String digit1 = "";
		String digit2 = "";
		String digit3 = "";
		Float odds;
		for (int i = 0; i < 18; i++) {
			if ((i % 3) == 0) {
				digit1 = arrDigit1[digit12Idx];
				digit2 = tr.child(i).text();
				arrDigit2[digit12Idx] = digit2;
				digit12Idx++;
			} else if ((i % 3) == 1) {
				digit3 = tr.child(i).text();
			} else if ((i % 3) == 2) {
				odds = BoatUtil.toFloat(tr.child(i).text());
				String key = digit1 + digit2 + digit3;
				mapOdds.put(key, odds);
				digit1 = "";
				digit2 = "";
				digit3 = "";
			}
		}
		
		return mapOdds;
	}
	

	/**
	 * 3連単
	 * @param mapOdds
	 * @param arrDigit1
	 * @param arrDigit2
	 * @param tr
	 * @return
	 */
	private HashMap<String, Float> parseDigit3Row3T(HashMap<String, Float> mapOdds, String[] arrDigit1, String[] arrDigit2, Element tr) {
		int digit12Idx = 0;
		String digit1 = "";
		String digit2 = "";
		String digit3 = "";
		Float odds;
		for (int i = 0; i < 12; i++) {
			if ((i % 2) == 0) {
				digit1 = arrDigit1[digit12Idx];
				digit2 = arrDigit2[digit12Idx];
				digit3 = tr.child(i).text();
				digit12Idx++;
			}
			
			if ((i % 2) == 1) {
				odds = BoatUtil.toFloat(tr.child(i).text());
				String key = digit1 + digit2 + digit3;
				mapOdds.put(key, odds);
				digit1 = "";
				digit2 = "";
				digit3 = "";
			}
		}
		
		return mapOdds;
	}
	
	private String createPrefix(String yyyyMMdd, String jyoCd, String raceNo, String type) {
		StringBuilder sb = new StringBuilder();
		sb.append(yyyyMMdd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(jyoCd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(raceNo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(type);
		sb.append(BoatTemplate.CSV_DELIMITER);
		
		return sb.toString();
	}

	public static void main(String[] args) {
		try {
//			System.setProperty("http.proxyHost", "127.0.0.1");
//			System.setProperty("http.proxyPort", "9668");
			
			OddsBankPageParser parser = new OddsBankPageParser();
			// http://www.orangebuoy.net/odds/?day=1&month=2&year=2018&jyo=7&r=4&mode=1
			List<String> result = parser.parse("20181105", "02", "1", "1");
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package com.pengkong.boatrace.scraping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.Odds;
import com.pengkong.boatrace.scraping.parser.Odds1TFPageParser;
import com.pengkong.boatrace.scraping.parser.Odds2TFPageParser;
import com.pengkong.boatrace.scraping.parser.Odds3FPageParser;
import com.pengkong.boatrace.scraping.parser.Odds3TPageParser;
import com.pengkong.boatrace.scraping.parser.OddsKFPageParser;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class OddsDownloader {
	org.slf4j.Logger logger = LoggerFactory.getLogger(BoatWebDownloader.class);
	PropertyUtil prop = PropertyUtil.getInstance();

	public List<String> download(String yyyyMMdd, String jyoCd, String raceNo) throws Exception {
		List<String> result = new ArrayList<>();
		String line = "";
		
		String urlTemplate;
		StringBuilder sb = new StringBuilder();

		urlTemplate = BoatTemplate.URL_ODDS_BASE.replace("{raceNo}", raceNo)
				.replace("{jyoCd}", jyoCd)
				.replace("{yyyyMMdd}", yyyyMMdd);
		// 3連単
		setPrefix(sb, yyyyMMdd, jyoCd, raceNo, "3T");
		String url = urlTemplate.replace("{oddsType}", "3t");
		HashMap<String, Float> oddsMap = new Odds3TPageParser().parse(url);
		line = Odds.toCsv(oddsMap);
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "3T") +  line);
		sb.append(line);
		sb.append(System.lineSeparator());
		
		// 3連複
		setPrefix(sb, yyyyMMdd, jyoCd, raceNo, "3F");
		url = urlTemplate.replace("{oddsType}", "3f");
		oddsMap = new Odds3FPageParser().parse(url);
		line = Odds.toCsv(oddsMap);
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "3F") +  line);
		sb.append(line);
		sb.append(System.lineSeparator());
		
		// 2連単、２連複
		setPrefix(sb, yyyyMMdd, jyoCd, raceNo, "2T");
		url = urlTemplate.replace("{oddsType}", "2tf");
		List<HashMap<String, Float>> oddsMapList = new Odds2TFPageParser().parse(url);
		line = Odds.toCsv(oddsMapList.get(0));
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "2T") +  line);
		sb.append(line);
		sb.append(System.lineSeparator());
		
		setPrefix(sb, yyyyMMdd, jyoCd, raceNo, "2F");
		line = Odds.toCsv(oddsMapList.get(1));
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "2F") +  line);
		sb.append(line);
		sb.append(System.lineSeparator());
		
		// 拡連複
		setPrefix(sb, yyyyMMdd, jyoCd, raceNo, "KF");
		url = urlTemplate.replace("{oddsType}", "k");
		HashMap<String, Float[]> oddsMap3 = new OddsKFPageParser().parse(url);
		line = Odds.toCsvFloats(oddsMap3);
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "KF") +  line);
		sb.append(line);
		sb.append(System.lineSeparator());
		
		// 単勝
		// 複勝
		url = urlTemplate.replace("{oddsType}", "tf");
		Odds1TFPageParser parser = new Odds1TFPageParser();
		parser.parse(url);
		oddsMap = parser.getOdds1T();
		setPrefix(sb, yyyyMMdd, jyoCd, raceNo, "1T");
		line = Odds.toCsv(oddsMap);
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "1T") +  line);
		sb.append(line);
		sb.append(System.lineSeparator());
		
		HashMap<String, Float[]> oddsMap2 = parser.getOdds1F();
		setPrefix(sb, yyyyMMdd, jyoCd, raceNo, "1F");
		line = Odds.toCsvFloats(oddsMap2);
		result.add(createPrefix(yyyyMMdd, jyoCd, raceNo, "1F") +  line);
		sb.append(line);
		
		save(yyyyMMdd, sb.toString());

		return result;
	}
	
	public void save(String yyyyMMdd, String csv) throws Exception {
		String filepath = prop.getString("DIRECTORY_ODDS") + "odds_" + yyyyMMdd + ".csv";
		FileUtil.appendFileByLine(filepath, csv);
	}
	
	private void setPrefix(StringBuilder sb, String yyyyMMdd, String jyoCd, String raceNo, String type) {
		sb.append(yyyyMMdd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(jyoCd);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(raceNo);
		sb.append(BoatTemplate.CSV_DELIMITER);
		sb.append(type);
		sb.append(BoatTemplate.CSV_DELIMITER);
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
			new OddsDownloader().download("20181014", "02", "1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

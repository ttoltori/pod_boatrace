package com.pengkong.boatrace.scraping.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.Schedule;
import com.pengkong.boatrace.util.BoatUtil;

public class SchedulePageParser {
	Logger logger = LoggerFactory.getLogger(SchedulePageParser.class);

	public List<Schedule> parse(String url, String yyyy) throws Exception {
		logger.debug("url=" + url);
		
		List<Schedule> result = new ArrayList<>();

		Document doc = Jsoup.connect(url).get();
		// parse first page (SG,PG)
		result.addAll(parseSchedule(doc, yyyy));

		// parse remains
		Elements elements = doc.select("ul.tab2_tabs a");

		for (Element element : elements) {
			doc = Jsoup.connect(BoatTemplate.HP_URL_BASE + element.attr("href")).get();
			result.addAll(parseSchedule(doc, yyyy));
		}

		doc = null;
		return result;
	}

	private List<Schedule> parseSchedule(Document doc, String yyyy) {
		List<Schedule> results = new ArrayList<>();

		Elements elements = doc.select("div.table1 table tbody tr");
		for (Element element : elements) {
			Schedule schedule = new Schedule();
			int tdIdx = (element.children().size() == 7) ? 0 : 1;
			String period = element.child(tdIdx).text();

			StringBuilder sb = new StringBuilder();
			sb.append(yyyy);
			sb.append(period.substring(0, 2));
			sb.append(period.substring(3, 5));
			schedule.fromYmd = BoatUtil.toInt(sb.toString());
			sb = new StringBuilder();
			sb.append(yyyy);
			sb.append(period.substring(6, 8));
			sb.append(period.substring(9, 11));
			schedule.toYmd = BoatUtil.toInt(sb.toString());
			schedule.jyo = element.child(tdIdx + 1).child(0).attr("alt");
			schedule.grade = element.child(tdIdx + 2).attr("class").substring(13, 15);
			if (element.child(tdIdx + 2).attr("class").contains("is-venus")) {
				schedule.isVenus = "Y";
			} else {
				schedule.isVenus = "";
			}
			String strTimezone = element.child(tdIdx + 3).attr("class");
			if (strTimezone.equals("is-nighter")) {
				schedule.timezone = "N";
			} else if (strTimezone.equals("is-summer")) {
				schedule.timezone = "S";
			} else if (strTimezone.equals("is-morning")) {
				schedule.timezone = "M";
			} else {
				schedule.timezone = "";
			}
			
			// <aタグの子elementが存在する場合
			if (element.child(tdIdx + 4).children().size() > 0) {
				schedule.title = element.child(tdIdx + 4).child(0).text().trim();
			} else {
				schedule.title = element.child(tdIdx + 4).text().trim();
			}
			schedule.winner = element.child(tdIdx + 5).text().replaceAll("　", "");
			if (schedule.winner.equals("")) {
				schedule.winner = "N/A";
			}
			schedule.isFromSgSchedulePage = "Y";
			System.out.println(schedule.toCsv());
			results.add(schedule);
		}
		return results;
	}
}

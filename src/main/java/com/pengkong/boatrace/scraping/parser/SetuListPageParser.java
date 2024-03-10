package com.pengkong.boatrace.scraping.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.model.Schedule;
import com.pengkong.boatrace.model.Setu;
import com.pengkong.boatrace.service.manager.JyoManager;
import com.pengkong.boatrace.service.manager.ScheduleManagger;
import com.pengkong.boatrace.util.BoatUtil;

public class SetuListPageParser {
	Logger logger = LoggerFactory.getLogger(SetuListPageParser.class);
	
	protected ScheduleManagger scheduleManagger;

	public SetuListPageParser() {
		this.scheduleManagger = ScheduleManagger.getInstance();
	}

	public List<Setu> parse(String url, String yyyyMMdd, boolean isToday) throws Exception {
		logger.debug("url=" + url);
		
		List<Setu> setuList = new ArrayList<>();
		Document doc = Jsoup.connect(url).get();

		Elements setuRows = doc.select("table tbody tr");
		for (Element setuRow : setuRows) {
			if (setuRow.children().size() < 3) {
				continue;
			}
			// 場が中止ならスキップ
			if (setuRow.child(1).text().contains("中止")) {
				logger.debug(setuRow.child(0).child(0).child(0).attr("alt").replace(">", "") + " 中止");
				continue;
			}
			// 場が発売終了ならスキップ
			if (setuRow.child(1).text().contains("発売終了")) {
				logger.debug(setuRow.child(0).child(0).child(0).attr("alt").replace(">", "") + " 発売終了");
				// 20231112 trend情報更新のために発売終了したレースでも取得する。
				// レースステータスをTOHYO_TIMEOUT(4)で設定してol_raceに保存される
				Setu setu = parseSetu(setuRow, yyyyMMdd, isToday);
				setuList.add(setu);
				
				continue;
			}
			
			// 前日発売ならスキップ
			if (setuRow.child(1).text().contains("前日")) {
				logger.debug(setuRow.child(0).child(0).child(0).attr("alt").replace(">", "") + " 前日");
				continue;
			}
			
			logger.debug(setuRow.child(0).child(0).child(0).attr("alt").replace(">", "") + " 発売可能");
			// 節情報取得
			Setu setu = parseSetu(setuRow, yyyyMMdd, isToday);
			setuList.add(setu);
		}

		doc = null;
		return setuList;
	}

	/**
	 * 
	 * @param setuRow
	 * @param yyyyMMdd
	 * @param isToday 使用しない2021/6/1
	 * @return
	 * @throws Exception
	 */
	private Setu parseSetu(Element setuRow, String yyyyMMdd, boolean isToday) throws Exception{
		int offset = 0;
		// 20210601 if (isToday) {
		if (setuRow.children().size() > 9) {
			// 20200511 offset = 3;
			offset = 2;
		}
		Setu setu = new Setu();
		String jyo=null;
		try {
			jyo = setuRow.child(0).child(0).child(0).attr("alt").replace(">", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Schedule schedule = scheduleManagger.find(jyo, yyyyMMdd);
		if (schedule == null) {
			schedule = createSchedule(setuRow, yyyyMMdd.substring(0, 4), offset);
			schedule.jyo = jyo;
			scheduleManagger.add(schedule);
		}
		// 20180415
		setu.jyoCd = JyoManager.getJyoCd(jyo);
		setu.jyo = jyo;
		setu.name = schedule.title;
		setu.grade = schedule.grade;
		setu.isVenus = schedule.isVenus;
		setu.timezone = schedule.timezone;
		setu.winner = schedule.winner;
		setu.turn = setuRow.child(5 + offset).text().split(" ")[1];
		
		return setu;
	}
	
	private Schedule createSchedule(Element setuRow, String yyyy, int offset) {
		Schedule schedule = new Schedule();
		String gradeStr = setuRow.child(2 + offset).attr("class");
		schedule.grade = gradeStr.substring(3, 5);
		// JSJ 20180707 is-Venusだけじゃないis-ladyも追加
		if (gradeStr.contains("is-venus") || gradeStr.contains("is-lady")) {
			schedule.isVenus = "Y";
		} else {
			schedule.isVenus = "";
		}
		String strTimezone = setuRow.child(3 + offset).attr("class");
		if (strTimezone.equals("is-nighter")) {
			schedule.timezone = "N";
		} else if (strTimezone.equals("is-summer")) {
			schedule.timezone = "S";
		} else if (strTimezone.equals("is-morning")) {
			schedule.timezone = "M";
		} else {
			schedule.timezone = "";
		}
		schedule.title = setuRow.child(4 + offset).child(0).text();
		String period = setuRow.child(5 + offset).text().split(" ")[0];
		String[] periodToken = period.split("-");
		String[] mdToken = periodToken[0].split("/");
		String fromYyyyMMdd = String.format("%s%02d%02d", yyyy, BoatUtil.toInt(mdToken[0]),
					BoatUtil.toInt(mdToken[1]));
		mdToken = periodToken[1].split("/");
		String toYyyyMMdd = String.format("%s%02d%02d", yyyy, BoatUtil.toInt(mdToken[0]),
				BoatUtil.toInt(mdToken[1]));
		schedule.fromYmd = BoatUtil.toInt(fromYyyyMMdd);
		schedule.toYmd = BoatUtil.toInt(toYyyyMMdd);
		schedule.winner = "N/A";
		schedule.isFromSgSchedulePage = "N";
		
		return schedule;
	}

	public static void main(String[] args) {
		try {
			SetuListPageParser parser = new SetuListPageParser();
			String url = "https://www.boatrace.jp/owpc/pc/race/index?hd=20210601";
			parser.parse(url, "20210601", true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

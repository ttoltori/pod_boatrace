package com.pengkong.boatrace.scraping;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.model.Schedule;
import com.pengkong.boatrace.scraping.parser.SchedulePageParser;
import com.pengkong.common.PropertyUtil;

public class ScheduleDownloader {
	static PropertyUtil prop = PropertyUtil.getInstance();
	/**
	 * 公式WEBから指定範囲のスケジュールをcsv保存する。
	 * 
	 * @param fromYear
	 *            scrapする年 (yyyy)
	 * @param toYear
	 *            scrapする年 (yyyy)
	 * @param filepath
	 *            (保存するcsvファイルパス)
	 */
	public void saveScheduleAsCsv(String fromYear, String toYear, String filepath) throws Exception{
		SchedulePageParser parser = new SchedulePageParser();
		int from = Integer.parseInt(fromYear);
		int to = Integer.parseInt(toYear);
		List<Schedule> schedules = new ArrayList<>();
		for (int i = from; i <= to; i++) {
			String yyyy = String.valueOf(i);
			String url = BoatTemplate.SCHEDULE_URL.replace("{year}", yyyy);
			schedules.addAll(parser.parse(url, yyyy));
		}
		
		try(FileWriter writer = new FileWriter(filepath)) {
			for (Schedule schedule : schedules) {
				writer.write(schedule.toCsv()+"\n");
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			ScheduleDownloader scheduleScraper = new ScheduleDownloader();
			String filepath = prop.getString("DIRECTORY_CSV") + "schedule.csv";
			scheduleScraper.saveScheduleAsCsv("2017", "2018", filepath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

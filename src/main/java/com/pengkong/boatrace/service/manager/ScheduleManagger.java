package com.pengkong.boatrace.service.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.exception.FatalException;
import com.pengkong.boatrace.model.Schedule;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class ScheduleManagger {
	private List<Schedule> schedules;
	private String csvFilepath;

	private volatile boolean isLoaded = false;
	Logger logger = LoggerFactory.getLogger(ScheduleManagger.class);

	private static ScheduleManagger instance = new ScheduleManagger();

	public ScheduleManagger(String csvFilepath) {
		this.csvFilepath = csvFilepath;
		try {
			load();
		} catch (Exception e) {
			logger.error("ScheduleManager initialization faild. file=" + csvFilepath, e);
		}
		logger.info("ScheduleManager initialization is OK. file=" + csvFilepath);
	}

	public ScheduleManagger() {
		this(PropertyUtil.getInstance().getString("DIRECTORY_CSV") + "schedule.csv");
	}

	public static ScheduleManagger getInstance() {
		return instance;
	}
	
	public synchronized Schedule find(String jyo, String strYyyyMMdd) {
		int yyyyMMdd = Integer.parseInt(strYyyyMMdd);
		for (Schedule schedule : schedules) {
			if (schedule.jyo.equals(jyo)) {
				if (yyyyMMdd >= schedule.fromYmd && yyyyMMdd <= schedule.toYmd) {
					return schedule;
				}
			}
		}

		return null;
	}

	public synchronized void add(Schedule schedule) throws Exception {
		schedules.add(schedule);
		FileUtil.appendFileByLine(csvFilepath, schedule.toCsv());
	}

	private synchronized void load() throws Exception {
		if (isLoaded) {
			return;
		}
		
		schedules = new ArrayList<Schedule>();
		List<String> contents = FileUtil.readFileByLineArr(csvFilepath, "UTF8");
		for (String line : contents) {
			schedules.add(parse(line));
		}
		
		isLoaded = true;
	}

	private Schedule parse(String str) throws Exception {
		Schedule schedule = new Schedule();
		String[] token = str.split(BoatTemplate.CSV_DELIMITER);
		if (token.length != 9) {
			throw new FatalException("parsing csv is failed: " + str);
		}
		schedule.fromYmd = Integer.parseInt(token[0]);
		schedule.toYmd = Integer.parseInt(token[1]);
		schedule.jyo = token[2];
		schedule.grade = token[3];
		schedule.isVenus = token[4];
		schedule.timezone = token[5];
		schedule.title = token[6];
		schedule.winner = token[7];
		schedule.isFromSgSchedulePage = token[8];

		return schedule;
	}
}

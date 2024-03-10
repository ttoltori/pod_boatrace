package com.pengkong.boatrace.util;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.pengkong.boatrace.converter.Csv2RaceEx;
import com.pengkong.boatrace.converter.RaceEx2RecRace;
import com.pengkong.boatrace.converter.RaceEx2RecRaceWaku;
import com.pengkong.boatrace.converter.RaceEx2RecRaceWaku2;
import com.pengkong.boatrace.converter.RaceEx2RecRacerList;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.mybatis.client.RecRaceMapper;
import com.pengkong.boatrace.mybatis.client.RecRaceWaku2Mapper;
import com.pengkong.boatrace.mybatis.client.RecRaceWakuMapper;
import com.pengkong.boatrace.mybatis.client.RecRacerMapper;
import com.pengkong.boatrace.mybatis.entity.RecRace;
import com.pengkong.boatrace.mybatis.entity.RecRaceExample;
import com.pengkong.boatrace.mybatis.entity.RecRaceWaku;
import com.pengkong.boatrace.mybatis.entity.RecRaceWaku2;
import com.pengkong.boatrace.mybatis.entity.RecRaceWaku2Example;
import com.pengkong.boatrace.mybatis.entity.RecRaceWakuExample;
import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.boatrace.mybatis.entity.RecRacerExample;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class RaceFileUploader {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	/**
	 * 레이스 파일을 DB에 업로드한다.
	 */
	public RaceFileUploader() {
	}
	
	public void execute(String yyyyMMdd) throws Exception {
		execute(yyyyMMdd, yyyyMMdd);
	}

	public void execute(String fromYmd, String toYmd) throws Exception {
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		// open session with auto-transaction mode = false
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			RecRaceMapper recRaceMapper = session.getMapper(RecRaceMapper.class);
			RecRacerMapper recRacerMapper = session.getMapper(RecRacerMapper.class);
			RecRaceWakuMapper recRaceWakuMapper = session.getMapper(RecRaceWakuMapper.class);
			RecRaceWaku2Mapper recRaceWaku2Mapper = session.getMapper(RecRaceWaku2Mapper.class);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			
			while (currDate.compareTo(toDate) <= 0) {
				String yyyyMMdd = sdf.format(currDate);

				// delete records of current date
				RecRaceExample example = new RecRaceExample();
				example.createCriteria().andYmdEqualTo(yyyyMMdd);
				recRaceMapper.deleteByExample(example);
				
				RecRacerExample example2 = new RecRacerExample();
				example2.createCriteria().andYmdEqualTo(yyyyMMdd);
				recRacerMapper.deleteByExample(example2);
				
				RecRaceWakuExample example3 = new RecRaceWakuExample();
				example3.createCriteria().andYmdEqualTo(yyyyMMdd);
				recRaceWakuMapper.deleteByExample(example3);
				
				RecRaceWaku2Example example4 = new RecRaceWaku2Example();
				example4.createCriteria().andYmdEqualTo(yyyyMMdd);
				recRaceWaku2Mapper.deleteByExample(example4);
				
				String filepath = PropertyUtil.getInstance().getString("DIRECTORY_CSV") + "race_" + yyyyMMdd + ".csv";
				File file = new File(filepath);
				if (!file.exists()) {
					// 1日増加
					calendar.add(Calendar.DATE, 1);
					currDate = calendar.getTime();
					continue;
				}
				
				String fname = file.getName();
				filepath = file.getPath();
				List<String> lines = FileUtil.readFileByLineArr(filepath, "UTF8");
				for (String line : lines) {
					// DBimport用にレースファイルデータを変換する
					RaceEx race = Csv2RaceEx.convertAndModifyForStatistics(line);

					if (isExcludeFromDB(race)) {
						continue;
					}
					
					// insert rec_race
					RecRace recRace = RaceEx2RecRace.convert(race);
					recRaceMapper.insert(recRace);
					
					// insert rec_race_waku
					RecRaceWaku recRaceWaku = RaceEx2RecRaceWaku.convert(race);
					recRaceWakuMapper.insert(recRaceWaku);
					
					// insert rec_race_waku2
					RecRaceWaku2 recRaceWaku2 = RaceEx2RecRaceWaku2.convert(race);
					recRaceWaku2Mapper.insert(recRaceWaku2);
					
					List<RecRacer> records = RaceEx2RecRacerList.convert(race);
					for (RecRacer record : records) {
						// insert rec_racer
						recRacerMapper.insert(record);
					}
				}
				System.out.println(fname);
				session.commit();
				
				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}
			
//			session.commit();
		}
	}
	
	/**
	 * DBimportから除外対象かを判断する。
	 * @param race
	 * @return
	 */
	private boolean isExcludeFromDB(RaceEx race) {
// 20210712 一旦DBには全部入れる
//		if (!race.setu.grade.equals("ip") && !race.setu.grade.equals("G3")) {
//			return true;
//		}
//		
//		if (race.raceInfo.weather.equals("霧"))
//			return true;
//		
//		if (race.raceInfo.wind > 9) {
//			return true;
//		}
//		
//		if (race.raceInfo.wave > 10) {
//			return true;
//		}
//		
		return false;
	}
	
	public static void main(String[] args) {
//		String fromYmd = "20100101";
//		String toYmd = "20100101";
//		String propFile = "C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties";

		String fromYmd = args[0];
		String toYmd = args[1];
		String propFile = args[2];
		
		try {
			PropertyUtil.getInstance().addFile(propFile);
			new RaceFileUploader().execute(fromYmd, toYmd);
			//BoatProperty.init("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			//new RaceFileUploader().execute("20100101", "20100102");
			//new RaceFileUploader().execute("20090730", "20121231");
			// 20090729 エラー発生
			//new RaceFileUploader().execute("20090101", "20170424");
			//new RaceFileUploader().execute("20090101", "20180725");
			// new RaceFileUploader().execute("20180726", "20181007");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

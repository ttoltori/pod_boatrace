package com.pengkong.boatrace.util.tmp;

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

import com.pengkong.boatrace.common.BoatTemplate;
import com.pengkong.boatrace.converter.Csv2RaceEx;
import com.pengkong.boatrace.converter.RaceEx2RecRace;
import com.pengkong.boatrace.model.RaceEx;
import com.pengkong.boatrace.mybatis.client.RecRaceMapper;
import com.pengkong.boatrace.mybatis.client.RecRacerMapper;
import com.pengkong.boatrace.mybatis.entity.RecRace;
import com.pengkong.boatrace.mybatis.entity.RecRacer;
import com.pengkong.common.FileUtil;
import com.pengkong.common.PropertyUtil;

public class BatchUtil {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	public BatchUtil() {
	}

	public void uploadRaceCsv2Db(String fromYmd, String toYmd) throws Exception {
		String resource = "mybatis-config.0.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		try(SqlSession session = sqlSessionFactory.openSession(false)) {
			RecRaceMapper recRaceMapper = session.getMapper(RecRaceMapper.class);
			RecRacerMapper recRacerMapper = session.getMapper(RecRacerMapper.class);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(fromYmd));
			Date currDate = calendar.getTime();
			Date toDate = sdf.parse(toYmd);
			
			while (currDate.compareTo(toDate) <= 0) {
				String yyyyMMdd = sdf.format(currDate);

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
					RaceEx race = Csv2RaceEx.convert(line);
					RecRace recRace = RaceEx2RecRace.convert(race);
					// insert rec_race
					recRaceMapper.insert(recRace);
					
//					List<RecRacer> records = BoatUtil.convertRecRacerList(race);
//					for (RecRacer record : records) {
//						// insert rec_racer
//						recRacerMapper.insert(record);
//					}
				}
				System.out.println(fname);

				// 1日増加
				calendar.add(Calendar.DATE, 1);
				currDate = calendar.getTime();
			}
			
			session.commit();
		}
	}
	
	public static void main(String[] args) {
		try {
			PropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/test/properties/race_result_rebuild.properties");
			new BatchUtil().uploadRaceCsv2Db("20130101", "20180505");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

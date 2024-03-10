package com.pengkong.boatrace.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.boatrace.mybatis.client.CustomMapper;
import com.pengkong.boatrace.mybatis.client.RecRacerArrMapper;
import com.pengkong.boatrace.mybatis.entity.RecRacerArrExample;
import com.pengkong.boatrace.scraping.BoatWebDownloader;
import com.pengkong.common.PropertyUtil;

public class DailyRaceUpdater {
	Logger logger = LoggerFactory.getLogger(DailyRaceUpdater.class);
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	PropertyUtil prop = PropertyUtil.getInstance();	
	/**
	 * 레이스 파일을 DB에 업로드한다.
	 */
	public DailyRaceUpdater() {
	}
	
	public void execute(String fromYmd, String toYmd, String daysOfTrend, String propFilepath) throws Exception {
		logger.info(String.join(Delimeter.COMMA.getValue(),"DailyRaceUpdater START", fromYmd, toYmd, daysOfTrend, propFilepath) );
		
		logger.info("BoatWebDownloader.execute");
		BoatWebDownloader wdl = new BoatWebDownloader();
		wdl.execute(fromYmd, toYmd);
		
		logger.info("RaceFileUploader.execute");
		RaceFileUploader uploader = new RaceFileUploader();
		uploader.execute(fromYmd, toYmd);
		
		logger.info("insertRecRacerArr SQL execute");
		insertRecRacerArr(fromYmd, toYmd);
		
//		logger.info("RecRacerTrendGenerator.execute");
//		RecRacerTrendGenerator rrtg = new RecRacerTrendGenerator();
//		rrtg.execute(fromYmd, toYmd, Integer.valueOf(daysOfTrend));
//		
//		logger.info("RecRacerArray2Generator.execute");
//		RecRacerArray2Generator rrag = new RecRacerArray2Generator();
//		rrag.execute(fromYmd, toYmd, Integer.valueOf(daysOfTrend));
		
		logger.info(String.join(Delimeter.COMMA.getValue(),"DailyRaceUpdater END", fromYmd, toYmd, daysOfTrend, propFilepath) );
	}

	public void insertRecRacerArr(String fromYmd, String toYmd) throws Exception{
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		try {
			RecRacerArrMapper rram = session.getMapper(RecRacerArrMapper.class);
			CustomMapper cm = session.getMapper(CustomMapper.class);
			
			// 既存データ削除
			RecRacerArrExample exam = new RecRacerArrExample();
			exam.createCriteria().andYmdBetween(fromYmd, toYmd);
			rram.deleteByExample(exam);

			// insert RecRacerArr
			HashMap<String, String> param = new HashMap<>();
			param.put("fromYmd", fromYmd);
			param.put("toYmd", toYmd);
			
			cm.insertRecRacerArr(param);
			
			session.commit();
		
		} finally {
			DatabaseUtil.close(session);		
		}
	}

	public static void main(String[] args) {
//		String fromYmd = "20100101";
//		String toYmd = "20100101";
//		String propFile = "C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties";

		String fromYmd = args[0];
		String toYmd = args[1];
		String daysOfTrend = args[2];
		String propFile = args[3];
		
		if (args.length < 4) {
			System.exit(-1);
		}
		
		try {
			PropertyUtil.getInstance().addFile(propFile);
			new DailyRaceUpdater().execute(fromYmd, toYmd, daysOfTrend, propFile);
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

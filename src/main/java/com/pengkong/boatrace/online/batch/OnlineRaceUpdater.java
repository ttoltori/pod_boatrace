package com.pengkong.boatrace.online.batch;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.common.PropertyUtil;

/**
 * online投票開始前に当日のレース情報、直近選手成績情報をDBに追加する。 
 * @author ttolt
 *
 */
public class OnlineRaceUpdater {
	Logger logger = LoggerFactory.getLogger(OnlineRaceUpdater.class);
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	PropertyUtil prop = PropertyUtil.getInstance();	
	/**
	 * 레이스 파일을 DB에 업로드한다.
	 */
	public OnlineRaceUpdater() {
	}
	
	public void execute(String todayYmd, String daysOfTrend, String propFilepath) throws Exception {
		logger.info(String.join(Delimeter.COMMA.getValue(),"OnlineRaceUpdater START", todayYmd, daysOfTrend, propFilepath) );
		
		logger.info("OnlineRaceDownloader.execute");
		OnlineRaceDownloader ord = new OnlineRaceDownloader(todayYmd);
		ord.execute();
		
		logger.info("RecRacerTrendGenerator.execute");
		RecRacerTrendGenerator rrtg = new RecRacerTrendGenerator();
		rrtg.execute(todayYmd, todayYmd, Integer.valueOf(daysOfTrend));
		
		
		logger.info("RecRacerArray2Generator.execute");
		RecRacerArray2Generator rrag = new RecRacerArray2Generator();
		rrag.execute(todayYmd, todayYmd, Integer.valueOf(daysOfTrend));
		
		
		logger.info(String.join(Delimeter.COMMA.getValue(),"OnlineRaceUpdater END", todayYmd, daysOfTrend, propFilepath) );
	}

	public static void main(String[] args) {
//		String fromYmd = "20100101";
//		String toYmd = "20100101";
//		String propFile = "C:/Dev/workspace/Oxygen/pod_boatrace_test/properties/race_result_rebuild.properties";

		String todayYmd = args[0];
		String daysOfTrend = args[1];
		String propFile = args[2];
		
		if (args.length != 3) {
			System.out.println("usage OnlineRaceUpdater {yyyyMMdd} 180 online.properties");
			System.exit(-1);
		}
		
		try {
			PropertyUtil.getInstance().addFile(propFile);
			new OnlineRaceUpdater().execute(todayYmd, daysOfTrend, propFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
